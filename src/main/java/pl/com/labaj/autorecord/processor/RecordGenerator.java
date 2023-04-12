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
import pl.com.labaj.autorecord.processor.context.AutoRecordContext;
import pl.com.labaj.autorecord.processor.context.Generation;
import pl.com.labaj.autorecord.processor.context.SourceInterface;
import pl.com.labaj.autorecord.processor.context.TargetRecord;
import pl.com.labaj.autorecord.processor.memoization.MemoizationFinder;
import pl.com.labaj.autorecord.processor.memoization.MemoizationGenerator;
import pl.com.labaj.autorecord.processor.special.HashCodeEqualsGenerator;
import pl.com.labaj.autorecord.processor.special.ToStringGenerator;
import pl.com.labaj.autorecord.processor.utils.Logger;
import pl.com.labaj.autorecord.processor.utils.Method;
import pl.com.labaj.autorecord.processor.utils.StaticImports;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.STATIC;
import static pl.com.labaj.autorecord.processor.utils.Annotations.getAnnotationWithEnforcedValues;

class RecordGenerator {
    private static final Map<String, Object> BUILDER_OPTIONS_ENFORCED_VALUES = Map.of("addClassRetainedGenerated", true);

    private final TypeElement sourceInterface;
    private final AutoRecord.Options recordOptions;
    private final RecordBuilder.Options builderOptions;
    private final ProcessingEnvironment processingEnv;
    private final MemoizationFinder memoizationFinder;
    private final Logger logger;
    private final List<Function<AutoRecordContext, SubGenerator>> subGenerators;

    RecordGenerator(TypeElement sourceInterface,
                    AutoRecord.Options recordOptions,
                    @Nullable RecordBuilder.Options builderOptions,
                    ProcessingEnvironment processingEnv,
                    Logger logger) {
        this.sourceInterface = sourceInterface;
        this.recordOptions = recordOptions;
        this.builderOptions = prepareBuilderOptions(builderOptions);
        this.processingEnv = processingEnv;

        memoizationFinder = new MemoizationFinder(processingEnv.getElementUtils());
        this.logger = logger;

        subGenerators = List.of(
                BasicGenerator::new,
                MemoizationGenerator::new,
                BuilderGenerator::new,
                HashCodeEqualsGenerator::new,
                ToStringGenerator::new
        );
    }

    private RecordBuilder.Options prepareBuilderOptions(@Nullable RecordBuilder.Options builderOptions) {
        return getAnnotationWithEnforcedValues(builderOptions, RecordBuilder.Options.class, BUILDER_OPTIONS_ENFORCED_VALUES);
    }

    JavaFile buildJavaFile() {
        var context = createContext();
        var recordBuilder = TypeSpec.recordBuilder(context.target().name());

        subGenerators.stream()
                .map(constructor -> constructor.apply(context))
                .forEach(subGenerator -> subGenerator.generate(recordBuilder));

        return buildJavaFile(context, recordBuilder.build());
    }

    private AutoRecordContext createContext() {
        var memoization = memoizationFinder.findMemoization(sourceInterface, recordOptions);

        var source = new SourceInterface(getInterfaceName(), sourceInterface.asType(), getPropertyMethods(), sourceInterface.getTypeParameters());
        var target = new TargetRecord(getPackageName(), createRecordName(), getRecordModifiers());
        var generation = new Generation(recordOptions, builderOptions, memoization, new StaticImports(), logger);

        return new AutoRecordContext(source, target, generation);
    }

    private String getPackageName() {
        return processingEnv.getElementUtils().getPackageOf(sourceInterface).getQualifiedName().toString();
    }

    private String createRecordName() {
        return getInterfaceName().replace('.', '_') + "Record";
    }

    private String getInterfaceName() {
        var qualifiedName = sourceInterface.getQualifiedName().toString();
        var packageName = getPackageName();

        var index = packageName.length();
        if (index > 0) {
            index++;
        }

        return qualifiedName.substring(index);
    }

    private Modifier[] getRecordModifiers() {
        return sourceInterface.getModifiers().stream()
                .filter(modifier -> modifier != ABSTRACT)
                .filter(modifier -> modifier != STATIC)
                .toArray(Modifier[]::new);
    }

    private List<ExecutableElement> getPropertyMethods() {
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

    private JavaFile buildJavaFile(AutoRecordContext context, TypeSpec recordSpec) {
        var javaFileBuilder = JavaFile.builder(context.target().packageName(), recordSpec);
        context.generation().staticImports().addTo(javaFileBuilder);

        return javaFileBuilder.build();
    }
}
