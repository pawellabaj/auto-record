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
import pl.com.labaj.autorecord.processor.utils.Annotations;

import java.util.List;

import static java.lang.annotation.ElementType.METHOD;
import static javax.lang.model.element.Modifier.PUBLIC;

class MemoizationGenerator implements RecordGenerator {

    @Override
    public void generate(ProcessorContext context, List<AutoRecordExtension> extensions, TypeSpec.Builder recordBuilder, StaticImports staticImports) {
        context.memoization().ifPresent(items -> items.stream()
                .map(item -> toMemoizedMethodSpec(context, item))
                .forEach(recordBuilder::addMethod));
    }

    private MethodSpec toMemoizedMethodSpec(ProcessorContext context, Memoization.Item item) {
        var name = item.name();
        var annotations = Annotations.createAnnotationSpecs(item.annotations(),
                METHOD,
                List.of(Memoized.class, Override.class),
                List.of());
        var memoizerType = MemoizerType.from(item.type());

        var statement = methodStatement(context, item, name, memoizerType);

        return MethodSpec.methodBuilder(name)
                .addModifiers(PUBLIC)
                .addAnnotations(annotations)
                .returns(TypeName.get(item.type()))
                .addStatement(statement)
                .build();
    }

    private CodeBlock methodStatement(ProcessorContext context, Memoization.Item item, String name, MemoizerType memoizerType) {
        var memoizerName = item.getMemoizerName();
        var computeMethod = memoizerType.computeMethod();

        if (item.internal()) {
            return CodeBlock.of("return $L.$L(this::_$L)", memoizerName, computeMethod, name);
        }

        return CodeBlock.of("return $L.$L($L.super::$L)", memoizerName, computeMethod, context.interfaceName(), name);
    }
}
