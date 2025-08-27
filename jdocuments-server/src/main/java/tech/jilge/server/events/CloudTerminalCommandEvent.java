package tech.jilge.server.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CloudTerminalCommandEvent extends CloudEvent {
    private final String command;
}
