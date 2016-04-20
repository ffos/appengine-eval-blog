package com.biswadahal.blog.models.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.appengine.api.datastore.Text;

public class TextSizeValidator implements ConstraintValidator<TextSize, Text> {
	private TextSize annotation;

	@Override
	public void initialize(TextSize annotation) {
		this.annotation = annotation;
	}

	@Override
	public boolean isValid(Text text, ConstraintValidatorContext context) {
		if(text == null){
			return true;
		}
		int size = text.getValue() == null ? 0 : text.getValue().length();
		if(size < annotation.min() || size > annotation.max()){
			return false;
		}else{
			return true;
		}
	}

}
