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

import com.squareup.javapoet.ClassName;
import pl.com.labaj.autorecord.context.Logger;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static pl.com.labaj.autorecord.extension.arice.InterfaceType.OBJECT;
import static pl.com.labaj.autorecord.extension.arice.InterfaceType.allProcessedTypes;

final class TypesStructure {
    private final EnumMap<InterfaceType, Set<ClassName>> classNames;
    private final Map<InterfaceType, Boolean> additionalMethodNeeded;

    private TypesStructure(EnumMap<InterfaceType, Set<ClassName>> classNames, Map<InterfaceType, Boolean> additionalMethodNeeded) {
        this.classNames = classNames;
        this.additionalMethodNeeded = additionalMethodNeeded;
    }

    String debugInfo() {
        return classNames.entrySet().stream()
                .map(item -> {
                    var types = item.getValue()
                            .stream()
                            .map(ClassName::simpleName)
                            .collect(joining(", ", "(", ")"));
                    var className = substringAfterLast(item.getKey().className(), ".");
                    var needsAdditionalMethod = additionalMethodNeeded.get(item.getKey());

                    return className + " : " + needsAdditionalMethod + " -> " + types;
                })
                .collect(joining("\n"));
    }

    boolean needsAdditionalMethod(InterfaceType iType) {
        return additionalMethodNeeded.containsKey(iType) && additionalMethodNeeded.get(iType);
    }

    Set<ClassName> getClassNames(InterfaceType iType) {
        return classNames.get(iType);
    }

    static class Builder {
        private final ExtensionContext extContext;
        private final Set<String> immutableNames;

        Builder(ExtensionContext extContext, Set<String> immutableNames) {
            this.extContext = extContext;
            this.immutableNames = immutableNames;
        }

        TypesStructure buildStructure(Logger logger) {
            var classNames = allProcessedTypes().stream()
                    .collect(toMap(
                            identity(),
                            pType -> collectClassNames(pType, logger),
                            (s1, s2) -> {
                                s1.addAll(s2);
                                return s1;
                            },
                            () -> new EnumMap<>(InterfaceType.class)
                    ));

            optimizeStructure(OBJECT, classNames);

            var additionalMethodNeeded = new EnumMap<InterfaceType, Boolean>(InterfaceType.class);
            collectNeedsInfo(null, OBJECT, classNames, additionalMethodNeeded);

            return new TypesStructure(classNames, additionalMethodNeeded);
        }

        private Set<ClassName> collectClassNames(InterfaceType iType, Logger logger) {
            var classNames = immutableNames
                    .stream()
                    .map(immutableName -> extContext.getImmutableType(immutableName, logger))
                    .filter(Objects::nonNull)
                    .filter(type -> extContext.isSubtype(type, iType, logger))
                    .map(ClassName::get)
                    .map(ClassName.class::cast)
                    .collect(toCollection(() -> new TreeSet<>(comparing(ClassName::canonicalName))));

            if (iType == OBJECT) {
                immutableNames.stream()
                        .filter(immutableName -> isNull(extContext.getImmutableType(immutableName, logger)))
                        .map(ClassName::bestGuess)
                        .forEach(className -> {
                            logger.debug("Cannot get TypeMirror for " + className + " so added to Object");
                            classNames.add(className);
                        });
            }

            return classNames;
        }

        private Set<ClassName> optimizeStructure(InterfaceType iType, EnumMap<InterfaceType, Set<ClassName>> classNames) {
            var typeClassNames = classNames.get(iType);

            if (iType.directSubTypes().isEmpty()) {
                return typeClassNames;
            }

            var toRemove = iType.directSubTypes().stream()
                    .map(subType -> optimizeStructure(subType, classNames))
                    .flatMap(Collection::stream)
                    .collect(toSet());

            classNames.get(iType).removeAll(toRemove);

            return Stream.concat(toRemove.stream(), typeClassNames.stream()).collect(toSet());
        }

        private boolean collectNeedsInfo(InterfaceType parent, InterfaceType iType,
                                         EnumMap<InterfaceType, Set<ClassName>> classNames,
                                         EnumMap<InterfaceType, Boolean> additionalMethodNeeded) {

            if (iType.directSubTypes().isEmpty()) {
                var result = !classNames.get(iType).isEmpty();
                additionalMethodNeeded.put(iType, result);
                return result;
            }

            var needs = new AtomicBoolean();
            var subNeededInfo = iType.directSubTypes().stream()
                    .collect(toMap(
                            identity(),
                            subType -> collectNeedsInfo(iType, subType, classNames, additionalMethodNeeded)
                    ));

            var someSubNeeds = subNeededInfo.values().stream()
                    .reduce(false, (b1, b2) -> b1 || b2);
            needs.set(someSubNeeds);

            if (!needs.get()) {
                needs.set(!nonNull(parent));
            }

            additionalMethodNeeded.put(iType, needs.get());
            return needs.get();
        }
    }
}
