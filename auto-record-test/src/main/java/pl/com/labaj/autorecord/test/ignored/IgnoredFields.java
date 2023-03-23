package pl.com.labaj.autorecord.test.ignored;

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.Ignored;
import pl.com.labaj.autorecord.test.ClassThrowingExceptionFromEquals;
import pl.com.labaj.autorecord.test.ClassThrowingExceptionFromHashCode;

@AutoRecord
public interface IgnoredFields {
    String one();

    int two();

    @Ignored
    String three();

    @Ignored
    ClassThrowingExceptionFromHashCode four();

    @Ignored
    ClassThrowingExceptionFromEquals five();
}
