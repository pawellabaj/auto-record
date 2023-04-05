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

import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.type.TypeKind.ARRAY;
import static javax.lang.model.type.TypeKind.VOID;
import static pl.com.labaj.autorecord.processor.AnnotationsHelper.getAnnotation;

record MethodHelper(ExecutableElement method) {

    String methodeName() {
        return method.getSimpleName().toString();
    }

    boolean hasParameters() {
        return !hasNoParameters();
    }

    public boolean hasNoParameters() {
        return method.getParameters().isEmpty();
    }

    boolean doesNotReturnVoid() {
        return !returnsVoid();
    }

    boolean returnsVoid() {
        return method.getReturnType().getKind() == VOID;
    }

    boolean isAbstract() {
        return method.getModifiers().contains(ABSTRACT);
    }

    boolean doesNotReturnPrimitive() {
        return !returnsPrimitive();
    }

    boolean returnsPrimitive() {
        return method.getReturnType().getKind().isPrimitive();
    }

    boolean returnsArray() {
        return method.getReturnType().getKind() == ARRAY;
    }

    boolean isSpecial() {
        return SpecialMethod.isSpecial(method);
    }

    public boolean isNotSpecial() {
        return !isSpecial();
    }

    boolean isAnnotatedWith(Class<? extends Annotation> annotationClass) {
        return getAnnotation(method, annotationClass).isPresent();
    }

    boolean isNotAnnotatedWith(Class<? extends Annotation> annotationClass) {
        return !isAnnotatedWith(annotationClass);
    }
}
