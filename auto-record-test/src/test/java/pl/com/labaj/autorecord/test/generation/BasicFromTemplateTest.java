package pl.com.labaj.autorecord.test.generation;

import pl.com.labaj.autorecord.test.TestFor;

import java.lang.reflect.RecordComponent;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BasicFromTemplateTest {
    /*
    @WithTemplate
    public interface BasicFromTemplate {
        String text();

        int number();

        List<Optional<Integer>> genericCollection();
    }
     */

    @TestFor(BasicFromTemplate.class)
    @SuppressWarnings("rawtypes")
    void shouldGenerateRecord() {
        //given
        Class<?> recordClass = assertDoesNotThrow(() -> Class.forName("pl.com.labaj.autorecord.test.generation.BasicFromTemplateRecord"));

        //then
        var recordComponents = recordClass.getRecordComponents();

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
