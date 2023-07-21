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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import pl.com.labaj.autorecord.context.Logger;
import pl.com.labaj.autorecord.context.StaticImports;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Objects;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static pl.com.labaj.autorecord.extension.arice.Names.ARICE_PACKAGE;
import static pl.com.labaj.autorecord.extension.arice.ProcessedType.allProcessedTypes;

final class StatementGenerator {

    private static final EnumMap<ProcessedType, RecordStatementGenerator> RECORD_STATEMENT_GENERATORS = allProcessedTypes().stream()
            .collect(toMap(
                    identity(),
                    StatementGenerator::builderFor,
                    (b1, b2) -> b1,
                    () -> new EnumMap<>(ProcessedType.class)
            ));

    private final ExtensionContext extContext;
    private final TypesStructure structure;
    private final String methodsClassName;
    private final StaticImports staticImports;
    private final Logger logger;

    StatementGenerator(ExtensionContext extContext, TypesStructure structure, String methodsClassName, StaticImports staticImports, Logger logger) {
        this.extContext = extContext;
        this.structure = structure;
        this.methodsClassName = methodsClassName;
        this.staticImports = staticImports;
        this.logger = logger;
    }

    CodeBlock generateStatement(RecordComponent recordComponent) {
        var pType = recordComponent.pType();
        return RECORD_STATEMENT_GENERATORS.get(pType)
                .generateStatement(recordComponent, extContext, structure, methodsClassName, staticImports, logger);
    }

    private static RecordStatementGenerator builderFor(ProcessedType pType) {
        return (component, extensionContext, structure, methodsClassName, staticImports, logger) -> {
            var nullable = component.isNullable();
            if (nullable) {
                staticImports.add(Objects.class, "isNull");
            }

            var format = nullable ? "$1L = isNull($1L) ? null : $2T.$3L($1L)" : "$1L = $2T.$3L($1L)";

            if (structure.needsAdditionalMethod(pType)) {
                var className = ClassName.get(ARICE_PACKAGE, substringAfterLast(methodsClassName, "."));
                return CodeBlock.of(format, component.name(), className, pType.methodName());
            }

            return CodeBlock.of(format, component.name(), pType.factoryClassName(), pType.factoryMethodName());
        };
    }

    @FunctionalInterface
    private interface RecordStatementGenerator {
        @Nullable
        CodeBlock generateStatement(RecordComponent recordComponent,
                                    ExtensionContext extensionContext,
                                    TypesStructure structure,
                                    String methodsClassName,
                                    StaticImports staticImports,
                                    Logger logger);
    }
}
