package com.biswadahal.blog.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Date;
import java.util.Map;

import org.testng.annotations.Test;

import com.biswadahal.AppEngineTests;
import com.biswadahal.blog.models.Asset;
import com.biswadahal.blog.models.ContentMeta;
import com.biswadahal.blog.models.MimeType;
import com.biswadahal.blog.models.validation.ValidationGroups.Create;
import com.biswadahal.blog.models.validation.ValidationGroups.Update;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;

public class AssetServiceTest extends AppEngineTests {

	@Test
	public void validationIsInvokedOnSave() throws IOException {
		AssetService service = injector.getInstance(AssetService.class);
		Asset asset = new Asset(null, null, null);
		ServiceResult<Asset> result = service.save(asset, createInputStream(), Create.class);
		assertNotNull(result);
		assertTrue(result.hasErrors());
		assertEquals(result.getErrors().size(), 2);
		Map<String, String> errorMsgs = mapToMessages(result.getErrors());
		assertEquals(errorMsgs.get("filePath"), ValidationMessageTemplates.NotNull.getLabel());
		assertEquals(errorMsgs.get("meta"), ValidationMessageTemplates.NotNull.getLabel());
		
	}

	@Test
	public void assetIsNotSavedOnValidationError() throws IOException {
		AssetService service = injector.getInstance(AssetService.class);
		Asset asset = new Asset(null, null, null);
		service.save(asset, createInputStream(), Create.class);
		assertNull(asset.getId());
	}
	
	@Test
	public void emptyInputStreamIsNotSaved() throws IOException {
		AssetService service = injector.getInstance(AssetService.class);
		Asset asset = new Asset(null, "a/b/c", new ContentMeta("title", new MimeType("class", "type")));
		ServiceResult<Asset> result = service.save(asset, new PushbackInputStream(new ByteArrayInputStream(new byte[0])), Create.class);
		assertNotNull(result);
		assertTrue(result.hasErrors());
		assertEquals(result.getErrors().get(0).getMessage(), "Asset's input byte stream (file) has length zero. Empty files not allowed.");
	}

	@Test
	public void assetIsSavedWithLatestLastModifiedTimestamp() throws InterruptedException, IOException {
		AssetService service = injector.getInstance(AssetService.class);
		ContentMeta meta = new ContentMeta("title", new MimeType("class", "type"));
		Date oldLastModifiedTimestamp = meta.getLastModifiedTimestamp();
		Thread.sleep(1000);
		Asset asset = new Asset(null, "a/b/c", meta);
		ServiceResult<Asset> result = service.save(asset, createInputStream(), Create.class);
		assertNotNull(result);
		assertFalse(result.hasErrors());
		assertNotNull(result.getValue());
		assertTrue(result.getValue().isPresent());
		assertNotNull(result.getValue().get().getId());

		Asset resultValue = result.getValue().get();
		assertNotEquals(resultValue.getMeta().getLastModifiedTimestamp(), oldLastModifiedTimestamp);
		assertEquals(resultValue.getMeta().getLastModifiedTimestamp().compareTo(oldLastModifiedTimestamp), 1);
	}

	@Test
	public void assetIsSavedWithFilePathBasedContentKey() throws InterruptedException, IOException {
		AssetService service = injector.getInstance(AssetService.class);
		ContentMeta meta = new ContentMeta("title", new MimeType("class", "type"));
		Asset asset = new Asset(null, "a/b/c", meta);
		ServiceResult<Asset> result = service.save(asset, createInputStream(), Create.class);
		assertNotNull(result);
		assertFalse(result.hasErrors());
		assertNotNull(result.getValue());
		assertTrue(result.getValue().isPresent());
		assertNotNull(result.getValue().get().getId());
		
		Asset resultValue = result.getValue().get();
		assertNotEquals(resultValue.getContentKey(), new BlobKey("dummy"));
		BlobstoreService bss = injector.getInstance(BlobstoreService.class);
		final String expectedBlobKeyPath = String.format("/gs/%s/%s", "biswadahal-dot-com.appspot.com", asset.getFilePath());
		BlobKey expectedBlobKey = bss.createGsBlobKey(expectedBlobKeyPath);
		assertEquals(resultValue.getContentKey(), expectedBlobKey);
	}
	
	@Test
	public void contentKeyAndIdCannotBeNullIfNotCreateValidationGroup() throws IOException {
		AssetService service = injector.getInstance(AssetService.class);
		ContentMeta meta = new ContentMeta("title", new MimeType("class", "type"));
		Asset asset = new Asset(null, "a/b/c", meta);
		ServiceResult<Asset> result = service.save(asset, createInputStream(), Update.class);
		assertNotNull(result);
		assertTrue(result.hasErrors());
		assertEquals(result.getErrors().size(), 2);
		Map<String, String> errorMsgs = mapToMessages(result.getErrors());
		assertEquals(errorMsgs.get("id"), ValidationMessageTemplates.NotNull.getLabel());
		assertEquals(errorMsgs.get("contentKey"), ValidationMessageTemplates.NotNull.getLabel());
	}
	
	
	private PushbackInputStream createInputStream(){
		return new PushbackInputStream(new ByteArrayInputStream(new byte[]{32,32,34}));		
	}
	
}
