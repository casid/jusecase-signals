package org.jusecase.signals;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.jusecase.signals.example.ResizeListener;

import static org.assertj.core.api.Assertions.assertThat;


class SignalTest {

    Signal<ResizeListener> signal = new Signal<>(ResizeListener.class);

    int calledListeners;
    int width;
    int height;

    ResizeListener listener = (w, h) -> {
        calledListeners++;
        width = w;
        height = h;
    };

    @Test
    void dispatch_noListeners() {
        whenSignalIsDispatched();
        assertThat(calledListeners).isEqualTo(0);
    }

    @Test
    void dispatch_listenerAddedAndRemoved() {
        signal.add(listener);
        signal.remove(listener);

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(0);
    }

    @Test
    void dispatch_listenerRemovedDuringDispatchAtEnd() {
        signal.add(listener);
        signal.add((w, h) -> signal.remove(listener));

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(1);
    }

    @Test
    void dispatch_listenerRemovedDuringDispatchAtBegin() {
        signal.add((w, h) -> signal.remove(listener));
        signal.add(listener);

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(0);
    }

    @Test
    void dispatch_oneListener() {
        signal.add(listener);

        whenSignalIsDispatched(800, 600);

        assertThat(calledListeners).isEqualTo(1);
        assertThat(width).isEqualTo(800);
        assertThat(height).isEqualTo(600);
    }

    @Test
    void dispatch_twoListeners() {
        signal.add(listener);
        signal.add(listener);

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(2);
    }

    @Test
    void dispatch_manyListeners() {
        for (int i = 0; i < 10; ++i) {
            signal.add(listener);
        }

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(10);
    }

    @Test
    void removeAll() {
        signal.add(listener);
        signal.add(listener);
        signal.add(listener);
        signal.removeAll();

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(0);
    }

    @Test
    void nullListeners_doNotCauseException() {
        signal.removeAll();
        signal.remove(listener);

        whenSignalIsDispatched();
    }

    @Disabled // Oh no!
    @Test
    void removeMethodReference() {
        signal.add(this::onResizeMethodReference);
        signal.remove(this::onResizeMethodReference);
        assertThat(signal.size()).isEqualTo(0);
    }

    void whenSignalIsDispatched(int width, int height) {
        signal.dispatch(s -> s.onResize(width, height));
    }

    void whenSignalIsDispatched() {
        whenSignalIsDispatched(0, 0);
    }

    @SuppressWarnings("unused")
    void onResizeMethodReference(int w, int h) {
    }
}