package pl.com.labaj.autorecord.processor;

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

import pl.com.labaj.autorecord.processor.context.MemoizerType;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

import static pl.com.labaj.autorecord.processor.context.MemoizerType.PL_COM_LABAJ_AUTORECORD_MEMOIZER;
import static pl.com.labaj.autorecord.processor.utils.Resources.copyResource;

public class MemoizerProcessor implements Consumer<MemoizerType> {
    private static final String UTILS = "utils/";
    private final ProcessingEnvironment processingEnv;
    private final ClassLoader classLoader;
    private Set<MemoizerType> memoizers;

    public MemoizerProcessor(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        memoizers = EnumSet.noneOf(MemoizerType.class);
        classLoader = getClass().getClassLoader();
    }

    @Override
    public void accept(MemoizerType memoizerType) {
        memoizers.add(memoizerType);
    }

    public void process() {
        memoizers.stream()
                .flatMap(MemoizerType::filesNamesToCopy)
                .distinct()
                .forEach(fileName -> copyResource(processingEnv, classLoader, UTILS, PL_COM_LABAJ_AUTORECORD_MEMOIZER, fileName));
    }
}
