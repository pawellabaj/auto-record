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
import pl.com.labaj.autorecord.processor.AutoRecordProcessorException;
import pl.com.labaj.autorecord.processor.utils.Generics;
import pl.com.labaj.autorecord.processor.utils.Logger;
import pl.com.labaj.autorecord.processor.utils.Methods;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.function.Predicate;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.utils.Methods.hasParameters;
import static pl.com.labaj.autorecord.processor.utils.Methods.isVoid;

public class ContextBuilder {
    private final MemoizationFinder memoizationFinder;
    private final Elements elementUtils;
    private final TypeElement sourceInterface;
    private final AutoRecord.Options recordOptions;
    private final RecordBuilder.Options builderOptions;
    private final Logger logger;

    public ContextBuilder(Elements elementUtils,
                          TypeElement sourceInterface,
                          AutoRecord.Options recordOptions,
                          RecordBuilder.Options builderOptions,
                          Logger logger) {
        memoizationFinder = new MemoizationFinder(elementUtils);
        this.elementUtils = elementUtils;
        this.sourceInterface = sourceInterface;
        this.recordOptions = recordOptions;
        this.builderOptions = builderOptions;
        this.logger = logger;
    }

    public GenerationContext buildContext() {
        var memoization = memoizationFinder.findMemoization(sourceInterface, recordOptions);

        boolean isPublic = sourceInterface.getModifiers().contains(PUBLIC);
        var propertyMetods = elementUtils.getAllMembers(sourceInterface).stream()
                .filter(isMethod())
                .map(ExecutableElement.class::cast)
                .filter(Methods::isAbstract)
                .filter(this::hasNoParameters)
                .filter(this::doesNotReturnVoid)
                .filter(Methods::isNotSpecial)
                .toList();
        var specialMethods = elementUtils.getAllMembers(sourceInterface).stream()
                .filter(element -> element.getKind() == METHOD)
                .map(ExecutableElement.class::cast)
                .filter(Methods::isAbstract)
                .filter(Methods::hasNoParameters)
                .filter(Methods::isNotVoid)
                .filter(Methods::isSpecial)
                .collect(toMap(SpecialMethod::fromMethod, identity()));

        return new GenerationContext(
                isPublic,
                getInterfaceName(),
                sourceInterface.asType(),
                recordOptions,
                builderOptions,
                propertyMetods,
                specialMethods,
                new Generics(sourceInterface.getTypeParameters()),
                memoization,
                getPackageName(),
                createRecordName(),
                logger);
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

    private Predicate<Element> isMethod() {
        return element -> element.getKind() == METHOD;
    }

    private boolean hasNoParameters(ExecutableElement method) {
        if (hasParameters(method)) {
            throw new AutoRecordProcessorException("The interface cannot have abstract method with parameters: %s".formatted(method.getSimpleName()));
        }

        return true;
    }

    private boolean doesNotReturnVoid(ExecutableElement method) {
        if (isVoid(method)) {
            throw new AutoRecordProcessorException("The interface cannot have abstract method returning void: %s".formatted(method.getSimpleName()));
        }

        return true;
    }
}
