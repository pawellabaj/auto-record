package pl.com.labaj.autorecord.test.arrays;

import pl.com.labaj.autorecord.test.TestFor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ArraysComponentTest {


    @TestFor(ArrayComponent.class)
    void shouldGenerateEquaslAndToString() {
        //given
        var record1 = new ArrayComponentRecord("one", new String[]{"A","B","C"});
        var record2 = new ArrayComponentRecord("one", new String[]{"A","B","C"});

        //then
        assertAll(
                () -> assertThat(record1).isEqualTo(record2),
                () -> assertThat(record1.toString()).contains("one", "two", "A", "B", "C")
        );
    }
}
