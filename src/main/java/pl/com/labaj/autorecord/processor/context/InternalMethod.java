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
import java.lang.reflect.Array;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.type.TypeKind.DECLARED;
import static javax.lang.model.type.TypeKind.INT;

@SuppressWarnings("ImmutableEnumChecker")
public enum InternalMethod {
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
    };

    private static final List<InternalMethod> ALL_METHODS = Stream.of(values()).toList();
    private static final EnumMap<InternalMethod, TypeMirror> TYPES = ALL_METHODS.stream()
            .collect(toMap(
                    identity(),
                    InternalMethodMirrorType::new,
                    (t1, t2) -> t1,
                    () -> new EnumMap<>(InternalMethod.class)
            ));
    private static final Set<String> METHOD_NAMES = ALL_METHODS.stream()
            .map(InternalMethod::methodName)
            .collect(toSet());
    private final String methodName;
    private final TypeKind typeKind;
    private final TypeName typeName;

    InternalMethod(String methodName, TypeKind typeKind, TypeName typeName) {
        this.methodName = methodName;
        this.typeKind = typeKind;
        this.typeName = typeName;
    }

    public static Stream<InternalMethod> allInternalMethods() {
        return ALL_METHODS.stream();
    }

    public static boolean isInternal(ExecutableElement method) {
        var methodName = method.getSimpleName().toString();
        return METHOD_NAMES.contains(methodName);
    }

    public static boolean isNotInternal(ExecutableElement method) {
        return !isInternal(method);
    }

    public abstract boolean isMemoizedInOptions(AutoRecord.Options recordOptions);

    public TypeMirror type() {
        return TYPES.get(this);
    }

    public String methodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return methodName;
    }

    private static class InternalMethodMirrorType implements TypeMirror {

        private final InternalMethod internalMethod;

        public InternalMethodMirrorType(InternalMethod internalMethod) {
            this.internalMethod = internalMethod;
        }

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
            return (A[]) Array.newInstance(annotationType, 0);
        }

        @Override
        public TypeKind getKind() {
            return internalMethod.typeKind;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R, P> R accept(TypeVisitor<R, P> v, P p) {
            return (R) internalMethod.typeName;
        }
    }
}
