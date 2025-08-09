package tech.jilge.server.auth.impl;

import lombok.Getter;
import lombok.NonNull;
import tech.jilge.server.CloudAPI;
import tech.jilge.server.auth.ICloudSession;
import tech.jilge.server.auth.ICloudUser;
import tech.jilge.server.auth.ICloudUserDatabase;
import tech.jilge.server.objects.CloudUserSessionRegistry;

import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public class DefaultCloudUserDatabaseImpl implements ICloudUserDatabase {
    private final CloudUserSessionRegistry registry;

    private DefaultCloudUserDatabaseImpl(CloudUserSessionRegistry registry) {
        this.registry = registry;
    }

    public static class Factory {
        public static ICloudUserDatabase open() {
            final var object = CloudAPI
                    .getInstance()
                    .getCloudConfigurationProcessor()
                    .open(
                            "UserDatabase",
                            Path.of("UserDatabase.json").toUri(),
                            new CloudUserSessionRegistry(new ArrayList<>()
                            )
                    );
            return new DefaultCloudUserDatabaseImpl(object);
        }

        public static void update() {
            CloudAPI
                    .getInstance()
                    .getCloudConfigurationProcessor()
                    .update("UserDatabase");
        }
    }

    @Override
    public List<ICloudUser> getCloudUsers() {
        return this.getRegistry()
                .userSessionPool()
                .stream()
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public List<ICloudSession> getAvailableSessions() {
        return this.getRegistry()
                .userSessionPool()
                .stream()
                .map(Map.Entry::getValue)
                .toList();
    }

    @Override
    public Optional<ICloudSession> getSessionById(@NonNull UUID uuid) {
        return this.getRegistry()
                .userSessionPool()
                .stream()
                .filter(iCloudUserICloudSessionEntry ->
                        iCloudUserICloudSessionEntry
                                .getValue()
                                .get()
                                .equals(uuid))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    @Override
    public Optional<ICloudUser> getUserBySession(@NonNull ICloudSession sessionID) {
        return this
                .getRegistry()
                .userSessionPool()
                .stream()
                .filter(iCloudUserICloudSessionEntry ->
                        iCloudUserICloudSessionEntry
                                .getValue()
                                .get()
                                .equals(sessionID.get()))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    @Override
    public Optional<ICloudSession> getSessionByUser(@NonNull ICloudUser user) {
        return this
                .getRegistry()
                .userSessionPool()
                .stream()
                .filter(iCloudUserICloudSessionEntry -> iCloudUserICloudSessionEntry
                        .getKey()
                        .getCloudUserEmail()
                        .equals(user.getCloudUserEmail()))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    @Override
    public Optional<ICloudUser> getUserByEmail(@NonNull String email) {
        return this
                .getRegistry()
                .userSessionPool()
                .stream()
                .filter(iCloudUserICloudSessionEntry -> iCloudUserICloudSessionEntry
                        .getKey()
                        .getCloudUserEmail()
                        .equals(email))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    @Override
    public void putSession(@NonNull ICloudUser user, LocalDateTime expires) {
        this
                .getRegistry()
                .userSessionPool()
                .stream()
                .filter(iCloudUserICloudSessionEntry ->
                        iCloudUserICloudSessionEntry
                                .getKey()
                                .getCloudUserEmail()
                                .equals(user.getCloudUserEmail()))
                .findAny().orElseThrow().setValue(new DefaultCloudSessionImpl(UUID.randomUUID(), expires));
    }

    @Override
    public void runExpireChecker() {
        CloudAPI.getInstance().getExecutorService().scheduleAtFixedRate(() -> {
            final var currentDateTime = LocalDateTime.now();
            for (final var entry : DefaultCloudUserDatabaseImpl.this.getRegistry().userSessionPool()) {
                if (entry.getValue().getExpireDateTime().isAfter(currentDateTime))
                    entry.setValue(null);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

}
