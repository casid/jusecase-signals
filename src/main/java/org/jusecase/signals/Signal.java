package org.jusecase.signals;

import net.jodah.typetools.TypeResolver;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public abstract class Signal<Event> {

    private final EventPool eventPool;
    private final List<Consumer<Event>> listeners;
    private final Class<Event> eventClass;

    public Signal() {
        this(EventPool.DEFAULT);
    }

    @SuppressWarnings("unchecked")
    public Signal(EventPool eventPool) {
        this.eventPool = eventPool;
        this.listeners = new CopyOnWriteArrayList<>();
        this.eventClass = (Class<Event>) TypeResolver.resolveRawArguments(Signal.class, getClass())[0];
    }

    public void add(Consumer<Event> listener) {
        listeners.add(listener);
    }

    public void remove(Consumer<Event> listener) {
        listeners.remove(listener);
    }

    public void removeAll() {
        listeners.clear();
    }

    protected void dispatch(Event event) {
        listeners.forEach(listener -> listener.accept(event));
    }

    protected void dispatch(Consumer<Event> eventSetup) {
        if (!listeners.isEmpty()) {
            Event event = eventPool.getUnusedEvent(eventClass);
            if (event == null) {
                event = createEvent();
            }
            eventSetup.accept(event);

            dispatch(event);

            eventPool.addUnusedEvent(eventClass, event);
        }
    }

    protected Event createEvent() {
        try {
            return eventClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create event instance. You probably need to override createEvent() in your signal subclass.");
        }
    }
}
