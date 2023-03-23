package pl.com.labaj.autorecord.test.memoization;

import pl.com.labaj.autorecord.AutoRecord;

@AutoRecord()
@AutoRecord.Options(memoizedToString = true)
public interface ToStringMemoized {
    String one();
    int two();
    Object three();
}
