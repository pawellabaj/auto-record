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
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static pl.com.labaj.autorecord.extension.arice.InterfaceType.allProcessedTypes;

class CopyMethodsGenerator {
    private static final EnumMap<InterfaceType, MethodGenerator> METHOD_GENERATORS = allProcessedTypes().stream()
            .collect(toMap(
                    identity(),
                    CopyMethodsGenerator::builderFor,
                    (b1, b2) -> b1,
                    () -> new EnumMap<>(InterfaceType.class)
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

    private static MethodGenerator builderFor(InterfaceType iType) {
        return (extContext, structure, staticImports, logger) -> {
            var methodBuilder = getMethodBuilder(extContext, iType, logger);

            immutableTypesBlock(structure, iType)
                    .ifPresent(methodBuilder::addCode);
            subTypesBlocks(extContext, iType, structure, logger)
                    .forEach(methodBuilder::addCode);

            var returnStatement = isNull(iType.factoryClassName())
                    ? CodeBlock.of("return $L", iType.argumentName())
                    : CodeBlock.of("return $T.$L($L)", iType.factoryClassName(), iType.factoryMethodName(), iType.argumentName());
            methodBuilder.addStatement(returnStatement);

            return methodBuilder.build();
        };
    }

    private static MethodSpec.Builder getMethodBuilder(ExtensionContext extContext, InterfaceType iType, Logger logger) {
        var builder = MethodSpec.methodBuilder("immutable")
                .addModifiers(PUBLIC, STATIC);

        if (iType.genericNames().isEmpty()) {
            var annotation = AnnotationSpec.builder(SuppressWarnings.class)
                    .addMember("value", "$S", "unchecked")
                    .build();
            builder.addAnnotation(annotation);
        }

        getTypeName(extContext, iType, builder, true);
        var trimmedTypeName = getTypeName(extContext, iType, builder, false);

        var parameterSpec = ParameterSpec.builder(trimmedTypeName, iType.argumentName()).build();

        builder.returns(trimmedTypeName)
                .addParameter(parameterSpec);

        return builder;
    }

    private static TypeName getTypeName(ExtensionContext extContext, InterfaceType iType, MethodSpec.Builder methodBuilder, boolean full) {

        var mirrorType = extContext.getInterfaceMirrorType(iType);

        if (iType.genericNames().isEmpty()) {
            return TypeName.get(mirrorType);
        }

        var className = (ClassName) ClassName.get(mirrorType);
        var typeVariableNames = iType.genericNames().stream()
                .map(name -> full ? name : substringBefore(name, " "))
                .map(TypeVariableName::get)
                .toList();

        if (full) {
            typeVariableNames.forEach(methodBuilder::addTypeVariable);
        }
        return ParameterizedTypeName.get(className, typeVariableNames.toArray(TypeVariableName[]::new));
    }

    private static Optional<CodeBlock> immutableTypesBlock(TypesStructure structure, InterfaceType iType) {
        var immutableTypeNames = structure.getClassNames(iType);
        if (immutableTypeNames.isEmpty()) {
            return Optional.empty();
        }

        var ifFormat = new StringBuilder("if (");
        var i = 0;
        for (var iterator = immutableTypeNames.iterator(); iterator.hasNext(); i++) {
            iterator.next();
            String name = iType.argumentName();

            ifFormat.append(name).append(" instanceof $T");
            if (!iType.genericNames().isEmpty()) {
                var genericClause = iType.genericNames().stream()
                        .map(n -> substringBefore(n, " "))
                        .collect(joining(", ", "<", ">"));
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
                .addStatement(size > 1 ? "$<$<return $L" : "return $L", iType.argumentName())
                .endControlFlow()
                .build();

        return Optional.of(block);
    }

    private static List<CodeBlock> subTypesBlocks(ExtensionContext extContext, InterfaceType iType, TypesStructure structure, Logger logger) {
        return iType.directSubTypes().stream()
                .filter(structure::needsAdditionalMethod)
                .sorted(reverseOrder())
                .map(subPType -> subTypeBlock(extContext, subPType, iType, structure))
                .toList();
    }

    private static CodeBlock subTypeBlock(ExtensionContext extContext, InterfaceType iType, InterfaceType parent, TypesStructure structure) {
        var argumentName = iType.argumentName();

        var parentGenericNames = parent.genericNames();
        var genericClause = parentGenericNames.isEmpty() || !iType.checkGenericInInstanceOf()
                ? ""
                : parentGenericNames.stream().collect(joining(",", "<", ">"));
        var statement = structure.needsAdditionalMethod(iType)
                ? CodeBlock.of("return $L($L)", "immutable", argumentName)
                : CodeBlock.of("return $T.$L($L)", iType.factoryClassName(), iType.factoryMethodName(), argumentName);

        return CodeBlock.builder()
                .beginControlFlow("if ($L instanceof $T$L $L)", parent.argumentName(), extContext.getInterfaceMirrorType(iType), genericClause, argumentName)
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
