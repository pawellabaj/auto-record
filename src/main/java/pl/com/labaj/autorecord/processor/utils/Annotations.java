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

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;

import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;

public final class Annotations {
    private Annotations() {}

    public static List<AnnotationSpec> createAnnotationSpecs(List<? extends AnnotationMirror> annotationMirrors, ElementType target) {
        return createAnnotationSpecs(annotationMirrors, target, emptyList(), emptyList());
    }

    public static List<AnnotationSpec> createAnnotationSpecs(List<? extends AnnotationMirror> annotationMirrors,
                                                             ElementType target,
                                                             List<Class<? extends Annotation>> annotationsToAdd,
                                                             List<Class<? extends Annotation>> annotationsToExclude) {
        var classNamesStream = annotationMirrors.stream()
                .map(AnnotationMirror::getAnnotationType)
                .map(DeclaredType::asElement)
                .map(TypeElement.class::cast)
                .filter(annotation -> canAnnotateElementType(annotation, target))
                .map(ClassName::get);
        var classNamesToAdd = annotationsToAdd.stream()
                .map(ClassName::get);
        var classNamesToExclude = annotationsToExclude.stream()
                .map(ClassName::get)
                .collect(toSet());

        return Stream.concat(classNamesStream, classNamesToAdd)
                .filter(not(classNamesToExclude::contains))
                .distinct()
                .map(AnnotationSpec::builder)
                .map(AnnotationSpec.Builder::build)
                .toList();
    }

    public static <A extends Annotation> Optional<A> getAnnotation(Element element, Class<A> annotationClass) {
        return Optional.ofNullable(element.getAnnotation(annotationClass));
    }
    public static <A extends Annotation> A createAnnotationIfNeeded(@Nullable A annotation, Class<A> annotationClass) {
        return createAnnotationIfNeeded(annotation, annotationClass, Map.of());
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A createAnnotationIfNeeded(@Nullable A annotation,
                                                                    Class<A> annotationClass,
                                                                    Map<String, Object> enforcedValues) {
        return (A) Proxy.newProxyInstance(
                annotationClass.getClassLoader(),
                new Class[] {annotationClass},
                (proxy, method, args) -> {
                    var methodName = method.getName();
                    if (enforcedValues.containsKey(methodName)) {
                        return enforcedValues.get(methodName);
                    }
                    return isNull(annotation) ? method.getDefaultValue() : method.invoke(annotation);
                });
    }

    private static boolean canAnnotateElementType(TypeElement annotation, ElementType targetType) {
        return getAnnotation(annotation, Target.class)
                .map(Target::value)
                .map(Arrays::stream)
                .map(elementTypes -> elementTypes.anyMatch(elementType -> elementType == targetType))
                .orElse(true);
    }
}
