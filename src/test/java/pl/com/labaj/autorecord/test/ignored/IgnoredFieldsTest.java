package pl.com.labaj.autorecord.test.ignored;

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

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.com.labaj.autorecord.test.Counters;
import pl.com.labaj.autorecord.test.ParameterizedTestFor;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class IgnoredFieldsTest {

    public static Stream<Arguments> testData() {
        var aRecord = new IgnoredFieldsRecord("one", 2, "A", new Counters());
        var bRecord = new IgnoredFieldsRecord("one", 2, "B", new Counters());
        var cRecord = new IgnoredFieldsRecord("one", 3, "C", new Counters());

        return Stream.of(
                Arguments.of(aRecord, aRecord, true),
                Arguments.of(aRecord, aRecord, true),
                Arguments.of(aRecord, "Not a record", false),
                Arguments.of(aRecord, bRecord, true),
                Arguments.of(aRecord, cRecord, false)
        );
    }

    @ParameterizedTestFor(IgnoredFields.class)
    @MethodSource("testData")
    void shouldIgnoreProperty(IgnoredFieldsRecord firstRecord, Object secondObject, boolean expectedResult) {
        //when
        var result = assertDoesNotThrow(() -> firstRecord.equals(secondObject));

        //then
        assertAll(
                () -> assertDoesNotThrow(firstRecord::hashCode),
                () -> assertThat(result).isEqualTo(expectedResult),
                () -> assertThat(firstRecord.four().hashCodeCount()).isZero(),
                () -> assertThat(firstRecord.four().equalsCount()).isZero()
        );
    }
}
