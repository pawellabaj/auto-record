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

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.lang.Object;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.extension.arice.ARICE;
import pl.com.labaj.autorecord.extension.arice.ARICEUtilities;

@Generated(
        comments = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension",
        value = "pl.com.labaj.autorecord.AutoRecord"
)
@GeneratedWithAutoRecord
@ARICEUtilities(className = "pl.com.labaj.autorecord.extension.arice.ARICE")
record ItemWithObjectRecord(Object object, @Nullable Object nullableObject) implements ItemWithObject {
    ItemWithObjectRecord {
        // pl.com.labaj.autorecord.processor.AutoRecordProcessor
        requireNonNull(object, "object must not be null");

        // pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension
        object = ARICE.immutable(object);
        nullableObject = isNull(nullableObject) ? null : ARICE.immutable(nullableObject);
    }
}