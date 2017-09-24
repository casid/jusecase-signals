package org.jusecase.signals;


import org.junit.Test;
import org.jusecase.signals.example.ResizeListener;

import static org.assertj.core.api.Assertions.assertThat;


public class SignalTest {

    Signal<ResizeListener> signal = new Signal<>();

    int calledListeners;
    int width;
    int height;

    ResizeListener listener = (w, h) -> {
        calledListeners++;
        width = w;
        height = h;
    };

    @Test
    public void dispatch_noListeners() {
        whenSignalIsDispatched();
        assertThat(calledListeners).isEqualTo(0);
    }

    @Test
    public void dispatch_listenerAddedAndRemoved() {
        signal.add(listener);
        signal.remove(listener);

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(0);
    }

    @Test
    public void dispatch_listenerRemovedDuringDispatchAtEnd() {
        signal.add(listener);
        signal.add((w, h) -> signal.remove(listener));

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(1);
    }

    @Test
    public void dispatch_listenerRemovedDuringDispatchAtBegin() {
        signal.add((w, h) -> signal.remove(listener));
        signal.add(listener);

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(1);
    }

    @Test
    public void dispatch_oneListener() {
        signal.add(listener);

        whenSignalIsDispatched(800, 600);

        assertThat(calledListeners).isEqualTo(1);
        assertThat(width).isEqualTo(800);
        assertThat(height).isEqualTo(600);
    }

    @Test
    public void dispatch_twoListeners() {
        signal.add(listener);
        signal.add(listener);

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(2);
    }

    @Test
    public void removeAll() {
        signal.add(listener);
        signal.add(listener);
        signal.add(listener);
        signal.removeAll();

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(0);
    }

    @Test
    public void nullListeners_doNotCauseException() {
        signal.removeAll();
        signal.remove(listener);

        whenSignalIsDispatched();
    }

    @Test
    public void clone_listenerReferencesAreNotLeaked() {
        signal.add(listener);
        Signal<ResizeListener> clone = signal.clone();

        assertThat(signal.size()).isEqualTo(1);
        assertThat(clone.size()).isEqualTo(0);
    }

    private void whenSignalIsDispatched(int width, int height) {
        signal.dispatch(s -> s.onResize(width, height));
    }

    private void whenSignalIsDispatched() {
        whenSignalIsDispatched(0, 0);
    }
}