package de.cmuellerke.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.cmuellerke.demo.entity.ReplicatedUser;

/**
 * This is a repository that holds user states
 */
public interface ReplicatedUserRepository extends JpaRepository<ReplicatedUser, UUID> {

}
