package com.biswadahal.blog.models;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.biswadahal.blog.models.validation.GcsFilePathIsUnique;
import com.biswadahal.blog.models.validation.RecordWithIdExists;
import com.biswadahal.blog.models.validation.ValidationGroups.Create;
import com.biswadahal.blog.models.validation.ValidationGroups.Delete;
import com.biswadahal.blog.models.validation.ValidationGroups.Update;
import com.biswadahal.blog.rest.AssetStreamURISerializer;
import com.biswadahal.blog.rest.KeyDeserializer;
import com.biswadahal.blog.rest.KeySerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Assets here represent anything that can be saved as an "artifact" This class
 * refers to assets which can be textual or non-textual, like text, code, images, pdf etc
 * 
 * These are referred by URLs and can be injected into pages
 */
@Entity
public class Asset {
	@Id
	@NotNull(groups = {Update.class, Delete.class})
	@RecordWithIdExists(idKind= Asset.class, groups = {Update.class, Delete.class})
	@JsonIgnore
	private Long id;

	@NotNull(groups = {Update.class, Delete.class})
	@JsonIgnore
	private BlobKey contentKey;
	
	@NotNull
	@Size(min = 3, max = 512)
	@Pattern(regexp="[a-z0-9+/\\-_]+")
	@GcsFilePathIsUnique(groups = {Create.class})
	private String filePath;

	@NotNull
	@Valid
	private ContentMeta meta;
	
	protected Asset(){
		//Needed by Objectify
	}

	public Asset(BlobKey contentKey, String pathOrFileName, ContentMeta meta) {
		super();
		this.contentKey = contentKey;
		this.filePath = pathOrFileName;
		this.meta = meta;
	}
	
	@JsonProperty
	@JsonSerialize(using = KeySerializer.class)
	public Key<Asset> getKey() {
		if (id == null) {
			return null;
		}
		return Key.create(Asset.class, id);
	}

	@JsonProperty
	@JsonDeserialize(using = KeyDeserializer.class)
	public void setKey(Key<Asset> key) {
		if (key == null) {
			id = null;
		}
		this.id = key.getId();
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty
	@JsonSerialize(using = AssetStreamURISerializer.class)
	public Key<Asset> getStream() {
		return getKey();
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BlobKey getContentKey() {
		return contentKey;
	}

	public void setContentKey(BlobKey contentKey) {
		this.contentKey = contentKey;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public ContentMeta getMeta() {
		return meta;
	}

	public void setMeta(ContentMeta meta) {
		this.meta = meta;
	}
	
	@Override
	public String toString(){
		return String.format("%s[id=%s, meta=%s]", super.toString(), getId(), getMeta());
	}
	
}
