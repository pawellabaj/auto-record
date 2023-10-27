package pl.com.labaj.autorecord.extension.arice;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pl.com.labaj.autorecord.processor.AutoRecordProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.JavaFileObjects.forResource;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertAll;

class ImmutableCollectionsExtensionTest {
    private static final String[] NAMES = {
            "ItemWithObject",
            "ItemWithLists",
            "ItemWithListsCustomTypes",
            "ItemWithMutableLists",
            "ItemWithSets",
            "ItemWithSetsCustomTypes",
            "ItemWithMutableSets",
            "ItemWithMaps",
            "ItemWithMapsCustomTypes",
            "ItemWithQueues",
            "ItemWithQueuesCustomTypes",
            "ItemWithMutableQueues",
            "ItemWithGuavaCollections"
    };
    private static final String GENERATED_PATH = "pl/com/labaj/autorecord/testcase/";

    static Stream<String> names() {
        return Arrays.stream(NAMES);
    }

    private Compiler compiler;

    @BeforeEach
    void setUp() {
        compiler = javac()
                .withOptions("-proc:only", "-Xprefer:source");
    }

    @ParameterizedTest(name = "{0}.java")
    @MethodSource("names")
    void shouldGenerateSingleRecord(String name) {
        //given
        var inputs = List.of(
                forResource("in/" + name + ".java"),
                forResource("in/UserCollections.java")
        );

        var expectedOutput = forResource("out/" + name + "Record.java");

        //when
        var compilation = compiler
                .withProcessors(new AutoRecordProcessor(), new ARICEUtilitiesProcessor())
                .compile(inputs);

        compilation.generatedSourceFile("pl.com.labaj.autorecord.extension.arice.ARICE")
                .ifPresent(fileObject -> {
                    System.out.println(fileObject);
                    try {
                        System.out.println(fileObject.getCharContent(true));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        //then
        assertAll(
                () -> assertThat(compilation).generatedSourceFile(GENERATED_PATH + name + "Record")
                        .hasSourceEquivalentTo(expectedOutput),
                () -> assertThat(compilation).succeeded()
        );
    }

    @Test
    void shouldGenerateCorrectAllRecordsTogether() {
        //given
        var inputs = Arrays.stream(NAMES)
                .map(name -> forResource("in/" + name + ".java"))
                .collect(toCollection(ArrayList::new));

        inputs.add(forResource("in/UserCollections.java"));

        var expectedOutputs = Arrays.stream(NAMES)
                .collect(toMap(
                        identity(),
                        name -> forResource("out/" + name + "Record.java")
                ));
        var expectedArice = forResource("out/ARICE.java");

        //when
        var compilation = compiler
                .withProcessors(new AutoRecordProcessor(), new ARICEUtilitiesProcessor())
                .compile(inputs);

        //then
        var assertions = Arrays.stream(NAMES)
                .map(name -> (Executable) () -> assertThat(compilation).generatedSourceFile(GENERATED_PATH + name + "Record")
                        .hasSourceEquivalentTo(expectedOutputs.get(name)))
                .collect(toCollection(ArrayList::new));

        assertions.add(() -> assertThat(compilation).succeeded());
        assertions.add(() -> assertThat(compilation).generatedSourceFile("pl/com/labaj/autorecord/extension/arice/ARICE")
                .hasSourceEquivalentTo(expectedArice));

        assertAll(assertions);
    }
}
