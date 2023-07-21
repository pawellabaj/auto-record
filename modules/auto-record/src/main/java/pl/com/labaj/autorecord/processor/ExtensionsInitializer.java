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

import javax.annotation.processing.ProcessingEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

class ExtensionsInitializer {
    private final ProcessingEnvironment processingEnv;
    private final Map<Integer, AutoRecordExtension> extensions = new HashMap<>();

    ExtensionsInitializer(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    List<AutoRecordExtension> initExtensions(List<AutoRecord.Extension> extensionAnnotations, Logger logger) {
        return extensionAnnotations.stream()
                .map(extensionAnnotation -> initExtension(extensionAnnotation, logger))
                .toList();
    }

    private AutoRecordExtension initExtension(AutoRecord.Extension extensionAnnotation, Logger logger) {
        var className = extensionAnnotation.extensionClass();
        var parameters = extensionAnnotation.parameters();

        logger.debug("Init " + className + " with " + Arrays.toString(parameters));

        return extensions.computeIfAbsent(extensionKey(className, parameters), key -> {
            var extension = instantiateExtension(className);
            extension.init(processingEnv, parameters);

            return extension;
        });
    }

    private int extensionKey(String className, String[] parameters) {
        var sortedParameters = new TreeSet<>(Arrays.asList(parameters));
        return Objects.hash(className, sortedParameters);
    }

    private AutoRecordExtension instantiateExtension(String className) {
        try {
            var extensionClass = Class.forName(className);
            var constructor = extensionClass.getDeclaredConstructor();

            return (AutoRecordExtension) constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new AutoRecordProcessorException("Can't instantiate extension " + e.getLocalizedMessage());
        }
    }
}
