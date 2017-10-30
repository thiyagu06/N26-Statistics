package com.n26.challenge.processor;

import static com.n26.challenge.helper.StatisticsTimeHelper.nowMillis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;

@RunWith(ConcurrentTestRunner.class)
public class StatisticsProcessorMultiThreadedTest {

	private static final int NO_OF_THREADS = 4;

	private OverallStatisticsProcessor stats;

	@Before
	public void setupStatistics() {
		stats = new OverallStatisticsProcessor();
		stats.clear();
	}

	@After
	public void multipleWindowIntervalTest() {
		stats.updateOverallStatistics();
		assertEquals(NO_OF_THREADS * 65.5, stats.getSum(), 0);
		assertEquals(16.375, stats.getAvg(), 0.001);
		assertEquals(NO_OF_THREADS * 4, stats.getCount());

		assertEquals(20.5, stats.getMax(), 0);
		assertEquals(10, stats.getMin(), 0);
	}

	@Test
	public void multipleWindowIntervalThreadedTest() {
		stats.setSlidingWindow(10);

		long time = nowMillis();

		// Add statistics into the current time window; they should all be added
		stats.addStatistic(15.0, time - 1500);
		stats.addStatistic(10.0, time - 3000);
		stats.addStatistic(20.0, time - 4000);
		stats.addStatistic(20.5, time - 5000);

		// assertEquals(4, stats.transactionCount());

		// Shorten sliding window to force eviction of three transactions
		stats.setSlidingWindow(2);

		/*
		 * Add first item in the new window. After adding the statistic, the
		 * stale transaction cleanup should leave one transaction from the
		 * previous window
		 */
		stats.addStatistic(10.0, time);

		stats.addStatistic(20.0, time);
		stats.addStatistic(20.5, time);

		// Older than time window, so should be ignored completely
		stats.addStatistic(150, time - 50000);
	}
}
