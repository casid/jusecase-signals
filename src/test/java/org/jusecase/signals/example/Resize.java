package org.jusecase.signals.example;

import org.jusecase.signals.Signal;

public class Resize extends Signal<ResizeListener> {
    public void dispatch(int w, int h) {
        for (ResizeListener listener : this) {
            listener.onResize(w, h);
        }
    }
}
