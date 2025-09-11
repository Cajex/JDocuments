package tech.jilge.server;

import lombok.Getter;
import tech.jilge.server.auth.ICloudUserDatabase;
import tech.jilge.server.auth.impl.DefaultCloudUserDatabaseImpl;
import tech.jilge.server.configuration.ICloudConfigurationProcessor;
import tech.jilge.server.configuration.impl.DefaultCloudConfigurationProcessorImpl;
import tech.jilge.server.events.CloudShutdownEvent;
import tech.jilge.server.events.CloudTerminalCommandEvent;
import tech.jilge.server.listeners.CloudChannelReadListener;
import tech.jilge.server.listeners.CloudReadCommandListener;
import tech.jilge.server.listeners.CloudShutdownChannelListener;
import tech.jilge.server.listeners.CloudUnexpectedExceptionListener;
import tech.jilge.server.log.IOTerminal;
import tech.jilge.server.objects.CloudSocketConfiguration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

@Getter
public final class CloudAPI {
    @Getter
    private static CloudAPI instance;

    private final ICloudConfigurationProcessor cloudConfigurationProcessor;
    private final CloudEventBus eventBus;
    private final CloudSocketConfiguration cloudChannelConfiguration;
    private final HttpCloudChannel cloudChannel;
    private final IOTerminal terminal;
    private final Map<String, String> mineTypeMap;
    private final ScheduledExecutorService executorService;
    private ICloudUserDatabase userDatabase;

    private CloudAPI(ICloudConfigurationProcessor cloudConfigurationProcessor, CloudEventBus eventBus, CloudSocketConfiguration cloudChannelConfiguration, HttpCloudChannel cloudChannel, IOTerminal terminal, Map<String, String> mineTypeMap, ScheduledExecutorService executorService) {
        this.cloudConfigurationProcessor = cloudConfigurationProcessor;
        this.eventBus = eventBus;
        this.cloudChannelConfiguration = cloudChannelConfiguration;
        this.cloudChannel = cloudChannel;
        this.terminal = terminal;
        this.mineTypeMap = mineTypeMap;
        this.executorService = executorService;
    }

    public static class Factory {
        public static void create() throws IOException {

            final var configurationProcessor = new DefaultCloudConfigurationProcessorImpl();
            final var cloudServerConfiguration = configurationProcessor
                    .open("configuration",
                            Path.of("configuration.json").toUri(),
                            new CloudSocketConfiguration("127.0.0.1", 2000, true, 5)
                    );
            CloudAPI.instance = new CloudAPI(
                    configurationProcessor,
                    new CloudEventBus(),
                    cloudServerConfiguration,
                    new HttpCloudChannel(),
                    new IOTerminal(),
                    Map.<String, String>ofEntries(
                            Map.entry("html", "text/html"),
                            Map.entry("htm", "text/html"),
                            Map.entry("css", "text/css"),
                            Map.entry("js", "application/javascript"),
                            Map.entry("ts", "application/x-typescript"),
                            Map.entry("json", "application/json"),
                            Map.entry("xml", "application/xml"),
                            Map.entry("txt", "text/plain"),
                            Map.entry("csv", "text/csv"),
                            Map.entry("md", "text/markdown"),
                            Map.entry("yml", "application/x-yaml"),
                            Map.entry("yaml", "application/x-yaml"),
                            Map.entry("wasm", "application/wasm"),

                            Map.entry("png", "image/png"),
                            Map.entry("jpg", "image/jpeg"),
                            Map.entry("jpeg", "image/jpeg"),
                            Map.entry("gif", "image/gif"),
                            Map.entry("bmp", "image/bmp"),
                            Map.entry("svg", "image/svg+xml"),
                            Map.entry("ico", "image/x-icon"),
                            Map.entry("webp", "image/webp"),
                            Map.entry("tif", "image/tiff"),
                            Map.entry("tiff", "image/tiff"),
                            Map.entry("heic", "image/heic"),
                            Map.entry("heif", "image/heif"),

                            Map.entry("woff", "font/woff"),
                            Map.entry("woff2", "font/woff2"),
                            Map.entry("ttf", "font/ttf"),
                            Map.entry("otf", "font/otf"),
                            Map.entry("eot", "application/vnd.ms-fontobject"),

                            Map.entry("mp3", "audio/mpeg"),
                            Map.entry("wav", "audio/wav"),
                            Map.entry("ogg", "audio/ogg"),
                            Map.entry("oga", "audio/ogg"),
                            Map.entry("m4a", "audio/mp4"),
                            Map.entry("aac", "audio/aac"),
                            Map.entry("flac", "audio/flac"),

                            Map.entry("mp4", "video/mp4"),
                            Map.entry("webm", "video/webm"),
                            Map.entry("ogv", "video/ogg"),
                            Map.entry("mov", "video/quicktime"),
                            Map.entry("avi", "video/x-msvideo"),
                            Map.entry("mkv", "video/x-matroska"),

                            Map.entry("pdf", "application/pdf"),
                            Map.entry("doc", "application/msword"),
                            Map.entry("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
                            Map.entry("xls", "application/vnd.ms-excel"),
                            Map.entry("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                            Map.entry("ppt", "application/vnd.ms-powerpoint"),
                            Map.entry("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),

                            Map.entry("zip", "application/zip"),
                            Map.entry("rar", "application/vnd.rar"),
                            Map.entry("7z", "application/x-7z-compressed"),
                            Map.entry("tar", "application/x-tar"),
                            Map.entry("gz", "application/gzip"),
                            Map.entry("bz2", "application/x-bzip2"),

                            Map.entry("apk", "application/vnd.android.package-archive"),
                            Map.entry("exe", "application/vnd.microsoft.portable-executable"),
                            Map.entry("bin", "application/octet-stream")),
                    Executors.newScheduledThreadPool(cloudServerConfiguration.nThreads())
            );
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                CloudAPI.getInstance().getEventBus().callEvent(new CloudShutdownEvent());
                CloudAPI.getInstance().onDisable();
            }));
            CloudAPI.getInstance().onEnable();
        }
    }

    public void onEnable() {
        this.userDatabase = DefaultCloudUserDatabaseImpl.Factory.open();
        this.getTerminal().print();

        this.userDatabase.runExpireChecker();
        this.getTerminal().send("Available Sessions loaded: [§a" + this.userDatabase.getCloudUsers().size() + "§r]");

        this.getTerminal().send("HttpServer blocked...");



        this.getTerminal().push(input -> this.getEventBus().callEvent(new CloudTerminalCommandEvent(input)));

        new CloudUnexpectedExceptionListener();
        new CloudShutdownChannelListener();
        new CloudChannelReadListener();
        new CloudReadCommandListener();

        this.debugQuery((Function<Void, Void>) unused -> {
            this.getTerminal().send("Current user directory: [" + System.getProperty("user.dir") + "]");
            for (final var value : HttpImplementedURI.values()) {
                for (final var availableURI : value.getUri()) {
                    for (final var file : Objects.requireNonNull(value.getTargetPackage().listFiles())) {
                        this.getTerminal().send("Available URI [§e" + availableURI.getPath() + file.getName() + "§r].");
                    }
                }
            }
            return null;
        });

        try {
            this.getCloudChannel().run(this.getCloudChannelConfiguration());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void onDisable() {

        this.getCloudConfigurationProcessor().close();
        this.getTerminal().send("cached configurations §aclosed§r");
    }

    @SuppressWarnings("UnusedReturnValue")
    public<R> R debugQuery(final Function<Void, R> fun) {
        if (this.getCloudChannelConfiguration().debug())
            return fun.apply(null);
        return null;
    }

}
