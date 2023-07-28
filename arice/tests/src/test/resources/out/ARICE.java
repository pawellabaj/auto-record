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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.RangeMap;
import com.google.common.collect.RangeSet;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.Table;
import java.lang.Object;
import java.lang.SuppressWarnings;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.collections.ImmutableCollection;
import pl.com.labaj.autorecord.collections.ImmutableDeque;

/**
 * Class providing methods to copy collections to their corresponding immutable versions
 */
@Generated(
    value = {
        "pl.com.labaj.autorecord.processor.AutoRecordProcessor",
        "pl.com.labaj.autorecord.extension.arice.ARICEUtilitiesProcessor"
    },
    comments = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension"
)
@GeneratedWithAutoRecord
public final class ARICE {
  private ARICE() {
  }

  @SuppressWarnings("unchecked")
  public static Object immutable(Object object) {
    if (object instanceof Collection collection) {
      return immutable(collection);
    }
    if (object instanceof Map map) {
      return immutable(map);
    }
    if (object instanceof Multimap multimap) {
      return immutable(multimap);
    }
    if (object instanceof RangeSet rangeSet) {
      return immutable(rangeSet);
    }
    if (object instanceof RangeMap rangeMap) {
      return immutable(rangeMap);
    }
    if (object instanceof Table table) {
      return immutable(table);
    }
    return object;
  }

  public static <E> Collection<E> immutable(Collection<E> collection) {
    if (collection instanceof ImmutableCollection<E>) {
      return collection;
    }
    if (collection instanceof Set<E> set) {
      return immutable(set);
    }
    if (collection instanceof List<E> list) {
      return immutable(list);
    }
    if (collection instanceof Queue<E> queue) {
      return immutable(queue);
    }
    if (collection instanceof Multiset<E> multiset) {
      return immutable(multiset);
    }
    return ImmutableCollection.copyOfCollection(collection);
  }

  public static <E> Set<E> immutable(Set<E> set) {
    if (set instanceof ImmutableSet<E>) {
      return set;
    }
    if (set instanceof SortedSet<E> sortedSet) {
      return immutable(sortedSet);
    }
    return ImmutableSet.copyOf(set);
  }

  public static <E> SortedSet<E> immutable(SortedSet<E> sortedSet) {
    if (sortedSet instanceof NavigableSet<E> navigableSet) {
      return immutable(navigableSet);
    }
    return ImmutableSortedSet.copyOfSorted(sortedSet);
  }

  public static <E> NavigableSet<E> immutable(NavigableSet<E> navigableSet) {
    if (navigableSet instanceof ImmutableSortedSet<E>) {
      return navigableSet;
    }
    return ImmutableSortedSet.copyOfSorted(navigableSet);
  }

  public static <E> List<E> immutable(List<E> list) {
    if (list instanceof ImmutableList<E>) {
      return list;
    }
    return ImmutableList.copyOf(list);
  }

  public static <E> Queue<E> immutable(Queue<E> queue) {
    if (queue instanceof Deque<E> deque) {
      return immutable(deque);
    }
    return ImmutableDeque.copyOfQueue(queue);
  }

  public static <E> Deque<E> immutable(Deque<E> deque) {
    if (deque instanceof ImmutableDeque<E>) {
      return deque;
    }
    return ImmutableDeque.copyOfQueue(deque);
  }

  public static <K, V> Map<K, V> immutable(Map<K, V> map) {
    if (map instanceof ImmutableMap<K, V>) {
      return map;
    }
    if (map instanceof SortedMap<K,V> sortedMap) {
      return immutable(sortedMap);
    }
    return ImmutableMap.copyOf(map);
  }

  public static <K, V> SortedMap<K, V> immutable(SortedMap<K, V> sortedMap) {
    if (sortedMap instanceof NavigableMap<K,V> navigableMap) {
      return immutable(navigableMap);
    }
    return ImmutableSortedMap.copyOfSorted(sortedMap);
  }

  public static <K, V> NavigableMap<K, V> immutable(NavigableMap<K, V> navigableMap) {
    if (navigableMap instanceof ImmutableSortedMap<K, V>) {
      return navigableMap;
    }
    return ImmutableSortedMap.copyOfSorted(navigableMap);
  }

  public static <E> Multiset<E> immutable(Multiset<E> multiset) {
    if (multiset instanceof ImmutableMultiset<E>) {
      return multiset;
    }
    if (multiset instanceof SortedMultiset<E> sortedMultiset) {
      return immutable(sortedMultiset);
    }
    return ImmutableMultiset.copyOf(multiset);
  }

  public static <E> SortedMultiset<E> immutable(SortedMultiset<E> sortedMultiset) {
    if (sortedMultiset instanceof ImmutableSortedMultiset<E>) {
      return sortedMultiset;
    }
    return ImmutableSortedMultiset.copyOfSorted(sortedMultiset);
  }

  public static <K, V> Multimap<K, V> immutable(Multimap<K, V> multimap) {
    if (multimap instanceof ImmutableMultimap<K, V>) {
      return multimap;
    }
    if (multimap instanceof SetMultimap<K,V> setMultimap) {
      return immutable(setMultimap);
    }
    if (multimap instanceof ListMultimap<K,V> listMultimap) {
      return immutable(listMultimap);
    }
    return ImmutableMultimap.copyOf(multimap);
  }

  public static <K, V> SetMultimap<K, V> immutable(SetMultimap<K, V> setMultimap) {
    if (setMultimap instanceof ImmutableSetMultimap<K, V>) {
      return setMultimap;
    }
    return ImmutableSetMultimap.copyOf(setMultimap);
  }

  public static <K, V> ListMultimap<K, V> immutable(ListMultimap<K, V> listMultimap) {
    if (listMultimap instanceof ImmutableListMultimap<K, V>) {
      return listMultimap;
    }
    return ImmutableListMultimap.copyOf(listMultimap);
  }

  public static <E extends Comparable<E>> RangeSet<E> immutable(RangeSet<E> rangeSet) {
    if (rangeSet instanceof ImmutableRangeSet<E>) {
      return rangeSet;
    }
    return ImmutableRangeSet.copyOf(rangeSet);
  }

  public static <K extends Comparable<K>, V> RangeMap<K, V> immutable(RangeMap<K, V> rangeMap) {
    if (rangeMap instanceof ImmutableRangeMap<K, V>) {
      return rangeMap;
    }
    return ImmutableRangeMap.copyOf(rangeMap);
  }

  public static <R, C, V> Table<R, C, V> immutable(Table<R, C, V> table) {
    if (table instanceof ImmutableTable<R, C, V>) {
      return table;
    }
    return ImmutableTable.copyOf(table);
  }
}