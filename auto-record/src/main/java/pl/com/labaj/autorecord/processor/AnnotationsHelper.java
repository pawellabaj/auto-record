package pl.com.labaj.autorecord.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;

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
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;

final class AnnotationsHelper {
    private AnnotationsHelper() {}

    static List<AnnotationSpec> createAnnotationSpecs(List<? extends AnnotationMirror> annotationMirrors, ElementType target) {
        return createAnnotationSpecs(annotationMirrors, target, emptyList(), emptyList());
    }

    static List<AnnotationSpec> createAnnotationSpecs(List<? extends AnnotationMirror> annotationMirrors,
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

    static <A extends Annotation> Optional<A> getAnnotation(Element element, Class<A> annotationClass) {
        return Optional.ofNullable(element.getAnnotation(annotationClass));
    }

    static <A extends Annotation> A getDefaultAnnotationIfNotPresent(Element element, Class<A> annotationClass) {
        return getAnnotation(element, annotationClass)
                .orElseGet(() -> getAnnotationWithDefaults(annotationClass));
    }

    private static boolean canAnnotateElementType(TypeElement annotation, ElementType targetType) {
        return getAnnotation(annotation, Target.class)
                .map(Target::value)
                .map(Arrays::stream)
                .map(elementTypes -> elementTypes.anyMatch(elementType -> elementType == targetType))
                .orElse(true);
    }

    @SuppressWarnings("unchecked")
    private static <A extends Annotation> A getAnnotationWithDefaults(Class<A> annotationClass) {
        return (A) Proxy.newProxyInstance(
                annotationClass.getClassLoader(),
                new Class[] {annotationClass},
                (proxy, method, args) -> method.getDefaultValue());
    }
}
