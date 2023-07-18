package pl.com.labaj.autorecord.extension.compact;

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

import com.squareup.javapoet.CodeBlock;
import pl.com.labaj.autorecord.context.Context;
import pl.com.labaj.autorecord.context.RecordComponent;
import pl.com.labaj.autorecord.context.StaticImports;
import pl.com.labaj.autorecord.extension.CompactConstructorExtension;

import javax.annotation.Nullable;
import javax.lang.model.type.DeclaredType;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.type.TypeKind.DECLARED;

public class ImmutableCollectionsExtension implements CompactConstructorExtension {
    private String[] classNames;
    private List<CollectionOrMapComponent> components;

    @Override
    public void setParameters(String[] parameters) {
        classNames = parameters;
    }

    @Override
    public boolean shouldGenerate(boolean isGeneratedByProcessor, Context context) {
        components = context.components()
                .stream()
                .filter(component -> component.type().getKind() == DECLARED)
                .map(component -> toCollectionOrMapComponent(context, component))
                .filter(Objects::nonNull)
                .toList();

        context.logger().debug(components.toString());

        return !components.isEmpty();
    }

    @Override
    public CodeBlock suffixCompactConstructorContent(Context context, StaticImports staticImports) {
        var immutableCollections = Arrays.stream(classNames)
                .map(className -> loadClass(context, className))
                .filter(Objects::nonNull)
                .collect(toSet());

        var codeBuilder = CodeBlock.builder();

        components.stream()
                .filter(component -> !immutableCollections.contains(component.componentClass))
                .forEach(comp -> {
                    var componentClass = comp.componentClass;

                    if (NavigableSet.class.isAssignableFrom(componentClass)) {
                        codeBuilder.addStatement("$1L = immutableNavigableSet($1L)", comp.component.name());
                        staticImports.add(ImmutableSets.class, "immutableNavigableSet");
                    } else if (SortedSet.class.isAssignableFrom(componentClass)) {
                        codeBuilder.addStatement("$1L = immutableSortedSet($1L)", comp.component.name());
                        staticImports.add(ImmutableSets.class, "immutableSortedSet");
                    } else if (LinkedHashSet.class.isAssignableFrom(componentClass)) {
                        codeBuilder.addStatement("$1L = immutableLinkedHashSet($1L)", comp.component.name());
                        staticImports.add(ImmutableSets.class, "immutableLinkedHashSet");
                    } else if (Set.class.isAssignableFrom(componentClass)) {
                        codeBuilder.addStatement("$1L = immutableSet($1L)", comp.component.name());
                        staticImports.add(ImmutableSets.class, "immutableSet");
                    } else {
                        codeBuilder.add("// " + comp.component.name() + "\n");
                    }
                });

        return codeBuilder.build();
    }

    private CollectionOrMapComponent toCollectionOrMapComponent(Context context, RecordComponent component) {
        var type = (DeclaredType) component.type();
        var packageName = type.asElement().getEnclosingElement().toString();
        var className = (packageName.isEmpty() ? "" : packageName + ".") + type.asElement().getSimpleName().toString();

        var aClass = loadClass(context, className);

        if (nonNull(aClass) && (Collection.class.isAssignableFrom(aClass) || Map.class.isAssignableFrom(aClass))) {
            return new CollectionOrMapComponent(component, aClass);
        }

        return null;
    }

    @Nullable
    private Class<?> loadClass(Context context, String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            context.logger().error("Cannot load " + className + "class", e);
            return null;
        }
    }

    private record CollectionOrMapComponent(RecordComponent component, Class<?> componentClass) {
        @Override
        public String toString() {
            return "CoMComp[" + component + ", " + componentClass + "]";
        }
    }
}
