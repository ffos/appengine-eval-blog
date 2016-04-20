package com.biswadahal.blog.models;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.biswadahal.blog.models.validation.RecordWithIdExists;
import com.biswadahal.blog.models.validation.ValidationGroups.Delete;
import com.biswadahal.blog.models.validation.ValidationGroups.Update;
import com.biswadahal.blog.rest.KeyDeserializer;
import com.biswadahal.blog.rest.KeySerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * PageTemplate always represents an HTML page It's always a template for
 * another page
 */
@Entity
public class PageTemplate {
	@Id
	@JsonIgnore
	@RecordWithIdExists(idKind= PageTemplate.class, groups = {Update.class, Delete.class})
	@NotNull(groups = {Update.class, Delete.class})
	private Long id;

	/**
	 * Anything between the start of HTML document, until the begining of
	 * &lt;body&gt; tag
	 */
	@Size(max = 750)
	private String head;

	/**
	 * Anything within the &lt;body&gt; tag
	 */
	@Size(max = 750)
	private String body;

	/**
	 * Anything to be appended just before the the &lt;body&gt; tag ends
	 */
	@Size(max = 750)
	private String footer;

	@NotNull
	@Valid
	private ContentMeta meta;
	
	protected PageTemplate(){
		//Needed by objectify
	}

	@JsonCreator
	public PageTemplate(@JsonProperty("meta") ContentMeta meta) {
		super();
		this.meta = meta;
	}

	@JsonProperty
	@JsonSerialize(using = KeySerializer.class)
	public Key<PageTemplate> getKey() {
		if (id == null) {
			return null;
		}
		return Key.create(PageTemplate.class, id);
	}
	
	@JsonProperty
	@JsonDeserialize(using = KeyDeserializer.class)
	public void setKey(Key<PageTemplate> key) {
		if (key == null) {
			id = null;
		}
		this.id = key.getId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
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
