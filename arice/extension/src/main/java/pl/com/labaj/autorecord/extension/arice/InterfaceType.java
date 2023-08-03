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

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static pl.com.labaj.autorecord.extension.arice.Names.ARICE_IMMUTABLE_COLLECTION_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.ARICE_IMMUTABLE_DEQUE_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_LIST_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_LIST_MULTIMAP_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_MAP_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_MULTIMAP_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_MULTISET_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_RANGE_MAP_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_RANGE_SET_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_SET_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_SET_MULTIMAP_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_SORTED_MAP_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_SORTED_MULTISET_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_SORTED_SET_CLASS_NAME;
import static pl.com.labaj.autorecord.extension.arice.Names.GUAVA_IMMUTABLE_TABLE_CLASS_NAME;

@SuppressWarnings("java:S1192")
enum InterfaceType {
    TABLE("com.google.common.collect.Table", Set.of(), GUAVA_IMMUTABLE_TABLE_CLASS_NAME, "copyOf") {
        @Override
        List<String> genericNames() {return List.of("R", "C", "V");}
    },
    RANGE_MAP("com.google.common.collect.RangeMap", Set.of(), GUAVA_IMMUTABLE_RANGE_MAP_CLASS_NAME, "copyOf") {
        @Override
        List<String> genericNames() {return List.of("K extends Comparable<K>", "V");}
    },
    RANGE_SET("com.google.common.collect.RangeSet", Set.of(), GUAVA_IMMUTABLE_RANGE_SET_CLASS_NAME, "copyOf") {
        @Override
        List<String> genericNames() {return List.of("E extends Comparable<E>");}
    },
    LIST_MULTIMAP("com.google.common.collect.ListMultimap", Set.of(), GUAVA_IMMUTABLE_LIST_MULTIMAP_CLASS_NAME, "copyOf") {
        @Override
        List<String> genericNames() {return List.of("K", "V");}
    },
    SET_MULTIMAP("com.google.common.collect.SetMultimap", Set.of(), GUAVA_IMMUTABLE_SET_MULTIMAP_CLASS_NAME, "copyOf") {
        @Override
        List<String> genericNames() {return List.of("K", "V");}
    },
    MULTIMAP("com.google.common.collect.Multimap", Set.of(SET_MULTIMAP, LIST_MULTIMAP), GUAVA_IMMUTABLE_MULTIMAP_CLASS_NAME, "copyOf") {
        @Override
        List<String> genericNames() {return List.of("K", "V");}
    },
    SORTED_MULTISET("com.google.common.collect.SortedMultiset", Set.of(), GUAVA_IMMUTABLE_SORTED_MULTISET_CLASS_NAME, "copyOfSorted"),
    MULTISET("com.google.common.collect.Multiset", Set.of(SORTED_MULTISET), GUAVA_IMMUTABLE_MULTISET_CLASS_NAME, "copyOf"),
    NAVIGABLE_MAP("java.util.NavigableMap", Set.of(), GUAVA_IMMUTABLE_SORTED_MAP_CLASS_NAME, "copyOfSorted") {
        @Override
        List<String> genericNames() {return List.of("K", "V");}
    },
    SORTED_MAP("java.util.SortedMap", Set.of(NAVIGABLE_MAP), GUAVA_IMMUTABLE_SORTED_MAP_CLASS_NAME, "copyOfSorted") {
        @Override
        List<String> genericNames() {return List.of("K", "V");}
    },
    MAP("java.util.Map", Set.of(SORTED_MAP), GUAVA_IMMUTABLE_MAP_CLASS_NAME, "copyOf") {
        @Override
        List<String> genericNames() {return List.of("K", "V");}
    },
    DEQUE("java.util.Deque", Set.of(), ARICE_IMMUTABLE_DEQUE_CLASS_NAME, "copyOfQueue"),
    QUEUE("java.util.Queue", Set.of(DEQUE), ARICE_IMMUTABLE_DEQUE_CLASS_NAME, "copyOfQueue"),
    LIST("java.util.List", Set.of(), GUAVA_IMMUTABLE_LIST_CLASS_NAME, "copyOf"),
    NAVIGABLE_SET("java.util.NavigableSet", Set.of(), GUAVA_IMMUTABLE_SORTED_SET_CLASS_NAME, "copyOfSorted"),
    SORTED_SET("java.util.SortedSet", Set.of(NAVIGABLE_SET), GUAVA_IMMUTABLE_SORTED_SET_CLASS_NAME, "copyOfSorted"),
    SET("java.util.Set", Set.of(SORTED_SET), GUAVA_IMMUTABLE_SET_CLASS_NAME, "copyOf"),
    COLLECTION("java.util.Collection", Set.of(SET, LIST, QUEUE, MULTISET), ARICE_IMMUTABLE_COLLECTION_CLASS_NAME, "copyOfCollection"),
    OBJECT("java.lang.Object", Set.of(COLLECTION, MAP, MULTIMAP, RANGE_SET, RANGE_MAP, TABLE), null, null) {
        @Override
        List<String> genericNames() {return List.of();}
    };

    private static final Set<InterfaceType> ALL_TYPES = EnumSet.allOf(InterfaceType.class);
    private static final Map<String, InterfaceType> NAME_TO_TYPES = ALL_TYPES.stream()
            .collect(toMap(InterfaceType::className, identity()));
    private final String className;

    private final Set<InterfaceType> directSubTypes;
    private final ClassName factoryClassName;
    private final String factoryMethodName;
    private final String argumentName;

    InterfaceType(String className, Set<InterfaceType> directSubTypes, ClassName factoryClassName, String factoryMethodName) {
        this.className = className;
        this.directSubTypes = directSubTypes;
        this.factoryClassName = factoryClassName;
        this.factoryMethodName = factoryMethodName;

        argumentName = nameToCamelCase();
    }

    static Set<InterfaceType> allProcessedTypes() {
        return ALL_TYPES;
    }

    @Nullable
    static InterfaceType interfaceTypeWith(String className) {
        return NAME_TO_TYPES.get(className);
    }

    String className() {
        return className;
    }

    String argumentName() {
        return argumentName;
    }

    Set<InterfaceType> directSubTypes() {
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
}
