package pl.com.labaj.autorecord.test;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(value = RUNTIME)
@Testable
@Test
public @interface TestFor {
    Class<?> value();
}
