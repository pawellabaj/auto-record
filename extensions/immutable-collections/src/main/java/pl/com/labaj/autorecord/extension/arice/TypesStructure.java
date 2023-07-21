package pl.com.labaj.autorecord.extension.arice;

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

import pl.com.labaj.autorecord.context.Logger;

import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static pl.com.labaj.autorecord.extension.arice.ProcessedType.OBJECT;
import static pl.com.labaj.autorecord.extension.arice.ProcessedType.allProcessedTypes;

final class TypesStructure {
    private final Map<ProcessedType, TreeSet<TypeMirror>> processedTypes;
    private final Map<ProcessedType, Boolean> additionalMethodNeeded;

    private TypesStructure(EnumMap<ProcessedType, TreeSet<TypeMirror>> processedTypes, Map<ProcessedType, Boolean> additionalMethodNeeded) {
        this.processedTypes = processedTypes;
        this.additionalMethodNeeded = additionalMethodNeeded;
    }

    String debugInfo() {
        return processedTypes.entrySet().stream()
                .map(item -> {
                    var types = item.getValue()
                            .stream()
                            .map(TypeMirror::toString)
                            .map(name -> substringAfterLast(name, "."))
                            .collect(joining(", ", "(", ")"));
                    var className = substringAfterLast(item.getKey().className(), ".");
                    var needsAdditionalMethod = additionalMethodNeeded.get(item.getKey());

                    return className + " : " + needsAdditionalMethod + " -> " + types;
                })
                .collect(joining("\n"));
    }

    boolean needsAdditionalMethod(ProcessedType processedType) {
        return additionalMethodNeeded.containsKey(processedType) && additionalMethodNeeded.get(processedType);
    }

    Set<TypeMirror> getImmutableTypes(ProcessedType pType) {
        return processedTypes.get(pType);
    }

    static class Builder {
        private final ExtensionContext extContext;
        private final Set<TypeMirror> immutableTypes;

        Builder(ExtensionContext extContext, Set<TypeMirror> immutableTypes) {
            this.extContext = extContext;
            this.immutableTypes = immutableTypes;
        }

        TypesStructure buildStructure(Logger logger) {
            var structure = allProcessedTypes().stream()
                    .collect(toMap(
                            identity(),
                            pType -> immutableTypes
                                    .stream()
                                    .filter(immutableType -> extContext.isSubtype(immutableType, pType, logger))
                                    .collect(toCollection(() -> new TreeSet<>(comparing(TypeMirror::toString)))),
                            (s1, s2) -> {
                                s1.addAll(s2);
                                return s1;
                            },
                            () -> new EnumMap<>(ProcessedType.class)
                    ));

            optimizeStructure(OBJECT, structure);

            var additionalMethodNeeded = new EnumMap<ProcessedType, Boolean>(ProcessedType.class);
            collectNeedsInfo(null, OBJECT, structure, additionalMethodNeeded);

            return new TypesStructure(structure, additionalMethodNeeded);
        }

        private Set<TypeMirror> optimizeStructure(ProcessedType pType, EnumMap<ProcessedType, TreeSet<TypeMirror>> structure) {
            var typeMirrors = structure.get(pType);

            if (pType.directSubTypes().isEmpty()) {
                return typeMirrors;
            }

            var toRemove = pType.directSubTypes().stream()
                    .map(subType -> optimizeStructure(subType, structure))
                    .flatMap(Collection::stream)
                    .collect(toSet());

            structure.get(pType).removeAll(toRemove);

            return Stream.concat(toRemove.stream(), typeMirrors.stream()).collect(toSet());
        }

        private boolean collectNeedsInfo(ProcessedType parent, ProcessedType pType,
                                         EnumMap<ProcessedType, TreeSet<TypeMirror>> structure,
                                         EnumMap<ProcessedType, Boolean> additionalMethodNeeded) {

            if (pType.directSubTypes().isEmpty()) {
                var result = !structure.get(pType).isEmpty();
                additionalMethodNeeded.put(pType, result);
                return result;
            }

            var needs = new AtomicBoolean();
            var subNeededInfo = pType.directSubTypes().stream()
                    .collect(toMap(
                            identity(),
                            subType -> collectNeedsInfo(pType, subType, structure, additionalMethodNeeded)
                    ));

            var someSubNeeds = subNeededInfo.values().stream()
                    .reduce(false, (b1, b2) -> b1 || b2);
            needs.set(someSubNeeds);

            if (!needs.get()) {
                needs.set(!nonNull(parent));
            }

            additionalMethodNeeded.put(pType, needs.get());
            return needs.get();
        }
    }
}
