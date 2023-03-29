package pl.com.labaj.autorecord.test.memoization;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import pl.com.labaj.autorecord.test.Counters;
import pl.com.labaj.autorecord.test.TestFor;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;

class DefaultMethodMemoizedTest {

    @TestFor(DefaultMethodMemoized.class)
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void shouldMemoizeToString() {
        //given
        var one = new Counters();
        var record = new DefaultMethodMemoizedRecord(one);

        //then
        var assertions = IntStream.range(0, 5)
                .peek(i -> record.aMethod())
                .map(i -> one.toStringCount())
                .mapToObj(count -> (Executable) () -> Assertions.assertThat(count).isEqualTo(1));

        //then
        assertAll(assertions);
    }
}
