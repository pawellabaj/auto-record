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

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.Memoized;
import pl.com.labaj.autorecord.annotations.ClassProperty;
import pl.com.labaj.autorecord.annotations.SourceProperty;

import javax.annotation.Nullable;

import static java.util.Objects.isNull;

@AutoRecord
interface AnnotationWithValue {
    @Nullable
    Integer id();

    @SourceProperty
    @ClassProperty(priority = 9)
    @SuppressWarnings("DataFlowIssue")
    @Memoized
    default String idToString() {
        if (isNull(id())) {
            return "absent";
        }

        return Integer.toString(id());
    }
}
