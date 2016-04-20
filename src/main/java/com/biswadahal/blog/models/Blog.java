package com.biswadahal.blog.models;

import javax.validation.constraints.NotNull;

import com.biswadahal.blog.models.validation.KeyKindMatches;
import com.biswadahal.blog.models.validation.RecordWithIdExists;
import com.biswadahal.blog.models.validation.RecordWithKeyExists;
import com.biswadahal.blog.models.validation.ValidationGroups.Delete;
import com.biswadahal.blog.models.validation.ValidationGroups.Update;
import com.biswadahal.blog.rest.KeyDeserializer;
import com.biswadahal.blog.rest.KeySerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Blog {
	@Id
	@NotNull(groups = {Update.class, Delete.class})
	@RecordWithIdExists(idKind= Blog.class, groups = {Update.class, Delete.class})
	@JsonIgnore
	private Long id;

	@JsonSerialize(using = KeySerializer.class)
	@JsonDeserialize(using = KeyDeserializer.class)
	@KeyKindMatches(expectedKind="Page")
	@RecordWithKeyExists
	private Key<Page> landingPage;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty
	@JsonSerialize(using = KeySerializer.class)
	public Key<Blog> getKey() {
		if (id == null) {
			return null;
		}
		return Key.create(Blog.class, id);
	}

	@JsonProperty
	@JsonDeserialize(using = KeyDeserializer.class)
	public void setKey(Key<Blog> key) {
		if (key == null) {
			id = null;
		}
		this.id = key.getId();
	}
	public Key<Page> getLandingPage() {
		return landingPage;
	}

	public void setLandingPage(Key<Page> landingPage) {
		this.landingPage = landingPage;
	}
}
