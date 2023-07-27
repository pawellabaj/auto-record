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

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.extension.arice.AutoRecordImmutableCollectionsUtilities;
import pl.com.labaj.autorecord.extension.arice.Methods_LIBZVBZLV;
import pl.com.labaj.autorecord.testcase.user.UserCollections;

@Generated("pl.com.labaj.autorecord.AutoRecord")
@GeneratedWithAutoRecord
@AutoRecordImmutableCollectionsUtilities(
        className = "pl.com.labaj.autorecord.extension.arice.Methods_LIBZVBZLV",
        immutableTypes = {
                "pl.com.labaj.autorecord.testcase.user.UserCollections.UserMap",
                "pl.com.labaj.autorecord.testcase.user.UserCollections.UserNavigableMap",
                "pl.com.labaj.autorecord.testcase.user.UserCollections.UserSortedMap"
        }
)
record ItemWithMapsRecord<K, V>(Map<K, V> set,
                                HashMap<K, V> hashMap,
                                TreeMap<K, V> treeMap,
                                SortedMap<K, V> sortedMap,
                                NavigableMap<K, V> navigableMap,
                                UserCollections.UserMap<K, V> userMap,
                                UserCollections.UserMapImpl<K, V> userMapImpl,
                                UserCollections.UserSortedMap<K, V> userSortedMap,
                                UserCollections.UserSortedMapImpl<K, V> userSortedMapImpl,
                                UserCollections.UserNavigableMap<K, V> userNavigableMap,
                                UserCollections.UserNavigableMapImpl<K, V> userNavigableMapImpl,
                                ImmutableMap<K, V> immutableMap,
                                @Nullable Map<K, V> nullableSet,
                                @Nullable HashMap<K, V> nullableHashMap,
                                @Nullable TreeMap<K, V> nullableTreeMap,
                                @Nullable SortedMap<K, V> nullableSortedMap,
                                @Nullable NavigableMap<K, V> nullableNavigableMap,
                                @Nullable UserCollections.UserMap<K, V> nullableUserMap,
                                @Nullable UserCollections.UserMapImpl<K, V> nullableUserMapImpl,
                                @Nullable UserCollections.UserSortedMap<K, V> nullableUserSortedMap,
                                @Nullable UserCollections.UserSortedMapImpl<K, V> nullableUserSortedMapImpl,
                                @Nullable UserCollections.UserNavigableMap<K, V> nullableUserNavigableMap,
                                @Nullable UserCollections.UserNavigableMapImpl<K, V> nullableUserNavigableMapImpl,
                                @Nullable ImmutableMap<K, V> nullableImmutableMap) implements ItemWithMaps<K, V> {
    ItemWithMapsRecord {
        // pl.com.labaj.autorecord.processor.AutoRecordProcessor
        requireNonNull(set, "set must not be null");
        requireNonNull(hashMap, "hashMap must not be null");
        requireNonNull(treeMap, "treeMap must not be null");
        requireNonNull(sortedMap, "sortedMap must not be null");
        requireNonNull(navigableMap, "navigableMap must not be null");
        requireNonNull(userMap, "userMap must not be null");
        requireNonNull(userMapImpl, "userMapImpl must not be null");
        requireNonNull(userSortedMap, "userSortedMap must not be null");
        requireNonNull(userSortedMapImpl, "userSortedMapImpl must not be null");
        requireNonNull(userNavigableMap, "userNavigableMap must not be null");
        requireNonNull(userNavigableMapImpl, "userNavigableMapImpl must not be null");
        requireNonNull(immutableMap, "immutableMap must not be null");

        // pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension
        set = Methods_LIBZVBZLV.immutable(set);
        sortedMap = Methods_LIBZVBZLV.immutable(sortedMap);
        navigableMap = Methods_LIBZVBZLV.immutable(navigableMap);
        nullableSet = isNull(nullableSet) ? null : Methods_LIBZVBZLV.immutable(nullableSet);
        nullableSortedMap = isNull(nullableSortedMap) ? null : Methods_LIBZVBZLV.immutable(nullableSortedMap);
        nullableNavigableMap = isNull(nullableNavigableMap) ? null : Methods_LIBZVBZLV.immutable(nullableNavigableMap);
    }
}