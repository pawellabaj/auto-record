package wiki;
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

import io.soabase.recordbuilder.core.RecordBuilder;
import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.Ignored;
import pl.com.labaj.autorecord.Memoized;
import pl.com.labaj.autorecord.extension.compact.LoggingExtension;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@AutoRecord
@AutoRecord.Options(withBuilder = true, memoizedHashCode = true, memoizedToString = true)
@RecordBuilder.Options(suffix = "_Builder")
@AutoRecord.Extension(extensionClass = LoggingExtension.class)
interface Full<D, H extends Comparable<H>> {
    String name();

    Map<D, H> aMap();

    int size();

    @Nullable
    @Ignored
    Long nullableProperty();

    List<D> genericCollection();

    String[] arrayProperty();

    D[] genericArrayProperty();

    @Ignored
    Supplier<H> hSupplier();

    @Memoized
    default H genericSlowProcessingMethod() {
        return hSupplier().get();
    }

    @Memoized
    default boolean booleanSlowProcessingMethod() {
        return true;
    }

    static <A extends CharSequence, B extends Comparable<B>> FullRecord_Builder<A, B> builder() {
        return FullRecord_Builder.builder();
    }

    FullRecord_Builder<D, H> toBuilder();
}
