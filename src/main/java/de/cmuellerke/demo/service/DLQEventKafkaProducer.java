package de.cmuellerke.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import de.cmuellerke.demo.entity.User;
import de.cmuellerke.demo.event.UserReplicationFailedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DLQEventKafkaProducer {

    @Autowired
    private KafkaTemplate<String, UserReplicationFailedEvent> kafkaTemplate;

    @Value("${application.topics.users.replication.dlq}")
    private String topic;

    public void send(UserReplicationFailedEvent event) {
        log.info("sending dlq payload='{}' to topic='{}'", event, topic);
        kafkaTemplate.send(topic, event);
    }
}