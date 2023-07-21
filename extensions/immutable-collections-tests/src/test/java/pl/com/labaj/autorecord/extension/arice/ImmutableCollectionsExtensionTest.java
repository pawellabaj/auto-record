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

import com.google.common.collect.ImmutableSet;
import com.google.testing.compile.Compiler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.processor.AutoRecordProcessor;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.JavaFileObjects.forResource;
import static java.util.Objects.isNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;

class ImmutableCollectionsExtensionTest {
    private static final String[] NAMES = {
            "ItemWithObject",
            "ItemWithListsNoUserDefinedCollections",
            "ItemWithLists",
            "ItemWithSetsNoUserDefinedCollections",
            "ItemWithSets",
            "ItemWithMaps",
            "ItemWithQueues",
    };
    private static final String GENERATED_PATH = "pl/com/labaj/autorecord/extension/arice/";

    static Stream<String> names() {
        return Arrays.stream(NAMES);
    }

    private Compiler compiler;

    @BeforeEach
    void setUp() {
        compiler = javac().withClasspath(prepareClasspath()).withOptions("-proc:only");
    }

    @ParameterizedTest(name = "{0}.java")
    @MethodSource("names")
    void shouldGenerateSingleRecord(String name) {
        //given
        var inputs = List.of(
                forResource("in/" + name + ".java"),
                forResource("UserCollections.java")
        );

        var expectedOutput = forResource("out/" + name + "Record.java");

        //when
        var compilation = compiler
                .withProcessors(new AutoRecordProcessor(), new AutoRecordImmutableCollectionsProcessor())
                .compile(inputs);

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

        inputs.add(forResource("UserCollections.java"));

        var expectedOutputs = Arrays.stream(NAMES)
                .collect(toMap(
                        identity(),
                        name -> forResource("out/" + name + "Record.java")
                ));

        //when
        var compilation = compiler
                .withProcessors(new AutoRecordProcessor(), new AutoRecordImmutableCollectionsProcessor())
                .compile(inputs);

        //then
        var assertions = Arrays.stream(NAMES)
                .map(name -> (Executable) () -> assertThat(compilation).generatedSourceFile(GENERATED_PATH + name + "Record")
                        .hasSourceEquivalentTo(expectedOutputs.get(name)))
                .collect(toCollection(ArrayList::new));

        assertions.add(() -> assertThat(compilation).succeeded());

        assertAll(assertions);
    }

    @SuppressWarnings("DataFlowIssue")
    private static List<File> prepareClasspath() {
        var autoRecordJar = findClasspathFile(AutoRecord.class);
        var guavaJar = findClasspathFile(ImmutableSet.class);
        var nullableJar = findClasspathFile(Nullable.class);
        var targetClassesFolder = findClasspathFile(ImmutableCollectionsExtension.class);

        return List.of(
                autoRecordJar,
                guavaJar,
                nullableJar,
                targetClassesFolder
        );
    }

    private static File findClasspathFile(Class<?> aClass) {
        var url = aClass.getResource(aClass.getSimpleName() + ".class");
        var fileInFolderWithoutExtension = aClass.getName().replace('.', '/');

        if (isNull(url)) {
            fail("Cannot get URL for " + aClass.getName());
            return null;
        }
        var path = url.getPath();
        path = removeStart(path, "jar:");
        path = removeStart(path, "file:");
        path = substringBefore(path, fileInFolderWithoutExtension);
        path = substringBefore(path, "!");

        return new File(path);
    }
}
