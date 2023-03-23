package pl.com.labaj.autorecord.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.ClassUtils;
import pl.com.labaj.autorecord.Memoized;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.AnnotationsHelper.createAnnotationSpecs;
import static pl.com.labaj.autorecord.processor.MemoizerHelper.memoizerComputeMethodName;
import static pl.com.labaj.autorecord.processor.MethodHelper.doesNotReturnVoid;
import static pl.com.labaj.autorecord.processor.MethodHelper.hasNoParameters;
import static pl.com.labaj.autorecord.processor.MethodHelper.isAnnotatedWith;
import static pl.com.labaj.autorecord.processor.SpecialMethod.HASH_CODE;
import static pl.com.labaj.autorecord.processor.SpecialMethod.TO_STRING;
import static pl.com.labaj.autorecord.processor.SpecialMethod.specialMethods;

class MemoizedElementsGenerator {
    private final GeneratorParameters parameters;
    private final TypeSpec.Builder recordSpecBuilder;

    public MemoizedElementsGenerator(GeneratorParameters parameters, TypeSpec.Builder recordSpecBuilder) {
        this.parameters = parameters;
        this.recordSpecBuilder = recordSpecBuilder;
    }

    MemoizedElementsGenerator.WithMemoization createMemoization() {
        var memoizedSpecialMethods = new EnumMap<SpecialMethod, Boolean>(SpecialMethod.class);

        var items = new ArrayList<Memoization.Item>();

        var elementUtils = parameters.processingEnv().getElementUtils();
        var logger = parameters.logger();
        var sourceInterface = parameters.sourceInterface();

        var methods = elementUtils.getAllMembers(sourceInterface).stream()
                .filter(this::isMethod)
                .map(ExecutableElement.class::cast)
                .filter(method -> isAnnotatedWith(method, Memoized.class))
                .filter(method -> hasNoParameters(method, logger))
                .filter(method -> doesNotReturnVoid(method, logger))
                .toList();

        methods.stream()
                .filter(MethodHelper::isAbstract)
                .filter(MethodHelper::isSpecial)
                .peek(method -> memoizedSpecialMethods.put(SpecialMethod.from(method), true))
                .map(method -> toMemoizedItem(method, true))
                .forEach(items::add);

        methods.stream()
                .filter(not(MethodHelper::isAbstract))
                .filter(not(MethodHelper::isSpecial))
                .map(method -> toMemoizedItem(method, false))
                .forEach(items::add);

        specialMethods().stream()
                .filter(not(specialMethod -> memoizedSpecialMethods.computeIfAbsent(specialMethod, sm -> false)))
                .filter(specialMethod -> specialMethod.isMemoizedInOptions(parameters.recordOptions()))
                .peek(specialMethod -> memoizedSpecialMethods.put(specialMethod, true))
                .map(this::toMemoizedItem)
                .forEach(items::add);

        var memoization = new Memoization(items, memoizedSpecialMethods.get(HASH_CODE), memoizedSpecialMethods.get(TO_STRING));
        return new WithMemoization(memoization);
    }

    private boolean isMethod(Element element) {
        return element.getKind() == METHOD;
    }

    private Memoization.Item toMemoizedItem(ExecutableElement method, boolean special) {
        var methodName = method.getSimpleName().toString();
        var annotations = method.getAnnotationMirrors().stream()
                .map(AnnotationMirror.class::cast)
                .toList();
        try {
            return new Memoization.Item(ClassUtils.getClass(method.getReturnType().toString()),
                    methodName,
                    annotations,
                    method.getModifiers(),
                    special);
        } catch (ClassNotFoundException e) {
            parameters.logger().error("Cannot get %s class".formatted(methodName));
            return null;
        }
    }

    private Memoization.Item toMemoizedItem(SpecialMethod specialMethod) {
        return new Memoization.Item(specialMethod.type(), specialMethod.methodName(), emptyList(), Set.of(PUBLIC), true);
    }

    final class WithMemoization {
        private final Memoization memoization;

        private WithMemoization(Memoization memoization) {
            this.memoization = memoization;
        }

        WithMemoization createMemoizedMethods() {
            memoization.items().stream()
                    .map(this::toMethodSpec)
                    .forEach(recordSpecBuilder::addMethod);

            return this;
        }

        Memoization returnMemoization() {
            return memoization;
        }

        private MethodSpec toMethodSpec(Memoization.Item item) {
            var name = item.name();
            var annotations = createAnnotationSpecs(item.annotations(), ElementType.METHOD, List.of(Memoized.class, Override.class), emptyList());
            var modifiers = item.modifiers().stream()
                    .filter(modifer -> modifer != ABSTRACT)
                    .filter(modifier -> modifier != DEFAULT)
                    .toList();
            var supplierName = item.special() ? "_" + name : parameters.sourceInterface().getSimpleName() + ".super." + name;

            return MethodSpec.methodBuilder(name)
                    .addModifiers(modifiers)
                    .addAnnotations(annotations)
                    .returns(item.type())
                    .addStatement("return $L(() -> $L())", memoizerComputeMethodName(name), supplierName)
                    .build();
        }
    }
}
