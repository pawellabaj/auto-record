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
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import pl.com.labaj.autorecord.context.Logger;
import pl.com.labaj.autorecord.context.StaticImports;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

import static java.util.Comparator.reverseOrder;
import static java.util.Objects.isNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static pl.com.labaj.autorecord.extension.arice.ProcessedType.allProcessedTypes;

class CopyMethodsGenerator {
    private static final EnumMap<ProcessedType, MethodGenerator> METHOD_GENERATORS = allProcessedTypes().stream()
            .collect(toMap(
                    identity(),
                    CopyMethodsGenerator::builderFor,
                    (b1, b2) -> b1,
                    () -> new EnumMap<>(ProcessedType.class)
            ));

    private final ExtensionContext extContext;

    private final StaticImports staticImports;
    private final Logger logger;

    CopyMethodsGenerator(ExtensionContext extContext, StaticImports staticImports, Logger logger) {
        this.extContext = extContext;
        this.staticImports = staticImports;
        this.logger = logger;
    }

    List<MethodSpec> generateMethods(TypesStructure structure) {
        return allProcessedTypes().stream()
                .sorted(reverseOrder())
                .map(METHOD_GENERATORS::get)
                .map(methodGenerator -> methodGenerator.generateMethod(extContext, structure, staticImports, logger))
                .toList();
    }

    private static MethodGenerator builderFor(ProcessedType pType) {
        return (extContext, structure, staticImports, logger) -> {
            var methodBuilder = getMethodBuilder(extContext, pType, logger);

            immutableTypesBlock(structure, pType)
                    .ifPresent(methodBuilder::addCode);
            subTypesBlocks(extContext, pType, structure, logger)
                    .forEach(methodBuilder::addCode);

            var returnStatement = isNull(pType.factoryClassName())
                    ? CodeBlock.of("return $L", pType.argumentName())
                    : CodeBlock.of("return $T.$L($L)", pType.factoryClassName(), pType.factoryMethodName(), pType.argumentName());
            methodBuilder.addStatement(returnStatement);

            return methodBuilder.build();
        };
    }

    private static MethodSpec.Builder getMethodBuilder(ExtensionContext extContext, ProcessedType pType, Logger logger) {
        var builder = MethodSpec.methodBuilder("immutable")
                .addModifiers(PUBLIC, STATIC);

        var typeName = getTypeName(extContext, pType, builder, logger);
        var parameterSpec = ParameterSpec.builder(typeName, pType.argumentName()).build();

        builder.returns(typeName)
                .addParameter(parameterSpec);

        return builder;
    }

    private static TypeName getTypeName(ExtensionContext extContext, ProcessedType pType, MethodSpec.Builder builder, Logger logger) {
        TypeName typeName;

        if (pType.genericNames().isEmpty()) {
            typeName = TypeName.get(extContext.getProcessedType(pType, logger));

            var annotation = AnnotationSpec.builder(SuppressWarnings.class)
                    .addMember("value", "$S", "unchecked")
                    .build();
            builder.addAnnotation(annotation);
        } else {
            var className = ClassName.get(extContext.getElement(pType));
            var typeVariableNames = pType.genericNames().stream()
                    .map(TypeVariableName::get)
                    .toList();
            typeName = ParameterizedTypeName.get(className, typeVariableNames.toArray(TypeVariableName[]::new));

            typeVariableNames.forEach(builder::addTypeVariable);
        }

        return typeName;
    }

    private static Optional<CodeBlock> immutableTypesBlock(TypesStructure structure, ProcessedType pType) {
        var immutableTypeNames = structure.getClassNames(pType);
        if (immutableTypeNames.isEmpty()) {
            return Optional.empty();
        }

        var ifFormat = new StringBuilder("if (");
        var i = 0;
        for (var iterator = immutableTypeNames.iterator(); iterator.hasNext(); i++) {
            iterator.next();
            String name = pType.argumentName();

            ifFormat.append(name).append(" instanceof $T");
            if (!pType.genericNames().isEmpty()) {
                var genericClause = pType.genericNames().stream()
                        .collect(joining(",", "<", ">"));
                ifFormat.append(genericClause);
            }

            if (iterator.hasNext()) {
                ifFormat.append(i == 0 ? "\n$>$>|| " : "\n|| ");
            }
        }
        ifFormat.append(")");

        var size = immutableTypeNames.size();
        var block = CodeBlock.builder()
                .beginControlFlow(ifFormat.toString(), immutableTypeNames.toArray())
                .addStatement(size > 1 ? "$<$<return $L" : "return $L", pType.argumentName())
                .endControlFlow()
                .build();

        return Optional.of(block);
    }

    private static List<CodeBlock> subTypesBlocks(ExtensionContext extContext, ProcessedType pType, TypesStructure structure, Logger logger) {
        return pType.directSubTypes().stream()
                .filter(structure::needsAdditionalMethod)
                .sorted(reverseOrder())
                .map(subPType -> subTypeBlock(extContext, subPType, pType, structure, logger))
                .toList();
    }

    private static CodeBlock subTypeBlock(ExtensionContext extContext, ProcessedType pType, ProcessedType parent, TypesStructure structure, Logger logger) {
        var argumentName = pType.argumentName();

        var parentGenericNames = parent.genericNames();
        var genericClause = parentGenericNames.isEmpty() || !pType.checkGenericInInstanceOf()
                ? ""
                : parentGenericNames.stream().collect(joining(",", "<", ">"));
        var statement = structure.needsAdditionalMethod(pType)
                ? CodeBlock.of("return $L($L)", "immutable", argumentName)
                : CodeBlock.of("return $T.$L($L)", pType.factoryClassName(), pType.factoryMethodName(), argumentName);

        return CodeBlock.builder()
                .beginControlFlow("if ($L instanceof $T$L $L)", parent.argumentName(), extContext.getProcessedType(pType, logger), genericClause, argumentName)
                .addStatement(statement)
                .endControlFlow()
                .build();
    }

    @FunctionalInterface
    private interface MethodGenerator {
        MethodSpec generateMethod(ExtensionContext extContext,
                                  TypesStructure structure,
                                  StaticImports staticImports,
                                  Logger logger);
    }
}
