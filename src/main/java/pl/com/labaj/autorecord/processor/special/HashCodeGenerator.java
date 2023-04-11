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

import static com.squareup.javapoet.TypeName.INT;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

class HashCodeGenerator extends HashCodeEqualsGenerator.HashCodeEqualsSubGenerator {
    private static final String OBJECTS_HASH = "hash";

    HashCodeGenerator(MetaData metaData, StaticImports staticImports, Logger logger, boolean memoizedHashCode, List<ExecutableElement> notIgnoredProperties) {
        super(metaData, staticImports, logger, memoizedHashCode, notIgnoredProperties);
    }

    @Override
    public void accept(TypeSpec.Builder recordSpecBuilder) {
        var methodName = (memoizedHashCode ? "_" : "") + "hashCode";
        var format = notIgnoredProperties.stream()
                .map(Method::new)
                .map(method -> method.returnsArray() ? "$T.hashCode($N)" : "$N")
                .collect(joining(", ", "return " + OBJECTS_HASH + "(", ")"));
        var arguments = notIgnoredProperties.stream()
                .flatMap(this::getArguments)
                .toArray();
        var hashCodeMethodBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(memoizedHashCode ? PRIVATE : PUBLIC)
                .returns(INT)
                .addStatement(format, arguments);

        if (!memoizedHashCode) {
            hashCodeMethodBuilder.addAnnotation(Override.class);
        }

        staticImports.add(Objects.class, OBJECTS_HASH);

        recordSpecBuilder.addMethod(hashCodeMethodBuilder.build());
    }

    private Stream<?> getArguments(ExecutableElement method) {
        var returnsArray = new Method(method).returnsArray();
        return returnsArray ? Stream.of(Arrays.class, method.getSimpleName()) : Stream.of(method.getSimpleName());
    }
}
