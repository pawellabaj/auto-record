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
import pl.com.labaj.autorecord.processor.GeneratorMetaData;
import pl.com.labaj.autorecord.processor.StaticImport;
import pl.com.labaj.autorecord.processor.SubGenerator;
import pl.com.labaj.autorecord.processor.utils.Logger;
import pl.com.labaj.autorecord.processor.utils.Method;

import javax.lang.model.element.ExecutableElement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.special.SpecialMethod.TO_STRING;

public class ToStringGenerator extends SubGenerator {
    public ToStringGenerator(GeneratorMetaData metaData) {
        super(metaData);
    }

    @Override
    public void generate(TypeSpec.Builder recordSpecBuilder, List<StaticImport> staticImports, Logger logger) {
        var memoizedToString = metaData.memoization().specialMemoized().get(TO_STRING);
        var propertyMethods = metaData.propertyMethods();

        if (shouldNotGenerateToString(memoizedToString, propertyMethods)) {
            return;
        }

        var methodName = (memoizedToString ? "_" : "") + "toString";
        var toStringFormat = propertyMethods.stream()
                .map(Method::new)
                .map(this::getFormat)
                .collect(joining(" + \", \" +\n", "return \"" + metaData.recordName() + "[\" +\n", " +\n\"]\""));
        var arguments = propertyMethods.stream()
                .map(Method::new)
                .flatMap(this::getArguments)
                .toArray();
        var toStringMethod = MethodSpec.methodBuilder(methodName)
                .addModifiers(memoizedToString ? PRIVATE : PUBLIC)
                .returns(String.class)
                .addStatement(toStringFormat, arguments)
                .build();

        recordSpecBuilder.addMethod(toStringMethod);
    }

    private boolean shouldNotGenerateToString(boolean memoizedToString, List<ExecutableElement> propertyMethods) {
        if (memoizedToString) {
            return false;
        }

        var hasArrayComponents = propertyMethods.stream()
                .map(Method::new)
                .anyMatch(Method::returnsArray);

        return !hasArrayComponents;
    }

    private String getFormat(Method method) {
        return method.returnsArray() ? "\"$N = \" + $T.toString($N)" : "\"$N = \" + $N";
    }

    private Stream<?> getArguments(Method method) {

        var methodName = method.methodeName();
        var returnsArray = method.returnsArray();
        return returnsArray ? Stream.of(methodName, Arrays.class, methodName) : Stream.of(methodName, methodName);
    }
}
