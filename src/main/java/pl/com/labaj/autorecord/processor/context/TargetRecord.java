package pl.com.labaj.autorecord.processor.context;

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

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.Objects;
public record TargetRecord(String packageName, String name, Modifier[] modifiers) {
    @Override
    public int hashCode() {
        int result = Objects.hash(packageName, name);
        result = 31 * result + Arrays.hashCode(modifiers);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetRecord that = (TargetRecord) o;
        return Objects.equals(packageName, that.packageName) && Objects.equals(name, that.name) && Arrays.equals(modifiers, that.modifiers);
    }

    @Override
    public String toString() {
        return "TargetRecord{" +
                "packageName='" + packageName + '\'' +
                ", name='" + name + '\'' +
                ", modifiers=" + Arrays.toString(modifiers) +
                '}';
    }
}
