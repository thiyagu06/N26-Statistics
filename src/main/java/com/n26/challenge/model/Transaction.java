package com.n26.challenge.model;

/**
 * @author tkamaraj
 * 
 *         Entity represent the transaction request.
 *
 */
public class Transaction {

	/**
	 * timestamp the transaction occurred.
	 */
	public long timeStamp;

	/**
	 * transaction amount
	 */
	public double amount;

	/**
	 * @return the timeStamp
	 */
	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp
	 *            the timeStamp to set
	 */
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}
}
