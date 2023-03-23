package pl.com.labaj.autorecord.processor;

import io.soabase.recordbuilder.core.RecordBuilder;
import pl.com.labaj.autorecord.AutoRecord;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static javax.lang.model.element.ElementKind.INTERFACE;
import static pl.com.labaj.autorecord.processor.AnnotationsHelper.getAnnotation;
import static pl.com.labaj.autorecord.processor.AnnotationsHelper.getDefaultAnnotationIfNotPresent;

@SupportedAnnotationTypes("pl.com.labaj.autorecord.*")
public class AutoRecordProcessor extends AbstractProcessor {

    static final String AUTO_RECORD_CLASS_NAME = AutoRecord.class.getName();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> processAnnotation(roundEnv, annotation));

        return true;
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
            var recordOptions = getDefaultAnnotationIfNotPresent(sourceInterface, AutoRecord.Options.class);
            var builderOptions = getDefaultAnnotationIfNotPresent(sourceInterface, RecordBuilder.Options.class);

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

    private void processElement(TypeElement sourceInterface, AutoRecord.Options recordOptions, RecordBuilder.Options builderOptions) {
        var logger = new MessagerLogger(processingEnv.getMessager(), sourceInterface);
        logger.debug("Generate record for %s".formatted(sourceInterface));

        try {
            var recordGenerator = new RecordGenerator(sourceInterface, recordOptions, builderOptions, processingEnv, logger);
            var javaFile = recordGenerator.buildJavaFile();
            javaFile.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            logger.error("Exception thrown during generation record", e);
        }
    }
}
