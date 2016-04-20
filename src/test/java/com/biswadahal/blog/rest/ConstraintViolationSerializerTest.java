package com.biswadahal.blog.rest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.IOException;

import org.hibernate.validator.internal.engine.PathImpl;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.biswadahal.AppEngineTests;
import com.biswadahal.ConstraintViolationTestImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConstraintViolationSerializerTest extends AppEngineTests{
	
	@Test(dataProvider = "violations")
	public void serializationTest(ConstraintViolationTestImpl<String> input) throws IOException{
		ObjectMapper om = injector.getInstance(ObjectMapper.class);
		String json= om.writeValueAsString(input);
		assertNotNull(json);
		JsonNode jn = om.readTree(json);
		if(input == null){
			assertEquals(json, "null");
			assertNull(jn.get("message"));
			assertNull(jn.get("property"));
		}else{
			assertEquals(jn.get("message").asText(), input.getMessage());
			if(input.getPropertyPath() == null){
				assertNotNull(jn.get("property"));
				assertEquals(jn.get("property").asText(), "null");
			}else{
				assertEquals(jn.get("property").asText(), input.getPropertyPath().toString());
			}
		}
		assertNull(jn.get("messageTemplate"));
		assertNull(jn.get("rootBean"));
		assertNull(jn.get("rootBeanClass"));
		assertNull(jn.get("leafBean"));
		assertNull(jn.get("invalidValue"));
		assertNull(jn.get("constraintDescriptor"));
		assertNull(jn.get("executableReturnValue"));
		assertNull(jn.get("executableParameters"));

	}
	
	@DataProvider(name="violations")
	private Object[][] violationTestCases(){
		return new Object[][]{
			{null},
			{new ConstraintViolationTestImpl<>("msg", "message")},
			{new ConstraintViolationTestImpl<>("msg", "message", PathImpl.createPathFromString("a.b"))}
		};
	}
}
