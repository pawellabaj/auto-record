package pl.com.labaj.autorecord.processor;

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
import org.apiguardian.api.API;
import pl.com.labaj.autorecord.context.StaticImports;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = INTERNAL)
class StaticImportsCollectors implements pl.com.labaj.autorecord.context.StaticImports {

    final Map<ClassName, Set<String>> statements = new HashMap<>();

    @Override
    public StaticImports add(Class<?> aClass, String name) {
        return add(ClassName.get(aClass), name);
    }

    @Override
    public StaticImports add(Enum<?> constant) {
        return add(ClassName.get(constant.getDeclaringClass()), constant.name());
    }

    @Override
    public StaticImports add(ClassName className, String name) {
        statements.computeIfAbsent(className, cName -> new HashSet<>())
                .add(name);

        return this;
    }

    public void forEach(BiConsumer<ClassName, String[]> importConsumer) {
        statements.forEach((className, names) -> importConsumer.accept(className, names.toArray(String[]::new)));
    }
}
