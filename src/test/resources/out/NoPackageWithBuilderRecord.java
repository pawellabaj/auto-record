
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

import io.soabase.recordbuilder.core.RecordBuilder;
import java.lang.Integer;
import java.lang.String;
import java.util.List;
import java.util.Optional;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;

@Generated("pl.com.labaj.autorecord.AutoRecord")
@GeneratedWithAutoRecord
@RecordBuilder
@RecordBuilder.Options(
        addClassRetainedGenerated = true
)
public record NoPackageWithBuilderRecord(String text, int number, List<Optional<Integer>> genericCollection) implements NoPackageWithBuilder {
    public NoPackageWithBuilderRecord {
        requireNonNull(text, "text must not be null");
        requireNonNull(genericCollection, "genericCollection must not be null");
    }

    public static NoPackageWithBuilderRecordBuilder builder() {
        return NoPackageWithBuilderRecordBuilder.builder();
    }

    public NoPackageWithBuilderRecordBuilder toBuilder() {
        return NoPackageWithBuilderRecordBuilder.builder(this);
    }
}
