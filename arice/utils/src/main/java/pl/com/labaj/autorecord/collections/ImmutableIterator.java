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

import java.util.Iterator;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * An immutable wrapper around an {@link Iterator} that prevents modifications to the underlying collection.
 * The class implements the Iterator interface and delegates the actual iteration operations to the provided
 * delegate Iterator. Any attempt to call the {@link #remove()} method will result in an
 * {@link UnsupportedOperationException} being thrown, as the Iterator is immutable.
 *
 * @param <E> the type of elements returned by this iterator
 */
@API(status = STABLE)
public class ImmutableIterator<E> implements Iterator<E> {
    private final Iterator<E> delegate;

    /**
     * Creates an immutable Iterator that wraps the provided delegate Iterator.
     *
     * @param iterator the delegate Iterator to be wrapped
     */
    public ImmutableIterator(Iterator<E> iterator) {
        delegate = iterator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E next() {
        return delegate.next();
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    public final void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
