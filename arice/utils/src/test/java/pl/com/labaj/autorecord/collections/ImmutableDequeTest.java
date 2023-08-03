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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.com.labaj.autorecord.collections.ImmutableDeque.toImmutableDeque;

class ImmutableDequeTest {

    @SuppressWarnings("unchecked")
    public static Stream<Arguments> testData() {
        return Stream.of(
                new TestArgument<>(
                        () -> List.of("A", "B", "A", "B"),
                        ImmutableDeque::copyOf),
                new TestArgument<>(
                        () -> {
                            var arrayDeque = new ArrayDeque<String>();
                            arrayDeque.add("A");
                            arrayDeque.add("B");
                            arrayDeque.add("A");
                            arrayDeque.add("B");
                            return arrayDeque;
                        },
                        ImmutableDeque::copyOf),
                new TestArgument<>(
                        () -> {
                            var arrayDeque = new ArrayDeque<String>();
                            arrayDeque.add("A");
                            arrayDeque.add("B");
                            arrayDeque.add("A");
                            arrayDeque.add("B");
                            return arrayDeque;
                        },
                        deque -> ImmutableDeque.copyOfQueue((ArrayDeque<String>) deque)),
                new TestArgument<>(
                        () -> List.of("A", "B", "A", "B"),
                        list -> list.stream().collect(toImmutableDeque()))
        );
    }

    @SuppressWarnings({"rawtypes", "unchecked", "Convert2MethodRef", "deprecation"})
    @ParameterizedTest
    @MethodSource("testData")
    void shouldReurnImmutableCollection(Supplier<Collection> collectionSupplier, Function<Collection, ImmutableDeque> idFunction) {
        //given
        var collection = collectionSupplier.get();
        var anotherCollection = List.of("A", "B");

        //when
        var immutableDeque = idFunction.apply(collection);
        var iterator = immutableDeque.iterator();
        var descendingIterator = immutableDeque.descendingIterator();

        //then
        assertAll(
                () -> assertThat(immutableDeque).hasSameElementsAs(collection),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.addFirst("A")),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.addLast("A")),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.offerFirst("A")),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.offerLast("A")),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.removeFirst()),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.removeLast()),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.pollFirst()),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.pollLast()),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.removeFirstOccurrence("A")),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.removeLastOccurrence("A")),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.add("A")),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.offer("A")),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.remove("A")),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.remove()),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.poll()),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.addAll(anotherCollection)),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.removeAll(anotherCollection)),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.retainAll(anotherCollection)),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.clear()),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.pop()),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.push("A")),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableDeque.remove("A")),
                () -> assertThrows(UnsupportedOperationException.class, () -> iterator.remove()),
                () -> assertThrows(UnsupportedOperationException.class, () -> descendingIterator.remove())
        );
    }

    record TestArgument<E>(Supplier<Collection<E>> collectionSupplier, Function<Collection<E>, ImmutableDeque<E>> idFunction) implements Arguments {

        @Override
        public Object[] get() {
            return new Object[] {collectionSupplier, idFunction};
        }
    }
}