package pl.com.labaj.autorecord.test.arrays;

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.test.Counters;

@AutoRecord
@AutoRecord.Options(memoizedHashCode = true, memoizedToString = true)
public interface ArrayComponentMemoized {
    Counters one();

    String[] two();
}
