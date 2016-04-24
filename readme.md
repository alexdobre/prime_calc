# Prime numbers calculator service by Alexandru Dobre
This project contains two different multi threaded approaches to calculating prime numbers.

>The first one is the classic Sieve_of_Eratosthenes detailed here: https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes

>With the sieve I use a boolean[] array going up to Integer.MAX_VALUE -5 (the JVM limit). This array stores primality flags and a worker thread pool removes non prime numbers from it until only the primes are left.

>The second one is called the Lucas_Numbers_Filter detailed here:
https://www.youtube.com/watch?v=lEvXcTYqtKU

>The Lucas numbers sequence is similar to Fibonacci's except it starts with the numbers 1 and 3. So the sequence would be: 1,3,4,7,11,14 ....
The Lucas numbers primality test goes as follows. If for example we wanted to test if 5 is prime, we look at the fifth number in the sequence, which would be 11 and we subtract 1 then test if it's a multiple of 5.
If yes then it's highly likely (but not guaranteed) that the tested number is prime, if no then it's guaranteed that the tested number is not prime.

>My Lucas filter algorithm has a thread which calculates and produces prime candidates (numbers that pass the filter), then a thread pool tests the prime candidates using the standard trial by division algorithm to obtain certainty.

## How to run
After cloning the repository there are a few ways to run the code.

### From the command line
To create the jar file do:
```
mvn clean package
```
Then run it:
```
java -jar ./target/prime-calc.jar
```
This starts the bundled Jetty server on localhost port 8080 ready to receive requests.

#### To get a code coverage report run:
```
mvn cobertura:cobertura
```
And then access
```
./target/site/cobertura/index.html
```
### From the IDE
The entry point class is:
```
com.therdl.prime.calc.ServiceControl
```
It can be ran directly from the IDE and it does not need any other configuration.

## Request format
You need to supply a 'method' which should be one of 'SIEVE' or 'LUCAS_FILTER' and a 'limit. The limit needs to be a positive integer between 0 and Integer.MAX_VALUE - 5 (inclusive).

The end point is: http://localhost:8080/v1/primeCalc and the HTTP method must be 'POST'

Example requests:
```
{"method":"LUCAS_FILTER", "limit":"12"}
```
```
{"method":"SIEVE", "limit":300}
```
## Response format
The response will either contain the 'primes' and given 'limit' or an 'error'.

Example response:
```
{"primes":[2,3,5,7,11,13,17,19,23,29],"limit":30}
```
```
{"error":["BAD_METHOD: The method is required in the request and must be one of: SIEVE,LUCAS_FILTER with no whitespace"]}
```
```
{"error":["TIMEOUT_ERROR: Processing timed out after 5 seconds, please lower the limit or run on a more powerful machine"]}
```
## Errors
* Both the limit and the method are required in the request.
* Even though the upper number limit is Integer.MAX_VALUE -5 there is a 5 seconds timeout limit to processing and that will be hit way before Integer.MAX_VALUE
* The HTTP GET method is not supported and will throw an appropriate error
