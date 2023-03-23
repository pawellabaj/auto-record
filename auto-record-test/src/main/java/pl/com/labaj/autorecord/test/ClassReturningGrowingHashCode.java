package pl.com.labaj.autorecord.test;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ClassReturningGrowingHashCode {
    private final AtomicInteger hash = new AtomicInteger(0);

    @Override
    public int hashCode() {
        return hash.getAndIncrement();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassReturningGrowingHashCode that = (ClassReturningGrowingHashCode) o;
        return Objects.equals(hash, that.hash);
    }

    @Override
    public String toString() {
        return "ClassReturningGrowingHashCode{hash=" + hash + '}';
    }
}
