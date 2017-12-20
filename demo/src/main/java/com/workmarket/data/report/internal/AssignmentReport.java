package com.workmarket.data.report.internal;

/**
 * Created by theogugoiu on 1/29/14.
 */
public class AssignmentReport {

	private double voidRate;
	private double cancelRate;
	private double paidRate;
	private double sentRate;
	private double workSend;
	private double workFeed;
	private double userSend;
	private double groups;
	private double search;

	public double getVoidRate() { return voidRate; }

	public void setVoidRate(double voidRate) {
		this.voidRate = voidRate;
	}

	public double getCancelRate() {
		return cancelRate;
	}

	public void setCancelRate(double cancelRate) {
		this.cancelRate = cancelRate;
	}

	public double getSentRate() {
		return sentRate;
	}

	public void setSentRate(double sentRate) {
		this.sentRate = sentRate;
	}

	public double getPaidRate() {
		return paidRate;
	}

	public void setPaidRate(double paidRate) {
		this.paidRate = paidRate;
	}

	public double getWorkSend() {
		return workSend;
	}

	public void setWorkSend(double workSend) {
		this.workSend = workSend;
	}

	public double getWorkFeed() {
		return workFeed;
	}

	public void setWorkFeed(double workFeed) {
		this.workFeed = workFeed;
	}

	public double getUserSend() {
		return userSend;
	}

	public void setUserSend(double userSend) {
		this.userSend = userSend;
	}

	public double getGroups() {
		return groups;
	}

	public void setGroups(double groups) {
		this.groups = groups;
	}

	public double getSearch() {
		return search;
	}

	public void setSearch(double search) {
		this.search = search;
	}
}
