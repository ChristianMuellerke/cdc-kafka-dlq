package de.cmuellerke.demo.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.cmuellerke.demo.entity.DLQInboxEntry;
import de.cmuellerke.demo.entity.User;
import de.cmuellerke.demo.event.UserReplicationFailedEvent;
import de.cmuellerke.demo.repository.DLQInboxRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Getter
public class DLQKafkaConsumer {

    private CountDownLatch latch = new CountDownLatch(1);
    private UserReplicationFailedEvent receivedEvent;
    private DLQInboxRepository dlqInboxRepository;

    @KafkaListener(topics = "${application.topics.users.replication.dlq}")
    public void receive(UserReplicationFailedEvent userReplicationFailedEvent) throws JsonProcessingException {
        log.info("[DLQ] received user with id '{}'", userReplicationFailedEvent.getOriginalEvent().getUser().getId());
        receivedEvent = userReplicationFailedEvent;
        
        String originalEventAsJson = new ObjectMapper().writeValueAsString(userReplicationFailedEvent.getOriginalEvent());
        
        DLQInboxEntry dlqInboxEntry = DLQInboxEntry.builder()
        		.originalEventAsJson(originalEventAsJson)
        		.tspInserted(Timestamp.from(Instant.now()))
        		.build();
        
        dlqInboxRepository.save(dlqInboxEntry);
        
        latch.countDown();
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    // other getters
}