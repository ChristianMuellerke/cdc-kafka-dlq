package de.cmuellerke.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.cmuellerke.demo.entity.OutboxEntry;

public interface OutboxRepository extends JpaRepository<OutboxEntry, UUID> {

}
