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
import java.lang.annotation.ElementType;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.utils.Annotations.createAnnotationSpecs;
import static pl.com.labaj.autorecord.processor.utils.Annotations.createParameterAnnotationSpecs;
import static pl.com.labaj.autorecord.processor.utils.Extensions.typedExtensions;

class BasicGenerator extends RecordGenerator {
    private static final AnnotationSpec GENERATED_ANNOTATION = AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", AutoRecord.class.getName())
            .build();

    private final CompactConstructorSubGenerator compactConstructorSubGenerator;

    BasicGenerator(ProcessorContext context, List<AutoRecordExtension> extensions) {
        super(context, extensions);
        compactConstructorSubGenerator = new CompactConstructorSubGenerator(context, typedExtensions(extensions, CompactConstructorExtension.class));
    }

    @Override
    public void generate(TypeSpec.Builder recordBuilder, StaticImports staticImports) {
        recordBuilder
                .addAnnotation(GENERATED_ANNOTATION)
                .addModifiers(getMainModifiers(context))
                .addSuperinterface(context.interfaceType());

        var recordComponentParameters = context.components().stream()
                .map(this::toParameterSpec)
                .toList();

        generateAnnotations(recordBuilder);
        generateTypeVariables(recordBuilder);
        generateComponents(recordBuilder, recordComponentParameters);
        createAdditionalConstructor(recordBuilder, recordComponentParameters);

        compactConstructorSubGenerator.generate(recordBuilder, staticImports);
    }

    private void generateAnnotations(TypeSpec.Builder recordBuilder) {
        var annotationSpecs = createAnnotationSpecs(context.interfaceAnnotations(),
                Set.of(ElementType.TYPE),
                List.of(GeneratedWithAutoRecord.class),
                List.of());

        annotationSpecs
                .forEach(recordBuilder::addAnnotation);
    }

    private void generateComponents(TypeSpec.Builder recordBuilder, List<ParameterSpec> recordComponentParameters) {
        recordBuilder.addRecordComponents(recordComponentParameters);

        context.memoization()
                .ifPresent(items -> items.stream()
                        .map(this::toParameterSpec)
                        .forEach(recordBuilder::addRecordComponent));
    }

    private void generateTypeVariables(TypeSpec.Builder recordBuilder) {
        context.generics()
                .ifPresent(recordBuilder::addTypeVariables);
    }

    private void createAdditionalConstructor(TypeSpec.Builder recordBuilder, List<ParameterSpec> recordComponentParameters) {
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

    private ParameterSpec toParameterSpec(RecordComponent recordComponent) {
        var type = TypeName.get(recordComponent.type());
        var annotations = createParameterAnnotationSpecs(recordComponent.annotations());

        return ParameterSpec.builder(type, recordComponent.name())
                .addAnnotations(annotations)
                .build();
    }

    private ParameterSpec toParameterSpec(Memoization.Item memoizedElement) {
        var memoizerType = memoizedElement.type();

        var typeMemoizer = MemoizerType.from(memoizerType);
        var annotations = createAnnotationSpecs(memoizedElement.annotations(),
                Set.of(TYPE_PARAMETER, RECORD_COMPONENT),
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
