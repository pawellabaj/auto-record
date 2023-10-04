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
import pl.com.labaj.autorecord.processor.utils.Methods;

import javax.lang.model.element.ExecutableElement;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static javax.lang.model.type.TypeKind.ERROR;
import static pl.com.labaj.autorecord.processor.utils.Annotations.annotationsAllowedFor;

class ComponentsFinder {

    public static final String ERROR_INDICATOR = "<any>";

    List<RecordComponent> getComponents(List<ExecutableElement> allMethods, Predicate<ExecutableElement> isNotSpecial) {
        return allMethods.stream()
                .filter(Methods::isAbstract)
                .filter(isNotSpecial)
                .filter(Methods::hasNoParameters)
                .filter(Methods::isNotVoid)
                .filter(InternalMethod::isNotInternal)
                .map(this::toRecordComponent)
                .toList();
    }

    private RecordComponent toRecordComponent(ExecutableElement method) {
        var returnType = method.getReturnType();

        if (returnType.getKind() == ERROR && returnType.toString().equals(ERROR_INDICATOR)) {
            throw new AutoRecordProcessorException("Cannot infer type of " + method.getSimpleName() + "() method. " +
                    "Probably it is generic and not in classpath or sourcepath yet. " +
                    "Try to move the type class into classpath or remove generic clause from " + method.getSimpleName() + "() method.");
        }

        var name = method.getSimpleName().toString();
        var annotations = annotationsAllowedFor(method.getAnnotationMirrors(), Set.of(PARAMETER, RECORD_COMPONENT));

        return new ProcessorRecordComponent(returnType, name, annotations);
    }
}
