package com.biswadahal.blog.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.Map;

import org.testng.annotations.Test;

import com.biswadahal.AppEngineTests;
import com.biswadahal.blog.models.ContentMeta;
import com.biswadahal.blog.models.MimeType;
import com.biswadahal.blog.models.PageTemplate;
import com.biswadahal.blog.models.validation.ValidationGroups.Create;
import com.biswadahal.blog.models.validation.ValidationGroups.Update;

public class PageTemplateServiceTest extends AppEngineTests {

	@Test
	public void validationIsInvokedOnSave() {
		PageTemplateService service = injector.getInstance(PageTemplateService.class);
		PageTemplate template = new PageTemplate(null);
		ServiceResult<PageTemplate> result = service.save(template, Create.class);
		assertNotNull(result);
		assertTrue(result.hasErrors());
		assertEquals(result.getErrors().size(), 1);
		Map<String, String> errorMsgs = mapToMessages(result.getErrors());
		assertEquals(errorMsgs.get("meta"), ValidationMessageTemplates.NotNull.getLabel());
	}

	@Test
	public void templateIsNotSavedOnValidationError() {
		PageTemplateService service = injector.getInstance(PageTemplateService.class);
		PageTemplate template = new PageTemplate(null);
		service.save(template, Create.class);
		assertNull(template.getId());
	}

	@Test
	public void templateIsSavedWithLatestLastModifiedTimestamp() throws InterruptedException {
		PageTemplateService service = injector.getInstance(PageTemplateService.class);
		ContentMeta meta = new ContentMeta("title", new MimeType("class", "type"));
		Date oldLastModifiedTimestamp = meta.getLastModifiedTimestamp();
		Thread.sleep(1000);
		PageTemplate template = new PageTemplate(meta);
		ServiceResult<PageTemplate> result = service.save(template, Create.class);
		assertNotNull(result);
		assertFalse(result.hasErrors());
		assertNotNull(result.getValue());
		assertTrue(result.getValue().isPresent());
		assertNotNull(result.getValue().get().getId());

		PageTemplate resultValue = result.getValue().get();
		assertNotEquals(resultValue.getMeta().getLastModifiedTimestamp(), oldLastModifiedTimestamp);
		assertEquals(resultValue.getMeta().getLastModifiedTimestamp().compareTo(oldLastModifiedTimestamp), 1);
	}

	@Test
	public void idCannotBeNullIfNotCreateValidationGroup() {
		PageTemplateService service = injector.getInstance(PageTemplateService.class);
		ContentMeta meta = new ContentMeta("title", new MimeType("class", "type"));
		PageTemplate template = new PageTemplate(meta);
		ServiceResult<PageTemplate> result = service.save(template, Update.class);
		assertNotNull(result);
		assertTrue(result.hasErrors());
		assertEquals(result.getErrors().size(), 1);
		Map<String, String> errorMsgs = mapToMessages(result.getErrors());
		assertEquals(errorMsgs.get("id"), ValidationMessageTemplates.NotNull.getLabel());
	}

}
