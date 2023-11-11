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

import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.Memoized;

import javax.lang.model.element.AnnotationMirror;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;
import static pl.com.labaj.autorecord.processor.context.InternalMethod.allInternalMethods;
import static pl.com.labaj.autorecord.processor.context.InternalMethod.isInternal;

class MemoizationFinder {

    private static final String MEMOIZED_CLASS_NAME = Memoized.class.getName();

    List<Memoization.Item> findMemoizationItems(List<Method> allMethods,
                                                AutoRecord.Options recordOptions,
                                                Predicate<Method> isSpecial,
                                                MessagerLogger logger) {
        var itemsFromOptions = allInternalMethods()
                .filter(internalMethod -> internalMethod.isMemoizedInOptions(recordOptions))
                .map(internalMethod -> new Memoization.Item(internalMethod.type(), internalMethod.methodName(), List.of(), List.of(), true));

        var memoizedMethods = allMethods.stream()
                .filter(this::isMemoized)
                .toList();

        memoizedMethods.stream()
                .filter(method -> !method.isNotVoid())
                .forEach(method -> logger.error("\"" + method.name() + "\" method is void. Can't memoize such method."));

        memoizedMethods = memoizedMethods.stream()
                .filter(Method::isNotVoid)
                .toList();

        memoizedMethods.stream()
                .filter(method -> !method.hasNoParameters())
                .forEach(method -> logger.mandatoryWarning("\"" + method.name() + "\" method accepts parameters. " +
                        "It's not a good idea to memoize it, unless it returns result independent from parameters."));

        var itemsFromAnnotation = memoizedMethods.stream()
                .map(method -> toMemoizedItem(method, isSpecial));

        return Stream.concat(itemsFromOptions, itemsFromAnnotation)
                .collect(collectingAndThen(
                        toMap(
                                Memoization.Item::name,
                                identity(),
                                Memoization.Item::mergeWith,
                                LinkedHashMap::new
                        ),
                        map -> List.copyOf(map.values())
                ));
    }

    private boolean isMemoized(Method method) {
        return method.annotations().stream()
                .map(AnnotationMirror::getAnnotationType)
                .map(Objects::toString)
                .anyMatch(MEMOIZED_CLASS_NAME::equals);
    }

    private Memoization.Item toMemoizedItem(Method method, Predicate<Method> isSpecial) {
        return new Memoization.Item(method.returnType(),
                method.name(),
                method.annotations(),
                method.parameters(),
                isInternal(method) || isSpecial.test(method));
    }
}
