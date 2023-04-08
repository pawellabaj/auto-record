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
import pl.com.labaj.autorecord.processor.utils.Method;
import pl.com.labaj.autorecord.processor.memoization.Memoization;

import javax.lang.model.element.ExecutableElement;
import java.util.Arrays;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.utils.SpecialMethod.TO_STRING;

class ToStringGenerator {
    private final GeneratorMetaData parameters;
    private final TypeSpec.Builder recordSpecBuilder;
    private final Memoization memoization;

    ToStringGenerator(GeneratorMetaData parameters, TypeSpec.Builder recordSpecBuilder, Memoization memoization) {
        this.parameters = parameters;
        this.recordSpecBuilder = recordSpecBuilder;
        this.memoization = memoization;
    }

    @SuppressWarnings("UnusedReturnValue")
    ToStringGenerator createToStringMethod() {
        var memoizedToString = memoization.specialMemoized().get(TO_STRING);
        var propertyMethods = parameters.propertyMethods();
        var hasArrayComponents = propertyMethods.stream()
                .map(Method::new)
                .anyMatch(Method::returnsArray);

        if (!memoizedToString && !hasArrayComponents) {
            return this;
        }

        var methodName = (memoizedToString ? "_" : "") + "toString";
        var toStringFormat = propertyMethods.stream()
                .map(method -> new Method(method).returnsArray() ? "\"$N = \" + $T.toString($N)" : "\"$N = \" + $N")
                .collect(joining(" + \", \" +\n", "return \"" + parameters.recordName() + "[\" +\n", " +\n\"]\""));
        var arguments = propertyMethods.stream()
                .flatMap(this::getArguments)
                .toArray();
        var toStringMethod = MethodSpec.methodBuilder(methodName)
                .addModifiers(memoizedToString ? PRIVATE : PUBLIC)
                .returns(String.class)
                .addStatement(toStringFormat, arguments)
                .build();

        recordSpecBuilder.addMethod(toStringMethod);

        return this;
    }

    private Stream<?> getArguments(ExecutableElement method) {
        var methodName = method.getSimpleName();

        var returnsArray = new Method(method).returnsArray();
        return returnsArray ? Stream.of(methodName, Arrays.class, methodName) : Stream.of(methodName, methodName);
    }
}
