package org.jusecase.signals;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Signal<Listener> implements Cloneable {

    private static final int INITIAL_CAPACITY = 4;
    private static final int LOAD_FACTOR = 2;

    // These are protected for dispatch utility methods in subclasses without lambda generation
    protected Listener[] listeners;
    protected int size;

    private final Class<Listener> listenerClass;


    public Signal(Class<Listener> listenerClass) {
        this.listenerClass = listenerClass;
    }

    public void add(Listener listener) {
        if (listeners == null) {
            //noinspection unchecked
            listeners = (Listener[]) Array.newInstance(listenerClass, INITIAL_CAPACITY);
        } else if (size + 1 > listeners.length) {
            listeners = Arrays.copyOf(listeners, size * LOAD_FACTOR);
        }
        listeners[size++] = listener;
    }

    public void remove(Listener listener) {
        if (listeners != null) {
            for (int i = 0; i < size; ++i) {
                if (listener.equals(listeners[i])) {
                    remove(i);
                    break;
                }
            }
        }
    }

    public void removeAll() {
        if (listeners != null) {
            listeners = null;
            size = 0;
        }
    }

    public void dispatch(Consumer<Listener> signal) {
        for (int i = 0; i < size; ++i) {
            signal.accept(listeners[i]);
        }
    }

    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Signal<Listener> clone() {
        try {
            Signal<Listener> clone = (Signal<Listener>) super.clone();
            clone.listeners = null;
            clone.size = 0;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private void remove(int index) {
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(listeners, index + 1, listeners, index, numMoved);
        }
        listeners[--size] = null;
    }
}
