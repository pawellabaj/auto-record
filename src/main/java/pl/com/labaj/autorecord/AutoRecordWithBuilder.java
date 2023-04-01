package pl.com.labaj.autorecord;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@Target(TYPE)
@AutoRecord.Template(
        recordOptions = @AutoRecord.Options(withBuilder = true),
        builderOptions = @RecordBuilder.Options(useImmutableCollections = true)
)
public @interface AutoRecordWithBuilder {
}
