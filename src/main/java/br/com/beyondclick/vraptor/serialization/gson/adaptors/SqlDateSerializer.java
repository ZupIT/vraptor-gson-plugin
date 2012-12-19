package br.com.beyondclick.vraptor.serialization.gson.adaptors;

import java.lang.reflect.Type;
import java.sql.Date;

import br.com.caelum.vraptor.ioc.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Component
public class SqlDateSerializer implements JsonSerializer<java.sql.Date> {
	public JsonElement serialize(java.sql.Date src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(new Date(src.getTime()).toString());
	}
}