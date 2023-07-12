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
import pl.com.labaj.autorecord.context.RecordComponent;
import pl.com.labaj.autorecord.context.StaticImports;
import pl.com.labaj.autorecord.extension.AutoRecordExtension;
import pl.com.labaj.autorecord.processor.context.ProcessorContext;

import java.util.List;

import static pl.com.labaj.autorecord.processor.context.InternalMethod.HASH_CODE;

class HashCodeEqualsGenerator implements RecordGenerator {
    private final HashCodeSubGenerator hashCodeSubGenerator = new HashCodeSubGenerator();
    private final EqualsSubGenerator equalsSubGenerator = new EqualsSubGenerator();

    @Override
    public void generate(ProcessorContext context, List<AutoRecordExtension> extensions, TypeSpec.Builder recordBuilder, StaticImports staticImports) {
        var isHashCodeMemoized = context.recordOptions().memoizedHashCode() || context.memoization().isMemoized(HASH_CODE);

        if (!shouldGenerate(context, isHashCodeMemoized)) {
            return;
        }

        var requiredComponents = context.components().stream()
                .filter(recordComponent -> recordComponent.isNotAnnotatedWith(Ignored.class))
                .toList();

        hashCodeSubGenerator.generate(staticImports, recordBuilder, isHashCodeMemoized, requiredComponents);
        equalsSubGenerator.generate(context, recordBuilder, isHashCodeMemoized, requiredComponents);
    }

    private boolean shouldGenerate(ProcessorContext context, boolean isHashCodeMemoized) {
        var atLeastOneIgnored = context.components().stream()
                .anyMatch(recordComponent -> recordComponent.isAnnotatedWith(Ignored.class));

        if (atLeastOneIgnored) {
            return true;
        }

        var atLeastOneArray = context.components().stream()
                .anyMatch(RecordComponent::isArray);

        if (atLeastOneArray) {
            return true;
        }

        return isHashCodeMemoized;
    }
}
