package pl.com.labaj.autorecord.extension.arice;

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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;
import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * This annotation is used to mark the generated record, so the ARICE (AutoRecord Immutable Collections Extension)
 * knows what utility class it should generate.
 *
 * @since 3.0.0
 */
@Retention(SOURCE)
@Target(TYPE)
@Inherited
@API(status = MAINTAINED)
public @interface ARICEUtilities {
    /**
     * Specifies the fully qualified name of the utility class that provides additional methods and functionality
     * to support the ARICE extension.
     *
     * @return the fully qualified name of the utility class.
     */
    String className();

    /**
     * Specifies an array of fully qualified names of immutable types. These types will be used in conjunction with the ARICE extension
     *
     * @return an array of fully qualified names of immutable types.
     */
    String[] immutableTypes() default {};
}
