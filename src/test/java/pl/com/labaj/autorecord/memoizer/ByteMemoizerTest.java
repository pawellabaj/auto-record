package pl.com.labaj.autorecord.memoizer;

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

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ByteMemoizerTest {
    @Test
    public void testMemoizer() {
        // given
        var memoizer = new ByteMemoizer();
        var counter = new AtomicInteger(0);
        ByteSupplier valueSupplier = () -> {
            counter.incrementAndGet();
            return (byte) 1;
        };

        // when
        var memoizedValue1 = memoizer.computeAsByteIfAbsent(valueSupplier);
        var memoizedValue2 = memoizer.computeAsByteIfAbsent(valueSupplier);
        var memoizedValue3 = memoizer.computeAsByteIfAbsent(valueSupplier);

        // then
        assertAll(
                () -> assertEquals(1, memoizedValue1),
                () -> assertEquals(memoizedValue1, memoizedValue2),
                () -> assertEquals(memoizedValue1, memoizedValue3),
                () -> assertEquals(1, counter.get())
        );
    }
}