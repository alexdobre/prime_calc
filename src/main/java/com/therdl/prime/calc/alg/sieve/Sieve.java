package com.therdl.prime.calc.alg.sieve;

import com.therdl.prime.calc.alg.ConcurrentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Wrapper class for the sieve used in the Sieve_of_Eratosthenes algorithm providing convenience methods.
 * It holds all the integers up to a given limit in a memory efficient way, each one having a primality flag
 * Created by Alex on 23/04/2016.
 */
public class Sieve {

	private static final Logger log = LoggerFactory.getLogger(Sieve.class);

	//because the sieve is a memory intensive data structure as it grows exponentially with the limit, we use a low
	//memory footprint data structure to hold it
	//the index of the flag is the integer it represents (adjusting for 0 based array indexing) and the boolean value
	//is the primality flag as in true = NOT prime (just because the default is false)
	private boolean[] contents;

	private AtomicInteger lowestPrimeCandidate;
	private final int limit;

	final Lock lock = new ReentrantLock();
	final Condition done = lock.newCondition();

	/**
	 * Creates a new sieve with contents up to the given limit
	 *
	 * @param limit the upper limit of the sieve
	 */
	public Sieve(int limit) {
		this.limit = limit;
		contents = new boolean[limit];
		//2 is the first prime number so we initialize with it
		lowestPrimeCandidate = new AtomicInteger(2);
	}

	/**
	 * Remove the given number from the sieve Please note this number is 1 based so we need to pass the actual integer
	 * number, we do not need to adjust
	 *
	 * @param nr the number who'se flag we set
	 */
	public void remove(int nr) {
		contents[nr - 1] = true;
		if (nr == lowestPrimeCandidate.get()) {
			calculateLowestPrimeCandidate();
		}
	}

	/**
	 * @return the limit up to which the sieve holds contents
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @return at any point in time this variable holds the lowest value in the sieve that has not (YET) been
	 * flagged as not prime (has not yet been eliminated from the sieve)
	 */
	public AtomicInteger getLowestPrimeCandidate() {
		return lowestPrimeCandidate;
	}

	/**
	 * @return the condition used in conjunction with {@link #getLock()} to signal the end of processing
	 */
	public Condition getDone() {
		return done;
	}

	/**
	 * @return the lock used in conjunction with {@link #getDone()} to signal the end of processing
	 */
	public Lock getLock() {
		return lock;
	}

	private void calculateLowestPrimeCandidate() {
		for (int i = lowestPrimeCandidate.get(); i < contents.length; i++) {
			if (!contents[i]) {
				lowestPrimeCandidate.set(i + 1);
				return;
			}
		}
		log.debug("Sieve signalling DONE");
		ConcurrentUtil.signalDone(lock, done);
	}

}
