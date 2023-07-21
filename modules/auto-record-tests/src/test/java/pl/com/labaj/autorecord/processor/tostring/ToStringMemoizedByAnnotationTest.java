package pl.com.labaj.autorecord.processor.tostring;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ToStringMemoizedByAnnotationTest {

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    void shouldMemoizeToString() {
        //given
        var three = new Counters();
        var record = new ToStringMemoizedByAnnotationRecord("one", 2, three);

        //then
        var assertions = IntStream.range(0, 5)
                .peek(i -> record.toString())
                .map(i -> three.toStringCount())
                .mapToObj(count -> (Executable) () -> assertThat(count).isEqualTo(1));

        //then
        assertAll(assertions);
    }
}
