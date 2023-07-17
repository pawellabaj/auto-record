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
import pl.com.labaj.autorecord.context.StaticImports;
import pl.com.labaj.autorecord.processor.AutoRecordProcessorException;
import pl.com.labaj.autorecord.processor.context.ProcessorContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;

class BuilderOptionsSubGenerator {
    private static final Class<RecordBuilder.Options> RECORD_BUILDER_OPTIONS_CLASS = RecordBuilder.Options.class;
    private final ProcessorContext context;

    BuilderOptionsSubGenerator(ProcessorContext context) {
        this.context = context;
    }

    void generate(TypeSpec.Builder recordBuilder, StaticImports staticImports) {
        var builderOptions = context.builderOptions();
        var methods = RECORD_BUILDER_OPTIONS_CLASS.getDeclaredMethods();
        var optionsDifferentThanDefault = Arrays.stream(methods)
                .map(method -> BuilderOption.of(builderOptions, method))
                .filter(BuilderOption::actualDifferentThanDefault)
                .sorted(comparing(BuilderOption::name))
                .toList();

        if (optionsDifferentThanDefault.isEmpty()) {
            return;
        }

        var optionsAnnotationBuilder = AnnotationSpec.builder(RECORD_BUILDER_OPTIONS_CLASS);
        optionsDifferentThanDefault.forEach(option -> addMember(staticImports, optionsAnnotationBuilder, option));

        recordBuilder.addAnnotation(optionsAnnotationBuilder.build());
    }

    private void addMember(StaticImports staticImports, AnnotationSpec.Builder annotationBuilder, BuilderOption option) {
        var name = option.name;
        var actualValue = option.actualValue;
        var returnType = option.type;

        if (returnType.isPrimitive()) {
            annotationBuilder.addMember(name, "$L", actualValue);
        } else if (option.type().isEnum()) {
            var enumName = ((Enum<?>) actualValue).name();
            staticImports.add(returnType, enumName);
            annotationBuilder.addMember(name, "$L", enumName);
        } else if (returnType.isArray()) {
            annotationBuilder.addMember(name, getArrayStatement(staticImports, (Object[]) actualValue));
        } else {
            annotationBuilder.addMember(name, "$S", actualValue);
        }
    }

    private String getArrayStatement(StaticImports staticImports, Object[] actualValue) {
        return Arrays.stream(actualValue)
                .map(value -> getItemStatement(staticImports, value))
                .collect(joining(", ", "{", "}"));
    }

    private String getItemStatement(StaticImports staticImports, Object value) {
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

    private record BuilderOption(String name, Class<?> type, Object defaultValue, Object actualValue) {

        static BuilderOption of(RecordBuilder.Options builderOptions, Method method) {
            return new BuilderOption(method.getName(), method.getReturnType(), method.getDefaultValue(), getActualValue(builderOptions, method));
        }

        private static Object getActualValue(RecordBuilder.Options builderOptions, Method method) {
            try {
                return method.invoke(builderOptions);
            } catch (Exception e) {
                throw new AutoRecordProcessorException("Cannot get RecordBuilder.Options." + method.getName() + " value", e);
            }
        }

        private boolean actualDifferentThanDefault() {
            if (type.isArray()) {
                return !Arrays.equals((Object[]) defaultValue, (Object[]) actualValue);
            }

            return !Objects.equals(defaultValue, actualValue);
        }
    }
}
