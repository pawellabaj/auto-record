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

final class MethodHelper {
    private MethodHelper() {}

    static boolean hasNoParameters(ExecutableElement method, MessagerLogger logger) {
        if (!method.getParameters().isEmpty()) {
            logger.error("The interface has abstract method with parameters: %s".formatted(method.getSimpleName()));
        }
        return true;
    }

    static boolean doesNotReturnVoid(ExecutableElement method, MessagerLogger logger) {
        if (method.getReturnType().getKind() == VOID) {
            logger.error("The interface has abstract method returning void: %s".formatted(method.getSimpleName()));
        }
        return true;
    }

    static boolean isAbstract(ExecutableElement method) {
        return method.getModifiers().contains(ABSTRACT);
    }

    static boolean doesNotReturnPrimitive(ExecutableElement method) {
        return !method.getReturnType().getKind().isPrimitive();
    }

    static boolean returnsArray(ExecutableElement method) {
        return method.getReturnType().getKind() == ARRAY;
    }

    static boolean isSpecial(ExecutableElement method) {
        return SpecialMethod.isSpecial(method);
    }

    static boolean isAnnotatedWith(ExecutableElement method, Class<? extends Annotation> annotationClass) {
        return getAnnotation(method, annotationClass).isPresent();
    }

    static boolean isNotAnnotatedWith(ExecutableElement method, Class<? extends Annotation> annotationClass) {
        return getAnnotation(method, annotationClass).isEmpty();
    }
}
