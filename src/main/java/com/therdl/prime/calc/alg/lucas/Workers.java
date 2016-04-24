package com.therdl.prime.calc.alg.lucas;

import com.therdl.prime.calc.alg.ConcurrentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Wraps around the worker threads which test Lucas pseudoprimes using trial by division with previous known primes.
 * The worker threads pick up Lucas pseudo primes as they become available in order and then start the trial by
 * division on each one based on the previously discovered primes.
 * Created by Alex on 24/04/2016.
 */
public class Workers {

	private static final Logger log = LoggerFactory.getLogger(Workers.class);

	private Lucas lucas;

	private SortedSet<Integer> primes = Collections.synchronizedSortedSet(new TreeSet<>());

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	final Lock lock = new ReentrantLock();
	final Condition done = lock.newCondition();

	private int maxPrime = 2;

	public Workers(Lucas lucas) {
		this.lucas = lucas;
		primes.add(2);
	}

	/**
	 * Starts the workers. They begin listening for lucas pseudo primes on the blocking queue. Once the {@link Lucas}
	 * thread injects {@link Lucas#POISON} into the {@link Lucas#getQueue()} the workers finish and signal DONE via
	 * the {@link #getDone()} and {@link #getLock()} condition lock pair.
	 */
	public void start() {
		executor.submit(() -> {
			int primeCandidate = 2;
			while (primeCandidate <= (lucas.getLimit())) {
				log.debug("Starting prime candidate worker thread: {}", primeCandidate);
				int sqrt = (int) (Math.sqrt(primeCandidate));

				boolean isPrime = true;
				for (int p : primes) {
					//we stop if we get above the square root = found a prime
					if (p >= sqrt) {
						log.debug("prime {} went above the sqrt of {} which is {}", p, primeCandidate, sqrt);
						break;
					}
					if (primeCandidate % p == 0) {
						log.debug("**** Prime candidate {} actually divides by: {}", primeCandidate, p);
						isPrime = false;
						break;
					}
				}
				if (isPrime) {
					addPrime(primeCandidate);
				}

				try {
					primeCandidate = lucas.getQueue().take();
					if (primeCandidate == Lucas.POISON) {
						log.debug("Queue has been poisoned, exiting");
						break;
					}
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
					Thread.currentThread().interrupt();
					break;
				}
			}

			log.debug("Workers signalling DONE");
			ConcurrentUtil.signalDone(lock, done);
		});
	}

	/**
	 * @return The primes discovered by the workers
	 */
	public SortedSet<Integer> getPrimes() {
		return primes;
	}


	/**
	 * @return the lock used in conjunction with {@link #getDone()} to signal the end of processing
	 */
	public Lock getLock() {
		return lock;
	}

	/**
	 * @return the condition used in conjunction with {@link #getLock()} to signal the end of processing
	 */
	public Condition getDone() {
		return done;
	}

	/**
	 * @return the worker thread executor
	 */
	public ExecutorService getExecutor() {
		return executor;
	}

	private void addPrime(int prime) {
		if (maxPrime < prime) {
			maxPrime = prime;
		}
		primes.add(prime);
	}
}
