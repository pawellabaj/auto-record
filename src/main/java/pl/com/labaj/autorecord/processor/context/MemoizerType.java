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
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import pl.com.labaj.autorecord.memoizer.BooleanMemoizer;
import pl.com.labaj.autorecord.memoizer.ByteMemoizer;
import pl.com.labaj.autorecord.memoizer.CharMemoizer;
import pl.com.labaj.autorecord.memoizer.DoubleMemoizer;
import pl.com.labaj.autorecord.memoizer.FloatMemoizer;
import pl.com.labaj.autorecord.memoizer.IntMemoizer;
import pl.com.labaj.autorecord.memoizer.LongMemoizer;
import pl.com.labaj.autorecord.memoizer.Memoizer;
import pl.com.labaj.autorecord.memoizer.ShortMemoizer;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.UnknownTypeException;
import java.util.Arrays;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public enum MemoizerType {
    BOOLEAN(TypeKind.BOOLEAN, BooleanMemoizer.class, "computeAsBooleanIfAbsent"),
    BYTE(TypeKind.BYTE, ByteMemoizer.class, "computeAsByteIfAbsent"),
    SHORT(TypeKind.SHORT, ShortMemoizer.class, "computeAsShortIfAbsent"),
    INT(TypeKind.INT, IntMemoizer.class, "computeAsIntIfAbsent"),
    LONG(TypeKind.LONG, LongMemoizer.class, "computeAsLongIfAbsent"),
    CHAR(TypeKind.CHAR, CharMemoizer.class, "computeAsCharIfAbsent"),
    FLOAT(TypeKind.FLOAT, FloatMemoizer.class, "computeAsFloatIfAbsent"),
    DOUBLE(TypeKind.DOUBLE, DoubleMemoizer.class, "computeAsDoubleIfAbsent"),
    ARRAY(TypeKind.ARRAY, Memoizer.class, Constants.COMPUTE_IF_ABSENT),
    DECLARED(TypeKind.DECLARED, Memoizer.class, Constants.COMPUTE_IF_ABSENT),
    TYPEVAR(TypeKind.TYPEVAR, Memoizer.class, Constants.COMPUTE_IF_ABSENT);

    private static final Map<TypeKind, MemoizerType> MEMOIZERS_BY_KIND = Arrays.stream(values())
            .collect(toMap(MemoizerType::kind, identity()));
    private final TypeKind kind;
    private final Class<?> memoizerClass;
    private final String computeMethod;

    MemoizerType(TypeKind kind, Class<?> memoizerClass, String computeMethod) {
        this.kind = kind;
        this.memoizerClass = memoizerClass;
        this.computeMethod = computeMethod;
    }

    public static MemoizerType from(TypeMirror type) {
        var kind = type.getKind();
        if (!MEMOIZERS_BY_KIND.containsKey(kind)) {
            throw new MemoizerUnknownTypeException(type);
        }

        return MEMOIZERS_BY_KIND.get(kind);
    }

    public TypeName typeName(TypeMirror type) {
        var className = ClassName.get(memoizerClass);
        return kind.isPrimitive() ? className : ParameterizedTypeName.get(className, TypeName.get(type));
    }

    public String getNewReference() {
        return memoizerClass.getSimpleName() + "::new";
    }

    public String getConstructorStatement() {
        return "new " + memoizerClass.getSimpleName() + (kind.isPrimitive() ? "" : "<>") + "()";
    }

    public String computeMethod() {
        return computeMethod;
    }

    private TypeKind kind() {
        return kind;
    }

    @SuppressWarnings("java:S110")
    private static class MemoizerUnknownTypeException extends UnknownTypeException {
        private final transient TypeMirror type;

        public MemoizerUnknownTypeException(TypeMirror type) {
            super(type, null);
            this.type = type;
        }

        @Override
        public String getMessage() {
            return super.getMessage() + ": Can't get MemoizerType with %s kind".formatted(type.getKind());
        }
    }

    @SuppressWarnings({"java:S2094", "java:S3985"})
    private static class Constants {
        private static final String COMPUTE_IF_ABSENT = "computeIfAbsent";
    }
}
