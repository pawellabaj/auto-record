package pl.com.labaj.autorecord.test;

public class ClassThrowingExceptionFromEquals {
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    @SuppressWarnings("java:S112")
    public boolean equals(Object obj) {
        throw new RuntimeException("This method should not be called");
    }

    @Override
    public String toString() {
        return "ClassThrowingExceptionFromEquals{}";
    }
}
