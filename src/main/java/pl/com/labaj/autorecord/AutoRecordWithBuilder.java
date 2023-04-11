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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Custom annotation used to mark interface for annotation processing.
 *
 * <p>Record generated from interface with this annotation has builder generated. In addition, builder will handle collection to use immutable versions of them.
 *
 * @see <a href="https://github.com/pawellabaj/auto-record/wiki/Customization#creating-a-custom-annotation">Custom Annotation Wiki</a>
 * @see io.soabase.recordbuilder.core.RecordBuilder.Options#useImmutableCollections()
 */
@Retention(SOURCE)
@Target(TYPE)
@AutoRecord.Template(
        recordOptions = @AutoRecord.Options(withBuilder = true),
        builderOptions = @RecordBuilder.Options(useImmutableCollections = true)
)
public @interface AutoRecordWithBuilder {
}
