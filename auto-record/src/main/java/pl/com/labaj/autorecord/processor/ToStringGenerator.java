package pl.com.labaj.autorecord.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static javax.lang.model.element.Modifier.PRIVATE;

class ToStringGenerator {
    private final GeneratorParameters parameters;
    private final TypeSpec.Builder recordSpecBuilder;
    private final List<ParameterSpec> recordComponents;

    ToStringGenerator(GeneratorParameters parameters, TypeSpec.Builder recordSpecBuilder, List<ParameterSpec> recordComponents) {
        this.parameters = parameters;
        this.recordSpecBuilder = recordSpecBuilder;
        this.recordComponents = recordComponents;
    }

    @SuppressWarnings("UnusedReturnValue")
    ToStringGenerator createToStringMethod() {
        var toStringFormat = IntStream.rangeClosed(1, recordComponents.size())
                .mapToObj("$%dN"::formatted)
                .map("\"%1$s = \" + %1$s"::formatted)
                .collect(joining(" + \", \" +\n", "return \"" + parameters.recordName() + "[\" +\n", " +\n\"]\""));

        var toStringMethod = MethodSpec.methodBuilder("_toString")
                .addModifiers(PRIVATE)
                .returns(String.class)
                .addStatement(toStringFormat, recordComponents.toArray())
                .build();

        recordSpecBuilder.addMethod(toStringMethod);

        return this;
    }
}
