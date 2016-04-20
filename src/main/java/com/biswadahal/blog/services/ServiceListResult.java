package com.biswadahal.blog.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.validation.ConstraintViolation;

import com.google.common.base.Optional;

public class ServiceListResult<T> {
	private final Optional<List<T>> value;
	private final List<ConstraintViolation<T>> errors = new ArrayList<>();
	private long pageSize;
	private long zeroBasedPageOffset;

	public ServiceListResult(List<T> result, Collection<ConstraintViolation<T>> errors) {
		super();
		this.value = (result == null ? Optional.<List<T>> absent() : Optional.of(result));
		if (errors != null && !errors.isEmpty()) {
			this.errors.addAll(errors);
		}
	}

	public ServiceListResult(List<T> result) {
		super();
		this.value = (result == null ? Optional.<List<T>> absent() : Optional.of(result));
	}
	
	public Optional<List<T>> getValue() {
		return value;
	}

	public void addError(ConstraintViolation<T> error) {
		if(error == null){
			return;
		}
		this.errors.add(error);
	}

	public void addErrors(Collection<ConstraintViolation<T>> errors) {
		if(errors == null || errors.isEmpty()){
			return;
		}
		this.errors.addAll(errors);
	}
	
	public List<ConstraintViolation<T>> getErrors() {
		return Collections.unmodifiableList(errors);
	}
	
	public boolean hasErrors(){
		return !errors.isEmpty();
	}

	public long getPageSize() {
		return pageSize;
	}

	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}

	public long getZeroBasedPageOffset() {
		return zeroBasedPageOffset;
	}

	public void setZeroBasedPageOffset(long zeroBasedPageOffset) {
		this.zeroBasedPageOffset = zeroBasedPageOffset;
	}
}
