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

import pl.com.labaj.autorecord.processor.AutoRecordProcessorException;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

public class Logger {

    private final Messager messager;
    private final Element elementContext;

    public Logger(Messager messager, Element elementContext) {
        this.messager = messager;
        this.elementContext = elementContext;
    }

    public void debug(String message) {
        messager.printMessage(NOTE, message, elementContext);
    }

    public void error(String message) {
        messager.printMessage(ERROR, errorMessage(message), elementContext);
    }

    public void error(String message, Throwable throwable) {
        if (throwable instanceof AutoRecordProcessorException) {
            error(throwable.getLocalizedMessage());
        } else {
            messager.printMessage(ERROR, errorMessage(message) + ": " + getStackTrace(throwable), elementContext);
        }
    }

    private String errorMessage(String message) {
        return "AutoRecordProcessor: " + message;
    }
}
