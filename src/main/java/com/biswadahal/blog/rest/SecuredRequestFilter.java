package com.biswadahal.blog.rest;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.NameBinding;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biswadahal.blog.rest.SecuredRequestFilter.Secured.AuthRole;
import com.biswadahal.blog.servlet.RequestInfoServletFilter;
import com.biswadahal.blog.servlet.RequestInfoServletFilter.RequestInfo;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Optional;

/**
 * Filter that gets applied with {@linkplain Secured} annotation is used
 */
@SecuredRequestFilter.Secured
public class SecuredRequestFilter implements ContainerRequestFilter{
	public static final Logger logger = LoggerFactory.getLogger(BlogRestAPI.class);

	@Inject
	private ResourceInfo resourceInfo;
	
	//Be warned:
	//Injection of servlet request as shown above has issues with app-engine as described in:
	//http://stackoverflow.com/questions/31354363/google-appengine-with-jersey-2-1x-works-fine-in-dev-server-but-not-in-appengine?lq=1
	//Therefore, this filter uses RequestInfoServletFilter to set attributes, accessed through requestContext.getProperty(name)
	//@Inject
	//private HttpServletRequest servletRequest;
	
	@NameBinding
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.TYPE})
	public @interface Secured{
		public enum AuthRole{
			PUBLIC("*"),
			ADMIN("admin");
			public final String role;
			private AuthRole(String r){
				this.role = r;
			}
		}
		AuthRole[] roles() default {AuthRole.ADMIN};
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Principal principal = requestContext.getSecurityContext().getUserPrincipal();
		if(principal == null){
			Map<String,String> message = new HashMap<>();
			UserService userService = UserServiceFactory.getUserService();
			String loginUri = userService.createLoginURL(requestContext.getUriInfo().getAbsolutePath().toString());
			message.put("login", String.format("%s", loginUri));
			requestContext.abortWith(Response.status(Status.UNAUTHORIZED).entity(message).type(MediaType.APPLICATION_JSON).build());
			logger.trace("Secured resource access attempted: {0}", remoteCallerDetails(requestContext));
		}else{
			Secured annotation = resourceInfo.getResourceMethod().getAnnotation(Secured.class);
			AuthRole[] roles = annotation.roles();
			boolean accessAllowed = false;
			for(AuthRole r: roles){
				accessAllowed = accessAllowed || canAccess(r, requestContext);
			}
			if(!accessAllowed){
				logger.warn("Access Denied. Secured resource access limited to roles:{} attempted. Request details: {}", Arrays.asList(roles), remoteCallerDetails(requestContext));
				requestContext.abortWith(Response.status(Status.FORBIDDEN).entity("").type(MediaType.APPLICATION_JSON).build());
			}
		}
	}
	
	private String remoteCallerDetails(ContainerRequestContext requestContext){
		Optional<RequestInfo> remoteInfo = Optional.fromNullable((RequestInfo)requestContext.getProperty(RequestInfoServletFilter.REQUEST_INFO_REQUEST_ATTRIBUTE_NAME));
		if(remoteInfo != null && remoteInfo.isPresent()){
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("[IP: %s]", remoteInfo.get().getRemoteAddress()));
			sb.append(String.format("[Port: %s]", remoteInfo.get().getRemotePort()));
			sb.append(String.format("[%s@%s]", remoteInfo.get().getRemoteUser(), remoteInfo.get().getRemoteHost()));
			sb.append(String.format("[%s %s]", remoteInfo.get().getRequestMethod(), remoteInfo.get().getRequestUrl()));
			return sb.toString();
		}else{
			logger.warn("Servlet attribute: %s was not accessible. Is the configuration correct?", RequestInfoServletFilter.REQUEST_INFO_REQUEST_ATTRIBUTE_NAME);
			return "Not Available";
		}
	}
	
	private boolean canAccess(AuthRole currentRole, ContainerRequestContext requestContext){
		if(!requestContext.getSecurityContext().isUserInRole(currentRole.role)) {
			return false;
		}else{
			return true;
		}
	}
	
}
