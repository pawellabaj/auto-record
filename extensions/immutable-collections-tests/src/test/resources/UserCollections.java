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

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

class UserCollections {

    interface UserSet<E> extends Set<E> {}

    interface UserNavigableSet<E> extends NavigableSet<E> {}

    interface UserSortedSet<E> extends SortedSet<E> {}
    interface UserList<E> extends List<E> {}
    interface UserMap<K, V> extends Map<K, V> {}
    public interface UserSortedMap<K, V> extends SortedMap<K, V> {}
    public interface UserNavigableMap<K, V> extends NavigableMap<K, V> {}
    interface UserQueue<E> extends Queue<E> {}
    interface UserDeque<E> extends Deque<E> {}

    static class UserSetImpl<E> extends AbstractSet<E> implements UserSet<E> {
        @Override public Iterator<E> iterator() {return null;}
        @Override public int size() {return 0;}
    }
    static class SortedSetImpl<E> extends AbstractSet<E> implements SortedSet<E> {
        @Override public Iterator<E> iterator() {return null;}
        @Override public int size() {return 0;}
        @Override public Comparator<? super E> comparator() {return null;}
        @Override public SortedSet<E> subSet(E fromElement, E toElement) {return null;}
        @Override public SortedSet<E> headSet(E toElement) {return null;}
        @Override public SortedSet<E> tailSet(E fromElement) {return null;}
        @Override public E first() {return null;}

        @Override public E last() {return null;}

    }
    static class UserSortedSetImpl<E> extends SortedSetImpl<E> implements UserSortedSet<E> {}
    static class UserNavigableSetImpl<E> extends TreeSet<E> implements UserNavigableSet<E> {}
    static class UserListImpl<E> extends AbstractList<E> implements UserList<E>{
        @Override public E get(int index) {return null;}
        @Override public int size() {return 0;}

    }
    static class UserMapImpl<K, V> extends AbstractMap<K, V> implements UserMap<K, V> {
        @Override public Set<Entry<K, V>> entrySet() {return null;}

    }
    static class UserSortedMapImpl<K, V> extends UserMapImpl<K, V> implements UserSortedMap<K, V> {
        @Override public Comparator<? super K> comparator() {return null;}
        @Override public SortedMap<K, V> subMap(K fromKey, K toKey) {return null;}
        @Override public SortedMap<K, V> headMap(K toKey) {return null;}
        @Override public SortedMap<K, V> tailMap(K fromKey) {return null;}
        @Override public K firstKey() {return null;}
        @Override public K lastKey() {return null;}

    }
    static class UserNavigableMapImpl<K, V> extends UserSortedMapImpl<K,V> implements UserNavigableMap<K,V> {
        @Override public Entry<K, V> lowerEntry(K key) {return null;}
        @Override public K lowerKey(K key) {return null;}
        @Override public Entry<K, V> floorEntry(K key) {return null;}
        @Override public K floorKey(K key) {return null;}
        @Override public Entry<K, V> ceilingEntry(K key) {return null;}
        @Override public K ceilingKey(K key) {return null;}
        @Override public Entry<K, V> higherEntry(K key) {return null;}
        @Override public K higherKey(K key) {return null;}
        @Override public Entry<K, V> firstEntry() {return null;}
        @Override public Entry<K, V> lastEntry() {return null;}
        @Override public Entry<K, V> pollFirstEntry() {return null;}
        @Override public Entry<K, V> pollLastEntry() {return null;}
        @Override public NavigableMap<K, V> descendingMap() {return null;}
        @Override public NavigableSet<K> navigableKeySet() {return null;}
        @Override public NavigableSet<K> descendingKeySet() {return null;}
        @Override public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {return null;}
        @Override public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {return null;}
        @Override public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {return null;}
    }

    static class UserQueueImpl<E> extends ArrayDeque<E> implements UserQueue<E> {}

    static class UserDequeImpl<E> extends ArrayDeque<E> implements UserDeque<E> {}
}
