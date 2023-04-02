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

import java.util.concurrent.atomic.AtomicInteger;

public final class Counters {
    private final AtomicInteger hashCodeCounter = new AtomicInteger(0);
    private final AtomicInteger toStringCounter = new AtomicInteger(0);
    private final AtomicInteger equalsCounter = new AtomicInteger(0);

    public Counters() {}

    @Override
    public int hashCode() {
        hashCodeCounter.addAndGet(1);
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        equalsCounter.addAndGet(1);
        return o instanceof Counters;
    }

    @Override
    public String toString() {
        toStringCounter.addAndGet(1);
        return "{}";
    }

    public int hashCodeCount() {
        return hashCodeCounter.get();
    }

    public int toStringCount() {
        return toStringCounter.get();
    }

    public int equalsCount() {
        return equalsCounter.get();
    }
}
