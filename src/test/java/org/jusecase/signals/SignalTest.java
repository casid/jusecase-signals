package org.jusecase.signals;


import org.junit.Test;
import org.jusecase.signals.example.ResizeEvent;
import org.jusecase.signals.example.ResizeSignal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;


public class SignalTest {

    ResizeSignal resizeSignal = new ResizeSignal();

    List<ResizeEvent> caughtEvents = new ArrayList<>();

    @Test
    public void dispatch_noListeners() {
        resizeSignal.dispatch(0, 0);
        assertThat(caughtEvents).isEmpty();
    }

    @Test
    public void dispatch_listenerAddedAndRemoved() {
        Consumer<ResizeEvent> listener = e -> caughtEvents.add(e);
        resizeSignal.add(listener);
        resizeSignal.remove(listener);

        resizeSignal.dispatch(0, 0);

        assertThat(caughtEvents).isEmpty();
    }

    @Test
    public void dispatch_listenerRemovedDuringDispatchAtEnd() {
        Consumer<ResizeEvent> listener = e -> caughtEvents.add(e);
        resizeSignal.add(listener);
        resizeSignal.add(e -> resizeSignal.remove(listener));

        resizeSignal.dispatch(0, 0);

        assertThat(caughtEvents).hasSize(1);
    }

    @Test
    public void dispatch_listenerRemovedDuringDispatchAtBegin() {
        Consumer<ResizeEvent> listener = e -> caughtEvents.add(e);
        resizeSignal.add(e -> resizeSignal.remove(listener));
        resizeSignal.add(listener);

        resizeSignal.dispatch(0, 0);

        assertThat(caughtEvents).hasSize(1);
    }

    @Test
    public void dispatch_oneListener() {
        resizeSignal.add(e -> caughtEvents.add(e));

        resizeSignal.dispatch(800, 600);

        assertThat(caughtEvents).hasSize(1);
        assertThat(caughtEvents.get(0).width).isEqualTo(800);
        assertThat(caughtEvents.get(0).height).isEqualTo(600);
    }

    @Test
    public void dispatch_twoListeners() {
        resizeSignal.add(e -> caughtEvents.add(e));
        resizeSignal.add(e -> caughtEvents.add(e));

        resizeSignal.dispatch(0 , 0);

        assertThat(caughtEvents).hasSize(2);
    }

    @Test
    public void removeAll() {
        resizeSignal.add(e -> caughtEvents.add(e));
        resizeSignal.add(e -> caughtEvents.add(e));
        resizeSignal.add(e -> caughtEvents.add(e));
        resizeSignal.removeAll();

        resizeSignal.dispatch(0, 0);

        assertThat(caughtEvents).isEmpty();
    }
}