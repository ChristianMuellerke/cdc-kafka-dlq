package de.cmuellerke.demo.service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.cmuellerke.demo.configuration.KafkaProducerConfig;
import de.cmuellerke.demo.entity.OutboxEntry;
import de.cmuellerke.demo.entity.User;
import de.cmuellerke.demo.event.UserChangedEvent;
import de.cmuellerke.demo.repository.OutboxRepository;
import de.cmuellerke.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxPollingService {
    
    final UserRepository userRepository;
    final OutboxRepository outboxRepository;
    final KafkaTemplate<String, Object> kafkaTemplate;

    
    @Scheduled(fixedDelay = 1000, timeUnit = TimeUnit.MILLISECONDS)
    public void poll() {
        long count = outboxRepository.count();
        log.info("found {} entries in outbox", count);
        
        if (count > 0) {
            // lese n-Sätze aus der Outbox - fuer jeden Satz die Person ermitteln und eine Kafka Nachricht versenden
            // danach die Nachricht aus der Outbox löschen
            // manuell nach n-Nachrichten einen Commit auslösen (Transaktionsklammer sonst zu groß?)
            // nochmal die poll() Methode rufen damit wir eben nicht so lange warten müssen
            Pageable limit = PageRequest.of(0, 100);
            Page<OutboxEntry> entries = outboxRepository.findAll(limit);
            entries.get().forEach(this::send);
        }
    }
    
    public void send(OutboxEntry entry) {
        UUID id = entry.getId();
        Objects.requireNonNull(id);
        userRepository.findById(id).ifPresentOrElse(user -> {
            sendMessage(user);

            log.info("deleting outbox entry {}", id);
            outboxRepository.deleteById(id);
        }, () -> {
            // was soll passieren, wenn User nicht gefunden wird? das dürfte es eigentlich gar nicht geben 
            // (okay doch: Outbox nicht abgearbeitet aber User schon gelöscht?)
            log.error("User defined in outbox does not exist");
        });
    }

    public void sendMessage(User user) {
        UserChangedEvent userChangedEvent = UserChangedEvent.builder().action("I").user(user).build();
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(KafkaProducerConfig.USER_REPLICA_TOPIC, userChangedEvent);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent UserChangedEvent=[" + user.getId() + 
                "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                log.error("Unable to send UserChangedEvent [" + 
                    user.getId() + "] due to : " + ex.getMessage());
            }
        });
    }
}
