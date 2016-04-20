package com.biswadahal.blog.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.annotation.Index;

@Index
public class MimeType implements Comparable<MimeType> {
	/**
	 * Represents the first part of a mime: e.g. in "text/html",
	 * className="text"
	 */
	@NotNull
	@Size(min = 1, max = 200)
	@Pattern(regexp="[a-z]+")
	private String className;

	/**
	 * Represents the second part of a mime: e.g. in "text/html",
	 * typeName="html"
	 */
	@NotNull
	@Size(min = 1, max = 200)
	@Pattern(regexp="[a-z0-9\\s\\-_\\.,+;]+")
	private String typeName;
	
	protected MimeType(){
		//Needed by Objectify
	}

	@JsonCreator
	public MimeType(@JsonProperty("className") String className, @JsonProperty("typeName") String typeName) {
		setClassName(className);
		setTypeName(typeName);
	}

	public String getFullMimeType() {
		return String.format("%s/%s", className, typeName);
	}

	public String getClassName() {
		return className;
	}

	/**
	 * Surrounding spaces are removed
	 */
	public void setClassName(String className) {
		if (className != null && className.trim().length()>0) {
			this.className = className.toLowerCase().trim();
		}
	}

	public String getTypeName() {
		return typeName;
	}

	/**
	 * Surrounding spaces are removed
	 */
	public void setTypeName(String typeName) {
		if (typeName != null && typeName.trim().length()>0) {
			this.typeName = typeName.toLowerCase().trim();
		}
	}

	@Override
	public String toString(){
		return String.format("%s[%s]", super.toString(), getFullMimeType());
	}
	
	@Override
	public int compareTo(MimeType o) {
		if (o == null) {
			return 1;
		}
		return getFullMimeType().compareTo(o.getFullMimeType());
	}
}
