package tech.jilge.server.configuration.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tech.jilge.server.UnsafeInstanceOperations;
import tech.jilge.server.configuration.CloudConfiguration;
import tech.jilge.server.configuration.CloudConfigurationInfo;
import tech.jilge.server.configuration.CloudConfigurationProperty;
import tech.jilge.server.configuration.ICloudConfigurationProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultCloudConfigurationProcessorImpl implements ICloudConfigurationProcessor {

    private final LinkedHashMap<String, Map.Entry<URI, CloudConfiguration>> configurations = new LinkedHashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
    @Override
    public <T extends CloudConfiguration> T open(URI uri, Class<? extends T> tClass) {
        if (tClass.isAnnotationPresent(CloudConfigurationInfo.class)) {
            final String name = tClass.getDeclaredAnnotation(CloudConfigurationInfo.class).name();
            if (Files.exists(Paths.get(uri))) {
                final StringBuilder stringBuilder = new StringBuilder();
                try {
                    final FileInputStream inputStream = new FileInputStream(uri.getPath());
                    int character;
                    while ((character = inputStream.read()) != -1) {
                        stringBuilder.append((char) character);
                    }
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String out = stringBuilder.toString();
                for (Field field : tClass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(CloudConfigurationProperty.class)) {
                        final var property = field.getDeclaredAnnotation(CloudConfigurationProperty.class);
                        out = out.replace(property.name(), field.getName());
                    }
                }
                T object = this.gson.fromJson(out, tClass);
                this.configurations.put(name, Map.entry(uri, object));
                return (T) this.configurations.get(name).getValue();
            } else {
                final T object;
                try {
                    object = tClass.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                final File file = new File(uri);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                this.configurations.put(name, Map.entry(uri, object));
                return object;
            }
        } else
            throw new UnsupportedOperationException("The configuration info is missing in type: " + tClass.getName());
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ExtractMethodRecommender"})
    @Override
    public <T extends CloudConfiguration> T open(String name, URI uri, Class<? extends T> tClass) {
        if (Files.exists(Paths.get(uri))) {
            final StringBuilder stringBuilder = new StringBuilder();
            try {
                final FileInputStream inputStream = new FileInputStream(uri.getPath());
                int character;
                while ((character = inputStream.read()) != -1) {
                    stringBuilder.append((char) character);
                }
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String out = stringBuilder.toString();
            for (final var field : tClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(CloudConfigurationProperty.class)) {
                    final var property = field.getDeclaredAnnotation(CloudConfigurationProperty.class);
                    out = out.replace(property.name(), field.getName());
                }
            }
            T object = this.gson.fromJson(out, tClass);
            this.configurations.put(name, Map.entry(uri, object));
            return object;
        } else {
            T object;
            try {
                object = tClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                object = UnsafeInstanceOperations.construct(tClass);
            }
            final var file = new File(uri);
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.configurations.put(name, Map.entry(uri, object));
            return object;
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
    @Override
    public <T extends CloudConfiguration> T open(String name, URI uri, T object) {
        final var file = new File(uri);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.configurations.put(name, Map.entry(uri, object));
        } else {
            return (T) this.open(name, uri, object.getClass());
        }
        return object;
    }

    @Override
    public void close() {
        for (final var entry : this.configurations.entrySet())
            this.close(entry.getKey());
    }

    @Override
    public void update(String name) {
        final var pair = this.configurations.get(name);
        String out = this.gson.toJson(pair.getValue());
        for (final var field : pair.getValue().getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(CloudConfigurationProperty.class)) {
                final var property = field.getDeclaredAnnotation(CloudConfigurationProperty.class);
                out = out.replace(field.getName(), property.name());
            }
        }
        try {
            final FileOutputStream outputStream = new FileOutputStream(pair.getKey().getPath());
            outputStream.write(out.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update() {
        for (final var name : this.configurations.keySet()) {
            this.update(name);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void close(String name) {
        final var pair = this.configurations.get(name);
        final var file = new File(pair.getKey());
        var out = this.gson.toJson(pair.getValue());
        for (final var field : pair.getValue().getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(CloudConfigurationProperty.class)) {
                final var property = field.getDeclaredAnnotation(CloudConfigurationProperty.class);
                out = out.replace(field.getName(), property.name());
            }
        }
        if (file.exists()) {
            try {
                final var outputStream = new FileOutputStream(pair.getKey().getPath());
                outputStream.write(out.getBytes(StandardCharsets.UTF_8));
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                final var outputStream = new FileOutputStream(pair.getKey().getPath());
                outputStream.write(out.getBytes(StandardCharsets.UTF_8));
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Gson getDefaultGson() {
        return this.gson;
    }

}
