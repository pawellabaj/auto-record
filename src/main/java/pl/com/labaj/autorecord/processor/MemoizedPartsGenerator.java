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

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.ClassUtils;
import pl.com.labaj.autorecord.Memoized;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.ElementType;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.AnnotationsHelper.createAnnotationSpecs;
import static pl.com.labaj.autorecord.processor.MemoizerHelper.memoizerComputeMethodName;
import static pl.com.labaj.autorecord.processor.SpecialMethod.HASH_CODE;
import static pl.com.labaj.autorecord.processor.SpecialMethod.TO_STRING;
import static pl.com.labaj.autorecord.processor.SpecialMethod.specialMethods;

class MemoizedPartsGenerator {
    private final GeneratorParameters parameters;
    private final TypeSpec.Builder recordSpecBuilder;

    public MemoizedPartsGenerator(GeneratorParameters parameters, TypeSpec.Builder recordSpecBuilder) {
        this.parameters = parameters;
        this.recordSpecBuilder = recordSpecBuilder;
    }

    MemoizedPartsGenerator.WithMemoization createMemoization() {

        var elementUtils = parameters.processingEnv().getElementUtils();
        var sourceInterface = parameters.sourceInterface();

        var items = new LinkedHashSet<Memoization.Item>();

        specialMethods().stream()
                .filter(specialMethod -> specialMethod.isMemoizedInOptions(parameters.recordOptions()))
                .map(this::toMemoizedItem)
                .forEach(items::add);

        elementUtils.getAllMembers(sourceInterface).stream()
                .filter(this::isMethod)
                .map(ExecutableElement.class::cast)
                .map(MethodHelper::new)
                .filter(helper -> helper.isAnnotatedWith(Memoized.class))
                .filter(MethodHelper::hasNoParameters)
                .filter(MethodHelper::doesNotReturnVoid)
                .map(helper -> toMemoizedItem(helper.method(), helper.isSpecial()))
                .forEach(items::add);

        var memoizedHashCode = items.stream()
                .anyMatch(item -> item.name().equals(HASH_CODE.methodName()));
        var memoizedToString = items.stream()
                .anyMatch(item -> item.name().equals(TO_STRING.methodName()));

        var memoization = new Memoization(items, memoizedHashCode, memoizedToString);
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
