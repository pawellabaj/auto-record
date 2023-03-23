package pl.com.labaj.autorecord.test.memoization;

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.test.Counters;

@AutoRecord
@AutoRecord.Options(memoizedHashCode = true)
public interface HashCodeMemoized {
    Counters one();
}
