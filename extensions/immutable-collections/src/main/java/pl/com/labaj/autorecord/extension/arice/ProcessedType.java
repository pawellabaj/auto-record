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

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static pl.com.labaj.autorecord.extension.arice.Names.ARICE_IMMUTABLE_COLLECTION_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.ARICE_IMMUTABLE_DEQUE_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_LIST_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_MAP_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_SET_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_SORTED_MAP_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_SORTED_SET_CLASS_NAME;

@SuppressWarnings("java:S1192")
enum ProcessedType {
    NAVIGABLE_MAP("java.util.NavigableMap", Set.of(), GUAVA_IMMUTABLE_SORTED_MAP_CLASS_NAME, "copyOfSorted"){
        @Override List<String> genericNames() {return List.of("K", "V");}
    },
    SORTED_MAP("java.util.SortedMap", Set.of(NAVIGABLE_MAP), GUAVA_IMMUTABLE_SORTED_MAP_CLASS_NAME, "copyOfSorted"){
        @Override List<String> genericNames() {return List.of("K", "V");}
    },
    MAP("java.util.Map", Set.of(SORTED_MAP), GUAVA_IMMUTABLE_MAP_CLASS_NAME, "copyOf"){
        @Override List<String> genericNames() {return List.of("K", "V");}
    },
    DEQUE("java.util.Deque", Set.of(), ARICE_IMMUTABLE_DEQUE_CLASS_NAME, "copyOfQueue"),
    QUEUE("java.util.Queue", Set.of(DEQUE), ARICE_IMMUTABLE_DEQUE_CLASS_NAME, "copyOfQueue"),
    LIST("java.util.List", Set.of(), GUAVA_IMMUTABLE_LIST_CLASS_NAME, "copyOf"),
    NAVIGABLE_SET("java.util.NavigableSet", Set.of(), GUAVA_IMMUTABLE_SORTED_SET_CLASS_NAME, "copyOfSorted"),
    SORTED_SET("java.util.SortedSet", Set.of(NAVIGABLE_SET), GUAVA_IMMUTABLE_SORTED_SET_CLASS_NAME, "copyOfSorted"),
    SET("java.util.Set", Set.of(SORTED_SET), GUAVA_IMMUTABLE_SET_CLASS_NAME, "copyOf"),
    COLLECTION("java.util.Collection", Set.of(SET, LIST, QUEUE), ARICE_IMMUTABLE_COLLECTION_CLASS_NAME, "copyOfCollection"),
    OBJECT("java.lang.Object", Set.of(COLLECTION, MAP), null, null) {
        @Override List<String> genericNames() {return List.of();}
    };

    private static final Set<ProcessedType> ALL_TYPES = EnumSet.allOf(ProcessedType.class);
    private static final Map<String, ProcessedType> NAME_TO_TYPES = ALL_TYPES.stream()
            .collect(toMap(ProcessedType::className, identity()));
    private final String className;

    private final Set<ProcessedType> directSubTypes;
    private final ClassName factoryClassName;
    private final String factoryMethodName;
    private final String argumentName;
    private final String methodName;

    ProcessedType(String className,
                  Set<ProcessedType> directSubTypes,
                  ClassName factoryClassName,
                  String factoryMethodName) {
        this.className = className;
        this.directSubTypes = directSubTypes;
        this.factoryClassName = factoryClassName;
        this.factoryMethodName = factoryMethodName;

        argumentName = nameToCamelCase();
        methodName = nameToMethodName();
    }

    static Set<ProcessedType> allProcessedTypes() {
        return ALL_TYPES;
    }

    static Optional<ProcessedType> pTypeOf(String className) {
        return Optional.ofNullable(NAME_TO_TYPES.get(className));
    }

    static boolean sameFactory(ProcessedType pType, ProcessedType subPType) {
        return subPType.factoryClassName().equals(pType.factoryClassName()) && subPType.factoryMethodName().equals(pType.factoryMethodName());
    }

    String className() {
        return className;
    }

    String methodName() {
        return methodName;
    }

    String argumentName() {
        return argumentName;
    }

    Set<ProcessedType> directSubTypes() {
        return directSubTypes;
    }

    boolean checkGenericInInstanceOf() {
        return true;
    }

    ClassName factoryClassName() {
        return factoryClassName;
    }

    String factoryMethodName() {
        return factoryMethodName;
    }

    List<String> genericNames() {
        return List.of("E");
    }

    @Override
    public String toString() {
        return className;
    }

    private String nameToCamelCase() {
        var parts = name().split("_");
        var camelCase = new StringBuilder(parts[0].toLowerCase());

        for (int i = 1; i < parts.length; i++) {
            camelCase.append(capitalize(parts[i].toLowerCase()));
        }

        return camelCase.toString();
    }

    private String nameToMethodName() {
        var parts = name().split("_");
        var camelCase = new StringBuilder("copyOf");

        for (String part : parts) {
            camelCase.append(capitalize(part.toLowerCase()));
        }

        return camelCase.toString();
    }
}
