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

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import pl.com.labaj.autorecord.Ignored;
import pl.com.labaj.autorecord.processor.utils.Method;
import pl.com.labaj.autorecord.processor.memoization.Memoization;

import javax.lang.model.element.ExecutableElement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.INT;
import static com.squareup.javapoet.TypeName.OBJECT;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.utils.SpecialMethod.HASH_CODE;

class HashCodeEqualsGenerator {

    private static final String OBJECTS_HASH = "hash";
    private static final String OTHER = "other";
    private static final String OTHER_RECORD = "otherRecord";
    private final GeneratorMetaData parameters;
    private final TypeSpec.Builder recordSpecBuilder;
    private final Memoization memoization;

    HashCodeEqualsGenerator(GeneratorMetaData parameters, TypeSpec.Builder recordSpecBuilder, Memoization memoization) {
        this.parameters = parameters;
        this.recordSpecBuilder = recordSpecBuilder;
        this.memoization = memoization;
    }

    WithNotIgnoredProperties findNotIgnoredProperties() {
        var notIgnoredProperties = parameters.propertyMethods().stream()
                .map(Method::new)
                .filter(method -> method.isNotAnnotatedWith(Ignored.class))
                .map(Method::method)
                .toList();
        return new WithNotIgnoredProperties(notIgnoredProperties);
    }

    final class WithNotIgnoredProperties {
        private final List<ExecutableElement> notIgnoredProperties;
        private final boolean memoizedHashCode;
        private final boolean hasArrayComponents;
        private final boolean hasIgnoredComponents;

        private WithNotIgnoredProperties(List<ExecutableElement> notIgnoredProperties) {
            this.notIgnoredProperties = notIgnoredProperties;

            memoizedHashCode = memoization.specialMemoized().get(HASH_CODE);
            hasIgnoredComponents = notIgnoredProperties.size() < parameters.propertyMethods().size();
            hasArrayComponents = notIgnoredProperties.stream()
                    .map(Method::new)
                    .anyMatch(Method::returnsArray);
        }

        WithNotIgnoredProperties createHashCodeMethod() {
            if (!memoizedHashCode && !hasIgnoredComponents && !hasArrayComponents) {
                return this;
            }

            var methodName = (memoizedHashCode ? "_" : "") + "hashCode";
            var hashCodeFormat = notIgnoredProperties.stream()
                    .map(Method::new)
                    .map(method -> method.returnsArray() ? "$T.hashCode($N)" : "$N")
                    .collect(joining(", ", "return " + OBJECTS_HASH + "(", ")"));
            var arguments = notIgnoredProperties.stream()
                    .flatMap(this::getHashCodeArguments)
                    .toArray();
            var hashCodeMethodBuilder = MethodSpec.methodBuilder(methodName)
                    .addModifiers(memoizedHashCode ? PRIVATE : PUBLIC)
                    .returns(INT)
                    .addStatement(hashCodeFormat, arguments);

            if (!memoizedHashCode) {
                hashCodeMethodBuilder.addAnnotation(Override.class);
            }

            parameters.staticImports().add(new StaticImport(Objects.class, OBJECTS_HASH));

            recordSpecBuilder.addMethod(hashCodeMethodBuilder.build());

            return this;
        }

        private Stream<?> getHashCodeArguments(ExecutableElement method) {
            var returnsArray = new Method(method).returnsArray();
            return returnsArray ? Stream.of(Arrays.class, method.getSimpleName()) : Stream.of(method.getSimpleName());
        }

        @SuppressWarnings({"UnusedReturnValue", "java:S1192"})
        WithNotIgnoredProperties createEqualsMethod() {
            if (!memoizedHashCode && !hasIgnoredComponents && !hasArrayComponents) {
                return this;
            }

            var recordName = parameters.recordName();
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

            var equalsFormat = notIgnoredProperties.stream()
                    .map(method -> "$T.equals($N, %s.$N)".formatted(OTHER_RECORD))
                    .collect(joining("\n&& ", "return ", ""));
            var arguments = notIgnoredProperties.stream()
                    .flatMap(this::getToStringArguments)
                    .toArray();
            var equalsMethod = equalsMethodBuilder
                    .addCode("\n")
                    .addStatement("var $L = ($L) $L", OTHER_RECORD, recordName, OTHER)
                    .addStatement(equalsFormat, arguments)
                    .build();
            recordSpecBuilder.addMethod(equalsMethod);

            return this;
        }

        private Stream<?> getToStringArguments(ExecutableElement method) {
            var methodName = method.getSimpleName();
            var returnsArray = new Method(method).returnsArray();

            return returnsArray ? Stream.of(Arrays.class, methodName, methodName) : Stream.of(Objects.class, methodName, methodName);
        }
    }
}
