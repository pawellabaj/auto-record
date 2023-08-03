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

import com.google.common.collect.ImmutableSet;
import java.lang.Enum;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.extension.arice.ARICE;
import pl.com.labaj.autorecord.extension.arice.ARICEUtilities;
import pl.com.labaj.autorecord.extension.arice.Mutable;
import pl.com.labaj.autorecord.testcase.user.UserCollections;

@Generated(
        comments = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension",
        value = "pl.com.labaj.autorecord.AutoRecord"
)
@GeneratedWithAutoRecord
@ARICEUtilities(className = "pl.com.labaj.autorecord.extension.arice.ARICE")
record ItemWithMutableSetsRecord<E, M extends Enum<M>>(Set<E> set, HashSet<E> hashSet,
                                                       LinkedHashSet<E> linkedHashSet, SortedSet<E> sortedSet,
                                                       UserCollections.SortedSetImpl<E> sortedSetImpl, NavigableSet<E> navigableSet,
                                                       TreeSet<E> treeSet, UserCollections.UserSet<E> userSet,
                                                       UserCollections.UserSetImpl<E> userSetImpl, UserCollections.UserSortedSet<E> userSortedSet,
                                                       UserCollections.UserSortedSetImpl<E> userSortedSetImpl,
                                                       UserCollections.UserNavigableSet<E> userNavigableSet,
                                                       UserCollections.UserNavigableSetImpl<E> userNavigableSetImpl, ImmutableSet<E> immutableSet,
                                                       @Mutable Set<E> mutableSet, @Mutable HashSet<E> mutableHashSet,
                                                       @Mutable LinkedHashSet<E> mutableLinkedHashSet, @Mutable SortedSet<E> mutableSortedSet,
                                                       @Mutable UserCollections.SortedSetImpl<E> mutableSortedSetImpl,
                                                       @Mutable NavigableSet<E> mutableNavigableSet, @Mutable TreeSet<E> mutableTreeSet,
                                                       @Mutable UserCollections.UserSet<E> mutableUserSet,
                                                       @Mutable UserCollections.UserSetImpl<E> mutableUserSetImpl,
                                                       @Mutable UserCollections.UserSortedSet<E> mutableUserSortedSet,
                                                       @Mutable UserCollections.UserSortedSetImpl<E> mutableUserSortedSetImpl,
                                                       @Mutable UserCollections.UserNavigableSet<E> mutableUserNavigableSet,
                                                       @Mutable UserCollections.UserNavigableSetImpl<E> mutableUserNavigableSetImpl,
                                                       @Mutable ImmutableSet<E> mutableImmutableSet) implements ItemWithMutableSets<E, M> {
  ItemWithMutableSetsRecord {
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
    requireNonNull(mutableSet, "mutableSet must not be null");
    requireNonNull(mutableHashSet, "mutableHashSet must not be null");
    requireNonNull(mutableLinkedHashSet, "mutableLinkedHashSet must not be null");
    requireNonNull(mutableSortedSet, "mutableSortedSet must not be null");
    requireNonNull(mutableSortedSetImpl, "mutableSortedSetImpl must not be null");
    requireNonNull(mutableNavigableSet, "mutableNavigableSet must not be null");
    requireNonNull(mutableTreeSet, "mutableTreeSet must not be null");
    requireNonNull(mutableUserSet, "mutableUserSet must not be null");
    requireNonNull(mutableUserSetImpl, "mutableUserSetImpl must not be null");
    requireNonNull(mutableUserSortedSet, "mutableUserSortedSet must not be null");
    requireNonNull(mutableUserSortedSetImpl, "mutableUserSortedSetImpl must not be null");
    requireNonNull(mutableUserNavigableSet, "mutableUserNavigableSet must not be null");
    requireNonNull(mutableUserNavigableSetImpl, "mutableUserNavigableSetImpl must not be null");
    requireNonNull(mutableImmutableSet, "mutableImmutableSet must not be null");

    // pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension
    set = ARICE.immutable(set);
    sortedSet = ARICE.immutable(sortedSet);
    navigableSet = ARICE.immutable(navigableSet);
  }
}