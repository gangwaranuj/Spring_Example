package com.workmarket.domains.model.kpi;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class KPIAggregateEntityTable implements Serializable {

	private static final long serialVersionUID = -3799197974619004608L;

	private long entityId;
	private String entityName;
	private List<DataPoint> dataPoints;

	public KPIAggregateEntityTable() {}

	public KPIAggregateEntityTable(long entityId, String entityName, List<DataPoint> dataPoints) {
		this.entityId = entityId;
		this.entityName = entityName;
		this.dataPoints = dataPoints;
	}

	public long getEntityId() {
		return this.entityId;
	}

	public KPIAggregateEntityTable setEntityId(long entityId) {
		this.entityId = entityId;
		return this;
	}

	public boolean isSetEntityId() {
		return (entityId > 0L);
	}

	public String getEntityName() {
		return this.entityName;
	}

	public KPIAggregateEntityTable setEntityName(String entityName) {
		this.entityName = entityName;
		return this;
	}

	public boolean isSetEntityName() {
		return this.entityName != null;
	}

	public int getDataPointsSize() {
		return (this.dataPoints == null) ? 0 : this.dataPoints.size();
	}

	public java.util.Iterator<DataPoint> getDataPointsIterator() {
		return (this.dataPoints == null) ? null : this.dataPoints.iterator();
	}

	public void addToDataPoints(DataPoint elem) {
		if (this.dataPoints == null) {
			this.dataPoints = new ArrayList<DataPoint>();
		}
		this.dataPoints.add(elem);
	}

	public List<DataPoint> getDataPoints() {
		return this.dataPoints;
	}

	public KPIAggregateEntityTable setDataPoints(List<DataPoint> dataPoints) {
		this.dataPoints = dataPoints;
		return this;
	}

	public boolean isSetDataPoints() {
		return this.dataPoints != null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("KPIAggregateEntityTable(");
		boolean first = true;

		sb.append("entityId:");
		sb.append(this.entityId);
		first = false;
		if (!first) sb.append(", ");
		sb.append("entityName:");
		if (this.entityName == null) {
			sb.append("null");
		} else {
			sb.append(this.entityName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("dataPoints:");
		if (this.dataPoints == null) {
			sb.append("null");
		} else {
			sb.append(this.dataPoints);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof KPIAggregateEntityTable)) {
			return false;
		}

		KPIAggregateEntityTable table = (KPIAggregateEntityTable) o;

		return new EqualsBuilder()
			.append(entityId, table.getEntityId())
			.append(dataPoints, table.getDataPoints())
			.append(entityName, table.getEntityName())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(entityId)
			.append(dataPoints)
			.append(entityName)
			.toHashCode();
	}
}

