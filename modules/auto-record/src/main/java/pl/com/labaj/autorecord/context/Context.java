package pl.com.labaj.autorecord.context;

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

import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Represents the context of the {@link AutoRecord} annotation processing.
 *
 * <p>This interface provides access to various information and options needed during generation of a record class.
 *
 * @since 2.1.0
 */
public interface Context {
    /**
     * Gets the package name of the annotated interface.
     *
     * @return the package name of the annotated interface.
     */
    String packageName();

    /**
     * Gets the options specified in the {@link AutoRecord} or {@link AutoRecord.Template} annotation
     * for the annotated interface.
     *
     * @return the {@link AutoRecord.Options} instance containing the options specified in annotation.
     * @see AutoRecord.Options
     * @see AutoRecord.Template#recordOptions()
     */
    AutoRecord.Options recordOptions();

    /**
     * Gets the builder options specified in the {@link AutoRecord} or {@link AutoRecord.Template} annotation
     * for the annotated interface.
     *
     * @return the {@link RecordBuilder.Options} instance containing the options specified in annotation.
     * @see RecordBuilder.Options
     * @see AutoRecord.Template#builderOptions()
     */
    RecordBuilder.Options builderOptions();

    /**
     * Checks if the generated record class should have public visibility.
     *
     * @return {@code true} if the generated record class should be public, {@code false} otherwise.
     */
    boolean isRecordPublic();

    /**
     * Gets the type of the annotated interface.
     *
     * @return the type of the annotated interface.
     * @see TypeMirror
     */
    TypeMirror interfaceType();

    /**
     * Gets the simple name of the annotated interface.
     *
     * @return the simple name of the annotated interface.
     */
    String interfaceName();

    /**
     * Gets the list of {@link RecordComponent} instances representing the components of the generated record.
     *
     * @return the list of {@link RecordComponent} instances representing the components of the generated record.
     * @see RecordComponent
     */
    List<RecordComponent> components();

    /**
     * Gets the list of {@link TypeParameterElement} instances representing the type parameters of the annotated interface.
     *
     * @return the list of {@link TypeParameterElement} instances representing the type parameters of the annotated interface.
     * @see TypeParameterElement
     */
    List<TypeParameterElement> typeParameters();

    /**
     * Gets the name of the generated record.
     *
     * @return the name of the generated record.
     */
    String recordName();

    /**
     * Gets the {@link Logger} instance to log messages during the annotation processing.
     *
     * @return the {@link Logger} instance for logging messages.
     * @see Logger
     */
    Logger logger();
}
