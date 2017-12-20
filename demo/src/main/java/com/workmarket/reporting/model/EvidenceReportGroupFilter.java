package com.workmarket.reporting.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class EvidenceReportGroupFilter {

	private static final long serialVersionUID = 1L;

	@JsonProperty("id")
	private Long id;
	@JsonProperty("name")
	private String name;

	public EvidenceReportGroupFilter(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	Long getId() {
		return id;
	}

	void setId(Long id) {
		this.id = id;
	}

	String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EvidenceReportGroupFilter)) return false;

		EvidenceReportGroupFilter that = (EvidenceReportGroupFilter) o;

		if (!id.equals(that.id)) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "EvidenceReportGroupFilter{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
