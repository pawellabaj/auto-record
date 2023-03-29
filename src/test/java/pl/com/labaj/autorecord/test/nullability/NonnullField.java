package pl.com.labaj.autorecord.test.nullability;

import pl.com.labaj.autorecord.AutoRecord;

import javax.annotation.Nullable;

@AutoRecord
public interface NonnullField {
    String one();

    @Nullable
    String two();
}
