package tech.jilge.server.auth.impl;

import lombok.Setter;
import tech.jilge.server.auth.ICloudSession;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
public class DefaultCloudSessionImpl implements ICloudSession {
    private final UUID uuid;
    private final LocalDateTime localDateTime;

    public DefaultCloudSessionImpl(UUID uuid, LocalDateTime localDateTime) {
        this.uuid = uuid;
        this.localDateTime = localDateTime;
    }

    @Override
    public UUID get() {
        return this.uuid;
    }

    @Override
    public LocalDateTime getExpireDateTime() {
        return this.localDateTime;
    }
}
