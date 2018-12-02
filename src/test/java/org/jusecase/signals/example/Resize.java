package org.jusecase.signals.example;

import org.jusecase.signals.Signal;

public class Resize extends Signal<ResizeListener> {
    public Resize() {
        super(ResizeListener.class);
    }

    public void dispatch(int w, int h) {
        for (int i = 0; i < size; ++i) {
            listeners[i].onResize(w, h);
        }
    }
}
