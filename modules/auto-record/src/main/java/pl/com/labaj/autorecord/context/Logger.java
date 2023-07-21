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

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.tools.Diagnostic.Kind;

/**
 * A Logger object is used to log messages during annotation processing with specified {@linkplain Kind levels}.
 * It uses {@link Messager} provided by {@link ProcessingEnvironment}
 *
 * @see Messager
 * @see Kind
 * @since 2.1.0
 */
public interface Logger {

    /**
     * Check if the logger instance should log debug information.
     *
     * @return {@code true} when env variable {@code AUTO_RECORD_PROCESSOR_DEBUG} is set to {@code "true"}, {@code false} otherwise
     * @see #debug(String)
     */
    boolean isDebugEnabled();

    /**
     * Log a message at the {@link Kind#NOTE} level with {@code "[DEBUG] "} prefix
     * when {@link #isDebugEnabled()} returns {@code true}
     *
     * @param message the message to be logged
     * @see #isDebugEnabled()
     */
    void debug(String message);

    /**
     * Log a message at the {@link Kind#NOTE} level.
     *
     * @param message the message to be logged
     */
    void info(String message);

    /**
     * Log a message at the {@link Kind#WARNING} level.
     *
     * @param message the message to be logged
     */
    void warning(String message);

    /**
     * Log a message at the {@link Kind#MANDATORY_WARNING} level.
     *
     * @param message the message to be logged
     */
    void mandatoryWarning(String message);

    /**
     * Log a message at the {@link Kind#ERROR} level.
     * In addition, {@linkplain RoundEnvironment#errorRaised raises an error} in a processing round
     *
     * @param message the message to be logged
     */
    void error(String message);

    /**
     * Log an exception (throwable) at the {@link Kind#ERROR} level with an accompanying message.
     * In addition, {@linkplain RoundEnvironment#errorRaised raises an error} in a processing round
     *
     * @param message   the message to be logged
     * @param throwable the exception (throwable) to log
     */
    void error(String message, Throwable throwable);

    /**
     * Log a message at the {@link Kind#OTHER} level.
     *
     * @param message the message to be logged
     */
    void other(String message);
}
