package com.therdl.prime.calc;


import com.therdl.prime.calc.model.Response;
import flexjson.JSONDeserializer;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A JAVA REST client used to test the prime calc service
 * Created by Alex on 22/04/2016.
 */
public class RestClient {

	public static final String PRIME_CALC_END_POINT = "/v1/primeCalc";
	public static final String HOSTNAME = "localhost";
	public static final String PORT = "8080";

	private static final String USER_AGENT = "Mozilla/5.0";


	/**
	 * Send the request with the given HTTP method. Please note only POST and GET are supported
	 *
	 * @param payload   the request contents
	 * @param reqMethod the request HTTP method
	 * @return the response received
	 */
	public Response sendRequest(String payload, String reqMethod) {
		String fullUrl = "http://" + HOSTNAME + ":" + PORT + PRIME_CALC_END_POINT;

		System.out.println("\nSending " + reqMethod + " request to " + fullUrl);
		System.out.println("parameters : " + payload);

		try {
			if ("POST".equals(reqMethod)) {
				return sendPost(payload, fullUrl);
			} else if ("GET".equals(reqMethod)) {
				return sendGet(payload, fullUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Response sendPost(String payload, String fullUrl) throws IOException {
		URL url;
		HttpURLConnection connection;
		DataOutputStream wr;
		BufferedReader in;
		Response response = new Response();


		url = new URL(fullUrl);
		connection = (HttpURLConnection) url.openConnection();

		//add request header
		connection.setRequestMethod("POST");

		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		connection.setRequestProperty("Content-type", "application/json; charset=UTF-8");

		//connection.set
		connection.setDoOutput(true);
		wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(payload);
		wr.flush();
		wr.close();

		response.setStatusCode(connection.getResponseCode());

		Map<String, List<String>> headers = connection.getHeaderFields();
		Iterator<String> itr = headers.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			List<String> values = headers.get(key);
			System.out.println("Header: " + key + " = " + values);
		}

		if (connection.getResponseCode() >= 400) {
			in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
		} else {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		}

		String inputLine;
		StringBuilder bdr = new StringBuilder();

		while ((inputLine = in.readLine()) != null) {
			bdr.append(inputLine);
		}

		response.setContents(new JSONDeserializer<Map<String, Object>>().deserialize(bdr.toString()));
		System.out.println("Received response: statusCode=" + response.getStatusCode() + " payload=" +
				response.getPayload());

		return response;

	}

	private Response sendGet(String payload, String fullUrl) throws Exception {

		URL url;
		HttpURLConnection connection = null;
		DataOutputStream wr = null;
		BufferedReader in = null;
		Response response = new Response();

		url = new URL(fullUrl);

		connection = (HttpURLConnection) url.openConnection();

		// optional default is GET
		connection.setRequestMethod("GET");

		//add request header
		connection.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = connection.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		response.setStatusCode(responseCode);

		if (responseCode >= 400) {
			in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
		} else {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		}

		String inputLine;
		StringBuilder sb = new StringBuilder();

		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		response.setContents(new JSONDeserializer<Map<String, Object>>().deserialize(sb.toString()));
		return response;
	}
}
