package pl.com.labaj.autorecord.processor;

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
import pl.com.labaj.autorecord.context.Logger;
import pl.com.labaj.autorecord.extension.AutoRecordExtension;

import javax.annotation.Nullable;
import javax.lang.model.type.MirroredTypeException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

class ExtensionsInitializer {

    List<AutoRecordExtension> initExtensions(List<AutoRecord.Extension> extensionAnnotations, Logger logger) {
        return extensionAnnotations.stream()
                .map(extendWith -> initExtension(extendWith, logger))
                .filter(Objects::nonNull)
                .toList();
    }

    @Nullable
    private AutoRecordExtension initExtension(AutoRecord.Extension extensionAnnotation, Logger logger) {
        var extension = createExtension(extensionAnnotation, logger);
        if (nonNull(extension)) {
            extension.setParameters(extensionAnnotation.parameters());
        }
        return extension;
    }

    @SuppressWarnings("unchecked")
    private AutoRecordExtension createExtension(AutoRecord.Extension extensionAnnotation, Logger logger) {
        try {
            var extensionClass = extensionAnnotation.extensionClass();
            logger.debug("Class from annotation: " + extensionClass);

            return instantiateExtension(extensionClass, logger);
        } catch (MirroredTypeException e) {
            var typeMirror = e.getTypeMirror();
            var extensionClassName = typeMirror.toString();
            logger.debug("Class name from typeMirror: " + extensionClassName);

            Class<? extends AutoRecordExtension> extensionClass = null;
            try {
                extensionClass = (Class<? extends AutoRecordExtension>) Class.forName(extensionClassName);
            } catch (ClassNotFoundException ex) {
                logger.error("Cannot load class " + extensionClassName, ex);
                return null;
            }

            return instantiateExtension(extensionClass, logger);
        }
    }

    private AutoRecordExtension instantiateExtension(Class<? extends AutoRecordExtension> extensionClass, Logger logger) {
        try {
            return extensionClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.debug("Can't instantiate extension because of " + e.getLocalizedMessage());
            return null;
        }
    }
}
