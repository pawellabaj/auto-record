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

import java.util.function.Supplier;

public final class Memoizer<T> {
    private volatile boolean valueMemoized;
    @SuppressWarnings("java:S3077")
    private volatile T value;

    public T computeIfAbsent(Supplier<T> valueSupplier) {
        if (!valueMemoized) {
            synchronized (this) {
                if (!valueMemoized) {
                    value = valueSupplier.get();
                    valueMemoized = true;
                }
            }
        }
        return value;
    }
}
