package br.com.beyondclick.vraptor.serialization.gson.exclusion;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class CustomExclusionStrategy implements ExclusionStrategy {

	private final Set<Class<?>[]> toExcludeClasses;
	private final Set<String> fieldsToExclude;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CustomExclusionStrategy(Set<String> fieldsToExclude, Class<?>... classes) {
		this.fieldsToExclude = fieldsToExclude;
		this.toExcludeClasses = new LinkedHashSet(Arrays.asList(classes));
	}

	public boolean shouldSkipField(FieldAttributes f) {
		return fieldsToExclude.contains(f.getName());
	}

	public boolean shouldSkipClass(Class<?> clazz) {
		return toExcludeClasses.contains(clazz);
	}
}
