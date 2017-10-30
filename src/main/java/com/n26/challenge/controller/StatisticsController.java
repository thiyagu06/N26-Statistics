package com.n26.challenge.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n26.challenge.processor.OverallStatisticsProcessor;

/**
 * @author tkamaraj
 *
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

	@Autowired
	OverallStatisticsProcessor statisticsProcessor;

	@GetMapping
	public ResponseEntity<Map<String, Object>> getStatistics() {
		statisticsProcessor.updateOverallStatistics();
		Map<String, Object> overallStatistics = new HashMap<>();
		overallStatistics.put("sum", statisticsProcessor.getSum());
		overallStatistics.put("avg", statisticsProcessor.getAvg());
		overallStatistics.put("max", statisticsProcessor.getMax());
		overallStatistics.put("min", statisticsProcessor.getMin());
		overallStatistics.put("count", statisticsProcessor.getCount());
		return new ResponseEntity<Map<String, Object>>(overallStatistics, HttpStatus.OK);
	}

}
