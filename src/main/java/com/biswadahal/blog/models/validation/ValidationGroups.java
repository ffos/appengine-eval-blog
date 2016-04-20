package com.biswadahal.blog.models.validation;

import javax.validation.groups.Default;

public interface ValidationGroups extends Default{
	public static interface Create extends ValidationGroups{} 
	public static interface Update extends ValidationGroups{} 
	public static interface Delete extends ValidationGroups{} 
}
