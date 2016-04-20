package com.biswadahal.blog.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class HelloEntity {
	public enum EnumType {
		VAL1;
	}
	@Id
	private Long id;
	private String dummyField1;
	private String dummyField2;
	private EnumType enumType;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDummyField1() {
		return dummyField1;
	}
	public void setDummyField1(String dummyField1) {
		this.dummyField1 = dummyField1;
	}
	public String getDummyField2() {
		return dummyField2;
	}
	public void setDummyField2(String dummyField2) {
		this.dummyField2 = dummyField2;
	}
	public EnumType getEnumType() {
		return enumType;
	}
	public void setEnumType(EnumType enumType) {
		this.enumType = enumType;
	}
}
