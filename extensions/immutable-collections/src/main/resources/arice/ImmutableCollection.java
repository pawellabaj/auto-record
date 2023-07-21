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
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class ImmutableCollection<E> implements Collection<E> {

    private final com.google.common.collect.ImmutableCollection<E> delegate;

    public static <E> ImmutableCollection<E> copyOfCollection(Collection<? extends E> collection) {
        return new ImmutableCollection<>(collection);
    }

    public ImmutableCollection(Collection<? extends E> collection) {
        delegate = (collection instanceof Set) ? ImmutableSet.copyOf(collection) : ImmutableList.copyOf(collection);
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
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
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
    public final boolean add(E e) {
        throw new UnsupportedOperationException("add");
    }

    @Override
    public final boolean remove(Object o) {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public final boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
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
}
