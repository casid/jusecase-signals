package org.jusecase.signals;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SchedulerTest {
    Scheduler scheduler = new Scheduler();
    int changeMe = 0;

    @Test
    void empty() {
        assertThat(scheduler.size()).isEqualTo(0);
    }

    @Test
    void add() {
        scheduler.add(() -> changeMe = 5);
        assertThat(scheduler.size()).isEqualTo(1);
    }

    @Test
    void dispatch() {
        scheduler.add(() -> changeMe = 5);

        scheduler.run();

        assertThat(changeMe).isEqualTo(5);
        assertThat(scheduler.isEmpty()).isTrue();
    }

    @Test
    void dispatch_many() {
        scheduler.add(() -> changeMe = 5);
        scheduler.add(() -> changeMe = 7);

        scheduler.run();

        assertThat(changeMe).isEqualTo(7);
        assertThat(scheduler.isEmpty()).isTrue();
    }
}