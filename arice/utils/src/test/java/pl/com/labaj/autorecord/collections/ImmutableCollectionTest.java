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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.com.labaj.autorecord.collections.ImmutableCollection.toImmutableCollection;

class ImmutableCollectionTest {

    public static Stream<Arguments> testData() {
        return Stream.of(
                new TestArgument<>(
                        () -> List.of("A", "B", "A", "B"),
                        ImmutableCollection::copyOfCollection),
                new TestArgument<>(
                        () -> Set.of("A", "B"),
                        ImmutableCollection::copyOfCollection),
                new TestArgument<>(
                        () -> List.of("A", "B", "A", "B"),
                        ImmutableCollection::copyOfCollection),
                new TestArgument<>(
                        () -> List.of("A", "B", "A", "B"),
                        collection -> collection.stream().collect(toImmutableCollection())),
                new TestArgument<>(
                        () -> List.of("A", "B", "A", "B"),
                        list -> list.stream().collect(toImmutableCollection()))
        );
    }

    @SuppressWarnings({"rawtypes", "unchecked", "Convert2MethodRef", "deprecation"})
    @ParameterizedTest
    @MethodSource("testData")
    void shouldReurnImmutableCollection(Supplier<Collection> collectionSupplier, Function<Collection, ImmutableCollection> icFunction) {
        //given
        var collection = collectionSupplier.get();

        //when
        var immutableCollection = icFunction.apply(collection);

        //then
        assertAll(
                () -> assertThat(immutableCollection).hasSameElementsAs(collection),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableCollection.add("A")),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableCollection.remove("A")),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableCollection.addAll(List.of("A", "B"))),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableCollection.removeAll(List.of("A", "B"))),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableCollection.retainAll(List.of("A", "B"))),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableCollection.clear()),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableCollection.iterator().remove())
        );
    }

    record TestArgument<E>(Supplier<Collection<E>> collectionSupplier, Function<Collection<E>, ImmutableCollection<E>> icFunction) implements Arguments {

        @Override
        public Object[] get() {
            return new Object[] {collectionSupplier, icFunction};
        }
    }
}