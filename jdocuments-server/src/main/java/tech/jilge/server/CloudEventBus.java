package tech.jilge.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import tech.jilge.server.events.CloudEvent;

import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public final class CloudEventBus {
    private final LinkedList<Map.Entry<Class<? extends CloudEvent>, Consumer<CloudEvent>>> listeners = new LinkedList<>();

    @SuppressWarnings("unchecked")
    public<T extends CloudEvent> CloudEventBus registerListener(Class<T> clazz, Consumer<T> listener) {
        listeners.add(Map.entry(clazz, cloudEvent -> listener.accept((T) cloudEvent)));
        return this;
    }

    public<T extends CloudEvent> void callEvent(final @NonNull T event) {
        for (final var listener : listeners) {
            if (listener.getKey().isAssignableFrom(event.getClass()))
                listener.getValue().accept(event);
        }
    }


}
