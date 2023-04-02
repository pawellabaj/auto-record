package pl.com.labaj.autorecord.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import pl.com.labaj.autorecord.Ignored;

import javax.lang.model.element.ExecutableElement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.INT;
import static com.squareup.javapoet.TypeName.OBJECT;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.MethodHelper.isNotAnnotatedWith;
import static pl.com.labaj.autorecord.processor.MethodHelper.returnsArray;

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

    WithNotIgnoredProperties findNotIgnoredProperties() {
        var notIgnoredProperties = parameters.propertyMethods().stream()
                .filter(method -> isNotAnnotatedWith(method, Ignored.class))
                .toList();
        return new WithNotIgnoredProperties(notIgnoredProperties);
    }

    final class WithNotIgnoredProperties {
        private final List<ExecutableElement> notIgnoredProperties;
        private final boolean memoizedHashCode;
        private final boolean hasArrayComponents;
        private final boolean hasIgnoredComponents;

        private WithNotIgnoredProperties(List<ExecutableElement> notIgnoredProperties) {
            this.notIgnoredProperties = notIgnoredProperties;

            memoizedHashCode = memoization.memoizedHashCode();
            hasIgnoredComponents = notIgnoredProperties.size() < parameters.propertyMethods().size();
            hasArrayComponents = notIgnoredProperties.stream().anyMatch(MethodHelper::returnsArray);
        }

        WithNotIgnoredProperties createHashCodeMethod() {
            if (!memoizedHashCode && !hasIgnoredComponents && !hasArrayComponents) {
                return this;
            }

            var methodName = (memoizedHashCode ? "_" : "") + "hashCode";
            var hashCodeFormat = notIgnoredProperties.stream()
                    .map(method -> returnsArray(method) ? "$T.hashCode($N)" : "$N")
                    .collect(joining(", ", "return " + OBJECTS_HASH + "(", ")"));
            var arguments = notIgnoredProperties.stream()
                    .flatMap(this::getHashCodeArguments)
                    .toArray();
            var hashCodeMethodBuilder = MethodSpec.methodBuilder(methodName)
                    .addModifiers(memoizedHashCode ? PRIVATE : PUBLIC)
                    .returns(INT)
                    .addStatement(hashCodeFormat, arguments);

            if (!memoizedHashCode) {
                hashCodeMethodBuilder.addAnnotation(Override.class);
            }

            parameters.staticImports().add(new StaticImport(Objects.class, OBJECTS_HASH));

            recordSpecBuilder.addMethod(hashCodeMethodBuilder.build());

            return this;
        }

        private Stream<?> getHashCodeArguments(ExecutableElement method) {
            return returnsArray(method) ? Stream.of(Arrays.class, method.getSimpleName()) : Stream.of(method.getSimpleName());
        }

        @SuppressWarnings({"UnusedReturnValue", "java:S1192"})
        WithNotIgnoredProperties createEqualsMethod() {
            if (!memoizedHashCode && !hasIgnoredComponents && !hasArrayComponents) {
                return this;
            }

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

            var equalsFormat = notIgnoredProperties.stream()
                    .map(method -> "$T.equals($N, %s.$N)".formatted(OTHER_RECORD))
                    .collect(joining("\n&& ", "return ", ""));
            var arguments = notIgnoredProperties.stream()
                    .flatMap(this::getToStringArguments)
                    .toArray();
            var equalsMethod = equalsMethodBuilder
                    .addCode("\n")
                    .addStatement("var $L = ($L) $L", OTHER_RECORD, recordName, OTHER)
                    .addStatement(equalsFormat, arguments)
                    .build();
            recordSpecBuilder.addMethod(equalsMethod);

            return this;
        }

        private Stream<?> getToStringArguments(ExecutableElement method) {
            var methodName = method.getSimpleName();

            return returnsArray(method) ?
                    Stream.of(Arrays.class, methodName, methodName) :
                    Stream.of(Objects.class, methodName, methodName);
        }
    }
}
