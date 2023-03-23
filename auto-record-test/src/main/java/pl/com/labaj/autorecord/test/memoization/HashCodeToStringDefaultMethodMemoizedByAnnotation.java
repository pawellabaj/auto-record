package pl.com.labaj.autorecord.test.memoization;

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.Memoized;

@AutoRecord
public interface HashCodeToStringDefaultMethodMemoizedByAnnotation {
    String property();

    @Memoized
    default String aMethod() {
        return property();
    }

    @Override
    @Memoized
    int hashCode();

    @Override
    @Memoized
    String toString();
}
