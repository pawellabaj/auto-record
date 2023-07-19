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

class HashCodeEqualsGenerator extends RecordGenerator {
    private final HashCodeSubGenerator hashCodeSubGenerator;
    private final EqualsSubGenerator equalsSubGenerator;

    HashCodeEqualsGenerator(ProcessorContext context, List<AutoRecordExtension> extensions) {
        super(context, extensions);
        hashCodeSubGenerator = new HashCodeSubGenerator();
        equalsSubGenerator = new EqualsSubGenerator(context);
    }

    @Override
    public void generate(TypeSpec.Builder recordBuilder, StaticImports staticImports) {
        var isHashCodeMemoized = context.recordOptions().memoizedHashCode() || context.memoization().isMemoized(HASH_CODE);

        if (!shouldGenerate(isHashCodeMemoized)) {
            return;
        }

        var requiredComponents = context.components().stream()
                .filter(recordComponent -> recordComponent.isNotAnnotatedWith(Ignored.class))
                .toList();

        hashCodeSubGenerator.generate(recordBuilder, staticImports, isHashCodeMemoized, requiredComponents);
        equalsSubGenerator.generate(recordBuilder, isHashCodeMemoized, requiredComponents);
    }

    private boolean shouldGenerate(boolean isHashCodeMemoized) {
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
