package pl.com.labaj.autorecord.testcase;

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

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import pl.com.labaj.autorecord.GeneratedWithAutoRecord;
import pl.com.labaj.autorecord.extension.arice.AutoRecordImmutableCollectionsUtilities;
import pl.com.labaj.autorecord.extension.arice.Methods;
import pl.com.labaj.autorecord.testcase.user.UserCollections;

@Generated("pl.com.labaj.autorecord.AutoRecord")
@GeneratedWithAutoRecord
@AutoRecordImmutableCollectionsUtilities(className = "pl.com.labaj.autorecord.extension.arice.Methods")
record ItemWithListsNoUserDefinedCollectionsRecord<E>(List<E> list,
                                                      LinkedList<E> linkedList,
                                                      ArrayList<E> arrayList,
                                                      UserCollections.UserList<E> userList,
                                                      UserCollections.UserListImpl<E> userListImpl,
                                                      ImmutableList<E> immutableList,
                                                      @Nullable List<E> nullableList,
                                                      @Nullable LinkedList<E> nullableLinkedList,
                                                      @Nullable ArrayList<E> nullableArrayList,
                                                      @Nullable UserCollections.UserList<E> nullableUserList,
                                                      @Nullable UserCollections.UserListImpl<E> nullableUserListImpl,
                                                      @Nullable ImmutableList<E> nullableImmutableList) implements ItemWithListsNoUserDefinedCollections<E> {
    ItemWithListsNoUserDefinedCollectionsRecord {
        // pl.com.labaj.autorecord.processor.AutoRecordProcessor
        requireNonNull(list, "list must not be null");
        requireNonNull(linkedList, "linkedList must not be null");
        requireNonNull(arrayList, "arrayList must not be null");
        requireNonNull(userList, "userList must not be null");
        requireNonNull(userListImpl, "userListImpl must not be null");
        requireNonNull(immutableList, "immutableList must not be null");

        // pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension
        list = Methods.immutable(list);
        nullableList = isNull(nullableList) ? null : Methods.immutable(nullableList);
    }
}