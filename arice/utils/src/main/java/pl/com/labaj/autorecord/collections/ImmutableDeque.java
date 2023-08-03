package pl.com.labaj.autorecord.collections;

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

import org.apiguardian.api.API;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;
import java.util.stream.Collector;

import static java.util.Objects.requireNonNull;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * An immutable implementation of the {@link Deque} interface that wraps a delegate deque.
 * The elements in the Deque cannot be modified after creation, and attempts to modify the Deque will result in
 * {@link UnsupportedOperationException} being thrown.
 * <p>
 * The class provides various static factory methods to create instances of ImmutableDeque.
 *
 * @param <E> the type of elements in the deque
 */
@API(status = STABLE)
public class ImmutableDeque<E> extends AbstractImmutableCollection<E> implements Deque<E> {

    private final Deque<E> delegate;

    /**
     * Creates an immutable copy of the given queue. The elements in the deque will be in the order
     * given by the collection's iterator.
     *
     * @param queue the collection whose elements are to be placed into the Deque
     * @param <E>   the type of elements in the deque
     * @return an immutable copy of the given queue
     * @throws NullPointerException if the specified collection is null
     */
    public static <E> ImmutableDeque<E> copyOfQueue(Queue<? extends E> queue) {
        requireNonNull(queue, "queue must not be null");
        return new ImmutableDeque<>(queue);
    }

    /**
     * Creates an immutable copy of the given collection. The elements in the deque will be in the order
     * given by the collection's iterator.
     *
     * @param collection the collection whose elements are to be placed into the Deque
     * @param <E>        the type of elements in the deque
     * @return an immutable copy of the given collection
     * @throws NullPointerException if the specified collection is null
     */
    public static <E> ImmutableDeque<E> copyOf(Collection<? extends E> collection) {
        requireNonNull(collection, "collection must not be null");
        return new ImmutableDeque<>(collection);
    }

    /**
     * A collector that accumulates elements into an {@link ImmutableDeque}.
     * The resulting deque will be immutable.
     *
     * @param <E> the type of elements to be collected
     * @return a collector that accumulates elements into an immutable deque
     * @see ImmutableDeque#toImmutableDeque()
     */
    public static <E> Collector<E, ?, ImmutableDeque<E>> toImmutableDeque() {
        return Collectors.toImmutableDeque();
    }

    ImmutableDeque(Collection<? extends E> collection) {
        delegate = new ArrayDeque<>(collection);
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final void addFirst(E e) {
        throw new UnsupportedOperationException("addFirst");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final void addLast(E e) {
        throw new UnsupportedOperationException("addLast");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final boolean offerFirst(E e) {
        throw new UnsupportedOperationException("offerFirst");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final boolean offerLast(E e) {
        throw new UnsupportedOperationException("offerLast");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final E removeFirst() {
        throw new UnsupportedOperationException("removeFirst");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final E removeLast() {
        throw new UnsupportedOperationException("removeLast");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final E pollFirst() {
        throw new UnsupportedOperationException("pollFirst");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final E pollLast() {
        throw new UnsupportedOperationException("pollLast");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getFirst() {
        return delegate.getFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getLast() {
        return delegate.getLast();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E peekFirst() {
        return delegate.peekFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E peekLast() {
        return delegate.getLast();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final boolean removeFirstOccurrence(Object o) {
        throw new UnsupportedOperationException("removeFirstOccurrence");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final boolean removeLastOccurrence(Object o) {
        throw new UnsupportedOperationException("removeLastOccurrence");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final boolean offer(E e) {
        throw new UnsupportedOperationException("offer");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final E remove() {
        throw new UnsupportedOperationException("remove");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final E poll() {
        throw new UnsupportedOperationException("poll");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E element() {
        return delegate.element();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E peek() {
        return delegate.peek();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final void push(E e) {
        throw new UnsupportedOperationException("push");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final E pop() {
        throw new UnsupportedOperationException("pop");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return delegate.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return new ImmutableIterator<>(delegate.iterator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> descendingIterator() {
        return new ImmutableIterator<>(delegate.descendingIterator());
    }
}
