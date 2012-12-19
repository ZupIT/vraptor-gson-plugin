package br.com.beyondclick.vraptor.serialization.gson.adaptors;

import java.lang.reflect.Type;
import java.util.Date;

import br.com.caelum.vraptor.ioc.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Component
public class DateSerializer implements JsonSerializer<Date> {
	public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}
}