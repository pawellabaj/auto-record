package pl.com.labaj.autorecord.processor.memoization;

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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import pl.com.labaj.autorecord.test.Counters;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static pl.com.labaj.autorecord.test.TestUtils.assertThat;

class ArrayComponentMemoizationTest {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void shouldMemoizeMethodCalls() {
        //given
                var counters = new Counters();

        //when
        var record = new ArrayComponentMemoizationRecord(counters, new String[] {"A"});

        //then
        var assertions = IntStream.range(0, 5)
                .boxed()
                .flatMap(i -> {
                    record.hashCode();
                    record.toString();

                    return Stream.<Executable>of(
                            () -> assertThat(counters.hashCodeCount()).describedAs("hashCode #%s", i).isEqualTo(1),
                            () -> assertThat(counters.toStringCount()).describedAs("toString #%s", i).isEqualTo(1)
                    );
                })
                .toList();

        assertAll(assertions);
    }
}
