package de.cmuellerke.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.cmuellerke.demo.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

}
