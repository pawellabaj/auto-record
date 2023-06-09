package pl.com.labaj.autorecord.processor.context;

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
import pl.com.labaj.autorecord.processor.utils.Generics;
import pl.com.labaj.autorecord.processor.utils.Logger;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;

public record GenerationContext(boolean isRecordPublic,
                                String interfaceName,
                                TypeMirror superType,
                                AutoRecord.Options recordOptions,
                                RecordBuilder.Options builderOptions,
                                List<ExecutableElement> propertyMethods,
                                Map<SpecialMethod, ExecutableElement> specialMethods,
                                Generics generics,
                                Memoization memoization,
                                String packageName,
                                String recordName,
                                Logger logger) {}
