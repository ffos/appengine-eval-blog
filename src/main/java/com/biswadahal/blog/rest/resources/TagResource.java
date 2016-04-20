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

import com.biswadahal.blog.models.Tag;
import com.biswadahal.blog.models.validation.ValidationGroups.Update;
import com.biswadahal.blog.rest.SecuredRequestFilter.Secured;
import com.biswadahal.blog.services.BlogUser;
import com.biswadahal.blog.services.ServiceResult;
import com.biswadahal.blog.services.TagService;
import com.google.common.base.Optional;
import com.google.inject.servlet.RequestScoped;
import com.googlecode.objectify.Key;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TagResource {
	@Inject
	private TagService tagService;

	@GET
	public Response getTag(@PathParam("webSafeKey") String webSafeKey, @Context BlogUser blogUser){
		Key<Tag> key = Key.create(webSafeKey);
		ServiceResult<Tag> result = tagService.findByKey(key, Optional.fromNullable(blogUser));
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
	public Response updateTemplate(@PathParam("webSafeKey") String webSafeKey, Tag tag, @Context BlogUser blogUser) {
		ServiceResult<Tag> result = tagService.save(tag, Update.class);
		Response response = null;
		if (result.hasErrors()) {
			response = Response.status(Status.BAD_REQUEST).entity(result.getErrors()).build();
		} else {
			response = Response.ok().entity(result.getValue().or(tag)).build();
		}
		return response;
	}

}
