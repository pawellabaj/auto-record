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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;
import static pl.com.labaj.autorecord.extension.arice.ProcessedType.allProcessedTypes;

class ExtensionContext {
    private Types existingTypeUtils;
    private Elements elementUtils;
    private Types typeUtils;

    private final Map<String, TypeMirror> immutableTypes = new HashMap<>();
    private final EnumMap<ProcessedType, TypeMirror> processedTypes = new EnumMap<>(ProcessedType.class);

    void init(ProcessingEnvironment processingEnv) {
        resetIfNeeded(processingEnv);
        allProcessedTypes().forEach(name -> processedTypes.computeIfAbsent(name, this::loadType));
    }

    Set<TypeMirror> getTypes(Set<String> immutableNames) {
        return immutableNames.stream()
                .map(name -> immutableTypes.computeIfAbsent(name, this::loadType))
                .filter(Objects::nonNull)
                .collect(toSet());
    }

    TypeMirror erasure(TypeMirror type) {
        return typeUtils.erasure(type);
    }

    boolean isSubtype(TypeMirror type, ProcessedType pType, Logger logger) {
        var pTypeMirror = processedTypes.get(pType);
        if (nonNull(pTypeMirror)) {
            return typeUtils.isSubtype(type, pTypeMirror);
        }

        logger.mandatoryWarning("isSubtype fallback for :" + pType);

        return typeUtils.directSupertypes(type).stream()
                .map(Object::toString)
                .anyMatch(s -> s.equals(pType.className()));
    }

    boolean isSubtype(TypeMirror type1, TypeMirror type2) {
        return typeUtils.isSubtype(type1, type2);
    }

    boolean isSameType(TypeMirror type, ProcessedType pType, Logger logger) {
        var pTypeMirror = processedTypes.get(pType);
        if (nonNull(pTypeMirror)) {
            return typeUtils.isSameType(type, pTypeMirror);
        }

        logger.mandatoryWarning("isSameType fallback for :" + pType);

        return type.toString().equals(pType.className());
    }

    boolean isSameType(TypeMirror type1, TypeMirror type2) {
        return typeUtils.isSameType(type1, type2);
    }

    TypeMirror getType(ProcessedType pType) {
        return processedTypes.get(pType);
    }

    TypeElement getElement(ProcessedType pType) {
        return (TypeElement) typeUtils.asElement(processedTypes.get(pType));
    }

    private void resetIfNeeded(ProcessingEnvironment processingEnv) {

        if (isNull(existingTypeUtils) || existingTypeUtils != processingEnv.getTypeUtils()) {
            elementUtils = processingEnv.getElementUtils();
            typeUtils = processingEnv.getTypeUtils();
            existingTypeUtils = typeUtils;

            immutableTypes.clear();
            processedTypes.clear();
        }
    }

    private TypeMirror loadType(ProcessedType pType) {
        return loadType(pType.className());
    }

    private TypeMirror loadType(String className) {
        var typeElement = elementUtils.getTypeElement(className);
        if (isNull(typeElement)) {
            return null;
        }

        var type = typeElement.asType();
        return typeUtils.erasure(type);
    }
}
