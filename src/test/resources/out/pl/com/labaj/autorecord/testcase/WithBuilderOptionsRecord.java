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
import static javax.lang.model.element.Modifier.FINAL;

import io.soabase.recordbuilder.core.RecordBuilder;
import java.lang.String;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;

@Generated("pl.com.labaj.autorecord.AutoRecord")
@GeneratedWithAutoRecord
@RecordBuilder
@RecordBuilder.Options(
        addClassRetainedGenerated = true,
        buildMethodName = "buildRecord",
        builderClassModifiers = {FINAL},
        builderMethodName = "create",
        copyMethodName = "copyOf",
        enableWither = false
)
record WithBuilderOptionsRecord(String one, int two) implements WithBuilderOptions {
    WithBuilderOptionsRecord {
        requireNonNull(one, () -> "one must not be null");
    }

    static WithBuilderOptionsRecordBuilder builder() {
        return WithBuilderOptionsRecordBuilder.create();
    }

    WithBuilderOptionsRecordBuilder toBuilder() {
        return WithBuilderOptionsRecordBuilder.copyOf(this);
    }
}
