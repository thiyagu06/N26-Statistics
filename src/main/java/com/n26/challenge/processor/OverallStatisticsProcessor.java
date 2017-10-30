/**
 * 
 */
package com.n26.challenge.processor;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.n26.challenge.model.PointInTimeStatistics;
import com.n26.challenge.model.Transaction;
import static com.n26.challenge.helper.StatisticsTimeHelper.now;

/**
 * This class is used to calculated the aggregation for each timestamp in the window
 * also act as the container for overall statistics.
 * 
 * @author Thiyagu
 *
 */
@Service
public class OverallStatisticsProcessor {

	/**
	 * The sliding window value in seconds
	 */
	private static long slidingWindow = 60;

	private volatile long count;
	private volatile double sum, avg, max, min;

	/**
	 * The main data structure holding aggregated data for the entire sliding
	 * window
	 */
	private static final ConcurrentHashMap<Long, PointInTimeStatistics> amounts = new ConcurrentHashMap<>();

	/**
	 * Fetches all the aggregated data for the existing sliding window
	 * 
	 * @return a stream of StatisticsAggregateData objects, each representing a
	 *         second of the sliding window
	 */
	private Stream<PointInTimeStatistics> fetchCurrentWindow() {
		return amounts.keySet().stream().filter(key -> (now() - key) <= slidingWindow).map(key -> amounts.get(key));
	}

	/**
	 * Removes transactions that are outside the sliding window and, are thus,
	 * stale.
	 */
	private void purgeStaleTransactions() {
		List<Long> keys = amounts.keySet().stream().filter(key -> (now() - key) > slidingWindow)
				.collect(Collectors.toList());

		keys.forEach(key -> amounts.remove(key));
	}

	/**
	 * Refreshes the instance to the latest state within the current sliding
	 * window. The public interface is useful from a unit testing perspective.
	 */
	public void updateOverallStatistics() {
		Stream<PointInTimeStatistics> transactions = fetchCurrentWindow();
		if (transactions.count() > 0) {
			max = fetchCurrentWindow().map(e -> e.getMax()).mapToDouble(d -> d).max().getAsDouble();
			min = fetchCurrentWindow().map(e -> e.getMin()).mapToDouble(d -> d).min().getAsDouble();
			count = fetchCurrentWindow().map(e -> e.getCount()).mapToLong(l -> l).sum();
			sum = fetchCurrentWindow().map(e->e.getSum()).mapToDouble(d->d).sum();
			avg = sum / count;
		}
	}

	/**
	 * Clear aggregated values stored by this instance.
	 */
	public void clear() {
		amounts.clear();
	}

	/**
	 * Update the sliding window during runtime.
	 * 
	 * @param seconds
	 *            the number of seconds to update the sliding window to
	 */
	public void setSlidingWindow(long seconds) {
		slidingWindow = seconds;
	}

	/**
	 * Adds a Transaction statistic to the system
	 * 
	 * @param transaction
	 *            a Transaction instance
	 * @return boolean value, true if statistic was successfully ingested. False
	 *         if not or if the transaction is stale
	 */
	public synchronized boolean addStatistic(Transaction transaction) {
		return addStatistic(transaction.getAmount(), transaction.getTimeStamp());
	}

	/**
	 * Adds statistic to the system. Data is aggregated in
	 * StatisticsAggregateData objects for each second of the sliding window
	 * 
	 * @param amount
	 *            the amount to store in memory
	 * @param timestamp
	 *            the timestamp of the statistic
	 * @return boolean value, true if statistic was successfully ingested. False
	 *         if not or if the timestamp is stale
	 */
	public boolean addStatistic(double amount, long timestamp) {
		long seconds = (timestamp / 1000);

		if ((now() - seconds) > slidingWindow)
			return false;

		synchronized (amounts) {
			purgeStaleTransactions();

			PointInTimeStatistics entry = amounts.containsKey(seconds) ? amounts.get(seconds).increment(amount)
					: new PointInTimeStatistics(amount);

			amounts.put(seconds, entry);
		}

		return true;
	}

	/**
	 * Current count of transactions stored in memory, irrespective of the
	 * sliding window. Stale, un-reaped statistics will be included in this
	 * count.
	 * 
	 * @return number of stored transactions
	 */
	public int transactionCount() {
		return amounts.size();
	}

	/**
	 * Gets the latest sum of statistics within the sliding window
	 * 
	 * @return the sum
	 */
	public double getSum() {
		return sum;
	}

	/**
	 * Gets the latest calculated average of statistics within the sliding
	 * window
	 * 
	 * @return the calculated average
	 */
	public double getAvg() {
		return avg;
	}

	/**
	 * Gets the latest max of statistics within the sliding window
	 * 
	 * @return largest amount within the sliding window
	 */
	public double getMax() {
		return max;
	}

	/**
	 * Gets the latest min of statistics within the sliding window
	 * 
	 * @return smallest amount within the sliding window
	 */
	public double getMin() {
		return min;
	}

	/**
	 * Gets the latest count of statistics within the sliding window
	 * 
	 * @return count of statistics within the sliding window
	 */
	public long getCount() {
		return count;
	}
}
