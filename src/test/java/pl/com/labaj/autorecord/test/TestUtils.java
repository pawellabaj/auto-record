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
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.Assertions;

public final class TestUtils {
    private static final String TEST_PACKAGE = "pl.com.labaj.autorecord.testcase";
    private static final String TEST_RESOURCE_PATH = TEST_PACKAGE.replaceAll("\\.", "/");

    private TestUtils() {}

    public static CompilationAssert assertThat(Compilation actual) {
        return new CompilationAssert(actual);
    }

    public static AbstractIntegerAssert<?> assertThat(int actual) {
        return Assertions.assertThat(actual);
    }

    public static String inputResourceName(String interfaceName) {
        return "in/" + TEST_RESOURCE_PATH + "/" + interfaceName + ".java";
    }

    public static String expectedResourceName(String interfaceName) {
        return "out/" + TEST_RESOURCE_PATH + "/" + interfaceName + "Record.java";
    }

    public static String generatedRecordName(String interfaceName) {
        return TEST_PACKAGE + "." + interfaceName + "Record";
    }

    public static String generatedRecordBuilderName(String interfaceName) {
        return TEST_PACKAGE + "." + interfaceName + "RecordBuilder";
    }
}
