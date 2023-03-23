package pl.com.labaj.autorecord.processor;

import pl.com.labaj.autorecord.AutoRecord;

import javax.lang.model.element.ExecutableElement;
import java.util.List;
import java.util.function.Predicate;

enum SpecialMethod {
    HASH_CODE("hashCode", Integer.TYPE, AutoRecord.Options::memoizedHashCode),
    TO_STRING("toString", String.class, AutoRecord.Options::memoizedToString);

    private static final List<SpecialMethod> ALL_METHODS = List.of(HASH_CODE, TO_STRING);
    private final String methodName;
    private final Class<?> type;
    private final Predicate<AutoRecord.Options> optionsPredicate;

    SpecialMethod(String methodName, Class<?> type, Predicate<AutoRecord.Options> optionsPredicate) {
        this.methodName = methodName;
        this.type = type;
        this.optionsPredicate = optionsPredicate;
    }

    static List<SpecialMethod> specialMethods() {
        return ALL_METHODS;
    }

    static boolean isSpecial(ExecutableElement method) {
        var methodName = method.getSimpleName();
        return methodName.contentEquals(HASH_CODE.methodName) || methodName.contentEquals(TO_STRING.methodName);
    }

    static SpecialMethod from(ExecutableElement method) {
        var methodName = method.getSimpleName();
        return methodName.contentEquals(HASH_CODE.methodName) ? HASH_CODE : TO_STRING;
    }

    String methodName() {
        return methodName;
    }

    Class<?> type() {
        return type;
    }

    boolean isMemoizedInOptions(AutoRecord.Options recordOptions) {
        return optionsPredicate.test(recordOptions);
    }
}
