/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource - guilherme.silveira@caelum.com.br
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.beyondclick.vraptor.restfulie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.restfulie.Restfulie;
import br.com.caelum.vraptor.restfulie.hypermedia.HypermediaResource;
import br.com.caelum.vraptor.restfulie.relation.Relation;
import br.com.caelum.vraptor.restfulie.relation.RelationBuilder;

/**
 * Ensure that JSON serialization, of Restful resources, contains resources links. And not restful resources remains untouched.
 * 
 * @author ac de souza
 */
public class HipermediaResourceGsonJSONSerializerTest {
	private static final Logger log = Logger.getLogger(HipermediaResourceGsonJSONSerializerTest.class);

	private @Mock Restfulie restfulie;
	private @Mock RelationBuilder builder;
	private @Mock HypermediaResource resource;
	
	private ByteArrayOutputStream stream;
	private PrintWriter writer = null;

	private HypermediaResourceGsonJSONSerializer gson;

	private Date defaultTestDate;

	@Before
	public void setup() throws IOException {
		MockitoAnnotations.initMocks(this);

		this.restfulie = mock(Restfulie.class);
		when(restfulie.newRelationBuilder()).thenReturn(builder);

		Configuration config = mock(Configuration.class);
		when(config.getApplicationPath()).thenReturn("http://www.caelum.com.br");

		stream = new ByteArrayOutputStream();
		writer = new PrintWriter(stream, true);
		gson = new HypermediaResourceGsonJSONSerializer(writer, restfulie, config, false);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(1982, 7, 28, 0, 0, 0);
		defaultTestDate = calendar.getTime();
	}
	@Test
	public void shouldSerializeNoLinksIfThereIsNoTransition() {
		gson.from(resource).serialize();
		
		try {
			JSONObject jsonOrder = new JSONObject(result());
			assertNotSame(JSONObject.NULL, jsonOrder);
	
			JSONArray jsonListLinks;
			jsonListLinks = jsonOrder.getJSONArray("links");
			fail("Não deveria ter uma lista de links aqui: "+ jsonListLinks.toString());
		} catch (JSONException e) {
			assertEquals("JSONObject[\"links\"] not found.", e.getMessage());
		}
	}

	@Test
	public void shouldSerializeOneLinkIfThereIsATransition() throws Exception {
		Relation kill = mock(Relation.class);
		when(kill.getName()).thenReturn("kill");
		when(kill.getUri()).thenReturn("/kill");

		when(builder.getRelations()).thenReturn(Arrays.asList(kill));
		gson.from(resource).serialize();

		JSONObject jsonOrder = new JSONObject(result());
		assertNotSame(JSONObject.NULL, jsonOrder);

		JSONArray jsonListLinks;
		jsonListLinks = jsonOrder.getJSONArray("links");
		assertEquals(1, jsonListLinks.length());
		
		JSONObject jsonLink_1 = jsonListLinks.getJSONObject(0);
		assertEquals("kill", jsonLink_1.get("rel"));
		assertEquals("http://www.caelum.com.br/kill", jsonLink_1.get("href"));
	}

	@Test
	public void shouldSerializeAllLinksIfThereAreTransitions() throws Exception {
		Relation kill = mock(Relation.class);
		when(kill.getName()).thenReturn("kill");
		when(kill.getUri()).thenReturn("/kill");

		Relation ressurect = mock(Relation.class);
		when(ressurect.getName()).thenReturn("ressurect");
		when(ressurect.getUri()).thenReturn("/ressurect");

		when(builder.getRelations()).thenReturn(Arrays.asList(kill, ressurect));
		gson.from(resource).serialize();

		JSONObject jsonOrder = new JSONObject(result());
		assertNotSame(JSONObject.NULL, jsonOrder);

		JSONArray jsonListLinks;
		jsonListLinks = jsonOrder.getJSONArray("links");
		assertEquals(2, jsonListLinks.length());
		
		JSONObject jsonLink_1 = jsonListLinks.getJSONObject(0);
		assertEquals("kill", jsonLink_1.get("rel"));
		assertEquals("http://www.caelum.com.br/kill", jsonLink_1.get("href"));
		
		JSONObject jsonLink_2 = jsonListLinks.getJSONObject(1);
		assertEquals("ressurect", jsonLink_2.get("rel"));
		assertEquals("http://www.caelum.com.br/ressurect", jsonLink_2.get("href"));
	}

	public static class Order implements HypermediaResource {
		Client client;
		double price;
		String comments;
		Date date;

		public Order(Client client, double price, String comments, Date date) {
			this.client = client;
			this.price = price;
			this.comments = comments;
			this.date = date;
		}

		public void configureRelations(RelationBuilder builder) {
			builder.relation("kill").at("/kill");
		}
	}
	
	public static class Client {
		String name;
		public Client(String name) {
			this.name = name;
		}
	}

	@Test
	public void shouldSerializeCollectionWithoutLinks() throws Exception {
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please", defaultTestDate);
		gson.from(Arrays.asList(order, order)).serialize();

		JSONArray jsonListOrders = new JSONArray(result());
		assertEquals(2, jsonListOrders.length());

		JSONObject jsonOrder_1 = jsonListOrders.getJSONObject(0);
		assertNotSame(JSONObject.NULL, jsonOrder_1);

		try {
			JSONArray jsonListLinks = jsonOrder_1.getJSONArray("links");
			fail("Não deveria ter uma lista de links aqui: "+ jsonListLinks.toString());
		} catch (JSONException e) {
			assertEquals("JSONObject[\"links\"] not found.", e.getMessage());
		}

		JSONObject jsonOrder_2 = jsonListOrders.getJSONObject(1);
		assertNotSame(JSONObject.NULL, jsonOrder_2);

		try {
			JSONArray jsonListLinks = jsonOrder_2.getJSONArray("links");
			fail("Não deveria ter uma lista de links aqui: "+ jsonListLinks.toString());
		} catch (JSONException e) {
			assertEquals("JSONObject[\"links\"] not found.", e.getMessage());
		}
	}

	private String result() {
		writer.flush();
		String strResult = new String(stream.toByteArray());
		log.debug("JSON: "+ strResult);
		return strResult;
	}
}
