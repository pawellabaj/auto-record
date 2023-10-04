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
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import pl.com.labaj.autorecord.Memoized;
import pl.com.labaj.autorecord.context.StaticImports;
import pl.com.labaj.autorecord.extension.AutoRecordExtension;
import pl.com.labaj.autorecord.processor.context.Memoization;
import pl.com.labaj.autorecord.processor.context.MemoizerType;
import pl.com.labaj.autorecord.processor.context.ProcessorContext;

import java.util.List;
import java.util.Set;

import static java.lang.annotation.ElementType.METHOD;
import static javax.lang.model.element.Modifier.PUBLIC;
import static pl.com.labaj.autorecord.processor.utils.Annotations.createAnnotationSpecs;

class MemoizationGenerator extends RecordGenerator {

    MemoizationGenerator(ProcessorContext context, List<AutoRecordExtension> extensions) {
        super(context, extensions);
    }

    @Override
    public void generate(TypeSpec.Builder recordBuilder, StaticImports staticImports) {
        context.memoization().ifPresent(items -> items.stream()
                .map(this::toMemoizedMethodSpec)
                .forEach(recordBuilder::addMethod));
    }

    private MethodSpec toMemoizedMethodSpec(Memoization.Item item) {
        var name = item.name();
        var annotations = createAnnotationSpecs(item.annotations(),
                Set.of(METHOD),
                List.of(Memoized.class, Override.class),
                List.of());
        var memoizerType = MemoizerType.from(item.type());

        var statement = methodStatement(item, name, memoizerType);

        return MethodSpec.methodBuilder(name)
                .addModifiers(PUBLIC)
                .addAnnotations(annotations)
                .returns(TypeName.get(item.type()))
                .addStatement(statement)
                .build();
    }

    private CodeBlock methodStatement(Memoization.Item item, String name, MemoizerType memoizerType) {
        var memoizerName = item.getMemoizerName();
        var computeMethod = memoizerType.computeMethod();

        if (item.internal()) {
            return CodeBlock.of("return $L.$L(this::_$L)", memoizerName, computeMethod, name);
        }

        return CodeBlock.of("return $L.$L($L.super::$L)", memoizerName, computeMethod, context.interfaceName(), name);
    }
}
