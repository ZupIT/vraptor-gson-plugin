package br.com.beyondclick.vraptor.serialization.gson;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import br.com.beyondclick.vraptor.serialization.gson.exclusion.CustomExclusionStrategy;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.view.ResultException;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * https://sites.google.com/site/gson/gson-user-guide
 * 
 * @author ac de souza
 */
public class GsonJSONSerializer implements SerializerBuilder {
	
	private final Writer writer;
	private Object root;
	private Set<String> fieldsToExclude = Sets.newHashSet();
	private boolean indented = false;

	public GsonJSONSerializer(final Writer writer, boolean indented) {
		this.writer = writer;
		this.indented = indented;
	}

	public Serializer exclude(String... names) {
		fieldsToExclude.addAll(Arrays.asList(names));
		return this;
	}

	public Serializer include(String... names) {
		return this;
	}

	public Serializer recursive() {
		return this;
	}

	public void serialize() {
		try {
			getWriter().write(convertUsingGson(root));
			getWriter().flush();
		} catch (IOException e) {
			throw new ResultException("Unable to serialize data width Gson API", e);
		}
	}

	protected String convertUsingGson(Object root) {
		return getGson().toJson(root);
	}

	protected Gson getGson() {
		final GsonBuilder gsonBuilder = new GsonBuilder()
								.registerTypeAdapter(Date.class, new DateSerializer())
								.registerTypeAdapter(java.sql.Date.class, new SqlDateSerializer())
								.setExclusionStrategies(new CustomExclusionStrategy(fieldsToExclude));
		if( indented ) gsonBuilder.setPrettyPrinting();
		return gsonBuilder.create();
	}
	
	private class DateSerializer implements JsonSerializer<Date> {
	  public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
	    return new JsonPrimitive(src.toString());
	  }
	}

	private class SqlDateSerializer implements JsonSerializer<java.sql.Date> {
		public JsonElement serialize(java.sql.Date src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(new Date(src.getTime()).toString());
		}
	}

	protected Writer getWriter() {
		return writer;
	}

	public <T> Serializer from(T object) {
		return from(object, null);
	}

	public <T> Serializer from(T object, String alias) {
		this.root = object;
		return this;
	}
}