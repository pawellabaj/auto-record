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

import static java.lang.System.out;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElseGet;

import java.lang.Override;
import java.lang.String;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.Memoized;
import pl.com.labaj.autorecord.memoizer.Memoizer;

@Generated("pl.com.labaj.autorecord.AutoRecord")
@GeneratedWithAutoRecord
record WithCompactConstructorExtensionsRecord(String property, @Nullable Memoizer<String> toStringMemoizer) implements WithCompactConstructorExtensions {
    WithCompactConstructorExtensionsRecord {
        // pl.com.labaj.autorecord.extension.compact.WhenProcessorCompactConstructorExtension
        System.err.println("Text printed at the beginning");

        // pl.com.labaj.autorecord.processor.AutoRecordProcessor
        requireNonNull(property, "property must not be null");

        toStringMemoizer = requireNonNullElseGet(toStringMemoizer, Memoizer::new);

        // pl.com.labaj.autorecord.extension.compact.AlwaysCompactConstructorExtension
        var params = "[Alpha]";
        out.println(params);

        // pl.com.labaj.autorecord.extension.compact.WhenProcessorCompactConstructorExtension
        System.err.println("Text printed at the end");
    }

    WithCompactConstructorExtensionsRecord(String property) {
        this(property, new Memoizer<>());
    }

    @Memoized
    @Override
    public String toString() {
        return toStringMemoizer.computeIfAbsent(this::_toString);
    }

    private String _toString() {
        return "WithCompactConstructorExtensionsRecord[" +
                "property = " + property +
                "]";
    }
}