package de.cmuellerke.demo.service;

import org.springframework.stereotype.Component;

import de.cmuellerke.demo.event.UserReplicationFailedEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Getter
public class DLQHandler {

    public void handleDLQMessage(UserReplicationFailedEvent failedReplicationEvent) {
    	log.error("received event on DLT! EventId={}", failedReplicationEvent.getOriginalEvent().getUser().getId());
    }
}
