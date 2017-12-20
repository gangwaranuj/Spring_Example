package com.workmarket.data.report.internal;

/**
 * Created by theogugoiu on 1/28/14.
 */
public class TopEntity {

	private Long id;
	private String name;
	private String type;
	private Integer sentAssignments;
	private Integer activeAssignments = 0;
	private Integer closedAssignments = 0;
	private Double throughput;

	public Long getId() { return id; }

	public void setId(Long Id) { this.id = Id; }

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public String getType() { return type; }

	public void setType(String type) { this.type = type; }

	public Double getThroughput() {	return throughput; }

	public void setThroughput(Double throughput) { this.throughput = throughput; }

	public Integer getSentAssignments() {
		return sentAssignments;
	}

	public void setSentAssignments(Integer sentAssignments) {
		this.sentAssignments = sentAssignments;
	}

	public Integer getActiveAssignments() {
		return activeAssignments;
	}

	public void setActiveAssignments(Integer activeAssignments) {
		this.activeAssignments = activeAssignments;
	}

	public Integer getClosedAssignments() {
		return closedAssignments;
	}

	public void setClosedAssignments(Integer closedAssignments) {
		this.closedAssignments = closedAssignments;
	}

	@Override
	public String toString() {
		return "TopUser{" +
				"Id=" + id +
				", Name='" + name + '\'' +
				", Type='" + type + '\'' +
				", sentAssignments=" + sentAssignments +
				", activeAssignments=" + activeAssignments +
				", closedAssignments=" + closedAssignments +
				'}';
	}
}
