package de.cmuellerke.demo.event;

import de.cmuellerke.demo.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserChangedEvent {
    private User user;
    private String action;
    private int retryCount;
}
