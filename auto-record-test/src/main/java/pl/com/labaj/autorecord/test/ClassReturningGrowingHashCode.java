package pl.com.labaj.autorecord.test;

import java.util.concurrent.atomic.AtomicInteger;

public class ClassReturningGrowingHashCode {
    private final AtomicInteger hash = new AtomicInteger(0);

    @Override
    public int hashCode() {
        return hash.getAndIncrement();
    }

    @Override
    public String toString() {
        return "ClassReturningGrowingHashCode{hash=" + hash + '}';
    }
}
