package de.cmuellerke.demo.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import de.cmuellerke.demo.entity.User;
import de.cmuellerke.demo.repository.UserRepositoryTest;

@SpringBootTest
@DirtiesContext
@ActiveProfiles(profiles = "dev")
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class KafkaConsumerTest implements WithAssertions {

    @Autowired
    private DLQKafkaConsumer consumer;

    @Autowired
    private de.cmuellerke.demo.service.UserChangedEventKafkaProducer producer;

    @Value("${application.topics.users.replication.dlq}")
    private String topic;

    @Test
    public void givenEmbeddedKafkaBroker_whenSendingWithSimpleProducer_thenMessageReceived() 
      throws Exception {
        User user = UserRepositoryTest.createUser();
        
        producer.send(topic, user);
        
        boolean messageConsumed = consumer.getLatch().await(30, TimeUnit.SECONDS);
        assertTrue(messageConsumed);
        assertThat(consumer.getReceivedUser()).isNotNull();
        assertThat(consumer.getReceivedUser().getId()).isEqualTo(user.getId()); 
        assertThat(consumer.getReceivedUser().getVorname()).isEqualTo(user.getVorname()); 
        assertThat(consumer.getReceivedUser().getNachname()).isEqualTo(user.getNachname()); 
    }
}