package tech.jilge.server.auth;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ICloudSession {

    public UUID get();

    public LocalDateTime getExpireDateTime();

}
