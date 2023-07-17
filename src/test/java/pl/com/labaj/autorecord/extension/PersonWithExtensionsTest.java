package pl.com.labaj.autorecord.extension;

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

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonWithExtensionsTest {

    @Test
    void shouldPrintAndThrowException() {
        //given
        var logger = Logger.getLogger(PersonWithExtensionsRecord.class.getName());
        var handler = new TestLogHandler();
        logger.addHandler(handler);

        //when then
        var exception = assertThrows(IllegalStateException.class, () -> new PersonWithExtensionsRecord("Jan", null, 16));
        assertAll(
                () -> assertThat(handler.records).hasSize(1),
                () -> assertThat(handler.records).extracting(LogRecord::getLevel).containsExactly(INFO),
                () -> assertThat(handler.records).extracting(LogRecord::getMessage).allMatch(message -> message.startsWith("Parameters passed to record")),
                () -> assertThat(exception).hasMessageStartingWith("Jan is not adult")
        );
    }

    private static class TestLogHandler extends Handler {
        private final List<LogRecord> records = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }

        @Override
        public void flush() {}

        @Override
        public void close() {}
    }
}
