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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * Starting from <a href="https://www.jacoco.org/">JaCoCo</a> 0.8.2, classes and methods annotated with annotation
 * following properties:
 * <ul>
 *     <li>the annotation name includes <em>"Generated"</em></li>
 *     <li>the annotation retention policy is {@code RUNTIME} or {@code CLASS}</li>
 * </ul>
 * are excluded from test coverage reports.
 * <p>
 * The annotation is added to all generated records.
 */
@Documented
@Retention(CLASS)
@Target({TYPE, METHOD, CONSTRUCTOR})
@API(status = STABLE)
public @interface GeneratedWithAutoRecord {}
