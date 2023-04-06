package pl.com.labaj.autorecord.processor;

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

import javax.lang.model.element.ExecutableElement;
import java.util.List;

enum SpecialMethod {
    HASH_CODE("hashCode", Integer.TYPE) {
        @Override
        boolean isMemoizedInOptions(AutoRecord.Options recordOptions) {
            return recordOptions.memoizedHashCode();
        }
    },
    TO_STRING("toString", String.class) {
        @Override
        boolean isMemoizedInOptions(AutoRecord.Options recordOptions) {
            return recordOptions.memoizedToString();
        }
    };

    private static final List<SpecialMethod> ALL_METHODS = List.of(HASH_CODE, TO_STRING);
    private final String methodName;
    private final Class<?> type;

    SpecialMethod(String methodName, Class<?> type) {
        this.methodName = methodName;
        this.type = type;
    }

    static List<SpecialMethod> specialMethods() {
        return ALL_METHODS;
    }

    static boolean isSpecial(ExecutableElement method) {
        var methodName = method.getSimpleName();
        return methodName.contentEquals(HASH_CODE.methodName) || methodName.contentEquals(TO_STRING.methodName);
    }

    abstract boolean isMemoizedInOptions(AutoRecord.Options recordOptions);

    String methodName() {
        return methodName;
    }

    Class<?> type() {
        return type;
    }
}
