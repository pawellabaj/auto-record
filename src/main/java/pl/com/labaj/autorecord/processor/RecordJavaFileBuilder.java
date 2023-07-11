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
import org.apiguardian.api.API;
import pl.com.labaj.autorecord.extension.AutoRecordExtension;
import pl.com.labaj.autorecord.processor.context.ProcessorContext;

import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;
import static pl.com.labaj.autorecord.processor.generator.Generators.generators;

@API(status = INTERNAL)
class RecordJavaFileBuilder {

    JavaFile buildJavaFile(ProcessorContext context, List<AutoRecordExtension> extensions) {
        var recordBuilder = TypeSpec.recordBuilder(context.recordName());
        var staticImports = new StaticImportsCollectors();

        generators(context, extensions)
                .forEach(generator -> generator.generate(recordBuilder, staticImports));

        return buildJavaFile(context.packageName(), staticImports, recordBuilder.build());
    }

    private JavaFile buildJavaFile(String packageName, StaticImportsCollectors staticImports, TypeSpec recordSpec) {
        var javaFileBuilder = JavaFile.builder(packageName, recordSpec);
        staticImports.forEach(javaFileBuilder::addStaticImport);

        return javaFileBuilder.build();
    }
}
