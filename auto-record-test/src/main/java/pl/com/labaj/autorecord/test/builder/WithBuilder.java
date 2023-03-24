package pl.com.labaj.autorecord.test.builder;

import pl.com.labaj.autorecord.AutoRecord;

@AutoRecord
@AutoRecord.Options(withBuilder = true)
public interface WithBuilder {
    String one();

    int two();
}

