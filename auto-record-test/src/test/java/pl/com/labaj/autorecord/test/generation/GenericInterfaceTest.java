package pl.com.labaj.autorecord.test.generation;

import pl.com.labaj.autorecord.test.TestFor;

import java.lang.reflect.RecordComponent;
import java.util.HashSet;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GenericInterfaceTest {
    /*
    @AutoRecord
    public interface GenericInterface<A, B extends Function<Integer, B> & Comparable<B>, C extends HashSet<A>> {
        A one();
        B two();
        C three();
    }
     */

    @TestFor(GenericInterface.class)
    @SuppressWarnings("rawtypes")
    void shouldGenerateRecord() {
        //given
        Class<?> recordClass = assertDoesNotThrow(() -> Class.forName("pl.com.labaj.autorecord.test.generation.GenericInterfaceRecord"));

        //then
        var recordComponents = recordClass.getRecordComponents();

        assertAll(
                () -> assertThat(recordClass.isRecord()).isTrue(),
                () -> assertThat(recordClass).isAssignableTo(GenericInterface.class),
                () -> assertThat(recordComponents)
                        .extracting(RecordComponent::getName).containsExactly("one", "two", "three"),
                () -> assertThat(recordComponents)
                        .extracting(recordComponent -> (Class) recordComponent.getType()).containsExactly(Object.class, Function.class, HashSet.class),
                () -> assertThat(recordComponents)
                        .extracting(this::typeParametersLength).containsExactly(0, 2, 1)
        );
    }

    private int typeParametersLength(RecordComponent recordComponent1) {
        return recordComponent1.getType().getTypeParameters().length;
    }
}
