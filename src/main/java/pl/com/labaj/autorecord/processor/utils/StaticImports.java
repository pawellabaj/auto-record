package pl.com.labaj.autorecord.processor.utils;

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
import pl.com.labaj.autorecord.processor.StaticImportsCollector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StaticImports implements StaticImportsCollector {

    final Map<ClassName, Set<String>> items = new HashMap<>();

    @Override
    public void add(Class<?> aClass, String name) {
        add(ClassName.get(aClass), name);
    }

    @Override
    public void add(Enum<?> constant) {
        add(ClassName.get(constant.getDeclaringClass()), constant.name());
    }

    @Override
    public void add(ClassName className, String name) {
        items.computeIfAbsent(className, cName -> new HashSet<>())
                .add(name);
    }

    public Map<ClassName, Set<String>> items() {
        return items;
    }
}
