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
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
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

    public static List<AnnotationSpec> createParameterAnnotationSpecs(List<AnnotationMirror> annotations) {
        return annotations.stream()
                .map(annotationMirror -> AnnotationDetails.toAnnotationDetails(annotationMirror, Set.of(ElementType.PARAMETER)))
                .filter(Objects::nonNull)
                .map(AnnotationDetails::toAnnotationSpec)
                .toList();
    }

    public static List<AnnotationSpec> createAnnotationSpecs(List<AnnotationMirror> annotationMirrors,
                                                             Set<ElementType> targets,
                                                             List<Class<? extends Annotation>> annotationsToAdd,
                                                             List<Class<? extends Annotation>> annotationsToExclude) {
        var parentAnnotationDetails = annotationMirrors.stream()
                .map(annotationMirror -> AnnotationDetails.toAnnotationDetails(annotationMirror, targets))
                .filter(Objects::nonNull);
        var annotationDetailsToAdd = annotationsToAdd.stream()
                .map(ClassName::get)
                .map(className -> new AnnotationDetails(className, Map.of()));

        var classNamesToExclude = annotationsToExclude.stream()
                .map(ClassName::get)
                .collect(toSet());

        return Stream.concat(parentAnnotationDetails, annotationDetailsToAdd)
                .filter(annotationDetails -> !classNamesToExclude.contains(annotationDetails.className()))
                .distinct()
                .map(AnnotationDetails::toAnnotationSpec)
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

    public static List<AnnotationMirror> annotationsAllowedFor(List<? extends AnnotationMirror> annotationMirrors, Set<ElementType> targets) {
        return annotationMirrors.stream()
                .map(AnnotationMirror.class::cast)
                .filter(annotation -> {
                    var annotationType = annotation.getAnnotationType();
                    var annotationTypeElement = (TypeElement) annotationType.asElement();

                    return canAnnotateElementType(annotationTypeElement, targets);
                })
                .toList();
    }

    private static boolean canAnnotateElementType(TypeElement annotation, Set<ElementType> targets) {
        return getAnnotation(annotation, Target.class)
                .map(Target::value)
                .map(Arrays::stream)
                .map(elementTypes -> elementTypes.anyMatch(targets::contains))
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

                        record Member(String name, CodeBlock value) {}
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
                        throw new AutoRecordProcessorException("Cannot merge annotations", e);
                    }
                })
                .toList();
    }

    private record AnnotationDetails(ClassName className, Map<String, AnnotationValue> values) {
        @Nullable
        private static AnnotationDetails toAnnotationDetails(AnnotationMirror annotationMirror, Set<ElementType> targets) {
            var annotationType = annotationMirror.getAnnotationType();
            var typeElement = (TypeElement) annotationType.asElement();

            if (!canAnnotateElementType(typeElement, targets)) {
                return null;
            }

            var className = ClassName.get(typeElement);

            var values = annotationMirror.getElementValues().entrySet().stream()
                    .collect(toMap(
                            entry -> entry.getKey().getSimpleName().toString(),
                            entry -> (AnnotationValue) entry.getValue()
                    ));

            return new AnnotationDetails(className, values);
        }

        private AnnotationSpec toAnnotationSpec() {
            var annotationBuilder = AnnotationSpec.builder(className);
            values.forEach((name, value) -> annotationBuilder.addMember(name, "$L", value.toString()));

            return annotationBuilder.build();
        }
    }
}
