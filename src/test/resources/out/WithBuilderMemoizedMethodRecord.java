package pl.com.labaj.autorecord.testcase;

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

import static java.util.Objects.requireNonNullElseGet;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.lang.Override;
import java.lang.String;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.Memoized;
import pl.com.labaj.autorecord.memoizer.Memoizer;

@Generated("pl.com.labaj.autorecord.AutoRecord")
@GeneratedWithAutoRecord
@RecordBuilder
@RecordBuilder.Options(
        addClassRetainedGenerated = true
)
record WithBuilderMemoizedMethodRecord(@Nullable Memoizer<String> aMethodMemoizer) implements WithBuilderMemoizedMethod {
    WithBuilderMemoizedMethodRecord {
        aMethodMemoizer = requireNonNullElseGet(aMethodMemoizer, Memoizer::new);
    }

    WithBuilderMemoizedMethodRecord() {
        this(new Memoizer<>());
    }

    @Memoized
    @Override
    public String aMethod() {
        return aMethodMemoizer.computeIfAbsent(WithBuilderMemoizedMethod.super::aMethod);
    }

    static WithBuilderMemoizedMethodRecordBuilder builder() {
        return WithBuilderMemoizedMethodRecordBuilder.builder()
                .aMethodMemoizer(new Memoizer<>());
    }

    @Override
    public WithBuilderMemoizedMethodRecordBuilder toBuilder() {
        return WithBuilderMemoizedMethodRecordBuilder.builder(this)
                .aMethodMemoizer(new Memoizer<>());
    }
}
