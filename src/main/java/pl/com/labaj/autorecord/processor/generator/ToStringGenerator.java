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
import pl.com.labaj.autorecord.processor.context.GenerationContext;
import pl.com.labaj.autorecord.processor.utils.Methods;

import javax.lang.model.element.ExecutableElement;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.context.InternalMethod.TO_STRING;
import static pl.com.labaj.autorecord.processor.utils.Methods.returnsArray;

public class ToStringGenerator implements RecordGenerator {

    @Override
    public void generate(GenerationContext context, StaticImportsCollector staticImports, TypeSpec.Builder recordBuilder) {
        var isToStringMemoized = context.recordOptions().memoizedToString() || context.memoization().isMemoized(TO_STRING);

        if (!shouldGenerate(context, isToStringMemoized)) {
            return;
        }

        var propertyMethods = context.propertyMethods();
        var recordName = context.recordName();
        var methodName = (isToStringMemoized ? "_" : "") + TO_STRING;
        var counter = new AtomicInteger(0);
        var format = propertyMethods.stream()
                .map(method -> methodStatementFormat(method, counter))
                .collect(joining(" + \", \" +\n", "return \"" + recordName + "[\" +\n", " +\n\"]\""));
        var arguments = propertyMethods.stream()
                .flatMap(this::methodStatementArguments)
                .toArray();

        var toStringMethod = MethodSpec.methodBuilder(methodName)
                .addModifiers(isToStringMemoized ? PRIVATE : PUBLIC)
                .returns(String.class)
                .addStatement(format, arguments)
                .build();

        recordBuilder.addMethod(toStringMethod);
    }

    private boolean shouldGenerate(GenerationContext context, boolean isToStringMemoized) {
        if (context.propertyMethods().stream()
                .anyMatch(Methods::returnsArray)) {
            return true;
        }

        return isToStringMemoized;
    }

    private String methodStatementFormat(ExecutableElement method, AtomicInteger counter) {
        var count = counter.getAndAdd(returnsArray(method) ? 2 : 1);

        var name = "$" + (count + 1) + "N";
        var classType = "$" + (count + 2) + "T";

        return returnsArray(method) ? "\"" + name + " = \" + " + classType + ".toString(" + name + ")" : "\"" + name + " = \" + " + name;
    }

    private Stream<Object> methodStatementArguments(ExecutableElement method) {
        return returnsArray(method) ? Stream.of(method.getSimpleName(), Arrays.class) : Stream.of(method.getSimpleName());
    }
}
