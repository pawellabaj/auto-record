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
import com.squareup.javapoet.TypeVariableName;
import pl.com.labaj.autorecord.processor.context.ProcessorContext;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static java.lang.annotation.ElementType.METHOD;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.context.ProcessorContext.TO_BUILDER;
import static pl.com.labaj.autorecord.processor.utils.Annotations.createAnnotationSpecs;

class ToBuilderMethodSubGenerator extends BuilderGenerator.MethodSubGenerator {

    @Override
    protected String methodName() {
        return TO_BUILDER.methodName();
    }

    @Override
    protected Modifier[] modifiers(ProcessorContext context) {
        return context.getSpecialMethodAnnotations(TO_BUILDER).isPresent() || context.isRecordPublic() ? new Modifier[] {PUBLIC} : new Modifier[] {};
    }

    @Override
    protected Optional<List<AnnotationSpec>> annotations(ProcessorContext context) {
        return context.getSpecialMethodAnnotations(TO_BUILDER)
                .map(annotations -> createAnnotationSpecs(annotations, METHOD, List.of(Override.class), List.of()));
    }

    @Override
    protected String methodArgument() {
        return "this";
    }

    @Override
    protected String methodToCallName(ProcessorContext context) {
        return context.builderOptions().copyMethodName();
    }

    @Override
    protected BiConsumer<MethodSpec.Builder, List<TypeVariableName>> genericVariableConsumer() {
        return (b, l) -> {};
    }
}
