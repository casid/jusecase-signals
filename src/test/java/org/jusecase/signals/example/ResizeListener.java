package org.jusecase.signals.example;

@FunctionalInterface
public interface ResizeListener {
    void onResize(int width, int height);
}
