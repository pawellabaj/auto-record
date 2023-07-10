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

import com.squareup.javapoet.TypeSpec;
import pl.com.labaj.autorecord.Ignored;
import pl.com.labaj.autorecord.processor.StaticImportsCollector;
import pl.com.labaj.autorecord.processor.context.GenerationContext;
import pl.com.labaj.autorecord.processor.utils.Methods;

import static pl.com.labaj.autorecord.processor.context.InternalMethod.HASH_CODE;
import static pl.com.labaj.autorecord.processor.utils.Methods.isAnnotatedWith;
import static pl.com.labaj.autorecord.processor.utils.Methods.isNotAnnotatedWith;

class HashCodeEqualsGenerator implements RecordGenerator {
    private final HashCodeSubGenerator hashCodeSubGenerator = new HashCodeSubGenerator();
    private final EqualsSubGenerator equalsSubGenerator = new EqualsSubGenerator();

    @Override
    public void generate(GenerationContext context, StaticImportsCollector staticImports, TypeSpec.Builder recordBuilder) {
        var isHashCodeMemoized = context.recordOptions().memoizedHashCode() || context.memoization().isMemoized(HASH_CODE);

        if (!shouldGenerate(context, isHashCodeMemoized)) {
            return;
        }

        var requiredProperties = context.propertyMethods().stream()
                .filter(method -> isNotAnnotatedWith(method, Ignored.class))
                .toList();

        hashCodeSubGenerator.generate(staticImports, recordBuilder, isHashCodeMemoized, requiredProperties);
        equalsSubGenerator.generate(context, recordBuilder, isHashCodeMemoized, requiredProperties);
    }

    private boolean shouldGenerate(GenerationContext context, boolean isHashCodeMemoized) {
        if (context.propertyMethods().stream()
                .anyMatch(method -> isAnnotatedWith(method, Ignored.class))) {
            return true;
        }

        if (context.propertyMethods().stream()
                .anyMatch(Methods::returnsArray)) {
            return true;
        }

        return isHashCodeMemoized;
    }
}
