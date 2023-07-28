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

import com.google.common.collect.ImmutableMap;
import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.testcase.user.UserCollections;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

@AutoRecord
@AutoRecord.Extension(extensionClass = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension")
interface ItemWithMaps<K, V> {
    Map<K, V> set();
    HashMap<K, V> hashMap();
    TreeMap<K, V> treeMap();
    SortedMap<K, V> sortedMap();
    NavigableMap<K, V> navigableMap();
    ImmutableMap<K, V> immutableMap();
    @Nullable Map<K, V>nullableSet();
    @Nullable HashMap<K, V>nullableHashMap();
    @Nullable TreeMap<K, V>nullableTreeMap();
    @Nullable SortedMap<K, V>nullableSortedMap();
    @Nullable NavigableMap<K, V> nullableNavigableMap();
    @Nullable ImmutableMap<K, V> nullableImmutableMap();
}
