package pl.com.labaj.autorecord.test.memoization;

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.test.Counters;

@AutoRecord
@AutoRecord.Options(memoizedToString = true)
public interface ToStringMemoized {
    String one();

    int two();

    Counters three();
}
