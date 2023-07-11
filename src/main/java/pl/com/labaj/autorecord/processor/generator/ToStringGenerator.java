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
import pl.com.labaj.autorecord.context.RecordComponent;
import pl.com.labaj.autorecord.context.StaticImports;
import pl.com.labaj.autorecord.processor.context.InternalContext;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.context.InternalMethod.TO_STRING;

public class ToStringGenerator implements RecordGenerator {

    @Override
    public void generate(InternalContext context, StaticImports staticImports, TypeSpec.Builder recordBuilder) {
        var isToStringMemoized = context.recordOptions().memoizedToString() || context.memoization().isMemoized(TO_STRING);

        if (!shouldGenerate(context, isToStringMemoized)) {
            return;
        }

        var components = context.components();
        var recordName = context.recordName();
        var methodName = (isToStringMemoized ? "_" : "") + TO_STRING;
        var counter = new AtomicInteger(0);
        var format = components.stream()
                .map(recordComponent -> methodStatementFormat(recordComponent, counter))
                .collect(joining(" + \", \" +\n", "return \"" + recordName + "[\" +\n", " +\n\"]\""));
        var arguments = components.stream()
                .flatMap(this::methodStatementArguments)
                .toArray();

        var toStringMethod = MethodSpec.methodBuilder(methodName)
                .addModifiers(isToStringMemoized ? PRIVATE : PUBLIC)
                .returns(String.class)
                .addStatement(format, arguments)
                .build();

        recordBuilder.addMethod(toStringMethod);
    }

    private boolean shouldGenerate(InternalContext context, boolean isToStringMemoized) {
        var atLeastOneArray = context.components().stream()
                .anyMatch(RecordComponent::isArray);

        if (atLeastOneArray) {
            return true;
        }

        return isToStringMemoized;
    }

    private String methodStatementFormat(RecordComponent recordComponent, AtomicInteger counter) {
        var isArray = recordComponent.isArray();
        var count = counter.getAndAdd(isArray ? 2 : 1);

        var name = "$" + (count + 1) + "N";
        var classType = "$" + (count + 2) + "T";

        return isArray ? "\"" + name + " = \" + " + classType + ".toString(" + name + ")" : "\"" + name + " = \" + " + name;
    }

    private Stream<Object> methodStatementArguments(RecordComponent recordComponent) {
        return recordComponent.isArray() ? Stream.of(recordComponent.name(), Arrays.class) : Stream.of(recordComponent.name());
    }
}
