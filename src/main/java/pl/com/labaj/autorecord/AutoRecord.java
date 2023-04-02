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
