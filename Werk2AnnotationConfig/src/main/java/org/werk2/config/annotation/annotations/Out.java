package org.werk2.config.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Out {
	/**
	 * Function parameter name
	 * Since parameter names via reflection look like (arg0, arg1, arg2, ...) in release build
	 * @return Parameter name 
	 */
	String name() default "";
	AnnoType type() default AnnoType.AUTO;
	
	//http://tutorials.jenkov.com/java-reflection/generics.html#fieldtypes
	String runtimeType() default "";
		
	String docTitle() default "";
	String docDescription() default "";
}