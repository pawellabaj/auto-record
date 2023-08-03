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
import com.squareup.javapoet.CodeBlock;
import org.apiguardian.api.API;
import pl.com.labaj.autorecord.processor.AutoRecordProcessorException;

import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = INTERNAL)
public final class Annotations {
    private Annotations() {}

    public static <A extends Annotation> Optional<A> getAnnotation(Element element, Class<A> annotationClass) {
        return Optional.ofNullable(element.getAnnotation(annotationClass));
    }

    public static <A extends Annotation> List<A> getAnnotations(TypeElement element, Class<A> annotationClass) {
        return List.of(element.getAnnotationsByType(annotationClass));
    }

    public static List<AnnotationSpec> createAnnotationSpecs(List<AnnotationMirror> annotations) {
        return annotations.stream()
                .map(AnnotationMirror::getAnnotationType)
                .map(DeclaredType::asElement)
                .map(TypeElement.class::cast)
                .map(ClassName::get)
                .map(AnnotationSpec::builder)
                .map(AnnotationSpec.Builder::build)
                .toList();
    }

    public static List<AnnotationSpec> createAnnotationSpecs(List<AnnotationMirror> annotationMirrors,
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

    public static List<AnnotationMirror> annotationsAllowedFor(List<? extends AnnotationMirror> annotationMirrors, ElementType target) {
        return annotationMirrors.stream()
                .map(AnnotationMirror.class::cast)
                .filter(annotation -> {
                    var annotationType = annotation.getAnnotationType();
                    var annotationTypeElement = (TypeElement) annotationType.asElement();

                    return canAnnotateElementType(annotationTypeElement, target);
                })
                .toList();
    }

    private static boolean canAnnotateElementType(TypeElement annotation, ElementType target) {
        return getAnnotation(annotation, Target.class)
                .map(Target::value)
                .map(Arrays::stream)
                .map(elementTypes -> elementTypes.anyMatch(elementType -> elementType == target))
                .orElse(true);
    }

    public static List<AnnotationSpec> merge(List<AnnotationSpec> annotations1, List<AnnotationSpec> annotations2) {
        var groupedBy = Stream.concat(annotations1.stream(), annotations2.stream())
                .collect(groupingBy(
                        annotationSpec -> annotationSpec.type.toString()

                ));

        return groupedBy.entrySet().stream()
                .flatMap(entry -> {
                    try {
                        var annotationsClassName = entry.getKey();
                        Class<?> annotationlass = Class.forName(annotationsClassName);
                        var repetable = annotationlass.getAnnotation(Repeatable.class);
                        if (nonNull(repetable)) {
                            return entry.getValue().stream();
                        }

                        var annotationBuilder = AnnotationSpec.builder(annotationlass);

                        record Member(String name, CodeBlock value){}
                        var members = entry.getValue().stream()
                                .map(annotationSpec -> annotationSpec.members)
                                .map(Map::entrySet)
                                .flatMap(Collection::stream)
                                .flatMap(en -> en.getValue()
                                        .stream()
                                        .map(value -> new Member(en.getKey(), value)))
                                .collect(groupingBy(Member::name, toSet()));

                        members.forEach((k, v) -> v.forEach(value -> annotationBuilder.addMember(k, value.value)));

                        return Stream.of(annotationBuilder.build());
                    } catch (ClassNotFoundException e) {
                        throw new AutoRecordProcessorException("Cannot merge annptations", e);
                    }
                })
                .toList();
    }
}
