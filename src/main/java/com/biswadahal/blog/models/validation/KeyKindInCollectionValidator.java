package com.biswadahal.blog.models.validation;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.googlecode.objectify.Key;

public class KeyKindInCollectionValidator implements ConstraintValidator<KeyKindMatches, Collection<?>> {
	//Implementation Note:
	//Cannot do: implements ConstraintValidator<KeyKindMatches, Collection<Key<?>>> or implements ConstraintValidator<KeyKindMatches, Collection<Key>>
	//Without validator failing with: HV000030: No validator could be found for type: java.util.Collection<com.googlecode.objectify.Key<com.biswadahal.blog.models.Tag>>
	//However, can do: implements ConstraintValidator<KeyKindMatches, Collection<Key<Tag>>>, but that defeats the purpose to generics
	private KeyKindMatches annotation;

	@Override
	public void initialize(KeyKindMatches annotation) {
		this.annotation = annotation;
	}

	@Override
	public boolean isValid(Collection<?> keys, ConstraintValidatorContext context) {
		if (keys == null || keys.isEmpty()) {
			return true;
		} else {
			boolean allValid = true;
			KeyKindValidator keyKindValidator = new KeyKindValidator();
			keyKindValidator.initialize(annotation);
			for(Object o: keys){
				if(!(o instanceof Key)){
					throw new IllegalArgumentException(String.format("Expected type %s, but got %s", Key.class.getName(), o.getClass() ));
				}
				Key<?> k = (Key<?>)o;
				allValid = keyKindValidator.isValid(k, context);
				if (!allValid) {
					break;
				}
			}
			if(!allValid){
				context.disableDefaultConstraintViolation();
		        context.buildConstraintViolationWithTemplate(KeyKindMatches.MESSAGE_WHEN_TARGET_IS_COLLECTION).addConstraintViolation();
			}
			return allValid;
		}
	}

}
