package com.workmarket.domains.model.assessment;

public class AssessmentStatistics {
	private Integer numberOfInvited;
	private Integer numberOfPassed;
	private Integer numberOfFailed;
	private Double averageScore;
	
	public Integer getNumberOfInvited() {
		return numberOfInvited;
	}
	public void setNumberOfInvited(Integer numberOfInvited) {
		this.numberOfInvited = numberOfInvited;
	}
	public Integer getNumberOfPassed() {
		return numberOfPassed;
	}
	public void setNumberOfPassed(Integer numberOfPassed) {
		this.numberOfPassed = numberOfPassed;
	}
	public Integer getNumberOfFailed() {
		return numberOfFailed;
	}
	public void setNumberOfFailed(Integer numberOfFailed) {
		this.numberOfFailed = numberOfFailed;
	}
	public Double getAverageScore() {
		return averageScore;
	}
	public void setAverageScore(Double averageScore) {
		this.averageScore = averageScore;
	}
}