package tech.jilge.server.listeners;

import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import tech.jilge.server.CloudAPI;
import tech.jilge.server.HttpImplementedURI;
import tech.jilge.server.events.CloudChannelReadEvent;
import tech.jilge.server.log.IOTerminal;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

public class CloudChannelReadListener {
    @SuppressWarnings({"resource", "OptionalGetWithoutIsPresent"})
    public CloudChannelReadListener() {
        CloudAPI
                .getInstance()
                .getEventBus()
                .registerListener(CloudChannelReadEvent.class,
                        cloudChannelReadEvent -> {
                            CloudAPI.getInstance().debugQuery((Function<Void, Void>) unused -> {
                                CloudAPI.getInstance().getTerminal().send("HttpRequest: " + cloudChannelReadEvent.getFullHttpRequest().toString());
                                return null;
                            });

                            final var requestedURI = cloudChannelReadEvent.getFullHttpRequest().uri();

                            /* File handler */
                            final var requestFileQuery = CloudAPI
                                    .getInstance()
                                    .getMineTypeMap()
                                    .keySet()
                                    .stream()
                                    .anyMatch(s -> requestedURI.endsWith("." + s));
                            if (requestFileQuery) {
                                for (final var value : HttpImplementedURI.values()) {
                                    for (final var availableURI : value.getUri())
                                        for (final var requestedFile : Objects.requireNonNull(value.getTargetPackage().listFiles())) {
                                            if (requestedFile.isFile()) {
                                                final var referer = cloudChannelReadEvent.getFullHttpRequest().headers().get(HttpHeaderNames.REFERER);
                                                if (referer != null) {
                                                    final var expected = referer.substring(0, referer.lastIndexOf("/") + 1);

                                                    if (expected.endsWith(availableURI.getPath())) {
                                                        if (requestedURI.contains(/*availableURI.getPath() + */requestedFile.getName())) {

                                                            try {
                                                                final var httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                                                                final var raf = new RandomAccessFile(requestedFile, "r");

                                                                httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, CloudAPI
                                                                        .getInstance()
                                                                        .getMineTypeMap()
                                                                        .get(
                                                                                requestedFile
                                                                                        .getName()
                                                                                        .substring(requestedFile.getName().lastIndexOf('.') + 1)
                                                                        )
                                                                );
                                                                httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, raf.length());
                                                                cloudChannelReadEvent.getChannelHandlerContext().write(httpResponse);

                                                                cloudChannelReadEvent
                                                                        .getChannelHandlerContext()
                                                                        .write(
                                                                                new DefaultFileRegion(
                                                                                        raf.getChannel(),
                                                                                        0, raf.length()),
                                                                                cloudChannelReadEvent
                                                                                        .getChannelHandlerContext()
                                                                                        .newProgressivePromise()
                                                                        );

                                                                cloudChannelReadEvent
                                                                        .getChannelHandlerContext()
                                                                        .writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

                                                                CloudAPI.getInstance().debugQuery((Function<Void, Void>) unused -> {
                                                                    CloudAPI.getInstance().getTerminal().send("WebServer accepted File request and sent: [§a" + requestedFile.getName() + "§r].");
                                                                    return null;
                                                                });

                                                            } catch (IOException e) {
                                                                throw new RuntimeException(e);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                }
                            } else {
                                for (final var values : HttpImplementedURI.values()) {
                                    for (final var availableURI : values.getUri()) {
                                        if (availableURI.getPath().equals(requestedURI)) {
                                            final var entryFile = Arrays.stream(Objects
                                                    .requireNonNull(
                                                            values
                                                                    .getTargetPackage()
                                                                    .listFiles()))
                                                    .filter(file -> file.getName().endsWith(".html")).findFirst().get();

                                            try {
                                                final var httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                                                final var raf = new RandomAccessFile(entryFile, "r");

                                                httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, CloudAPI
                                                        .getInstance()
                                                        .getMineTypeMap()
                                                        .get(
                                                                entryFile
                                                                        .getName()
                                                                        .substring(entryFile.getName().lastIndexOf('.') + 1)
                                                        )
                                                );
                                                httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, raf.length());
                                                cloudChannelReadEvent.getChannelHandlerContext().write(httpResponse);

                                                cloudChannelReadEvent
                                                        .getChannelHandlerContext()
                                                        .write(
                                                                new DefaultFileRegion(
                                                                        raf.getChannel(),
                                                                        0, raf.length()),
                                                                cloudChannelReadEvent
                                                                        .getChannelHandlerContext()
                                                                        .newProgressivePromise()
                                                        );

                                                cloudChannelReadEvent
                                                        .getChannelHandlerContext()
                                                        .writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

                                                CloudAPI.getInstance().debugQuery((Function<Void, Void>) unused -> {
                                                    CloudAPI.getInstance().getTerminal().send("WebServer accepted File request and redirected: [§a" + entryFile.getName() + "§r].");
                                                    return null;
                                                });


                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }

                                        }
                                    }
                                }
                            }

                        });
    }
}
