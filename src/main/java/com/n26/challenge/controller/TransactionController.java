/**
 * 
 */
package com.n26.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n26.challenge.model.Transaction;
import com.n26.challenge.processor.OverallStatisticsProcessor;

/**
 * @author tkamaraj
 *
 */
@RestController
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	OverallStatisticsProcessor statisticsProcessor;

	@PostMapping
	public ResponseEntity<Void> addTransaction(@RequestBody Transaction transaction) {
		boolean success = statisticsProcessor.addStatistic(transaction);
		HttpStatus status = success ? HttpStatus.CREATED : HttpStatus.NO_CONTENT;
		return new ResponseEntity<>(status);
	}

}
