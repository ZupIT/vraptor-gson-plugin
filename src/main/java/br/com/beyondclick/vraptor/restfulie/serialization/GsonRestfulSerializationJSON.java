package br.com.beyondclick.vraptor.restfulie.serialization;

import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import br.com.beyondclick.vraptor.restfulie.HypermediaResourceGsonJSONSerializer;
import br.com.beyondclick.vraptor.serialization.gson.GsonJSONSerialization;
import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.restfulie.Restfulie;
import br.com.caelum.vraptor.serialization.SerializerBuilder;

@Component
@RequestScoped
public class GsonRestfulSerializationJSON extends GsonJSONSerialization {
	
	private final Restfulie restfulie;
	private final Configuration config;

	public GsonRestfulSerializationJSON(HttpServletResponse response, Restfulie restfulie, Configuration config) {
		super(response);
		this.restfulie = restfulie;
		this.config = config;
	}

	public SerializerBuilder getSerializer(Writer writer) {
        return new HypermediaResourceGsonJSONSerializer(writer, restfulie, config, indented);
    }
}