package com.biswadahal.blog.guice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.glassfish.jersey.servlet.ServletContainer;
import org.hibernate.validator.messageinterpolation.ValueFormatterMessageInterpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biswadahal.blog.dao.OfyService;
import com.biswadahal.blog.guice.AppConfiguration.ConfigKey;
import com.biswadahal.blog.rest.BlogRestAPI;
import com.biswadahal.blog.rest.ConstraintViolationSerializer;
import com.biswadahal.blog.rest.DatastoreTextDeserializer;
import com.biswadahal.blog.rest.DatastoreTextSerializer;
import com.biswadahal.blog.rest.DateTimeISOFormatDeserializer;
import com.biswadahal.blog.rest.DateTimeISOFormatSerializer;
import com.biswadahal.blog.rest.resources.RootResource;
import com.biswadahal.blog.services.PageService;
import com.biswadahal.blog.services.PageTemplateService;
import com.biswadahal.blog.services.TagService;
import com.biswadahal.blog.servlet.RequestInfoServletFilter;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.common.base.Optional;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;

public class BlogServletModule extends ServletModule {
	public static final Logger logger = LoggerFactory.getLogger(BlogServletModule.class);
	private final ValidatorFactory validatorFactory;
	private final AppConfiguration appConfiguration;
	
	public BlogServletModule() {
		super();
		Configuration<?> configuration = Validation.byDefaultProvider().configure();
		validatorFactory = configuration.messageInterpolator(
					new ValueFormatterMessageInterpolator(configuration.getDefaultMessageInterpolator())
				).buildValidatorFactory();
		appConfiguration = new AppConfiguration();
	}

	@Override
	public void configureServlets() {
		super.configureServlets();
		bindAppConfigurationLoader();
		bindServletFilters();
		bindJerseyServlet();
		
		bindObjectMapper();
		bindObjectify();
		bindAppEngineServices();
		bindBizObjects();
		bindJerseyResources();
		bindValidators();
		logger.info(String.format("Guice module configured: %s", this.getClass().getName()));
	}
	
	private void bindAppConfigurationLoader(){
		logger.trace("Bind App Configuration loader");
		bind(AppConfiguration.class).toInstance(appConfiguration);
	}
	
	private void bindServletFilters(){
		logger.trace("Bind servlet filters");
		/**Added for [Appengine Workaround]. 
		 * See {@link RequestInfoServletFilter} comments for details 
		 * */
		bind(RequestInfoServletFilter.class).in(Singleton.class);
		filter(String.format("%s*", appConfiguration.basePath())).through(RequestInfoServletFilter.class);
	}
	
	private void bindJerseyServlet(){
		logger.trace("Bind jersey servlet");
		bind(ServletContainer.class).in(Singleton.class);
		Map<String, String> servletParams = new HashMap<>();
		servletParams.put("javax.ws.rs.Application", BlogRestAPI.class.getName());
		serve(String.format("%s*", appConfiguration.basePath())).with(ServletContainer.class, servletParams);
	}

	private void bindObjectMapper() {
		logger.trace("Bind jackson object mapper");
		ObjectMapper om = new ObjectMapper();
		om.setSerializationInclusion(Include.ALWAYS).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true)
				.configure(SerializationFeature.INDENT_OUTPUT, true)
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
				.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		
        SimpleModule module = new SimpleModule("BlogApiOMapperModule", new Version(1, 0, 0, null, null, null));
        module.addSerializer(new ConstraintViolationSerializer());
        module.addSerializer(new DatastoreTextSerializer());
        module.addDeserializer(Text.class, new DatastoreTextDeserializer());
        module.addSerializer(new DateTimeISOFormatSerializer());
        module.addDeserializer(Date.class, new DateTimeISOFormatDeserializer());
        om.registerModule(module);
        
		bind(ObjectMapper.class).toInstance(om);
		logger.trace(String.format("Bounded guice object mapper: %s", om));
	}

	private void bindObjectify() {
		logger.trace("Bind objectify");
		filter("/*").through(ObjectifyFilter.class);
		bind(ObjectifyFilter.class).in(Singleton.class);
		requestStaticInjection(OfyService.class);
	}

	private void bindBizObjects() {
		logger.trace("Bind models");
		bind(TagService.class);
		bind(PageTemplateService.class);
		bind(PageService.class);
	}

	private void bindJerseyResources() {
		logger.trace("Bind jersey resoures");
		bind(RootResource.class);
	}

	private void bindValidators() {
		logger.trace("Bind validator");
		final Validator validator = validatorFactory.usingContext().getValidator();
		//From the docs: http://hibernate.org/validator/documentation/getting-started/
		//...Validator instances are thread-safe and may be reused multiple times...
		bind(Validator.class).toInstance(validator);
	}
	
	private void bindAppEngineServices() {
		logger.trace("Bind appengine services");
		Optional<Integer> retryDelay= appConfiguration.getIntValue(ConfigKey.GCS_RETRY_DELAY_MILLIS, 5);
		Optional<Integer> retryPeriod= appConfiguration.getIntValue(ConfigKey.GCS_RETRY_PERIOD_MILLIS, 2000);
		Optional<Integer> retryMin= appConfiguration.getIntValue(ConfigKey.GCS_RETRY_MIN_ATTEMPTS, 1);
		Optional<Integer> retryMax= appConfiguration.getIntValue(ConfigKey.GCS_RETRY_MAX_ATTEMPTS, 4);

		GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
			      .initialRetryDelayMillis(retryDelay.get())
			      .totalRetryPeriodMillis(retryPeriod.get())
			      .retryMinAttempts(retryMin.get())
			      .retryMaxAttempts(retryMax.get())
			      .build());
		bind(GcsService.class).toInstance(gcsService);
		bind(BlobstoreService.class).toInstance(BlobstoreServiceFactory.getBlobstoreService());
	}

}
