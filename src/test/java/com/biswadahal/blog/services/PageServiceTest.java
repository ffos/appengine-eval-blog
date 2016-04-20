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
import com.biswadahal.blog.dao.OfyService;
import com.biswadahal.blog.models.ContentMeta;
import com.biswadahal.blog.models.MimeType;
import com.biswadahal.blog.models.Page;
import com.biswadahal.blog.models.Page.Type;
import com.biswadahal.blog.models.PageTemplate;
import com.biswadahal.blog.models.validation.ValidationGroups.Create;
import com.biswadahal.blog.models.validation.ValidationGroups.Update;
import com.googlecode.objectify.Key;

public class PageServiceTest extends AppEngineTests {

	@Test
	public void validationIsInvokedOnSave() {
		PageService service = injector.getInstance(PageService.class);
		Page page = new Page(null, null, null);
		ServiceResult<Page> result = service.save(page, Create.class);
		assertNotNull(result);
		assertTrue(result.hasErrors());
		Map<String, String> errorMsgs = mapToMessages(result.getErrors());
		assertEquals(errorMsgs.get("template"), ValidationMessageTemplates.NotNull.getLabel());
		assertEquals(errorMsgs.get("type"), ValidationMessageTemplates.NotNull.getLabel());
		assertEquals(errorMsgs.get("meta"), ValidationMessageTemplates.NotNull.getLabel());
	}

	@Test
	public void pageIsNotSavedOnValidationError() {
		PageService service = injector.getInstance(PageService.class);
		Page page = new Page(null, null, null);
		service.save(page, Create.class);
		assertNull(page.getId());
	}

	@Test
	public void pageIsSavedWithLatestLastModifiedTimestamp() throws InterruptedException {
		PageService service = injector.getInstance(PageService.class);
		ContentMeta meta = new ContentMeta("title", new MimeType("class", "type"));
		Date oldLastModifiedTimestamp = meta.getLastModifiedTimestamp();
		Thread.sleep(1000);
		PageTemplate template = getSavedPageTemplate(meta);
		Page page = new Page(template.getKey(), Type.MAIN, meta);
		ServiceResult<Page> result = service.save(page, Create.class);
		assertNotNull(result);
		assertFalse(result.hasErrors());
		assertNotNull(result.getValue());
		assertTrue(result.getValue().isPresent());
		assertNotNull(result.getValue().get().getId());

		Page resultValue = result.getValue().get();
		assertNotEquals(resultValue.getMeta().getLastModifiedTimestamp(), oldLastModifiedTimestamp);
		assertEquals(resultValue.getMeta().getLastModifiedTimestamp().compareTo(oldLastModifiedTimestamp), 1);
	}

	private PageTemplate getSavedPageTemplate(ContentMeta meta) {
		PageTemplate template = new PageTemplate(meta);
		OfyService.ofy().save().entities(template).now();
		return template;
	}
	
	@Test
	public void idCannotBeNullIfNotCreateValidationGroup() {
		PageService service = injector.getInstance(PageService.class);
		ContentMeta meta = new ContentMeta("title", new MimeType("class", "type"));
		Page page = new Page(Key.create(PageTemplate.class, 101L), Type.MAIN, meta);
		ServiceResult<Page> result = service.save(page, Update.class);
		assertNotNull(result);
		assertTrue(result.hasErrors());
		Map<String, String> errorMsgs = mapToMessages(result.getErrors());
		assertEquals(errorMsgs.get("id"), ValidationMessageTemplates.NotNull.getLabel());		
	}
	
}
