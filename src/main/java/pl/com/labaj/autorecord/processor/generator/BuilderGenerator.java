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
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import io.soabase.recordbuilder.core.RecordBuilder;
import pl.com.labaj.autorecord.context.StaticImports;
import pl.com.labaj.autorecord.extension.AutoRecordExtension;
import pl.com.labaj.autorecord.processor.context.MemoizerType;
import pl.com.labaj.autorecord.processor.context.ProcessorContext;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static java.util.stream.Collectors.joining;
import static pl.com.labaj.autorecord.processor.context.ProcessorContext.TO_BUILDER;

class BuilderGenerator extends RecordGenerator {
    BuilderOptionsSubGenerator builderOptionsSubGenerator;
    BuilderMethodSubGenerator builderMethodSubGenerator;
    ToBuilderMethodSubGenerator toBuilderMethodSubGenerator;

    private static final AnnotationSpec BUILDER_ANNOTATION = AnnotationSpec.builder(RecordBuilder.class).build();

    BuilderGenerator(ProcessorContext context, List<AutoRecordExtension> extensions) {
        super(context, extensions);
        builderOptionsSubGenerator = new BuilderOptionsSubGenerator(context);
        builderMethodSubGenerator = new BuilderMethodSubGenerator(context);
        toBuilderMethodSubGenerator = new ToBuilderMethodSubGenerator(context);
    }

    @Override
    public void generate(TypeSpec.Builder recordBuilder, StaticImports staticImports) {
        if (!shouldGenerate()) {
            return;
        }

        recordBuilder.addAnnotation(BUILDER_ANNOTATION);

        builderOptionsSubGenerator.generate(recordBuilder, staticImports);
        builderMethodSubGenerator.generate(recordBuilder);
        toBuilderMethodSubGenerator.generate(recordBuilder);
    }

    private boolean shouldGenerate() {
        return context.recordOptions().withBuilder() || context.getSpecialMethodAnnotations(TO_BUILDER).isPresent();
    }

    abstract static class MethodSubGenerator {
        protected final ProcessorContext context;

        MethodSubGenerator(ProcessorContext context) {
            this.context = context;
        }

        void generate(TypeSpec.Builder recordBuilder) {
            var recordBuilderName = recordBuilderName();
            var returnedClassName = ClassName.get(context.packageName(), recordBuilderName);

            var methodBuilder = methodBuilder(methodName())
                    .addModifiers(modifiers());

            annotations().ifPresent(methodBuilder::addAnnotations);

            var statementFormat = new StringBuilder();
            var statementArguments = new ArrayList<>();

            statementArguments.add(recordBuilderName);

            context.generics().ifPresentOrElse(
                    (types, names) -> {
                        statementFormat.append("return $L.")
                                .append(variablesStatement(names))
                                .append("$L(").append(methodArgument()).append(")");

                        statementArguments.addAll(types);

                        genericVariableConsumer().accept(methodBuilder, names);
                        methodBuilder.returns(ParameterizedTypeName.get(returnedClassName, types.toArray(TypeName[]::new)));
                    },
                    () -> {
                        statementFormat.append("return $L.$L(").append(methodArgument()).append(")");
                        methodBuilder.returns(returnedClassName);
                    });

            statementArguments.add(methodToCallName());

            context.memoization().ifPresent(
                    items -> items.forEach(item -> {
                        statementFormat.append("\n.$N($N)");

                        var type = item.type();
                        var memoizerType = MemoizerType.from(type);

                        statementArguments.add(item.getMemoizerName());
                        statementArguments.add(memoizerType.getConstructorStatement());
                    }));

            methodBuilder.addStatement(statementFormat.toString(), statementArguments.toArray());

            recordBuilder.addMethod(methodBuilder.build());
        }

        protected abstract String methodName();

        protected abstract Modifier[] modifiers();

        protected abstract Optional<List<AnnotationSpec>> annotations();

        protected abstract String methodArgument();

        private String recordBuilderName() {
            return context.recordName() + context.builderOptions().suffix();
        }

        protected abstract String methodToCallName();

        protected abstract BiConsumer<MethodSpec.Builder, List<TypeVariableName>> genericVariableConsumer();

        private String variablesStatement(List<TypeVariableName> variables) {
            return variables.stream()
                    .map(v -> "$T")
                    .collect(joining(", ", "<", ">"));
        }
    }
}
