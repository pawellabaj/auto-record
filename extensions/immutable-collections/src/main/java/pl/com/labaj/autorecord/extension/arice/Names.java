
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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

final class Names {
    static final String ARICE_PACKAGE = "pl.com.labaj.autorecord.extension.arice";

    private static final String GUAVA_PACKAGE = "com.google.common.collect";
    private static final String IMMUTABLE_SET = "ImmutableSet";
    private static final String IMMUTABLE_SORTED_SET = "ImmutableSortedSet";
    private static final String IMMUTABLE_LIST = "ImmutableList";
    private static final String IMMUTABLE_MAP = "ImmutableMap";
    private static final String IMMUTABLE_SORTED_MAP = "ImmutableSortedMap";
    private static final String IMMUTABLE_COLLECTION = "ImmutableCollection";
    private static final String IMMUTABLE_DEQUE = "ImmutableDeque";

    static final ClassName GUAVA_IMMUTABLE_SET_CLASS_NAME = ClassName.get(GUAVA_PACKAGE, IMMUTABLE_SET);
    static final ClassName GUAVA_IMMUTABLE_SORTED_SET_CLASS_NAME = ClassName.get(GUAVA_PACKAGE, IMMUTABLE_SORTED_SET);
    static final ClassName GUAVA_IMMUTABLE_LIST_CLASS_NAME = ClassName.get(GUAVA_PACKAGE, IMMUTABLE_LIST);
    static final ClassName GUAVA_IMMUTABLE_MAP_CLASS_NAME = ClassName.get(GUAVA_PACKAGE, IMMUTABLE_MAP);
    static final ClassName GUAVA_IMMUTABLE_SORTED_MAP_CLASS_NAME = ClassName.get(GUAVA_PACKAGE, IMMUTABLE_SORTED_MAP);
    static final ClassName ARICE_IMMUTABLE_COLLECTION_CLASS_NAME = ClassName.get(ARICE_PACKAGE, IMMUTABLE_COLLECTION);
    static final ClassName ARICE_IMMUTABLE_DEQUE_CLASS_NAME = ClassName.get(ARICE_PACKAGE, IMMUTABLE_DEQUE);

    private static final Set<String> PREDEFINED_IMMUTABLE_NAMES = Set.of(
            GUAVA_PACKAGE + "." + IMMUTABLE_SET,
            GUAVA_PACKAGE + "." + IMMUTABLE_SORTED_SET,
            GUAVA_PACKAGE + "." + IMMUTABLE_LIST,
            GUAVA_PACKAGE + "." + IMMUTABLE_MAP,
            GUAVA_PACKAGE + "." + IMMUTABLE_SORTED_MAP,
            ARICE_PACKAGE + "." + IMMUTABLE_COLLECTION,
            ARICE_PACKAGE + "." + IMMUTABLE_DEQUE
    );

    private Names() {}

    static Set<String> notPredefinedNames(String[] parameters) {
        return Arrays.stream(parameters)
                .filter(name -> !PREDEFINED_IMMUTABLE_NAMES.contains(name))
                .collect(toSet());
    }

    static Set<String> allImmutableNames(String[] parameters) {
        return Stream.concat(
                PREDEFINED_IMMUTABLE_NAMES.stream(),
                Arrays.stream(parameters)
        ).collect(toSet());
    }
}
