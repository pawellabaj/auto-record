package pl.com.labaj.autorecord.processor.utils;

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

import pl.com.labaj.autorecord.processor.memoization.Memoization;
import pl.com.labaj.autorecord.processor.special.SpecialMethod;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.type.TypeKind.ARRAY;
import static javax.lang.model.type.TypeKind.VOID;
import static pl.com.labaj.autorecord.processor.utils.Annotations.getAnnotation;

public record Method(ExecutableElement method) {

    public String methodeName() {
        return method.getSimpleName().toString();
    }

    public boolean hasParameters() {
        return !hasNoParameters();
    }

    public boolean hasNoParameters() {
        return method.getParameters().isEmpty();
    }

    public boolean doesNotReturnVoid() {
        return !returnsVoid();
    }

    public boolean returnsVoid() {
        return method.getReturnType().getKind() == VOID;
    }

    public boolean isAbstract() {
        return method.getModifiers().contains(ABSTRACT);
    }

    public boolean doesNotReturnPrimitive() {
        return !returnsPrimitive();
    }

    public boolean returnsPrimitive() {
        return method.getReturnType().getKind().isPrimitive();
    }

    public boolean returnsArray() {
        return method.getReturnType().getKind() == ARRAY;
    }

    public boolean isSpecial() {
        return SpecialMethod.isSpecial(method);
    }

    public boolean isNotSpecial() {
        return !isSpecial();
    }

    public boolean isAnnotatedWith(Class<? extends Annotation> annotationClass) {
        return getAnnotation(method, annotationClass).isPresent();
    }

    public boolean isNotAnnotatedWith(Class<? extends Annotation> annotationClass) {
        return !isAnnotatedWith(annotationClass);
    }

    public Memoization.Item getToMemoizedItem() {
        var annotations = method.getAnnotationMirrors().stream()
                .map(AnnotationMirror.class::cast)
                .toList();

        return new Memoization.Item(method.getReturnType(),
                methodeName(),
                annotations,
                method.getModifiers(),
                isSpecial());
    }
}
