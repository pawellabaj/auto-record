package pl.com.labaj.autorecord;

/*-
 * Copyright © 2023 Auto Record
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.soabase.recordbuilder.core.RecordBuilder;
import org.apiguardian.api.API;
import pl.com.labaj.autorecord.extension.AutoRecordExtension;
import pl.com.labaj.autorecord.extension.AutoRecordExtensions;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static java.lang.annotation.RetentionPolicy.SOURCE;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * AutoRecord is a code generator that helps you easily generate Java records.
 * It provides an easy way to avoid writing repetitive boilerplate code.
 *
 * <p>{@link AutoRecord} annotation is used to mark interface for annotation processing.
 * Processor generates a Java {@code record} that implements annotated interface.
 * The constructor parameters correspond, in order, to the interface methods.
 *
 * @see <a href="https://github.com/pawellabaj/auto-record/wiki">Wiki</a>
 */
@Retention(SOURCE)
@Target(TYPE)
@Inherited
@API(status = STABLE)
public @interface AutoRecord {

    /**
     * Specifies options for the {@link AutoRecord} annotation.
     * <p>
     * <b>Compatibility Note:</b> Methods may be added to this annotation in future releases of the library.
     *
     * @see <a href="https://github.com/pawellabaj/auto-record/wiki/Customization">Customization Wiki</a>
     */
    @Retention(SOURCE)
    @Target({ANNOTATION_TYPE, TYPE})
    @Inherited
    @interface Options {
        /**
         * If {@code true}, {@link RecordBuilder} annotation is added to generated record. This causes generating a builder.
         *
         * @return a flag indicating if builder should be generated
         * @see <a href="https://github.com/pawellabaj/auto-record/wiki/Record-Builder">Builder Wiki</a>
         */
        boolean withBuilder() default false;

        /**
         * If {@code true}, {@code hashCode()} method is memoized.
         *
         * @return a flag indicating if {@code hashCode()} method should be memoized.
         * @see <a href="https://github.com/pawellabaj/auto-record/wiki/Memoization#hashcode-memoization">Memoization Wiki</a>
         */
        boolean memoizedHashCode() default false;

        /**
         * If {@code true}, {@code toString()} method is memoized.
         *
         * @return a flag indicating if {@code toString()} method should be memoized.
         * @see <a href="https://github.com/pawellabaj/auto-record/wiki/Memoization#tostring-memoization">Memoization Wiki</a>
         */
        boolean memoizedToString() default false;
    }

    /**
     * Used to specify an extension for the {@link AutoRecord} Processor used during a record generation.
     * <p>
     * Note: Class specified in {@link #extensionClass()} must be already present on classpath during the annotation processing.
     *
     * @see AutoRecordExtension
     * @see Template#extensions()
     * @see <a href="https://github.com/pawellabaj/auto-record/wiki/Extensions">Extensions Wiki</a>
     * @since 3.0.0
     */
    @Retention(SOURCE)
    @Target({ANNOTATION_TYPE, TYPE})
    @Inherited
    @Repeatable(AutoRecordExtensions.class)
    @interface Extension {
        /**
         * The FQCN of the custom extension. The class needs to implement the {@link AutoRecordExtension} interface.
         *
         * @return the class name of the custom extension.
         */
        String extensionClass();

        /**
         * Additional parameters for the custom extension.
         *
         * @return an array of {@link String} objects representing the additional parameters for the custom extension.
         * @see AutoRecordExtension#init(javax.annotation.processing.ProcessingEnvironment, String[])
         */
        String[] parameters() default {};
    }

    /**
     * Provides a base to create custom annotation that can be used to mark interfaces for record generation.
     *
     * @see <a href="https://github.com/pawellabaj/auto-record/wiki/Customization#creating-a-custom-annotation">Custom Annotation Wiki</a>
     */
    @Retention(CLASS)
    @Target(ANNOTATION_TYPE)
    @Inherited
    @interface Template {

        /**
         * Specifies options for the {@link AutoRecord} annotation applied during record generation.
         *
         * @return options for record generation
         */
        AutoRecord.Options recordOptions() default @AutoRecord.Options();

        /**
         * Specifies options for the {@link RecordBuilder} used for builder generation if enabled by
         * {@link Options#withBuilder() withBuilder()}.
         *
         * @return options for builder generation
         * @see <a href="https://github.com/pawellabaj/auto-record/wiki/Record-Builder#customizing-builder-generation">Builder Customization Wiki</a>
         */
        RecordBuilder.Options builderOptions() default @RecordBuilder.Options();

        /**
         * Specifies the custom extensions to be used during record generation.
         *
         * @return an array of {@link AutoRecord.Extension} annotations representing the extensions to be used during record generation.
         * @see AutoRecord.Extension
         * @see AutoRecordExtension
         * @since 2.1.0
         */
        AutoRecord.Extension[] extensions() default {};
    }
}
