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

import pl.com.labaj.autorecord.context.RecordComponent;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;

import static javax.lang.model.type.TypeKind.ARRAY;

record ProcessorRecordComponent(TypeMirror type, String name, List<AnnotationMirror> annotations) implements RecordComponent {

    @Override
    public boolean isPrimitive() {
        return type.getKind().isPrimitive();
    }

    @Override
    public boolean isArray() {
        return type.getKind() == ARRAY;
    }

    @Override
    public boolean isAnnotatedWith(Class<? extends Annotation> annotationClass) {
        var annotationClassName = annotationClass.getName();

        return annotations.stream()
                .map(this::getQualifiedClassName)
                .anyMatch(qualifiedName -> qualifiedName.contentEquals(annotationClassName));
    }

    private Name getQualifiedClassName(AnnotationMirror annotation) {
        var annotationType = annotation.getAnnotationType();
        var typeElement = (TypeElement) annotationType.asElement();

        return typeElement.getQualifiedName();
    }
}
