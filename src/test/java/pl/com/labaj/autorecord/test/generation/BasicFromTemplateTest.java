package pl.com.labaj.autorecord.test.generation;

import org.junit.jupiter.api.Assertions;
import pl.com.labaj.autorecord.test.TestFor;

import java.lang.reflect.RecordComponent;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class BasicFromTemplateTest {
    @TestFor(BasicFromTemplate.class)
    @SuppressWarnings("rawtypes")
    void shouldGenerateRecord() {
        //given
        Class<?> recordClass = Assertions.assertDoesNotThrow(() -> Class.forName("pl.com.labaj.autorecord.test.generation.BasicFromTemplateRecord"));

        //when
        var recordComponents = recordClass.getRecordComponents();

        //then
        assertAll(
                () -> assertThat(recordClass.isRecord()).isTrue(),
                () -> assertThat(recordClass).isAssignableTo(BasicFromTemplate.class),
                () -> assertThat(recordComponents)
                        .extracting(RecordComponent::getName).containsExactly("text", "number", "genericCollection"),
                () -> assertThat(recordComponents)
                        .extracting(recordComponent -> (Class) recordComponent.getType()).containsExactly(String.class, Integer.TYPE, List.class)
        );
    }
}
