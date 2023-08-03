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

import com.google.common.collect.ImmutableSet;
import java.lang.Enum;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.extension.arice.ARICE;
import pl.com.labaj.autorecord.extension.arice.ARICEUtilities;
import pl.com.labaj.autorecord.testcase.user.UserCollections;

@Generated(
        comments = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension",
        value = "pl.com.labaj.autorecord.AutoRecord"
)
@GeneratedWithAutoRecord
@ARICEUtilities(
        className = "pl.com.labaj.autorecord.extension.arice.ARICE"
)
record ItemWithSetsRecord<E, M extends Enum<M>>(Set<E> set,
                                                HashSet<E> hashSet,
                                                LinkedHashSet<E> linkedHashSet,
                                                SortedSet<E> sortedSet,
                                                UserCollections.SortedSetImpl<E> sortedSetImpl,
                                                NavigableSet<E> navigableSet,
                                                TreeSet<E> treeSet,
                                                UserCollections.UserSet<E> userSet,
                                                UserCollections.UserSetImpl<E> userSetImpl,
                                                UserCollections.UserSortedSet<E> userSortedSet,
                                                UserCollections.UserSortedSetImpl<E> userSortedSetImpl,
                                                UserCollections.UserNavigableSet<E> userNavigableSet,
                                                UserCollections.UserNavigableSetImpl<E> userNavigableSetImpl,
                                                ImmutableSet<E> immutableSet,
                                                @Nullable Set<E> nullableSet,
                                                @Nullable HashSet<E> nullableHashSet,
                                                @Nullable LinkedHashSet<E> nullableLinkedHashSet,
                                                @Nullable SortedSet<E> nullableSortedSet,
                                                @Nullable UserCollections.SortedSetImpl<E> nullableSortedSetImpl,
                                                @Nullable NavigableSet<E> nullableNavigableSet,
                                                @Nullable TreeSet<E> nullableTreeSet,
                                                @Nullable UserCollections.UserSet<E> nullableUserSet,
                                                @Nullable UserCollections.UserSetImpl<E> nullableUserSetImpl,
                                                @Nullable UserCollections.UserSortedSet<E> nullableUserSortedSet,
                                                @Nullable UserCollections.UserSortedSetImpl<E> nullableUserSortedSetImpl,
                                                @Nullable UserCollections.UserNavigableSet<E> nullableUserNavigableSet,
                                                @Nullable UserCollections.UserNavigableSetImpl<E> nullableUserNavigableSetImpl,
                                                @Nullable ImmutableSet<E> nullableImmutableSet) implements ItemWithSets<E, M> {
    ItemWithSetsRecord {
        // pl.com.labaj.autorecord.processor.AutoRecordProcessor
        requireNonNull(set, "set must not be null");
        requireNonNull(hashSet, "hashSet must not be null");
        requireNonNull(linkedHashSet, "linkedHashSet must not be null");
        requireNonNull(sortedSet, "sortedSet must not be null");
        requireNonNull(sortedSetImpl, "sortedSetImpl must not be null");
        requireNonNull(navigableSet, "navigableSet must not be null");
        requireNonNull(treeSet, "treeSet must not be null");
        requireNonNull(userSet, "userSet must not be null");
        requireNonNull(userSetImpl, "userSetImpl must not be null");
        requireNonNull(userSortedSet, "userSortedSet must not be null");
        requireNonNull(userSortedSetImpl, "userSortedSetImpl must not be null");
        requireNonNull(userNavigableSet, "userNavigableSet must not be null");
        requireNonNull(userNavigableSetImpl, "userNavigableSetImpl must not be null");
        requireNonNull(immutableSet, "immutableSet must not be null");

        // pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension
        set = ARICE.immutable(set);
        sortedSet = ARICE.immutable(sortedSet);
        navigableSet = ARICE.immutable(navigableSet);
        nullableSet = isNull(nullableSet) ? null : ARICE.immutable(nullableSet);
        nullableSortedSet = isNull(nullableSortedSet) ? null : ARICE.immutable(nullableSortedSet);
        nullableNavigableSet = isNull(nullableNavigableSet) ? null : ARICE.immutable(nullableNavigableSet);
    }
}