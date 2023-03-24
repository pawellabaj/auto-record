package pl.com.labaj.autorecord.processor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

class MessagerLogger {

    private final Messager messager;
    private final Element elementContext;

    MessagerLogger(Messager messager, Element elementContext) {
        this.messager = messager;
        this.elementContext = elementContext;
    }

    void debug(String message) {
        messager.printMessage(NOTE, message);
    }

    void error(String message) {
        messager.printMessage(ERROR, errorMessage(message), elementContext);
    }

    void error(String message, Throwable throwable) {
        messager.printMessage(ERROR, errorMessage(message) + ": " + getStackTrace(throwable), elementContext);
    }

    private String errorMessage(String message) {
        return "AutoRecordProcessor: " + message;
    }
}
