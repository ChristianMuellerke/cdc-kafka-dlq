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
import de.cmuellerke.demo.repository.OutboxRepository;
import de.cmuellerke.demo.repository.UserRepository;
import de.cmuellerke.demo.repository.UserRepositoryTest;

@SpringBootTest
@ActiveProfiles(profiles = "dev")
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class OutboxPollingServiceTest implements WithAssertions {
	
    @Autowired
	private UserRepository userRepository;

    @Autowired
	private OutboxRepository outboxRepository;

    @Autowired
	private OutboxPollingService outboxPoller;

    @Autowired
    private UserChangedEventKafkaConsumer consumer;

    // @Autowired
    // private UserChangedEventKafkaProducer producer;

    // @Value("${application.topics.users.replication.dlq}")
    // private String dlqTopic;


    @Test
	void savesNewUserAndHandlesMessages() throws InterruptedException {

		User newUser = UserRepositoryTest.createUser();
		User savedUser = userRepository.save(newUser);

		assertThat(outboxRepository.findById(savedUser.getId())).isNotNull();

		boolean messageConsumed = consumer.getLatch().await(120, TimeUnit.SECONDS);
        assertTrue(messageConsumed);
        assertThat(consumer.getReceivedUser()).isNotNull();
        assertThat(consumer.getReceivedUser().getId()).isEqualTo(newUser.getId()); 
        assertThat(consumer.getReceivedUser().getVorname()).isEqualTo(newUser.getVorname()); 
        assertThat(consumer.getReceivedUser().getNachname()).isEqualTo(newUser.getNachname()); 


        outboxPoller.poll();
	}

}
