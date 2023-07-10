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
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.context.InternalMethod.allInternalMethods;
import static pl.com.labaj.autorecord.processor.context.InternalMethod.isInternal;
import static pl.com.labaj.autorecord.processor.utils.Methods.isAnnotatedWith;

public class MemoizationFinder {
    private final Elements elementUtils;

    public MemoizationFinder(Elements elementUtils) {
        this.elementUtils = elementUtils;
    }

    public Memoization findMemoization(TypeElement sourceInterface, AutoRecord.Options recordOptions) {
        var itemsFromOptions = allInternalMethods()
                .filter(internalMethod -> internalMethod.isMemoizedInOptions(recordOptions))
                .map(internalMethod -> new Memoization.Item(internalMethod.type(), internalMethod.methodName(), emptyList(), Set.of(PUBLIC), true));

        var itemsFromAnnotation = elementUtils.getAllMembers(sourceInterface).stream()
                .filter(Methods::isMethod)
                .map(ExecutableElement.class::cast)
                .filter(method -> isAnnotatedWith(method, Memoized.class))
                .filter(Methods::hasNoParameters)
                .filter(Methods::isNotVoid)
                .filter(SpecialMethod::isNotSpecial)
                .map(this::toMemoizedItem);

        var items = Stream.concat(itemsFromOptions, itemsFromAnnotation)
                .distinct()
                .toList();

        return new Memoization(items);
    }

    private Memoization.Item toMemoizedItem(ExecutableElement method) {
        var annotations = method.getAnnotationMirrors().stream()
                .map(AnnotationMirror.class::cast)
                .toList();

        return new Memoization.Item(method.getReturnType(),
                method.getSimpleName().toString(),
                annotations,
                method.getModifiers(),
                isInternal(method));
    }
}
