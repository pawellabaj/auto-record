package pl.com.labaj.autorecord.processor.utils;

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

import pl.com.labaj.autorecord.processor.AutoRecordProcessorException;
import pl.com.labaj.autorecord.processor.context.MessagerLogger;

import javax.annotation.processing.ProcessingEnvironment;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class Resources {
    private Resources() {}

    public static void copyResource(ProcessingEnvironment processingEnv, ClassLoader classLoader, String resourcesPath, String packageName, String fileName) {
        try {
            var generatedName = packageName + '.' + fileName;
            var resourcePath = resourcesPath + packageName.replace('.', '/') + '/' + fileName + ".java";
            var logger = new MessagerLogger(processingEnv.getMessager(), null);
            logger.debug("Copy " + classLoader.getResource(resourcePath) + " to " + generatedName);

            var filerSourceFile = processingEnv.getFiler().createSourceFile(generatedName);

            try (var inputStream = classLoader.getResourceAsStream(resourcePath); var writer = filerSourceFile.openWriter()) {
                var reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
                String line;

                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.write(System.lineSeparator());
                }
            } catch (Exception e) {
                filerSourceFile.delete();
                throw e;
            }
        } catch (Exception e) {
            throw new AutoRecordProcessorException("Cannot create " + fileName + " sourceFile", e);
        }
    }
}
