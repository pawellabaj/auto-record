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
import com.squareup.javapoet.TypeSpec;
import io.soabase.recordbuilder.core.RecordBuilder;
import pl.com.labaj.autorecord.processor.AutoRecordProcessorException;
import pl.com.labaj.autorecord.processor.StaticImportsCollector;
import pl.com.labaj.autorecord.processor.context.GenerationContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;

class BuilderOptionsSubGenerator {
    void generate(GenerationContext context, StaticImportsCollector staticImports, TypeSpec.Builder recordBuilder) {
        var methods = RecordBuilder.Options.class.getDeclaredMethods();
        var optionsDifferentThanDefault = Arrays.stream(methods)
                .map(method -> BuilderOption.fromMethod(context.builderOptions(), method))
                .filter(BuilderOption::actualDifferentThanDefault)
                .sorted(comparing(BuilderOption::name))
                .toList();

        if (optionsDifferentThanDefault.isEmpty()) {
            return;
        }

        var optionsAnnotationBuilder = AnnotationSpec.builder(RecordBuilder.Options.class);
        optionsDifferentThanDefault.forEach(option -> addMember(staticImports, optionsAnnotationBuilder, option));

        recordBuilder.addAnnotation(optionsAnnotationBuilder.build());
    }

    private void addMember(StaticImportsCollector staticImports, AnnotationSpec.Builder optionsAnnotationBuilder, BuilderOption option) {
        var name = option.name;
        var actualValue = option.actualValue;
        var returnType = option.type;

        if (returnType.isPrimitive()) {
            optionsAnnotationBuilder.addMember(name, "$L", actualValue);
        } else if (option.type().isEnum()) {
            var enumName = ((Enum<?>) actualValue).name();
            staticImports.add(returnType, enumName);
            optionsAnnotationBuilder.addMember(name, "$L", enumName);
        } else if (returnType.isArray()) {
            optionsAnnotationBuilder.addMember(name, getArrayStatement(staticImports, (Object[]) actualValue));
        } else {
            optionsAnnotationBuilder.addMember(name, "$S", actualValue);
        }
    }

    private String getArrayStatement(StaticImportsCollector staticImports, Object[] actualValue) {
        return Arrays.stream(actualValue)
                .map(value -> getItemStatement(staticImports, value))
                .collect(joining(", ", "{", "}"));
    }

    private String getItemStatement(StaticImportsCollector staticImports, Object value) {
        var valueClass = value.getClass();
        if (valueClass.isPrimitive()) {
            return String.valueOf(value);
        }

        if (valueClass.isEnum()) {
            Enum<?> enumValue = (Enum<?>) value;
            staticImports.add(enumValue);
            return enumValue.name();
        }

        return "\"" + value + "\"";
    }

    private static final class BuilderOption {
        private final String name;
        private final Class<?> type;
        private final Object defaultValue;
        private final Object actualValue;

        BuilderOption(String name, Class<?> type, Object defaultValue, Object actualValue) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
            this.actualValue = actualValue;
        }

        static BuilderOption fromMethod(RecordBuilder.Options builderOptions, Method method) {
            return new BuilderOption(method.getName(), method.getReturnType(), method.getDefaultValue(), getActualValue(builderOptions, method));
        }

        private static Object getActualValue(RecordBuilder.Options builderOptions, Method method) {
            try {
                return method.invoke(builderOptions);
            } catch (Exception e) {
                throw new AutoRecordProcessorException("Cannot get RecordBuilder.Options.%s value".formatted(method.getName()), e);
            }
        }

        private boolean actualDifferentThanDefault() {
            if (type.isArray()) {
                return !Arrays.equals((Object[]) defaultValue, (Object[]) actualValue);
            }
            return !Objects.equals(defaultValue, actualValue);
        }

        private String name() {
            return name;
        }

        public Class<?> type() {
            return type;
        }
    }
}
