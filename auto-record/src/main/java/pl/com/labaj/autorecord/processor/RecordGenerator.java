package pl.com.labaj.autorecord.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import io.soabase.recordbuilder.core.RecordBuilder;
import pl.com.labaj.autorecord.AutoRecord;

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
                .addModifiers(recordModifiers)
                .addSuperinterface(sourceInterface.asType());

        var memoization = generateMemoizationParts(parameters, recordSpecBuilder);
        var recordComponents = generateConstructionParts(parameters, recordSpecBuilder, memoization);
        generateBuilderParts(parameters, recordSpecBuilder);
        generateHashCodeAndEqualsParts(parameters, recordSpecBuilder, memoization);
        generateToStringParts(parameters, recordSpecBuilder, memoization, recordComponents);

        return buildJavaFile(packageName, recordSpecBuilder.build(), staticImports);
    }

    private Memoization generateMemoizationParts(GeneratorParameters parameters, TypeSpec.Builder recordSpecBuilder) {
        return new MemoizedElementsGenerator(parameters, recordSpecBuilder)
                .createMemoization()
                .createMemoizedMethods()
                .returnMemoization();
    }

    private List<ParameterSpec> generateConstructionParts(GeneratorParameters parameters, TypeSpec.Builder recordSpecBuilder, Memoization memoization) {
        return new ConstructionPartsGenerator(parameters, recordSpecBuilder, memoization)
                .createTypeVariables()
                .createRecordComponents()
                .createAdditionalRecordComponents()
                .createAdditionalConstructor()
                .createCompactConstructor()
                .returnRecordComponents();
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
                .findNotIgnoredNames()
                .createHashCodeMethod()
                .createEqualsMethod();
    }

    private void generateToStringParts(GeneratorParameters parameters,
                                       TypeSpec.Builder recordSpecBuilder,
                                       Memoization memoization,
                                       List<ParameterSpec> recordComponents) {
        if (!memoization.memoizedToString()) {
            return;
        }

        new ToStringGenerator(parameters, recordSpecBuilder, recordComponents)
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
