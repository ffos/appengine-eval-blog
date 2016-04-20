package com.biswadahal.blog.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
import com.googlecode.objectify.annotation.Index;

@Entity
public class Tag implements Comparable<Tag> {
	@Id
	@NotNull(groups = {Update.class, Delete.class})
	@RecordWithIdExists(idKind= Tag.class, groups = {Update.class, Delete.class})
	@JsonIgnore
	private Long id;

	@Index
	@NotNull
	@Size(min = 1, max = 30)
	@Pattern(regexp = "[a-z0-9#*+\\-_]+")
	private String label;
	
	protected Tag(){
		//Needed by Objectify
	}

	@JsonCreator
	public Tag(@JsonProperty("label") String label) {
		setLabel(label);
	}

	@JsonProperty
	@JsonSerialize(using = KeySerializer.class)
	public Key<Tag> getKey() {
		if (id == null) {
			return null;
		}
		return Key.create(Tag.class, id);
	}

	@JsonProperty
	@JsonDeserialize(using = KeyDeserializer.class)
	public void setKey(Key<Tag> key) {
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

	public String getLabel() {
		return label;
	}

	/**
	 * Value is trimmed and lowercased (not locale sensitive) before being
	 * stored
	 * 
	 * @param label
	 */
	public void setLabel(String label) {
		if (label != null && label.trim().length() > 0) {
			this.label = label.toLowerCase().trim();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

	@Override
	public int compareTo(Tag o) {
		if (o == null) {
			return 1;
		}
		return label.compareTo(o.getLabel());
	}

	@Override
	public String toString() {
		return String.format("%s[id=%s, label=%s]", super.toString(), getId(), getLabel());
	}

}
