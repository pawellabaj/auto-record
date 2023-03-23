package pl.com.labaj.autorecord.test.ignored;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.com.labaj.autorecord.test.Counters;
import pl.com.labaj.autorecord.test.ParameterizedTestFor;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class IgnoredFieldsTest {

    public static Stream<Arguments> testData() {
        var aRecord = new IgnoredFieldsRecord("one", 2, "A", new Counters());
        var bRecord = new IgnoredFieldsRecord("one", 2, "B", new Counters());
        var cRecord = new IgnoredFieldsRecord("one", 3, "C", new Counters());

        return Stream.of(
                Arguments.of(aRecord, aRecord, true),
                Arguments.of(aRecord, aRecord, true),
                Arguments.of(aRecord, "Not a record", false),
                Arguments.of(aRecord, bRecord, true),
                Arguments.of(aRecord, cRecord, false)
        );
    }

    @ParameterizedTestFor(IgnoredFields.class)
    @MethodSource("testData")
    void shouldIgnoreProperty(IgnoredFieldsRecord firstRecord, Object secondObject, boolean expectedResult) {
        //when
        var result = assertDoesNotThrow(() -> firstRecord.equals(secondObject));

        //then
        assertAll(
                () -> assertDoesNotThrow(firstRecord::hashCode),
                () -> assertThat(result).isEqualTo(expectedResult),
                () -> assertThat(firstRecord.four().hashCodeCount()).isEqualTo(0),
                () -> assertThat(firstRecord.four().equalsCount()).isEqualTo(0)
        );
    }
}
