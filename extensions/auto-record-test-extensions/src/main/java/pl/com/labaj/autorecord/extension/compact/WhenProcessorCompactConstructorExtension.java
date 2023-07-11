package pl.com.labaj.autorecord.extension.compact;

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
import pl.com.labaj.autorecord.context.Context;
import pl.com.labaj.autorecord.context.StaticImports;
import pl.com.labaj.autorecord.extension.CompactConstructorExtension;

public class WhenProcessorCompactConstructorExtension implements CompactConstructorExtension {

    @Override
    public boolean shouldGenerate(boolean isGeneratedByProcessor, Context context) {
        return isGeneratedByProcessor;
    }

    @Override
    public CodeBlock prefixCompactConstructorContent(Context context, StaticImports staticImports) {
        return CodeBlock.builder()
                .addStatement("System.err.println($S)", "Text printed at the beginning")
                .build();
    }

    @Override
    public CodeBlock suffixCompactConstructorContent(Context context, StaticImports staticImports) {
        return CodeBlock.builder()
                .addStatement("System.err.println($S)", "Text printed at the end")
                .build();
    }
}
