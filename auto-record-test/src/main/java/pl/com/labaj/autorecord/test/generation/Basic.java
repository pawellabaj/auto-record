package pl.com.labaj.autorecord.test.generation;

import pl.com.labaj.autorecord.AutoRecord;

import java.util.List;
import java.util.Optional;

@AutoRecord
public interface Basic {
    String text();

    int number();

    List<Optional<Integer>> genericCollection();
}
