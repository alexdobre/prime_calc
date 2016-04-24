package com.therdl.prime.calc.alg.lucas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Wraps around the Lucas numbers calculator thread and provides convenience methods
 * Created by Alex on 24/04/2016.
 */
public class Lucas {

	public static final int POISON = -1;

	private static final Logger log = LoggerFactory.getLogger(Lucas.class);

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	private BigInteger x1 = new BigInteger("1");
	private BigInteger x2 = new BigInteger("3");

	private int index = 3;
	private final int limit;
	private ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);

	/**
	 * Creates a new Lucas thread with the given limit
	 * @param limit the upper limit (inclusive) to where the thread checks for pseudo primes
	 */
	public Lucas(int limit) {
		this.limit = limit;
	}

	/**
	 * Start the Lucas numbers thread. This thread calculates the Lucas numbers series and tests each integer up
	 * to the limit to be a lucas pseudo prime. Each pseudo prime is placed on a blocking queue to be picked up by
	 * the workers and checked to achieve certainty.
	 *
	 * @throws InterruptedException
	 */
	public void start() throws InterruptedException {
		executor.submit(() -> {
			BigInteger nextLucasNr = new BigInteger("0");
			//calculate lucas numbers
			while (index <= limit) {
				nextLucasNr = x1.add(x2);
				//check if Lucas pseudo prime
				if (nextLucasNr.subtract(BigInteger.ONE).mod(BigInteger.valueOf(index)).equals(BigInteger.ZERO)) {
					try {
						log.debug("Placing Lucas pseudo prime: {}", index);
						queue.put(index);
					} catch (InterruptedException e) {
						log.error(e.getMessage(), e);
						Thread.currentThread().interrupt();
					}
				}
				x1 = x2;
				x2 = nextLucasNr;
				index++;
			}
			log.debug("Lucas is done at limit: {} with lucas nr: {}", limit, nextLucasNr.toString());
			//once done, the thread injects a poison value into the queue to notify the workers on the other side
			injectPoison();
		});
	}

	/**
	 * @return the queue where Lucas pseudo primes are placed by the thread
	 */
	public ArrayBlockingQueue<Integer> getQueue() {
		return queue;
	}

	/**
	 * @return The limit up to which the lucas thread looks for pseudo primes
	 */
	public int getLimit() {
		return limit;
	}

	private void injectPoison() {
		queue.add(POISON);
	}


}
