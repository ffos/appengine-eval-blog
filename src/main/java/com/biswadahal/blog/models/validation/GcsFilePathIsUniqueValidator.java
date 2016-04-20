package com.biswadahal.blog.models.validation;

import java.io.IOException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biswadahal.blog.guice.AppConfiguration;
import com.biswadahal.blog.guice.AppConfiguration.ConfigKey;
import com.biswadahal.blog.guice.BlogServletModule;
import com.google.appengine.tools.cloudstorage.GcsFileMetadata;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class GcsFilePathIsUniqueValidator implements ConstraintValidator<GcsFilePathIsUnique, String> {
	public static final Logger logger = LoggerFactory.getLogger(GcsFilePathIsUniqueValidator.class);
	private static Injector injector = Guice.createInjector(new BlogServletModule());

	
	@Override
	public void initialize(GcsFilePathIsUnique annotation) {
	}

	@Override
	public boolean isValid(String filePath, ConstraintValidatorContext context) {
		if (StringUtils.isNotBlank(filePath)) {
			return filePathIsUnique(filePath);
		} else {
			return true; //true because blank-checks should be done with other annotations
		}
	}
	
	private boolean filePathIsUnique(final String filePath){
		GcsService gcsService = injector.getInstance(GcsService.class);
		AppConfiguration appConfiguration = injector.getInstance(AppConfiguration.class);
		logger.trace(String.format("GCSService instance in validator: %s", gcsService));
		logger.trace(String.format("AppConfiguration instance in validator:: %s", appConfiguration));
		boolean retVal = false;
		try{
			GcsFilename fileName = new GcsFilename(appConfiguration.getStringValue(ConfigKey.GCS_BUCKET_DEFAULT_NAME, null).orNull(), filePath);
			GcsFileMetadata metaData = gcsService.getMetadata(fileName);
			if(metaData == null){
				retVal = true;
			}else{
				retVal = false;
			}
		} catch (IOException e){
			logger.error("IO Exception encountered in validator: ", e);
			retVal = false;
		}
		return retVal;
	}
}
