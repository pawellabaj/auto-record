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

import com.google.common.collect.ImmutableSet;
import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.extension.arice.Mutable;
import pl.com.labaj.autorecord.testcase.user.UserCollections;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@AutoRecord
@AutoRecord.Extension(extensionClass = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension")
interface ItemWithMutableSets<E, M extends Enum<M>> {
    Set<E> set();
    HashSet<E> hashSet();
    LinkedHashSet<E> linkedHashSet();
    SortedSet<E> sortedSet();
    UserCollections.SortedSetImpl<E> sortedSetImpl();
    NavigableSet<E> navigableSet();
    TreeSet<E> treeSet();
    UserCollections.UserSet<E> userSet();
    UserCollections.UserSetImpl<E> userSetImpl();
    UserCollections.UserSortedSet<E> userSortedSet();
    UserCollections.UserSortedSetImpl<E> userSortedSetImpl();
    UserCollections.UserNavigableSet<E> userNavigableSet();
    UserCollections.UserNavigableSetImpl<E> userNavigableSetImpl();
    ImmutableSet<E> immutableSet();
    @Mutable Set<E> mutableSet();
    @Mutable HashSet<E> mutableHashSet();
    @Mutable LinkedHashSet<E> mutableLinkedHashSet();
    @Mutable SortedSet<E> mutableSortedSet();
    @Mutable UserCollections.SortedSetImpl<E> mutableSortedSetImpl();
    @Mutable NavigableSet<E> mutableNavigableSet();
    @Mutable TreeSet<E> mutableTreeSet();
    @Mutable UserCollections.UserSet<E> mutableUserSet();
    @Mutable UserCollections.UserSetImpl<E> mutableUserSetImpl();
    @Mutable UserCollections.UserSortedSet<E> mutableUserSortedSet();
    @Mutable UserCollections.UserSortedSetImpl<E> mutableUserSortedSetImpl();
    @Mutable UserCollections.UserNavigableSet<E> mutableUserNavigableSet();
    @Mutable UserCollections.UserNavigableSetImpl<E> mutableUserNavigableSetImpl();
    @Mutable
    ImmutableSet<E> mutableImmutableSet();
}
