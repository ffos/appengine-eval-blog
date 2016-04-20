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
@Constraint(validatedBy = {RecordWithIdExistsValidator.class})
@Documented
public @interface RecordWithIdExists {
	public static final String MESSAGE_KEY = "{com.biswadahal.blog.models.validation.RecordWithIdExists.message}";
	
    String message() default MESSAGE_KEY;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    Class<?> idKind();
}
