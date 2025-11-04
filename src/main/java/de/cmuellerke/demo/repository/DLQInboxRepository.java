package de.cmuellerke.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.cmuellerke.demo.entity.DLQInboxEntry;

public interface DLQInboxRepository extends JpaRepository<DLQInboxEntry, UUID> {

}
