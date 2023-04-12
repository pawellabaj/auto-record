package pl.com.labaj.autorecord.test.arrays;

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

import org.junit.jupiter.api.function.Executable;
import pl.com.labaj.autorecord.test.Counters;
import pl.com.labaj.autorecord.test.TestFor;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ArraysComponentMemoizedTest {

    @TestFor(ArrayComponentMemoized.class)
    void shouldGenerateEqualsAndToString() {
        //given
        var one = new Counters();
        var record1 = new ArrayComponentMemoizedRecord(one, new String[] {"A", "B", "C"});
        var record2 = new ArrayComponentMemoizedRecord(new Counters(), new String[] {"A", "B", "C"});

        var assertions = new ArrayList<Executable>();
        IntStream.range(0, 5)
                .peek(i -> record1.toString())
                .map(i -> one.toStringCount())
                .forEach(count -> assertions.add(() -> assertThat(count).isEqualTo(1)));

        assertions.add(() -> assertThat(record1).isEqualTo(record2));
        assertions.add(() -> assertThat(record1.toString()).contains("one", "two", "A", "B", "C"));

        //then
        assertAll(assertions);
    }
}
