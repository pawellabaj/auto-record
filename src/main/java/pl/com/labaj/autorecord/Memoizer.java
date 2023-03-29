package pl.com.labaj.autorecord;

import java.util.function.Supplier;

public final class Memoizer<T> {
    private volatile boolean valueMemoized;
    @SuppressWarnings("java:S3077")
    private volatile T value;

    public T computeIfAbsent(Supplier<T> valueSupplier) {
        if (!valueMemoized) {
            synchronized (this) {
                if (!valueMemoized) {
                    value = valueSupplier.get();
                    valueMemoized = true;
                }
            }
        }
        return value;
    }
}