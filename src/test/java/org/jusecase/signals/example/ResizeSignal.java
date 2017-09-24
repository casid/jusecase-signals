package org.jusecase.signals.example;

import org.jusecase.signals.Signal;

public class ResizeSignal extends Signal<ResizeEvent> {

    public void dispatch(int width, int height) {
        dispatch(e -> {
            e.width = width;
            e.height = height;
        });
    }
}
