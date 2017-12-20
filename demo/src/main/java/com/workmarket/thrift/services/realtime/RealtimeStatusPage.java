package com.workmarket.thrift.services.realtime;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RealtimeStatusPage implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<RealtimeRow> rows;
	private long numResults;
	private List<RealtimeUser> ownerFilterOptions;
	private List<RealtimeDropDownOption> projects;
	private List<RealtimeDropDownOption> clients;
	private int maxUnansweredQuestions;
	private com.workmarket.thrift.core.RequestStatistics stats;

	public RealtimeStatusPage() {
	}

	public RealtimeStatusPage(long numResults) {
		this();
		this.numResults = numResults;
	}

	public int getRowsSize() {
		return (this.rows == null) ? 0 : this.rows.size();
	}

	public java.util.Iterator<RealtimeRow> getRowsIterator() {
		return (this.rows == null) ? null : this.rows.iterator();
	}

	public void addToRows(RealtimeRow elem) {
		if (this.rows == null) {
			this.rows = new ArrayList<RealtimeRow>();
		}
		this.rows.add(elem);
	}

	public List<RealtimeRow> getRows() {
		return this.rows;
	}

	public RealtimeStatusPage setRows(List<RealtimeRow> rows) {
		this.rows = rows;
		return this;
	}

	public boolean isSetRows() {
		return this.rows != null;
	}

	public long getNumResults() {
		return this.numResults;
	}

	public RealtimeStatusPage setNumResults(long numResults) {
		this.numResults = numResults;
		return this;
	}

	public boolean isSetNumResults() {
		return (numResults > 0L);
	}

	public int getOwnerFilterOptionsSize() {
		return (this.ownerFilterOptions == null) ? 0 : this.ownerFilterOptions.size();
	}

	public java.util.Iterator<RealtimeUser> getOwnerFilterOptionsIterator() {
		return (this.ownerFilterOptions == null) ? null : this.ownerFilterOptions.iterator();
	}

	public void addToOwnerFilterOptions(RealtimeUser elem) {
		if (this.ownerFilterOptions == null) {
			this.ownerFilterOptions = new ArrayList<RealtimeUser>();
		}
		this.ownerFilterOptions.add(elem);
	}

	public List<RealtimeUser> getOwnerFilterOptions() {
		return this.ownerFilterOptions;
	}

	public RealtimeStatusPage setOwnerFilterOptions(List<RealtimeUser> ownerFilterOptions) {
		this.ownerFilterOptions = ownerFilterOptions;
		return this;
	}

	public boolean isSetOwnerFilterOptions() {
		return this.ownerFilterOptions != null;
	}

	public int getProjectsSize() {
		return (this.projects == null) ? 0 : this.projects.size();
	}

	public java.util.Iterator<RealtimeDropDownOption> getProjectsIterator() {
		return (this.projects == null) ? null : this.projects.iterator();
	}

	public void addToProjects(RealtimeDropDownOption elem) {
		if (this.projects == null) {
			this.projects = new ArrayList<RealtimeDropDownOption>();
		}
		this.projects.add(elem);
	}

	public List<RealtimeDropDownOption> getProjects() {
		return this.projects;
	}

	public RealtimeStatusPage setProjects(List<RealtimeDropDownOption> projects) {
		this.projects = projects;
		return this;
	}

	public boolean isSetProjects() {
		return this.projects != null;
	}

	public int getClientsSize() {
		return (this.clients == null) ? 0 : this.clients.size();
	}

	public java.util.Iterator<RealtimeDropDownOption> getClientsIterator() {
		return (this.clients == null) ? null : this.clients.iterator();
	}

	public void addToClients(RealtimeDropDownOption elem) {
		if (this.clients == null) {
			this.clients = new ArrayList<RealtimeDropDownOption>();
		}
		this.clients.add(elem);
	}

	public List<RealtimeDropDownOption> getClients() {
		return this.clients;
	}

	public RealtimeStatusPage setClients(List<RealtimeDropDownOption> clients) {
		this.clients = clients;
		return this;
	}

	public boolean isSetClients() {
		return this.clients != null;
	}

	public int getMaxUnansweredQuestions() {
		return this.maxUnansweredQuestions;
	}

	public RealtimeStatusPage setMaxUnansweredQuestions(int maxUnansweredQuestions) {
		this.maxUnansweredQuestions = maxUnansweredQuestions;
		return this;
	}

	public boolean isSetMaxUnansweredQuestions() {
		return (maxUnansweredQuestions > 0);
	}

	public com.workmarket.thrift.core.RequestStatistics getStats() {
		return this.stats;
	}

	public RealtimeStatusPage setStats(com.workmarket.thrift.core.RequestStatistics stats) {
		this.stats = stats;
		return this;
	}

	public boolean isSetStats() {
		return this.stats != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RealtimeStatusPage)
			return this.equals((RealtimeStatusPage) that);
		return false;
	}

	private boolean equals(RealtimeStatusPage that) {
		if (that == null)
			return false;

		boolean this_present_rows = true && this.isSetRows();
		boolean that_present_rows = true && that.isSetRows();
		if (this_present_rows || that_present_rows) {
			if (!(this_present_rows && that_present_rows))
				return false;
			if (!this.rows.equals(that.rows))
				return false;
		}

		boolean this_present_numResults = true;
		boolean that_present_numResults = true;
		if (this_present_numResults || that_present_numResults) {
			if (!(this_present_numResults && that_present_numResults))
				return false;
			if (this.numResults != that.numResults)
				return false;
		}

		boolean this_present_ownerFilterOptions = true && this.isSetOwnerFilterOptions();
		boolean that_present_ownerFilterOptions = true && that.isSetOwnerFilterOptions();
		if (this_present_ownerFilterOptions || that_present_ownerFilterOptions) {
			if (!(this_present_ownerFilterOptions && that_present_ownerFilterOptions))
				return false;
			if (!this.ownerFilterOptions.equals(that.ownerFilterOptions))
				return false;
		}

		boolean this_present_projects = true && this.isSetProjects();
		boolean that_present_projects = true && that.isSetProjects();
		if (this_present_projects || that_present_projects) {
			if (!(this_present_projects && that_present_projects))
				return false;
			if (!this.projects.equals(that.projects))
				return false;
		}

		boolean this_present_clients = true && this.isSetClients();
		boolean that_present_clients = true && that.isSetClients();
		if (this_present_clients || that_present_clients) {
			if (!(this_present_clients && that_present_clients))
				return false;
			if (!this.clients.equals(that.clients))
				return false;
		}

		boolean this_present_maxUnansweredQuestions = true && this.isSetMaxUnansweredQuestions();
		boolean that_present_maxUnansweredQuestions = true && that.isSetMaxUnansweredQuestions();
		if (this_present_maxUnansweredQuestions || that_present_maxUnansweredQuestions) {
			if (!(this_present_maxUnansweredQuestions && that_present_maxUnansweredQuestions))
				return false;
			if (this.maxUnansweredQuestions != that.maxUnansweredQuestions)
				return false;
		}

		boolean this_present_stats = true && this.isSetStats();
		boolean that_present_stats = true && that.isSetStats();
		if (this_present_stats || that_present_stats) {
			if (!(this_present_stats && that_present_stats))
				return false;
			if (!this.stats.equals(that.stats))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_rows = true && (isSetRows());
		builder.append(present_rows);
		if (present_rows)
			builder.append(rows);

		boolean present_numResults = true;
		builder.append(present_numResults);
		if (present_numResults)
			builder.append(numResults);

		boolean present_ownerFilterOptions = true && (isSetOwnerFilterOptions());
		builder.append(present_ownerFilterOptions);
		if (present_ownerFilterOptions)
			builder.append(ownerFilterOptions);

		boolean present_projects = true && (isSetProjects());
		builder.append(present_projects);
		if (present_projects)
			builder.append(projects);

		boolean present_clients = true && (isSetClients());
		builder.append(present_clients);
		if (present_clients)
			builder.append(clients);

		boolean present_maxUnansweredQuestions = true && (isSetMaxUnansweredQuestions());
		builder.append(present_maxUnansweredQuestions);
		if (present_maxUnansweredQuestions)
			builder.append(maxUnansweredQuestions);

		boolean present_stats = true && (isSetStats());
		builder.append(present_stats);
		if (present_stats)
			builder.append(stats);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RealtimeStatusPage(");
		boolean first = true;

		if (isSetRows()) {
			sb.append("rows:");
			if (this.rows == null) {
				sb.append("null");
			} else {
				sb.append(this.rows);
			}
			first = false;
		}
		if (!first) sb.append(", ");
		sb.append("numResults:");
		sb.append(this.numResults);
		first = false;
		if (isSetOwnerFilterOptions()) {
			if (!first) sb.append(", ");
			sb.append("ownerFilterOptions:");
			if (this.ownerFilterOptions == null) {
				sb.append("null");
			} else {
				sb.append(this.ownerFilterOptions);
			}
			first = false;
		}
		if (isSetProjects()) {
			if (!first) sb.append(", ");
			sb.append("projects:");
			if (this.projects == null) {
				sb.append("null");
			} else {
				sb.append(this.projects);
			}
			first = false;
		}
		if (isSetClients()) {
			if (!first) sb.append(", ");
			sb.append("clients:");
			if (this.clients == null) {
				sb.append("null");
			} else {
				sb.append(this.clients);
			}
			first = false;
		}
		if (isSetMaxUnansweredQuestions()) {
			if (!first) sb.append(", ");
			sb.append("maxUnansweredQuestions:");
			sb.append(this.maxUnansweredQuestions);
			first = false;
		}
		if (isSetStats()) {
			if (!first) sb.append(", ");
			sb.append("stats:");
			if (this.stats == null) {
				sb.append("null");
			} else {
				sb.append(this.stats);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}