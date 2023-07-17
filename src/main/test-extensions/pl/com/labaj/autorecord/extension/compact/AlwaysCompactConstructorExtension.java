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

import java.util.Arrays;

public class AlwaysCompactConstructorExtension implements CompactConstructorExtension {
    private String[] parameters;

    @Override
    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean shouldGenerate(boolean isGeneratedByProcessor, Context context) {
        return true;
    }

    @Override
    public CodeBlock suffixCompactConstructorContent(Context context, StaticImports staticImports) {
        staticImports.add(System.class, "out");

        return CodeBlock.builder()
                .addStatement("var params = $S", Arrays.toString(parameters))
                .addStatement("out.println(params)")
                .build();
    }
}
