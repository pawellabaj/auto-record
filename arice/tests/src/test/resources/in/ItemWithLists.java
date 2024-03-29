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

import com.google.common.collect.ImmutableList;
import pl.com.labaj.autorecord.AutoRecord;
import pl.com.labaj.autorecord.testcase.user.UserCollections;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@AutoRecord
@AutoRecord.Extension(extensionClass = "pl.com.labaj.autorecord.extension.arice.ImmutableCollectionsExtension")
interface ItemWithLists<E> {
    List<E> list();
    LinkedList<E> linkedList();
    ArrayList<E> arrayList();
    UserCollections.UserList<E> userList();
    UserCollections.UserListImpl<E> userListImpl();
    ImmutableList<E> immutableList();
    @Nullable List<E> nullableList();
    @Nullable LinkedList<E> nullableLinkedList();
    @Nullable ArrayList<E> nullableArrayList();
    @Nullable UserCollections.UserList<E> nullableUserList();
    @Nullable UserCollections.UserListImpl<E> nullableUserListImpl();
    @Nullable ImmutableList<E> nullableImmutableList();
}
