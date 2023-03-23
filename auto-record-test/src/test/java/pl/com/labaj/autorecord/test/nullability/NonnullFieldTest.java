package pl.com.labaj.autorecord.test.nullability;

import pl.com.labaj.autorecord.test.TestFor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NonnullFieldTest {
    /*
    @AutoRecord
    public interface NonnullField {
        String one();
        @Nullable String two();
    }
     */

    @TestFor(NonnullField.class)
    void shouldThrowExcpetionWhenNonullComponentIsMissing() {
        var nullPointerException = assertThrows(NullPointerException.class, () -> new NonnullFieldRecord(null, null));
        assertThat(nullPointerException).hasMessageStartingWith("one");
    }

    @TestFor(NonnullField.class)
    void shouldConstructRecordWhenNullableComponentIsMissing() {
        assertDoesNotThrow(() -> new NonnullFieldRecord("one", null));
    }
}
