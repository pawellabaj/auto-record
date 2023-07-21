package pl.com.labaj.autorecord.test;

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

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.OptionalAssert;

import javax.tools.JavaFileObject;

public class JavaFileObjectAssert {
    private final Compilation compilation;
    private final String qualifiedName;

    public JavaFileObjectAssert(Compilation compilation, String qualifiedName) {
        this.compilation = compilation;
        this.qualifiedName = qualifiedName;
    }

    public void hasSourceEquivalentTo(JavaFileObject expectedSource) {
        CompilationSubject.assertThat(compilation).generatedSourceFile(qualifiedName).hasSourceEquivalentTo(expectedSource);
    }

    public OptionalAssert<JavaFileObject> isPresent() {
        return Assertions.assertThat(compilation.generatedSourceFile(qualifiedName)).isPresent();
    }
}
