package br.com.beyondclick.vraptor.serialization.gson;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonSerializer;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.NoRootSerialization;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.view.ResultException;

/**
 * Gson implementation for JSONSerialization
 * 
 * Know limitation: you can not serialize objects with circular references since that will result in infinite recursion.
 * https://sites.google.com/site/gson/gson-user-guide#TOC-Object-Examples
 * 
 * @author ac de souza
 * @since 3.3.2
 *
 */

public class GsonJSONSerialization implements JSONSerialization {

	private final HttpServletResponse response;
	protected final Collection<JsonSerializer<?>> serializers;
	
	public GsonJSONSerialization(HttpServletResponse response, Collection<JsonSerializer<?>> serializers) {
		this.response = response;
		this.serializers = serializers;
	}

	public boolean accepts(String format) {
		return "json".equals(format);
	}

	public <T> Serializer from(T object) {
		return from(object, null);
	}

	public <T> Serializer from(T object, String alias) {
		try {
			response.setContentType("application/json");
			return getSerializer(response.getWriter()).from(object, alias);
		} catch (IOException e) {
			throw new ResultException("Unable to serialize data", e);
		}
	}

	public SerializerBuilder getSerializer(Writer writer) {
            return new GsonJSONSerializer(writer, indented, serializers);
    }

	public <T> NoRootSerialization withoutRoot() {
		return this;
	}

	protected boolean indented = false;
	public JSONSerialization indented() {
		indented = true;
		return this;
	}
}