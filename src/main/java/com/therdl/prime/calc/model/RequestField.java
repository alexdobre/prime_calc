package com.therdl.prime.calc.model;

import com.therdl.prime.calc.api.Error;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.function.Predicate;

/**
 * Each element in this ENUM is a field in the JSON request to the prime calculator service
 * Created by Alex on 22/04/2016.
 */
public enum RequestField {

	METHOD("method", v -> {
		try {
			Method.valueOf(v.toString());
			return true;
		} catch (Exception e) {
			return false;
		}
	}, Error.BAD_METHOD),
	LIMIT("limit", v -> {
		try {
			if (!NumberUtils.isNumber(v.toString())) {
				return false;
			}
			Integer nr = Integer.parseInt(v.toString());
			if (nr < 0 || nr > (Integer.MAX_VALUE - 5)) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}, Error.BAD_LIMIT);

	private String label;
	private Predicate<Object> validTest;
	private Error error;

	RequestField(String label, Predicate<Object> validTest, Error error) {
		this.label = label;
		this.validTest = validTest;
		this.error = error;
	}

	/**
	 * Validates the given value
	 *
	 * @param value the value to validate
	 * @return true if the request value is valid
	 */
	public boolean isValid(Object value) {
		return validTest.test(value);
	}

	/**
	 * @return the string label expected in the JSON request
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the error associated with the request filed this ENUM instance represents
	 */
	public Error getError() {
		return error;
	}
}
