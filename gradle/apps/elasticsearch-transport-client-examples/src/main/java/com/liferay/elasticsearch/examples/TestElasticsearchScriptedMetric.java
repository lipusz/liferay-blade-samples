
package com.liferay.elasticsearch.examples;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.ScriptedMetricAggregationBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class TestElasticsearchScriptedMetric {

	public static String CLUSTER_NAME = "LiferayElasticsearchCluster";
	public static String INDEX_NAME = "ledger";
	public static String NETWORK_HOST = "localhost";
	public static String TRANSPORT_PORT = "9300";

	public static void main(String[] args)
		throws IOException {

		Builder settingsBuilder = Settings.builder();

		settingsBuilder.put("client.transport.ignore_cluster_name", false);
		settingsBuilder.put("client.transport.sniff", false);
		settingsBuilder.put(
			"cluster.name", _getSystemProperty("cluster.name", CLUSTER_NAME));
		settingsBuilder.put(
			"request.headers.X-Found-Cluster",
			_getSystemProperty("cluster.name", CLUSTER_NAME));

		PreBuiltTransportClient client =
			new PreBuiltTransportClient(settingsBuilder.build());

		try {
			String host = _getSystemProperty("network.host", NETWORK_HOST);
			int port = Integer.valueOf(
				_getSystemProperty("transport.port", TRANSPORT_PORT));

			TransportAddress address =
				new TransportAddress(InetAddress.getByName(host),port);

			client.addTransportAddress(address);
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// Run example from 
		// https://www.elastic.co/guide/en/elasticsearch/reference/7.9/search-aggregations-metrics-scripted-metric-aggregation.html

		SearchRequestBuilder searchRequestBuilder =
			new SearchRequestBuilder(client, SearchAction.INSTANCE);

		ScriptedMetricAggregationBuilder scriptedMetricAggregationBuilder =
			AggregationBuilders.scriptedMetric(
				"profit");

		scriptedMetricAggregationBuilder.initScript(
			new Script("state.transactions = []"));

		scriptedMetricAggregationBuilder.mapScript(
			new Script(
				"state.transactions.add(doc.type.value == 'sale' ? doc.amount.value : -1 * doc.amount.value)"));

		scriptedMetricAggregationBuilder.combineScript(
			new Script(
				"double profit = 0; for (t in state.transactions) { profit += t } return profit"));

		scriptedMetricAggregationBuilder.reduceScript(
			new Script(
				"double profit = 0; for (a in states) { profit += a } return profit"));

		searchRequestBuilder.addAggregation(scriptedMetricAggregationBuilder);
		searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
		searchRequestBuilder.setIndices(
			_getSystemProperty("index.name", INDEX_NAME));

		System.out.println(
			"Request JSON\n" + _getPrettyPrintedRequestString(
				searchRequestBuilder));

		ActionFuture<SearchResponse> future = client.search(
			searchRequestBuilder.request());

		SearchResponse searchResponse = future.actionGet();

		System.out.println("Response JSON\n" + _getPrettyPrintedRequestString(
			searchResponse));

		client.close();
	}

	private static String _getSystemProperty(String key, String defaultValue) {
		String value = System.getProperty(key);

		if (value == null || value.isEmpty()) {
			return defaultValue;
		}

		return value;
	}

	private static String _getPrettyPrintedRequestString(
		Object builder) {

		GsonBuilder gsonBuilder = new GsonBuilder();

		gsonBuilder.setPrettyPrinting();

		Gson gson = gsonBuilder.create();

		JsonParser jsonParser = new JsonParser();

		try {
			return gson.toJson(
				jsonParser.parse(builder.toString()));
		}
		catch (Exception exception) {
			return exception.getMessage();
		}
	}

}
