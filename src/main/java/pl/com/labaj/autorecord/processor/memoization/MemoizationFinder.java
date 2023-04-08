package pl.com.labaj.autorecord.processor.memoization;

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
import pl.com.labaj.autorecord.processor.utils.Method;
import pl.com.labaj.autorecord.processor.utils.SpecialMethod;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.EnumMap;
import java.util.LinkedHashSet;

import static java.util.stream.Collectors.toMap;
import static javax.lang.model.element.ElementKind.METHOD;
import static pl.com.labaj.autorecord.processor.utils.SpecialMethod.allSpecialMethods;

public class MemoizationFinder {
    private final Elements elementUtils;

    public MemoizationFinder(Elements elementUtils) {
        this.elementUtils = elementUtils;
    }

    public Memoization findMemoization(TypeElement sourceInterface, AutoRecord.Options recordOptions) {
        var items = new LinkedHashSet<Memoization.Item>();

        allSpecialMethods().stream()
                .filter(specialMethod -> specialMethod.isMemoizedInOptions(recordOptions))
                .map(SpecialMethod::toMemoizedItem)
                .forEach(items::add);

        elementUtils.getAllMembers(sourceInterface).stream()
                .filter(this::isMethod)
                .map(ExecutableElement.class::cast)
                .map(Method::new)
                .filter(method -> method.isAnnotatedWith(Memoized.class))
                .filter(Method::hasNoParameters)
                .filter(Method::doesNotReturnVoid)
                .map(Method::getToMemoizedItem)
                .forEach(items::add);

        var specialMemoized = findSpecialMemoized(items);

        return new Memoization(items, specialMemoized);
    }

    private EnumMap<SpecialMethod, Boolean> findSpecialMemoized(LinkedHashSet<Memoization.Item> items) {
        var specialMemoized = items.stream()
                .filter(Memoization.Item::special)
                .map(Memoization.Item::name)
                .collect(toMap(
                        SpecialMethod::fromName,
                        name -> true,
                        (m1, m2) -> m1 || m2,
                        () -> new EnumMap<>(SpecialMethod.class)
                ));

        allSpecialMethods().forEach(m -> specialMemoized.computeIfAbsent(m, sm -> false));

        return specialMemoized;
    }

    private boolean isMethod(Element element) {
        return element.getKind() == METHOD;
    }
}
