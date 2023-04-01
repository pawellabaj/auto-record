package pl.com.labaj.autorecord.test.ignored;

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.Ignored;
import pl.com.labaj.autorecord.test.Counters;

@AutoRecord
public interface IgnoredFields {
    String one();

    int two();

    @Ignored
    String three();

    @Ignored
    Counters four();
}
