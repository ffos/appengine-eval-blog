package com.biswadahal.blog.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.biswadahal.blog.models.validation.ValidationGroups;
import com.google.common.base.Preconditions;

/**
 * Package scope for internal use only
 */
class ValidationGroupsUtil {
	public static final List<Class<? extends ValidationGroups>> validValidationGroupClasses = Arrays.asList(
			ValidationGroups.Create.class,
			ValidationGroups.Update.class,
			ValidationGroups.Delete.class
			);
	
	public static Class<?>[] checkArguments(Class<?>...classes) {
		Preconditions.checkNotNull(classes);
		Preconditions.checkArgument(classes.length > 0, "At least one validation group must be specified");
		List<Class<?>> invalidGroups = new ArrayList<>();
		for(Class<?> c: classes){
			if (!validValidationGroupClasses.contains(c)) {
				invalidGroups.add(c);
			}
		}
		if (invalidGroups.isEmpty()){
			return classes;
		}else{
			throw new IllegalArgumentException(String.format("Invalid validation group(s): %s", invalidGroups));
		}
	}
	
}
