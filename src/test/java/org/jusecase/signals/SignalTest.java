package org.jusecase.signals;


import org.junit.Test;
import org.jusecase.signals.example.ResizeListener;

import static org.assertj.core.api.Assertions.assertThat;


public class SignalTest {

    Signal<ResizeListener> resizeSignal = new Signal<>();

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
        resizeSignal.add(listener);
        resizeSignal.remove(listener);

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(0);
    }

    @Test
    public void dispatch_listenerRemovedDuringDispatchAtEnd() {
        resizeSignal.add(listener);
        resizeSignal.add((w, h) -> resizeSignal.remove(listener));

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(1);
    }

    @Test
    public void dispatch_listenerRemovedDuringDispatchAtBegin() {
        resizeSignal.add((w, h) -> resizeSignal.remove(listener));
        resizeSignal.add(listener);

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(1);
    }

    @Test
    public void dispatch_oneListener() {
        resizeSignal.add(listener);

        whenSignalIsDispatched(800, 600);

        assertThat(calledListeners).isEqualTo(1);
        assertThat(width).isEqualTo(800);
        assertThat(height).isEqualTo(600);
    }

    @Test
    public void dispatch_twoListeners() {
        resizeSignal.add(listener);
        resizeSignal.add(listener);

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(2);
    }

    @Test
    public void removeAll() {
        resizeSignal.add(listener);
        resizeSignal.add(listener);
        resizeSignal.add(listener);
        resizeSignal.removeAll();

        whenSignalIsDispatched();

        assertThat(calledListeners).isEqualTo(0);
    }

    private void whenSignalIsDispatched(int width, int height) {
        resizeSignal.dispatch(s -> s.onResize(width, height));
    }

    private void whenSignalIsDispatched() {
        whenSignalIsDispatched(0, 0);
    }
}