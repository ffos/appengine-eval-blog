package com.biswadahal.blog.models.validation;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import com.biswadahal.AppEngineTests;
import com.googlecode.objectify.Key;

public class KeyKindInCollectionValidatorTest extends AppEngineTests {
	
	@KeyKindMatches(expectedKind="Integer")
	public static void methodToTestAnnotation(){};

	private KeyKindInCollectionValidator createValidator() throws NoSuchMethodException {
		KeyKindInCollectionValidator validator = new KeyKindInCollectionValidator();
		KeyKindMatches annotation = KeyKindInCollectionValidatorTest.class.getMethod("methodToTestAnnotation").getAnnotation(KeyKindMatches.class);
		validator.initialize(annotation);
		return validator;
	}

	private ArgumentCaptor<String> configureValidatorContext(ConstraintValidatorContext context) {
		ConstraintViolationBuilder cvBuilder= Mockito.mock(ConstraintViolationBuilder.class);
		Mockito.doNothing().when(context).disableDefaultConstraintViolation();
		ArgumentCaptor<String> argCapture = ArgumentCaptor.forClass(String.class);
		Mockito.doReturn(cvBuilder).when(context).buildConstraintViolationWithTemplate(argCapture.capture());
		Mockito.doReturn(context).when(cvBuilder).addConstraintViolation();
		return argCapture;
	}
	
	@Test
	public void isValid() throws NoSuchMethodException, SecurityException{
		KeyKindInCollectionValidator validator = createValidator();
		Collection<Key<?>> validKeys = new ArrayList<>();
		validKeys.add(Key.create(Integer.class, "websafeIntKeyString1"));
		validKeys.add(Key.create(Integer.class, "websafeIntKeyString2"));
		
		Collection<Collection<Key<?>>> testKeyCollections = new ArrayList<>();
		testKeyCollections.add(null); //should be valid
		testKeyCollections.add(Collections.<Key<?>>emptyList());
		testKeyCollections.add(validKeys);
		
		for(Collection<Key<?>> keyCollection: testKeyCollections){
			ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
			configureValidatorContext(context);
			boolean isValid = validator.isValid(keyCollection, context);
			assertTrue(isValid);
		}
	}
	
	@Test
	public void isInValid() throws NoSuchMethodException, SecurityException{
		KeyKindInCollectionValidator validator = createValidator();
		Collection<Key<?>> keys = new ArrayList<>();
		keys.add(Key.create(String.class, "websafeIntKeyString"));
		keys.add(Key.create(Integer.class, "websafeIntKeyString"));
		ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
		configureValidatorContext(context);
		boolean isValid = validator.isValid(keys, context);
		assertFalse(isValid);
	}

	@Test
	public void validationMessageOnFailure() throws NoSuchMethodException, SecurityException{
		KeyKindInCollectionValidator validator = createValidator();
		Collection<Key<?>> keys = new ArrayList<>();
		keys.add(Key.create(String.class, "websafeIntKeyString"));
		keys.add(Key.create(Integer.class, "websafeIntKeyString"));
		
		ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
		ArgumentCaptor<String> argCapture = configureValidatorContext(context);
				
		boolean isValid = validator.isValid(keys, context);
		assertFalse(isValid);
		assertEquals(argCapture.getValue(), KeyKindMatches.MESSAGE_WHEN_TARGET_IS_COLLECTION);
	}
}
