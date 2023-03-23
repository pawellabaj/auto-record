package pl.com.labaj.autorecord.processor;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.TypeParameterElement;
import java.util.List;

final class GenericHelper {
    private GenericHelper() {}

    static TypeName[] getGenericNames(List<? extends TypeParameterElement> typeParameters) {
        return typeParameters.stream()
                .map(Object::toString)
                .map(TypeVariableName::get)
                .toArray(TypeName[]::new);
    }

    static List<TypeVariableName> getGenericVariables(List<? extends TypeParameterElement> typeParameters) {
        return typeParameters.stream()
                .map(GenericHelper::toTypeVariableName)
                .toList();
    }

    private static TypeVariableName toTypeVariableName(TypeParameterElement typeParameterElement) {
        var name = typeParameterElement.asType().toString();
        var bounds = typeParameterElement.getBounds().stream()
                .map(TypeName::get)
                .toArray(TypeName[]::new);

        return TypeVariableName.get(name, bounds);
    }
}
