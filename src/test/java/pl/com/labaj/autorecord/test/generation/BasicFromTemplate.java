package pl.com.labaj.autorecord.test.generation;

import pl.com.labaj.autorecord.WithTemplate;

import java.util.List;
import java.util.Optional;

@WithTemplate
public interface BasicFromTemplate {
    String text();

    int number();

    List<Optional<Integer>> genericCollection();
}
