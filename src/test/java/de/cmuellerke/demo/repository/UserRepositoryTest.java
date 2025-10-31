package de.cmuellerke.demo.repository;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import javax.sql.DataSource;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import de.cmuellerke.demo.DatabaseIntegrationTest;
import de.cmuellerke.demo.entity.User;

//@DatabaseIntegrationTest
@SpringBootTest
@ActiveProfiles(profiles = "dev")
public class UserRepositoryTest implements WithAssertions {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void deleteAllUsers() {
		userRepository.deleteAll();
	}

	@Test
	void injectedComponentsAreNotNull() {
		assertThat(dataSource).isNotNull();
		assertThat(jdbcTemplate).isNotNull();
		assertThat(entityManager).isNotNull();
		assertThat(userRepository).isNotNull();
	}

	@Test
	void savesNewUser() {

		User newUser = createUser();
		User savedUser = userRepository.save(newUser);

		assertThat(savedUser.getId()).isNotNull();
		assertThat(savedUser.getVorname()).isEqualTo(newUser.getVorname());
		assertThat(savedUser.getNachname()).isEqualTo(newUser.getNachname());
	}

	@Test
	void savesNewUserAndReloadsIt() {

		User newUser = createUser();
		User savedUser = userRepository.save(newUser);
		Optional<User> reloadedUser = userRepository.findById(savedUser.getId());

		assertThat(reloadedUser).isNotEmpty();
		assertThat(reloadedUser.get().getId()).isEqualTo(savedUser.getId());
	}

	@Test
	void savesNewUserAndDeletesIt() {

		User newUser = createUser();
		User savedUser = userRepository.save(newUser);
		Optional<User> reloadedUser = userRepository.findById(savedUser.getId());

		assertThat(reloadedUser).isNotEmpty();
		assertThat(reloadedUser.get().getId()).isEqualTo(savedUser.getId());

		userRepository.delete(reloadedUser.get());
	}

	@Test
	void savesNewUserAndModiefiesIt() {

		User newUser = createUser();
		User savedUser = userRepository.save(newUser);
		Optional<User> reloadedUser = userRepository.findById(savedUser.getId());

		assertThat(reloadedUser).isNotEmpty();
		assertThat(reloadedUser.get().getId()).isEqualTo(savedUser.getId());

		User modifiedUser = reloadedUser.get();
		modifiedUser.setVorname("modified");
		modifiedUser.setNachname("modified");

		userRepository.save(reloadedUser.get());

		modifiedUser = userRepository.getOne(modifiedUser.getId());
		assertThat(modifiedUser.getVorname()).isEqualTo("modified");
		assertThat(modifiedUser.getNachname()).isEqualTo("modified");
	}

	public static User createUser() {
		return User.builder().vorname("Tom").nachname("Tester").build();
	}
}