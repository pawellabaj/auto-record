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

import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.google.testing.compile.Compiler.javac;
import static org.junit.jupiter.api.Assertions.assertAll;
import static pl.com.labaj.autorecord.test.TestUtils.assertThat;
import static pl.com.labaj.autorecord.test.TestUtils.expectedGenearatedRecordName;
import static pl.com.labaj.autorecord.test.TestUtils.expectedResourceName;
import static pl.com.labaj.autorecord.test.TestUtils.inputResourceName;

class GenerationTest {

    private final Compiler compiler = javac().withProcessors(new AutoRecordProcessor());

    @ParameterizedTest(name = "{0}.java")
    @ValueSource(strings = {
            "Basic",
            "FromTemplate",
            "Generics",
            "ArrayComponent",
            "ArrayComponentMemoization",
            "NonnullField",
            "IgnoredFields",
            "DefaultMethodMemoized",
            "HashCodeMemoized",
            "HashCodeMemoizedByAnnotation",
            "ToStringMemoized",
            "ToStringMemoizedByAnnotation",
            "HashCodeIgnoredFieldMemoized"
    })
    void shouldGenerateRecord(String interfaceName) {
        //given
        var inputInterface = JavaFileObjects.forResource(inputResourceName(interfaceName));
        var expectedRecord = JavaFileObjects.forResource(expectedResourceName(interfaceName));

        //when
        var compilation = compiler.compile(inputInterface);

        //then
        assertAll(
                () -> assertThat(compilation).succeeded(),
                () -> assertThat(compilation).generatedSourceFile(expectedGenearatedRecordName(interfaceName)).hasSourceEquivalentTo(expectedRecord)
        );
    }

    @Test
    void shouldGenerateRecordWithNoPackage() {
        //given
        var inputInterface = JavaFileObjects.forResource(inputResourceName("NoPackage"));
        var expectedRecord = JavaFileObjects.forResource(expectedResourceName("NoPackage"));

        //when
        var compilation = compiler.compile(inputInterface);

        //then
        assertAll(
                () -> assertThat(compilation).succeeded(),
                () -> assertThat(compilation).generatedSourceFile("NoPackageRecord").hasSourceEquivalentTo(expectedRecord)
        );
    }

    @Test
    void shouldGenerateRecordForNestedInterface() {
        //given
        var inputInterface = JavaFileObjects.forResource(inputResourceName("NestedInterface"));
        var expectedRecord = JavaFileObjects.forResource(expectedResourceName("NestedInterface_Intermediate_Internal"));

        //when
        var compilation = compiler.compile(inputInterface);

        //then
        assertAll(
                () -> assertThat(compilation).succeeded(),
                () -> assertThat(compilation).generatedSourceFile(expectedGenearatedRecordName("NestedInterface_Intermediate_Internal"))
                        .hasSourceEquivalentTo(expectedRecord)
        );
    }
}
