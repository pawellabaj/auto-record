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

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.testcase.user.UserCollections;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

@AutoRecord
@AutoRecord.Extension(
        extensionClass = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension",
        parameters = {
                "pl.com.labaj.autorecord.testcase.user.UserCollections.UserQueue",
                "pl.com.labaj.autorecord.testcase.user.UserCollections.UserDeque"
        }
)
interface ItemWithQueues<E> {
    Queue<E> queue();
    Deque<E> deque();
    ArrayDeque<E> arrayDeque();
    UserCollections.UserQueue<E> userQueue();
    UserCollections.UserDeque<E> userDeque();
    UserCollections.UserQueueImpl<E> userQueueImpl();
    UserCollections.UserDequeImpl<E> userDequeImpl();
    @Nullable Queue<E>nullableQueue();
    @Nullable Deque<E> nullableDeque();
    @Nullable ArrayDeque<E> nullableArrayDeque();
    @Nullable UserCollections.UserQueue<E> nullableUserQueue();
    @Nullable UserCollections.UserDeque<E> nullableUserDeque();
    @Nullable UserCollections.UserQueueImpl<E> nullableUserQueueImpl();
    @Nullable UserCollections.UserDequeImpl<E> nullableUserDequeImpl();
}
