package com.biswadahal.blog.models;

import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Pattern;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class MimeTypeTest {
	Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	public void emptyClassNamesAreNotStored() {
		String[] mimeClassNames = new String[] { "", "     " };
		for (String mimeClass : mimeClassNames) {
			MimeType mt = new MimeType(mimeClass, "A");
			assertNull(mt.getClassName(), String.format("Mime classname input:'%s' should not be allowed", mimeClass));
		}
	}

	@Test
	public void emptyTypeNamesAreNotStored() {
		String[] mimeTypeNames = new String[] { "", "     " };
		for (String mimeType : mimeTypeNames) {
			MimeType mt = new MimeType("A", mimeType);
			assertNull(mt.getTypeName(), String.format("Mime typename input:'%s' should not be allowed", mimeType));
		}
	}

	@Test
	public void classNamesNotStoredWhenIncludesDisallowedCharacters() {
		String[] mimeClassNames = new String[] { "1", "_", "." };
		for (String mimeClass : mimeClassNames) {
			MimeType mt = new MimeType(mimeClass, "A");
			List<ConstraintViolation<MimeType>> violations = new ArrayList<>(validator.validate(mt));
			assertEquals(violations.size(), 1,
					String.format("Mime classname input:'%s' should not be allowed", mimeClass));
			ConstraintViolation<MimeType> v = violations.get(0);
			assertEquals(v.getMessageTemplate(), String.format("{%s.message}", Pattern.class.getName()));
		}
	}

	@Test
	public void typeNamesStoredWhenIncludesAllowedCharacters() {
		String[] typeNames = new String[] { "1", "a1", "a b", "a-b", "a.b", "a,b", "a+b", "a;b", "a_b" };
		for (String typeName : typeNames) {
			MimeType mt = new MimeType("A", typeName);
			List<ConstraintViolation<MimeType>> violations = new ArrayList<>(validator.validate(mt));
			assertEquals(violations.size(), 0, String.format("Mime classname input:'%s' should be allowed", typeName));
		}
	}

	@Test
	public void typeNamesNotStoredWhenIncludesDisallowedCharacters() {
		String[] typeNames = new String[] { "a@", "a?" };
		for (String typeName : typeNames) {
			MimeType mt = new MimeType("A", typeName);
			List<ConstraintViolation<MimeType>> violations = new ArrayList<>(validator.validate(mt));
			assertEquals(violations.size(), 1, String.format("Mime classname input:'%s' should not be allowed", typeName));
			ConstraintViolation<MimeType> v = violations.get(0);
			assertEquals(v.getMessageTemplate(), String.format("{%s.message}", Pattern.class.getName()));
		}
	}

	@Test
	public void mimeIsStoredInLowerCase() {
		MimeType mt = new MimeType("ClAsS", "tYpE");
		assertEquals(mt.getClassName(), "class");
		assertEquals(mt.getTypeName(), "type");
	}

}
