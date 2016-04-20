package com.biswadahal.blog.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.validation.ConstraintViolation;

import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ViolationBuilderTest {
	
	@Test(dataProvider="violationInstances")
	public void wrapInCollection(IllegalArgumentViolation<String>[] input, List<Object> expectedOutput){
		Collection<ConstraintViolation<String>> output = ViolationBuilder.wrapInCollection(input);
		assertNotNull(output);
		assertEquals(output.size(), expectedOutput.size());
		for(ConstraintViolation<String> o: output){
			assertTrue(expectedOutput.contains(o));
		}
		
	}
	
	@DataProvider(name="violationInstances")
	private Object[][] getViolationInstances(){
		IllegalArgumentViolation<String> v1 = new IllegalArgumentViolation<>("v1", String.class);
		IllegalArgumentViolation<String> v2 = new IllegalArgumentViolation<>("v2", String.class);
		return new Object[][]{
			{null, Collections.emptyList()},
			{new IllegalArgumentViolation[]{null, null}, Collections.emptyList()},
			{new IllegalArgumentViolation[]{v1,v2}, Arrays.asList(v1, v2)},
			{new IllegalArgumentViolation[]{v1,null, v2}, Arrays.asList(v1, v2) },
		};
	}
}
