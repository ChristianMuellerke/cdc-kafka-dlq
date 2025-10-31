package de.cmuellerke.demo.service;

import java.util.concurrent.CountDownLatch;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import de.cmuellerke.demo.entity.User;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Getter
public class DLQKafkaConsumer {

    private CountDownLatch latch = new CountDownLatch(1);
    private User receivedUser;

    @KafkaListener(topics = "${application.topics.users.replication.dlq}")
    public void receive(User user) {
        log.info("[DLQ] received user with id '{}'", user.getId());
        receivedUser = user;
        latch.countDown();
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    // other getters
}