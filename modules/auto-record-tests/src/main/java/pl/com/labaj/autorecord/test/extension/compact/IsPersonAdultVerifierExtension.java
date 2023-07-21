package pl.com.labaj.autorecord.test.extension.compact;

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

import com.squareup.javapoet.CodeBlock;
import pl.com.labaj.autorecord.context.Context;
import pl.com.labaj.autorecord.context.StaticImports;
import pl.com.labaj.autorecord.extension.CompactConstructorExtension;
import pl.com.labaj.autorecord.processor.AutoRecordProcessorException;

public class IsPersonAdultVerifierExtension implements CompactConstructorExtension {

    private static final Class<RuntimeException> RUNTIME_EXCEPTION_CLASS = RuntimeException.class;
    private Class<? extends RuntimeException> exceptionClass;

    @SuppressWarnings("java:S112")
    @Override
    public void setParameters(String[] parameters) {
        if (parameters.length < 1) {
            exceptionClass = RUNTIME_EXCEPTION_CLASS;
        } else {
            var className = parameters[0];

            try {
                var aClass = Class.forName(className);
                exceptionClass =  getExceptionClass(aClass);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Cannot load " + className + " class", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends RuntimeException> getExceptionClass(Class<?> aClass) {
        if (RUNTIME_EXCEPTION_CLASS.isAssignableFrom(aClass)) {
            return (Class<? extends RuntimeException>) aClass;
        } else {
            throw new AutoRecordProcessorException("Class " + aClass.getName() + " does not extend " + RUNTIME_EXCEPTION_CLASS.getName());
        }
    }

    @Override
    public CodeBlock suffixCompactConstructorContent(Context context, StaticImports staticImports) {
        return CodeBlock.builder()
                .beginControlFlow("if (age < 18)")
                .addStatement("var message = \"%s is not adult. She/he is only %d!\".formatted(name, age)")
                .addStatement("throw new $T(message)", exceptionClass)
                .endControlFlow()
                .build();
    }
}
