package com.biswadahal.blog.services;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value=Include.NON_NULL)
public class IllegalArgumentViolation<T> implements ConstraintViolation<T> {
	private String message;
	private Class<T> beanClass;

	public IllegalArgumentViolation(String message, Class<T> beanClass) {
		super();
		this.message = message;
		this.beanClass = beanClass;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getMessageTemplate() {
		return null;
	}

	@Override
	public T getRootBean() {
		return null;
	}

	@Override
	public Class<T> getRootBeanClass() {
		return beanClass;
	}

	@Override
	public Object getLeafBean() {
		return null;
	}

	@Override
	public Object[] getExecutableParameters() {
		return null;
	}

	@Override
	public Object getExecutableReturnValue() {
		return null;
	}

	@Override
	public Path getPropertyPath() {
		return null;
	}

	@Override
	public Object getInvalidValue() {
		return null;
	}

	@Override
	public ConstraintDescriptor<?> getConstraintDescriptor() {
		return null;
	}

	@Override
	public <U> U unwrap(Class<U> type) {
		return null;
	}

}
