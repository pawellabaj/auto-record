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
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import io.soabase.recordbuilder.core.RecordBuilder;

import javax.lang.model.element.ExecutableElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.STATIC;
import static pl.com.labaj.autorecord.processor.utils.Generics.getGenericTypeNames;
import static pl.com.labaj.autorecord.processor.utils.Generics.getGenericVariableNames;

class BuilderGenerator extends SubGenerator {

    private static final AnnotationSpec BUILDER_ANNOTATION = AnnotationSpec.builder(RecordBuilder.class).build();
    private final RecordBuilder.Options builderOptions;
    private final String recordBuilderName;

    BuilderGenerator(GeneratorMetaData metaData) {
        super(metaData);
        builderOptions = metaData.builderOptions();
        recordBuilderName = metaData.recordName() + builderOptions.suffix();
    }

    @Override
    public void generate(TypeSpec.Builder recordSpecBuilder, List<StaticImport> staticImports, Logger logger) {
        if (!metaData.recordOptions().withBuilder()) {
            return;
        }

        createRecordBuilderAnnotation(recordSpecBuilder);
        createRecordBuilderOptionsAnnotation(recordSpecBuilder, staticImports, logger);
        createBuilderMethod(recordSpecBuilder);
        createToBuilderMethod(recordSpecBuilder);
    }

    private void createRecordBuilderAnnotation(TypeSpec.Builder recordSpecBuilder) {
        recordSpecBuilder.addAnnotation(BUILDER_ANNOTATION);
    }

    private void createRecordBuilderOptionsAnnotation(TypeSpec.Builder recordSpecBuilder, List<StaticImport> staticImports, Logger logger) {
        var methods = RecordBuilder.Options.class.getDeclaredMethods();
        var options = Arrays.stream(methods)
                .map(method -> toOption(method, logger))
                .filter(BuilderOption::actualDifferentThanDefault)
                .toList();

        if (options.isEmpty()) {
            return;
        }

        var optionsAnnotationBuilder = AnnotationSpec.builder(RecordBuilder.Options.class);
        options.forEach(builderOption -> addMember(optionsAnnotationBuilder, staticImports, builderOption));

        recordSpecBuilder.addAnnotation(optionsAnnotationBuilder.build());
    }

    private void createBuilderMethod(TypeSpec.Builder recordSpecBuilder) {
        var builderMethodBuilder = MethodSpec.methodBuilder("builder")
                .addModifiers(metaData.recordModifiers())
                .addModifiers(STATIC)
                .addStatement("return $L.$L()", recordBuilderName, builderOptions.builderMethodName());
        var methodSpec = builderMethodSpec(builderMethodBuilder);

        recordSpecBuilder.addMethod(methodSpec);
    }

    private void createToBuilderMethod(TypeSpec.Builder recordSpecBuilder) {
        var propertyMethods = metaData.propertyMethods();

        var format = propertyMethods.stream()
                .map(method -> ".$N($N)")
                .collect(joining("", "return $L.$L()", ""));

        var arguments = new ArrayList<>();
        arguments.add(recordBuilderName);
        arguments.add(builderOptions.builderMethodName());
        propertyMethods.stream()
                .map(ExecutableElement::getSimpleName)
                .forEach(name -> {
                    arguments.add(name);
                    arguments.add(name);
                });

        var toBuilderMethodBuilder = MethodSpec.methodBuilder("toBuilder")
                .addModifiers(metaData.recordModifiers())
                .addStatement(format, arguments.toArray());
        var methodSpec = builderMethodSpec(toBuilderMethodBuilder);

        recordSpecBuilder.addMethod(methodSpec);
    }

    private MethodSpec builderMethodSpec(MethodSpec.Builder methodBuilder) {
        var returClassName = ClassName.get(metaData.packageName(), recordBuilderName);

        var typeParameters = metaData.sourceInterface().getTypeParameters();
        if (typeParameters.isEmpty()) {
            methodBuilder.returns(returClassName);
        } else {
            var genericVariables = getGenericVariableNames(typeParameters);
            var genericNames = getGenericTypeNames(typeParameters);

            methodBuilder.addTypeVariables(genericVariables)
                    .returns(ParameterizedTypeName.get(returClassName, genericNames));
        }

        return methodBuilder.build();
    }

    private BuilderOption toOption(Method method, Logger logger) {
        var defaultValue = method.getDefaultValue();
        var actualValue = getActualValue(method, logger);
        var returnType = method.getReturnType();

        return new BuilderOption(method.getName(), returnType, defaultValue, actualValue);
    }

    private Object getActualValue(Method method, Logger logger) {
        try {
            return method.invoke(builderOptions);
        } catch (Exception e) {
            logger.error("Cannot get RecordBuilder.Options.%s value".formatted(method.getName()));
        }
        return null;
    }

    private void addMember(AnnotationSpec.Builder optionsAnnotationBuilder, List<StaticImport> staticImports, BuilderOption builderOption) {
        var name = builderOption.name;
        var actualValue = builderOption.actualValue;
        var returnType = builderOption.returnType;

        if (returnType.isPrimitive()) {
            optionsAnnotationBuilder.addMember(name, "$L", actualValue);
        } else if (builderOption.returnType().isEnum()) {
            var enumName = ((Enum<?>) actualValue).name();
            metaData.staticImports().add(new StaticImport(returnType, enumName));
            optionsAnnotationBuilder.addMember(name, "$L", enumName);
        } else if (returnType.isArray()) {
            var format = getArrayFormat((Object[]) actualValue, staticImports);
            optionsAnnotationBuilder.addMember(name, format);
        } else {
            optionsAnnotationBuilder.addMember(name, "$S", actualValue);
        }
    }

    private String getArrayFormat(Object[] actualValue, List<StaticImport> staticImports) {
        return Arrays.stream(actualValue)
                .map(value -> getItemFormat(value, staticImports))
                .collect(joining(", ", "{", "}"));
    }

    private String getItemFormat(Object value, List<StaticImport> staticImports) {
        var valueClass = value.getClass();
        if (valueClass.isPrimitive()) {
            return String.valueOf(value);
        }
        if (valueClass.isEnum()) {
            var enumName = ((Enum<?>) value).name();
            staticImports.add(new StaticImport(valueClass, enumName));
            return enumName;
        }
        return "\"" + value + "\"";
    }

    private record BuilderOption(String name, Class<?> returnType, Object defaultValue, Object actualValue) {
        private boolean actualDifferentThanDefault() {
            if (returnType.isArray()) {
                return !Arrays.equals((Object[]) defaultValue, (Object[]) actualValue);
            }
            return !Objects.equals(defaultValue, actualValue);
        }
    }
}
