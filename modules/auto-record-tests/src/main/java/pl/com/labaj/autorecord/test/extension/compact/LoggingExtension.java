package pl.com.labaj.autorecord.test.extension.compact;

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
import pl.com.labaj.autorecord.context.RecordComponent;
import pl.com.labaj.autorecord.context.StaticImports;
import pl.com.labaj.autorecord.extension.CompactConstructorExtension;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;

public class LoggingExtension implements CompactConstructorExtension {

    private Level level;

    @Override
    public void init(ProcessingEnvironment processingEnv, String[] parameters) {
        if (parameters.length < 1) {
            level = FINE;
        } else {
            level = Level.parse(parameters[0].toUpperCase());
        }
    }

    @Override
    public boolean shouldGenerateCompactConstructor(boolean isGeneratedByProcessor, Context context) {
        return true;
    }

    @Override
    public CodeBlock prefixCompactConstructorContent(Context context, StaticImports staticImports) {
        var codeBuilder = CodeBlock.builder();

        codeBuilder.addStatement("var map = new $T<$T, $T>()", HashMap.class, String.class, Object.class);

        context.components().stream()
                .map(RecordComponent::name)
                .forEach(name -> codeBuilder.addStatement("map.put($S, $L)", name, name));

        codeBuilder.addStatement("var logger = getLogger($L.class.getName())", context.recordName())
                .addStatement("logger.log($L, $S, $L)", level, "Parameters passed to record: {0}", "map");

        staticImports.add(Logger.class, "getLogger")
                .add(Level.class, level.getName());

        return codeBuilder.build();
    }
}
