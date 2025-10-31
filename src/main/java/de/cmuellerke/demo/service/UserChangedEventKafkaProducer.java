package de.cmuellerke.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import de.cmuellerke.demo.entity.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserChangedEventKafkaProducer {

    @Autowired
    private KafkaTemplate<String, User> kafkaTemplate;

    public void send(String topic, User user) {
        // TODO refactor: diesem producer braucht man nicht von aussen ein topic zu benennen
        // TODO statt nur den User zu versenden, sollten wir einen echten Event versenden, der dann auch nachher ein paar Metadaten hat
        log.info("sending payload='{}' to topic='{}'", user, topic);
        kafkaTemplate.send(topic, user);
    }
}