package com.biswadahal.blog.models.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.googlecode.objectify.Key;

public class KeyKindValidator implements ConstraintValidator<KeyKindMatches, Key<?>> {
	private KeyKindMatches annotation;
	
	@Override
	public void initialize(KeyKindMatches annotation) {
		this.annotation = annotation;
	}

	@Override
	public boolean isValid(Key<?> key, ConstraintValidatorContext context) {
		if (key != null) {
			String keyKind = key.getKind();
			return annotation.expectedKind().equals(keyKind);
		} else {
			return true; //true because null checks should be done with @NotNull
		}
	}

}
