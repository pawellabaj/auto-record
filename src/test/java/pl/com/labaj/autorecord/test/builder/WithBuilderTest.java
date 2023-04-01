package pl.com.labaj.autorecord.test.builder;

import pl.com.labaj.autorecord.test.TestFor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class WithBuilderTest {

    @TestFor(WithBuilder.class)
    void shouldGenerateBuilder() {
        //given
        var recordFromBuilder1 = WithBuilderRecordBuilder.builder()
                .one("one")
                .two(2)
                .build();
        var recordFromBuilder2 = WithBuilderRecord.builder()
                .one("one")
                .two(2)
                .build();
        var recordFromConstructor = new WithBuilderRecord("one", 2);

        //then
        assertAll(
                () -> assertThat(recordFromBuilder1).isEqualTo(recordFromConstructor),
                () -> assertThat(recordFromBuilder2).isEqualTo(recordFromConstructor)
        );
    }
}
