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
import pl.com.labaj.autorecord.extension.AutoRecordExtension;
import pl.com.labaj.autorecord.extension.CompactConstructorExtension;
import pl.com.labaj.autorecord.processor.context.Memoization;
import pl.com.labaj.autorecord.processor.context.MemoizerType;
import pl.com.labaj.autorecord.processor.context.ProcessorContext;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;
import static javax.lang.model.element.Modifier.PUBLIC;

class CompactConstructorSubGenerator {
    private static final String OBJECTS_REQUIRE_NON_NULL = "requireNonNull";
    private static final String OBJECTS_REQUIRE_NON_NULL_ELSE_GET = "requireNonNullElseGet";

    void generate(ProcessorContext context,
                  List<AutoRecordExtension> extensions,
                  TypeSpec.Builder recordBuilder,
                  StaticImports staticImports) {
        var nonNullNames = context.components().stream()
                .filter(RecordComponent::isNotPrimitive)
                .filter(rc -> rc.isNotAnnotatedWith(Nullable.class))
                .map(RecordComponent::name)
                .toList();

        var memoization = context.memoization();

        var compactConstructorExtensions = extensions.stream()
                .filter(CompactConstructorExtension.class::isInstance)
                .map(CompactConstructorExtension.class::cast)
                .toList();

        var generatedByProcessor = !nonNullNames.isEmpty() || !memoization.isEmpty();
        var shouldExtensionGenerate = compactConstructorExtensions.stream()
                .collect(toMap(
                        Object::hashCode,
                        extension -> extension.shouldGenerate(generatedByProcessor, context)
                ));
        var generatedByAtLeastOneExtension = shouldExtensionGenerate.values().stream()
                .reduce(false, (g1, g2) -> g1 || g2);

        if (!generatedByProcessor && !generatedByAtLeastOneExtension) {
            return;
        }

        var compactConstructor = generateCompactConstructor(context,
                compactConstructorExtensions,
                shouldExtensionGenerate,
                staticImports,
                memoization,
                nonNullNames);

        recordBuilder.compactConstructor(compactConstructor);
    }

    private MethodSpec generateCompactConstructor(ProcessorContext context,
                                                  List<CompactConstructorExtension> extensions,
                                                  Map<Integer, Boolean> shouldExtensionGenerate,
                                                  StaticImports staticImports,
                                                  Memoization memoization,
                                                  List<String> nonNullNames) {
        var compactConstructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(getMainModifiers(context));

        if (!nonNullNames.isEmpty()) {
            nonNullNames.forEach(name -> compactConstructorBuilder.addStatement("$1L($2N, \"$2N must not be null\")", OBJECTS_REQUIRE_NON_NULL, name));
            staticImports.add(Objects.class, OBJECTS_REQUIRE_NON_NULL);
        }

        if (!nonNullNames.isEmpty() && memoization.isPresent()) {
            compactConstructorBuilder.addCode("\n");
        }

        memoization.ifPresent(items -> {
            items.forEach(item -> {
                var memoizerType = MemoizerType.from(item.type());
                var newReference = memoizerType.getNewReference();
                compactConstructorBuilder.addStatement("$2N = $1L($2N, $3L)", OBJECTS_REQUIRE_NON_NULL_ELSE_GET, item.getMemoizerName(), newReference);
            });
            staticImports.add(Objects.class, OBJECTS_REQUIRE_NON_NULL_ELSE_GET);
        });

        return compactConstructorBuilder.build();
    }

    private Modifier[] getMainModifiers(ProcessorContext context) {
        return context.isRecordPublic() ? new Modifier[] {PUBLIC} : new Modifier[0];
    }
}
