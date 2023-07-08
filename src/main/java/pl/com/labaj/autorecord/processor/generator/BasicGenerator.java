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

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.Memoized;
import pl.com.labaj.autorecord.processor.StaticImportsCollector;
import pl.com.labaj.autorecord.processor.context.GenerationContext;
import pl.com.labaj.autorecord.processor.context.Memoization;
import pl.com.labaj.autorecord.processor.context.MemoizerType;
import pl.com.labaj.autorecord.processor.utils.Methods;

import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.context.MemoizerType.from;
import static pl.com.labaj.autorecord.processor.utils.Annotations.createAnnotationSpecs;

class BasicGenerator implements RecordGenerator {
    private static final AnnotationSpec GENERATED_ANNOTATION = AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", AutoRecord.class.getName())
            .build();
    private static final AnnotationSpec GENERATED_WITH_AUTO_RECORD_ANNOTATION = AnnotationSpec.builder(GeneratedWithAutoRecord.class).build();
    private static final String OBJECTS_REQUIRE_NON_NULL = "requireNonNull";
    private static final String OBJECTS_REQUIRE_NON_NULL_ELSE_GET = "requireNonNullElseGet";

    @Override
    public void generate(GenerationContext context, StaticImportsCollector staticImports, TypeSpec.Builder recordBuilder) {
        recordBuilder
                .addAnnotation(GENERATED_ANNOTATION)
                .addAnnotation(GENERATED_WITH_AUTO_RECORD_ANNOTATION)
                .addModifiers(getMainModifiers(context))
                .addSuperinterface(context.superType());

        var recordComponentParameters = context.propertyMethods().stream()
                .map(this::toParameterSpec)
                .toList();

        generateTypeVariables(context, recordBuilder);
        generateComponents(context, recordBuilder, recordComponentParameters);
        createAdditionalConstructor(context, recordBuilder, recordComponentParameters);
        createCompactConstructor(context, recordBuilder, staticImports);
    }

    private void generateComponents(GenerationContext context, TypeSpec.Builder recordBuilder, List<ParameterSpec> recordComponentParameters) {
        recordBuilder.addRecordComponents(recordComponentParameters);

        context.memoization()
                .ifPresent(items -> items.stream()
                        .map(this::toParameterSpec)
                        .forEach(recordBuilder::addRecordComponent));
    }

    private void generateTypeVariables(GenerationContext context, TypeSpec.Builder recordBuilder) {
        context.generics()
                .ifPresent((variables, types) -> recordBuilder.addTypeVariables(variables));
    }

    private void createAdditionalConstructor(GenerationContext context, TypeSpec.Builder recordBuilder, List<ParameterSpec> recordComponentParameters) {
        context.memoization()
                .ifPresent(items -> {
                    var constructorCallStatement = Stream.concat(
                                    recordComponentParameters.stream()
                                            .map(spec -> spec.name),
                                    items.stream()
                                            .map(Memoization.Item::type)
                                            .map(MemoizerType::from)
                                            .map(MemoizerType::getConstructorStatement)
                            )
                            .collect(joining(", ", "this(", ")"));
                    var constructor = MethodSpec.constructorBuilder()
                            .addModifiers(getMainModifiers(context))
                            .addParameters(recordComponentParameters)
                            .addStatement(constructorCallStatement)
                            .build();

                    recordBuilder.addMethod(constructor);
                });
    }

    private void createCompactConstructor(GenerationContext context, TypeSpec.Builder recordBuilder, StaticImportsCollector staticImports) {
        var nonNullNames = context.propertyMethods().stream()
                .filter(Methods::doesNotReturnPrimitive)
                .filter(method -> Methods.isNotAnnotatedWith(method, Nullable.class))
                .map(ExecutableElement::getSimpleName)
                .toList();

        var memoization = context.memoization();

        if (nonNullNames.isEmpty() && memoization.isEmpty()) {
            return;
        }

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
                var memoizerType = from(item.type());
                var newReference = memoizerType.getNewReference();
                compactConstructorBuilder.addStatement("$2N = $1L($2N, $3L)", OBJECTS_REQUIRE_NON_NULL_ELSE_GET, item.getMemoizerName(), newReference);
            });
            staticImports.add(Objects.class, OBJECTS_REQUIRE_NON_NULL_ELSE_GET);
        });

        var compactConstructor = compactConstructorBuilder.build();

        recordBuilder.compactConstructor(compactConstructor);
    }

    private ParameterSpec toParameterSpec(ExecutableElement method) {
        var type = TypeName.get(method.getReturnType());
        var name = method.getSimpleName().toString();
        var methodAnnotation = createAnnotationSpecs(method.getAnnotationMirrors(), TYPE_PARAMETER);

        return ParameterSpec.builder(type, name)
                .addAnnotations(methodAnnotation)
                .build();
    }

    private ParameterSpec toParameterSpec(Memoization.Item memoizedElement) {
        var memoizerType = memoizedElement.type();

        var typeMemoizer = from(memoizerType);
        var annotations = createAnnotationSpecs(memoizedElement.annotations(),
                TYPE_PARAMETER,
                List.of(Nullable.class),
                List.of(Memoized.class));

        return ParameterSpec.builder(typeMemoizer.typeName(memoizerType), memoizedElement.getMemoizerName())
                .addAnnotations(annotations)
                .build();
    }

    private Modifier[] getMainModifiers(GenerationContext context) {
        return context.isRecordPublic() ? new Modifier[] {PUBLIC} : new Modifier[0];
    }
}
