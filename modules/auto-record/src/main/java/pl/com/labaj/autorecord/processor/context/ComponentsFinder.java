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

import pl.com.labaj.autorecord.context.RecordComponent;
import pl.com.labaj.autorecord.processor.AutoRecordProcessorException;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static javax.lang.model.type.TypeKind.ERROR;
import static pl.com.labaj.autorecord.processor.utils.Annotations.annotationsAllowedFor;

class ComponentsFinder {

    public static final String ERROR_INDICATOR = "<any>";

    List<RecordComponent> getComponents(List<Method> allMethods, Predicate<Method> isNotSpecial) {
        return allMethods.stream()
                .filter(Method::isAbstract)
                .filter(isNotSpecial)
                .filter(Method::hasNoParameters)
                .filter(Method::isNotVoid)
                .filter(InternalMethod::isNotInternal)
                .map(this::toRecordComponent)
                .toList();
    }

    private RecordComponent toRecordComponent(Method method) {
        var name = method.name();
        var returnType = method.returnType();

        if (returnType.getKind() == ERROR && returnType.toString().equals(ERROR_INDICATOR)) {
            throw new AutoRecordProcessorException("Cannot infer type of " + name + "() method. " +
                    "Probably it is generic and not in classpath or sourcepath yet. " +
                    "Try to move the type class into classpath or remove generic clause from " + name + "() method.");
        }

        var annotations = annotationsAllowedFor(method.annotations(), Set.of(PARAMETER, RECORD_COMPONENT));

        return new ProcessorRecordComponent(returnType, name, annotations);
    }
}
