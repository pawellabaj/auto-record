package pl.com.labaj.autorecord.extension;

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
import pl.com.labaj.autorecord.processor.AutoRecordProcessor;

/**
 * Represents an extension for the {@link AutoRecordProcessor} that can be used
 * to customize the record generation process.
 * <p>
 * To create a custom extension, implement this interface and override any specific methods as needed.
 * <p>
 * Note: At least one of the following specific interfaces should be implemented to customize the generation of the record:
 * <ul>
 * <li>{@link CompactConstructorExtension}</li>
 * </ul>
 *
 * @see <a href="https://github.com/pawellabaj/auto-record/wiki/Extensions">Extensions Wiki</a>
 * @since 2.1.0
 */
public interface AutoRecordExtension {
    /**
     * Sets the parameters for the extension.
     * <p>
     * This method can be overridden by the extension to receive any custom parameters that may be needed during record generation.
     *
     * @param parameters array of {@link String} objects representing the additional parameters for the custom extension.
     * @see AutoRecord.Extension#parameters()
     */
    default void setParameters(String[] parameters) {}
}
