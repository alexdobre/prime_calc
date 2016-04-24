package com.therdl.prime.calc.model;

/**
 * Each element in this ENUM is a field in the JSON response from the prime calculator service to the client
 * Created by Alex on 22/04/2016.
 */
public enum ResponseField {

	ERROR("error"),
	PRIMES("primes"),
	LIMIT("limit");

	private String label;

	ResponseField(String label) {
		this.label = label;
	}

	/**
	 * @return the string label as expected to be seen in the JSON response
	 */
	public String getLabel() {
		return label;
	}
}
