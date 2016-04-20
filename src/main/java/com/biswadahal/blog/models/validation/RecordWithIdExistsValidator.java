package com.biswadahal.blog.models.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.googlecode.objectify.Key;

public class RecordWithIdExistsValidator implements ConstraintValidator<RecordWithIdExists, Long> {
	private RecordWithIdExists annotation;
	
	@Override
	public void initialize(RecordWithIdExists annotation) {
		this.annotation = annotation;
	}

	@Override
	public boolean isValid(Long id, ConstraintValidatorContext context) {
		if (id != null) {
			if (id == 0L) {
				return false;
			}
			Class<?> idKind = annotation.idKind();
			Key<?> key = Key.create(idKind, id);
			RecordWithKeyExistsValidator recordWithKeyExistsValidator = new RecordWithKeyExistsValidator();
			return recordWithKeyExistsValidator.isValid(key, context);
		} else {
			return true; //true because null checks should be done with @NotNull
		}
	}

}
