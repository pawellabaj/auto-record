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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.com.labaj.autorecord.AutoRecord;

import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.com.labaj.autorecord.extension.arice.ImmutabilityTest.TestArgument.data;

class ImmutabilityTest {
    @AutoRecord
    @AutoRecord.Extension(extensionClass = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension")
    static
    interface Simple {
        Object value();
        @Mutable Object mutableValue();
    }


    @SuppressWarnings("unchecked")
    static Stream<Arguments> testData() {
        var aValue = "A";
        return Stream.of(
                data(Collection.class, TestCollection::new, collection -> collection.add(aValue)),
                data(Set.class, HashSet::new, set -> set.add(aValue)),
                data(SortedSet.class, TreeSet::new, set -> set.add(aValue)),
                data(NavigableSet.class, TreeSet::new, set -> set.add(aValue)),
                data(List.class, ArrayList::new, list -> list.add(aValue)),
                data(Queue.class, ArrayDeque::new, queue -> queue.add(aValue)),
                data(Deque.class, ArrayDeque::new, queue -> queue.add(aValue)),
                data(Map.class, HashMap::new, map -> map.put(aValue, aValue))
        );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @ParameterizedTest(name = "{0}")
    @MethodSource("testData")
    void shouldWrapCollectionWithImmutable(Class aClass, Supplier mutableValueSupplier, Consumer methodToCall) {
        //given
        var mutableValue = mutableValueSupplier.get();

        //when
        var simpleRecord = new ImmutabilityTest_SimpleRecord(mutableValue, mutableValue);
        var immutableValue = simpleRecord.value();
        var mutableValueFromRecord = simpleRecord.mutableValue();

        System.out.println(mutableValue.getClass() + " -> " + immutableValue.getClass());

        //then
        assertAll(
                () -> assertThat(immutableValue).isInstanceOf(aClass),
                () -> assertDoesNotThrow(() -> methodToCall.accept(mutableValue)),
                () -> assertThrows(UnsupportedOperationException.class, () -> methodToCall.accept(immutableValue)),
                () -> assertThat(mutableValueFromRecord).isSameInstanceAs(mutableValue),
                () -> assertDoesNotThrow(() -> methodToCall.accept(mutableValueFromRecord))
        );
    }
    record TestArgument<E>(Class<E> aClass, Supplier<? extends E> mutableValueSupplier, Consumer<E> methodToCall) implements Arguments {


        static <E> TestArgument<E> data(Class<E> aClass, Supplier<? extends E> mutableValueSupplier, Consumer<E> methodToCall) {
            return new TestArgument<>(aClass, mutableValueSupplier, methodToCall);
        }
        @Override
        public Object[] get() {
            return new Object[] {aClass, mutableValueSupplier, methodToCall};
        }

    }
    private static class TestCollection<E> extends AbstractCollection<E> implements Collection<E> {

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public E next() {
                    return null;
                }
            };
        }

        @Override
        public int size() {
            return 0;
        }
        @Override
        public boolean add(E e) {
            return false;
        }

    }
}
