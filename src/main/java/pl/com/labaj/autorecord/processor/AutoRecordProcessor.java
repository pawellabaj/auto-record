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
import pl.com.labaj.autorecord.extension.AutoRecordExtension;

import javax.annotation.Nullable;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

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
            var extensions = getAnnotations(sourceInterface, AutoRecord.ExtendWith.class).stream()
                    .map(extendWith -> createExtension(extendWith, processingEnv.getMessager(), sourceInterface))
                    .toList();

            processElement(sourceInterface, recordOptions, builderOptions, extensions);
        } else {
            var autoRecordTemplateName = AutoRecord.Template.class.getName().replace('$', '.');
            var messager = processingEnv.getMessager();
            var autoRecordElement = processingEnv.getElementUtils().getTypeElement(AutoRecord.class.getName());

//            extracted(annotation, autoRecordTemplateName, messager, autoRecordElement);

            getAnnotation(annotation, AutoRecord.Template.class)
                    .ifPresent(template -> {
                        var recordOptions = template.recordOptions();
                        var builderOptions = template.builderOptions();
                        var extensions = Arrays.stream(template.extendWith())
                                .map(extendWith -> initExtension(extendWith, processingEnv.getMessager(), sourceInterface))
                                .toList();

                        processElement(sourceInterface, recordOptions, builderOptions, extensions);
                    });
        }
    }

    private static void extracted(TypeElement annotation, String autoRecordTemplateName, Messager messager, TypeElement autoRecordElement) {
        autoRecordElement.getEnclosedElements().stream()
                .filter(element -> ((QualifiedNameable) element).getQualifiedName().contentEquals(autoRecordTemplateName))
                .findAny()
                .ifPresent(templateElement -> {
//                        messager.printMessage(NOTE, "T : " + templateElement);

//                        templateElement.getAnnotationMirrors().stream()
////                                .filter(am -> am.getAnnotationType().equals(templateElement.asType()))
//                                .forEach(am -> messager.printMessage(NOTE, "AM: " + am + ", " + am.getAnnotationType()));

                    annotation.getAnnotationMirrors().stream()
                            .filter(am -> am.getAnnotationType().toString().equals(autoRecordTemplateName))
//                                .forEach(am -> messager.printMessage(NOTE, "TTTT: " + am + ", " + am.getAnnotationType().getClass(), sourceInterface));
                            .findFirst()
                            .ifPresent(templateAnnotationMirror -> {
                                var extendWith =
                                        templateAnnotationMirror.getElementValues().entrySet().stream()
                                                .filter(entry -> entry.getKey().getSimpleName().contentEquals("extendWith"))
                                                .map(entry -> entry.getValue())
                                                .findFirst();

                                extendWith.map(v -> v.getValue())
                                        .map(v -> List.class.cast(v))
                                        .ifPresent(values -> {
                                            ((List<Object>) values).stream()
                                                    .map(am -> ((AnnotationMirror) am).getElementValues())
                                                    .forEach(map -> {
                                                        var knownValues = map.entrySet().stream()
                                                                .collect(Collectors.toMap(
                                                                        entry -> entry.getKey().getSimpleName().toString(),
                                                                        entry -> entry.getValue().getValue()
                                                                ));

                                                        var extensionClass = knownValues.get("extension");
                                                        var parameters = knownValues.get("parameters");

                                                        messager.printMessage(NOTE, "KV : " + extensionClass + ", " + parameters);
                                                        messager.printMessage(NOTE,
                                                                "KV : " + extensionClass.getClass() + ", " + (parameters != null ? parameters.getClass()
                                                                        : "NULL"));

                                                        var className = extensionClass.toString();
                                                        var params = (parameters != null) ?
                                                                ((List<Object>) parameters).stream().map(String::valueOf).toArray(i -> new String[i]) :
                                                                new String[0];

                                                        try {
                                                            ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
                                                            var aClass = Class.forName(className, true, systemClassLoader);
//                                                                var aClass = Class.forName(className);
                                                            var ext = aClass.getDeclaredConstructor().newInstance();
                                                            var extension = (AutoRecordExtension) ext;
                                                            extension.setParameters(params);
                                                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                                                                 InvocationTargetException | NoSuchMethodException e) {
                                                            throw new AutoRecordProcessorException("Cannot " + className, e);
                                                        }

//                                                            try {
//                                                                var classLoader = processingEnv.getClass().getClassLoader();
////                                                                var classLoader = ClassLoader.getPlatformClassLoader();
//                                                                Class<?> aClass = classLoader.loadClass("pl.com.labaj.autorecord.extension
//                                                                .ReplaceCompactConstructorExtension");
//                                                                AutoRecordExtension o = (AutoRecordExtension) aClass.getDeclaredConstructor().newInstance();
//                                                                messager.printMessage(NOTE, "UU "+o);
//                                                            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
//                                                            IllegalAccessException | NoSuchMethodException e) {
//                                                                throw new RuntimeException(e);
//                                                            }
                                                    });
                                        });
                            });
                });
    }

    private void processElement(TypeElement sourceInterface,
                                AutoRecord.Options recordOptions,
                                @Nullable RecordBuilder.Options builderOptions,
                                List<AutoRecordExtension> extensions) {
        var logger = new MessagerLogger(processingEnv.getMessager(), sourceInterface);
        logger.info("Generate record for %s".formatted(sourceInterface));

        try {
            var recordGenerator = new RecordJavaFileBuilder(sourceInterface, recordOptions, builderOptions, extensions, processingEnv, logger);
            var javaFile = recordGenerator.buildJavaFile();
            javaFile.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            logger.error("Exception thrown during generation record", e);
        }
    }

    private AutoRecordExtension initExtension(AutoRecord.ExtendWith extendWith, Messager messager, Element sourceInterface) {
        var extension = createExtension(extendWith, messager, sourceInterface);
//        extension.setParameters(extendWith.parameters());
        return extension;
    }

    private AutoRecordExtension createExtension(AutoRecord.ExtendWith extendWith, Messager messager, Element sourceInterface) {
        try {
            var extensionClass = extendWith.extension();
            messager.printMessage(NOTE, "Class from annotation: " + extensionClass, sourceInterface);
            try {
                return extensionClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//                throw new AutoRecordProcessorException("Cannot construct %s extension".formatted(extensionClass.getName()), e);
            }
        } catch (MirroredTypeException e) {
            var typeMirror = e.getTypeMirror();
            var extensionClassName = typeMirror.toString();
            messager.printMessage(NOTE, "Class name from typeMirror: " + extensionClassName, sourceInterface);

            try {
//                var extensionClass = Class.forName(extensionClassName, true, ClassLoader.getPlatformClassLoader());
//                var extensionClass = Class.forName(extensionClassName, true, ClassLoader.getSystemClassLoader());
                var extensionClass = Class.forName(extensionClassName);
                messager.printMessage(NOTE, "Class from classloader: " + extensionClass, sourceInterface);

                try {
                    return (AutoRecordExtension) extensionClass.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
//                    throw new AutoRecordProcessorException("Cannot construct %s extension".formatted(extensionClass.getName()), ex);
                    messager.printMessage(NOTE, "Second ex: " + ex, sourceInterface);
                }
            } catch (ClassNotFoundException ex) {
                messager.printMessage(NOTE, "CL: cannot find " + extensionClassName, sourceInterface);
//                throw new AutoRecordProcessorException("Cannot construct %s extension".formatted(extensionClassName), ex);
            }

            var loader = ServiceLoader.load(AutoRecordExtension.class);

            var autoRecordExtension = loader.stream()
                    .peek(ext -> messager.printMessage(NOTE, "EXT: " + ext))
                    .filter(ext -> ext.getClass().getName().equals(extensionClassName))
                    .map(ServiceLoader.Provider::get)
                    .peek(ext -> messager.printMessage(NOTE, "Extension from service loader: " + ext, sourceInterface))
                    .findAny()
//                    .orElseThrow(() -> new AutoRecordProcessorException("Cannot construct %s extension".formatted(extensionClassName)));
                    .orElse(null);

            if (autoRecordExtension == null) {
                messager.printMessage(NOTE, "SL got null for " + extensionClassName, sourceInterface);
            }

            return autoRecordExtension;
        }
        return null;
    }
}
