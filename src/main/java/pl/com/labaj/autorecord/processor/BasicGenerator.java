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

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.Memoized;
import pl.com.labaj.autorecord.processor.memoization.Memoization;
import pl.com.labaj.autorecord.processor.memoization.TypeMemoizer;
import pl.com.labaj.autorecord.processor.utils.Method;

import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import javax.lang.model.element.ExecutableElement;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.util.stream.Collectors.joining;
import static pl.com.labaj.autorecord.processor.memoization.TypeMemoizer.typeMemoizerWith;
import static pl.com.labaj.autorecord.processor.utils.Annotations.createAnnotationSpecs;
import static pl.com.labaj.autorecord.processor.utils.Generics.getGenericVariableNames;

public class BasicGenerator extends SubGenerator {
    private static final AnnotationSpec GENERATED_ANNOTATION = AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", AutoRecord.class.getName())
            .build();
    private static final AnnotationSpec GENERATED_WITH_AUTO_RECORD_ANNOTATION = AnnotationSpec.builder(GeneratedWithAutoRecord.class).build();
    private static final String OBJECTS_REQUIRE_NON_NULL = "requireNonNull";

    public BasicGenerator(GeneratorMetaData metaData) {
        super(metaData);
    }

    @Override
    public void generate(TypeSpec.Builder recordSpecBuilder, List<StaticImport> staticImports, Logger logger) {
        createBasicElements(recordSpecBuilder);
        createTypeVariables(recordSpecBuilder);

        var recordComponents = createRecordComponents(recordSpecBuilder);

        createAdditionalRecordComponents(recordSpecBuilder);
        createAdditionalConstructor(recordSpecBuilder, recordComponents);
        createCompactConstructor(recordSpecBuilder, staticImports);
    }

    private void createBasicElements(TypeSpec.Builder recordSpecBuilder) {
        recordSpecBuilder.addAnnotation(GENERATED_ANNOTATION)
                .addAnnotation(GENERATED_WITH_AUTO_RECORD_ANNOTATION)
                .addModifiers(metaData.recordModifiers())
                .addSuperinterface(metaData.sourceInterface().asType());
    }

    private void createTypeVariables(TypeSpec.Builder recordSpecBuilder) {
        var typeParameters = metaData.sourceInterface().getTypeParameters();

        if (typeParameters.isEmpty()) {
            return;
        }

        var genericVariables = getGenericVariableNames(typeParameters);
        recordSpecBuilder.addTypeVariables(genericVariables);
    }

    private List<ParameterSpec> createRecordComponents(TypeSpec.Builder recordSpecBuilder) {
        var recordComponents = metaData.propertyMethods().stream()
                .map(this::toParameterSpec)
                .toList();
        recordSpecBuilder.addRecordComponents(recordComponents);

        return recordComponents;
    }

    private void createAdditionalRecordComponents(TypeSpec.Builder recordSpecBuilder) {
        metaData.memoization().items().stream()
                .map(this::toParameterSpec)
                .forEach(recordSpecBuilder::addRecordComponent);
    }

    private void createAdditionalConstructor(TypeSpec.Builder recordSpecBuilder, List<ParameterSpec> recordComponents) {
        var items = metaData.memoization().items();
        if (items.isEmpty()) {
            return;
        }

        var componentFormats = recordComponents.stream().map(p -> "$N");
        var memoizerFormats = items.stream()
                .map(Memoization.Item::type)
                .map(TypeMemoizer::typeMemoizerWith)
                .map(TypeMemoizer::getConstructorStatement);
        var constructorCallFormat = Stream.concat(componentFormats, memoizerFormats)
                .collect(joining(", ", "this(", ")"));

        var constructor = MethodSpec.constructorBuilder()
                .addModifiers(metaData.recordModifiers())
                .addParameters(recordComponents)
                .addStatement(constructorCallFormat, recordComponents.toArray())
                .build();

        recordSpecBuilder.addMethod(constructor);
    }

    private void createCompactConstructor(TypeSpec.Builder recordSpecBuilder, List<StaticImport> staticImports) {
        var nonNullNames = metaData.propertyMethods().stream()
                .map(Method::new)
                .filter(Method::doesNotReturnPrimitive)
                .filter(method -> method.isNotAnnotatedWith(Nullable.class))
                .map(Method::method)
                .map(ExecutableElement::getSimpleName)
                .toList();

        if (nonNullNames.isEmpty()) {
            return;
        }

        var compactConstructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(metaData.recordModifiers());

        nonNullNames.forEach(name -> compactConstructorBuilder.addStatement("$1N($2N, () -> \"$2N must not be null\")", OBJECTS_REQUIRE_NON_NULL, name));
        staticImports.add(new StaticImport(Objects.class, OBJECTS_REQUIRE_NON_NULL));

        recordSpecBuilder.compactConstructor(compactConstructorBuilder.build());
    }

    private ParameterSpec toParameterSpec(ExecutableElement method) {
        var type = TypeName.get(method.getReturnType());
        var name = method.getSimpleName().toString();
        var componentAnnotations = createAnnotationSpecs(method.getAnnotationMirrors(), TYPE_PARAMETER);

        return ParameterSpec.builder(type, name)
                .addAnnotations(componentAnnotations)
                .build();
    }

    private ParameterSpec toParameterSpec(Memoization.Item memoizedElement) {
        var type = memoizedElement.type();

        var typeMemoizer = typeMemoizerWith(type);
        var annotations = createAnnotationSpecs(memoizedElement.annotations(),
                TYPE_PARAMETER,
                List.of(Nullable.class),
                List.of(Memoized.class));

        return ParameterSpec.builder(typeMemoizer.getTypeName(type), memoizedElement.getMemoizerName())
                .addAnnotations(annotations)
                .build();
    }
}
