package com.biswadahal.blog.models.validation;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.channels.Channels;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import com.biswadahal.AppEngineTests;
import com.biswadahal.blog.guice.AppConfiguration;
import com.biswadahal.blog.guice.AppConfiguration.ConfigKey;
import com.google.api.client.util.IOUtils;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

public class GcsFilePathIsUniqueValidatorTest extends AppEngineTests {
	
	@GcsFilePathIsUnique()
	public static void methodToTestAnnotation(){};

	private GcsFilePathIsUniqueValidator createValidator() throws NoSuchMethodException {
		GcsFilePathIsUniqueValidator validator = new GcsFilePathIsUniqueValidator();
		GcsFilePathIsUnique annotation = GcsFilePathIsUniqueValidatorTest.class.getMethod("methodToTestAnnotation").getAnnotation(GcsFilePathIsUnique.class);
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
	
	private void createFile(String filePath) throws IOException {
		AppConfiguration appConfiguration = getInjector().getInstance(AppConfiguration.class);
		GcsService gcsService = GcsServiceFactory.createGcsService();
		final String bucketName = appConfiguration.getStringValue(ConfigKey.GCS_BUCKET_DEFAULT_NAME, null).orNull();
		GcsFilename fileName = new GcsFilename(bucketName, filePath);
		GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
		GcsOutputChannel gcsOutputChannel = gcsService.createOrReplace(fileName, instance);
		IOUtils.copy(new ByteArrayInputStream("test".getBytes()), Channels.newOutputStream(gcsOutputChannel));
		gcsOutputChannel.close();
	}
	
	@Test
	public void isValid() throws NoSuchMethodException, SecurityException{
		GcsFilePathIsUniqueValidator validator = createValidator();
		ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
		configureValidatorContext(context);
		String[] paths = new String[] {null, "", " ", "GcsFilePathIsUniqueValidatorTest/path/string"};
		for (String path: paths){
			boolean isValid = validator.isValid(path, context);
			assertTrue(isValid);
		}
	}
	
	@Test
	public void isInValid() throws NoSuchMethodException, SecurityException, IOException{
		GcsFilePathIsUniqueValidator validator = createValidator();
		ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
		configureValidatorContext(context);
		final String path = "/a/b/c/test";
		createFile(path);
		boolean isValid = validator.isValid(path, context);
		assertFalse(isValid);
	}

}
