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

import javax.lang.model.type.MirroredTypeException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

class ExtensionsInitializer {

    List<AutoRecordExtension> initExtensions(List<AutoRecord.Extension> extensionAnnotations, Logger logger) {
        return extensionAnnotations.stream()
                .map(extendWith -> initExtension(extendWith, logger))
                .toList();
    }

    private AutoRecordExtension initExtension(AutoRecord.Extension extensionAnnotation, Logger logger) {
        var extensionClass = loadExtensionClass(extensionAnnotation, logger);
        var extension = instantiateExtension(extensionClass);
        extension.setParameters(extensionAnnotation.parameters());

        return extension;
    }

    private Class<? extends AutoRecordExtension> loadExtensionClass(AutoRecord.Extension extensionAnnotation, Logger logger) {
        Class<? extends AutoRecordExtension> extensionClass;
        try {
            extensionClass = extensionAnnotation.extensionClass();
            logger.debug("Class from annotation: " + extensionClass);
        } catch (MirroredTypeException e) {
            var className = e.getTypeMirror().toString();
            logger.debug("Class name from typeMirror: " + className);

            try {
                var aClass = Class.forName(className);
                extensionClass = getExtensionClass(aClass);
            } catch (ClassNotFoundException ex) {
                throw new AutoRecordProcessorException("Cannot load class " + className, ex);
            }
        }

        return extensionClass;
    }

    @SuppressWarnings("unchecked")
    private  Class<? extends AutoRecordExtension> getExtensionClass(Class<?> aClass) {
        if (AutoRecordExtension.class.isAssignableFrom(aClass)) {
            return (Class<? extends AutoRecordExtension>) aClass;
        }
        throw new AutoRecordProcessorException("Class " + aClass.getName() + " does not extend " + AutoRecordExtension.class.getName());
    }

    private AutoRecordExtension instantiateExtension(Class<? extends AutoRecordExtension> extensionClass) {
        try {
            return extensionClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new AutoRecordProcessorException("Can't instantiate extension " + e.getLocalizedMessage());
        }
    }
}
