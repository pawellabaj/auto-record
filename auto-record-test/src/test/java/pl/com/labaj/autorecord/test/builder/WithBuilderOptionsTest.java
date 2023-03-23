package pl.com.labaj.autorecord.test.builder;

import pl.com.labaj.autorecord.test.TestFor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class WithBuilderOptionsTest {
    /*
    @AutoRecord
    @AutoRecord.Options(withBuilder = true)
    @RecordBuilder.Options(suffix = "_Builder",
            builderMethodName = "create",
            buildMethodName = "buildRecord",
            enableWither = false,
            builderClassModifiers = {FINAL})
    public interface WithBuilderOptions {
        String one();
        int two();
    }
     */

    @TestFor(WithBuilderOptions.class)
    void shouldGenerateBuilder() {
        //given
        var recordFromBuilder1 = WithBuilderOptionsRecord_Builder.create()
                .one("one")
                .two(2)
                .buildRecord();
        var recordFromBuilder2 = WithBuilderOptionsRecord.builder()
                .one("one")
                .two(2)
                .buildRecord();
        var recordFromConstructor = new WithBuilderOptionsRecord("one", 2);

        //then
        assertAll(
                () -> assertThat(recordFromBuilder1).isEqualTo(recordFromConstructor),
                () -> assertThat(recordFromBuilder2).isEqualTo(recordFromConstructor)
        );
    }
}
