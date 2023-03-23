package pl.com.labaj.autorecord.test.ignored;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.com.labaj.autorecord.test.ClassThrowingExceptionFromEquals;
import pl.com.labaj.autorecord.test.ClassThrowingExceptionFromHashCode;
import pl.com.labaj.autorecord.test.ParameterizedTestFor;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class IgnoredFieldsTest {
    /*
    @AutoRecord
    public interface IgnoredFields {
        String one();
        int two();
        @Ignored String three();
        @Ignored ClassThrowingExceptionFromHashCode four();
        @Ignored ClassThrowingExceptionFromEquals five();
    }
     */

    public static Stream<Arguments> testData() {
        var hashCodeException = new ClassThrowingExceptionFromHashCode();
        var equalsException = new ClassThrowingExceptionFromEquals();

        var aRecord = new IgnoredFieldsRecord("one", 2, "A", hashCodeException, equalsException);
        var bRecord = new IgnoredFieldsRecord("one", 2, "B", hashCodeException, equalsException);
        var cRecord = new IgnoredFieldsRecord("one", 3, "C", hashCodeException, equalsException);

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
    void shouldIgnoreProperty(IgnoredFieldsRecord first, Object second, boolean expectedResult) {
        //when
        var result = assertDoesNotThrow(() -> first.equals(second));

        //then
        assertAll(
                () -> assertDoesNotThrow(first::hashCode),
                () -> assertThat(result).isEqualTo(expectedResult)
        );
    }
}
