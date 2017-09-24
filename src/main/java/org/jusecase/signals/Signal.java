package org.jusecase.signals;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Signal<Listener> {

    private final List<Listener> listeners;

    public Signal() {
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public void add(Listener listener) {
        listeners.add(listener);
    }

    public void remove(Listener listener) {
        listeners.remove(listener);
    }

    public void removeAll() {
        listeners.clear();
    }

    public void dispatch(Consumer<Listener> signal) {
        listeners.forEach(signal);
    }
}
