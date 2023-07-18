package pl.com.labaj.autorecord.extension.compact;

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

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;

import static java.util.Objects.isNull;

@SuppressWarnings("NullableProblems")
public final class ImmutableSets {

    private ImmutableSets() {}

    @SuppressWarnings("java:S1168")
    public static <E> NavigableSet<E> immutableNavigableSet(NavigableSet<E> navigableSet) {
        if (isNull(navigableSet)) {
            return null;
        }

        if (navigableSet.isEmpty()) {
            return Collections.emptyNavigableSet();
        }

        return new ImmutableNavigableSet<>(navigableSet);
    }

    @SuppressWarnings("java:S1168")
    public static <E> SortedSet<E> immutableSortedSet(SortedSet<E> sortedSet) {
        if (isNull(sortedSet)) {
            return null;
        }

        if (sortedSet.isEmpty()) {
            return Collections.emptySortedSet();
        }

        return new ImmutableSortedSet<>(sortedSet);
    }

    @SuppressWarnings({"java:S1168", "java:S1319", "unchecked"})
    public static <E> LinkedHashSet<E> immutableLinkedHashSet(LinkedHashSet<E> linkedHashSet) {
        if (isNull(linkedHashSet)) {
            return null;
        }

        if (linkedHashSet.isEmpty()) {
            return (LinkedHashSet<E>) ImmutableLinkedHashSet.EMPTY;
        }

        return new ImmutableLinkedHashSet<>(linkedHashSet);
    }

    @SuppressWarnings("java:S1168")
    public static <E> Set<E> immutableSet(Set<E> set) {
        if (isNull(set)) {
            return null;
        }

        if (set.isEmpty()) {
            return Collections.emptySet();
        }

        if (set instanceof NavigableSet<E> navigableSet) {
            return new ImmutableNavigableSet<>(navigableSet);
        }

        if (set instanceof SortedSet<E> sortedSet) {
            return new ImmutableSortedSet<>(sortedSet);
        }

        return Set.copyOf(set);
    }

    private static UnsupportedOperationException uoe(String methodName) {
        return new UnsupportedOperationException(methodName);
    }

    private record ImmutableIterator<E>(Iterator<E> iterator) implements Iterator<E> {

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public E next() {
            return iterator.next();
        }
    }

    @SuppressWarnings("java:S2160")
    private static sealed class ImmutableSortedSet<E> extends AbstractSet<E> implements SortedSet<E>, Serializable permits ImmutableNavigableSet {

        protected final transient TreeSet<E> elements;
        private final int size;

        protected ImmutableSortedSet(SortedSet<E> sortedSet) {
            elements = new TreeSet<>(sortedSet.comparator());
            elements.addAll(sortedSet);
            size = elements.size();
        }

        @Override
        public Iterator<E> iterator() {return new ImmutableIterator<>(elements.iterator());}

        @Override
        public int size() {return size;}

        @Override
        public boolean isEmpty() {return size == 0;}

        @Nullable
        @Override
        public Comparator<? super E> comparator() {return elements.comparator();}

        @Override
        public SortedSet<E> subSet(E fromElement, E toElement) {return new ImmutableSortedSet<>(elements.subSet(fromElement, toElement));}

        @Override
        public SortedSet<E> headSet(E toElement) {return new ImmutableSortedSet<>(elements.headSet(toElement));}

        @Override
        public SortedSet<E> tailSet(E fromElement) {return new ImmutableSortedSet<>(elements.tailSet(fromElement));}

        @Override
        public E first() {return elements.first();}

        @Override
        public E last() {return elements.last();}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ImmutableSortedSet<?> that)) return false;
            if (!super.equals(o)) return false;
            return elements.equals(that.elements);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), elements);
        }
    }

    @SuppressWarnings("java:S2160")
    private static final class ImmutableNavigableSet<E> extends ImmutableSortedSet<E> implements NavigableSet<E> {
        private ImmutableNavigableSet(NavigableSet<E> navigableSet) {
            super(navigableSet);
        }

        @Nullable
        @Override
        public E lower(E e) {return elements.lower(e);}

        @Nullable
        @Override
        public E floor(E e) {return elements.floor(e);}

        @Nullable
        @Override
        public E ceiling(E e) {return elements.ceiling(e);}

        @Nullable
        @Override
        public E higher(E e) {return elements.higher(e);}

        @Nullable
        @Override
        public E pollFirst() {throw uoe("pollFirst");}

        @Nullable
        @Override
        public E pollLast() {throw uoe("pollLast");}

        @Override
        public NavigableSet<E> descendingSet() {return new ImmutableNavigableSet<>(elements.descendingSet());}

        @Override
        public Iterator<E> descendingIterator() {return new ImmutableIterator<>(elements.descendingIterator());}

        @Override
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            return new ImmutableNavigableSet<>(elements.subSet(fromElement, fromInclusive, toElement, toInclusive));
        }

        @Override
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {return new ImmutableNavigableSet<>(elements.headSet(toElement, inclusive));}

        @Override
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {return new ImmutableNavigableSet<>(elements.tailSet(fromElement, inclusive));}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ImmutableNavigableSet<?> that)) return false;
            if (!super.equals(o)) return false;
            return elements.equals(that.elements);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), elements);
        }
    }

    private static final class ImmutableLinkedHashSet<E> extends LinkedHashSet<E> {
        private static final ImmutableLinkedHashSet<?> EMPTY = new ImmutableLinkedHashSet<>(new LinkedHashSet<>(0));
        private final transient LinkedHashSet<E> elements;
        private final int size;

        @SuppressWarnings("java:S1319")
        public ImmutableLinkedHashSet(LinkedHashSet<E> linkedHashSet) {
            elements = new LinkedHashSet<>(linkedHashSet.size());
            elements.addAll(linkedHashSet);
            size = elements.size();
        }

        @Override
        public boolean add(E e) {throw uoe("add");}

        @Override
        public boolean addAll(Collection<? extends E> c) {throw uoe("addAll");}

        @Override
        public void clear() {throw uoe("clear");}

        @Override
        public boolean remove(Object o) {throw uoe("remove");}

        @Override
        public boolean removeAll(Collection<?> c) {throw uoe("removeAll");}

        @Override
        public boolean removeIf(Predicate<? super E> filter) {throw uoe("removeIf");}

        @Override
        public boolean retainAll(Collection<?> c) {throw uoe("retainAll");}

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @Override
        public Iterator<E> iterator() {
            return new ImmutableIterator<>(elements.iterator());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ImmutableLinkedHashSet<?> that)) return false;
            if (!super.equals(o)) return false;
            return elements.equals(that.elements);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), elements);
        }
    }
}
