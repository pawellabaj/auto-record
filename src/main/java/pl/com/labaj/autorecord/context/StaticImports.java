package pl.com.labaj.autorecord.context;

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

import com.squareup.javapoet.ClassName;

/**
 * Collects {@code static import} statements that will be added into generated {@code record} source
 */
public interface StaticImports {
    /**
     * Adds {@code static import} statement
     *
     * @param aClass class to be statically imported
     * @param name   name of a method or constant to be statically imported
     */
    void add(Class<?> aClass, String name);

    /**
     * Adds {@code static import} statement
     *
     * @param className name of class to be statically imported
     * @param name      name of the method or constant be statically imported
     */
    void add(ClassName className, String name);

    /**
     * Adds {@code static import} statement
     *
     * @param constant enum constant to be statically imported
     */
    void add(Enum<?> constant);
}
