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

import pl.com.labaj.autorecord.processor.special.SpecialMethod;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import static java.lang.Boolean.TRUE;

public record Memoization(Set<Item> items, EnumMap<SpecialMethod, Boolean> specialMemoized) {
    public boolean isMemoized(SpecialMethod specialMethod) {
        return TRUE.equals(specialMemoized().get(specialMethod));
    }

    public record Item(TypeMirror type, String name, List<AnnotationMirror> annotations, Set<Modifier> modifiers, boolean special) {}
}
