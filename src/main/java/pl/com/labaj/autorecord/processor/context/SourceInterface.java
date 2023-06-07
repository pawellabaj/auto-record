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

import pl.com.labaj.autorecord.processor.special.SpecialMethod;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record SourceInterface(String name, TypeMirror type, List<ExecutableElement> propertyMethods, Map<SpecialMethod, ExecutableElement> specialMethods,  List<? extends TypeParameterElement> typeParameters) {
    public Optional<ExecutableElement> specialMethod(SpecialMethod specialMethod) {
        return Optional.ofNullable(specialMethods.get(specialMethod));
    }
}
