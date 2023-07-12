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

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class Memoization {

    private final List<Item> items;
    private final Set<String> memoizedInternalMethodNames;

    public Memoization(List<Item> items) {
        this.items = items;
        memoizedInternalMethodNames = items.stream()
                .filter(Item::internal)
                .map(Item::name)
                .collect(toSet());
    }

    public boolean isMemoized(InternalMethod internalMethod) {
        var methodName = internalMethod.methodName();
        return memoizedInternalMethodNames.contains(methodName);
    }

    public void ifPresent(Consumer<List<Item>> itemsConsumer) {
        if (!items.isEmpty()) {
            itemsConsumer.accept(items);
        }
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean isPresent() {
        return !isEmpty();
    }

    public record Item(TypeMirror type, String name, List<AnnotationMirror> annotations, boolean internal) {
        public String getMemoizerName() {
            return name + "Memoizer";
        }

        public Item mergeWith(Item otherItem) {
            var mergedAnnotations = Stream.concat(annotations.stream(), otherItem.annotations.stream())
                    .distinct()
                    .toList();
            return new Item(type, name, mergedAnnotations, internal || otherItem.internal);
        }
    }
}
