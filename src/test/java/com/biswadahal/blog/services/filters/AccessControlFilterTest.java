package com.biswadahal.blog.services.filters;

import org.mockito.Mockito;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.biswadahal.AppEngineTests;
import com.biswadahal.blog.dao.OfyService;
import com.biswadahal.blog.models.AccessControl;
import com.biswadahal.blog.models.Page;
import com.biswadahal.blog.services.BlogUser;
import com.biswadahal.blog.services.filters.Filter.FilterKey;
import com.google.appengine.api.users.User;
import com.google.common.base.Optional;
import com.googlecode.objectify.cmd.Query;

public class AccessControlFilterTest extends AppEngineTests {

	@Test(dataProvider = "filterArguments")
	public void queryRestrictionApplication(Query<Page> mockedQuery, Optional<BlogUser> blogUser, boolean isFilterAppliedExpectation){
		AccessControlFilter<Page> filter = new AccessControlFilter<>(mockedQuery, blogUser, "test");
		filter.apply();
		if(isFilterAppliedExpectation){
			Mockito.verify(mockedQuery, Mockito.times(1)).filter(Mockito.eq("test"+FilterKey.META_ACCESS_CONTROL_PERM_OTHER.getPropertyName()), Mockito.eq(AccessControl.Permission.VIEW));
		}else{
			Mockito.verify(mockedQuery, Mockito.times(0)).filter(Mockito.eq("test"+FilterKey.META_ACCESS_CONTROL_PERM_OTHER.getPropertyName()), Mockito.eq(AccessControl.Permission.VIEW));
		}
	}
	
	@DataProvider(name = "filterArguments")
	private Object[][] filterArguments() {
		User googleUser = new User("test", "test");
		return new Object[][]{
			{Mockito.spy(OfyService.ofy().load().type(Page.class).distinct(false)), Optional.absent(), true},
			{Mockito.spy(OfyService.ofy().load().type(Page.class).distinct(false)), Optional.of(new BlogUser(googleUser, false)), true},
			{Mockito.spy(OfyService.ofy().load().type(Page.class).distinct(false)), Optional.of(new BlogUser(googleUser, true)), false}
		};
	}

}
