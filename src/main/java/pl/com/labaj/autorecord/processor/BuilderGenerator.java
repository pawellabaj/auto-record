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
import pl.com.labaj.autorecord.processor.context.AutoRecordContext;
import pl.com.labaj.autorecord.processor.utils.StaticImports;

import javax.lang.model.element.ExecutableElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.STATIC;
import static pl.com.labaj.autorecord.processor.utils.Generics.getGenericTypeNames;
import static pl.com.labaj.autorecord.processor.utils.Generics.getGenericVariableNames;

class BuilderGenerator extends SubGenerator {

    private static final AnnotationSpec BUILDER_ANNOTATION = AnnotationSpec.builder(RecordBuilder.class).build();
    private final RecordBuilder.Options builderOptions;
    private final String recordBuilderName;

    BuilderGenerator(AutoRecordContext context) {
        super(context);
        builderOptions = context.generation().builderOptions();
        recordBuilderName = context.target().name() + builderOptions.suffix();
    }

    @Override
    public void generate(TypeSpec.Builder recordBuilder) {
        if (!context.generation().recordOptions().withBuilder()) {
            return;
        }

        createRecordBuilderAnnotation(recordBuilder);
        createRecordBuilderOptionsAnnotation(recordBuilder);
        createBuilderMethod(recordBuilder);
        createToBuilderMethod(recordBuilder);
    }

    private void createRecordBuilderAnnotation(TypeSpec.Builder recordBuilder) {
        recordBuilder.addAnnotation(BUILDER_ANNOTATION);
    }

    private void createRecordBuilderOptionsAnnotation(TypeSpec.Builder recordBuilder) {
        var methods = RecordBuilder.Options.class.getDeclaredMethods();
        var options = Arrays.stream(methods)
                .map(this::toOption)
                .filter(BuilderOption::actualDifferentThanDefault)
                .toList();

        if (options.isEmpty()) {
            return;
        }

        var optionsAnnotationBuilder = AnnotationSpec.builder(RecordBuilder.Options.class);
        options.forEach(builderOption -> addMember(optionsAnnotationBuilder, builderOption));

        recordBuilder.addAnnotation(optionsAnnotationBuilder.build());
    }

    private void createBuilderMethod(TypeSpec.Builder recordBuilder) {
        var builderMethodBuilder = MethodSpec.methodBuilder("builder")
                .addModifiers(context.target().modifiers())
                .addModifiers(STATIC)
                .addStatement("return $L.$L()", recordBuilderName, builderOptions.builderMethodName());
        var methodSpec = builderMethodSpec(builderMethodBuilder);

        recordBuilder.addMethod(methodSpec);
    }

    private void createToBuilderMethod(TypeSpec.Builder recordBuilder) {
        var propertyMethods = context.source().propertyMethods();

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
                .addModifiers(context.target().modifiers())
                .addStatement(format, arguments.toArray());
        var methodSpec = builderMethodSpec(toBuilderMethodBuilder);

        recordBuilder.addMethod(methodSpec);
    }

    private MethodSpec builderMethodSpec(MethodSpec.Builder methodBuilder) {
        var returClassName = ClassName.get(context.target().packageName(), recordBuilderName);

        var typeParameters = context.source().typeParameters();
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

    private BuilderOption toOption(Method method) {
        var defaultValue = method.getDefaultValue();
        var actualValue = getActualValue(method);
        var returnType = method.getReturnType();

        return new BuilderOption(method.getName(), returnType, defaultValue, actualValue);
    }

    private Object getActualValue(Method method) {
        try {
            return method.invoke(builderOptions);
        } catch (Exception e) {
            context.generation().logger().error("Cannot get RecordBuilder.Options.%s value".formatted(method.getName()));
        }
        return null;
    }

    private void addMember(AnnotationSpec.Builder optionsAnnotationBuilder, BuilderOption builderOption) {
        var name = builderOption.name;
        var actualValue = builderOption.actualValue;
        var returnType = builderOption.returnType;
        var staticImports = context.generation().staticImports();

        if (returnType.isPrimitive()) {
            optionsAnnotationBuilder.addMember(name, "$L", actualValue);
        } else if (builderOption.returnType().isEnum()) {
            var enumName = ((Enum<?>) actualValue).name();
            staticImports.add(returnType, enumName);
            optionsAnnotationBuilder.addMember(name, "$L", enumName);
        } else if (returnType.isArray()) {
            var format = getArrayFormat((Object[]) actualValue, staticImports);
            optionsAnnotationBuilder.addMember(name, format);
        } else {
            optionsAnnotationBuilder.addMember(name, "$S", actualValue);
        }
    }

    private String getArrayFormat(Object[] actualValue, StaticImports staticImports) {
        return Arrays.stream(actualValue)
                .map(value -> getItemFormat(value, staticImports))
                .collect(joining(", ", "{", "}"));
    }

    private String getItemFormat(Object value, StaticImports staticImports) {
        var valueClass = value.getClass();
        if (valueClass.isPrimitive()) {
            return String.valueOf(value);
        }
        if (valueClass.isEnum()) {
            var enumName = ((Enum<?>) value).name();
            staticImports.add(valueClass, enumName);
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
