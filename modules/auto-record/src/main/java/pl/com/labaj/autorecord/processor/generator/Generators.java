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

import pl.com.labaj.autorecord.extension.AutoRecordExtension;
import pl.com.labaj.autorecord.processor.context.ProcessorContext;

import java.util.List;
import java.util.function.BiFunction;

public final class Generators {
    private static final List<BiFunction<ProcessorContext, List<AutoRecordExtension>, RecordGenerator>> GENERATOR_CONSTRUCTORS = List.of(
            BasicGenerator::new,
            MemoizationGenerator::new,
            HashCodeEqualsGenerator::new,
            ToStringGenerator::new,
            BuilderGenerator::new
    );

    private Generators() {}

    public static List<RecordGenerator> generators(ProcessorContext context, List<AutoRecordExtension> extensions) {
        return GENERATOR_CONSTRUCTORS.stream()
                .map(constructor -> constructor.apply(context, extensions))
                .toList();
    }
}
