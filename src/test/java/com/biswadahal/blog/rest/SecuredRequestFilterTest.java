package com.biswadahal.blog.rest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.Principal;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.internal.MapPropertiesDelegate;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.routing.UriRoutingContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.biswadahal.AppEngineTests;
import com.biswadahal.blog.rest.SecuredRequestFilter.Secured;
import com.biswadahal.blog.rest.SecuredRequestFilter.Secured.AuthRole;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Optional;

public class SecuredRequestFilterTest extends AppEngineTests{
	private SecuredRequestFilter filter = null;
	final Object annotatedObj = new Object() {
		@Secured(roles={})
		public void none(){}
		
		@Secured
		public void defaultRole(){}
		
		@Secured(roles=AuthRole.PUBLIC)
		public void all(){}
		
		@Secured(roles={AuthRole.PUBLIC, AuthRole.ADMIN})
		public void all_admin(){}
	};

	
	@BeforeMethod
	public void beforeMethod(){
		super.beforeMethod();
		filter = new SecuredRequestFilter();
	}
	
	@Test
	public void noPrincipalReturnsUnauthorizedStatus() throws IOException{
		ContainerRequestContext crc = buildMockedContainerRequestContext(true, "noUserInRole");
		ArgumentCaptor<Response> responseArg = ArgumentCaptor.forClass(Response.class);
		filter.filter(crc);
		Mockito.verify(crc).abortWith(responseArg.capture());
		Response response = responseArg.getValue();
		assertNotNull(response);
		assertEquals(response.getStatus(), Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void noPrincipalReturnsLoginUrlInPayload() throws IOException{
		ContainerRequestContext crc = buildMockedContainerRequestContext(true, "noUserInRole");
		ArgumentCaptor<Response> responseArg = ArgumentCaptor.forClass(Response.class);
		filter.filter(crc);
		Mockito.verify(crc).abortWith(responseArg.capture());
		Response response = responseArg.getValue();
		assertNotNull(response);
		assertEquals(response.getMediaType(), MediaType.APPLICATION_JSON_TYPE);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String,String> entity = (Map)response.getEntity();
		assertNotNull(entity);
		assertTrue(entity.containsKey("login"));
		UserService userService = UserServiceFactory.getUserService();
		String loginUri = userService.createLoginURL(crc.getUriInfo().getAbsolutePath().toString());
		assertEquals(entity.get("login"), loginUri);
	}
	
	@Test
	public void authorizationFailureReturnsForbiddenStatus() throws IOException, NoSuchMethodException, SecurityException{
		ContainerRequestContext crc = buildMockedContainerRequestContext(false, "admin");
		setResourceInfo(filter, crc, "none");
		filter.filter(crc);
		ArgumentCaptor<Response> responseArg = ArgumentCaptor.forClass(Response.class);
		Mockito.verify(crc).abortWith(responseArg.capture());
		Response response = responseArg.getValue();
		assertNotNull(response);
		assertEquals(response.getStatus(), Status.FORBIDDEN.getStatusCode());
	}
	
	@Test
	public void authorizationCheckUsesORLogic() throws IOException, NoSuchMethodException, SecurityException{
		//"admin" user is in role, on a method allowed for "all" or "admin"
		ContainerRequestContext crc = buildMockedContainerRequestContext(false, "admin");
		setResourceInfo(filter, crc, "all_admin");
		ArgumentCaptor<Response> responseArg = ArgumentCaptor.forClass(Response.class);
		filter.filter(crc);
		Mockito.verify(crc, Mockito.times(0)).abortWith(responseArg.capture());
	}
	
	@Test(dataProvider="authorizationChecksDataSet")
	public void authorizationChecksAreValid(String userInRole, String resourceMethodName, Optional<Status> abortedStatus) throws IOException, NoSuchMethodException, SecurityException{
		ContainerRequestContext crc = buildMockedContainerRequestContext(false, userInRole);
		setResourceInfo(filter, crc, resourceMethodName);
		filter.filter(crc);
		ArgumentCaptor<Response> responseArg = ArgumentCaptor.forClass(Response.class);
		if(abortedStatus.isPresent()){
			Mockito.verify(crc).abortWith(responseArg.capture());
			Response response = responseArg.getValue();
			assertNotNull(response);
			assertEquals(response.getStatus(), abortedStatus.get().getStatusCode());
		}else{
			Mockito.verify(crc, Mockito.times(0)).abortWith(responseArg.capture());
		}
	}
	
	@DataProvider(name="authorizationChecksDataSet")
	private Object[][] authorizationChecksDataSet(){
		return new Object[][]{
			{"admin", "none", Optional.of(Status.FORBIDDEN)},
			{"admin", "defaultRole", Optional.absent()},
			{"admin", "all", Optional.absent()},
			{"admin", "all_admin", Optional.absent()},
			{"*", "none", Optional.of(Status.FORBIDDEN)},
			{"*", "defaultRole", Optional.of(Status.FORBIDDEN)},
			{"*", "all", Optional.absent()},
			{"*", "all_admin", Optional.absent()},
			{"unsupportedRole", "none", Optional.of(Status.FORBIDDEN)},
			{"unsupportedRole", "defaultRole", Optional.of(Status.FORBIDDEN)},
			{"unsupportedRole", "all", Optional.of(Status.FORBIDDEN)},
			{"unsupportedRole", "all_admin", Optional.of(Status.FORBIDDEN)}
		};
	}
	
	private void setResourceInfo(SecuredRequestFilter filter, ContainerRequestContext crc, String annotatedObjectMethodNameForRoleAnnotation) throws NoSuchMethodException, SecurityException{
		ResourceInfo resourceInfo = Mockito.spy(new UriRoutingContext((ContainerRequest)crc));
		//Method.class cannot be mocked
		if(annotatedObjectMethodNameForRoleAnnotation == null){
			annotatedObjectMethodNameForRoleAnnotation = "none";
		}
		Method method = annotatedObj.getClass().getMethod(annotatedObjectMethodNameForRoleAnnotation);
		Mockito.doReturn(method).when(resourceInfo).getResourceMethod();
		setField(filter, "resourceInfo", resourceInfo);
	}
	
	private ContainerRequestContext buildMockedContainerRequestContext(boolean nullPrincipal, final String userInRole){
		Principal principal = null;
		if(!nullPrincipal){
			principal = Mockito.mock(Principal.class);
		}
		SecurityContext sc = buildSecurityContext(userInRole, principal);
		ContainerRequest cr = Mockito.spy(new ContainerRequest(URI.create("/blog/api/"), URI.create("some-resource"), "GET", sc, new MapPropertiesDelegate()));
		return cr;
	}
	
	private SecurityContext buildSecurityContext(final String userInRole, final Principal userPrincipal){
	    SecurityContext sc = new SecurityContext() {
	        @Override
	        public boolean isUserInRole(final String role) {
	            return userInRole.equals(role) || "admin".equals(userInRole);
	        }
	        @Override
	        public boolean isSecure() {
	            return false;
	        }
	        @Override
	        public Principal getUserPrincipal() {
	            return userPrincipal;
	        }
	        @Override
	        public String getAuthenticationScheme() {
	            return null;
	        }
	    };
	    return sc;

	}
	
}
