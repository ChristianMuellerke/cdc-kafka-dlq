package de.cmuellerke.demo.event;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserReplicationFailedEvent {
    private UserChangedEvent originalEvent;
    private int retryCount;  
    private ZonedDateTime lastRetry;
}
