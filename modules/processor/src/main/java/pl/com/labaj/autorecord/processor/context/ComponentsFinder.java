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
import pl.com.labaj.autorecord.processor.utils.Methods;

import javax.lang.model.element.ExecutableElement;
import java.util.List;
import java.util.function.Predicate;

import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static pl.com.labaj.autorecord.processor.utils.Annotations.annotationsAllowedFor;

class ComponentsFinder {
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
        var type = method.getReturnType();
        var name = method.getSimpleName().toString();
        var annotations = annotationsAllowedFor(method.getAnnotationMirrors(), TYPE_PARAMETER);

        return new RecordComponent(type, name, annotations);
    }
}
