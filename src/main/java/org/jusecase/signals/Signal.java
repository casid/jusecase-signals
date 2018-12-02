package org.jusecase.signals;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Fast signal implementation
 * Not thread safe, use only in single threaded context, e.g. UI
 */
public class Signal<Listener> implements Iterable<Listener>, Iterator<Listener> {

    private static final int INITIAL_CAPACITY = 4;
    private static final int LOAD_FACTOR = 2;

    private Object[] listeners;
    private int size;
    private int next = -1;
    private int removed;


    public final void add(Listener listener) {
        if (listeners == null) {
            listeners = new Object[INITIAL_CAPACITY];
        } else if (size + 1 > listeners.length) {
            listeners = Arrays.copyOf(listeners, size * LOAD_FACTOR);
        }
        listeners[size++] = listener;
    }

    public final void remove(Listener listener) {
        if (listeners != null) {
            for (int i = 0; i < size; ++i) {
                if (listener == listeners[i]) {
                    if (isIterating()) {
                        listeners[i] = null;
                        ++removed;
                    } else {
                        remove(i--);
                    }
                }
            }
        }
    }

    public final void removeAll() {
        if (listeners != null) {
            for (int i = 0; i < size; ++i) {
                listeners[i] = null;
            }
            size = 0;
            removed = 0;
        }
    }

    public final int size() {
        return size;
    }

    private void cleanup() {
        if (removed > 0) {
            for (int i = 0; i < size; ++i) {
                if (listeners[i] == null) {
                    remove(i--);
                    if (--removed <= 0) {
                        return;
                    }
                }
            }
        }
    }

    private void remove(int index) {
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(listeners, index + 1, listeners, index, numMoved);
        }
        listeners[--size] = null;
    }

    private boolean isIterating() {
        return next >= 0;
    }

    @Override
    public final Iterator<Listener> iterator() {
        if (isIterating()) {
            throw new IllegalStateException("Nested signal dispatch is not supported");
        }
        next = 0;
        return this;
    }

    @Override
    public final boolean hasNext() {
        if (next >= 0) {
            for (; next < size; ++next) {
                if (listeners[next] != null) {
                    return true;
                }
            }

            cleanup();
            next = -1;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Listener next() {
        return (Listener) listeners[next++];
    }
}
