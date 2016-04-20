package com.biswadahal.blog.rest.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
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

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.biswadahal.blog.models.Asset;
import com.biswadahal.blog.models.Page;
import com.biswadahal.blog.models.validation.ValidationGroups.Create;
import com.biswadahal.blog.rest.SecuredRequestFilter.Secured;
import com.biswadahal.blog.rest.ResourceEndpoints;
import com.biswadahal.blog.rest.ResourceEndpoints.EndpointKeys;
import com.biswadahal.blog.services.AssetService;
import com.biswadahal.blog.services.BlogUser;
import com.biswadahal.blog.services.ServiceListResult;
import com.biswadahal.blog.services.ServiceResult;
import com.biswadahal.blog.services.filters.AssetFilter;
import com.google.common.base.Optional;
import com.google.inject.servlet.RequestScoped;
import com.googlecode.objectify.Key;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssetsResource {
	public static final Logger logger = LoggerFactory.getLogger(AssetsResource.class);

	@Inject
	private AssetService assetService;

	@Inject
	private AssetResource assetResource;
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Secured
	public Response createAsset(@FormDataParam("asset") FormDataBodyPart bodyPart, @FormDataParam("file") FormDataContentDisposition fileDetails , @FormDataParam("file") InputStream inputStream) {
		bodyPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
	    Asset asset = bodyPart.getEntityAs(Asset.class);
	    
		ServiceResult<Asset> result;
		try {
			result = assetService.save(asset, new PushbackInputStream(inputStream), Create.class);
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
			return Response.status(Status.SERVICE_UNAVAILABLE).build();
		}
		Response response = null;
		if (result.hasErrors()) {
			response = Response.status(Status.BAD_REQUEST).entity(result.getErrors()).build();
		} else {
			Key<Page> assetKey = Key.create(Page.class, asset.getId());
			URI uri = URI.create(String.format(ResourceEndpoints.getById(EndpointKeys.ASSET), assetKey.toWebSafeString()));
			response = Response.created(uri).entity(asset).build();
		}
		return response;
	}
	
	@GET
	@Secured
	public Response getAssets(@QueryParam("pageSize") @DefaultValue("10") int pageSize,
			@QueryParam("offset") @DefaultValue("0") int offset, @Context BlogUser blogUser) {
		ServiceListResult<Asset> result = assetService.filter(pageSize, offset, Optional.<List<AssetFilter>>absent(), Optional.fromNullable(blogUser));
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
	public AssetResource assetResource(){
		return assetResource;
	}
	
}
