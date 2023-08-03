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

import com.google.common.collect.ImmutableList;
import org.apiguardian.api.API;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collector;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * An implementation of {@link Collection} that represents an immutable collection. This class is backed by
 * Google Guava's {@link com.google.common.collect.ImmutableCollection}, which ensures that the collection cannot be
 * modified after creation.
 * <p>
 * The {@link ImmutableCollection} can be created using the static factory method {@link #copyOfCollection(Collection)}
 * or by using the {@link #toImmutableCollection()} collector.
 *
 * @param <E> the type of elements in the collection
 */
@API(status = STABLE)
public class ImmutableCollection<E> extends AbstractImmutableCollection<E> {

    private final com.google.common.collect.ImmutableCollection<E> delegate;

    /**
     * Returns an immutable copy of the given collection. If the input collection is already an instance of
     * {@link ImmutableCollection}, it is returned as is. Otherwise, a new {@link ImmutableCollection} is created
     * based on whether duplicates are allowed in the input collection or not.
     *
     * @param collection the input collection to copy
     * @param <E>        the type of elements in the collection
     * @return an immutable collection that is a copy of the input collection
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <E> ImmutableCollection<E> copyOfCollection(Collection<? extends E> collection) {
        if (collection instanceof ImmutableCollection immutableCollection) {
            return immutableCollection;
        }

        return new ImmutableCollection<>(collection);
    }

    /**
     * A collector that accumulates elements into an {@link ImmutableCollection}.
     * The resulting collection can either allow duplicates or not, depending on the provided flag.
     *
     * @param <E> the type of elements to be collected
     * @return a collector that accumulates elements into an immutable collection
     */
    public static <E> Collector<E, ?, ImmutableCollection<E>> toImmutableCollection() {
        return Collectors.toImmutableCollection();
    }

    ImmutableCollection(Collection<? extends E> collection) {
        delegate = ImmutableList.copyOf(collection);
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
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
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
    public final boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }
}
