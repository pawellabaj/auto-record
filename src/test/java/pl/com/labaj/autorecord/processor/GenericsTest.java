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

import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.junit.jupiter.api.Assertions.assertAll;

class GenericsTest {

    @Test
    void shouldGenerateRecord() {
        //given
        var inputInterface = JavaFileObjects.forSourceString("pl.com.labaj.autorecord.test.Generics", """
                package pl.com.labaj.autorecord.test;
                                
                import pl.com.labaj.autorecord.AutoRecord;
                import java.util.HashSet;
                import java.util.function.Function;
                
                @AutoRecord
                public interface Generics<A, B extends Function<Integer, B> & Comparable<B>, C extends HashSet<A>> {
                    A one();
                    B two();
                    C three();
                }
                """);

        var expectedRecord = JavaFileObjects.forSourceString("pl.com.labaj.autorecord.test.GenericsRecord", """
                package pl.com.labaj.autorecord.test;
                
                import static java.util.Objects.requireNonNull;
                 
                import java.lang.Comparable;
                import java.lang.Integer;
                import java.util.HashSet;
                import java.util.function.Function;
                import javax.annotation.processing.Generated;
                import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
                                
                @Generated("pl.com.labaj.autorecord.AutoRecord")
                @GeneratedWithAutoRecord
                public record GenericsRecord<A, B extends Function<Integer, B> & Comparable<B>, C extends HashSet<A>>(A one, B two, C three)
                              implements Generics<A, B, C> {
                    public ComplicatedGenericsRecord {
                        requireNonNull(one, () -> "one must not be null");
                        requireNonNull(two, () -> "two must not be null");
                        requireNonNull(three, () -> "three must not be null");
                    }
                }
                """);

        var compiler = javac().withProcessors(new AutoRecordProcessor());

        //when
        var compilation = compiler.compile(inputInterface);

        //then
        assertAll(
                () -> assertThat(compilation).succeeded(),
                () -> assertThat(compilation).generatedSourceFile("pl.com.labaj.autorecord.test.GenericsRecord").hasSourceEquivalentTo(expectedRecord)
        );
    }
}
