package pl.com.labaj.autorecord.extension;

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
import com.squareup.javapoet.MethodSpec;
import org.apiguardian.api.API;
import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.context.Context;
import pl.com.labaj.autorecord.context.StaticImports;

import java.util.List;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * Represents an extension of the {@link AutoRecord} Processor for customizing the compact constructor generation process.
 * <p>
 * Implement this interface to provide custom behavior for compact constructor generation.
 * <p><b>Compatibility Note:</b> Methods may be added to this interface in future releases of the library.
 *
 * @since 2.1.0
 */
@API(status = STABLE)
public interface CompactConstructorExtension extends AutoRecordExtension {
    /**
     * Determines whether the extension should generate the compact constructor content.
     *
     * @param isGeneratedByProcessor a flag indicating if the compact constructor is generated by the
     *                               {@link AutoRecord} Processor.
     *                               If the flag is {@code false}, it means that compact constructor won't be generated without extensions.
     * @param context                the {@link Context} object containing of the annotation processing.
     * @return {@code} if the compact constructor should be generated by the extension, {@code false} otherwise.
     */
    default boolean shouldGenerateCompactConstructor(boolean isGeneratedByProcessor, Context context) {
        return isGeneratedByProcessor;
    }

    /**
     * Provides a code to be inserted before the current content of the compact constructor.
     * <p>
     * <b>Note:</b> The method is called when {@link #shouldGenerateCompactConstructor(boolean, Context)} returns {@code true}.
     *
     * @param context       the {@link Context} object containing of the annotation processing.
     * @param staticImports the {@link StaticImports} object {@code static import} to statements that will be added into
     *                      generated record.
     * @return a {@link CodeBlock} representing the code to be inserted before the current content of the compact constructor.
     * @see CodeBlock
     */
    default CodeBlock prefixCompactConstructorContent(Context context, StaticImports staticImports) {
        return null;
    }

    /**
     * Provides a code to be inserted after the current content of the compact constructor.
     * <p>
     * <b>Note:</b> The method is called when {@link #shouldGenerateCompactConstructor(boolean, Context)} returns {@code true}.
     *
     * @param context       the {@link Context} object containing of the annotation processing.
     * @param staticImports the {@link StaticImports} object {@code static import} to statements that will be added into
     *                      generated record.
     * @return a {@link CodeBlock} representing the code to be inserted after the current content of the compact constructor.
     * @see CodeBlock
     */
    default CodeBlock suffixCompactConstructorContent(Context context, StaticImports staticImports) {
        return null;
    }

    /**
     * Provides list of additional methods that needs to be added to generated record.
     * <p>
     * <b>Notes:</b>
     * <ul>
     *     <li>The method is called when {@link #shouldGenerateCompactConstructor(boolean, Context)} returns {@code true}.</li>
     *     <li>The method is called after {@link #prefixCompactConstructorContent(Context, StaticImports)}
     *     and {@link #suffixCompactConstructorContent(Context, StaticImports)} methods.</li>
     * </ul>
     *
     * @param context       the {@link Context} object containing of the annotation processing.
     * @param staticImports the {@link StaticImports} object {@code static import} to statements that will be added into
     *                      generated record.
     * @return a list of {@link MethodSpec} representing additional methods that needs to be added to generated record
     * @see MethodSpec
     * @since 3.0.0
     */
    default List<MethodSpec> additionalMethodsToSupportCompactConstructor(Context context, StaticImports staticImports) {
        return List.of();
    }

    /**
     * Provides list of annotations that the generated record needs to be annotated with.
     * <p>
     * <b>Notes:</b>
     * <ul>
     *     <li>The method is called when {@link #shouldGenerateCompactConstructor(boolean, Context)} returns {@code true}.</li>
     *     <li>The method is called after {@link #prefixCompactConstructorContent(Context, StaticImports)}
     *     and {@link #suffixCompactConstructorContent(Context, StaticImports)} methods.</li>
     * </ul>
     *
     * @param context       the {@link Context} object containing of the annotation processing.
     * @param staticImports the {@link StaticImports} object {@code static import} to statements that will be added into
     *                      generated record.
     * @return a list of {@link AnnotationSpec} representing additional methods that needs to be added to generated record
     * @see AnnotationSpec
     * @since 3.0.0
     */
    default List<AnnotationSpec> annotationsToSupportCompactConstructor(Context context, StaticImports staticImports) {
        return List.of();
    }
}