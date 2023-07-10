package pl.com.labaj.autorecord.processor.generator;

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

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import pl.com.labaj.autorecord.processor.StaticImportsCollector;

import javax.lang.model.element.ExecutableElement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.squareup.javapoet.TypeName.INT;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.context.InternalMethod.HASH_CODE;
import static pl.com.labaj.autorecord.processor.utils.Methods.returnsArray;

class HashCodeSubGenerator {

    private static final String OBJECTS_HASH = "hash";

    void generate(StaticImportsCollector staticImports, TypeSpec.Builder recordBuilder, boolean isHashCodeMemoized, List<ExecutableElement> requiredProperties) {
        var methodName = (isHashCodeMemoized ? "_" : "") + HASH_CODE;
        var format = requiredProperties.stream()
                .map(this::methodStatementFormat)
                .collect(joining(", ", "return " + OBJECTS_HASH + "(", ")"));
        var arguments = requiredProperties.stream()
                .flatMap(this::methodStatementArguments)
                .toArray();

        var hashCodeMethodBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(isHashCodeMemoized ? PRIVATE : PUBLIC)
                .returns(INT)
                .addStatement(format, arguments);

        if (!isHashCodeMemoized) {
            hashCodeMethodBuilder.addAnnotation(Override.class);
        }

        staticImports.add(Objects.class, OBJECTS_HASH);

        var hashCodeMethod = hashCodeMethodBuilder.build();
        recordBuilder.addMethod(hashCodeMethod);
    }

    private String methodStatementFormat(ExecutableElement method) {
        return returnsArray(method) ? "$T.hashCode($N)" : "$N";
    }

    private Stream<Object> methodStatementArguments(ExecutableElement method) {
        return returnsArray(method) ? Stream.of(Arrays.class, method.getSimpleName()) : Stream.of(method.getSimpleName());
    }
}
