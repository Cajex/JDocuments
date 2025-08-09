package tech.jilge.server.listeners;

import tech.jilge.server.CloudAPI;
import tech.jilge.server.events.CloudShutdownEvent;

public class CloudShutdownChannelListener {
    public CloudShutdownChannelListener() {
        CloudAPI
                .getInstance()
                .getEventBus()
                .registerListener(CloudShutdownEvent.class, cloudShutdownEvent -> {
                    if (CloudAPI.getInstance().getCloudChannel().getEventLoopGroupMap().isPresent()) {
                        final var map = CloudAPI.getInstance().getCloudChannel().getEventLoopGroupMap().get();
                        map.getKey().shutdownGracefully();
                        map.getValue().shutdownGracefully();
                        CloudAPI.getInstance().getTerminal().send("EventLoop shutdown gracefully.");
                    }
                }
        );
    }
}
