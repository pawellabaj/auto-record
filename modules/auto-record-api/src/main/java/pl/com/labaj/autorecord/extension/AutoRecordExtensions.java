package pl.com.labaj.autorecord.extension;

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

import org.apiguardian.api.API;
import pl.com.labaj.autorecord.AutoRecord;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * An annotation used to mark the {@link AutoRecord.Extension} annotation as repeatable.
 *
 * @see AutoRecord.Extension
 * @see Repeatable
 * @since 2.1.0
 */
@Retention(SOURCE)
@Target({ANNOTATION_TYPE, TYPE})
@Inherited
@API(status = STABLE)
public @interface AutoRecordExtensions {
    /**
     * An array of {@link AutoRecord.Extension} annotations representing the extensions to be used during record generation.
     *
     * @return an array of {@link AutoRecord.Extension} annotations.
     */
    AutoRecord.Extension[] value();
}
