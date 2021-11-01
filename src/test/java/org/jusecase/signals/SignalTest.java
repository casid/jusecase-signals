package org.jusecase.signals;


import org.junit.jupiter.api.Test;
import org.jusecase.signals.example.Resize;
import org.jusecase.signals.example.ResizeListener;

import static org.assertj.core.api.Assertions.assertThat;


class SignalTest {

    Resize signal = new Resize();

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

    @Test
    void removeMethodReference() {
        signal.add(this::onResizeMethodReference);
        signal.remove(this::onResizeMethodReference);
        assertThat(signal.size()).isEqualTo(1); // yes, method references are dangerous to use!
    }

    @Test
    void remove_addedTwice() {
        signal.add(listener);
        signal.add(listener);

        signal.remove(listener);

        assertThat(signal.size()).isEqualTo(0);
    }

    @Test
    void addFirst_two() {
        StringBuilder result = new StringBuilder();
        ResizeListener one = (width, height) -> result.append("one");
        ResizeListener two = (width, height) -> result.append("two");

        signal.add(one);
        signal.addFirst(two);

        whenSignalIsDispatched();

        assertThat(result.toString()).isEqualTo("twoone");
    }

    @Test
    void addFirst_three() {
        StringBuilder result = new StringBuilder();
        ResizeListener one = (width, height) -> result.append("one");
        ResizeListener two = (width, height) -> result.append("two");
        ResizeListener three = (width, height) -> result.append("three");

        signal.addFirst(one);
        signal.addFirst(two);
        signal.addFirst(three);

        whenSignalIsDispatched();

        assertThat(result.toString()).isEqualTo("threetwoone");
    }

    @Test
    void resizeWhileIterating() {
        class RemoveMyselfListener implements ResizeListener {

            @Override
            public void onResize(int width, int height) {
                signal.remove(this);
            }
        }

        signal.add(new RemoveMyselfListener());
        signal.add(new RemoveMyselfListener());
        signal.add(new RemoveMyselfListener());

        whenSignalIsDispatched();

        assertThat(signal.size()).isEqualTo(0);
    }

    @Test
    void nestedDispatch() {
        class DispatchAgainListener implements ResizeListener {

            @Override
            public void onResize(int width, int height) {
                SignalTest.this.width = width;
                if (width != -1) {
                    signal.dispatch(-1, height);
                }
            }
        }
        signal.add(new DispatchAgainListener());

        whenSignalIsDispatched();

        assertThat(width).isEqualTo(-1);
    }

    @Test
    void nestedDispatch_mulitpleDepths() {
        class DispatchAgainListener implements ResizeListener {

            @Override
            public void onResize(int width, int height) {
                SignalTest.this.width = width;
                if (width > -5) {
                    signal.dispatch(width - 1, height);
                }
            }
        }
        signal.add((w, h) -> {
        });
        signal.add(new DispatchAgainListener());
        signal.add((w, h) -> {
        });

        whenSignalIsDispatched();

        assertThat(width).isEqualTo(-5);
    }

    void whenSignalIsDispatched(int width, int height) {
        signal.dispatch(width, height);
    }

    void whenSignalIsDispatched() {
        whenSignalIsDispatched(0, 0);
    }

    @SuppressWarnings("unused")
    void onResizeMethodReference(int w, int h) {
    }
}