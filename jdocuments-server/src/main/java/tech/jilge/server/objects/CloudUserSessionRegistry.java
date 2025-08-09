package tech.jilge.server.objects;

import tech.jilge.server.auth.ICloudSession;
import tech.jilge.server.auth.ICloudUser;
import tech.jilge.server.configuration.CloudConfiguration;
import tech.jilge.server.configuration.CloudConfigurationInfo;
import tech.jilge.server.configuration.CloudConfigurationProperty;

import java.util.List;
import java.util.Map;

@CloudConfigurationInfo(name = "UserDatabase")
public record CloudUserSessionRegistry(
        @CloudConfigurationProperty(name = "database") List<Map.Entry<ICloudUser, ICloudSession>> userSessionPool)
        implements CloudConfiguration {

}
