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

import com.google.testing.compile.Compilation;

import java.io.IOException;

import static javax.tools.StandardLocation.CLASS_OUTPUT;

public class CompilationClassLoader extends ClassLoader {
    private final Compilation compilation;

    public CompilationClassLoader(Compilation compilation) {
        super();
        this.compilation = compilation;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String packageName = "";
        String className = name + ".class";
        var index = name.lastIndexOf('.');
        if (index > 0) {
            packageName = name.substring(0, index);
            className = name.substring(index + 1) + ".class";
        }

        var possibleGeneratedClass = compilation.generatedFile(CLASS_OUTPUT, packageName, className);

        if (possibleGeneratedClass.isPresent()) {
            var generatedClass = possibleGeneratedClass.get();
            try (var inputStream = generatedClass.openInputStream()) {
                var bytes = inputStream.readAllBytes();
                return defineClass(name, bytes, 0, bytes.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        }

        return super.findClass(name);
    }
}
