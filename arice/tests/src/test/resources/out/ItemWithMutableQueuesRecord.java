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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.collections.ImmutableDeque;
import pl.com.labaj.autorecord.extension.arice.ARICE;
import pl.com.labaj.autorecord.extension.arice.ARICEUtilities;
import pl.com.labaj.autorecord.extension.arice.Mutable;

@Generated(
        comments = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension",
        value = "pl.com.labaj.autorecord.AutoRecord"
)
@GeneratedWithAutoRecord
@ARICEUtilities(className = "pl.com.labaj.autorecord.extension.arice.ARICE")
record ItemWithMutableQueuesRecord<E>(Queue<E> queue,
                                      Deque<E> deque,
                                      ArrayDeque<E> arrayDeque,
                                      ImmutableDeque<E> immutableDeque,
                                      @Mutable Queue<E> mutableQueue,
                                      @Mutable Deque<E> mutableDeque,
                                      @Mutable ArrayDeque<E> mutableArrayDeque,
                                      @Mutable ImmutableDeque<E> mutableImmutableDeque) implements ItemWithMutableQueues<E> {
    ItemWithMutableQueuesRecord {
        // pl.com.labaj.autorecord.processor.AutoRecordProcessor
        requireNonNull(queue, "queue must not be null");
        requireNonNull(deque, "deque must not be null");
        requireNonNull(arrayDeque, "arrayDeque must not be null");
        requireNonNull(immutableDeque, "immutableDeque must not be null");
        requireNonNull(mutableQueue, "mutableQueue must not be null");
        requireNonNull(mutableDeque, "mutableDeque must not be null");
        requireNonNull(mutableArrayDeque, "mutableArrayDeque must not be null");
        requireNonNull(mutableImmutableDeque, "mutableImmutableDeque must not be null");

        // pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension
        queue = ARICE.immutable(queue);
        deque = ARICE.immutable(deque);
    }
}