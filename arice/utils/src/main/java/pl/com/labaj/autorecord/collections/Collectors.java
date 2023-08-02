package pl.com.labaj.autorecord.collections;

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
import org.apiguardian.api.API;

import java.util.ArrayDeque;
import java.util.stream.Collector;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * Utility class that provides custom {@linkplain Collector}s for creating immutable collections.
 * <p>
 * The collectors provided by this class allow you to create both {@link ImmutableCollection}
 * and {@link ImmutableDeque} instances.
 */

@API(status = STABLE)
public final class Collectors {
    static final Collector<Object, ?, ImmutableCollection<Object>> TO_IMMUTABLE_COLLECTION = Collector.of(
            ImmutableList::builder,
            ImmutableList.Builder::add,
            (builder1, builder2) -> builder1.addAll(builder2.build()),
            builder -> new ImmutableCollection<>(builder.build())
    );
    private static final Collector<Object, ?, ImmutableDeque<Object>> TO_IMMUTABLE_DEQUE = Collector.of(
            ArrayDeque::new,
            ArrayDeque::add,
            (deque1, deque2) -> {
                deque1.addAll(deque2);
                return deque1;
            },
            ImmutableDeque::new
    );

    private Collectors() {}

    /**
     * A collector that accumulates elements into an {@link ImmutableCollection}.
     * The resulting collection can either allow duplicates or not, depending on the provided flag.
     *
     * @param <E> the type of elements to be collected
     * @return a collector that accumulates elements into an immutable collection
     * @see ImmutableCollection#toImmutableCollection()
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E> Collector<E, ?, ImmutableCollection<E>> toImmutableCollection() {
        return (Collector) TO_IMMUTABLE_COLLECTION;
    }

    /**
     * A collector that accumulates elements into an {@link ImmutableDeque}.
     * The resulting deque will be immutable.
     *
     * @param <E> the type of elements to be collected
     * @return a collector that accumulates elements into an immutable deque
     * @see ImmutableDeque#toImmutableDeque()
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E> Collector<E, ?, ImmutableDeque<E>> toImmutableDeque() {
        return (Collector) TO_IMMUTABLE_DEQUE;
    }
}
