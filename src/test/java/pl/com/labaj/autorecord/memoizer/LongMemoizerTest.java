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
import java.util.function.LongSupplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LongMemoizerTest {
    @Test
    void testMemoizer() {
        // given
        var memoizer = new LongMemoizer();
        var counter = new AtomicInteger(0);
        LongSupplier valueSupplier = () -> {
            counter.incrementAndGet();
            return 5L;
        };

        // when
        var memoizedValue1 = memoizer.computeAsLongIfAbsent(valueSupplier);
        var memoizedValue2 = memoizer.computeAsLongIfAbsent(valueSupplier);
        var memoizedValue3 = memoizer.computeAsLongIfAbsent(valueSupplier);

        // then
        assertAll(
                () -> assertThat(memoizedValue1).isEqualTo(5L),
                () -> assertThat(memoizedValue2).isSameAs(memoizedValue1),
                () -> assertThat(memoizedValue3).isSameAs(memoizedValue1),
                () -> assertThat(counter.get()).isEqualTo(1)
        );
    }
}