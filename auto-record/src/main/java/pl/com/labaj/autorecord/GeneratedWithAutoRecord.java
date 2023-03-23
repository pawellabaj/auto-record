package pl.com.labaj.autorecord;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Starting from <a href="https://www.jacoco.org/">JaCoCo</a> 0.8.2, classes and methods annotated with annotation
 * following properties:
 * <ul>
 *     <li>the annotation name includes <em>"Generated"</em></li>
 *     <li>the annotation retention policy is <code>RUNTIME</code> or <code>CLASS</code></li>
 * </ul>
 */
@Documented
@Retention(CLASS)
@Target({TYPE, METHOD, CONSTRUCTOR})
public @interface GeneratedWithAutoRecord {}
