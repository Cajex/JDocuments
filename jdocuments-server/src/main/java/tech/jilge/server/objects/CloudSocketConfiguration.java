package tech.jilge.server.objects;

import tech.jilge.server.configuration.CloudConfiguration;
import tech.jilge.server.configuration.CloudConfigurationInfo;
import tech.jilge.server.configuration.CloudConfigurationProperty;

@CloudConfigurationInfo(name = "configuration")
public record CloudSocketConfiguration(@CloudConfigurationProperty(name = "server.host") String host,
                                       @CloudConfigurationProperty(name = "server.port") int port,
                                       @CloudConfigurationProperty(name = "configuration.debug") boolean debug,
                                       @CloudConfigurationProperty(name = "configuration.threads") int nThreads) implements CloudConfiguration {

}
