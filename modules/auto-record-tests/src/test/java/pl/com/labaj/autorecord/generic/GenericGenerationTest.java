package pl.com.labaj.autorecord.generic;

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

import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;
import pl.com.labaj.autorecord.processor.AutoRecordProcessor;

import java.util.List;

import static com.google.testing.compile.Compiler.javac;
import static org.junit.jupiter.api.Assertions.assertAll;
import static pl.com.labaj.autorecord.test.TestUtils.assertThat;
import static pl.com.labaj.autorecord.test.TestUtils.expectedGenearatedRecordName;
import static pl.com.labaj.autorecord.test.TestUtils.expectedResourceName;
import static pl.com.labaj.autorecord.test.TestUtils.inputResourceName;

class GenericGenerationTest {

    private final Compiler compiler = javac().withProcessors(new AutoRecordProcessor());

    @Test
    void shouldGenerateRecordWithProperGenerics() {
        //given
        var inputInterfaces = List.of(
                JavaFileObjects.forResource(inputResourceName("BaseType")),
                JavaFileObjects.forResource(inputResourceName("ConcreteType")),
                JavaFileObjects.forResource(inputResourceName("WithGeneric")),
                JavaFileObjects.forResource(inputResourceName("WithSubGeneric")));
        var expectedRecord = JavaFileObjects.forResource(expectedResourceName("WithSubGeneric"));

        //when
        var compilation = compiler.compile(inputInterfaces);

        //then
        assertAll(
                () -> assertThat(compilation).succeeded(),
                () -> assertThat(compilation).generatedSourceFile(expectedGenearatedRecordName("WithSubGeneric")).hasSourceEquivalentTo(expectedRecord)
        );
    }
}