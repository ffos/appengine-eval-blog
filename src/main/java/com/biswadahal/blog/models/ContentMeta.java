package com.biswadahal.blog.models;

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.biswadahal.blog.models.validation.KeyKindMatches;
import com.biswadahal.blog.models.validation.RecordWithKeyExists;
import com.biswadahal.blog.rest.KeyDeserializer;
import com.biswadahal.blog.rest.KeySerializer;
import com.biswadahal.blog.rest.UTCDateTime;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Index;

@Index
public class ContentMeta {
	public enum Status {
		NEVER_PUBLISHED, PUBLISHED, UNPUBLISHED
	}

	@NotNull
	@Size(max = 200)
	private String title;

	@Size(max = 300)
	private String caption;

	@NotNull
	@Valid
	private MimeType mimeType;

	@NotNull
	private Status status = Status.NEVER_PUBLISHED;

	@NotNull
	private Date lastModifiedTimestamp = UTCDateTime.now();

	private Date createdTimestamp = UTCDateTime.now();

	@NotNull
	@Size(max = 9)
	@JsonSerialize(contentUsing=KeySerializer.class)
	@JsonDeserialize(as=TreeSet.class, contentUsing=KeyDeserializer.class)
	@KeyKindMatches(expectedKind="Tag")	
	@RecordWithKeyExists
	private SortedSet<Key<Tag>> tags = new TreeSet<>();
	
	@NotNull
	@Valid
	private AccessControl accessControl = new AccessControl();
	
	protected ContentMeta(){
		//Needed by Objectify
	}

	@JsonCreator
	public ContentMeta(@JsonProperty("title") String title, @JsonProperty("mimeType") MimeType mime) {
		this.title = title;
		this.mimeType = mime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public void setMimeType(MimeType mime) {
		this.mimeType = mime;
	}

	public Date getLastModifiedTimestamp() {
		return lastModifiedTimestamp;
	}

	public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
		this.lastModifiedTimestamp = lastModifiedTimestamp;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public Set<Key<Tag>> getTags() {
		return tags;
	}

	public AccessControl getAccessControl() {
		return accessControl;
	}

	public void setAccessControl(AccessControl accessControl) {
		this.accessControl = accessControl;
	}
	
	public void updateLastModifiedTimestampToCurrentTime(){
		setLastModifiedTimestamp(UTCDateTime.now());
	}

	@Override
	public String toString(){
		return String.format("%s[title=%s, mime=%s]", super.toString(), getTitle(), getMimeType());
	}

}
