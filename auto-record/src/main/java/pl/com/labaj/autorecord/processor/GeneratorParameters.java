package pl.com.labaj.autorecord.processor;

import io.soabase.recordbuilder.core.RecordBuilder;
import pl.com.labaj.autorecord.AutoRecord;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("java:S6218")
record GeneratorParameters(ProcessingEnvironment processingEnv,
                           TypeElement sourceInterface,
                           AutoRecord.Options recordOptions,
                           RecordBuilder.Options builderOptions,
                           ArrayList<StaticImport> staticImports,
                           String packageName,
                           Modifier[] recordModifiers,
                           String recordName,
                           List<ExecutableElement> propertyMethods,
                           MessagerLogger logger) {}
