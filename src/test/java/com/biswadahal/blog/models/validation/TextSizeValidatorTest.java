package com.biswadahal.blog.models.validation;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import javax.validation.Validator;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.biswadahal.AppEngineTests;
import com.google.appengine.api.datastore.Text;

public class TextSizeValidatorTest extends AppEngineTests {
	
	@Test(dataProvider="cases")
	public void correctness(TestSizeCase testCase, boolean errorExpected){
		Validator v = this.injector.getInstance(Validator.class);
		Set<?> violations = v.validate(testCase);
		if(errorExpected){
			assertFalse(violations.isEmpty());
		}else{
			assertTrue(violations.isEmpty());
		}
	}
	
	@DataProvider(name="cases")
	private Object[][] cases(){
		return new Object[][]{
			{new TestSizeCase(null, null, null, null), false},
			{new TestSizeCase(new Text("morethan3"), null, null, null), false},
			{new TestSizeCase(null, new Text("1"), null, null), true},
			{new TestSizeCase(null, new Text("123"), null, null), false},
			{new TestSizeCase(null, new Text("1234"), null, null), false},
			{new TestSizeCase(null, null, new Text("1234"), null), true},
			{new TestSizeCase(null, null, new Text("123"), null), false},
			{new TestSizeCase(null, null, new Text("1"), null), false},
			{new TestSizeCase(null, null, null, new Text("1234")), true},
			{new TestSizeCase(null, null, null, new Text("12")), false},
			{new TestSizeCase(null, null, null, new Text("123")), false},
		};
		
	}
	
	class TestSizeCase{
		@TextSize
		Text anySize;
		
		@TextSize(min=3)
		Text minSized; 
		
		@TextSize(max=3)
		Text maxSized;
		
		@TextSize(min=2, max=3)
		Text bothMinMaxSized;

		public TestSizeCase(Text anySize, Text minSized, Text maxSized, Text bothMinMaxSized) {
			super();
			this.anySize = anySize;
			this.minSized = minSized;
			this.maxSized = maxSized;
			this.bothMinMaxSized = bothMinMaxSized;
		}
		
		
	}
}
