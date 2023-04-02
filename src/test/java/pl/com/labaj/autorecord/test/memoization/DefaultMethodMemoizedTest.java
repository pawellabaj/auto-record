package pl.com.labaj.autorecord.test.memoization;

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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import pl.com.labaj.autorecord.test.Counters;
import pl.com.labaj.autorecord.test.TestFor;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;

class DefaultMethodMemoizedTest {

    @TestFor(DefaultMethodMemoized.class)
    void shouldMemoizeToString() {
        //given
        var one = new Counters();
        var record = new DefaultMethodMemoizedRecord(one);

        //then
        var assertions = IntStream.range(0, 5)
                .peek(i -> record.aMethod())
                .map(i -> one.toStringCount())
                .mapToObj(count -> (Executable) () -> Assertions.assertThat(count).isEqualTo(1));

        //then
        assertAll(assertions);
    }
}
