package pl.com.labaj.autorecord.test.memoization;

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.Ignored;
import pl.com.labaj.autorecord.test.ClassThrowingExceptionFromHashCode;

@AutoRecord.Options(memoizedHashCode = true)
@AutoRecord()
public interface HashCodeIgnoredFieldMemoized {
    String used();

    int anotherUsed();

    @Ignored
    String ignored();

    @Ignored
    ClassThrowingExceptionFromHashCode anotherIgnored();
}
