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
    private int[] nestedNext;
    private int nestedCount;
    private int removed;


    public final void add(Listener listener) {
        if (listeners == null) {
            listeners = new Object[INITIAL_CAPACITY];
        } else if (size + 1 > listeners.length) {
            listeners = Arrays.copyOf(listeners, size * LOAD_FACTOR);
        }
        listeners[size++] = listener;
    }

    @SuppressWarnings("unchecked")
    public final void addFirst(Listener listener) {
        if (listeners == null) {
            add(listener);
        } else {
            Object[] oldListeners = listeners;
            listeners = null;
            size = 0;

            add(listener);
            for (Object oldListener : oldListeners) {
                add((Listener)oldListener);
            }
        }
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
        pushIteration();
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

            popIteration();
        }
        return false;
    }

    private void pushIteration() {
        if (isIterating()) {
            if (nestedNext == null) {
                nestedNext = new int[2];
            } else if (nestedCount + 1 > nestedNext.length) {
                nestedNext = Arrays.copyOf(nestedNext, nestedCount * LOAD_FACTOR);
            }
            nestedNext[nestedCount++] = next;
        }
        next = 0;
    }

    private void popIteration() {
        if (nestedCount > 0) {
            next = nestedNext[--nestedCount];
        } else {
            cleanup();
            next = -1;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Listener next() {
        return (Listener) listeners[next++];
    }
}
