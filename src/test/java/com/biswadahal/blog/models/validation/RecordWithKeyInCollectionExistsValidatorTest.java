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
import com.biswadahal.blog.models.ContentMeta;
import com.biswadahal.blog.models.MimeType;
import com.biswadahal.blog.models.PageTemplate;
import com.biswadahal.blog.models.validation.ValidationGroups.Create;
import com.biswadahal.blog.services.PageTemplateService;
import com.biswadahal.blog.services.ServiceResult;
import com.googlecode.objectify.Key;

public class RecordWithKeyInCollectionExistsValidatorTest extends AppEngineTests {
	
	@RecordWithKeyExists
	public static void methodToTestAnnotation(){};

	private RecordWithKeyInCollectionExistsValidator createValidator() throws NoSuchMethodException {
		RecordWithKeyInCollectionExistsValidator validator = new RecordWithKeyInCollectionExistsValidator();
		RecordWithKeyExists annotation = RecordWithKeyInCollectionExistsValidatorTest.class.getMethod("methodToTestAnnotation").getAnnotation(RecordWithKeyExists.class);
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
		RecordWithKeyInCollectionExistsValidator validator = createValidator();
		PageTemplateService service = injector.getInstance(PageTemplateService.class);
		PageTemplate pt1 = new PageTemplate(new ContentMeta("title", new MimeType("class", "type")));
		ServiceResult<PageTemplate> result = service.save(pt1, Create.class);
		assertFalse(result.hasErrors());
		PageTemplate pt2 = new PageTemplate(new ContentMeta("title", new MimeType("class", "type")));
		result = service.save(pt2, Create.class);
		assertFalse(result.hasErrors());
		
		Collection<Key<PageTemplate>> validKeys = new ArrayList<>();
		validKeys.add(pt1.getKey());
		validKeys.add(pt2.getKey());

		
		Collection<Collection<Key<PageTemplate>>> testKeyCollections = new ArrayList<>();
		testKeyCollections.add(null); //should be valid
		testKeyCollections.add(Collections.<Key<PageTemplate>>emptyList());
		testKeyCollections.add(validKeys);
		
		for(Collection<Key<PageTemplate>> keyCollection: testKeyCollections){
			ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
			configureValidatorContext(context);
			boolean isValid = validator.isValid(keyCollection, context);
			assertTrue(isValid);
		}
	}
	
	@Test
	public void isInValid() throws NoSuchMethodException, SecurityException{
		RecordWithKeyInCollectionExistsValidator validator = createValidator();
		Collection<Key<PageTemplate>> keys = new ArrayList<>();
		keys.add(Key.create(PageTemplate.class, "websafeIntKeyString1"));
		keys.add(Key.create(PageTemplate.class, "websafeIntKeyString2"));
		ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
		configureValidatorContext(context);
		boolean isValid = validator.isValid(keys, context);
		assertFalse(isValid);
	}

	@Test
	public void validationMessageOnFailure() throws NoSuchMethodException, SecurityException{
		RecordWithKeyInCollectionExistsValidator validator = createValidator();
		Collection<Key<PageTemplate>> keys = new ArrayList<>();
		keys.add(Key.create(PageTemplate.class, "websafeIntKeyString1"));
		keys.add(Key.create(PageTemplate.class, "websafeIntKeyString2"));
		ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
		configureValidatorContext(context);
		ArgumentCaptor<String> argCapture = configureValidatorContext(context);
		boolean isValid = validator.isValid(keys, context);
		assertFalse(isValid);
		assertEquals(argCapture.getValue(), RecordWithKeyExists.MESSAGE_WHEN_TARGET_IS_COLLECTION);
	}
}
