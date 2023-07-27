package pl.com.labaj.autorecord.extension.arice;

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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayDeque;
import java.util.stream.Collector;

public final class Collectors {
    static final Collector<Object, ?, ImmutableCollection<Object>> TO_IMMUTABLE_COLLECTION_WITH_DUPLICATES = Collector.of(
            ImmutableList::builder,
            ImmutableList.Builder::add,
            (builder1, builder2) -> builder1.addAll(builder2.build()),
            builder -> new ImmutableCollection<>(builder.build(), true)
    );
    private static final Collector<Object, ?, ImmutableCollection<Object>> TO_IMMUTABLE_COLLECTION_NO_DUPLICATES = Collector.of(
            ImmutableSet::builder,
            ImmutableSet.Builder::add,
            (builder1, builder2) -> builder1.addAll(builder2.build()),
            builder -> new ImmutableCollection<>(builder.build(), false)
    );
    private static final Collector<Object, ?, ImmutableDeque<Object>> TO_IMMUTABLE_DEQUE = Collector.of(
            ArrayDeque::new,
            ArrayDeque::add,
            (deque1, deque2) -> {deque1.addAll(deque2); return deque1;},
            ImmutableDeque::new
    );

    private Collectors() {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E> Collector<E, ?, ImmutableCollection<E>> toImmutableCollection(boolean allowForDuplicates) {
        return (Collector) (allowForDuplicates ? TO_IMMUTABLE_COLLECTION_WITH_DUPLICATES : TO_IMMUTABLE_COLLECTION_NO_DUPLICATES);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E> Collector<E, ?, ImmutableDeque<E>> toImmutableDeque() {
        return (Collector) TO_IMMUTABLE_DEQUE;
    }
}
