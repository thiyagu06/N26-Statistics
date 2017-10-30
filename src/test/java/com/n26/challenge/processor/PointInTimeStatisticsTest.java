package com.n26.challenge.processor;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.n26.challenge.model.PointInTimeStatistics;

public class PointInTimeStatisticsTest {

	private PointInTimeStatistics pointInTimeStatistics;

	@Before
	public void setUp() {
		pointInTimeStatistics = new PointInTimeStatistics(10.0);
	}

	@Test
	public void basicConstructorTest() {
		assertEquals(10.0, pointInTimeStatistics.getSum(), 0);
		assertEquals(10.0, pointInTimeStatistics.getAvg(), 0.001);
		assertEquals(1, pointInTimeStatistics.getCount(), 0);

		assertEquals(10.0, pointInTimeStatistics.getMax(), 0);
		assertEquals(10.0, pointInTimeStatistics.getMin(), 0);

		pointInTimeStatistics.increment(20.0);

		assertEquals(30.0, pointInTimeStatistics.getSum(), 0);
		assertEquals(15.0, pointInTimeStatistics.getAvg(), 0.001);
		assertEquals(2, pointInTimeStatistics.getCount(), 0);

		assertEquals(20.0, pointInTimeStatistics.getMax(), 0);
		assertEquals(10.0, pointInTimeStatistics.getMin(), 0);

	}
}
