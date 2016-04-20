package com.biswadahal.blog.services.filters;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.validation.ConstraintViolation;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.biswadahal.AppEngineTests;
import com.biswadahal.blog.models.Tag;
import com.googlecode.objectify.Key;

public class FilterTest extends AppEngineTests {

	@Test(dataProvider="invalidFilters")
	public void errorCases(TestFilter f, List<String> errorMsg){
		assertTrue(f.hasErrors());
		assertEquals(f.getErrors().size(), errorMsg.size());
		List<String> allActualErrors = new ArrayList<>();
		List<String> containedErrors = new ArrayList<>();
		for(ConstraintViolation<String> v: f.getErrors()){
			String vMsg = v.getMessage();
			allActualErrors.add(vMsg);
			for(String m: errorMsg){
				if(vMsg.contains(m)){
					containedErrors.add(m);
				}
			}
		}
		List<String> remaining = new ArrayList<>(errorMsg);
		remaining.removeAll(containedErrors);
		assertTrue(remaining.isEmpty(), String.format("The following errors were expected but not found: %s. Actual found: %s", remaining, allActualErrors));
	}
	
	@Test
	public void tagWebSafeKeysAreConvertedToKeyObjects(){
		final Long id = 123L;
		final Key<Tag> key = Key.create(Tag.class, id);
		final String wsKey = key.toWebSafeString();
		TestFilter f = new TestFilter("tags", "invalidOp", Arrays.asList(wsKey));
		Object val = f.getValue();
		assertNotNull(val);
		assertTrue(Collection.class.isAssignableFrom(val.getClass()));
		@SuppressWarnings("unchecked")
		List<Key<Tag>> keyList = (List<Key<Tag>>)val;
		assertEquals(keyList.size(), 1);
		assertEquals(Long.valueOf(keyList.get(0).getId()), id);
	}
	
	@Test
	public void conditionStringCorrectness(){
		TestFilter f = new TestFilter("pref..", "caption", "=", "test");
		assertEquals(f.getCondition(), "pref..caption =");
	}
	
	@DataProvider(name="invalidFilters")
	public Object[][] invalidFilters(){
		return new Object[][]{
			{
				new TestFilter("invalidKey", "invalidOp", null),
				Arrays.asList("Filter key should be one of:", "Filter operator should be one of: ")
			},
			{
				new TestFilter("tags", "invalidOp", Arrays.asList(null, "a")),
				Arrays.asList("Filter operator should be one of:", "Tag key cannot be null in filter", "Invalid Tag key:")
			},
			{
				new TestFilter("tags", "in", "notAList"),
				Arrays.asList("Tags to filter with should be array")
			}
		};
	}
	
	class TestFilter extends Filter<String> {

		protected TestFilter(String key, String op, Object value) {
			this("", key, op, value);
		}

		protected TestFilter(String prefix, String key, String op, Object value) {
			super(String.class, prefix, key, op, value);
		}
		
		@Override
		protected void configureAllowedKeys() {
			allowedFilterKeys.clear();
			allowedFilterKeys.put("caption", FilterKey.META_CAPTION);
			allowedFilterKeys.put("tags", FilterKey.META_TAGS);
		}
	}
}
