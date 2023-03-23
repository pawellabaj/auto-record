package pl.com.labaj.autorecord.test;

public class ClassThrowingExceptionFromHashCode {
    @Override
    public int hashCode() {
        throw new RuntimeException("This method should not be called");
    }

    @Override
    public String toString() {
        return "ClassThrowingExceptionFromHashCode{}";
    }
}
