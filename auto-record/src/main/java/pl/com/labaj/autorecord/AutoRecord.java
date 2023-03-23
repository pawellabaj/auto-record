package pl.com.labaj.autorecord;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@Target(TYPE)
@Inherited
public @interface AutoRecord {

    @Retention(SOURCE)
    @Target({ANNOTATION_TYPE, TYPE})
    @Inherited
    @interface Options {
        boolean withBuilder() default false;
        boolean memoizedHashCode() default false;
        boolean memoizedToString() default false;
    }

    @Retention(SOURCE)
    @Target(ANNOTATION_TYPE)
    @Inherited
    @interface Template {
        AutoRecord.Options recordOptions() default @AutoRecord.Options();
        RecordBuilder.Options builderOptions() default @RecordBuilder.Options();
    }
}
