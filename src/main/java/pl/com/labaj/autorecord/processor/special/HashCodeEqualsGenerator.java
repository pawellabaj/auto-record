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
import pl.com.labaj.autorecord.processor.Generator;
import pl.com.labaj.autorecord.processor.GeneratorMetaData;
import pl.com.labaj.autorecord.processor.StaticImport;
import pl.com.labaj.autorecord.processor.SubGenerator;
import pl.com.labaj.autorecord.processor.utils.Logger;
import pl.com.labaj.autorecord.processor.utils.Method;

import javax.lang.model.element.ExecutableElement;
import java.util.List;

import static pl.com.labaj.autorecord.processor.special.SpecialMethod.HASH_CODE;

public class HashCodeEqualsGenerator extends SubGenerator {

    public HashCodeEqualsGenerator(GeneratorMetaData metaData) {
        super(metaData);
    }

    @Override
    public void generate(TypeSpec.Builder recordSpecBuilder, List<StaticImport> staticImports, Logger logger) {
        var memoizedHashCode = metaData.memoization().specialMemoized().get(HASH_CODE);
        var notIgnoredProperties = findNotIgnoredProperties();

        if (shouldNotGenerateHashCodeAndEquals(memoizedHashCode, notIgnoredProperties)) {
            return;
        }

        List.of(
                new HashCodeGenerator(metaData, memoizedHashCode, notIgnoredProperties),
                new EqualsGenerator(metaData, memoizedHashCode, notIgnoredProperties)
        ).forEach(generator -> generator.generate(recordSpecBuilder, staticImports, logger));
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

    protected static abstract class HashCodeEqualsSubGenerator implements Generator {
        protected final GeneratorMetaData metaData;
        protected final boolean memoizedHashCode;
        protected final List<ExecutableElement> notIgnoredProperties;

        protected HashCodeEqualsSubGenerator(GeneratorMetaData metaData, boolean memoizedHashCode, List<ExecutableElement> notIgnoredProperties) {
            this.metaData = metaData;
            this.memoizedHashCode = memoizedHashCode;
            this.notIgnoredProperties = notIgnoredProperties;
        }
    }
}
