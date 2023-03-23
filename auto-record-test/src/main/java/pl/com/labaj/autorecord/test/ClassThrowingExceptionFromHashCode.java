package pl.com.labaj.autorecord.test;

public class ClassThrowingExceptionFromHashCode {
    @Override
    @SuppressWarnings("java:S112")
    public int hashCode() {
        throw new RuntimeException("This method should not be called");
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "ClassThrowingExceptionFromHashCode{}";
    }
}
