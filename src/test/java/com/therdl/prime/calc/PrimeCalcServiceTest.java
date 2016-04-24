package com.therdl.prime.calc;

import com.therdl.prime.calc.api.Error;
import com.therdl.prime.calc.model.Method;
import com.therdl.prime.calc.model.Response;
import com.therdl.prime.calc.model.ResponseField;
import flexjson.JSONSerializer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static com.therdl.prime.calc.model.RequestField.LIMIT;
import static com.therdl.prime.calc.model.RequestField.METHOD;
import static com.therdl.prime.calc.TestUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alex on 22/04/2016.
 */
@RunWith(Enclosed.class)
public class PrimeCalcServiceTest {

	private static RestClient rc;

	@BeforeClass
	public static void setUp() {
		ServiceControl.main(null);
		rc = new RestClient();
	}

	@RunWith(Parameterized.class)
	public static class ParameterizedRunner {

		private Method method;

		@Parameterized.Parameters
		public static Collection methodsToTest() {
			return Arrays.asList(new Object[][]{
					{Method.SIEVE},
					{Method.LUCAS_FILTER}
			});
		}

		public ParameterizedRunner(Method method) {
			this.method = method;
		}

		@Test
		public void basicSanityTest() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), "121");

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 200", response.getStatusCode(), 200);
			assertTrue("Response contains primes", responseMap.containsKey(ResponseField.PRIMES.getLabel()));
			List<Long> primes = (List<Long>) responseMap.get(ResponseField.PRIMES.getLabel());
			assertTrue("Primes size must be " + primesTill121.size(),
					primes.size() == primesTill121.size());
			primes.forEach(p -> assertTrue("Primes must contain " + p.intValue(),
					primesTill121.contains(p.intValue())));
		}

		@Test
		public void lowerLimit0Test() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), "0");

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 200", response.getStatusCode(), 200);
			assertTrue("Response contains primes", responseMap.containsKey(ResponseField.PRIMES.getLabel()));
			List<Long> primes = (List<Long>) responseMap.get(ResponseField.PRIMES.getLabel());
			assertTrue("Primes list must be empty", primes.isEmpty());
		}

		@Test
		public void lowerLimit1Test() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), "1");

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 200", response.getStatusCode(), 200);
			assertTrue("Response contains primes", responseMap.containsKey(ResponseField.PRIMES.getLabel()));
			List<Long> primes = (List<Long>) responseMap.get(ResponseField.PRIMES.getLabel());
			assertTrue("Primes list must be empty", primes.isEmpty());
		}

		@Test
		public void lowerLimit2Test() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), 2L);

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 200", response.getStatusCode(), 200);
			assertTrue("Response contains primes", responseMap.containsKey(ResponseField.PRIMES.getLabel()));
			List<Long> primes = (List<Long>) responseMap.get(ResponseField.PRIMES.getLabel());
			assertTrue("Primes list must have one element", primes.size() == 1);
			assertTrue("Primes must be just 2", primes.contains(2L));
		}

		@Test
		public void lowerLimit3Test() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), 3);

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 200", response.getStatusCode(), 200);
			assertTrue("Response contains primes", responseMap.containsKey(ResponseField.PRIMES.getLabel()));
			List<Long> primes = (List<Long>) responseMap.get(ResponseField.PRIMES.getLabel());
			assertTrue("Primes list must have two elements", primes.size() == 2);
			assertTrue("Primes must contain 2", primes.contains(2L));
			assertTrue("Primes must contain 3", primes.contains(3L));
		}

		@Test
		public void lowerLimit4Test() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), "4");

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 200", response.getStatusCode(), 200);
			assertTrue("Response contains primes", responseMap.containsKey(ResponseField.PRIMES.getLabel()));
			List<Long> primes = (List<Long>) responseMap.get(ResponseField.PRIMES.getLabel());
			assertTrue("Primes list must have two elements", primes.size() == 2);
			assertTrue("Primes must contain 2", primes.contains(2L));
			assertTrue("Primes must contain 3", primes.contains(3L));
		}

		@Test
		public void lowerLimit5Test() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), "5");

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 200", response.getStatusCode(), 200);
			assertTrue("Response contains primes", responseMap.containsKey(ResponseField.PRIMES.getLabel()));
			List<Long> primes = (List<Long>) responseMap.get(ResponseField.PRIMES.getLabel());
			assertTrue("Primes list must have two elements", primes.size() == 3);
			assertTrue("Primes must contain 2", primes.contains(2L));
			assertTrue("Primes must contain 3", primes.contains(3L));
			assertTrue("Primes must contain 5", primes.contains(5L));
		}

		@Test
		public void mediumLimitJustBelowPrimeTest() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), "7918");

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 200", response.getStatusCode(), 200);
			assertTrue("Response contains primes", responseMap.containsKey(ResponseField.PRIMES.getLabel()));
			List<Long> primes = (List<Long>) responseMap.get(ResponseField.PRIMES.getLabel());
			assertTrue("Primes list must have 9999 elements", primes.size() == 999);
		}

		@Test
		public void mediumLimitAtPrimeTest() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), 7919);

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 200", response.getStatusCode(), 200);
			assertTrue("Response contains primes", responseMap.containsKey(ResponseField.PRIMES.getLabel()));
			List<Long> primes = (List<Long>) responseMap.get(ResponseField.PRIMES.getLabel());
			assertTrue("Primes size must be: " + primesTill7919.size(),
					primes.size() == primesTill7919.size());
			primes.forEach(p -> assertTrue("Primes must contain: " + p.intValue(),
					primesTill7919.contains(p.intValue())));
		}

		@Test
		public void mediumLimitJustAbovePrimeTest() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), 7920L);

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 200", response.getStatusCode(), 200);
			assertTrue("Response contains primes", responseMap.containsKey(ResponseField.PRIMES.getLabel()));
			List<Long> primes = (List<Long>) responseMap.get(ResponseField.PRIMES.getLabel());
			assertTrue("Primes list must have 9999 elements", primes.size() == 1000);
			assertTrue("Primes must contain 7919", primes.contains(7919L));
		}

		@Test
		public void limitRequiredErrorTest() {
			//Given
			Map<String, String> requestMap = new HashMap<>();
			requestMap.put(METHOD.getLabel(), method.name());

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 400", response.getStatusCode(), 400);
			assertTrue("Response contains error", responseMap.containsKey(ResponseField.ERROR.getLabel()));
			List<String> errors = (List<String>) responseMap.get(ResponseField.ERROR.getLabel());
			assertTrue("There must be only one error", errors.size() == 1);
			assertTrue("The error must be bad limit", errors.contains(Error.BAD_LIMIT.getLabel()));
		}

		@Test
		public void methodRequiredErrorTest() {
			//Given
			Map<String, String> requestMap = new HashMap<>();
			requestMap.put(LIMIT.getLabel(), "20");

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 400", response.getStatusCode(), 400);
			assertTrue("Response contains error", responseMap.containsKey(ResponseField.ERROR.getLabel()));
			List<String> errors = (List<String>) responseMap.get(ResponseField.ERROR.getLabel());
			assertTrue("There must be only one error", errors.size() == 1);
			assertTrue("The error must be bad method", errors.contains(Error.BAD_METHOD.getLabel()));
		}

		@Test
		public void upperLimitTest() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), Integer.MAX_VALUE - 5);

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 400", response.getStatusCode(), 400);
			assertTrue("Response contains error", responseMap.containsKey(ResponseField.ERROR.getLabel()));
			List<String> errors = (List<String>) responseMap.get(ResponseField.ERROR.getLabel());
			assertTrue("There must be only one error", errors.size() == 1);
			assertTrue("The error must be timeout", errors.contains(Error.TIMEOUT_ERROR.getLabel()));
		}

		@Test
		public void justAboveUpperLimitTest() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), Integer.MAX_VALUE - 4);

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 400", response.getStatusCode(), 400);
			assertTrue("Response contains error", responseMap.containsKey(ResponseField.ERROR.getLabel()));
			List<String> errors = (List<String>) responseMap.get(ResponseField.ERROR.getLabel());
			assertTrue("There must be only one error", errors.size() == 1);
			assertTrue("The error must be bad limit", errors.contains(Error.BAD_LIMIT.getLabel()));
		}

		@Test
		public void negativeLimitErrorTest() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), "-1");

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 400", response.getStatusCode(), 400);
			assertTrue("Response contains error", responseMap.containsKey(ResponseField.ERROR.getLabel()));
			List<String> errors = (List<String>) responseMap.get(ResponseField.ERROR.getLabel());
			assertTrue("There must be only one error", errors.size() == 1);
			assertTrue("The error must be bad limit", errors.contains(Error.BAD_LIMIT.getLabel()));
		}

		@Test
		public void badNumberErrorTest() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), "1.1");

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();


			//Then
			assertEquals("Status code must be 400", response.getStatusCode(), 400);
			assertTrue("Response contains error", responseMap.containsKey(ResponseField.ERROR.getLabel()));
			List<String> errors = (List<String>) responseMap.get(ResponseField.ERROR.getLabel());
			assertTrue("There must be only one error", errors.size() == 1);
			assertTrue("The error must be bad limit", errors.contains(Error.BAD_LIMIT.getLabel()));
		}

		@Test
		public void httpGetNotSupportedTest() {
			//Given
			Map<String, Object> requestMap = buildRequest(method.name(), "121");

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "GET");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 400", response.getStatusCode(), 400);
			assertTrue("Response contains error", responseMap.containsKey(ResponseField.ERROR.getLabel()));
			List<String> errors = (List<String>) responseMap.get(ResponseField.ERROR.getLabel());
			assertTrue("There must be only one error", errors.size() == 1);
			assertTrue("The error must be get not supported", errors.contains(Error.GET_NOT_SUPPORTED.getLabel()));
		}

	}


	public static class NonParameterizedRunner {


		@Test
		public void badMethodAndLimitMultiErrorTest() {
			//Given
			Map<String, Object> requestMap = buildRequest("SAIVE", "xy");

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 400", response.getStatusCode(), 400);
			assertTrue("Response contains error", responseMap.containsKey(ResponseField.ERROR.getLabel()));
			List<String> errors = (List<String>) responseMap.get(ResponseField.ERROR.getLabel());
			assertTrue("There must be only one error", errors.size() == 2);
			assertTrue("The error must be bad method", errors.contains(Error.BAD_METHOD.getLabel()));
			assertTrue("The error must be bad limit", errors.contains(Error.BAD_LIMIT.getLabel()));
		}


		@Test
		public void methodAndLimitRequiredMultiErrorTest() {
			//Given
			Map<String, String> requestMap = new HashMap<>();

			//When
			Response response = rc.sendRequest(new JSONSerializer().deepSerialize(requestMap), "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 400", response.getStatusCode(), 400);
			assertTrue("Response contains error", responseMap.containsKey(ResponseField.ERROR.getLabel()));
			List<String> errors = (List<String>) responseMap.get(ResponseField.ERROR.getLabel());
			assertTrue("There must be only one error", errors.size() == 2);
			assertTrue("The error must be bad method", errors.contains(Error.BAD_METHOD.getLabel()));
			assertTrue("The error must be bad limit", errors.contains(Error.BAD_LIMIT.getLabel()));
		}

		@Test
		public void badJsonFormatTest() {
			//Given
			String payload = "bad JSON string";

			//When
			Response response = rc.sendRequest(payload, "POST");
			Map<String, Object> responseMap = response.getContents();

			//Then
			assertEquals("Status code must be 400", response.getStatusCode(), 400);
			assertTrue("Response contains error", responseMap.containsKey(ResponseField.ERROR.getLabel()));
			List<String> errors = (List<String>) responseMap.get(ResponseField.ERROR.getLabel());
			assertTrue("There must be only one error", errors.size() == 1);
			assertTrue("The error must be bad request", errors.contains(Error.BAD_REQUEST.getLabel()));
		}
	}
}
