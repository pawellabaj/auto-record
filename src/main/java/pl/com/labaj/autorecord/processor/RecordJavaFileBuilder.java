package pl.com.labaj.autorecord.processor;

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

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import io.soabase.recordbuilder.core.RecordBuilder;
import org.apiguardian.api.API;
import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.context.Logger;
import pl.com.labaj.autorecord.processor.context.ContextBuilder;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Map;

import static org.apiguardian.api.API.Status.INTERNAL;
import static pl.com.labaj.autorecord.processor.generator.Generators.generators;
import static pl.com.labaj.autorecord.processor.utils.Annotations.createAnnotationIfNeeded;

@API(status = INTERNAL)
class RecordJavaFileBuilder {
    private static final Map<String, Object> BUILDER_OPTIONS_ENFORCED_VALUES = Map.of("addClassRetainedGenerated", true);

    private final ContextBuilder contextBuilder;

    RecordJavaFileBuilder(TypeElement sourceInterface,
                          @Nullable AutoRecord.Options recordOptions,
                          @Nullable RecordBuilder.Options builderOptions,
                          ProcessingEnvironment processingEnv,
                          Logger logger) {
        var nonNullRecordOptions = createAnnotationIfNeeded(recordOptions, AutoRecord.Options.class);
        var nonNullBuilderOptions = createAnnotationIfNeeded(builderOptions, RecordBuilder.Options.class, BUILDER_OPTIONS_ENFORCED_VALUES);

        contextBuilder = new ContextBuilder(processingEnv.getElementUtils(), sourceInterface, nonNullRecordOptions, nonNullBuilderOptions, logger);
    }

    JavaFile buildJavaFile() {
        var context = contextBuilder.buildContext();
        var staticImports = new StaticImportsCollectors();
        var recordBuilder = TypeSpec.recordBuilder(context.recordName());

        generators()
                .forEach(generator -> generator.generate(context, staticImports, recordBuilder));

        return buildJavaFile(context.packageName(), staticImports, recordBuilder.build());
    }

    private JavaFile buildJavaFile(String packageName, StaticImportsCollectors staticImports, TypeSpec recordSpec) {
        var javaFileBuilder = JavaFile.builder(packageName, recordSpec);
        staticImports.forEach(javaFileBuilder::addStaticImport);

        return javaFileBuilder.build();
    }
}
