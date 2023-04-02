package pl.com.labaj.autorecord.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import pl.com.labaj.autorecord.Memoized;
import pl.com.labaj.autorecord.Memoizer;

import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.ClassUtils.primitiveToWrapper;
import static pl.com.labaj.autorecord.processor.AnnotationsHelper.createAnnotationSpecs;
import static pl.com.labaj.autorecord.processor.MemoizerHelper.memoizerComponentName;
import static pl.com.labaj.autorecord.processor.MemoizerHelper.memoizerConstructorStatement;
import static pl.com.labaj.autorecord.processor.MethodHelper.isNotAnnotatedWith;

class ConstructionPartsGenerator {

    private static final String OBJECTS_REQUIRE_NON_NULL = "requireNonNull";
    private final GeneratorParameters parameters;
    private final TypeSpec.Builder recordSpecBuilder;
    private final Memoization memoization;
    private final List<ExecutableElement> propertyMethods;
    private final Modifier[] recordModifiers;

    ConstructionPartsGenerator(GeneratorParameters parameters, TypeSpec.Builder recordSpecBuilder, Memoization memoization) {
        this.parameters = parameters;
        this.recordSpecBuilder = recordSpecBuilder;
        this.memoization = memoization;
        propertyMethods = parameters.propertyMethods();
        recordModifiers = parameters.recordModifiers();
    }

    WithRecordComponents createTypeVariables() {
        var typeParameters = parameters.sourceInterface().getTypeParameters();

        if (typeParameters.isEmpty()) {
            return createRecordComponents();
        }

        var genericVariables = GenericHelper.getGenericVariables(typeParameters);
        recordSpecBuilder.addTypeVariables(genericVariables);

        return createRecordComponents();
    }

    private ConstructionPartsGenerator.WithRecordComponents createRecordComponents() {
        var recordComponents = propertyMethods.stream()
                .map(this::toParameterSpec)
                .toList();
        recordSpecBuilder.addRecordComponents(recordComponents);

        return new WithRecordComponents(recordComponents);
    }

    private ParameterSpec toParameterSpec(ExecutableElement method) {
        var type = TypeName.get(method.getReturnType());
        var name = method.getSimpleName().toString();
        var componentAnnotations = createAnnotationSpecs(method.getAnnotationMirrors(), TYPE_PARAMETER);

        return ParameterSpec.builder(type, name)
                .addAnnotations(componentAnnotations)
                .build();
    }

    final class WithRecordComponents {
        private final List<ParameterSpec> recordComponents;

        private WithRecordComponents(List<ParameterSpec> recordComponents) {
            this.recordComponents = recordComponents;
        }

        WithRecordComponents createAdditionalRecordComponents() {
            memoization.items().stream()
                    .map(this::toParameterSpec)
                    .forEach(recordSpecBuilder::addRecordComponent);
            return this;
        }

        WithRecordComponents createAdditionalConstructor() {
            var items = memoization.items();
            if (items.isEmpty()) {
                return this;
            }

            var componentFormats = recordComponents.stream().map(p -> "$N");
            var memoizerFormats = items.stream().map(item -> memoizerConstructorStatement());
            var constructorCallFormat = Stream.concat(componentFormats, memoizerFormats)
                    .collect(joining(", ", "this(", ")"));

            var constructor = MethodSpec.constructorBuilder()
                    .addModifiers(recordModifiers)
                    .addParameters(recordComponents)
                    .addStatement(constructorCallFormat, recordComponents.toArray())
                    .build();

            recordSpecBuilder.addMethod(constructor);

            return this;
        }

        WithRecordComponents createCompactConstructor() {
            var nonNullNames = propertyMethods.stream()
                    .filter(MethodHelper::doesNotReturnPrimitive)
                    .filter(method -> isNotAnnotatedWith(method, Nullable.class))
                    .map(ExecutableElement::getSimpleName)
                    .toList();

            if (nonNullNames.isEmpty()) {
                return this;
            }

            var compactConstructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(recordModifiers);

            nonNullNames.forEach(name -> compactConstructorBuilder.addStatement("$1N($2N, () -> \"$2N must not be null\")", OBJECTS_REQUIRE_NON_NULL, name));
            parameters.staticImports().add(new StaticImport(Objects.class, OBJECTS_REQUIRE_NON_NULL));

            recordSpecBuilder.compactConstructor(compactConstructorBuilder.build());

            return this;
        }

        List<ParameterSpec> returnRecordComponents() {
            return recordComponents;
        }

        private ParameterSpec toParameterSpec(Memoization.Item memoizedElement) {
            var type = memoizedElement.type();
            var parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(Memoizer.class),
                    TypeName.get(type.isPrimitive() ? primitiveToWrapper(type) : type));
            var annotations = createAnnotationSpecs(memoizedElement.annotations(),
                    TYPE_PARAMETER,
                    List.of(Nullable.class),
                    List.of(Memoized.class));

            return ParameterSpec.builder(parameterizedTypeName, memoizerComponentName(memoizedElement.name()))
                    .addAnnotations(annotations)
                    .build();
        }
    }
}
