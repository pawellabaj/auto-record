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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import pl.com.labaj.autorecord.AutoRecord;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.type.TypeKind.DECLARED;
import static javax.lang.model.type.TypeKind.INT;

public enum SpecialMethod {
    HASH_CODE("hashCode", INT, TypeName.INT) {
        @Override
        public boolean isMemoizedInOptions(AutoRecord.Options recordOptions) {
            return recordOptions.memoizedHashCode();
        }
    },
    TO_STRING("toString", DECLARED, ClassName.get(String.class)) {
        @Override
        public boolean isMemoizedInOptions(AutoRecord.Options recordOptions) {
            return recordOptions.memoizedToString();
        }
    },
    TO_BUILDER("toBuilder") {
        @Override
        public boolean isMemoizedInOptions(AutoRecord.Options recordOptions) {
            return false;
        }
    };

    private static final List<SpecialMethod> ALL_METHODS = List.of(HASH_CODE, TO_STRING, TO_BUILDER);
    private static final Map<String, SpecialMethod> METHODS_BY_NAME = ALL_METHODS.stream().collect(toMap(SpecialMethod::methodName, identity()));
    private final String methodName;
    private final TypeMirror type;

    SpecialMethod(String methodName) {
        this.methodName = methodName;
        type = null;
    }

    SpecialMethod(String methodName, TypeKind typeKind, TypeName typeName) {
        this.methodName = methodName;
        type = new InternalTypeMirror(typeKind, typeName);
    }

    public static List<SpecialMethod> allSpecialMethods() {
        return ALL_METHODS;
    }

    public static boolean isSpecial(ExecutableElement method) {
        var methodName = method.getSimpleName();
        return METHODS_BY_NAME.containsKey(methodName.toString());
    }

    public static SpecialMethod fromMethod(ExecutableElement method) {
        var methodName = method.getSimpleName();
        return METHODS_BY_NAME.get(methodName.toString());
    }

    public String methodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return methodName;
    }

    public Memoization.Item toMemoizedItem() {
        return new Memoization.Item(type, methodName, emptyList(), Set.of(PUBLIC), true);
    }

    public abstract boolean isMemoizedInOptions(AutoRecord.Options recordOptions);

    private record InternalTypeMirror(TypeKind typeKind, TypeName typeName) implements TypeMirror {

        @Override
        public List<? extends AnnotationMirror> getAnnotationMirrors() {
            return emptyList();
        }

        @Override
        public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
            return (A[]) java.lang.reflect.Array.newInstance(annotationType, 0);
        }

        @Override
        public TypeKind getKind() {
            return typeKind;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R, P> R accept(TypeVisitor<R, P> v, P p) {
            return (R) typeName;
        }
    }
}
