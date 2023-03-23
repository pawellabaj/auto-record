package pl.com.labaj.autorecord.test.memoization;

import pl.com.labaj.autorecord.AutoRecord;

@AutoRecord()
@AutoRecord.Options(memoizedHashCode = true)
public interface HashCodeMemoized {
    String property();
}
