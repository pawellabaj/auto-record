package pl.com.labaj.autorecord.test.generation;

import pl.com.labaj.autorecord.AutoRecord;

import java.util.HashSet;
import java.util.function.Function;

@AutoRecord
public interface ComplicatedGenerics<A, B extends Function<Integer, B> & Comparable<B>, C extends HashSet<A>> {
    A one();

    B two();

    C three();
}
