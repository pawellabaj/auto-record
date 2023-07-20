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
import pl.com.labaj.autorecord.processor.utils.Methods;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;
import static pl.com.labaj.autorecord.processor.context.InternalMethod.allInternalMethods;
import static pl.com.labaj.autorecord.processor.context.InternalMethod.isInternal;
import static pl.com.labaj.autorecord.processor.utils.Methods.isAnnotatedWith;

class MemoizationFinder {
    List<Memoization.Item> findMemoizationItems(List<ExecutableElement> allMethods, AutoRecord.Options recordOptions, Predicate<ExecutableElement> isSpecial) {
        var itemsFromOptions = allInternalMethods()
                .filter(internalMethod -> internalMethod.isMemoizedInOptions(recordOptions))
                .map(internalMethod -> new Memoization.Item(internalMethod.type(), internalMethod.methodName(), List.of(), true));

        var itemsFromAnnotation = allMethods.stream()
                .filter(method -> isAnnotatedWith(method, Memoized.class))
                .filter(Methods::isNotVoid)
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

    private Memoization.Item toMemoizedItem(ExecutableElement method, Predicate<ExecutableElement> isSpecial) {
        var annotations = method.getAnnotationMirrors().stream()
                .map(AnnotationMirror.class::cast)
                .toList();

        return new Memoization.Item(method.getReturnType(),
                method.getSimpleName().toString(),
                annotations,
                isInternal(method) || isSpecial.test(method));
    }
}
