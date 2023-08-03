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
import pl.com.labaj.autorecord.testcase.user.UserCollections;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@AutoRecord
@AutoRecord.Extension(
        extensionClass = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension",
        parameters = {
                "pl.com.labaj.autorecord.testcase.user.UserCollections.UserSet",
                "pl.com.labaj.autorecord.testcase.user.UserCollections.UserSortedSet",
                "pl.com.labaj.autorecord.testcase.user.UserCollections.UserNavigableSet"
        })
interface ItemWithSetsCustomTypes<E, M extends Enum<M>> {
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
    @Nullable Set<E> nullableSet();
    @Nullable HashSet<E> nullableHashSet();
    @Nullable LinkedHashSet<E> nullableLinkedHashSet();
    @Nullable SortedSet<E> nullableSortedSet();
    @Nullable UserCollections.SortedSetImpl<E> nullableSortedSetImpl();
    @Nullable NavigableSet<E> nullableNavigableSet();
    @Nullable TreeSet<E> nullableTreeSet();
    @Nullable UserCollections.UserSet<E> nullableUserSet();
    @Nullable UserCollections.UserSetImpl<E> nullableUserSetImpl();
    @Nullable UserCollections.UserSortedSet<E> nullableUserSortedSet();
    @Nullable UserCollections.UserSortedSetImpl<E> nullableUserSortedSetImpl();
    @Nullable UserCollections.UserNavigableSet<E> nullableUserNavigableSet();
    @Nullable
    UserCollections.UserNavigableSetImpl<E> nullableUserNavigableSetImpl();
    @Nullable ImmutableSet<E> nullableImmutableSet();
}
