package pl.com.labaj.autorecord.context;

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
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;

import static javax.lang.model.type.TypeKind.ARRAY;

/**
 * @param type
 * @param name
 * @param annotations
 * @since 2.1.0
 */
public record RecordComponent(TypeMirror type, String name, List<AnnotationMirror> annotations) {

    public boolean isNotPrimitive() {
        return !type.getKind().isPrimitive();
    }

    public boolean isArray() {
        return type.getKind() == ARRAY;
    }

    public boolean isAnnotatedWith(Class<? extends Annotation> annotationClass) {
        var annotationClassName = annotationClass.getName();

        return annotations.stream()
                .map(this::getQualifiedClassName)
                .anyMatch(qualifiedName -> qualifiedName.contentEquals(annotationClassName));
    }

    public boolean isNotAnnotatedWith(Class<? extends Annotation> annotationClass) {
        return !isAnnotatedWith(annotationClass);
    }

    private Name getQualifiedClassName(AnnotationMirror annotation) {
        var annotationType = annotation.getAnnotationType();
        var typeElement = (TypeElement) annotationType.asElement();

        return typeElement.getQualifiedName();
    }
}
