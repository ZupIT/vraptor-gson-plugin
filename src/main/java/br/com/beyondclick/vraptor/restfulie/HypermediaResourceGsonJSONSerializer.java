package br.com.beyondclick.vraptor.restfulie;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonSerializer;

import br.com.beyondclick.vraptor.serialization.gson.GsonJSONSerializer;
import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.restfulie.Restfulie;
import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;
import br.com.caelum.vraptor.restfulie.relation.Relation;
import br.com.caelum.vraptor.restfulie.relation.RelationBuilder;

public class HypermediaResourceGsonJSONSerializer extends GsonJSONSerializer {
	
	private final Restfulie restfulie;
	private final Configuration config;

	public HypermediaResourceGsonJSONSerializer(Writer writer, Restfulie restfulie, Configuration config, boolean indented, Collection<JsonSerializer<?>> serializers) {
		super(writer, indented, serializers);
		this.restfulie = restfulie;
		this.config = config;
	}

	@Override
	protected String convertUsingGson(Object root) {
		String jsonConverted = getGson().toJson(root);

		String linksConverted = "";
		if( root instanceof HypermediaResource ) {
			HypermediaResource resource = (HypermediaResource) root;
			RelationBuilder builder = restfulie.newRelationBuilder();
			resource.configureRelations(builder);
	
			if( !builder.getRelations().isEmpty() ) {
				linksConverted = ",\"links\":";
				List<Link> list = new ArrayList<Link>();
				for (Relation t : builder.getRelations()) {
					list.add( new Link(t.getName(), config.getApplicationPath() + t.getUri()) );
				}
				linksConverted += getGson().toJson(list);
				jsonConverted = jsonConverted.substring(0, jsonConverted.length() - 1) + linksConverted + "}";
			}
		}

		return jsonConverted;
	}
}