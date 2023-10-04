package pl.com.labaj.autorecord.testcase;

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

import static java.util.Objects.requireNonNull;

import java.lang.String;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.annotations.ClassProperty;
import pl.com.labaj.autorecord.annotations.RuntimeProperty;
import pl.com.labaj.autorecord.annotations.SourceProperty;

@Generated("pl.com.labaj.autorecord.AutoRecord")
@GeneratedWithAutoRecord
record AnnotatedPropertyRecord(@RuntimeProperty String runtimeName,
                               @ClassProperty String compileName,
                               @SourceProperty String sourceName) implements AnnotatedProperty {
    AnnotatedPropertyRecord {
        requireNonNull(runtimeName, "runtimeName must not be null");
        requireNonNull(compileName, "compileName must not be null");
        requireNonNull(sourceName, "sourceName must not be null");
    }
}