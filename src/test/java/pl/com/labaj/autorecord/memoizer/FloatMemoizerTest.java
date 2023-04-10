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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class FloatMemoizerTest {
    @Test
    void testMemoizer() {
        // given
        var memoizer = new FloatMemoizer();
        var counter = new AtomicInteger(0);
        FloatSupplier valueSupplier = () -> {
            counter.incrementAndGet();
            return 3.5f;
        };

        // when
        var memoizedValue1 = memoizer.computeAsFloatIfAbsent(valueSupplier);
        var memoizedValue2 = memoizer.computeAsFloatIfAbsent(valueSupplier);
        var memoizedValue3 = memoizer.computeAsFloatIfAbsent(valueSupplier);

        // then
        assertAll(
                () -> assertThat(memoizedValue1).isEqualTo(3.5f),
                () -> assertThat(memoizedValue2).isEqualTo(memoizedValue1),
                () -> assertThat(memoizedValue3).isEqualTo(memoizedValue1),
                () -> assertThat(counter.get()).isEqualTo(1)
        );
    }
}