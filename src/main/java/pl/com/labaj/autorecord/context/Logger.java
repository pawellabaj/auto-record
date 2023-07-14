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

/**
 * A Logger object is used to log messages during annotation processing with specified {@linkplain javax.tools.Diagnostic.Kind levels}.
 *
 * <p>Note that the messages logged by methods in this
 * interface may or may not appear as textual output to a location
 * like {@link System#out} or {@link System#err}.  Implementations may
 * choose to present this information in a different fashion, such as
 * messages in a window.
 *
 * @see javax.tools.Diagnostic.Kind
 */
public interface Logger {

    /**
     * Log a message at the {@link javax.tools.Diagnostic.Kind#NOTE} level
     * only when system property <em>AutoRecordProcessor.debug.enabled</em> is set to {@code true}
     *
     * @param message the message to be logged
     */
    void debug(String message);

    /**
     * Log a message at the {@link javax.tools.Diagnostic.Kind#NOTE} level.
     *
     * @param message the message to be logged
     */
    void info(String message);

    /**
     * Log a message at the {@link javax.tools.Diagnostic.Kind#WARNING} level.
     *
     * @param message the message to be logged
     */
    void warning(String message);

    /**
     * Log a message at the {@link javax.tools.Diagnostic.Kind#MANDATORY_WARNING} level.
     *
     * @param message the message to be logged
     */
    void mandatoryWarning(String message);

    /**
     * Log a message at the {@link javax.tools.Diagnostic.Kind#ERROR} level.
     * In addition, {@linkplain javax.annotation.processing.RoundEnvironment#errorRaised raises an error} in a processing round
     *
     * @param message the message to be logged
     */
    void error(String message);

    /**
     * Log an exception (throwable) at the {@link javax.tools.Diagnostic.Kind#ERROR} level with an accompanying message.
     * In addition, {@linkplain javax.annotation.processing.RoundEnvironment#errorRaised raises an error} in a processing round
     *
     * @param message   the message to be logged
     * @param throwable the exception (throwable) to log
     */
    void error(String message, Throwable throwable);

    /**
     * Log a message at the {@link javax.tools.Diagnostic.Kind#OTHER} level.
     *
     * @param message the message to be logged
     */
    void other(String message);
}
