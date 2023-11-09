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
import static java.util.Objects.requireNonNullElseGet;

import java.lang.Override;
import java.lang.String;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.Memoized;
import pl.com.labaj.autorecord.memoizer.Memoizer;

@Generated("pl.com.labaj.autorecord.AutoRecord")
@GeneratedWithAutoRecord
record WithSubGenericRecord<X, Y extends Collection<X>>(ConcreteType a,
                                                        List<ConcreteType> listOfA,
                                                        X b,
                                                        Y y,
                                                        @Nullable Memoizer<ConcreteType> memAMemoizer,
                                                        @Nullable Memoizer<String> fromABMemoizer,
                                                        @Nullable Memoizer<Y> memYMemoizer,
                                                        @Nullable Memoizer<String> toStringMemoizer) implements WithSubGeneric<X, Y> {
    WithSubGenericRecord {
        requireNonNull(a, "a must not be null");
        requireNonNull(listOfA, "listOfA must not be null");
        requireNonNull(b, "b must not be null");
        requireNonNull(y, "y must not be null");

        memAMemoizer = requireNonNullElseGet(memAMemoizer, Memoizer::new);
        fromABMemoizer = requireNonNullElseGet(fromABMemoizer, Memoizer::new);
        memYMemoizer = requireNonNullElseGet(memYMemoizer, Memoizer::new);
        toStringMemoizer = requireNonNullElseGet(toStringMemoizer, Memoizer::new);
    }

    WithSubGenericRecord(ConcreteType a, List<ConcreteType> listOfA, X b, Y y) {
        this(a, listOfA, b, y, new Memoizer<>(), new Memoizer<>(), new Memoizer<>(), new Memoizer<>());
    }

    @Memoized
    @Override
    public ConcreteType memA() {
        return memAMemoizer.computeIfAbsent(WithSubGeneric.super::memA);
    }

    @Memoized
    @Override
    public String fromAB(ConcreteType anA, X anB) {
        return fromABMemoizer.computeIfAbsent(() -> WithSubGeneric.super.fromAB(anA, anB));
    }

    @Memoized
    @Override
    public Y memY() {
        return memYMemoizer.computeIfAbsent(WithSubGeneric.super::memY);
    }

    @Memoized
    @Override
    public String toString() {
        return toStringMemoizer.computeIfAbsent(this::_toString);
    }

    private String _toString() {
        return "WithSubGenericRecord[" +
                "a = " + a + ", " +
                "listOfA = " + listOfA + ", " +
                "b = " + b + ", " +
                "y = " + y +
                "]";
    }
}
