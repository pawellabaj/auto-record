package pl.com.labaj.autorecord.processor.special;

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

import com.squareup.javapoet.TypeSpec;
import pl.com.labaj.autorecord.Ignored;
import pl.com.labaj.autorecord.processor.SubGenerator;
import pl.com.labaj.autorecord.processor.context.AutoRecordContext;
import pl.com.labaj.autorecord.processor.utils.Method;

import javax.lang.model.element.ExecutableElement;
import java.util.List;

import static pl.com.labaj.autorecord.processor.special.SpecialMethod.HASH_CODE;

public class HashCodeEqualsGenerator extends SubGenerator {

    public HashCodeEqualsGenerator(AutoRecordContext context) {
        super(context);
    }

    @Override
    public void generate(TypeSpec.Builder recordBuilder) {
        boolean memoizedHashCode = context.generation().memoization().isMemoized(HASH_CODE);
        List<ExecutableElement> notIgnoredProperties = findNotIgnoredProperties();

        if (shouldNotGenerateHashCodeAndEquals(memoizedHashCode, notIgnoredProperties)) {
            return;
        }

        List.of(
                new HashCodeGenerator(context, memoizedHashCode, notIgnoredProperties),
                new EqualsGenerator(context, memoizedHashCode, notIgnoredProperties)
        ).forEach(generator -> generator.generate(recordBuilder));
    }

    private List<ExecutableElement> findNotIgnoredProperties() {
        return context.source().propertyMethods().stream()
                .map(Method::new)
                .filter(method -> method.isNotAnnotatedWith(Ignored.class))
                .map(Method::method)
                .toList();
    }

    private boolean shouldNotGenerateHashCodeAndEquals(boolean memoizedHashCode, List<ExecutableElement> notIgnoredProperties) {
        if (memoizedHashCode) {
            return false;
        }

        var hasIgnoredComponents = notIgnoredProperties.size() < context.source().propertyMethods().size();
        if (hasIgnoredComponents) {
            return false;
        }

        var hasArrayComponents = notIgnoredProperties.stream()
                .map(Method::new)
                .anyMatch(Method::returnsArray);

        return !hasArrayComponents;
    }

    static abstract class HashCodeEqualsSubGenerator extends SubGenerator {
        private final boolean memoizedHashCode;
        private final List<ExecutableElement> notIgnoredProperties;

        protected HashCodeEqualsSubGenerator(AutoRecordContext context, boolean memoizedHashCode, List<ExecutableElement> notIgnoredProperties) {
            super(context);
            this.memoizedHashCode = memoizedHashCode;
            this.notIgnoredProperties = notIgnoredProperties;
        }

        protected boolean isMemoizedHashCode() {
            return memoizedHashCode;
        }

        protected List<ExecutableElement> notIgnoredProperties() {
            return notIgnoredProperties;
        }
    }
}
