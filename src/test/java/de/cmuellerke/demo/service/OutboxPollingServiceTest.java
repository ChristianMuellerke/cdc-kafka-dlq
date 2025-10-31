package de.cmuellerke.demo.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import de.cmuellerke.demo.entity.ReplicatedUser;
import de.cmuellerke.demo.entity.User;
import de.cmuellerke.demo.repository.OutboxRepository;
import de.cmuellerke.demo.repository.ReplicatedUserRepository;
import de.cmuellerke.demo.repository.UserRepository;
import de.cmuellerke.demo.repository.UserRepositoryTest;
import jdk.jfr.Description;

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
	private ReplicatedUserRepository replicatedUserRepository;

    
    @Autowired
	private OutboxPollingService outboxPoller;

    @Autowired
    private UserChangedEventKafkaConsumer consumer;

    // @Autowired
    // private UserChangedEventKafkaProducer producer;

    // @Value("${application.topics.users.replication.dlq}")
    // private String dlqTopic;


    @Test
    @DisplayName("a new user is successfully replicated")
	void savesNewUser_HappyPath() throws InterruptedException {

		User newUser = UserRepositoryTest.createUser();
		User savedUser = userRepository.save(newUser);

		assertThat(outboxRepository.findById(savedUser.getId())).isNotNull();

		boolean messageConsumed = consumer.getLatch().await(10, TimeUnit.SECONDS);
        assertTrue(messageConsumed);
        assertThat(consumer.getReceivedUserChangedEvent()).isNotNull();
        assertThat(consumer.getReceivedUserChangedEvent().getUser().getId()).isEqualTo(newUser.getId()); 
        assertThat(consumer.getReceivedUserChangedEvent().getUser().getVorname()).isEqualTo(newUser.getVorname()); 
        assertThat(consumer.getReceivedUserChangedEvent().getUser().getNachname()).isEqualTo(newUser.getNachname()); 

        assertThat(outboxRepository.count()).isZero();
        
        Optional<ReplicatedUser> foundReplicatedUser = replicatedUserRepository.findById(savedUser.getId());
        
        assertThat(foundReplicatedUser).isNotEmpty();
        foundReplicatedUser.ifPresent(replicatedUser -> {
        	assertThat(replicatedUser.getId()).isEqualTo(savedUser.getId());
        	assertThat(replicatedUser.getVorname()).isEqualTo(savedUser.getVorname());
        	assertThat(replicatedUser.getNachname()).isEqualTo(savedUser.getNachname());
        	assertThat(replicatedUser.getRetryCount()).isZero();
        });
	}

    // Test für 
}
