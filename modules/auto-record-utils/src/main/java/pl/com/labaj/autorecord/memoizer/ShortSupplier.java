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

import java.util.function.Supplier;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * Represents a supplier of {@code short}-valued results.  This is the
 * {@code short}-producing primitive specialization of {@link Supplier}.
 *
 * <p>There is no requirement that a distinct result be returned each
 * time the supplier is invoked.
 *
 * <p>This is a {@link java.util.function functional interface}
 * whose functional method is {@link #getAsShort()}.
 *
 * @see Supplier
 * @since 1.8
 */
@FunctionalInterface
@API(status = STABLE)
public interface ShortSupplier {
    /**
     * Gets a {@code short}-valued result.
     *
     * @return a result
     */
    short getAsShort();
}
