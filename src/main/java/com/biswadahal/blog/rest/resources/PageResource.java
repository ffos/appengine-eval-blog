package com.biswadahal.blog.rest.resources;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

import org.glassfish.jersey.server.mvc.Viewable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biswadahal.blog.models.Page;
import com.biswadahal.blog.models.PageTemplate;
import com.biswadahal.blog.models.validation.ValidationGroups.Update;
import com.biswadahal.blog.rest.SecuredRequestFilter.Secured;
import com.biswadahal.blog.services.BlogUser;
import com.biswadahal.blog.services.PageService;
import com.biswadahal.blog.services.PageTemplateService;
import com.biswadahal.blog.services.ServiceResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.inject.servlet.RequestScoped;
import com.googlecode.objectify.Key;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PageResource {
	public static final Logger logger = LoggerFactory.getLogger(PageResource.class);

	@Inject
	private PageService pageService;
	
	@Inject
	private PageTemplateService pageTemplateService;
		
	@Inject
	private ObjectMapper objMapper;
	
	@GET
	public Response getPage(@PathParam("webSafeKey") String webSafeKey, @Context BlogUser blogUser){
		Key<Page> key = Key.create(webSafeKey);
		ServiceResult<Page> result = pageService.findByKey(key, Optional.fromNullable(blogUser));
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

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getPageHtml(@PathParam("webSafeKey") String webSafeKey, @Context BlogUser blogUser) {
		Response response = getPage(webSafeKey, blogUser);
		if(response.getStatus() == Status.OK.getStatusCode()){
			Page page = (Page)response.getEntity();
			Map<String, Object> injectedObjects = new HashMap<>();
			injectedObjects.put("page", page);
			//fetch page template if exists (for user)
			ServiceResult<PageTemplate> templateResult = pageTemplateService.findByKey(page.getTemplate(), Optional.fromNullable(blogUser));
			if(templateResult.getValue().isPresent()){
				injectedObjects.put("pageTemplate", templateResult.getValue().get());
			}
			Optional<Map<String,Object>> jsonTranslatedMap = convertMapToJsonToMap(injectedObjects);
			if(jsonTranslatedMap.isPresent()){
				response = Response.ok(new Viewable("/mustache/page.mustache", jsonTranslatedMap.get())).build();
			}else{
				response = Response.serverError().build();
			}
		}
		return response;
	}
	
	@PUT
	@Secured
	public Response updatePage(@PathParam("webSafeKey") String webSafeKey, Page page) {
		ServiceResult<Page> result = pageService.save(page, Update.class);
		Response response = null;
		if (result.hasErrors()) {
			response = Response.status(Status.BAD_REQUEST).entity(result.getErrors()).build();
		} else {
			response = Response.ok().entity(result.getValue().or(page)).build();
		}
		return response;
	}
	
	
	/**
	 * Piggybacking on existing serializers on correct format and URL transformations
	 */
	private Optional<Map<String,Object>> convertMapToJsonToMap(Map<String,Object> map){
		Optional<Map<String,Object>> retVal = Optional.absent();
		try{
			String json = objMapper.writeValueAsString(map);
			Map<String,Object> m = objMapper.readValue(json, new TypeReference<Map<String,Object>>(){});
			retVal = Optional.of(m);
		}catch(IOException e){
			logger.error(String.format("Error in conversion. Input map = %s", map), e);
		}
		return retVal;
	}
	
}
