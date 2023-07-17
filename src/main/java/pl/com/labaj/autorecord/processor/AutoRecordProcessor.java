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
import pl.com.labaj.autorecord.processor.context.MessagerLogger;

import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.tools.Diagnostic.Kind.NOTE;
import static org.apiguardian.api.API.Status.STABLE;
import static pl.com.labaj.autorecord.processor.utils.Annotations.getAnnotation;
import static pl.com.labaj.autorecord.processor.utils.Annotations.getAnnotations;

/**
 * Annotation processor for generating record based on an interface. Processes annotations from {@code pl.com.labaj.autorecord} package
 */
@API(status = STABLE)
@SupportedAnnotationTypes("pl.com.labaj.autorecord.*")
public class AutoRecordProcessor extends AbstractProcessor {

    private static final String AUTO_RECORD_CLASS_NAME = AutoRecord.class.getName();

    private final ExtensionsInitializer extensionsInitializer = new ExtensionsInitializer();

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
            AutoRecord.Options recordOptions = getAnnotation(sourceInterface, AutoRecord.Options.class).orElse(null);
            RecordBuilder.Options builderOptions = getAnnotation(sourceInterface, RecordBuilder.Options.class).orElse(null);
            List<AutoRecord.Extension> extensionAnnotations = getAnnotations(sourceInterface, AutoRecord.Extension.class);

            processElement(sourceInterface, recordOptions, builderOptions, extensionAnnotations);
        } else {
            getAnnotation(annotation, AutoRecord.Template.class)
                    .ifPresent(template -> {
                        AutoRecord.Options recordOptions = template.recordOptions();
                        processingEnv.getMessager().printMessage(NOTE, "BO :" + template.builderOptions().getClass());
                        RecordBuilder.Options builderOptions = template.builderOptions();
                        List<AutoRecord.Extension> extensionAnnotations = List.of(template.extensions());

                        processElement(sourceInterface, recordOptions, builderOptions, extensionAnnotations);
                    });
        }
    }

    private void processElement(TypeElement sourceInterface,
                                @Nullable AutoRecord.Options recordOptions,
                                @Nullable RecordBuilder.Options builderOptions,
                                List<AutoRecord.Extension> extensionAnnotations) {
        var logger = new MessagerLogger(processingEnv.getMessager(), sourceInterface);
        logger.debug("Generate record for %s".formatted(sourceInterface));

        try {
            var extensions = extensionsInitializer.initExtensions(extensionAnnotations, logger);
            var recordGenerator = new RecordJavaFileBuilder(sourceInterface, recordOptions, builderOptions, extensions, processingEnv, logger);
            var javaFile = recordGenerator.buildJavaFile();
            javaFile.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            logger.error("Exception thrown during generation record", e);
        }
    }
}
