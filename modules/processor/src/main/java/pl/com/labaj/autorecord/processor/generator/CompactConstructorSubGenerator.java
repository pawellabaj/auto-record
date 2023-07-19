package pl.com.labaj.autorecord.processor.generator;

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

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import pl.com.labaj.autorecord.context.RecordComponent;
import pl.com.labaj.autorecord.context.StaticImports;
import pl.com.labaj.autorecord.extension.CompactConstructorExtension;
import pl.com.labaj.autorecord.processor.AutoRecordProcessor;
import pl.com.labaj.autorecord.processor.context.MemoizerType;
import pl.com.labaj.autorecord.processor.context.ProcessorContext;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static java.util.Objects.nonNull;
import static javax.lang.model.element.Modifier.PUBLIC;

class CompactConstructorSubGenerator {
    private static final String AUTO_RECORD_PROCESSOR_NAME = AutoRecordProcessor.class.getName();
    private static final String OBJECTS_REQUIRE_NON_NULL = "requireNonNull";
    private static final String OBJECTS_REQUIRE_NON_NULL_ELSE_GET = "requireNonNullElseGet";
    private final ProcessorContext context;
    private final List<CompactConstructorExtension> extensions;
    private final Deque<CodeElement> elements = new LinkedList<>();

    CompactConstructorSubGenerator(ProcessorContext context, List<CompactConstructorExtension> extensions) {
        this.context = context;
        this.extensions = extensions;
    }

    void generate(TypeSpec.Builder recordBuilder, StaticImports staticImports) {
        var nonNullNames = context.components().stream()
                .filter(RecordComponent::isNotPrimitive)
                .filter(rc -> rc.isNotAnnotatedWith(Nullable.class))
                .map(RecordComponent::name)
                .toList();

        var memoization = context.memoization();

        var generatedByProcessor = !nonNullNames.isEmpty() || !memoization.isEmpty();
        var filteredExtensions = extensions.stream()
                .filter(extension -> extension.shouldGenerate(generatedByProcessor, context))
                .toList();

        if (!generatedByProcessor && filteredExtensions.isEmpty()) {
            return;
        }

        var compactConstructor = generateCompactConstructor(staticImports, generatedByProcessor, filteredExtensions, nonNullNames);

        recordBuilder.compactConstructor(compactConstructor);
    }

    private MethodSpec generateCompactConstructor(StaticImports staticImports,
                                                  boolean generatedByProcessor,
                                                  List<CompactConstructorExtension> filteredExtensions,
                                                  List<String> nonNullNames) {
        collectElements(filteredExtensions, extension -> extension.prefixCompactConstructorContent(context, staticImports), elements::addFirst);

        if (generatedByProcessor) {
            var processorContent = processorContent(staticImports, nonNullNames);
            var sourceCode = new CodeElement(extensions.isEmpty() ? null : AUTO_RECORD_PROCESSOR_NAME, processorContent);
            elements.add(sourceCode);
        }

        collectElements(filteredExtensions, extension -> extension.suffixCompactConstructorContent(context, staticImports), elements::addLast);

        var compactConstructorBuilder = constructorBuilder()
                .addModifiers(getMainModifiers());

        addElementsTo(compactConstructorBuilder);

        return compactConstructorBuilder.build();
    }

    private void collectElements(List<CompactConstructorExtension> filteredExtensions,
                                 Function<CompactConstructorExtension, CodeBlock> codeFunction,
                                 Consumer<CodeElement> finisher) {
        filteredExtensions.stream()
                .map(extension -> CodeElement.of(extension, codeFunction))
                .filter(cs -> nonNull(cs.code))
                .forEach(finisher);
    }

    private void addElementsTo(MethodSpec.Builder methodBuilder) {
        for (var iterator = elements.iterator(); iterator.hasNext(); ) {
            var sourceCode = iterator.next();

            if (nonNull(sourceCode.sourceName)) {
                methodBuilder.addComment(sourceCode.sourceName);
            }

            methodBuilder.addCode(sourceCode.code);

            if (iterator.hasNext()) {
                methodBuilder.addCode("\n");
            }
        }
    }

    private CodeBlock processorContent(StaticImports staticImports, List<String> nonNullNames) {
        var memoization = context.memoization();
        var blockBuilder = CodeBlock.builder();

        if (!nonNullNames.isEmpty()) {
            nonNullNames.forEach(name -> blockBuilder.addStatement("$1L($2N, \"$2N must not be null\")", OBJECTS_REQUIRE_NON_NULL, name));
            staticImports.add(Objects.class, OBJECTS_REQUIRE_NON_NULL);
        }

        if (!nonNullNames.isEmpty() && memoization.isPresent()) {
            blockBuilder.add("\n");
        }

        memoization.ifPresent(items -> {
            items.forEach(item -> {
                var memoizerType = MemoizerType.from(item.type());
                var newReference = memoizerType.getNewReference();
                blockBuilder.addStatement("$2N = $1L($2N, $3L)", OBJECTS_REQUIRE_NON_NULL_ELSE_GET, item.getMemoizerName(), newReference);
            });
            staticImports.add(Objects.class, OBJECTS_REQUIRE_NON_NULL_ELSE_GET);
        });

        return blockBuilder.build();
    }

    private Modifier[] getMainModifiers() {
        return context.isRecordPublic() ? new Modifier[] {PUBLIC} : new Modifier[0];
    }

    record CodeElement(String sourceName, CodeBlock code) {

        static CodeElement of(CompactConstructorExtension extension, Function<CompactConstructorExtension, CodeBlock> codeFunction) {
            return new CodeElement(extension.getClass().getName(), codeFunction.apply(extension));
        }
    }
}
