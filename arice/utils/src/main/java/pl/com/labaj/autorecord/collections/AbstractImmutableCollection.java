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

import java.util.Collection;

abstract class AbstractImmutableCollection<E> implements Collection<E> {
    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final boolean add(E e) {
        throw new UnsupportedOperationException("add");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final boolean remove(Object o) {
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
    public final boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("addAll");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("removeAll");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated(since = "1.0.0")
    public final boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("retainAll");
    }

    /**
     * Guaranteed to throw an exception and leave the collection unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */

    @Override
    @Deprecated(since = "1.0.0")
    public final void clear() {
        throw new UnsupportedOperationException("clear");
    }
}
