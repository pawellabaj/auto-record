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

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.UnknownTypeException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public enum MemoizerType {
    BOOLEAN(TypeKind.BOOLEAN, "BooleanMemoizer", "computeAsBooleanIfAbsent"),
    BYTE(TypeKind.BYTE, "ByteMemoizer", "computeAsByteIfAbsent", "ByteSupplier"),
    SHORT(TypeKind.SHORT, "ShortMemoizer", "computeAsShortIfAbsent", "ShortSupplier"),
    INT(TypeKind.INT, "IntMemoizer", "computeAsIntIfAbsent"),
    LONG(TypeKind.LONG, "LongMemoizer", "computeAsLongIfAbsent"),
    CHAR(TypeKind.CHAR, "CharMemoizer", "computeAsCharIfAbsent", "CharSupplier"),
    FLOAT(TypeKind.FLOAT, "FloatMemoizer", "computeAsFloatIfAbsent", "FloatSupplier"),
    DOUBLE(TypeKind.DOUBLE, "DoubleMemoizer", "computeAsDoubleIfAbsent"),
    ARRAY(TypeKind.ARRAY, "Memoizer", Constants.COMPUTE_IF_ABSENT),
    DECLARED(TypeKind.DECLARED, "Memoizer", Constants.COMPUTE_IF_ABSENT),
    TYPEVAR(TypeKind.TYPEVAR, "Memoizer", Constants.COMPUTE_IF_ABSENT);

    public static final String PL_COM_LABAJ_AUTORECORD_MEMOIZER = "pl.com.labaj.autorecord.memoizer";
    private static final Map<TypeKind, MemoizerType> MEMOIZERS_BY_KIND = Arrays.stream(values())
            .collect(toMap(MemoizerType::kind, identity()));
    private final TypeKind kind;
    private final String className;
    private final String computeMethod;
    private final String[] additionalFiles;

    MemoizerType(TypeKind kind, String className, String computeMethod, String... additionalFiles) {
        this.kind = kind;
        this.className = className;
        this.computeMethod = computeMethod;
        this.additionalFiles = additionalFiles;
    }

    public static MemoizerType from(TypeMirror type) {
        var kind = type.getKind();
        if (!MEMOIZERS_BY_KIND.containsKey(kind)) {
            throw new MemoizerUnknownTypeException(type);
        }

        return MEMOIZERS_BY_KIND.get(kind);
    }

    public TypeName typeName(TypeMirror type) {
        var className = ClassName.get(PL_COM_LABAJ_AUTORECORD_MEMOIZER, this.className);
        return kind.isPrimitive() ? className : ParameterizedTypeName.get(className, TypeName.get(type));
    }

    public String getNewReference() {
        return className + "::new";
    }

    public String getConstructorStatement() {
        return "new " + className + (kind.isPrimitive() ? "" : "<>") + "()";
    }

    public String computeMethod() {
        return computeMethod;
    }

    private TypeKind kind() {
        return kind;
    }

    public Stream<String> filesNamesToCopy() {
        return isNull(additionalFiles) ? Stream.of(className) : Stream.concat(Stream.of(className), Stream.of(additionalFiles));
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
            return super.getMessage() + (": Can't get MemoizerType with " + type.getKind() + " kind");
        }
    }

    @SuppressWarnings({"java:S2094", "java:S3985"})
    private static class Constants {
        private static final String COMPUTE_IF_ABSENT = "computeIfAbsent";
    }
}
