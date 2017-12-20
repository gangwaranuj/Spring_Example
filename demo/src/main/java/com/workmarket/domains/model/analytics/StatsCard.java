package com.workmarket.domains.model.analytics;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

/**
 * User: iloveopt
 * Date: 11/17/14
 */
public class StatsCard<Enum> implements Serializable {

	private static final long serialVersionUID = -3022907531928262887L;
	private Map<Enum, Object> values = Maps.newLinkedHashMap();

	protected StatsCard() {
	}

	public Map<Enum, Object> getValues() {
		return values;
	}

	public void setValues(Map<Enum, Object> values) {
		this.values = values;
	}

	public StatsCard addToValues(Enum key, Object value) {
		if (key != null) {
			values.put(key, value);
		}
		return this;
	}

	public Map<String, Object> getValuesWithStringKey() {
		Map<String, Object> valuesWithStringKey = Maps.newLinkedHashMap();
		for (Map.Entry<Enum, Object> entry : values.entrySet()) {
			valuesWithStringKey.put(entry.getKey().toString(), entry.getValue());
		}
		return valuesWithStringKey;
	}

	public boolean isBad() {
		if (values == null) {
			return false;
		}
		Object isPastDueMoreThan3Days = values.get(CompanyStatsField.PAST_DUE_MORE_THAN_3_DAYS);
		return  isPastDueMoreThan3Days != null && (boolean) isPastDueMoreThan3Days;
	}


}
