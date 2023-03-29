package pl.com.labaj.autorecord.processor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Set;

record Memoization(List<Item> items, boolean memoizedHashCode, boolean memoizedToString) {
    record Item(Class<?> type, String name, List<AnnotationMirror> annotations, Set<Modifier> modifiers, boolean special) {}
}
