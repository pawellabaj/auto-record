package pl.com.labaj.autorecord.processor.context;

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

import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import pl.com.labaj.autorecord.processor.AutoRecordProcessorException;
import pl.com.labaj.autorecord.processor.utils.Methods;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static pl.com.labaj.autorecord.processor.context.ProcessorContext.TO_BUILDER;

final class SpecialMethodsFinder {

    private static final Set<MethodDefinition> SPECIAL_METHODS = Set.of(TO_BUILDER);

    SpecialMethodsFinder() {}

    Map<MethodDefinition, List<AnnotationMirror>> findSpecialMethods(List<ExecutableElement> allMethods) {
        record Pair(MethodDefinition specialMethod, List<AnnotationMirror> annotations) {}

        return allMethods.stream()
                .filter(Methods::isAbstract)
                .filter(InternalMethod::isNotInternal)
                .map(method -> {
                    var methodDefinition = toMethodDefinition(method);
                    var annotations = method.getAnnotationMirrors().stream()
                            .map(AnnotationMirror.class::cast)
                            .toList();

                    return new Pair(methodDefinition, annotations);
                })
                .filter(pair -> SPECIAL_METHODS.contains(pair.specialMethod))
                .collect(toMap(Pair::specialMethod, Pair::annotations));
    }

    boolean isSpecial(ExecutableElement method) {
        var specialMethod = toMethodDefinition(method);
        return SPECIAL_METHODS.contains(specialMethod);
    }

    boolean isNotSpecial(ExecutableElement method) {
        return !isSpecial(method);
    }

    private MethodDefinition toMethodDefinition(ExecutableElement method) {
        var methodName = method.getSimpleName().toString();
        var rawClasses = method.getParameters().stream()
                .map(VariableElement::asType)
                .map(TypeName::get)
                .map(this::forName)
                .toList();

        return new MethodDefinition(methodName, rawClasses);
    }

    @SuppressWarnings("rawtypes")
    private Class forName(TypeName typeName) {
        var className = typeName instanceof ParameterizedTypeName parameterizedTypeName ? parameterizedTypeName.rawType.toString() : typeName.toString();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new AutoRecordProcessorException("Cannot find class", e);
        }
    }
}
