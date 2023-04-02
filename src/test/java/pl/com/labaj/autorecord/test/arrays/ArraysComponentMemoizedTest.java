package pl.com.labaj.autorecord.test.arrays;

import org.junit.jupiter.api.function.Executable;
import pl.com.labaj.autorecord.test.Counters;
import pl.com.labaj.autorecord.test.TestFor;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ArraysComponentMemoizedTest {

    @TestFor(ArrayComponentMemoized.class)
    void shouldGenerateEquaslAndToString() {
        //given
        var one = new Counters();
        var record1 = new ArrayComponentMemoizedRecord(one, new String[] {"A", "B", "C"});
        var record2 = new ArrayComponentMemoizedRecord(new Counters(), new String[] {"A", "B", "C"});

        var assertions = new ArrayList<Executable>();
        IntStream.range(0, 5)
                .peek(i -> record1.toString())
                .map(i -> one.toStringCount())
                .forEach(count -> assertions.add(() -> assertThat(count).isEqualTo(1)));

        assertions.add(() -> {
            assertThat(record1).isEqualTo(record2);});
        assertions.add(() -> assertThat(record1.toString()).contains("one", "two", "A", "B", "C"));

        //then
        assertAll(assertions);
    }
}
