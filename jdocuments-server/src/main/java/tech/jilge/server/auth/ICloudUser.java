package tech.jilge.server.auth;

import lombok.NonNull;

import java.util.Map;

public interface ICloudUser {

    public String getCloudUserEmail();

    public String getCloudUserPassword();

    public void setCloudUserEmail(final @NonNull String email);

    public void setCloudUserPassword(final @NonNull String password);

    public Map<String, String> getUserMap();

}
