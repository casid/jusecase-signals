package org.jusecase.signals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Signal<Listener> implements Cloneable {

    private static final int INITIAL_CAPACITY = 4;
    private static final int LOAD_FACTOR = 2;

    private Listener[] listeners;
    private int size;

    public void add(Listener listener) {
        if (listeners == null) {
            //noinspection unchecked
            listeners = (Listener[]) new Object[INITIAL_CAPACITY];
        } else if (size + 1 > listeners.length) {
            listeners = Arrays.copyOf(listeners, size * LOAD_FACTOR);
        }
        listeners[size++] = listener;
    }

    public void remove(Listener listener) {
        if (listeners != null) {
            for (int i = 0; i < size; ++i) {
                if (listeners[i] == listener) {
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
        if (listeners != null) {
            for (int i = 0; i < size; ++i) {
                signal.accept(listeners[i]);
            }
        }
    }

    public int size() {
        return size;
    }

    /**
     * For memory efficient dispatch utilities in subclasses
     */
    protected Listener[] getListeners() {
        return listeners;
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
