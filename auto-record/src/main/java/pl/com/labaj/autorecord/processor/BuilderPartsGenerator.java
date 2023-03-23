package pl.com.labaj.autorecord.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import io.soabase.recordbuilder.core.RecordBuilder;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static pl.com.labaj.autorecord.processor.GenericHelper.getGenericNames;
import static pl.com.labaj.autorecord.processor.GenericHelper.getGenericVariables;

class BuilderPartsGenerator {

    private static final String ADD_CLASS_RETAINED_GENERATED = "addClassRetainedGenerated";
    private final GeneratorParameters parameters;
    private final TypeSpec.Builder recordSpecBuilder;

    BuilderPartsGenerator(GeneratorParameters parameters, TypeSpec.Builder recordSpecBuilder) {
        this.parameters = parameters;
        this.recordSpecBuilder = recordSpecBuilder;
    }

    BuilderPartsGenerator createRecordBuilderAnnotation() {
        var builderAnnotation = AnnotationSpec.builder(RecordBuilder.class).build();
        recordSpecBuilder.addAnnotation(builderAnnotation);
        return this;
    }

    BuilderPartsGenerator createRecordBuilderOptionsAnnotation() {

        var methods = RecordBuilder.Options.class.getDeclaredMethods();
        var valuesList = Arrays.stream(methods)
                .map(this::toValues)
                .map(this::forceRetainedGenerated)
                .filter(this::hasDifferentValues)
                .toList();

        var optionsAnnotationBuilder = AnnotationSpec.builder(RecordBuilder.Options.class);
        valuesList.forEach(values -> addMember(optionsAnnotationBuilder, values));

        recordSpecBuilder.addAnnotation(optionsAnnotationBuilder.build());

        return this;
    }

    private Values forceRetainedGenerated(Values values) {
        if (ADD_CLASS_RETAINED_GENERATED.equals(values.name)) {
            return new Values(ADD_CLASS_RETAINED_GENERATED, Boolean.TYPE, false, true);
        }
        return values;
    }

    @SuppressWarnings("UnusedReturnValue")
    BuilderPartsGenerator createBuilderMethod() {
        var builderOptions = parameters.builderOptions();
        var recordBuilderName = parameters.recordName() + builderOptions.suffix();

        var builderMethodBuilder = MethodSpec.methodBuilder("builder")
                .addModifiers(PUBLIC, STATIC)
                .addStatement("return $L.$L()", recordBuilderName, builderOptions.builderMethodName());
        var returnedClassName = ClassName.get(parameters.packageName(), recordBuilderName);

        var typeParameters = parameters.sourceInterface().getTypeParameters();
        if (typeParameters.isEmpty()) {
            builderMethodBuilder.returns(returnedClassName);
        } else {
            var genericVariables = getGenericVariables(typeParameters);
            var genericNames = getGenericNames(typeParameters);

            builderMethodBuilder.addTypeVariables(genericVariables)
                    .returns(ParameterizedTypeName.get(returnedClassName, genericNames));
        }

        var builderMethod = builderMethodBuilder
                .build();
        recordSpecBuilder.addMethod(builderMethod);

        return this;
    }

    private Values toValues(Method method) {
        var defaultValue = method.getDefaultValue();
        var actualValue = getActualValue(method);
        var returnType = method.getReturnType();

        return new Values(method.getName(), returnType, defaultValue, actualValue);
    }

    private Object getActualValue(Method method) {
        try {
            return method.invoke(parameters.builderOptions());
        } catch (Exception e) {
            parameters.logger().error("Cannot get RecordBuilder.Options.%s value".formatted(method.getName()));
        }
        return null;
    }

    private boolean hasDifferentValues(Values values) {
        if (values.returnType.isArray()) {
            return !Arrays.equals((Object[]) values.defaultValue, (Object[]) values.actualValue);
        }
        return !Objects.equals(values.defaultValue, values.actualValue);
    }

    private void addMember(AnnotationSpec.Builder optionsAnnotationBuilder, Values values) {
        var name = values.name;
        var actualValue = values.actualValue;
        var returnType = values.returnType;

        if (returnType.isPrimitive()) {
            optionsAnnotationBuilder.addMember(name, "$L", actualValue);
        } else if (values.returnType().isEnum()) {
            var enumName = ((Enum<?>) actualValue).name();
            parameters.staticImports().add(new StaticImport(returnType, enumName));
            optionsAnnotationBuilder.addMember(name, "$L", enumName);
        } else if (returnType.isArray()) {
            var format = getArrayFormat((Object[]) actualValue);
            optionsAnnotationBuilder.addMember(name, format);
        } else {
            optionsAnnotationBuilder.addMember(name, "$S", actualValue);
        }
    }

    private String getArrayFormat(Object[] actualValue) {
        return Arrays.stream(actualValue)
                .map(this::getItemFormat)
                .collect(joining(", ", "{", "}"));
    }

    private String getItemFormat(Object value) {
        var valueClass = value.getClass();
        if (valueClass.isPrimitive()) {
            return String.valueOf(value);
        }
        if (valueClass.isEnum()) {
            var enumName = ((Enum<?>) value).name();
            parameters.staticImports().add(new StaticImport(valueClass, enumName));
            return enumName;
        }
        return "\"" + value + "\"";
    }

    record Values(String name, Class<?> returnType, Object defaultValue, Object actualValue) {}
}
