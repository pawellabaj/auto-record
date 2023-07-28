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

import pl.com.labaj.autorecord.context.Logger;

import javax.annotation.Nullable;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Boolean.FALSE;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;
import static javax.lang.model.type.TypeKind.ERROR;
import static pl.com.labaj.autorecord.extension.arice.InterfaceType.OBJECT;
import static pl.com.labaj.autorecord.extension.arice.InterfaceType.allProcessedTypes;
import static pl.com.labaj.autorecord.extension.arice.InterfaceType.interfaceTypeWith;

record RecordComponent(String name, boolean isNullable, TypeMirror declaredType, InterfaceType pType) {

    static String debugInfo(List<RecordComponent> recordComponents) {
        return recordComponents.stream()
                .map(RecordComponent::toString)
                .collect(joining("\n"));
    }

    @Override
    public String toString() {
        return name + " : " + declaredType;
    }

    static class Builder {
        private final ExtensionContext extContext;
        private final Set<TypeMirror> immutableTypes;
        private final Logger logger;
        private final Map<String, Boolean> toBeProcessedCache = new HashMap<>();

        Builder(ExtensionContext extContext, Set<TypeMirror> immutableTypes, Logger logger) {
            this.extContext = extContext;
            this.immutableTypes = immutableTypes;
            this.logger = logger;
        }

        @Nullable
        RecordComponent toExtensionRecordComponent(pl.com.labaj.autorecord.context.RecordComponent component) {
            var isNullable = component.isAnnotatedWith(Nullable.class);
            var componentType = component.type();
            var declaredType = extContext.erasure(componentType);
            var shouldBeProcessed = toBeProcessedCache.computeIfAbsent(declaredType.toString(), name -> shouldBeProcessed(declaredType));

            if (FALSE.equals(shouldBeProcessed)) {
                return null;
            }

            var pType = interfaceTypeWith(declaredType.toString());
            if (isNull(pType) || declaredType.getKind() == ERROR) {
                logger.warning("Unrecognized type for processing " + declaredType);
                pType = OBJECT;
            }

            return new RecordComponent(component.name(), isNullable, declaredType, pType);
        }

        private boolean shouldBeProcessed(TypeMirror componentType) {
            if (componentType.getKind() == ERROR) {
                return true;
            }

            var isImmutableType = immutableTypes.stream()
                    .anyMatch(immutableType -> extContext.isSameType(componentType, immutableType));
            if (isImmutableType) {
                logger.debug(componentType + " is immutable");
                return false;
            }

            var hasImmutableSuperType = immutableTypes.stream()
                    .anyMatch(immutableType -> extContext.isSubtype(componentType, immutableType));
            if (hasImmutableSuperType) {
                logger.debug(componentType + " has immutable super type");
                return false;
            }

            var isCollection = allProcessedTypes().stream()
                    .anyMatch(pType -> extContext.isSameType(componentType, pType, logger));
            if (isCollection) {
                logger.debug(componentType + " has processed type");
            }

            return isCollection;
        }
    }
}
