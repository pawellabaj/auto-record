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

import javax.lang.model.element.ExecutableElement;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public enum SpecialMethod {
    TO_BUILDER("toBuilder");

    private static final Map<String, SpecialMethod> METHODS_BY_NAME = Stream.of(values())
            .collect(toMap(SpecialMethod::methodName, identity()));
    private final String methodName;

    SpecialMethod(String methodName) {
        this.methodName = methodName;
    }

    public static boolean isSpecial(ExecutableElement method) {
        var methodName = method.getSimpleName().toString();
        return METHODS_BY_NAME.containsKey(methodName);
    }

    public static boolean isNotSpecial(ExecutableElement method) {
        return !isSpecial(method);
    }

    public static SpecialMethod fromMethod(ExecutableElement method) {
        var methodName = method.getSimpleName().toString();
        return METHODS_BY_NAME.get(methodName);
    }

    public String methodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return methodName;
    }
}
