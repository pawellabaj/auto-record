package pl.com.labaj.autorecord.test.memoization;

import org.junit.jupiter.api.function.Executable;
import pl.com.labaj.autorecord.test.Counters;
import pl.com.labaj.autorecord.test.TestFor;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ToStringMemoizedTest {

    @TestFor(ToStringMemoized.class)
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void shouldMemoizeToString() {
        //given
        var three = new Counters();
        var record = new ToStringMemoizedRecord("one", 2, three);

        //then
        var assertions = IntStream.range(0, 5)
                .peek(i -> record.toString())
                .map(i -> three.toStringCount())
                .mapToObj(count -> (Executable) () -> assertThat(count).isEqualTo(1));

        //then
        assertAll(assertions);
    }
}
