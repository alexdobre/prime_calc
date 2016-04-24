package com.therdl.prime.calc.model;

import flexjson.JSONSerializer;

import java.util.Map;

/**
 * Convenience class for the response the Prime calculator sends back to the client
 * Created by Alex on 22/04/2016.
 */
public class Response {

	public static final int BAD_REQUEST = 400;
	public static final int OK = 200;
	public static final int SERVER_ERROR = 500;

	private int statusCode = OK;

	private Map<String, Object> contents;

	/**
	 * @return The HTTP status code of the response
	 */
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return The contents of the response in JSON format
	 */
	public String getPayload() {
		return new JSONSerializer().deepSerialize(contents);
	}

	/**
	 * @return The contents of the response
	 */
	public Map<String, Object> getContents() {
		return contents;
	}

	public void setContents(Map<String, Object> contents) {
		this.contents = contents;
	}

}
