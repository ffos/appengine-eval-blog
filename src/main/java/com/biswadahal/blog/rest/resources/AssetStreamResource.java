package com.biswadahal.blog.rest.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
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
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.ByteRange;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssetStreamResource {
	public static final Logger logger = LoggerFactory.getLogger(AssetStreamResource.class);

	@Inject
	private AssetResource assetResource;
	
	@Inject
	private BlobstoreService blobStoreService;

	@Inject
	private AssetService assetService;

	@GET
	@Produces(MediaType.WILDCARD)
	public Response getAssetStream(
			@PathParam("webSafeKey") String webSafeKey, 
			@Context BlogUser blogUser,
			@Context HttpServletRequest httpRequest,
			@Context HttpServletResponse httpResponse
			) {
		Response assetResponse = assetResource.getAsset(webSafeKey, blogUser);
		if (assetResponse.getStatus() != Status.OK.getStatusCode()) {
			return assetResponse;
		}
		Asset asset = (Asset) assetResponse.getEntity();
		String outgoingMime = asset.getMeta().getMimeType().getFullMimeType();
		ByteRange requestedByteRange = blobStoreService.getByteRange(httpRequest);
		ResponseBuilder rb = Response.ok().header(HttpHeaders.CONTENT_TYPE, outgoingMime);
		try {
			blobStoreService.serve(asset.getContentKey(), requestedByteRange, httpResponse);
		} catch (IOException e) {
			logger.error(String.format("Error streaming bytes for asset: %s, byteRange: %s", asset.getId(), requestedByteRange), e);
			return Response.status(Status.SERVICE_UNAVAILABLE).build();
		}
		return rb.build();
	}
	
	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Secured
	public Response updateAsset(@PathParam("webSafeKey") String webSafeKey, @FormDataParam("file") FormDataContentDisposition fileDetails , @FormDataParam("file") InputStream inputStream, @Context BlogUser blogUser) {
		Response assetResponse = assetResource.getAsset(webSafeKey, blogUser);
		if (assetResponse.getStatus() != Status.OK.getStatusCode()) {
			return assetResponse;
		}
		Asset asset = (Asset) assetResponse.getEntity();
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
			response = Response.noContent().build();
		}
		return response;
	}
	
	
}
