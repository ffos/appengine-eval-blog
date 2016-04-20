package com.biswadahal.blog.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.lang3.BooleanUtils;

import com.biswadahal.blog.dao.OfyService;
import com.biswadahal.blog.guice.AppConfiguration;
import com.biswadahal.blog.guice.AppConfiguration.ConfigKey;
import com.biswadahal.blog.models.Asset;
import com.biswadahal.blog.services.filters.AccessControlFilter;
import com.biswadahal.blog.services.filters.AssetFilter;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

public class AssetService {
	@Inject
	private Validator validator;

	@Inject
	private GcsService gcsService;

	@Inject
	private BlobstoreService blobStoreService;
	
	@Inject
	private AppConfiguration appConfiguration;

	public ServiceResult<Asset> save(Asset asset, PushbackInputStream inputStream, Class<?>... validationGroups) throws IOException {
		ValidationGroupsUtil.checkArguments(validationGroups);
		if (asset == null || inputStream == null) {
			Collection<ConstraintViolation<Asset>> noInputViolations = new ArrayList<>();
			if (asset == null) {
				noInputViolations.add(new IllegalArgumentViolation<>("Asset to save is missing", Asset.class));
			}
			if (inputStream == null) {
				noInputViolations.add(new IllegalArgumentViolation<>("Asset's input byte stream (file) to save is missing", Asset.class));
			}
			return new ServiceResult<Asset>(null, noInputViolations);
		}
		if (inputStreamIsZeroBytes(inputStream)) {
			Collection<ConstraintViolation<Asset>> noInputViolation = ViolationBuilder.wrapInCollection(
					new IllegalArgumentViolation<>("Asset's input byte stream (file) has length zero. Empty files not allowed.", Asset.class));
			return new ServiceResult<Asset>(null, noInputViolation);
		}
		
		ServiceResult<Asset> result = null;
		if (asset.getMeta() != null) {
			asset.getMeta().updateLastModifiedTimestampToCurrentTime();
		}

		//modifying "filepath" creates new entites in GCS
		//Therefore, during updates of an existing ID, filepath should be immutable
		if (filePathMutatedOnUpdate(asset)) {
			Collection<ConstraintViolation<Asset>> noInputViolation = ViolationBuilder.wrapInCollection(
					new IllegalArgumentViolation<>("Asset's filePath is immutable on update", Asset.class));
			return new ServiceResult<Asset>(null, noInputViolation);
		}

		Set<ConstraintViolation<Asset>> violations = validator.validate(asset, validationGroups);
		if (!violations.isEmpty()) {
			result = new ServiceResult<Asset>(null, violations);
		} else {
			final String bucketName = appConfiguration.getStringValue(ConfigKey.GCS_BUCKET_DEFAULT_NAME, null).orNull();
			GcsFilename fileName = new GcsFilename(bucketName, asset.getFilePath());
			GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
			GcsOutputChannel gcsOutputChannel = gcsService.createOrReplace(fileName, instance);
			copy(inputStream, Channels.newOutputStream(gcsOutputChannel));
			final String blobKeyPath = String.format("/gs/%s/%s", fileName.getBucketName(), fileName.getObjectName());
			BlobKey blobKey = blobStoreService.createGsBlobKey(blobKeyPath);
			asset.setContentKey(blobKey);
			OfyService.ofy().save().entities(asset).now();
			result = new ServiceResult<Asset>(asset);
			// FIXME: Figure out what exception GCS throws when content length >
			// 10 MB (max allowed by GCS)
		}

		return result;
	}
	
	public ServiceResult<Asset> findByKey(Key<Asset> key, Optional<BlogUser> blogUser ){
		if (key == null) {
			Collection<ConstraintViolation<Asset>> noInputViolation = ViolationBuilder.wrapInCollection(
					new IllegalArgumentViolation<>("Asset key to get is missing", Asset.class));
			return new ServiceResult<Asset>(null, noInputViolation);
		}
		Query<Asset> query = OfyService.ofy().load().type(Asset.class).filterKey(key);
		AccessControlFilter<Asset> accessControlFilter = new AccessControlFilter<>(query, blogUser, "meta.");
		query = accessControlFilter.apply();
		ServiceResult<Asset> result = new ServiceResult<Asset>(query.first().now());
		return result;
	}
	
	
	
	public ServiceListResult<Asset> filter(int pageSize, int zeroBasedPageOffset, Optional<List<AssetFilter>> filters, Optional<BlogUser> blogUser) {
		Optional<Query<Asset>> optQuery = null; 
		Collection<ConstraintViolation<Asset>> violations = Collections.emptyList();
		if (filters.isPresent() && !filters.get().isEmpty()){
			FilteredQueryBuilder<Asset> queryBuilder = new FilteredQueryBuilder<>(Asset.class);
			optQuery = queryBuilder.buildFilterQuery(filters.get(), pageSize, zeroBasedPageOffset);
			violations = queryBuilder.getViolations();
		}else{
			QueryBuilder<Asset> queryBuilder = new QueryBuilder<>(Asset.class);
			optQuery = queryBuilder.buildQuery(pageSize, zeroBasedPageOffset);
			violations = queryBuilder.getViolations();
		}
		if(!violations.isEmpty()){
			return new ServiceListResult<Asset>(null, violations);
		}else{
			Query<Asset> query = optQuery.get();
			AccessControlFilter<Asset> accessControlFilter = new AccessControlFilter<>(query, blogUser, "meta.");
			query = accessControlFilter.apply();
			query.order("-lastModifiedTimestamp");
			ServiceListResult<Asset> result = new ServiceListResult<Asset>(query.list());
			return result;
		}
	}
	
	private boolean filePathMutatedOnUpdate(Asset asset){
		boolean retVal = false;
		if (asset.getKey() != null){
			Asset existingAsset = OfyService.ofy().load().type(Asset.class).filterKey(asset.getKey()).first().now();
			if (existingAsset != null) {
				//BooleanUtils.negate(x.func()) more readable and less buggy than !x.func()
				retVal = BooleanUtils.negate(Objects.equals(asset.getFilePath(), existingAsset.getFilePath()));
			}
		}
		return retVal;
	}
	
	private boolean inputStreamIsZeroBytes(PushbackInputStream input) throws IOException {
		byte[] oneByte = new byte[1];
		int numBytesRead = -1;
		try{
			numBytesRead = input.read(oneByte);
		}catch(IOException e){
			if(!e.getMessage().contains("Stream closed")) {
				throw e;
			}else{
				//Short circuit
				return true;
			}
		}
		boolean isEmpty = false;
		if (numBytesRead < 0) {
			isEmpty = true;
		} else {
			input.unread(oneByte);
		}
		return isEmpty;
	}

	private void copy(InputStream input, OutputStream output) throws IOException {
		try {
			final int defaultBufferSize = 2 * 1024 * 1024;
			final Integer bufferSize= appConfiguration.getIntValue(ConfigKey.GCS_BUCKET_DEFAULT_NAME, defaultBufferSize).get();
			byte[] buffer = new byte[bufferSize];
			int bytesRead = input.read(buffer);
			while (bytesRead != -1) {
				output.write(buffer, 0, bytesRead);
				bytesRead = input.read(buffer);
			}
		} finally {
			input.close();
			output.close();
		}
	}
}