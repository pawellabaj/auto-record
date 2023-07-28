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
import pl.com.labaj.autorecord.processor.AutoRecordProcessorException;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
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
import static pl.com.labaj.autorecord.extension.arice.InterfaceType.allProcessedTypes;

class ExtensionContext {
    private Types existingTypeUtils;
    private Elements elementUtils;
    private Types typeUtils;

    private final Map<String, TypeMirror> immutableTypes = new HashMap<>();
    private final EnumMap<InterfaceType, TypeMirror> interfaceTypes = new EnumMap<>(InterfaceType.class);

    void init(ProcessingEnvironment processingEnv) {
        resetIfNeeded(processingEnv);
        allProcessedTypes().forEach(name -> interfaceTypes.computeIfAbsent(name, this::loadType));
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

    boolean isSubtype(TypeMirror type, InterfaceType iType, Logger logger) {
        var pTypeMirror = interfaceTypes.get(iType);
        if (nonNull(pTypeMirror)) {
            return typeUtils.isSubtype(type, pTypeMirror);
        }

        logger.warning("isSubtype fallback for :" + iType);

        return typeUtils.directSupertypes(type).stream()
                .map(Object::toString)
                .anyMatch(s -> s.equals(iType.className()));
    }

    boolean isSubtype(TypeMirror type1, TypeMirror type2) {
        return typeUtils.isSubtype(type1, type2);
    }

    boolean isSameType(TypeMirror type, InterfaceType iType, Logger logger) {
        var pTypeMirror = interfaceTypes.get(iType);
        if (nonNull(pTypeMirror)) {
            return typeUtils.isSameType(type, pTypeMirror);
        }

        logger.warning("isSameType fallback for :" + iType);

        return type.toString().equals(iType.className());
    }

    boolean isSameType(TypeMirror type1, TypeMirror type2) {
        return typeUtils.isSameType(type1, type2);
    }

    @Nullable
    TypeMirror getImmutableType(String name, Logger logger) {
        var typeMirror = immutableTypes.computeIfAbsent(name, this::loadType);

        if (isNull(typeMirror)) {
            logger.debug("Cannot get type for " + name);
        }

        return typeMirror;
    }

    TypeMirror getInterfaceMirrorType(InterfaceType iType, Logger logger) {
        var typeMirror = interfaceTypes.computeIfAbsent(iType, this::loadType);

        if (isNull(typeMirror)) {
            throw new AutoRecordProcessorException("Cannot get type for " + iType+ ". Please check if it's in classpath");
        }

        return typeMirror;
    }

    private void resetIfNeeded(ProcessingEnvironment processingEnv) {

        if (isNull(existingTypeUtils) || existingTypeUtils != processingEnv.getTypeUtils()) {
            elementUtils = processingEnv.getElementUtils();
            typeUtils = processingEnv.getTypeUtils();
            existingTypeUtils = typeUtils;

            immutableTypes.clear();
            interfaceTypes.clear();
        }
    }

    private TypeMirror loadType(InterfaceType iType) {
        return loadType(iType.className());
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
