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
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.StringUtils;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.context.Logger;
import pl.com.labaj.autorecord.processor.AutoRecordProcessorException;
import pl.com.labaj.autorecord.processor.StaticImportsCollectors;
import pl.com.labaj.autorecord.processor.context.MessagerLogger;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Generated;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static pl.com.labaj.autorecord.extension.arice.Names.ARICE_PACKAGE;
import static pl.com.labaj.autorecord.extension.arice.Names.allImmutableNames;

class MethodsClassGenerator {
    private static final AnnotationSpec GENERATED_WITH_EXTENSION_ANNOTATION = AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", AutoRecordImmutableCollectionsProcessor.class.getName())
            .addMember("comments", "\"because of $L\"", ImmutableCollectionsExtension.class.getName())
            .build();
    private static final AnnotationSpec GENERATED_WITH_AUTO_RECORD_ANNOTATION = AnnotationSpec.builder(GeneratedWithAutoRecord.class).build();
    private static final List<AnnotationSpec> ANNOTATIONS = List.of(
            GENERATED_WITH_EXTENSION_ANNOTATION,
            GENERATED_WITH_AUTO_RECORD_ANNOTATION
    );
    private final ExtensionContext extContext;
    private final Filer filer;

    public MethodsClassGenerator(ExtensionContext extContext, Filer filer) {
        this.extContext = extContext;
        this.filer = filer;
    }

    public void generate(String className, String[] immutableTypes, MessagerLogger logger) {
        logStartEnd("[START] ", className, logger);

        try {
            var classSimpleName = StringUtils.removeStart(className, ARICE_PACKAGE + ".");
            var classBuilder = TypeSpec.classBuilder(classSimpleName);
            var staticImports = new StaticImportsCollectors();

            classBuilder.addAnnotations(ANNOTATIONS)
                    .addModifiers(PUBLIC, FINAL)
                    .addJavadoc(generateJavadoc(immutableTypes));

            var structure = fillStructure(immutableTypes, logger);
            var additionalMethodsGenerator = new CopyMethodsGenerator(extContext, staticImports, logger);
            additionalMethodsGenerator.generateMethods(structure)
                    .forEach(classBuilder::addMethod);

            var constructor = MethodSpec.constructorBuilder()
                    .addModifiers(PRIVATE)
                    .build();
            classBuilder.addMethod(constructor);

            var javaFile = buildJavaFile(staticImports, classBuilder.build());
            javaFile.writeTo(filer);

            logStartEnd("[ END ] ", className, logger);
        } catch (IOException e) {
            throw new AutoRecordProcessorException("Cannot generate file for " + className, e);
        }
    }

    private TypesStructure fillStructure(String[] names, Logger logger) {
        var structreBuilder = new TypesStructure.Builder(extContext, allImmutableNames(names));
        var structure = structreBuilder.buildStructure(logger);

        if (logger.isDebugEnabled()) {
            logger.note("Types structure:\n" + structure.debugInfo());
        }

        return structure;
    }

    private CodeBlock generateJavadoc(String[] immutableTypes) {
        var builder = CodeBlock.builder()
                .add("Class providing methods to copy collections to their corresponding immutable versions");

        if (immutableTypes.length > 0) {
            builder.add("\n")
                    .add("<p>\n")
                    .add("User defined immutable types:\n")
                    .add("<ul>\n");

            Arrays.stream(immutableTypes)
                    .map(type -> "\t<li>{@link " + type + "}</li>\n")
                    .forEach(builder::add);

            builder.add("</ul>");
        }
        return builder.build();
    }

    private JavaFile buildJavaFile(StaticImportsCollectors staticImports, TypeSpec classSpec) {
        var javaFileBuilder = JavaFile.builder(ARICE_PACKAGE, classSpec);
        staticImports.forEach(javaFileBuilder::addStaticImport);

        return javaFileBuilder.build();
    }

    private void logStartEnd(String prefix, String className, Logger logger) {
        if (logger.isDebugEnabled()) {
            var message = rightPad(prefix + className + " ", 100, "-");
            logger.note(message);
        }
    }
}
