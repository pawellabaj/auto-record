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

class ArrayComponentTest {

    @Test
    void shouldGenerateRecord() {
        //given
        var inputInterface = JavaFileObjects.forSourceString("pl.com.labaj.autorecord.test.ArrayComponent", """
                package pl.com.labaj.autorecord.test;
                                
                import pl.com.labaj.autorecord.AutoRecord;
                                
                @AutoRecord
                public interface ArrayComponent {
                    String[] anArray();
                }
                """);

        var expectedRecord = JavaFileObjects.forSourceString("pl.com.labaj.autorecord.test.ArrayComponentRecord", """
                package pl.com.labaj.autorecord.test;
                                
                import static java.util.Objects.hash;
                import static java.util.Objects.requireNonNull;
                                
                import java.lang.Object;
                import java.lang.Override;
                import java.lang.String;
                import java.util.Arrays;
                import javax.annotation.processing.Generated;
                import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
                                
                @Generated("pl.com.labaj.autorecord.AutoRecord")
                @GeneratedWithAutoRecord
                public record ArrayComponentRecord(String[] anArray) implements ArrayComponent {
                    public ArrayComponentRecord {
                        requireNonNull(anArray, () -> "anArray must not be null");
                    }
                                
                    @Override
                    public int hashCode() {
                        return hash(Arrays.hashCode(anArray));
                    }
                                
                    @Override
                    public boolean equals(Object other) {
                        if (this == other) {
                            return true;
                        }
                        if (other == null) {
                            return false;
                        }
                        if (!(other instanceof ArrayComponentRecord)) {
                            return false;
                        }
                                
                        var otherRecord = (ArrayComponentRecord) other;
                        return Arrays.equals(anArray, otherRecord.anArray);
                    }
                                
                    public String toString() {
                        return "ArrayComponentRecord[" +
                            "anArray = " + Arrays.toString(anArray) +
                            "]";
                    }
                }
                """);

        var compiler = javac().withProcessors(new AutoRecordProcessor());

        //when
        var compilation = compiler.compile(inputInterface);

        //then
        assertAll(
                () -> assertThat(compilation).succeeded(),
                () -> assertThat(compilation).generatedSourceFile("pl.com.labaj.autorecord.test.ArrayComponentRecord").hasSourceEquivalentTo(expectedRecord)
        );
    }
}
