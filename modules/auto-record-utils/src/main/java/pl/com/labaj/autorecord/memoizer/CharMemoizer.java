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

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * Memoizer is a thread-safe utility class that allows for the memoization of a single value using the double-check idiom.
 *
 * <p>The value will be computed on the first call to {@link #computeAsCharIfAbsent(CharSupplier)} method and the same value will be returned for
 * subsequent calls
 * without invoking the original computation.
 *
 * @see <a href="https://github.com/pawellabaj/auto-record/wiki/Memoization">Memoization Wiki</a>
 */
@API(status = STABLE)
public final class CharMemoizer {

    /**
     * Indicates whether the memoized value has already been computed
     */
    private volatile boolean valueMemoized;

    /**
     * The memoized value
     */
    private volatile char value;

    /**
     * Computes and memoizes the value of the function supplied if it has not already been computed.
     *
     * @param valueSupplier a {@link CharSupplier} representing the function that will generate the value to be memoized
     * @return the memoized value
     */
    public char computeAsCharIfAbsent(CharSupplier valueSupplier) {
        if (!valueMemoized) {
            synchronized (this) {
                if (!valueMemoized) {
                    value = valueSupplier.getAsChar();
                    valueMemoized = true;
                }
            }
        }
        return value;
    }
}
