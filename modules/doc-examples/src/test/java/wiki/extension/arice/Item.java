package wiki.extension.arice;
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

import pl.com.labaj.autorecord.AutoRecord;

import java.util.Set;

@AutoRecord
@AutoRecord.Extension(
        extensionClass = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension",
        parameters = "wiki.extension.arice.UserImmutableList"
)
interface Item {
    String name();

    Set<String> properties();

    UserImmutableList<Double> rates();
}