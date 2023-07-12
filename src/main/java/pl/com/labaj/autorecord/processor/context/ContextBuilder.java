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
import pl.com.labaj.autorecord.context.Logger;
import pl.com.labaj.autorecord.processor.utils.Methods;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.util.Elements;
import java.util.List;

import static javax.lang.model.element.Modifier.PUBLIC;

public class ContextBuilder {
    private final Elements elementUtils;
    private final TypeElement sourceInterface;
    private final AutoRecord.Options recordOptions;
    private final RecordBuilder.Options builderOptions;
    private final Logger logger;
    private final MemoizationFinder memoizationFinder = new MemoizationFinder();
    private final SpecialMethodsFinder specialMethodsFinder = new SpecialMethodsFinder();
    private final ComponentsFinder componentsFinder = new ComponentsFinder();

    public ContextBuilder(Elements elementUtils,
                          TypeElement sourceInterface,
                          AutoRecord.Options recordOptions,
                          RecordBuilder.Options builderOptions,
                          Logger logger) {
        this.elementUtils = elementUtils;
        this.sourceInterface = sourceInterface;
        this.recordOptions = recordOptions;
        this.builderOptions = builderOptions;
        this.logger = logger;
    }

    public ProcessorContext buildContext() {
        var allMethods = elementUtils.getAllMembers(sourceInterface).stream()
                .filter(Methods::isMethod)
                .map(ExecutableElement.class::cast)
                .toList();

        boolean isPublic = sourceInterface.getModifiers().contains(PUBLIC);
        var typeParameters = getTypeParameters();

        var specialMethodAnnotations = specialMethodsFinder.findSpecialMethods(allMethods);
        var memoizationItems = memoizationFinder.findMemoizationItems(allMethods, recordOptions, specialMethodsFinder::isSpecial);
        var components = componentsFinder.getComponents(allMethods, specialMethodsFinder::isNotSpecial);

        return new ProcessorContext(getPackageName(),
                recordOptions,
                builderOptions,
                isPublic,
                sourceInterface.asType(),
                getInterfaceName(),
                components,
                typeParameters,
                new Generics(typeParameters),
                specialMethodAnnotations,
                new Memoization(memoizationItems),
                createRecordName(),
                logger);
    }

    private List<TypeParameterElement> getTypeParameters() {
        return sourceInterface.getTypeParameters().stream()
                .map(TypeParameterElement.class::cast)
                .toList();
    }

    private String getPackageName() {
        return elementUtils.getPackageOf(sourceInterface).getQualifiedName().toString();
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

    private String createRecordName() {
        return getInterfaceName().replace('.', '_') + "Record";
    }
}
