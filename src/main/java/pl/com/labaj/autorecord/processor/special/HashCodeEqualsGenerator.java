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
import pl.com.labaj.autorecord.processor.MetaData;
import pl.com.labaj.autorecord.processor.SubGenerator;
import pl.com.labaj.autorecord.processor.utils.Logger;
import pl.com.labaj.autorecord.processor.utils.Method;
import pl.com.labaj.autorecord.processor.utils.StaticImports;

import javax.lang.model.element.ExecutableElement;
import java.util.List;

import static pl.com.labaj.autorecord.processor.special.SpecialMethod.HASH_CODE;

public class HashCodeEqualsGenerator extends SubGenerator {

    public HashCodeEqualsGenerator(MetaData metaData, StaticImports staticImports, Logger logger) {
        super(metaData, staticImports, logger);
    }

    @Override
    public void accept(TypeSpec.Builder recordSpecBuilder) {
        var memoizedHashCode = metaData.memoization().specialMemoized().get(HASH_CODE);
        var notIgnoredProperties = findNotIgnoredProperties();

        if (shouldNotGenerateHashCodeAndEquals(memoizedHashCode, notIgnoredProperties)) {
            return;
        }

        List.of(
                new HashCodeGenerator(metaData, staticImports, logger, memoizedHashCode, notIgnoredProperties),
                new EqualsGenerator(metaData, staticImports, logger, memoizedHashCode, notIgnoredProperties)
        ).forEach(generator -> generator.accept(recordSpecBuilder));
    }

    private List<ExecutableElement> findNotIgnoredProperties() {
        return metaData.propertyMethods().stream()
                .map(Method::new)
                .filter(method -> method.isNotAnnotatedWith(Ignored.class))
                .map(Method::method)
                .toList();
    }

    private boolean shouldNotGenerateHashCodeAndEquals(boolean memoizedHashCode, List<ExecutableElement> notIgnoredProperties) {
        if (memoizedHashCode) {
            return false;
        }

        var hasIgnoredComponents = notIgnoredProperties.size() < metaData.propertyMethods().size();
        if (hasIgnoredComponents) {
            return false;
        }

        var hasArrayComponents = notIgnoredProperties.stream()
                .map(Method::new)
                .anyMatch(Method::returnsArray);

        return !hasArrayComponents;
    }

    protected static abstract class HashCodeEqualsSubGenerator extends SubGenerator {
        protected final boolean memoizedHashCode;
        protected final List<ExecutableElement> notIgnoredProperties;

        protected HashCodeEqualsSubGenerator(MetaData metaData,
                                             StaticImports staticImports,
                                             Logger logger,
                                             boolean memoizedHashCode,
                                             List<ExecutableElement> notIgnoredProperties) {
            super(metaData, staticImports, logger);

            this.memoizedHashCode = memoizedHashCode;
            this.notIgnoredProperties = notIgnoredProperties;
        }
    }
}
