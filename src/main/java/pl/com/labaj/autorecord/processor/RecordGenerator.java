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

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.soabase.recordbuilder.core.RecordBuilder;
import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;

import javax.annotation.processing.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

import static java.util.function.Predicate.not;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static pl.com.labaj.autorecord.processor.MethodHelper.doesNotReturnVoid;
import static pl.com.labaj.autorecord.processor.MethodHelper.hasNoParameters;

class RecordGenerator {
    private static final AnnotationSpec GENERATED_ANNOTATION = AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", AutoRecord.class.getName())
            .build();
    private static final AnnotationSpec GENERATED_WITH_AUTO_RECORD_ANNOTATION = AnnotationSpec.builder(GeneratedWithAutoRecord.class).build();
    private final TypeElement sourceInterface;
    private final AutoRecord.Options recordOptions;
    private final RecordBuilder.Options builderOptions;
    private final ProcessingEnvironment processingEnv;
    private final MessagerLogger logger;

    RecordGenerator(TypeElement sourceInterface,
                    AutoRecord.Options recordOptions,
                    RecordBuilder.Options builderOptions,
                    ProcessingEnvironment processingEnv,
                    MessagerLogger logger) {
        this.sourceInterface = sourceInterface;
        this.recordOptions = recordOptions;
        this.builderOptions = builderOptions;
        this.processingEnv = processingEnv;
        this.logger = logger;
    }

    JavaFile buildJavaFile() {
        var staticImports = new ArrayList<StaticImport>();
        var packageName = getPackageName(sourceInterface);
        var recordModifiers = getRecordModifiers(sourceInterface);
        var recordName = createRecordName(sourceInterface);
        var propertyMethods = getPropertyMethods(sourceInterface);

        var parameters = new GeneratorParameters(processingEnv,
                sourceInterface,
                recordOptions,
                builderOptions,
                staticImports,
                packageName,
                recordModifiers,
                recordName,
                propertyMethods,
                logger);

        var recordSpecBuilder = TypeSpec.recordBuilder(recordName)
                .addAnnotation(GENERATED_ANNOTATION)
                .addAnnotation(GENERATED_WITH_AUTO_RECORD_ANNOTATION)
                .addModifiers(recordModifiers)
                .addSuperinterface(sourceInterface.asType());

        var memoization = generateMemoizationParts(parameters, recordSpecBuilder);
        generateConstructionParts(parameters, recordSpecBuilder, memoization);
        generateBuilderParts(parameters, recordSpecBuilder);
        generateHashCodeAndEqualsParts(parameters, recordSpecBuilder, memoization);
        generateToStringParts(parameters, recordSpecBuilder, memoization);

        return buildJavaFile(packageName, recordSpecBuilder.build(), staticImports);
    }

    private Memoization generateMemoizationParts(GeneratorParameters parameters, TypeSpec.Builder recordSpecBuilder) {
        return new MemoizedPartsGenerator(parameters, recordSpecBuilder)
                .createMemoization()
                .createMemoizedMethods()
                .returnMemoization();
    }

    private void generateConstructionParts(GeneratorParameters parameters, TypeSpec.Builder recordSpecBuilder, Memoization memoization) {
        new ConstructionPartsGenerator(parameters, recordSpecBuilder, memoization)
                .createTypeVariables()
                .createAdditionalRecordComponents()
                .createAdditionalConstructor()
                .createCompactConstructor();
    }

    private void generateBuilderParts(GeneratorParameters parameters, TypeSpec.Builder recordSpecBuilder) {
        if (!parameters.recordOptions().withBuilder()) {
            return;
        }

        new BuilderPartsGenerator(parameters, recordSpecBuilder)
                .createRecordBuilderAnnotation()
                .createRecordBuilderOptionsAnnotation()
                .createBuilderMethod();
    }

    private void generateHashCodeAndEqualsParts(GeneratorParameters parameters, TypeSpec.Builder recordSpecBuilder, Memoization memoization) {
        new HashCodeEqualsGenerator(parameters, recordSpecBuilder, memoization)
                .findNotIgnoredProperties()
                .createHashCodeMethod()
                .createEqualsMethod();
    }

    private void generateToStringParts(GeneratorParameters parameters,
                                       TypeSpec.Builder recordSpecBuilder,
                                       Memoization memoization) {
        new ToStringGenerator(parameters, recordSpecBuilder, memoization)
                .createToStringMethod();
    }

    private String getPackageName(TypeElement sourceInterface) {
        return processingEnv.getElementUtils().getPackageOf(sourceInterface).getQualifiedName().toString();
    }

    private String createRecordName(TypeElement sourceInterface) {
        return sourceInterface.getSimpleName() + "Record";
    }

    private Modifier[] getRecordModifiers(TypeElement sourceInterface) {
        return sourceInterface.getModifiers().stream()
                .filter(modifier -> modifier != ABSTRACT)
                .toArray(Modifier[]::new);
    }

    private List<ExecutableElement> getPropertyMethods(TypeElement sourceInterface) {
        return processingEnv.getElementUtils().getAllMembers(sourceInterface).stream()
                .filter(element -> element.getKind() == METHOD)
                .map(ExecutableElement.class::cast)
                .filter(MethodHelper::isAbstract)
                .filter(method -> hasNoParameters(method, logger))
                .filter(method -> doesNotReturnVoid(method, logger))
                .filter(not(MethodHelper::isSpecial))
                .toList();
    }

    private JavaFile buildJavaFile(String packageName, TypeSpec recordSpec, ArrayList<StaticImport> staticImports) {
        var javaFileBuilder = JavaFile.builder(packageName, recordSpec);

        staticImports.forEach(staticImport -> javaFileBuilder.addStaticImport(staticImport.aClass(), staticImport.methodName()));

        return javaFileBuilder.build();
    }
}
