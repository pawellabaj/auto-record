package pl.com.labaj.autorecord.extension;

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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.extension.compact.ImmutableCollectionsExtension;

import java.util.LinkedHashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

@AutoRecord
@AutoRecord.Extension(
        extensionClass = ImmutableCollectionsExtension.class,
        parameters = {"com.google.common.collect.ImmutableSet", "com.google.common.collect.ImmutableSortedSet"})
public interface CollectionsInterface {
    String dummyProperty();

    Set<String> aSet();

    SortedSet<String> aSortedSet();

    NavigableSet<String> aNavigableSet();

    LinkedHashSet<String> aLinkedHashSet();

    ImmutableSet<String> anImmutableSet();
    ImmutableSortedSet<String> anImmutableSortedSet();
}
