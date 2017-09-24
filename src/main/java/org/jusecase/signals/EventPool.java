package org.jusecase.signals;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class EventPool {
    public static final EventPool DEFAULT = new EventPool();

    private final Map<Class<?>, Queue<Object>> unusedEvents = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <Event> Event getUnusedEvent(Class<Event> eventClass) {
        return (Event) getUnusedEvents(eventClass).poll();
    }

    public <Event> void addUnusedEvent(Class<Event> eventClass, Event event) {
        getUnusedEvents(eventClass).add(event);
    }

    private <Event> Queue<Object> getUnusedEvents(Class<Event> eventClass) {
        return unusedEvents.computeIfAbsent(eventClass, this::createQueue);
    }

    @SuppressWarnings("unused") // We want to have a method reference here
    private Queue<Object> createQueue(Class eventClass) {
        return new ArrayDeque<>();
    }
}
