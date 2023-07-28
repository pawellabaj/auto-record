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

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.RangeMap;
import com.google.common.collect.RangeSet;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.Table;
import java.lang.Comparable;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.extension.arice.ARICE;
import pl.com.labaj.autorecord.extension.arice.ARICEUtilities;

@Generated(
        comments = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension",
        value = "pl.com.labaj.autorecord.AutoRecord"
)
@GeneratedWithAutoRecord
@ARICEUtilities(className = "pl.com.labaj.autorecord.extension.arice.ARICE")
record ItemWithGuavaCollectionsRecord<E, K, V, C extends Comparable<C>>(Multiset<E> multiset,
                                                                        SortedMultiset<E> sortedMultiset,
                                                                        Multimap<K, V> multimap,
                                                                        SetMultimap<K, V> setMultiMap,
                                                                        SortedSetMultimap<K, V> sortedSetMultimap,
                                                                        ListMultimap<K, V> listMultiMap,
                                                                        RangeSet<C> rangeSet,
                                                                        RangeMap<C, V> rangeMap,
                                                                        Table<E, K, V> table) implements ItemWithGuavaCollections<E, K, V, C> {
    ItemWithGuavaCollectionsRecord {
        // pl.com.labaj.autorecord.processor.AutoRecordProcessor
        requireNonNull(multiset, "multiset must not be null");
        requireNonNull(sortedMultiset, "sortedMultiset must not be null");
        requireNonNull(multimap, "multimap must not be null");
        requireNonNull(setMultiMap, "setMultiMap must not be null");
        requireNonNull(sortedSetMultimap, "sortedSetMultimap must not be null");
        requireNonNull(listMultiMap, "listMultiMap must not be null");
        requireNonNull(rangeSet, "rangeSet must not be null");
        requireNonNull(rangeMap, "rangeMap must not be null");
        requireNonNull(table, "table must not be null");

        // pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension
        multiset = ARICE.immutable(multiset);
        sortedMultiset = ARICE.immutable(sortedMultiset);
        multimap = ARICE.immutable(multimap);
        setMultiMap = ARICE.immutable(setMultiMap);
        listMultiMap = ARICE.immutable(listMultiMap);
        rangeSet = ARICE.immutable(rangeSet);
        rangeMap = ARICE.immutable(rangeMap);
        table = ARICE.immutable(table);
    }
}