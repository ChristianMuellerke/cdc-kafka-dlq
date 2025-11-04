create sequence hibernate_sequence start with 1 increment by 1;

-- das ist die eigentliche Tabelle, die den originalen Bestand hält
-- von dieser wollen wir eine Replik versorgen
-- die Tabelle erhält noch einen Trigger, der dann in die outbox schreiben soll
create table if not exists users (
    id uuid not null, 
    vorname varchar(100) not null, 
    nachname varchar(100) not null, 
    primary key (id)
);

-- die outbox nimmt die vom Trigger erstellten Einträge auf
create table if not exists users_outbox (
    id uuid not null,
    tspInserted TIMESTAMP WITH TIME ZONE not null, 
    primary key (id)
);

-- hier kommt der replizierte Bestand rein
create table if not exists users_replica (
    id uuid not null, 
    vorname varchar(100) not null, 
    nachname varchar(100) not null, 
    tspInserted TIMESTAMP WITH TIME ZONE not null, 
    primary key (id)
);

-- DLQ Nachrichten kommen hier herein
create table if not exists users_replica_dlq (
    id uuid not null,
    tspInserted TIMESTAMP WITH TIME ZONE not null, 
    payload text,
    retryCount int, 
    primary key (id)
);
