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
import pl.com.labaj.autorecord.processor.context.ContextBuilder;
import pl.com.labaj.autorecord.processor.context.MessagerLogger;

import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.tools.Diagnostic.Kind.WARNING;
import static org.apache.commons.lang3.StringUtils.rightPad;
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

    private ContextBuilder contextBuilder;
    private ExtensionsInitializer extensionsInitializer;
    private RecordJavaFileBuilder recordGenerator;

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        extensionsInitializer = new ExtensionsInitializer();
        contextBuilder = new ContextBuilder(processingEnv.getElementUtils());
        recordGenerator = new RecordJavaFileBuilder();
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
            var extensionAnnotations = getAnnotations(sourceInterface, AutoRecord.Extension.class);

            processElement(sourceInterface, recordOptions, builderOptions, extensionAnnotations);
        } else {
            getAnnotation(annotation, AutoRecord.Template.class)
                    .ifPresent(template -> {
                        var recordOptions = readAnnotationProperty(annotation, template, AutoRecord.Template::recordOptions, () -> null);
                        var builderOptions = readAnnotationProperty(annotation, template, AutoRecord.Template::builderOptions, () -> null);
                        var extensionAnnotations = readAnnotationProperty(annotation,
                                template,
                                tmp -> List.of(tmp.extensions()),
                                List::<AutoRecord.Extension>of);

                        processElement(sourceInterface, recordOptions, builderOptions, extensionAnnotations);
                    });
        }
    }

    @Nullable
    private <T extends Annotation, V> V readAnnotationProperty(Element element, T annotation, Function<T, V> propertyGetter, Supplier<V> defaultValueSupplier) {
        try {
            return propertyGetter.apply(annotation);
        } catch (AnnotationTypeMismatchException e) {
            //In some compiling environments, eg. some of GitHub workflow runners
            processingEnv.getMessager().printMessage(WARNING, "Cannot get annotation property: " + e.getLocalizedMessage(), element);
            return defaultValueSupplier.get();
        }
    }

    private void processElement(TypeElement sourceInterface,
                                @Nullable AutoRecord.Options recordOptions,
                                @Nullable RecordBuilder.Options builderOptions,
                                List<AutoRecord.Extension> extensionAnnotations) {
        var logger = new MessagerLogger(processingEnv.getMessager(), sourceInterface);

        try {
            logStartEnd("[START] ", sourceInterface, logger);

            var extensions = extensionsInitializer.initExtensions(extensionAnnotations, logger);
            var context = contextBuilder.buildContext(sourceInterface, recordOptions, builderOptions, extensions, logger);
            var javaFile = recordGenerator.buildJavaFile(context, extensions);

            javaFile.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            logger.error("Exception thrown during generation record", e);
        } finally {
            logStartEnd("[ END ] ", sourceInterface, logger);
        }
    }

    private void logStartEnd(String prefix, TypeElement sourceInterface, MessagerLogger logger) {
        if (logger.isDebugEnabled()) {
            var message = rightPad(prefix + sourceInterface.getQualifiedName() + " ", 100, "-");
            logger.info(message);
        }
    }
}
