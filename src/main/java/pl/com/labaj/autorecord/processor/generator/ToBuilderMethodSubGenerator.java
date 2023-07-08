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
import com.squareup.javapoet.TypeVariableName;
import pl.com.labaj.autorecord.processor.AutoRecordProcessorException;
import pl.com.labaj.autorecord.processor.context.GenerationContext;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static java.lang.annotation.ElementType.METHOD;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.context.SpecialMethod.TO_BUILDER;
import static pl.com.labaj.autorecord.processor.utils.Annotations.createAnnotationSpecs;

class ToBuilderMethodSubGenerator extends BuilderGenerator.MethodSubGenerator {
    @Override
    protected String methodName() {
        return "toBuilder";
    }

    @Override
    protected Modifier[] modifiers(GenerationContext context) {
        return context.specialMethods().containsKey(TO_BUILDER) || context.isRecordPublic() ? new Modifier[] {PUBLIC} : new Modifier[] {};
    }

    @Override
    protected Optional<List<AnnotationSpec>> annotations(GenerationContext context, ClassName returnedClassName, String recordBuilderName) {
        if (context.specialMethods().containsKey(TO_BUILDER)) {
            var parentMethod = context.specialMethods().get(TO_BUILDER);
            validateReturnedClass(context, parentMethod, returnedClassName, recordBuilderName);
            var annotationSpecs = createAnnotationSpecs(parentMethod.getAnnotationMirrors(), METHOD, List.of(Override.class), List.of());
            return Optional.of(annotationSpecs);
        }

        return Optional.empty();
    }

    @Override
    protected String methodArgument() {
        return "this";
    }

    @Override
    protected String methodToCallName(GenerationContext context) {
        return context.builderOptions().copyMethodName();
    }

    @Override
    protected BiConsumer<MethodSpec.Builder, List<TypeVariableName>> genericVariableConsumer() {
        return (b, l) -> {};
    }

    private void validateReturnedClass(GenerationContext context, ExecutableElement parentMethod, ClassName returnClassName, String recordBuilderName) {
        var returnType = parentMethod.getReturnType();
        var parentReturnClass = returnType.toString();

        if (parentReturnClass.equals("<any>")) {
            context.logger().warn("Cannot parse returned class of " + TO_BUILDER + " method");
            return;
        }

        var properReturnClass = returnClassName.toString();

        if (!(parentReturnClass.equals(properReturnClass) || parentReturnClass.equals(recordBuilderName))) {
            throw new AutoRecordProcessorException("Method " + TO_BUILDER + " has to return " + properReturnClass);
        }
    }
}
