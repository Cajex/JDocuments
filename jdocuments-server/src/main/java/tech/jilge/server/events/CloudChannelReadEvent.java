package tech.jilge.server.events;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Getter;

@Getter
public class CloudChannelReadEvent extends CloudChannelEvent {
    private final FullHttpRequest fullHttpRequest;

    public CloudChannelReadEvent(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {
        super(channelHandlerContext);
        this.fullHttpRequest = fullHttpRequest;
    }
}

