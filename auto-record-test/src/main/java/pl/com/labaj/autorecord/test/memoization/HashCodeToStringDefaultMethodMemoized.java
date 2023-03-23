package pl.com.labaj.autorecord.test.memoization;

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.Memoized;

@AutoRecord
@AutoRecord.Options(memoizedHashCode = true, memoizedToString = true)
public interface HashCodeToStringDefaultMethodMemoized {
    String property();

    @Memoized
    default String aMethod() {
        return property();
    }

}
