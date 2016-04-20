package com.biswadahal.blog.models.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.biswadahal.blog.dao.OfyService;
import com.googlecode.objectify.Key;

public class RecordWithKeyExistsValidator implements ConstraintValidator<RecordWithKeyExists, Key<?>> {
	@Override
	public void initialize(RecordWithKeyExists annotation) {
	}

	@Override
	public boolean isValid(Key<?> key, ConstraintValidatorContext context) {
		if (key != null) {
			Object object = OfyService.ofy().load().key(key).now();
			return object != null;
		} else {
			return true; //true because null-checks should be done with @NotNull
		}
	}

}
