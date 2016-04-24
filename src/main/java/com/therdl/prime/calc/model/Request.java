package com.therdl.prime.calc.model;

import java.util.Map;

/**
 * Encapsulates a prime calc service request
 * Created by Alex on 22/04/2016.
 */
public class Request {

	private Map<String, Object> contents;

	/**
	 * Creates a prime calc service request, please see {@link RequestField} for details
	 *
	 * @param contents the request contents
	 */
	public Request(Map<String, Object> contents) {
		this.contents = contents;
	}

	/**
	 * @return the request contents
	 */
	public Map<String, Object> getContents() {
		return contents;
	}
}
