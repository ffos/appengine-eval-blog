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
@Constraint(validatedBy = {RecordWithKeyExistsValidator.class, RecordWithKeyInCollectionExistsValidator.class})
@Documented
public @interface RecordWithKeyExists {
	public static final String MESSAGE_WHEN_TARGET_IS_COLLECTION = "{com.biswadahal.blog.models.validation.RecordWithKeyExists.inCollection.message}";
	public static final String MESSAGE_WHEN_TARGET_IS_NOT_COLLECTION = "{com.biswadahal.blog.models.validation.RecordWithKeyExists.message}";
	
    String message() default MESSAGE_WHEN_TARGET_IS_NOT_COLLECTION;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
