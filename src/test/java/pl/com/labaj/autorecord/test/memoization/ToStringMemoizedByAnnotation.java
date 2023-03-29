package pl.com.labaj.autorecord.test.memoization;

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.Memoized;
import pl.com.labaj.autorecord.test.Counters;

@AutoRecord
public interface ToStringMemoizedByAnnotation {
    String one();

    int two();

    Counters three();

    @Memoized
    @Override
    String toString();
}
