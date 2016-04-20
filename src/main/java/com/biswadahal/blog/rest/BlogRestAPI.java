package com.biswadahal.blog.rest;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biswadahal.blog.guice.AppConfiguration;
import com.biswadahal.blog.rest.resources.RootResource;
import com.biswadahal.blog.services.BlogUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.github.mustachejava.DefaultMustacheFactory;
import com.google.inject.Injector;

public class BlogRestAPI extends ResourceConfig {
	public static final Logger logger = LoggerFactory.getLogger(BlogRestAPI.class);
	
	public static AppConfiguration appConfiguration;
	
	@Inject
	public BlogRestAPI(ServiceLocator serviceLocator, ServletContext servletContext) {
		logger.info("Initializing Rest API Application");
		Injector guiceInjector = (Injector) servletContext.getAttribute(Injector.class.getName());
        if (guiceInjector == null) {
        	logger.error("Guice Injector was not found in servlet context when trying to bridge guice into HK2");
        	throw new RuntimeException("Config Exception - Guice Injector not found");
        }
		
		bridgeGuiceIntoHK2(serviceLocator, guiceInjector);
		setAppConfiguration(guiceInjector.getInstance(AppConfiguration.class));
		
		setServerProperties();
		registerObjectMapper(guiceInjector.getInstance(ObjectMapper.class));
		registerExceptionMapper();
		registerCustomInjectors();
		registerRequestFilters();
		registerFeatures();
		addResources();
	}
	
	//Getting around having to inject in utility classes (not managed by guice, or transitively instantiated by other libraries)
	private static void setAppConfiguration(AppConfiguration appConfiguration){
		logger.trace(String.format("Using guice injected instance to bind to static Oapp appConfiguration: %s", appConfiguration));
		BlogRestAPI.appConfiguration = appConfiguration;
	}
	
	private void setServerProperties() {
		property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");
		
		//[Appengine Workaround] Issue: This forces the access as classpath resources, instead of file-system access 
		property(MustacheMvcFeature.TEMPLATE_OBJECT_FACTORY, new DefaultMustacheFactory("/"));
		property(MustacheMvcFeature.CACHE_TEMPLATES, "true");
	}

	private void bridgeGuiceIntoHK2(ServiceLocator serviceLocator, Injector guiceInjector) {
		logger.trace(String.format("Briding guice into hk2 using servicelocator %s, and guiceInjector: %s", serviceLocator, guiceInjector));
		GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(guiceInjector);
	}
	
	private void registerObjectMapper(ObjectMapper om){
		logger.trace(String.format("Configuring RestAPI object mapper: %s", om));
		JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider();
		jsonProvider.setMapper(om);
		register(jsonProvider);
	}
	
	private void registerExceptionMapper(){
		logger.trace("Configuring RestAPI exception mapper");
		register(new JsonParseExceptionMapper());
	}
	
	private void registerRequestFilters(){
		logger.trace("Configuring RestAPI request filters");
		register(SecuredRequestFilter.class);
	}
	
	private void registerFeatures(){
		logger.trace("Configuring RestAPI Features");
		register(MultiPartFeature.class);
		//register(FreemarkerMvcFeature.class);
		register(MustacheMvcFeature.class);
	}
	
	private void registerCustomInjectors(){
		logger.trace("Configuring RestAPI Custom Injectors");
		register(new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(BlogUserInjector.class).to(BlogUser.class).proxy(false).proxyForSameScope(false).in(RequestScoped.class);
			}
		});
	}
	
	private void addResources() {
		register(RootResource.class);
	}
}
