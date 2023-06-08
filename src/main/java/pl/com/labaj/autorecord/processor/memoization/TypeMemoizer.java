package pl.com.labaj.autorecord.processor.memoization;

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
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

public enum TypeMemoizer {
    BOOLEAN(TypeKind.BOOLEAN, BooleanMemoizer.class, "return $LMemoizer.computeAsBooleanIfAbsent(() -> $L())"),
    BYTE(TypeKind.BYTE, ByteMemoizer.class, "return $LMemoizer.computeAsByteIfAbsent(() -> $L())"),
    SHORT(TypeKind.SHORT, ShortMemoizer.class, "return $LMemoizer.computeAsShortIfAbsent(() -> $L())"),
    INT(TypeKind.INT, IntMemoizer.class, "return $LMemoizer.computeAsIntIfAbsent(() -> $L())"),
    LONG(TypeKind.LONG, LongMemoizer.class, "return $LMemoizer.computeAsLongIfAbsent(() -> $L())"),
    CHAR(TypeKind.CHAR, CharMemoizer.class, "return $LMemoizer.computeAsCharIfAbsent(() -> $L())"),
    FLOAT(TypeKind.FLOAT, FloatMemoizer.class, "return $LMemoizer.computeAsFloatIfAbsent(() -> $L())"),
    DOUBLE(TypeKind.DOUBLE, DoubleMemoizer.class, "return $LMemoizer.computeAsDoubleIfAbsent(() -> $L())"),
    ARRAY(TypeKind.ARRAY, Memoizer.class, Constants.COMMON_RETURN_STATEMENT),
    DECLARED(TypeKind.DECLARED, Memoizer.class, Constants.COMMON_RETURN_STATEMENT),
    TYPEVAR(TypeKind.TYPEVAR, Memoizer.class, Constants.COMMON_RETURN_STATEMENT);

    private static final Map<TypeKind, TypeMemoizer> MEMOIZERS_BY_KIND = Arrays.stream(values()).collect(Collectors.toMap(TypeMemoizer::kind, identity()));
    private final TypeKind kind;
    private final Class<?> memoizerClass;
    private final String returnStatement;

    TypeMemoizer(TypeKind kind, Class<?> memoizerClass, String returnStatement) {
        this.kind = kind;
        this.memoizerClass = memoizerClass;
        this.returnStatement = returnStatement;
    }

    public static TypeMemoizer typeMemoizerWith(TypeMirror type) {
        var kind = type.getKind();
        if (!MEMOIZERS_BY_KIND.containsKey(kind)) {
            throw new MemoizerUnknownTypeException(type);
        }

        return MEMOIZERS_BY_KIND.get(kind);
    }

    public TypeName getTypeName(TypeMirror type) {
        var className = ClassName.get(memoizerClass());
        return kind.isPrimitive() ? className : ParameterizedTypeName.get(className, TypeName.get(type));
    }

    public String getNewStatement() {
        return kind.isPrimitive() ? "new " + memoizerClass.getSimpleName() + "()" : "new Memoizer<>()";
    }

    public String getReturnStatement() {
        return returnStatement;
    }

    private TypeKind kind() {
        return kind;
    }

    private Class<?> memoizerClass() {
        return memoizerClass;
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
            return super.getMessage() + ": Can't get TypeMemoizer with %s kind".formatted(type.getKind());
        }
    }

    private static class Constants {
        static final String COMMON_RETURN_STATEMENT = "return $LMemoizer.computeIfAbsent(() -> $L())";
    }
}
