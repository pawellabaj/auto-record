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

import pl.com.labaj.autorecord.memoizer.Memoizer;
import pl.com.labaj.autorecord.test.TestFor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WithBuilderMemoizedMethodTest {

    @TestFor(WithBuilderMemoizedMethod.class)
    void shouldGenerateRecordFromInlinedBuilder() {
        //given
        var recordFromBuilder = WithBuilderMemoizedMethodRecord.builder()
                .build();

        //then
        assertDoesNotThrow(recordFromBuilder::aMethod);
    }

    @TestFor(WithBuilderMemoizedMethod.class)
    void shouldThrowNPEWhenForgotToInitializeMemoizer() {
        //given then
        assertThrows(NullPointerException.class, () -> WithBuilderMemoizedMethodRecordBuilder.builder().build());
    }

    @TestFor(WithBuilderMemoizedMethod.class)
    void shouldGenerateRecordFromBuilder() {
        //given
        var recordFromBuilder = WithBuilderMemoizedMethodRecordBuilder.builder()
                .aMethodMemoizer(new Memoizer<>())
                .build();

        //then
        assertDoesNotThrow(recordFromBuilder::aMethod);
    }

    @TestFor(WithBuilderMemoizedMethod.class)
    void shouldGenerateRecordFromToBuilder() {
        //given
        var recordFromBuilderParent = new WithBuilderMemoizedMethodRecord();
        var recordFromBuilder = recordFromBuilderParent.toBuilder().build();

        //then
        assertDoesNotThrow(recordFromBuilder::aMethod);
    }
}
