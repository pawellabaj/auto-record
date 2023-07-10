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

import pl.com.labaj.autorecord.processor.context.SpecialMethod;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;

import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.type.TypeKind.ARRAY;
import static javax.lang.model.type.TypeKind.VOID;
import static pl.com.labaj.autorecord.processor.utils.Annotations.getAnnotation;

public final class Methods {
    private Methods() {}

    public static boolean isMethod(Element element) {
        return element.getKind() == METHOD;
    }

    public static boolean hasParameters(ExecutableElement method) {
        return !hasNoParameters(method);
    }

    public static boolean hasNoParameters(ExecutableElement method) {
        return method.getParameters().isEmpty();
    }

    public static boolean isNotVoid(ExecutableElement method) {
        return !isVoid(method);
    }

    public static boolean isVoid(ExecutableElement method) {
        return method.getReturnType().getKind() == VOID;
    }

    public static boolean isAbstract(ExecutableElement method) {
        return method.getModifiers().contains(ABSTRACT);
    }

    public static boolean doesNotReturnPrimitive(ExecutableElement method) {
        return !returnsPrimitive(method);
    }

    public static boolean returnsPrimitive(ExecutableElement method) {
        return method.getReturnType().getKind().isPrimitive();
    }

    public static boolean returnsArray(ExecutableElement method) {
        return method.getReturnType().getKind() == ARRAY;
    }

    public static boolean isSpecial(ExecutableElement method) {
        return SpecialMethod.isSpecial(method);
    }

    public static boolean isNotSpecial(ExecutableElement method) {
        return !isSpecial(method);
    }

    public static boolean isAnnotatedWith(ExecutableElement method, Class<? extends Annotation> annotationClass) {
        return getAnnotation(method, annotationClass).isPresent();
    }

    public static boolean isNotAnnotatedWith(ExecutableElement method, Class<? extends Annotation> annotationClass) {
        return !isAnnotatedWith(method, annotationClass);
    }
}
