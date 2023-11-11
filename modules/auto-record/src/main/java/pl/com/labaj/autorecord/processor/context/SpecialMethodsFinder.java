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

import javax.lang.model.element.AnnotationMirror;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static pl.com.labaj.autorecord.processor.context.ProcessorContext.TO_BUILDER;

final class SpecialMethodsFinder {

    private static final Set<MethodDefinition> SPECIAL_METHODS = Set.of(TO_BUILDER);

    SpecialMethodsFinder() {}

    Map<MethodDefinition, List<AnnotationMirror>> findSpecialMethods(List<Method> allMethods) {
        record MethodWithAnnotations(MethodDefinition specialMethod, List<AnnotationMirror> annotations) {}

        return allMethods.stream()
                .filter(Method::isAbstract)
                .filter(InternalMethod::isNotInternal)
                .map(method -> {
                    var methodDefinition = toMethodDefinition(method);

                    return new MethodWithAnnotations(methodDefinition, method.annotations());
                })
                .filter(methodWithAnnotations -> SPECIAL_METHODS.contains(methodWithAnnotations.specialMethod))
                .collect(toMap(MethodWithAnnotations::specialMethod, MethodWithAnnotations::annotations));
    }

    boolean isSpecial(Method method) {
        var specialMethod = toMethodDefinition(method);
        return SPECIAL_METHODS.contains(specialMethod);
    }

    boolean isNotSpecial(Method method) {
        return !isSpecial(method);
    }

    private MethodDefinition toMethodDefinition(Method method) {
        var rawClasses = method.parameters().stream()
                .map(Method.Parameter::type)
                .map(TypeName::get)
                .map(this::toClassName)
                .toList();

        return new MethodDefinition(method.name(), rawClasses);
    }

    private String toClassName(TypeName typeName) {
        return typeName instanceof ParameterizedTypeName parameterizedTypeName ? parameterizedTypeName.rawType.toString() : typeName.toString();
    }
}
