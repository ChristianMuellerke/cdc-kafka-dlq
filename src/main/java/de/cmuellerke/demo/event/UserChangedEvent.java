package de.cmuellerke.demo.event;

import de.cmuellerke.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserChangedEvent {
    private User user;
    private String action;
    private int retryCount;
}
