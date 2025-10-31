package de.cmuellerke.demo.service;

import java.time.ZonedDateTime;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import de.cmuellerke.demo.entity.User;
import de.cmuellerke.demo.event.UserChangedEvent;
import de.cmuellerke.demo.event.UserReplicationFailedEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Wenn ein User verändert wird, dann wird ein UserChangedEvent ausgelöst 
 * und auf dem Kafka Topic platziert.
 * Dieser Consumer fängt das UserChangedEvent.
 */
@Component
@Slf4j
@Getter
@RequiredArgsConstructor
public class UserChangedEventKafkaConsumer {

    private CountDownLatch latch = new CountDownLatch(1);
    private UserChangedEvent receivedUserChangedEvent;
    
    private final DLQEventKafkaProducer dlqProducer;
    @KafkaListener(topics = "${application.topics.users.replication}")
    public void receive(UserChangedEvent userChangedEvent) {
        log.info("[UserChangedEventConsumer] received UserChangedEvent with user id '{}'", userChangedEvent.getUser().getId());
        receivedUserChangedEvent = userChangedEvent;

        if (userChangedEvent.getUser().getNachname().equals("Fehlerteufel")){
            UserReplicationFailedEvent dlqEvent = UserReplicationFailedEvent.builder()
            .lastRetry(ZonedDateTime.now())
            .originalEvent(null) // TODO!
            .retryCount(0)
            .build();

            sendToDLQ(userChangedEvent);
        }

        // TODO hier jetzt in die users_replica speichern

        latch.countDown();
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    private void sendToDLQ(UserChangedEvent failedEvent) {
        UserReplicationFailedEvent dlqEvent = UserReplicationFailedEvent.builder()
            .lastRetry(ZonedDateTime.now())
            .retryCount(0)
            .originalEvent(failedEvent)
            .build();

        dlqProducer.send(dlqEvent);
    }
}