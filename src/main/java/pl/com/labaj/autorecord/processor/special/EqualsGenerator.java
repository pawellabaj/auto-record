package pl.com.labaj.autorecord.processor.special;

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
import pl.com.labaj.autorecord.processor.MetaData;
import pl.com.labaj.autorecord.processor.utils.Logger;
import pl.com.labaj.autorecord.processor.utils.Method;
import pl.com.labaj.autorecord.processor.utils.StaticImports;

import javax.lang.model.element.ExecutableElement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.OBJECT;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PUBLIC;

class EqualsGenerator extends HashCodeEqualsGenerator.HashCodeEqualsSubGenerator {
    private static final String OTHER = "other";
    private static final String OTHER_RECORD = "otherRecord";

    EqualsGenerator(MetaData metaData, StaticImports staticImports, Logger logger, boolean memoizedHashCode, List<ExecutableElement> notIgnoredProperties) {
        super(metaData, staticImports, logger, memoizedHashCode, notIgnoredProperties);
    }

    @Override
    public void accept(TypeSpec.Builder recordSpecBuilder) {
        var recordName = metaData.recordName();
        var equalsMethodBuilder = MethodSpec.methodBuilder("equals")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(BOOLEAN)
                .addParameter(OBJECT, OTHER)
                .beginControlFlow("if (this == $L)", OTHER)
                .addStatement("return true")
                .endControlFlow()
                .beginControlFlow("if ($L == null)", OTHER)
                .addStatement("return false")
                .endControlFlow()
                .beginControlFlow("if (!($L instanceof $L))", OTHER, recordName)
                .addStatement("return false")
                .endControlFlow();

        if (memoizedHashCode) {
            equalsMethodBuilder
                    .beginControlFlow("if (hashCode() != $L.hashCode())", OTHER)
                    .addStatement("return false")
                    .endControlFlow();
        }

        var format = notIgnoredProperties.stream()
                .map(method -> "$T.equals($N, %s.$N)".formatted(OTHER_RECORD))
                .collect(joining("\n&& ", "return ", ""));
        var arguments = notIgnoredProperties.stream()
                .flatMap(this::getArguments)
                .toArray();
        var equalsMethod = equalsMethodBuilder
                .addCode("\n")
                .addStatement("var $L = ($L) $L", OTHER_RECORD, recordName, OTHER)
                .addStatement(format, arguments)
                .build();
        recordSpecBuilder.addMethod(equalsMethod);
    }

    private Stream<?> getArguments(ExecutableElement method) {
        var methodName = method.getSimpleName();
        var returnsArray = new Method(method).returnsArray();

        return returnsArray ? Stream.of(Arrays.class, methodName, methodName) : Stream.of(Objects.class, methodName, methodName);
    }
}
