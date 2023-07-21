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

import javax.annotation.processing.Filer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static javax.tools.StandardLocation.SOURCE_OUTPUT;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static pl.com.labaj.autorecord.extension.arice.Names.ARICE_PACKAGE;

class ClassesCopier {
    private final Filer filer;

    ClassesCopier(Filer filer) {this.filer = filer;}

    void copyResourceToFiler(String className, Logger logger) {
        var fqcn = ARICE_PACKAGE + "." + className;

        if (logger.isDebugEnabled()) {
            var message = rightPad("Write " + className + " ", 100, "-");
            logger.note(message);
        }

        try {
            var classLoader = AutoRecordImmutableCollectionsProcessor.class.getClassLoader();
            try (var inputStream = classLoader.getResourceAsStream("arice/" + className + ".java");
                 var reader = new BufferedReader(new InputStreamReader(inputStream))) {

                var fileObject = filer.createResource(SOURCE_OUTPUT, ARICE_PACKAGE, className + ".java");
                try (var writer = fileObject.openWriter()) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.append(line).append("\n");
                    }
                }
            }
        } catch (IOException e) {
            throw new AutoRecordProcessorException("Cannot copy " + fqcn + " source output", e);
        }
    }
}
