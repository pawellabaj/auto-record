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

class FromTemplateTest {

    @Test
    void shouldGenerateRecord() {
        //given
        var inputInterface = JavaFileObjects.forSourceString("pl.com.labaj.autorecord.test.FromTemplate", """
                package pl.com.labaj.autorecord.test;
                                
                import pl.com.labaj.autorecord.WithTemplate;
                import java.util.List;
                import java.util.Optional;
                
                @WithTemplate
                public interface FromTemplate {
                    String text();
                    int number();
                    List<Optional<Integer>> genericCollection();
                }
                """);

        var expectedRecord = JavaFileObjects.forSourceString("pl.com.labaj.autorecord.test.FromTemplateRecord", """
                package pl.com.labaj.autorecord.test;
                
                import static java.util.Objects.requireNonNull;
                 
                import java.lang.Integer;
                import java.lang.String;
                import java.util.List;
                import java.util.Optional;
                import javax.annotation.processing.Generated;
                import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
                                
                @Generated("pl.com.labaj.autorecord.AutoRecord")
                @GeneratedWithAutoRecord
                public record FromTemplateRecord(String text, int number, List<Optional<Integer>> genericCollection) implements FromTemplate {
                    public FromTemplateRecord {
                        requireNonNull(text, () -> "text must not be null");
                        requireNonNull(genericCollection, () -> "genericCollection must not be null");
                    }
                }
                """);

        var compiler = javac().withProcessors(new AutoRecordProcessor());

        //when
        var compilation = compiler.compile(inputInterface);

        //then
        assertAll(
                () -> assertThat(compilation).succeeded(),
                () -> assertThat(compilation).generatedSourceFile("pl.com.labaj.autorecord.test.FromTemplateRecord").hasSourceEquivalentTo(expectedRecord)
        );
    }
}
