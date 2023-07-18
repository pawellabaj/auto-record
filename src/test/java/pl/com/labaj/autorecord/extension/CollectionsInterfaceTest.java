package pl.com.labaj.autorecord.extension;

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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.reverseOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CollectionsInterfaceTest {

    public static Stream<Arguments> setsParameters() {
        return Stream.of(
                Arguments.of(Set.class, (Function<CollectionsInterfaceRecord, Set<String>>) CollectionsInterfaceRecord::aSet),
                Arguments.of(SortedSet.class, (Function<CollectionsInterfaceRecord, Set<String>>) CollectionsInterfaceRecord::aSortedSet),
                Arguments.of(NavigableSet.class, (Function<CollectionsInterfaceRecord, Set<String>>) CollectionsInterfaceRecord::aNavigableSet),
                Arguments.of(LinkedHashSet.class, (Function<CollectionsInterfaceRecord, Set<String>>) CollectionsInterfaceRecord::aLinkedHashSet),
                Arguments.of(ImmutableSet.class, (Function<CollectionsInterfaceRecord, Set<String>>) CollectionsInterfaceRecord::anImmutableSet),
                Arguments.of(ImmutableSortedSet.class, (Function<CollectionsInterfaceRecord, Set<String>>) CollectionsInterfaceRecord::anImmutableSortedSet)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("setsParameters")
    @SuppressWarnings({"Convert2MethodRef", "deprecation"})
    void shouldCopySetToImmutable(Class<? extends Set<?>> aClass, Function<CollectionsInterfaceRecord, Set<String>> setFunction) {
        //given
        var alfa = "Alfa";
        var bravo = "Bravo";
        var setOfBravo = Set.of(bravo);
        var setOfAlfa = Set.of(alfa);

        var aSet = new HashSet<String>();
        aSet.add(alfa);

        var aSortedSet = new TreeSet<String>(reverseOrder());
        aSortedSet.add(alfa);

        var aNavigableSet = new TreeSet<String>(naturalOrder());
        aNavigableSet.add(alfa);

        var aLinkedHashSet = new LinkedHashSet<String>();
        aLinkedHashSet.add(alfa);

        var anImmutableSet = ImmutableSet.of(alfa);

        var anImmutableSortedSet = ImmutableSortedSet.of(alfa);

        //when
        var record = new CollectionsInterfaceRecord("dummy", aSet, aSortedSet, aNavigableSet, aLinkedHashSet, anImmutableSet, anImmutableSortedSet);

        var immutableSet = setFunction.apply(record);
        var immutableIterator = immutableSet.iterator();

        //then
        assertAll(
                () -> assertThat(immutableSet).isInstanceOf(aClass),
                () -> assertThat(immutableSet).containsOnly(alfa),

                () -> assertThrows(UnsupportedOperationException.class, () -> immutableSet.add(bravo)),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableSet.addAll(setOfBravo)),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableSet.clear()),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableSet.remove(alfa)),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableSet.removeAll(setOfAlfa)),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableSet.removeIf(text -> text.startsWith("A"))),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableSet.retainAll(setOfBravo)),
                () -> assertThrows(UnsupportedOperationException.class, () -> immutableIterator.remove()),

                () -> assertDoesNotThrow(() -> aSet.add(bravo)),
                () -> assertDoesNotThrow(() -> aSortedSet.add(bravo)),
                () -> assertDoesNotThrow(() -> aNavigableSet.add(bravo)),
                () -> assertDoesNotThrow(() -> aLinkedHashSet.add(bravo)),
                () -> assertThrows(UnsupportedOperationException.class, () -> anImmutableSet.add(bravo)),
                () -> assertThrows(UnsupportedOperationException.class, () -> anImmutableSortedSet.add(bravo))
        );
    }
}
