package pl.com.labaj.autorecord.test.memoization;

import org.junit.jupiter.api.function.Executable;
import pl.com.labaj.autorecord.test.Counters;
import pl.com.labaj.autorecord.test.TestFor;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HashCodeMemoizedTest {

    @TestFor(HashCodeMemoized.class)
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void shouldMemoizeHashCode() {
        //given
        var one = new Counters();
        var record = new HashCodeMemoizedRecord(one);

        //then
        var assertions = IntStream.range(0, 5)
                .peek(i -> record.hashCode())
                .map(i -> one.hashCodeCount())
                .mapToObj(count -> (Executable) () -> assertThat(count).isEqualTo(1));

        //then
        assertAll(assertions);
    }
}
