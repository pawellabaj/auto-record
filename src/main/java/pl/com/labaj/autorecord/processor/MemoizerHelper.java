package pl.com.labaj.autorecord.processor;

import pl.com.labaj.autorecord.Memoizer;

final class MemoizerHelper {
    private static final String MEMOIZER = Memoizer.class.getSimpleName();

    private MemoizerHelper() {}

    static String memoizerComponentName(String name) {
        return name + MEMOIZER;
    }

    static String memoizerComputeMethodName(String name) {
        return memoizerComponentName(name) + ".computeIfAbsent";
    }

    static String memoizerConstructorStatement() {
        return "new " + MEMOIZER + "<>()";
    }
}
