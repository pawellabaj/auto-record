package pl.com.labaj.autorecord;

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

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * Annotates methods in {@link AutoRecord @AutoRecord} annotated interface for which the corresponding components in generated record are not relevant for
 * equality checks - they are ignored in {@code hashCode()} and {@code toString()} methods.
 *
 * @see <a href="https://github.com/pawellabaj/auto-record/wiki/Ignored-components">Ignored Components Wiki</a>
 */
@Retention(SOURCE)
@Target({METHOD, TYPE_PARAMETER, PARAMETER})
@Inherited
@API(status = STABLE)
public @interface Ignored {}
