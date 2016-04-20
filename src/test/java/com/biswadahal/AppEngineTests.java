package com.biswadahal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolation;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.biswadahal.blog.dao.OfyFactory;
import com.biswadahal.blog.guice.BlogServletModule;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

public class AppEngineTests {

	private final LocalServiceTestHelper aeHelper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
			new LocalMemcacheServiceTestConfig(), new LocalTaskQueueTestConfig());
	
	protected Closeable session;
	protected Injector injector;

	public enum ValidationMessageTemplates {
		NotNull ("{javax.validation.constraints.NotNull.message}");
		private String label;
		private ValidationMessageTemplates(String label){
			this.label = label;
		}
		public String getLabel(){
			return label;
		}
	}
	
	public AppEngineTests(){
		init();
	}
	
	public Injector getInjector() {
		return injector;
	}

	/**
	 * Until bug gets resolved, subclass needs to invoke it manually
	 * To ease the process, calling it in constructor even though 
	 * that's not ideal. But it shouldn't be expensive to startup guice
	 * since there is no scanning involved
	 * See: https://github.com/cbeust/testng/issues/420
	 */
	@BeforeSuite(alwaysRun=true)
	public void init(){
		ServletModule module = new BlogServletModule();
		injector = Guice.createInjector(module);
		OfyFactory factory = new OfyFactory(injector);
		ObjectifyService.setFactory(factory);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void beforeMethod(){
		session = ObjectifyService.begin();
		aeHelper.setUp();
	}
	@AfterMethod(alwaysRun=true)
	public void afterMethod(){
		session.close();
		aeHelper.tearDown();
	}
	
	protected void setField(Object target, String fieldName, Object value){
		try {
			Field f = target.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(target, value);
		} catch (Throwable e) {
			throw new RuntimeException("Could not set field", e);
		}
	}
	
	protected <T> Map<String, String> mapToMessages(Collection<ConstraintViolation<T>> violations) {
		Map<String, String> retVal = new HashMap<>();
		for(ConstraintViolation<?> v: violations) {
			retVal.put(v.getPropertyPath().toString(), v.getMessageTemplate());
		}
		return retVal;
	}
	
}
