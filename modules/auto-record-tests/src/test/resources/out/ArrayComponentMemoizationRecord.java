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
import static java.util.Objects.requireNonNullElseGet;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.Memoized;
import pl.com.labaj.autorecord.memoizer.IntMemoizer;
import pl.com.labaj.autorecord.memoizer.Memoizer;
import pl.com.labaj.autorecord.test.Counters;

@Generated("pl.com.labaj.autorecord.AutoRecord")
@GeneratedWithAutoRecord
public record ArrayComponentMemoizationRecord(Counters counters,
                                              String[] anArray,
                                              @Nullable IntMemoizer hashCodeMemoizer,
                                              @Nullable Memoizer<String> toStringMemoizer) implements ArrayComponentMemoization {
    public ArrayComponentMemoizationRecord {
        requireNonNull(counters, "counters must not be null");
        requireNonNull(anArray, "anArray must not be null");

        hashCodeMemoizer = requireNonNullElseGet(hashCodeMemoizer, IntMemoizer::new);
        toStringMemoizer = requireNonNullElseGet(toStringMemoizer, Memoizer::new);
    }

    public ArrayComponentMemoizationRecord(Counters counters, String[] anArray) {
        this(counters, anArray, new IntMemoizer(), new Memoizer<>());
    }

    @Memoized
    @Override
    public int hashCode() {
        return hashCodeMemoizer.computeAsIntIfAbsent(this::_hashCode);
    }

    @Memoized
    @Override
    public String toString() {
        return toStringMemoizer.computeIfAbsent(this::_toString);
    }

    private int _hashCode() {
        return hash(counters, Arrays.hashCode(anArray));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof ArrayComponentMemoizationRecord)) {
            return false;
        }
        if (hashCode() != other.hashCode()) {
            return false;
        }

        var otherRecord = (ArrayComponentMemoizationRecord) other;
        return Objects.equals(counters, otherRecord.counters)
                && Arrays.equals(anArray, otherRecord.anArray);
    }

    private String _toString() {
        return "ArrayComponentMemoizationRecord[" +
                "counters = " + counters + ", " +
                "anArray = " + Arrays.toString(anArray) +
                "]";
    }
}
