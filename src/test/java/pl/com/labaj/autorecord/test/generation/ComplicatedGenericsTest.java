package pl.com.labaj.autorecord.test.generation;

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

import java.lang.reflect.RecordComponent;
import java.util.HashSet;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ComplicatedGenericsTest {
    @TestFor(ComplicatedGenerics.class)
    @SuppressWarnings("rawtypes")
    void shouldGenerateRecord() {
        //given
        Class<?> recordClass = assertDoesNotThrow(() -> Class.forName("pl.com.labaj.autorecord.test.generation.ComplicatedGenericsRecord"));

        //when
        var recordComponents = recordClass.getRecordComponents();

        //then
        assertAll(
                () -> assertThat(recordClass.isRecord()).isTrue(),
                () -> assertThat(recordClass).isAssignableTo(ComplicatedGenerics.class),
                () -> assertThat(recordComponents)
                        .extracting(RecordComponent::getName).containsExactly("one", "two", "three"),
                () -> assertThat(recordComponents)
                        .extracting(recordComponent -> (Class) recordComponent.getType()).containsExactly(Object.class, Function.class, HashSet.class),
                () -> assertThat(recordComponents)
                        .extracting(this::typeParametersLength).containsExactly(0, 2, 1)
        );
    }

    private int typeParametersLength(RecordComponent recordComponent1) {
        return recordComponent1.getType().getTypeParameters().length;
    }
}
