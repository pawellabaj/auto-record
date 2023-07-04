package pl.com.labaj.autorecord.processor;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class WithBuilderTest {

    @Test
    void shouldGenerateBuilder() {
        //given
        var recordFromBuilder1 = WithBuilderRecordBuilder.builder()
                .one("one")
                .two(2)
                .build();
        var recordFromBuilder2 = WithBuilderRecord.builder()
                .one("one")
                .two(2)
                .build();
        var recordFromBuilder3 = recordFromBuilder1.toBuilder()
                .build();
        var recordFromConstructor = new WithBuilderRecord("one", 2);

        //then
        assertAll(
                () -> assertNotSame(recordFromBuilder1, recordFromConstructor),
                () -> assertThat(recordFromBuilder1).isEqualTo(recordFromConstructor),
                () -> assertNotSame(recordFromBuilder2, recordFromConstructor),
                () -> assertThat(recordFromBuilder2).isEqualTo(recordFromConstructor),
                () -> assertNotSame(recordFromBuilder3, recordFromConstructor),
                () -> assertThat(recordFromBuilder3).isEqualTo(recordFromConstructor)
        );
    }
}
