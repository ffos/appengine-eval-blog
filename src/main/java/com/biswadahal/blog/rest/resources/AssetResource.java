package com.biswadahal.blog.rest.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import com.biswadahal.blog.models.validation.ValidationGroups.Update;
import com.biswadahal.blog.rest.SecuredRequestFilter.Secured;
import com.biswadahal.blog.services.AssetService;
import com.biswadahal.blog.services.BlogUser;
import com.biswadahal.blog.services.ServiceResult;
import com.google.common.base.Optional;
import com.google.inject.servlet.RequestScoped;
import com.googlecode.objectify.Key;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssetResource {
	public static final Logger logger = LoggerFactory.getLogger(AssetResource.class);

	@Inject
	private AssetService assetService;
	
	@Inject
	private AssetStreamResource assetStreamResource;
	

	@GET
	public Response getAsset(@PathParam("webSafeKey") String webSafeKey, @Context BlogUser blogUser) {
		Key<Asset> key = Key.create(webSafeKey);
		ServiceResult<Asset> result = assetService.findByKey(key, Optional.fromNullable(blogUser));
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
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Secured
	public Response updateAsset(@PathParam("webSafeKey") String webSafeKey, @FormDataParam("asset") FormDataBodyPart bodyPart, @FormDataParam("file") FormDataContentDisposition fileDetails , @FormDataParam("file") InputStream inputStream, @Context BlogUser blogUser) {
		Response assetResponse = getAsset(webSafeKey, blogUser);
		if (assetResponse.getStatus() != Status.OK.getStatusCode()) {
			return assetResponse;
		}
		Asset existingEntity = (Asset) assetResponse.getEntity();
		bodyPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
	    Asset asset = bodyPart.getEntityAs(Asset.class);
	    //copy over content key (because it is not exposed to clients but is a required field for update validation)
	    asset.setContentKey(existingEntity.getContentKey()); 
	    
		ServiceResult<Asset> result;
		try {
			result = assetService.save(asset, new PushbackInputStream(inputStream), Update.class);
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
			return Response.status(Status.SERVICE_UNAVAILABLE).build();
		}
		Response response = null;
		if (result.hasErrors()) {
			response = Response.status(Status.BAD_REQUEST).entity(result.getErrors()).build();
		} else {
			response = Response.ok().entity(result.getValue().or(asset)).build();
		}
		return response;
	}
	
	@Path("/stream")
	public AssetStreamResource getAssetStream() {
		return assetStreamResource;
	}
	
}
