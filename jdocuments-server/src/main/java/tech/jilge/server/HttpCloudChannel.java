package tech.jilge.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.Getter;
import lombok.NonNull;
import tech.jilge.server.events.CloudChannelReadEvent;
import tech.jilge.server.log.IOTerminal;
import tech.jilge.server.objects.CloudSocketConfiguration;

import java.util.Map;
import java.util.Optional;

@Getter
public final class HttpCloudChannel {

    private final int maxContentLength = 10*1024*1024;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Map.Entry<MultiThreadIoEventLoopGroup, MultiThreadIoEventLoopGroup>> eventLoopGroupMap = Optional.empty();

    @SuppressWarnings("deprecation")
    public void run(final @NonNull CloudSocketConfiguration configuration) throws InterruptedException {
        final var epollQuery = Epoll.isAvailable();
        final var eventLoopGroup = Map.entry(epollQuery ? new EpollEventLoopGroup(1) : new NioEventLoopGroup(1),
                epollQuery ? new EpollEventLoopGroup(1) : new NioEventLoopGroup(1));
        eventLoopGroupMap = Optional.of(eventLoopGroup);
        try {
            CloudAPI.getInstance().getTerminal().send("EventLoopGroup §asetup§r and channel bound on: [§a" + CloudAPI.getInstance().getCloudChannelConfiguration().toString() + "§r]");
            new ServerBootstrap()
                    .group(eventLoopGroup.getKey(), eventLoopGroup.getValue())
                    .channel(epollQuery ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(HttpCloudChannel.this.getMaxContentLength()))
                                    .addLast(new HttpCloudChannelHandler());
                        }
                    }).bind(configuration.host(), configuration.port()).sync().channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static final class HttpCloudChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            super.channelRegistered(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            super.channelUnregistered(ctx);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {
            CloudAPI.getInstance().getEventBus().callEvent(new CloudChannelReadEvent(channelHandlerContext, fullHttpRequest));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            CloudAPI.getInstance().getTerminal().send(cause.getMessage(), IOTerminal.Output.ERROR);
        }

    }
}
