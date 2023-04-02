package pl.com.labaj.autorecord.test;

import java.util.concurrent.atomic.AtomicInteger;

public final class Counters {
    private final AtomicInteger hashCodeCounter = new AtomicInteger(0);
    private final AtomicInteger toStringCounter = new AtomicInteger(0);
    private final AtomicInteger equalsCounter = new AtomicInteger(0);

    public Counters() {}

    @Override
    public int hashCode() {
        hashCodeCounter.addAndGet(1);
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        equalsCounter.addAndGet(1);
        return true;
    }

    @Override
    public String toString() {
        toStringCounter.addAndGet(1);
        return "{}";
    }

    public int hashCodeCount() {
        return hashCodeCounter.get();
    }

    public int toStringCount() {
        return toStringCounter.get();
    }

    public int equalsCount() {
        return equalsCounter.get();
    }
}
