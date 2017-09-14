package org.jusecase.signals;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public abstract class Signal<Event> {

    private final List<Consumer<Event>> listeners;

    public Signal() {
        listeners = new CopyOnWriteArrayList<>();
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
            Event event = createEvent();
            eventSetup.accept(event);
            dispatch(event);
        }
    }

    protected abstract Event createEvent();
}
