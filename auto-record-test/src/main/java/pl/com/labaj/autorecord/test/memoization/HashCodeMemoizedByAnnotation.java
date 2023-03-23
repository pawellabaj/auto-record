package pl.com.labaj.autorecord.test.memoization;

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.Memoized;
import pl.com.labaj.autorecord.test.Counters;

@AutoRecord
public interface HashCodeMemoizedByAnnotation {
    Counters one();

    @Memoized
    @Override
    int hashCode();
}
