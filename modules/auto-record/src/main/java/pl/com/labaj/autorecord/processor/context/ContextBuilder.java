package pl.com.labaj.autorecord.processor.context;

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
import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.extension.AutoRecordExtension;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.utils.Annotations.createAnnotationIfNeeded;

public class ContextBuilder {
    private static final String IMMUTABLE_COLLECTIONS_EXTENSION = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension";
    private static final Map<String, Object> DEFAULT_BUILDER_OPTIONS_ENFORCED_VALUES = Map.of("addClassRetainedGenerated", true);
    private static final Map<String, Object> WITH_IMMUTABLE_COLLECTIONS_BUILDER_OPTIONS_ENFORCED_VALUES = Map.of(
            "addClassRetainedGenerated", true,
            "useImmutableCollections", false,
            "useUnmodifiableCollections", false
    );
    private static final String PL_COM_LABAJ_AUTORECORD = "pl.com.labaj.autorecord";
    private static final String PL_COM_LABAJ_AUTORECORD_EXTENSION = "pl.com.labaj.autorecord.extension";
    private static final String IO_SOABASE_RECORDBUILDER_CORE = "io.soabase.recordbuilder.core";

    private final ProcessingEnvironment processingEnv;
    private final MemoizationFinder memoizationFinder = new MemoizationFinder();
    private final SpecialMethodsFinder specialMethodsFinder = new SpecialMethodsFinder();
    private final ComponentsFinder componentsFinder = new ComponentsFinder();

    public ContextBuilder(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    public ProcessorContext buildContext(TypeElement sourceInterface,
                                         @Nullable AutoRecord.Options recordOptions,
                                         @Nullable RecordBuilder.Options builderOptions,
                                         List<AutoRecordExtension> extensions,
                                         MessagerLogger logger,
                                         Consumer<MemoizerType> memoizerCollector) {
        var nonNullRecordOptions = createAnnotationIfNeeded(recordOptions, AutoRecord.Options.class);
        var nonNullBuilderOptions = createAnnotationIfNeeded(builderOptions, RecordBuilder.Options.class, getBuilderOptionsEnforcedValues(extensions));

        var elementUtils = processingEnv.getElementUtils();
        var typeUtils = processingEnv.getTypeUtils();
        var allMethods = elementUtils.getAllMembers(sourceInterface).stream()
                .filter(element -> element.getKind() == METHOD)
                .map(ExecutableElement.class::cast)
                .map(method -> Method.from(method, typeUtils, sourceInterface))
                .toList();

        boolean isPublic = sourceInterface.getModifiers().contains(PUBLIC);
        var interfaceAnnotations = getInterfaceAnnotations(sourceInterface);
        var typeParameters = getTypeParameters(sourceInterface);

        var specialMethodAnnotations = specialMethodsFinder.findSpecialMethods(allMethods);
        var memoizationItems = memoizationFinder.findMemoizationItems(allMethods, nonNullRecordOptions, specialMethodsFinder::isSpecial, logger);
        var components = componentsFinder.getComponents(allMethods, specialMethodsFinder::isNotSpecial);

        return new ProcessorContext(processingEnv,
                getPackageName(sourceInterface),
                nonNullRecordOptions,
                nonNullBuilderOptions,
                isPublic,
                interfaceAnnotations,
                sourceInterface.asType(),
                getInterfaceName(sourceInterface),
                components,
                typeParameters,
                new Generics(typeParameters),
                specialMethodAnnotations,
                new Memoization(memoizationItems),
                createRecordName(sourceInterface),
                logger,
                memoizerCollector);
    }

    private Map<String, Object> getBuilderOptionsEnforcedValues(List<AutoRecordExtension> extensions) {
        var possibleImmutableCollectionsExtension = extensions.stream()
                .filter(extension -> IMMUTABLE_COLLECTIONS_EXTENSION.equals(extension.getClass().getName()))
                .findAny();
        return possibleImmutableCollectionsExtension.isPresent() ?
                WITH_IMMUTABLE_COLLECTIONS_BUILDER_OPTIONS_ENFORCED_VALUES : DEFAULT_BUILDER_OPTIONS_ENFORCED_VALUES;
    }

    private List<AnnotationMirror> getInterfaceAnnotations(TypeElement sourceInterface) {
        var elementUtils = processingEnv.getElementUtils();

        return elementUtils.getAllAnnotationMirrors(sourceInterface).stream()
                .filter(annotationMirror -> hasPackageDifferentThan(annotationMirror, PL_COM_LABAJ_AUTORECORD))
                .filter(annotationMirror -> hasPackageDifferentThan(annotationMirror, PL_COM_LABAJ_AUTORECORD_EXTENSION))
                .filter(annotationMirror -> hasPackageDifferentThan(annotationMirror, IO_SOABASE_RECORDBUILDER_CORE))
                .map(AnnotationMirror.class::cast)
                .toList();
    }

    private boolean hasPackageDifferentThan(AnnotationMirror annotationMirror, String packageName) {
        var elementUtils = processingEnv.getElementUtils();
        var annotationPackage = elementUtils.getPackageOf(annotationMirror.getAnnotationType().asElement());

        return !annotationPackage.getQualifiedName().contentEquals(packageName);
    }

    private List<TypeParameterElement> getTypeParameters(TypeElement sourceInterface) {
        return sourceInterface.getTypeParameters().stream()
                .map(TypeParameterElement.class::cast)
                .toList();
    }

    private String getPackageName(TypeElement sourceInterface) {
        return processingEnv.getElementUtils().getPackageOf(sourceInterface).getQualifiedName().toString();
    }

    private String getInterfaceName(TypeElement sourceInterface) {
        var qualifiedName = sourceInterface.getQualifiedName().toString();
        var packageName = getPackageName(sourceInterface);

        var index = packageName.length();
        if (index > 0) {
            index++;
        }

        return qualifiedName.substring(index);
    }

    private String createRecordName(TypeElement sourceInterface) {
        return getInterfaceName(sourceInterface).replace('.', '_') + "Record";
    }
}
