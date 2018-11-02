package org.jusecase.signals;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Scheduler {

    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

    public void add(Runnable task) {
        queue.add(task);
    }

    public int size() {
        return queue.size();
    }

    public void run() {
        Runnable runnable;
        while ((runnable = queue.poll()) != null) {
            runnable.run();
        }
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
