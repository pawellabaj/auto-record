package pl.com.labaj.autorecord.test.builder;

import io.soabase.recordbuilder.core.RecordBuilder;
import pl.com.labaj.autorecord.AutoRecord;

import static javax.lang.model.element.Modifier.FINAL;

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
