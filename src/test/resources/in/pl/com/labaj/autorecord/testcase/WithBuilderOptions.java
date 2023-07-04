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

import io.soabase.recordbuilder.core.RecordBuilder;
import pl.com.labaj.autorecord.AutoRecord;

import static javax.lang.model.element.Modifier.FINAL;

@AutoRecord
@AutoRecord.Options(withBuilder = true)
@RecordBuilder.Options(builderMethodName = "create",
        copyMethodName = "copyOf",
        buildMethodName = "buildRecord",
        enableWither = false,
        builderClassModifiers = {FINAL})
interface WithBuilderOptions {
    String one();

    int two();
}