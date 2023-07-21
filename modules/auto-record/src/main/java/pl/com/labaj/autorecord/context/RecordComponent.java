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

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Represents a component of the generated record.
 * <p>
 * This interface provides methods to access information about the record component.
 *
 * @since 2.1.0
 */
public interface RecordComponent {
    /**
     * Gets the type of the record component.
     *
     * @return the type of the record component.
     * @see TypeMirror
     */
    TypeMirror type();

    /**
     * Gets the name of the record component.
     *
     * @return the name of the record component.
     */
    String name();

    /**
     * Gets the list of {@link AnnotationMirror} instances representing the annotations applied to the record component.
     *
     * @return the list of {@link AnnotationMirror} instances representing the annotations applied to the record component.
     * @see AnnotationMirror
     */
    List<AnnotationMirror> annotations();

    /**
     * Checks if the component's type is a primitive type.
     *
     * @return {@code true} if the component's type is a primitive type, {@code false} otherwise.
     */
    boolean isPrimitive();

    /**
     * Checks if the component's type is an array type.
     *
     * @return {@code true} if the component's type is an array type, {@code false} otherwise.
     */
    boolean isArray();

    /**
     * Checks if the component is annotated with a specific annotation class.
     *
     * @param annotationClass the {@link Class} object representing the annotation to check for.
     * @return {@code true} if the component is annotated with the specified annotation class, {@code false} otherwise.
     */
    boolean isAnnotatedWith(Class<? extends Annotation> annotationClass);
}
