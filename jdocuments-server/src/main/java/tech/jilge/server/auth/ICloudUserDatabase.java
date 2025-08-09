package tech.jilge.server.auth;

import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface ICloudUserDatabase {

    public List<ICloudUser> getCloudUsers();

    public List<ICloudSession> getAvailableSessions();

    public Optional<ICloudSession> getSessionById(final @NonNull UUID uuid);

    public Optional<ICloudUser> getUserBySession(final @NonNull ICloudSession sessionID);

    public Optional<ICloudSession> getSessionByUser(final @NonNull ICloudUser user);

    public Optional<ICloudUser> getUserByEmail(final @NonNull String email);

    public void putSession(final @NonNull ICloudUser user, LocalDateTime expires);

    public void runExpireChecker();

}
