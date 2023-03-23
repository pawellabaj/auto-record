package pl.com.labaj.autorecord.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import pl.com.labaj.autorecord.Ignored;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.INT;
import static com.squareup.javapoet.TypeName.OBJECT;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.MethodHelper.isNotAnnotatedWith;

class HashCodeEqualsGenerator {

    private static final String OBJECTS_HASH = "hash";
    private static final String OTHER = "other";
    private static final String OTHER_RECORD = "otherRecord";
    private final GeneratorParameters parameters;
    private final TypeSpec.Builder recordSpecBuilder;
    private final Memoization memoization;

    HashCodeEqualsGenerator(GeneratorParameters parameters, TypeSpec.Builder recordSpecBuilder, Memoization memoization) {
        this.parameters = parameters;
        this.recordSpecBuilder = recordSpecBuilder;
        this.memoization = memoization;
    }

    WithNotIgnoredNames findNotIgnoredNames() {
        var notIgnoredNames = parameters.propertyMethods().stream()
                .filter(method -> isNotAnnotatedWith(method, Ignored.class))
                .map(ExecutableElement::getSimpleName)
                .toList();
        return new WithNotIgnoredNames(notIgnoredNames);
    }

    final class WithNotIgnoredNames {
        private final List<Name> notIgnoredNames;
        private final boolean memoizedHashCode;
        private final boolean allComponentsRequired;

        private WithNotIgnoredNames(List<Name> notIgnoredNames) {
            this.notIgnoredNames = notIgnoredNames;
            memoizedHashCode = memoization.memoizedHashCode();
            allComponentsRequired = notIgnoredNames.size() == parameters.propertyMethods().size();
        }

        WithNotIgnoredNames createHashCodeMethod() {
            if (!memoizedHashCode && allComponentsRequired) {
                return this;
            }

            var hashCodeFormat = notIgnoredNames.stream()
                    .map(name -> "$N")
                    .collect(joining(", ", "return " + OBJECTS_HASH + "(", ")"));

            var methodName = (memoizedHashCode ? "_" : "") + "hashCode";
            var hashCodeMethodBuilder = MethodSpec.methodBuilder(methodName)
                    .addModifiers(memoizedHashCode ? PRIVATE : PUBLIC)
                    .returns(INT)
                    .addStatement(hashCodeFormat, notIgnoredNames.toArray());

            if (!memoizedHashCode) {
                hashCodeMethodBuilder.addAnnotation(Override.class);
            }

            parameters.staticImports().add(new StaticImport(Objects.class, OBJECTS_HASH));

            recordSpecBuilder.addMethod(hashCodeMethodBuilder.build());

            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        WithNotIgnoredNames createEqualsMethod() {
            if (!memoizedHashCode && allComponentsRequired) {
                return this;
            }

            var equalsFormat = IntStream.rangeClosed(1, notIgnoredNames.size())
                    .mapToObj("$%dN"::formatted)
                    .map(name -> "java.util.Objects.equals(%1$s, %2$s.%1$s)".formatted(name, OTHER_RECORD))
                    .collect(joining("\n&& ", "return ", ""));

            var recordName = parameters.recordName();
            var equalsMethodBuilder = MethodSpec.methodBuilder("equals")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(BOOLEAN)
                    .addParameter(OBJECT, OTHER)
                    .beginControlFlow("if (this == $L)", OTHER)
                    .addStatement("return true")
                    .endControlFlow()
                    .beginControlFlow("if ($L == null)", OTHER)
                    .addStatement("return false")
                    .endControlFlow()
                    .beginControlFlow("if (!($L instanceof $L))", OTHER, recordName)
                    .addStatement("return false")
                    .endControlFlow();

            if (memoizedHashCode) {
                equalsMethodBuilder
                        .beginControlFlow("if (hashCode() != $L.hashCode())", OTHER)
                        .addStatement("return false")
                        .endControlFlow();
            }

            var equalsMethod = equalsMethodBuilder
                    .addCode("\n")
                    .addStatement("var $L = ($L) $L", OTHER_RECORD, recordName, OTHER)
                    .addStatement(equalsFormat, notIgnoredNames.toArray())
                    .build();
            recordSpecBuilder.addMethod(equalsMethod);

            return this;
        }
    }
}
