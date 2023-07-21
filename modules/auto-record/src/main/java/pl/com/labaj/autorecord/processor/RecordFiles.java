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

import com.squareup.javapoet.JavaFile;
import pl.com.labaj.autorecord.context.Logger;

import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import java.io.IOException;
import java.util.List;

import static javax.tools.StandardLocation.SOURCE_OUTPUT;

record RecordFiles(JavaFile recordFile, List<JavaFile> additionalFiles) {
    void writeFiles(Filer filer, Logger logger) {
        write(filer, recordFile);

        additionalFiles.stream()
                .filter(javaFile -> hasToBeWritten(javaFile, filer, logger))
                .forEach(javaFile -> write(filer, javaFile));
    }

    private void write(Filer filer, JavaFile javaFile) {
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            throw new AutoRecordProcessorException("Cannot write file", e);
        }
    }

    private boolean hasToBeWritten(JavaFile javaFile, Filer filer, Logger logger) {
        try {
            var packageName = javaFile.packageName;
            var fileName = javaFile.typeSpec.name;

            boolean hasToBeWritten = true;
            try {
                var resource = filer.getResource(SOURCE_OUTPUT, packageName, fileName + ".java");
                hasToBeWritten = resource.getLastModified() == 0;
            } catch (FilerException e) {
                var filePath = packageName.replace('.', '/') + "/" + fileName + ".java";
                var message = e.getMessage();
                if (message.contains("Attempt to reopen a file for path") && message.contains(filePath)) {
                    hasToBeWritten = false;
                }
            }

            logger.debug("Additional file " + packageName + "." + fileName + " hasToBeWritten: " + hasToBeWritten);
            return hasToBeWritten;
        } catch (IOException e) {
            throw new AutoRecordProcessorException("Cannot check if file exists", e);
        }
    }
}
