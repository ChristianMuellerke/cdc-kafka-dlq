package de.cmuellerke.demo.event;

import java.time.ZonedDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserReplicationFailedEvent {
    private UserChangedEvent originalEvent;
    private int retryCount;  
    private ZonedDateTime lastRetry;
}
