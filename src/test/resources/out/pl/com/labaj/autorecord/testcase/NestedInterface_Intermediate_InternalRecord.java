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

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.Memoized;
import pl.com.labaj.autorecord.memoizer.IntMemoizer;
import pl.com.labaj.autorecord.memoizer.Memoizer;

@Generated("pl.com.labaj.autorecord.AutoRecord")
@GeneratedWithAutoRecord
public record NestedInterface_Intermediate_InternalRecord(String name,
                                                          @Nullable IntMemoizer hashCodeMemoizer,
                                                          @Nullable Memoizer<String> alaMemoizer) implements NestedInterface.Intermediate.Internal {
    public NestedInterface_Intermediate_InternalRecord {
        requireNonNull(name, () -> "name must not be null");
        requireNonNull(hashCodeMemoizer, () -> "hashCodeMemoizer must not be null");
        requireNonNull(alaMemoizer, () -> "alaMemoizer must not be null");
    }

    public NestedInterface_Intermediate_InternalRecord(String name) {
        this(name, new IntMemoizer(), new Memoizer<>());
    }

    @Memoized
    @Override
    public int hashCode() {
        return hashCodeMemoizer.computeAsIntIfAbsent(() -> _hashCode());
    }

    @Memoized
    @Override
    public String ala() {
        return alaMemoizer.computeIfAbsent(() -> NestedInterface.Intermediate.Internal.super.ala());
    }

    private int _hashCode() {
        return hash(name);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof NestedInterface_Intermediate_InternalRecord)) {
            return false;
        }
        if (hashCode() != other.hashCode()) {
            return false;
        }

        var otherRecord = (NestedInterface_Intermediate_InternalRecord) other;
        return Objects.equals(name, otherRecord.name);
    }
}
