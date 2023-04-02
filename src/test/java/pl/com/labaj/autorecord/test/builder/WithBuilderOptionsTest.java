package pl.com.labaj.autorecord.test.builder;

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

import pl.com.labaj.autorecord.test.TestFor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class WithBuilderOptionsTest {

    @TestFor(WithBuilderOptions.class)
    void shouldGenerateBuilder() {
        //given
        var recordFromBuilder1 = WithBuilderOptionsRecord_Builder.create()
                .one("one")
                .two(2)
                .buildRecord();
        var recordFromBuilder2 = WithBuilderOptionsRecord.builder()
                .one("one")
                .two(2)
                .buildRecord();
        var recordFromConstructor = new WithBuilderOptionsRecord("one", 2);

        //then
        assertAll(
                () -> assertThat(recordFromBuilder1).isEqualTo(recordFromConstructor),
                () -> assertThat(recordFromBuilder2).isEqualTo(recordFromConstructor)
        );
    }
}
