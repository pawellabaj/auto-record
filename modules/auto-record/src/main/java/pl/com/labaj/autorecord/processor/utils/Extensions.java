package pl.com.labaj.autorecord.processor.utils;

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

import org.apiguardian.api.API;
import pl.com.labaj.autorecord.extension.AutoRecordExtension;

import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = INTERNAL)
public final class Extensions {
    private Extensions() {}

    public static <E extends AutoRecordExtension> List<E> typedExtensions(List<AutoRecordExtension> extensions, Class<E> extensionClass) {
        return extensions.stream()
                .filter(extensionClass::isInstance)
                .map(extensionClass::cast)
                .toList();
    }
}
