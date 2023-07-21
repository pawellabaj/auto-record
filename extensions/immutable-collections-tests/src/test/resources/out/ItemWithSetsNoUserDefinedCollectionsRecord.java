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

@Generated("pl.com.labaj.autorecord.AutoRecord")
@GeneratedWithAutoRecord
@AutoRecordImmutableCollectionsUtilities(className = "pl.com.labaj.autorecord.extension.arice.Methods")
record ItemWithSetsNoUserDefinedCollectionsRecord<E, M extends Enum<M>>(Set<E> set,
                                                                        HashSet<E> hashSet,
                                                                        LinkedHashSet<E> linkedHashSet,
                                                                        SortedSet<E> sortedSet,
                                                                        NavigableSet<E> navigableSet,
                                                                        TreeSet<E> treeSet,
                                                                        ImmutableSet<E> immutableSet,
                                                                        @Nullable Set<E> nullableSet,
                                                                        @Nullable HashSet<E> nullableHashSet,
                                                                        @Nullable LinkedHashSet<E> nullableLinkedHashSet,
                                                                        @Nullable SortedSet<E> nullableSortedSet,
                                                                        @Nullable NavigableSet<E> nullableNavigableSet,
                                                                        @Nullable TreeSet<E> nullableTreeSet,
                                                                        @Nullable ImmutableSet<E> nullableImmutableSet) implements ItemWithSetsNoUserDefinedCollections<E, M> {
    ItemWithSetsNoUserDefinedCollectionsRecord {
        // pl.com.labaj.autorecord.processor.AutoRecordProcessor
        requireNonNull(set, "set must not be null");
        requireNonNull(hashSet, "hashSet must not be null");
        requireNonNull(linkedHashSet, "linkedHashSet must not be null");
        requireNonNull(sortedSet, "sortedSet must not be null");
        requireNonNull(navigableSet, "navigableSet must not be null");
        requireNonNull(treeSet, "treeSet must not be null");
        requireNonNull(immutableSet, "immutableSet must not be null");

        // pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension
        set = Methods.copyOfSet(set);
        sortedSet = Methods.copyOfSortedSet(sortedSet);
        navigableSet = Methods.copyOfNavigableSet(navigableSet);
        nullableSet = isNull(nullableSet) ? null : Methods.copyOfSet(nullableSet);
        nullableSortedSet = isNull(nullableSortedSet) ? null : Methods.copyOfSortedSet(nullableSortedSet);
        nullableNavigableSet = isNull(nullableNavigableSet) ? null : Methods.copyOfNavigableSet(nullableNavigableSet);
    }
}