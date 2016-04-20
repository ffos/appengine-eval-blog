package com.biswadahal.blog.models;

import java.net.URI;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.biswadahal.blog.models.validation.KeyKindMatches;
import com.biswadahal.blog.models.validation.RecordWithIdExists;
import com.biswadahal.blog.models.validation.RecordWithKeyExists;
import com.biswadahal.blog.models.validation.TextSize;
import com.biswadahal.blog.models.validation.ValidationGroups.Delete;
import com.biswadahal.blog.models.validation.ValidationGroups.Update;
import com.biswadahal.blog.rest.KeyDeserializer;
import com.biswadahal.blog.rest.KeySerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Translate;

/**
 * Page always represents an HTML page body It always refers to a page template
 * for the rest of the page
 * 
 */
@Entity
public class Page {
	public enum Type {
		MAIN, SUPPORT
	}
	
	@Id
	@NotNull(groups = {Update.class, Delete.class})
	@RecordWithIdExists(idKind= Page.class, groups = {Update.class, Delete.class})
	@JsonIgnore
	private Long id;

	@NotNull
	@JsonSerialize(using = KeySerializer.class)
	@JsonDeserialize(using = KeyDeserializer.class)
	@KeyKindMatches(expectedKind="PageTemplate")
	@RecordWithKeyExists
	private Key<PageTemplate> template;

	/**
	 * A page could be a "main" page, for actual publication stuff,
	 * or supporting page, like contacts, promotions, menu etc
	 */
	@NotNull
	private Type type = Type.MAIN;

	/**
	 * Contents of page. Maximum length of allowed content ~1MB (524288 16-bit
	 * chars)
	 */
	@TextSize(max=524288)
	private Text htmlContent; // Not indexed in appengine

	@NotNull
	@Valid
	private ContentMeta meta;  //TODO: always set mime to text/html

	/**
	 * Injected into the head tag
	 */
	@NotNull
	@Size(max = 5)
	@Translate(value=URITranslatorFactory.class)
	private Set<URI> extraCss = new TreeSet<>();

	/**
	 * Injected into the bottom of body tag
	 */
	@NotNull
	@Size(max = 5)
	@Translate(value=URITranslatorFactory.class)
	private Set<URI> extraJs = new TreeSet<>();
	
	protected Page(){
		//Needed by Objectify
	}

	public Page(Key<PageTemplate> template, Type type, ContentMeta meta) {
		this.template = template;
		this.type = type;
		this.meta = meta;
	}

	@JsonProperty
	@JsonSerialize(using = KeySerializer.class)
	public Key<Page> getKey() {
		if (id == null) {
			return null;
		}
		return Key.create(Page.class, id);
	}

	@JsonProperty
	@JsonDeserialize(using = KeyDeserializer.class)
	public void setKey(Key<Page> key) {
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

	public Key<PageTemplate> getTemplate() {
		return template;
	}

	public void setTemplate(Key<PageTemplate> template) {
		this.template = template;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Text getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(Text htmlContent) {
		this.htmlContent = htmlContent;
	}

	public ContentMeta getMeta() {
		return meta;
	}

	public void setMeta(ContentMeta meta) {
		this.meta = meta;
	}

	public Set<URI> getExtraCss() {
		return extraCss;
	}

	public Set<URI> getExtraJs() {
		return extraJs;
	}

	@Override
	public String toString(){
		return String.format("%s[id=%s, type=%s, meta=%s]", super.toString(), getId(), getType(), getMeta());
	}

}
