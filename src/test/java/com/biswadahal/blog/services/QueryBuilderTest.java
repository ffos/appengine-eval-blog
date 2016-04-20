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
import com.google.common.base.Optional;
import com.googlecode.objectify.cmd.Query;

public class QueryBuilderTest extends AppEngineTests{

	@Test
	public void aggregatesErrors(){
		QueryBuilder<Tag> qb = new QueryBuilder<Tag>(Tag.class);
		Optional<Query<Tag>> q = qb.buildQuery(-1, -1);
		assertFalse(q.isPresent());
		assertEquals(qb.getViolations().size(), 2);
		
		List<String> allActualErrors = new ArrayList<>();
		List<String> containedErrors = new ArrayList<>();
		List<String> expectedErrors = Arrays.asList(
				"Invalid page offset (zero based):",
				"Invalid page size:"
				);
		for(ConstraintViolation<Tag> v: qb.getViolations()){
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
	
}
