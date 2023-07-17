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

import pl.com.labaj.autorecord.context.Logger;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.MANDATORY_WARNING;
import static javax.tools.Diagnostic.Kind.NOTE;
import static javax.tools.Diagnostic.Kind.OTHER;
import static javax.tools.Diagnostic.Kind.WARNING;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

public class MessagerLogger implements Logger {

    private static final String AUTO_RECORD_PROCESSOR_DEBUG = "AUTO_RECORD_PROCESSOR_DEBUG";
    private static final boolean DEBUG_ENABLED = Boolean.parseBoolean(System.getenv(AUTO_RECORD_PROCESSOR_DEBUG));
    private final Messager messager;

    private final Element elementContext;

    public MessagerLogger(Messager messager, Element elementContext) {
        this.messager = messager;
        this.elementContext = elementContext;
    }

    @Override
    public void debug(String message) {
        if (DEBUG_ENABLED) {
            messager.printMessage(NOTE, "[DEBUG] " + message, elementContext);
        }
    }

    @Override
    public void info(String message) {
        printMessage(NOTE, message);
    }

    @Override
    public void warning(String message) {
        printMessage(WARNING, message);
    }

    @Override
    public void mandatoryWarning(String message) {
        printMessage(MANDATORY_WARNING, message);
    }

    @Override
    public void error(String message) {
        printMessage(ERROR, message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        printMessage(ERROR, message + ": " + getStackTrace(throwable));
    }

    @Override
    public void other(String message) {
        printMessage(OTHER, message);
    }

    private void printMessage(Diagnostic.Kind kind, String message) {
        messager.printMessage(kind, message, elementContext);
    }
}
