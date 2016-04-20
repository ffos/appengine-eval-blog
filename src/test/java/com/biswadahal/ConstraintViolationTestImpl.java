package com.biswadahal;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

public class ConstraintViolationTestImpl<T> implements ConstraintViolation<T> {
	private String message;
	private T bean;
	private Path propertyPath;
	
	public ConstraintViolationTestImpl(String msg, T bean) {
		this(msg, bean, null);
	}

	public ConstraintViolationTestImpl(String msg, T bean, Path propertyPath) {
		this.message = msg;
		this.bean = bean;
		this.propertyPath = propertyPath;
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
		return bean;
	}

	@Override
	public Class<T> getRootBeanClass() {
		return null;
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
		return propertyPath;
	}
	
	public void setPropertyPath(Path path){
		this.propertyPath = path;
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
