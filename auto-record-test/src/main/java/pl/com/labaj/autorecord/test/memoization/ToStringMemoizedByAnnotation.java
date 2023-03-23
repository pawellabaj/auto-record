package pl.com.labaj.autorecord.test.memoization;

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.Memoized;

@AutoRecord()
public interface ToStringMemoizedByAnnotation {
    String one();
    int two();
    Object three();

    @Memoized
    @Override
    String toString();
}
