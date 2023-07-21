package pl.com.labaj.autorecord.processor.context;

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

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.TypeParameterElement;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Generics {
    private final List<TypeVariableName> names;
    private final List<TypeVariableName> types;
    private final List<TypeParameterElement> typeParameters;

    Generics(List<TypeParameterElement> typeParameters) {
        this.typeParameters = typeParameters;

        names = getGenericVariableNames(typeParameters);
        types = getGenericTypeNames(typeParameters);
    }

    public void ifPresent(Consumer<List<TypeVariableName>> namesConsumer) {
        if (!typeParameters.isEmpty()) {
            namesConsumer.accept(names);
        }
    }

    public void ifPresentOrElse(BiConsumer<List<TypeVariableName>, List<TypeVariableName>> genericsConsumer, Runnable emptyAction) {
        if (!typeParameters.isEmpty()) {
            genericsConsumer.accept(types, names);
        } else {
            emptyAction.run();
        }
    }

    private List<TypeVariableName> getGenericVariableNames(List<? extends TypeParameterElement> typeParameters) {
        return typeParameters.stream()
                .map(this::toVariableName)
                .toList();
    }

    private TypeVariableName toVariableName(TypeParameterElement typeParameterElement) {
        var name = typeParameterElement.asType().toString();
        var bounds = typeParameterElement.getBounds().stream()
                .map(TypeName::get)
                .toArray(TypeName[]::new);

        return TypeVariableName.get(name, bounds);
    }

    private List<TypeVariableName> getGenericTypeNames(List<? extends TypeParameterElement> typeParameters) {
        return typeParameters.stream()
                .map(Object::toString)
                .map(TypeVariableName::get)
                .toList();
    }
}
