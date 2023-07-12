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
import pl.com.labaj.autorecord.processor.context.ProcessorContext;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.OBJECT;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PUBLIC;

class EqualsSubGenerator {
    private static final String OTHER = "other";
    private static final String OTHER_RECORD = "otherRecord";
    private static final String RETURN_TRUE_STATEMENT = "return true";
    private static final String RETURN_FALSE_STATEMENT = "return false";

    void generate(ProcessorContext context, TypeSpec.Builder recordBuilder, boolean memoizedHashCode, List<RecordComponent> components) {
        var recordName = context.recordName();

        var equalsMethodBuilder = MethodSpec.methodBuilder("equals")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(BOOLEAN)
                .addParameter(OBJECT, OTHER)
                .beginControlFlow("if (this == $L)", OTHER)
                .addStatement(RETURN_TRUE_STATEMENT)
                .endControlFlow()
                .beginControlFlow("if ($L == null)", OTHER)
                .addStatement(RETURN_FALSE_STATEMENT)
                .endControlFlow()
                .beginControlFlow("if (!($L instanceof $L))", OTHER, recordName)
                .addStatement(RETURN_FALSE_STATEMENT)
                .endControlFlow();

        if (memoizedHashCode) {
            equalsMethodBuilder
                    .beginControlFlow("if (hashCode() != $L.hashCode())", OTHER)
                    .addStatement(RETURN_FALSE_STATEMENT)
                    .endControlFlow();
        }

        equalsMethodBuilder
                .addCode("\n")
                .addStatement("var $L = ($L) $L", OTHER_RECORD, recordName, OTHER);

        var format = IntStream.range(0, components.size())
                .mapToObj(this::methodStatementFormat)
                .collect(joining("\n&& ", "return ", ""));
        var arguments = components.stream()
                .flatMap(this::methodStatementArguments)
                .toArray();
        var equalsMethod = equalsMethodBuilder
                .addStatement(format, arguments)
                .build();

        recordBuilder.addMethod(equalsMethod);
    }

    private String methodStatementFormat(int index) {
        var classType = "$" + (index * 2 + 1) + "T";
        var name = "$" + (index * 2 + 2) + "N";

        return classType + ".equals(" + name + ", " + OTHER_RECORD + "." + name + ")";
    }

    private Stream<Object> methodStatementArguments(RecordComponent recordComponent) {
        var classToCall = recordComponent.isArray() ? Arrays.class : Objects.class;
        return Stream.of(classToCall, recordComponent.name());
    }
}
