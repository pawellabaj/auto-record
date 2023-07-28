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

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.collections.ImmutableDeque;
import pl.com.labaj.autorecord.extension.arice.ARICE;
import pl.com.labaj.autorecord.extension.arice.ARICEUtilities;

@Generated(
        comments = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension",
        value = "pl.com.labaj.autorecord.AutoRecord"
)
@GeneratedWithAutoRecord
@ARICEUtilities(className = "pl.com.labaj.autorecord.extension.arice.ARICE")
record ItemWithQueuesRecord<E>(Queue<E> queue,
                               Deque<E> deque,
                               ArrayDeque<E> arrayDeque,
                               ImmutableDeque<E> immutableDeque,
                               @Nullable Queue<E> nullableQueue,
                               @Nullable Deque<E> nullableDeque,
                               @Nullable ArrayDeque<E> nullableArrayDeque,
                               @Nullable ImmutableDeque<E> nullableImmutableDeque) implements ItemWithQueues<E> {
    ItemWithQueuesRecord {
        // pl.com.labaj.autorecord.processor.AutoRecordProcessor
        requireNonNull(queue, "queue must not be null");
        requireNonNull(deque, "deque must not be null");
        requireNonNull(arrayDeque, "arrayDeque must not be null");
        requireNonNull(immutableDeque, "immutableDeque must not be null");

        // pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension
        queue = ARICE.immutable(queue);
        deque = ARICE.immutable(deque);
        nullableQueue = isNull(nullableQueue) ? null : ARICE.immutable(nullableQueue);
        nullableDeque = isNull(nullableDeque) ? null : ARICE.immutable(nullableDeque);
    }
}