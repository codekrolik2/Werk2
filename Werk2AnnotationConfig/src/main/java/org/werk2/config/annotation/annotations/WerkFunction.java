package org.werk2.config.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface WerkFunction {
	/**
	 * @return Function alias, if physical name is too cumbersome
	 */
	String name() default "";
	String docTitle() default "";
	String docDescription() default "";
}
