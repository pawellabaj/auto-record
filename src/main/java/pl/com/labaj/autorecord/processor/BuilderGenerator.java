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
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.soabase.recordbuilder.core.RecordBuilder;
import pl.com.labaj.autorecord.processor.context.AutoRecordContext;
import pl.com.labaj.autorecord.processor.memoization.TypeMemoizer;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.lang.annotation.ElementType.METHOD;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static pl.com.labaj.autorecord.processor.special.SpecialMethod.TO_BUILDER;
import static pl.com.labaj.autorecord.processor.utils.Annotations.createAnnotationSpecs;
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
        var memoizedItems = context.generation().memoization().items();

        var builderMethodBuilder = MethodSpec.methodBuilder("builder")
                .addModifiers(context.target().modifiers())
                .addModifiers(STATIC);

        String formatPefix;
        var statementValues = new ArrayList<>();
        statementValues.add(recordBuilderName);
        var returnClassName = ClassName.get(context.target().packageName(), recordBuilderName);

        var typeParameters = context.source().typeParameters();
        if (typeParameters.isEmpty()) {
            formatPefix = "return $L.$L()";
            builderMethodBuilder.returns(returnClassName);
        } else {
            var genericVariables = getGenericVariableNames(typeParameters);
            var genericNames = getGenericTypeNames(typeParameters);

            formatPefix = genericVariables.stream()
                    .map(v -> "$T")
                    .collect(joining(", ", "return $L.<", ">$L()"));

            statementValues.addAll(genericNames);

            builderMethodBuilder.addTypeVariables(genericVariables)
                    .returns(ParameterizedTypeName.get(returnClassName, genericNames.toArray(TypeName[]::new)));
        }

        var statementFormat = memoizedItems.stream()
                .map(method -> ".$N($N)")
                .collect(joining("", formatPefix, ""));

        statementValues.add(builderOptions.builderMethodName());

        memoizedItems.forEach(item -> {
            var type = item.type();
            var typeMemoizer = TypeMemoizer.typeMemoizerWith(type);

            statementValues.add(typeMemoizer.getMemoizerName(item.name()));
            statementValues.add(typeMemoizer.getNewStatement());
        });

        builderMethodBuilder.addStatement(statementFormat, statementValues.toArray());

        recordBuilder.addMethod(builderMethodBuilder.build());
    }

    private void createToBuilderMethod(TypeSpec.Builder recordBuilder) {
        var parentToBuilderMethod = context.source().specialMethod(TO_BUILDER);
        var typeParameters = context.source().typeParameters();

        var modifiers = context.target().modifiers();

        if (parentToBuilderMethod.isPresent()) {
            modifiers = forcePublicModifier(modifiers);
        }

        var toBuilderMethodBuilder = MethodSpec.methodBuilder("toBuilder")
                .addModifiers(modifiers);

        parentToBuilderMethod.ifPresent(parentMethod -> {
            var annotationSpecs = createAnnotationSpecs(parentMethod.getAnnotationMirrors(), METHOD, List.of(Override.class), List.of());
            toBuilderMethodBuilder.addAnnotations(annotationSpecs);
        });

        String statementFormat;
        var statementValues = new ArrayList<>();
        statementValues.add(recordBuilderName);
        var returnClassName = ClassName.get(context.target().packageName(), recordBuilderName);

        parentToBuilderMethod.ifPresent(parentMethod -> {
            var returnType = parentMethod.getReturnType();
            var parentReturnClass = returnType.toString();
            var properReturnClass = returnClassName.toString();

            context.generation().logger().debug("*--> " + parentReturnClass + ", " + properReturnClass);

            if (!(parentReturnClass.equals(properReturnClass) || parentReturnClass.equals(recordBuilderName))) {
                context.generation().logger().error("Method " + TO_BUILDER + " has to return " + properReturnClass);
            }
        });

        if (typeParameters.isEmpty()) {
            statementFormat = "return $L.$L(this)";
            toBuilderMethodBuilder.returns(returnClassName);
        } else {
            var genericVariables = getGenericVariableNames(typeParameters);
            var genericNames = getGenericTypeNames(typeParameters);

            statementFormat = genericVariables.stream()
                    .map(v -> "$T")
                    .collect(joining(", ", "return $L.<", ">$L(this)"));

            statementValues.addAll(genericNames);

            toBuilderMethodBuilder.returns(ParameterizedTypeName.get(returnClassName, genericNames.toArray(TypeName[]::new)));
        }

        statementValues.add(builderOptions.copyMethodName());

        toBuilderMethodBuilder.addStatement(statementFormat, statementValues.toArray());

        recordBuilder.addMethod(toBuilderMethodBuilder.build());
    }

    private Modifier[] forcePublicModifier(Modifier[] modifiers1) {
        var mods = new ArrayList<Modifier>();
        mods.add(PUBLIC);

        for (Modifier m : modifiers1) {
            if (m == PROTECTED || m == PUBLIC) {
                continue;
            }
            mods.add(m);
        }

        modifiers1 = mods.toArray(new Modifier[0]);
        return modifiers1;
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

        if (returnType.isPrimitive()) {
            optionsAnnotationBuilder.addMember(name, "$L", actualValue);
        } else if (builderOption.returnType().isEnum()) {
            var enumName = ((Enum<?>) actualValue).name();
            context.generation().staticImports().add(returnType, enumName);
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
            context.generation().staticImports().add(valueClass, enumName);
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
