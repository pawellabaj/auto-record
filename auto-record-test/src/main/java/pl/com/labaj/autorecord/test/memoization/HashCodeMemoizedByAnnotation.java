package pl.com.labaj.autorecord.test.memoization;

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.Memoized;

@AutoRecord()
public interface HashCodeMemoizedByAnnotation {
    String property();

    @Memoized
    @Override
    int hashCode();
}
