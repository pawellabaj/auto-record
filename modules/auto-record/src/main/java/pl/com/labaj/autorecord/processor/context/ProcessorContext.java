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
import pl.com.labaj.autorecord.context.Context;
import pl.com.labaj.autorecord.context.Logger;
import pl.com.labaj.autorecord.context.RecordComponent;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public record ProcessorContext(ProcessingEnvironment processingEnv,
                               String packageName,
                               AutoRecord.Options recordOptions,
                               RecordBuilder.Options builderOptions,
                               boolean isRecordPublic,
                               TypeMirror interfaceType,
                               String interfaceName,
                               List<RecordComponent> components,
                               List<TypeParameterElement> typeParameters,
                               Generics generics,
                               Map<MethodDefinition, List<AnnotationMirror>> specialMethodAnnotations,
                               Memoization memoization,
                               String recordName,
                               Logger logger,
                               Consumer<MemoizerType> memoizerCollector) implements Context {

    public static final MethodDefinition TO_BUILDER = new MethodDefinition("toBuilder", List.of());

    public Optional<List<AnnotationMirror>> getSpecialMethodAnnotations(MethodDefinition methodDefinition) {
        return Optional.ofNullable(specialMethodAnnotations.get(methodDefinition));
    }
}
