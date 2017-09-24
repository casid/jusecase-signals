package org.jusecase.signals;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Signal<Listener> {

    private List<Listener> listeners;

    public void add(Listener listener) {
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>(Collections.singletonList(listener));
        } else {
            listeners.add(listener);
        }
    }

    public void remove(Listener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public void removeAll() {
        if (listeners != null) {
            listeners.clear();
        }
    }

    public void dispatch(Consumer<Listener> signal) {
        if (listeners != null) {
            listeners.forEach(signal);
        }
    }
}
