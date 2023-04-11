package pl.com.labaj.autorecord.processor.memoization;

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

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import pl.com.labaj.autorecord.Memoized;
import pl.com.labaj.autorecord.processor.GeneratorMetaData;
import pl.com.labaj.autorecord.processor.StaticImport;
import pl.com.labaj.autorecord.processor.SubGenerator;
import pl.com.labaj.autorecord.processor.utils.Annotations;
import pl.com.labaj.autorecord.processor.utils.Logger;

import java.lang.annotation.ElementType;
import java.util.List;

import static java.util.Collections.emptyList;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.DEFAULT;
import static pl.com.labaj.autorecord.processor.memoization.TypeMemoizer.typeMemoizerWith;

public class MemoizationGenerator extends SubGenerator {

    public MemoizationGenerator(GeneratorMetaData metaData) {
        super(metaData);
    }

    @Override
    public void generate(TypeSpec.Builder recordSpecBuilder, List<StaticImport> staticImports, Logger logger) {
        metaData.memoization().items().stream()
                .map(this::toMethodSpec)
                .forEach(recordSpecBuilder::addMethod);
    }

    private MethodSpec toMethodSpec(Memoization.Item item) {
        var name = item.name();
        var annotations = Annotations.createAnnotationSpecs(item.annotations(),
                ElementType.METHOD,
                List.of(Memoized.class, Override.class),
                emptyList());
        var modifiers = item.modifiers().stream()
                .filter(modifer -> modifer != ABSTRACT)
                .filter(modifier -> modifier != DEFAULT)
                .toList();
        var supplierName = item.special() ? "_" + name : metaData.interfaceName() + ".super." + name;

        var typeMemoizer = typeMemoizerWith(item.type());

        return MethodSpec.methodBuilder(name)
                .addModifiers(modifiers)
                .addAnnotations(annotations)
                .returns(TypeName.get(item.type()))
                .addStatement(typeMemoizer.getReturnStatement(), name, supplierName)
                .build();
    }
}
