package de.cmuellerke.demo.entity;

import java.sql.Timestamp;
import java.util.UUID;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users_outbox")
@Data
@NoArgsConstructor
public class OutboxEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Nonnull
    private UUID id;
    private Timestamp tspInserted;
}
