package com.biswadahal.blog.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintViolation;

import org.testng.annotations.Test;

import com.biswadahal.AppEngineTests;
import com.biswadahal.blog.models.Tag;
import com.biswadahal.blog.services.filters.Filter;
import com.google.common.base.Optional;
import com.googlecode.objectify.cmd.Query;

public class FilteredQueryBuilderTest extends AppEngineTests{

	@Test
	public void aggregatesErrors(){
		TestFilter f = new TestFilter("invalidKey", "=", null);
		FilteredQueryBuilder<Tag> fqb = new FilteredQueryBuilder<Tag>(Tag.class);
		Optional<Query<Tag>> q = fqb.buildFilterQuery(Arrays.asList(f), -1, -1);
		assertFalse(q.isPresent());
		assertEquals(fqb.getViolations().size(), 3);
		
		List<String> allActualErrors = new ArrayList<>();
		List<String> containedErrors = new ArrayList<>();
		List<String> expectedErrors = Arrays.asList(
				"Filter key should be one of:",
				"Invalid page offset (zero based):",
				"Invalid page size:"
				);
		for(ConstraintViolation<Tag> v: fqb.getViolations()){
			String msg = v.getMessage();
			allActualErrors.add(msg);
			for(String m: expectedErrors){
				if(msg.contains(m)){
					containedErrors.add(m);
				}
			}
		}
		List<String> remaining = new ArrayList<>(expectedErrors);
		remaining.removeAll(containedErrors);
		assertTrue(remaining.isEmpty(), String.format("The following errors were expected but not found: %s. Actual found: %s", remaining, allActualErrors));
	}
	
	class TestFilter extends Filter<Tag> {

		protected TestFilter(String key, String op, Object value) {
			super(Tag.class, "", key, op, value);
		}
		
		@Override
		protected void configureAllowedKeys() {
			allowedFilterKeys.clear();
			allowedFilterKeys.put("caption", FilterKey.META_CAPTION);
			allowedFilterKeys.put("tags", FilterKey.META_TAGS);
		}
	}
	
}
