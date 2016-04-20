package com.biswadahal.blog.models;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Pattern;

import org.testng.annotations.Test;

public class TagTest {
	Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	public void emptyLabelsAreNotStored() {
		String[] labels = new String[] { "", "     " };
		for (String label : labels) {
			Tag tag = new Tag(label);
			assertNull(tag.getLabel(), String.format("Label input:'%s' should not be allowed", label));
		}
	}

	@Test
	public void labelIsLowercased() {
		Tag tag = new Tag("LaBeL");
		assertEquals(tag.getLabel(), "label");
	}

	@Test
	public void labelIsStoredWhenIncludesAllowedCharacters() {
		String[] goodInputs = new String[] { "c", "c#", "c++", "c*", "c++x10", "category-theory", "category_theory" };
		for (String label : goodInputs) {
			Tag tag = new Tag(label);
			Set<?> violations = validator.validate(tag);
			assertEquals(violations.size(), 0, String.format("Label input:'%s' should be allowed", label));
		}
	}

	@Test
	public void labelIsNotStoredWhenIncludesDisallowedCharacters() {
		String[] badInputs = new String[] { "c@", "c$", "c!"};
		for (String label : badInputs) {
			Tag tag = new Tag(label);
			List<ConstraintViolation<Tag>> violations = new ArrayList<>(validator.validate(tag));
			assertEquals(violations.size(), 1, String.format("Label input:'%s' should not be allowed", label));
			ConstraintViolation<Tag> v = violations.get(0);
			assertEquals(v.getMessageTemplate(), String.format("{%s.message}", Pattern.class.getName()));
		}
	}
	
}
