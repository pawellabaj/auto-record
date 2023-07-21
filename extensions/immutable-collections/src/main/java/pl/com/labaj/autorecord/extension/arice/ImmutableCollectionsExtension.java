package pl.com.labaj.autorecord.extension.arice;

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
import com.squareup.javapoet.CodeBlock;
import org.apiguardian.api.API;
import pl.com.labaj.autorecord.context.Context;
import pl.com.labaj.autorecord.context.StaticImports;
import pl.com.labaj.autorecord.extension.CompactConstructorExtension;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.type.TypeKind.ARRAY;
import static org.apiguardian.api.API.Status.STABLE;
import static pl.com.labaj.autorecord.extension.arice.Names.PREDEFINED_IMMUTABLE_NAMES;
import static pl.com.labaj.autorecord.extension.arice.RecordComponent.debugInfo;

@API(status = STABLE)
public class ImmutableCollectionsExtension implements CompactConstructorExtension {

    private static final String METHODS_CLASS_NAME = "pl.com.labaj.autorecord.extension.arice.Methods";

    private static final ExtensionContext extContext = new ExtensionContext();

    private Set<String> immutableTypeNames;
    private Set<TypeMirror> immutableTypes;
    private List<RecordComponent> componentsToProcess;
    private String methodsClassName;

    @Override
    public void init(ProcessingEnvironment processingEnv, String[] parameters) {
        extContext.init(processingEnv);

        immutableTypeNames = Arrays.stream(parameters).collect(toSet());

        var immutableNames = new HashSet<String>();
        immutableNames.addAll(PREDEFINED_IMMUTABLE_NAMES);
        immutableNames.addAll(immutableTypeNames);

        immutableTypes = extContext.getTypes(immutableNames);

        methodsClassName = methodsClassName();
    }

    @Override
    public boolean shouldGenerateCompactConstructor(boolean isGeneratedByProcessor, Context context) {
        var logger = context.logger();

        var componentBuilder = new RecordComponent.Builder(extContext, immutableTypes, logger);
        var declaredComponents = context.components()
                .stream()
                .filter(recordComponent -> !recordComponent.type().getKind().isPrimitive())
                .filter(recordComponent -> recordComponent.type().getKind() != ARRAY)
                .toList();
        if (declaredComponents.isEmpty()) {
            return false;
        }

        componentsToProcess = declaredComponents.stream()
                .map(componentBuilder::toExtensionRecordComponent)
                .filter(Objects::nonNull)
                .toList();

        return !componentsToProcess.isEmpty();
    }

    @Override
    public CodeBlock suffixCompactConstructorContent(Context context, StaticImports staticImports) {
        var logger = context.logger();

        var structreBuilder = new TypesStructure.Builder(extContext, immutableTypes);
        var structure = structreBuilder.buildStructure(logger);

        if (logger.isDebugEnabled()) {
            logger.debug("Components to process:\n" + debugInfo(componentsToProcess));
            logger.note("Types structure:\n" + structure.debugInfo());
        }

        var codeBuilder = CodeBlock.builder();

        var statementGenerator = new StatementGenerator(extContext, structure, methodsClassName, staticImports, logger);

        componentsToProcess.stream()
                .map(statementGenerator::generateStatement)
                .forEach(codeBuilder::addStatement);

        return codeBuilder.build();
    }

    @Override
    public List<AnnotationSpec> annotationsToSupportCompactConstructor(Context context, StaticImports staticImports) {
        var builder = AnnotationSpec.builder(AutoRecordImmutableCollectionsUtilities.class)
                .addMember("className", "$S", methodsClassName);

        immutableTypeNames.forEach(parameter -> builder.addMember("immutableTypes", "$S", parameter));

        return List.of(builder.build());
    }

    private String methodsClassName() {
        return METHODS_CLASS_NAME + (immutableTypeNames.isEmpty() ? "" : "_" + stringHashCode(immutableTypeNames));
    }

    private String stringHashCode(Set<String> parameters) {
        var longHashCode = parameters.stream()
                .sorted()
                .mapToLong(String::hashCode)
                .reduce(31, (h1, h2) -> (31 * h1 + h2));
        var alphaString = Long.toUnsignedString(abs(longHashCode), 26);

        return alphaString.chars()
                .map(i -> Character.isLowerCase(i) ? i - 22 : i + 17)
                .map(i -> 65 + (i + 17) % 26)
                .mapToObj(Character::toString)
                .collect(joining());
    }
}
