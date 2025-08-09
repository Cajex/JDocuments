package tech.jilge.server.auth.impl;

import lombok.NonNull;
import tech.jilge.server.auth.ICloudUser;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultCloudUserImpl implements ICloudUser {
    private String email;
    private String password;
    private final LinkedHashMap<String, String> userMap;

    public DefaultCloudUserImpl(String email, String password, LinkedHashMap<String, String> userMap) {
        this.email = email;
        this.password = password;
        this.userMap = userMap;
    }

    @Override
    public String getCloudUserEmail() {
        return this.email;
    }

    @Override
    public String getCloudUserPassword() {
        return this.password;
    }

    @Override
    public void setCloudUserEmail(@NonNull String email) {
        this.email = email;
    }

    @Override
    public void setCloudUserPassword(@NonNull String password) {
        this.password = password;
    }

    @Override
    public Map<String, String> getUserMap() {
        return this.userMap;
    }
}
