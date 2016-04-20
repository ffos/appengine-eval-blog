package com.biswadahal.blog.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.testng.annotations.Test;

import com.biswadahal.AppEngineTests;
import com.biswadahal.blog.models.Tag;
import com.biswadahal.blog.models.validation.ValidationGroups.Create;
import com.biswadahal.blog.models.validation.ValidationGroups.Update;

public class TagServiceTest extends AppEngineTests {
	
	@Test
	public void validationIsInvokedOnSave(){
		TagService service = injector.getInstance(TagService.class);
		Tag tag = new Tag(null);
		ServiceResult<Tag> result = service.save(tag, Create.class);
		assertNotNull(result);
		assertTrue(result.hasErrors());
		assertEquals(result.getErrors().size(), 1);
		Map<String, String> errorMsgs = mapToMessages(result.getErrors());
		assertEquals(errorMsgs.get("label"), ValidationMessageTemplates.NotNull.getLabel());
	}
	
	@Test
	public void tagIsNotSavedOnValidationError(){
		TagService service = injector.getInstance(TagService.class);
		Tag tag = new Tag(null);
		service.save(tag, Create.class);
		assertNull(tag.getId());
	}
	
	@Test
	public void tagIsSaved(){
		TagService service = injector.getInstance(TagService.class);
		Tag tag = new Tag("good");
		ServiceResult<Tag> result = service.save(tag, Create.class);
		assertNotNull(result);
		assertFalse(result.hasErrors());
		assertNotNull(result.getValue());
		assertTrue(result.getValue().isPresent());
		assertNotNull(result.getValue().get().getId());
		assertEquals(result.getValue().get(), tag);
	}
	
	@Test
	public void idCannotBeNullIfNotCreateValidationGroup() {
		TagService service = injector.getInstance(TagService.class);
		Tag tag = new Tag("good");
		ServiceResult<Tag> result = service.save(tag, Update.class);
		assertNotNull(result);
		assertTrue(result.hasErrors());
		assertEquals(result.getErrors().size(), 1);
		Map<String, String> errorMsgs = mapToMessages(result.getErrors());
		assertEquals(errorMsgs.get("id"), ValidationMessageTemplates.NotNull.getLabel());
	}
	
	
	@Test
	public void findByLabelOnEmptyQueryString(){
		TagService service = injector.getInstance(TagService.class);
		String[] queryStrings = new String[]{null, "", "  "};
		for(String query: queryStrings){
			ServiceResult<Tag> result = service.findByLabel(query);
			assertNotNull(result);
			assertFalse(result.hasErrors());
			assertNotNull(result.getValue());
			assertFalse(result.getValue().isPresent());
		}
	}
	@Test
	public void findByLabelFiltersCaseInsensitive(){
		TagService service = injector.getInstance(TagService.class);
		Tag toSave = new Tag("ToSave");
		ServiceResult<Tag> result = service.save(toSave, Create.class);
		assertFalse(result.hasErrors());
		Long savedId = toSave.getId();
		
		result = service.findByLabel("TOSAVE");
		assertNotNull(result);
		assertFalse(result.hasErrors());
		assertNotNull(result.getValue());
		assertTrue(result.getValue().isPresent());
		assertEquals(result.getValue().get().getId(), savedId);
	}
}
