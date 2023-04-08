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
import com.squareup.javapoet.TypeSpec;
import io.soabase.recordbuilder.core.RecordBuilder;
import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.processor.utils.Method;
import pl.com.labaj.autorecord.processor.memoization.Memoization;
import pl.com.labaj.autorecord.processor.memoization.MemoizationFinder;
import pl.com.labaj.autorecord.processor.memoization.MemoizationGenerator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.ABSTRACT;

class RecordGenerator {

    private final TypeElement sourceInterface;
    private final AutoRecord.Options recordOptions;
    private final RecordBuilder.Options builderOptions;
    private final ProcessingEnvironment processingEnv;
    private final MemoizationFinder memoizationFinder;
    private final Logger logger;

    RecordGenerator(TypeElement sourceInterface,
                    AutoRecord.Options recordOptions,
                    RecordBuilder.Options builderOptions,
                    ProcessingEnvironment processingEnv,
                    Logger logger) {
        this.sourceInterface = sourceInterface;
        this.recordOptions = recordOptions;
        this.builderOptions = builderOptions;
        this.processingEnv = processingEnv;

        memoizationFinder = new MemoizationFinder(processingEnv.getElementUtils());
        this.logger = logger;
    }

    JavaFile buildJavaFile() {
        var staticImports = new ArrayList<StaticImport>();
        var packageName = getPackageName(sourceInterface);
        var recordModifiers = getRecordModifiers(sourceInterface);
        var recordName = createRecordName(sourceInterface);
        var propertyMethods = getPropertyMethods(sourceInterface);
        var memoization = memoizationFinder.findMemoization(sourceInterface, recordOptions);

        var metaData = new GeneratorMetaData(processingEnv,
                sourceInterface,
                recordOptions,
                builderOptions,
                staticImports,
                packageName,
                recordModifiers,
                recordName,
                propertyMethods,
                memoization,
                logger);

        var subGenerators = createSubGenerators(metaData);

        var recordSpecBuilder = TypeSpec.recordBuilder(recordName);
        subGenerators.forEach(subGenerator -> subGenerator.generate(recordSpecBuilder, staticImports, logger));

        generateBuilderParts(metaData, recordSpecBuilder);
        generateHashCodeAndEqualsParts(metaData, recordSpecBuilder, memoization);
        generateToStringParts(metaData, recordSpecBuilder, memoization);

        return buildJavaFile(packageName, recordSpecBuilder.build(), staticImports);
    }

    private static List<SubGenerator> createSubGenerators(GeneratorMetaData metaData) {
        return List.of(
                new BasicGenerator(metaData),
                new MemoizationGenerator(metaData)
        );
    }

    private void generateBuilderParts(GeneratorMetaData parameters, TypeSpec.Builder recordSpecBuilder) {
        if (!parameters.recordOptions().withBuilder()) {
            return;
        }

        new BuilderPartsGenerator(parameters, recordSpecBuilder)
                .createRecordBuilderAnnotation()
                .createRecordBuilderOptionsAnnotation()
                .createBuilderMethod()
                .createToBuilderMethod();
    }

    private void generateHashCodeAndEqualsParts(GeneratorMetaData parameters, TypeSpec.Builder recordSpecBuilder, Memoization memoization) {
        new HashCodeEqualsGenerator(parameters, recordSpecBuilder, memoization)
                .findNotIgnoredProperties()
                .createHashCodeMethod()
                .createEqualsMethod();
    }

    private void generateToStringParts(GeneratorMetaData parameters,
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
                .map(Method::new)
                .filter(Method::isAbstract)
                .filter(this::hasNoParameters)
                .filter(this::doesNotReturnVoid)
                .filter(Method::isNotSpecial)
                .map(Method::method)
                .toList();
    }

    private boolean hasNoParameters(Method method) {
        if (method.hasParameters()) {
            logger.error("The interface has abstract method with parameters: %s".formatted(method.methodeName()));
            return false;
        }

        return true;
    }

    private boolean doesNotReturnVoid(Method method) {
        if (method.returnsVoid()) {
            logger.error("The interface has abstract method returning void: %s".formatted(method.methodeName()));
            return false;
        }

        return true;
    }

    private JavaFile buildJavaFile(String packageName, TypeSpec recordSpec, ArrayList<StaticImport> staticImports) {
        var javaFileBuilder = JavaFile.builder(packageName, recordSpec);

        staticImports.forEach(staticImport -> javaFileBuilder.addStaticImport(staticImport.aClass(), staticImport.methodName()));

        return javaFileBuilder.build();
    }
}
