package tech.jilge.server.events;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class CloudChannelEvent extends CloudEvent {
    private final ChannelHandlerContext channelHandlerContext;
}
