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
import pl.com.labaj.autorecord.memoizer.Memoizer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WithBuilderMemoizedMethodTest {

    @Test
    void shouldGenerateRecordFromInlinedBuilder() {
        //given
        var recordFromBuilder = WithBuilderMemoizedMethodRecord.builder()
                .build();

        //then
        assertDoesNotThrow(recordFromBuilder::aMethod);
    }

    @Test
    void shouldThrowNPEWhenForgotToInitializeMemoizer() {
        //given
        var builder = WithBuilderMemoizedMethodRecordBuilder.builder();

        //then
        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    void shouldGenerateRecordFromBuilder() {
        //given
        var recordFromBuilder = WithBuilderMemoizedMethodRecordBuilder.builder()
                .aMethodMemoizer(new Memoizer<>())
                .build();

        //then
        assertDoesNotThrow(recordFromBuilder::aMethod);
    }

    @Test
    void shouldGenerateRecordFromToBuilder() {
        //given
        var recordFromBuilderParent = new WithBuilderMemoizedMethodRecord();
        var recordFromBuilder = recordFromBuilderParent.toBuilder().build();

        //then
        assertDoesNotThrow(recordFromBuilder::aMethod);
    }
}
