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
import pl.com.labaj.autorecord.processor.utils.Methods;

import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.util.Elements;
import java.util.List;
import java.util.Map;

import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.utils.Annotations.createAnnotationIfNeeded;

public class ContextBuilder {
    private static final Map<String, Object> BUILDER_OPTIONS_ENFORCED_VALUES = Map.of("addClassRetainedGenerated", true);
    private final Elements elementUtils;
    private final MemoizationFinder memoizationFinder = new MemoizationFinder();
    private final SpecialMethodsFinder specialMethodsFinder = new SpecialMethodsFinder();
    private final ComponentsFinder componentsFinder = new ComponentsFinder();

    public ContextBuilder(Elements elementUtils) {
        this.elementUtils = elementUtils;
    }

    public ProcessorContext buildContext(TypeElement sourceInterface,
                                         @Nullable AutoRecord.Options recordOptions,
                                         @Nullable RecordBuilder.Options builderOptions,
                                         MessagerLogger logger) {
        var nonNullRecordOptions = createAnnotationIfNeeded(recordOptions, AutoRecord.Options.class);
        var nonNullBuilderOptions = createAnnotationIfNeeded(builderOptions, RecordBuilder.Options.class, BUILDER_OPTIONS_ENFORCED_VALUES);

        var allMethods = elementUtils.getAllMembers(sourceInterface).stream()
                .filter(Methods::isMethod)
                .map(ExecutableElement.class::cast)
                .toList();

        boolean isPublic = sourceInterface.getModifiers().contains(PUBLIC);
        var typeParameters = getTypeParameters(sourceInterface);

        var specialMethodAnnotations = specialMethodsFinder.findSpecialMethods(allMethods);
        var memoizationItems = memoizationFinder.findMemoizationItems(allMethods, nonNullRecordOptions, specialMethodsFinder::isSpecial);
        var components = componentsFinder.getComponents(allMethods, specialMethodsFinder::isNotSpecial);

        return new ProcessorContext(getPackageName(sourceInterface),
                nonNullRecordOptions,
                nonNullBuilderOptions,
                isPublic,
                sourceInterface.asType(),
                getInterfaceName(sourceInterface),
                components,
                typeParameters,
                new Generics(typeParameters),
                specialMethodAnnotations,
                new Memoization(memoizationItems),
                createRecordName(sourceInterface),
                logger);
    }

    private List<TypeParameterElement> getTypeParameters(TypeElement sourceInterface) {
        return sourceInterface.getTypeParameters().stream()
                .map(TypeParameterElement.class::cast)
                .toList();
    }

    private String getPackageName(TypeElement sourceInterface) {
        return elementUtils.getPackageOf(sourceInterface).getQualifiedName().toString();
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
