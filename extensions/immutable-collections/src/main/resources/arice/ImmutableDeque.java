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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;
import java.util.stream.Collector;

public class ImmutableDeque<E> implements Deque<E> {

    private final Deque<E> delegate;

    public static <E> ImmutableDeque<E> copyOfQueue(Queue<? extends E> queue) {
        return new ImmutableDeque<>(queue);
    }

    public static <E> Collector<E, ?, ImmutableDeque<E>> toImmutableDeque() {
        return Collectors.toImmutableDeque();
    }

    ImmutableDeque(Queue<? extends E> deque) {
        delegate = new ArrayDeque<>(deque);
    }

    @Override
    public final void addFirst(E e) {
        throw new UnsupportedOperationException("addFirst");
    }

    @Override
    public final void addLast(E e) {
        throw new UnsupportedOperationException("addLast");
    }

    @Override
    public final boolean offerFirst(E e) {
        throw new UnsupportedOperationException("offerFirst");
    }

    @Override
    public final boolean offerLast(E e) {
        throw new UnsupportedOperationException("offerLast");
    }

    @Override
    public final E removeFirst() {
        throw new UnsupportedOperationException("removeFirst");
    }

    @Override
    public final E removeLast() {
        throw new UnsupportedOperationException("removeLast");
    }

    @Override
    public final E pollFirst() {
        throw new UnsupportedOperationException("pollFirst");
    }

    @Override
    public final E pollLast() {
        throw new UnsupportedOperationException("pollLast");
    }

    @Override
    public E getFirst() {
        return delegate.getFirst();
    }

    @Override
    public E getLast() {
        return delegate.getLast();
    }

    @Override
    public E peekFirst() {
        return delegate.peekFirst();
    }

    @Override
    public E peekLast() {
        return delegate.getLast();
    }

    @Override
    public final boolean removeFirstOccurrence(Object o) {
        throw new UnsupportedOperationException("removeFirstOccurrence");
    }

    @Override
    public final boolean removeLastOccurrence(Object o) {
        throw new UnsupportedOperationException("removeLastOccurrence");
    }

    @Override
    public final boolean add(E e) {
        throw new UnsupportedOperationException("add");
    }

    @Override
    public final boolean offer(E e) {
        throw new UnsupportedOperationException("offer");
    }

    @Override
    public final E remove() {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public final E poll() {
        throw new UnsupportedOperationException("poll");
    }

    @Override
    public E element() {
        return delegate.element();
    }

    @Override
    public E peek() {
        return delegate.peek();
    }

    @Override
    public final boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("addAll");
    }

    @Override
    public final boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("removeAll");
    }

    @Override
    public final boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("retainAll");
    }

    @Override
    public final void clear() {
        throw new UnsupportedOperationException("clear");
    }

    @Override
    public final void push(E e) {
        throw new UnsupportedOperationException("push");
    }

    @Override
    public final E pop() {
        throw new UnsupportedOperationException("pop");
    }

    @Override
    public final boolean remove(Object o) {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return new ImmutableIterator<>(delegate.iterator());
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new ImmutableIterator<>(delegate.descendingIterator());
    }
}
