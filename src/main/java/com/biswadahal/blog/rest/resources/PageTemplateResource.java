package com.biswadahal.blog.rest.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.biswadahal.blog.models.PageTemplate;
import com.biswadahal.blog.models.validation.ValidationGroups.Update;
import com.biswadahal.blog.rest.SecuredRequestFilter.Secured;
import com.biswadahal.blog.services.BlogUser;
import com.biswadahal.blog.services.PageTemplateService;
import com.biswadahal.blog.services.ServiceResult;
import com.google.common.base.Optional;
import com.google.inject.servlet.RequestScoped;
import com.googlecode.objectify.Key;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PageTemplateResource {
	@Inject
	private PageTemplateService pageTemplateService;

	@GET
	@Secured
	public Response getTemplate(@PathParam("webSafeKey") String webSafeKey, @Context BlogUser blogUser) {
		Key<PageTemplate> key = Key.create(webSafeKey);
		ServiceResult<PageTemplate> result = pageTemplateService.findByKey(key, Optional.fromNullable(blogUser));
		Response response = null;
		if(result.hasErrors()){
			response = Response.status(Status.BAD_REQUEST).entity(result.getErrors()).build();
		}else{
			if(result.getValue().isPresent()){
				response = Response.ok(result.getValue().get()).build();
			}else{
				response = Response.status(Status.NOT_FOUND).build();
			}
		}
		return response;
	}

	@PUT
	@Secured
	public Response updateTemplate(@PathParam("webSafeKey") String webSafeKey, PageTemplate pageTemplate, @Context BlogUser blogUser) {
		ServiceResult<PageTemplate> result = pageTemplateService.save(pageTemplate, Update.class);
		Response response = null;
		if (result.hasErrors()) {
			response = Response.status(Status.BAD_REQUEST).entity(result.getErrors()).build();
		} else {
			response = Response.ok().entity(result.getValue().or(pageTemplate)).build();
		}
		return response;
	}
}
