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

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;

@Generated("pl.com.labaj.autorecord.AutoRecord")
@GeneratedWithAutoRecord
@AutoRecordImmutableCollectionsUtilities(
    className = "pl.com.labaj.autorecord.extension.arice.Methods_GPLQYPW",
    immutableTypes = {
        "pl.com.labaj.autorecord.extension.arice.UserCollections.UserQueue",
        "pl.com.labaj.autorecord.extension.arice.UserCollections.UserDeque"
    }
)
record ItemWithQueuesRecord<E>(Queue<E> queue,
                               Deque<E> deque,
                               ArrayDeque<E> arrayDeque,
                               UserCollections.UserQueue<E> userQueue,
                               UserCollections.UserDeque<E> userDeque,
                               UserCollections.UserQueueImpl<E> userQueueImpl,
                               UserCollections.UserDequeImpl<E> userDequeImpl,
                               @Nullable Queue<E> nullableQueue,
                               @Nullable Deque<E> nullableDeque,
                               @Nullable ArrayDeque<E> nullableArrayDeque,
                               @Nullable UserCollections.UserQueue<E> nullableUserQueue,
                               @Nullable UserCollections.UserDeque<E> nullableUserDeque,
                               @Nullable UserCollections.UserQueueImpl<E> nullableUserQueueImpl,
                               @Nullable UserCollections.UserDequeImpl<E> nullableUserDequeImpl) implements ItemWithQueues<E> {
    ItemWithQueuesRecord {
        // pl.com.labaj.autorecord.processor.AutoRecordProcessor
        requireNonNull(queue, "queue must not be null");
        requireNonNull(deque, "deque must not be null");
        requireNonNull(arrayDeque, "arrayDeque must not be null");
        requireNonNull(userQueue, "userQueue must not be null");
        requireNonNull(userDeque, "userDeque must not be null");
        requireNonNull(userQueueImpl, "userQueueImpl must not be null");
        requireNonNull(userDequeImpl, "userDequeImpl must not be null");

        // pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension
        queue = Methods_GPLQYPW.copyOfQueue(queue);
        deque = Methods_GPLQYPW.copyOfDeque(deque);
        nullableQueue = isNull(nullableQueue) ? null : Methods_GPLQYPW.copyOfQueue(nullableQueue);
        nullableDeque = isNull(nullableDeque) ? null : Methods_GPLQYPW.copyOfDeque(nullableDeque);
    }
}