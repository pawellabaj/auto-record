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
import pl.com.labaj.autorecord.context.RecordComponent;
import pl.com.labaj.autorecord.context.StaticImports;
import pl.com.labaj.autorecord.extension.AutoRecordExtension;
import pl.com.labaj.autorecord.extension.CompactConstructorExtension;
import pl.com.labaj.autorecord.processor.context.Memoization;
import pl.com.labaj.autorecord.processor.context.MemoizerType;
import pl.com.labaj.autorecord.processor.context.ProcessorContext;

import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.utils.Annotations.createAnnotationSpecs;

class BasicGenerator implements RecordGenerator {
    private static final AnnotationSpec GENERATED_ANNOTATION = AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", AutoRecord.class.getName())
            .build();
    private static final AnnotationSpec GENERATED_WITH_AUTO_RECORD_ANNOTATION = AnnotationSpec.builder(GeneratedWithAutoRecord.class).build();
    private static final String OBJECTS_REQUIRE_NON_NULL = "requireNonNull";
    private static final String OBJECTS_REQUIRE_NON_NULL_ELSE_GET = "requireNonNullElseGet";

    @Override
    public void generate(ProcessorContext context, List<AutoRecordExtension> extensions, TypeSpec.Builder recordBuilder, StaticImports staticImports) {
        recordBuilder
                .addAnnotation(GENERATED_ANNOTATION)
                .addAnnotation(GENERATED_WITH_AUTO_RECORD_ANNOTATION)
                .addModifiers(getMainModifiers(context))
                .addSuperinterface(context.interfaceType());

        var recordComponentParameters = context.components().stream()
                .map(this::toParameterSpec)
                .toList();

        generateTypeVariables(context, recordBuilder);
        generateComponents(context, recordBuilder, recordComponentParameters);
        createAdditionalConstructor(context, recordBuilder, recordComponentParameters);
        createCompactConstructor(context, extensions, recordBuilder, staticImports);
    }

    private void generateComponents(ProcessorContext context, TypeSpec.Builder recordBuilder, List<ParameterSpec> recordComponentParameters) {
        recordBuilder.addRecordComponents(recordComponentParameters);

        context.memoization()
                .ifPresent(items -> items.stream()
                        .map(this::toParameterSpec)
                        .forEach(recordBuilder::addRecordComponent));
    }

    private void generateTypeVariables(ProcessorContext context, TypeSpec.Builder recordBuilder) {
        context.generics()
                .ifPresent(recordBuilder::addTypeVariables);
    }

    private void createAdditionalConstructor(ProcessorContext context, TypeSpec.Builder recordBuilder, List<ParameterSpec> recordComponentParameters) {
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

    private void createCompactConstructor(ProcessorContext context,
                                          List<AutoRecordExtension> extensions,
                                          TypeSpec.Builder recordBuilder,
                                          StaticImports staticImports) {
        var nonNullNames = context.components().stream()
                .filter(RecordComponent::isNotPrimitive)
                .filter(rc -> rc.isNotAnnotatedWith(Nullable.class))
                .map(RecordComponent::name)
                .toList();

//        var className = "pl.com.labaj.autorecord.extension.AlwaysCompactConstructorExtension";
//        try {
//
//            ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
////                                                                var aClass = Class.forName(className, true, systemClassLoader);
//            var aClass = Class.forName(className, true, systemClassLoader);
//            var ext = aClass.getDeclaredConstructor().newInstance();
//            var extension = (AutoRecordExtension) ext;
////            extension.setParameters(params);
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
//                 InvocationTargetException | NoSuchMethodException e) {
//            throw new AutoRecordProcessorException("Cannot " + className, e);
//        }





        var memoization = context.memoization();

        var compactConstructorExtensions = extensions.stream()
                .filter(CompactConstructorExtension.class::isInstance)
                .map(CompactConstructorExtension.class::cast)
                .toList();

        var generatedByProcessor = !nonNullNames.isEmpty() || !memoization.isEmpty();
        var generatedByExtensions = compactConstructorExtensions.stream()
                .collect(toMap(
                        Object::hashCode,
                        extension -> extension.shouldGenerate(generatedByProcessor, context)
                ));
        context.logger().info("Extensions: " + generatedByExtensions);
        var generatedByAtLeastOneExtension = generatedByExtensions.values().stream()
                .reduce(false, (g1, g2) -> g1 || g2);

        if (!generatedByProcessor && !generatedByAtLeastOneExtension) {
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
                var memoizerType = MemoizerType.from(item.type());
                var newReference = memoizerType.getNewReference();
                compactConstructorBuilder.addStatement("$2N = $1L($2N, $3L)", OBJECTS_REQUIRE_NON_NULL_ELSE_GET, item.getMemoizerName(), newReference);
            });
            staticImports.add(Objects.class, OBJECTS_REQUIRE_NON_NULL_ELSE_GET);
        });

        var compactConstructor = compactConstructorBuilder.build();

        recordBuilder.compactConstructor(compactConstructor);
    }

    private ParameterSpec toParameterSpec(RecordComponent recordComponent) {
        var type = TypeName.get(recordComponent.type());
        var annotations = createAnnotationSpecs(recordComponent.annotations());

        return ParameterSpec.builder(type, recordComponent.name())
                .addAnnotations(annotations)
                .build();
    }

    private ParameterSpec toParameterSpec(Memoization.Item memoizedElement) {
        var memoizerType = memoizedElement.type();

        var typeMemoizer = MemoizerType.from(memoizerType);
        var annotations = createAnnotationSpecs(memoizedElement.annotations(),
                TYPE_PARAMETER,
                List.of(Nullable.class),
                List.of(Memoized.class));

        return ParameterSpec.builder(typeMemoizer.typeName(memoizerType), memoizedElement.getMemoizerName())
                .addAnnotations(annotations)
                .build();
    }

    private Modifier[] getMainModifiers(ProcessorContext context) {
        return context.isRecordPublic() ? new Modifier[] {PUBLIC} : new Modifier[0];
    }
}
