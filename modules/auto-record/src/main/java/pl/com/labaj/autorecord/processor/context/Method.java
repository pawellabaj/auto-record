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

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.type.TypeKind.VOID;

public record Method(String name,
                     boolean isAbstract,
                     List<Parameter> parameters,
                     List<AnnotationMirror> annotations,
                     TypeMirror returnType) {
    @SuppressWarnings("unchecked")
    public static Method from(ExecutableElement method, Types typeUtils, TypeElement sourceInterface) {
        var name = method.getSimpleName().toString();
        var modifiers = method.getModifiers();
        var isAbstract = modifiers.contains(ABSTRACT);
        var returnType = findReturnType(method, typeUtils, sourceInterface);
        var parameters = findParameters(method, typeUtils, sourceInterface);
        var annotations = (List<AnnotationMirror>) method.getAnnotationMirrors();

        return new Method(name, isAbstract, parameters, annotations, returnType);
    }

    public boolean hasNoParameters() {
        return parameters().isEmpty();
    }

    public boolean isNotVoid() {
        return returnType.getKind() != VOID;
    }

    private static TypeMirror findReturnType(ExecutableElement method, Types typeUtils, TypeElement sourceInterface) {
        var methodAsMember = (ExecutableType) typeUtils.asMemberOf((DeclaredType) sourceInterface.asType(), method);

        return methodAsMember.getReturnType();
    }

    @SuppressWarnings("unchecked")
    private static List<Parameter> findParameters(ExecutableElement method, Types typeUtils, TypeElement sourceInterface) {
        var variableElements = method.getParameters();

        if (variableElements.isEmpty()) {
            return List.of();
        }

        var methodAsMember = (ExecutableType) typeUtils.asMemberOf((DeclaredType) sourceInterface.asType(), method);
        var variableTypes = methodAsMember.getParameterTypes();

        var parameters = new ArrayList<Parameter>();
        for (int i = 0; i < variableElements.size(); i++) {
            var variable = variableElements.get(i);

            var name = variable.getSimpleName().toString();
            var type = variableTypes.get(i);
            var annotations = (List<AnnotationMirror>) variable.getAnnotationMirrors();
            var parameter = new Parameter(name, type, annotations);

            parameters.add(parameter);
        }

        return List.copyOf(parameters);
    }

    public record Parameter(String name, TypeMirror type, List<AnnotationMirror> annotations) {}
}
