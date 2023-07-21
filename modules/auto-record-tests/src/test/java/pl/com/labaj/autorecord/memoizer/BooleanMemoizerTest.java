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
import java.util.function.BooleanSupplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class BooleanMemoizerTest {
    @Test
    @SuppressWarnings("java:S5838")
    void testMemoizer() {
        // given
        var memoizer = new BooleanMemoizer();
        var counter = new AtomicInteger(0);
        BooleanSupplier valueSupplier = () -> {
            counter.incrementAndGet();
            return true;
        };

        // when
        var memoizedValue1 = memoizer.computeAsBooleanIfAbsent(valueSupplier);
        var memoizedValue2 = memoizer.computeAsBooleanIfAbsent(valueSupplier);
        var memoizedValue3 = memoizer.computeAsBooleanIfAbsent(valueSupplier);

        // then
        assertAll(
                () -> assertThat(memoizedValue1).isEqualTo(true),
                () -> assertThat(memoizedValue2).isSameAs(memoizedValue1),
                () -> assertThat(memoizedValue3).isSameAs(memoizedValue1),
                () -> assertThat(counter.get()).isEqualTo(1)
        );
    }
}