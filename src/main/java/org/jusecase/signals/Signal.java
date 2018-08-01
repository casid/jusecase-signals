package org.jusecase.signals;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Signal<Listener> implements Cloneable {

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

    public Iterable<Listener> getListeners() {
        if (listeners != null) {
            return listeners;
        }
        return Collections.emptyList();
    }

    public int size() {
        return listeners == null ? 0 : listeners.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Signal<Listener> clone() {
        try {
            Signal<Listener> clone = (Signal<Listener>) super.clone();
            clone.listeners = null;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
