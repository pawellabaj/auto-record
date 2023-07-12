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
import com.squareup.javapoet.TypeSpec;
import pl.com.labaj.autorecord.context.Context;
import pl.com.labaj.autorecord.context.StaticImports;
import pl.com.labaj.autorecord.extension.CompactConstructorExtension;
import pl.com.labaj.autorecord.extension.ContentOperation;

import static pl.com.labaj.autorecord.extension.ContentOperation.REPLACE;

public class ReplaceCompactConstructorExtension implements CompactConstructorExtension {
    @Override
    public void setParameters(String[] parameters) {}

    @Override
    public boolean shouldGenerate(boolean isGeneratedByProcessor, Context context) {
        return true;
    }

    @Override
    public ContentOperation contentOperation() {
        return REPLACE;
    }

    @Override
    public CodeBlock generateContent(Context context, TypeSpec.Builder recordBuilder, StaticImports staticImports) {
        return CodeBlock.of("//Compact constructor content replaced");
    }
}
