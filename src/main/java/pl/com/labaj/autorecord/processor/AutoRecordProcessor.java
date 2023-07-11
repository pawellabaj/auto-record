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

import io.soabase.recordbuilder.core.RecordBuilder;
import org.apiguardian.api.API;
import pl.com.labaj.autorecord.AutoRecord;

import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static javax.lang.model.element.ElementKind.INTERFACE;
import static org.apiguardian.api.API.Status.STABLE;
import static pl.com.labaj.autorecord.processor.utils.Annotations.getAnnotation;

/**
 * Annotation processor for generating record based on an interface. Processes annotations from {@code pl.com.labaj.autorecord} package
 */
@API(status = STABLE)
@SupportedAnnotationTypes("pl.com.labaj.autorecord.*")
public class AutoRecordProcessor extends AbstractProcessor {

    private static final String AUTO_RECORD_CLASS_NAME = AutoRecord.class.getName();

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> processAnnotation(roundEnv, annotation));

        return false;
    }

    private void processAnnotation(RoundEnvironment roundEnv, TypeElement annotation) {
        roundEnv.getElementsAnnotatedWith(annotation)
                .stream()
                .filter(element -> element.getKind() == INTERFACE)
                .map(TypeElement.class::cast)
                .forEach(typeElement -> processAnnotatedElement(annotation, typeElement));
    }

    private void processAnnotatedElement(TypeElement annotation, TypeElement sourceInterface) {
        var annotationQualifiedName = annotation.getQualifiedName();

        if (annotationQualifiedName.contentEquals(AUTO_RECORD_CLASS_NAME)) {
            var recordOptions = getAnnotation(sourceInterface, AutoRecord.Options.class).orElse(null);
            var builderOptions = getAnnotation(sourceInterface, RecordBuilder.Options.class).orElse(null);

            processElement(sourceInterface, recordOptions, builderOptions);
        } else {
            getAnnotation(annotation, AutoRecord.Template.class)
                    .ifPresent(template -> {
                        var recordOptions = template.recordOptions();
                        var builderOptions = template.builderOptions();

                        processElement(sourceInterface, recordOptions, builderOptions);
                    });
        }
    }

    private void processElement(TypeElement sourceInterface, AutoRecord.Options recordOptions, @Nullable RecordBuilder.Options builderOptions) {
        var logger = new MessagerLogger(processingEnv.getMessager(), sourceInterface);
        logger.info("Generate record for %s".formatted(sourceInterface));

        try {
            var recordGenerator = new RecordJavaFileBuilder(sourceInterface, recordOptions, builderOptions, processingEnv, logger);
            var javaFile = recordGenerator.buildJavaFile();
            javaFile.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            logger.error("Exception thrown during generation record", e);
        }
    }
}
