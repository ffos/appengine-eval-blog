package com.biswadahal.blog.models.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {TextSizeValidator.class})
@Documented
public @interface TextSize {
	//ported over from @Size
	String message() default "{javax.validation.constraints.Size.message}";
	Class<?>[] groups() default { };
	Class<? extends Payload>[] payload() default { };
	int min() default 0;
	int max() default Integer.MAX_VALUE;
}
