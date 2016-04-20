package com.biswadahal.blog.rest.resources;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.biswadahal.blog.models.PageTemplate;
import com.biswadahal.blog.models.validation.ValidationGroups.Create;
import com.biswadahal.blog.rest.SecuredRequestFilter.Secured;
import com.biswadahal.blog.rest.ResourceEndpoints;
import com.biswadahal.blog.rest.ResourceEndpoints.EndpointKeys;
import com.biswadahal.blog.services.BlogUser;
import com.biswadahal.blog.services.PageTemplateService;
import com.biswadahal.blog.services.ServiceListResult;
import com.biswadahal.blog.services.ServiceResult;
import com.biswadahal.blog.services.filters.PageTemplateFilter;
import com.google.common.base.Optional;
import com.google.inject.servlet.RequestScoped;
import com.googlecode.objectify.Key;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PageTemplatesResource {
	@Inject
	private PageTemplateService pageTemplateService;
	
	@Inject
	private PageTemplateResource pageTemplateResource;

	@POST
	@Secured
	public Response createPageTemplate(PageTemplate pageTemplate) {
		ServiceResult<PageTemplate> result = pageTemplateService.save(pageTemplate, Create.class);
		Response response = null;
		if (result.hasErrors()) {
			response = Response.status(Status.BAD_REQUEST).entity(result.getErrors()).build();
		} else {
			Key<PageTemplate> pageTemplateKey = Key.create(PageTemplate.class, pageTemplate.getId());
			URI uri = URI.create(String.format(ResourceEndpoints.getById(EndpointKeys.PAGE_TEMPLATE), pageTemplateKey.toWebSafeString()));
			response = Response.created(uri).entity(pageTemplate).build();
		}
		return response;
	}

	@GET
	@Secured
	public Response getTemplates(@QueryParam("pageSize") @DefaultValue("10") int pageSize,
			@QueryParam("offset") @DefaultValue("0") int offset, @Context BlogUser blogUser) {
		ServiceListResult<PageTemplate> result = pageTemplateService.filter(pageSize, offset, Optional.<List<PageTemplateFilter>>absent(), Optional.fromNullable(blogUser));
		Response response = null;
		if (result.hasErrors()) {
			response = Response.status(Status.BAD_REQUEST).entity(result.getErrors()).build();
		} else {
			if (result.getValue().isPresent()) {
				response = Response.ok(result.getValue().get()).build();
			} else {
				response = Response.ok(Collections.emptyList()).build();
			}
		}
		return response;
	}
	
	@Path("{webSafeKey:[a-zA-Z0-9]+}")
	public PageTemplateResource pageResource(){
		return pageTemplateResource;
	}
	
}
