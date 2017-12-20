package com.workmarket.domains.authentication.features;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.data.aggregate.FeatureAggregate;
import com.workmarket.utility.StringUtilities;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@ManagedResource(objectName="bean:name=features", description="Deliver features to specific or random user segments")
public class FeatureEvaluatorConfiguration implements InitializingBean {
	private List<String> supportedUserProperties = new ArrayList<>();
	private Map<String,Feature> features = Maps.newHashMap();

	@Resource(name = "readOnlyJdbcTemplate")
	@Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public void afterPropertiesSet() throws Exception {
		reload();
	}

	public List<String> getSupportedUserProperties() {
		return supportedUserProperties;
	}

	public Feature get(String key) {
		return features.get(key);
	}

	@ManagedOperation(description="Set entitlements for a feature to a segment of users")
	@ManagedOperationParameters({
		@ManagedOperationParameter(name = "key", description = "Feature"),
		@ManagedOperationParameter(name = "segment", description = "Segment"),
		@ManagedOperationParameter(name = "value", description = "Values")
	})
	public void put(String key, String segment, String value) {
		Feature f;
		if (features.containsKey(key)) {
			f = features.get(key);
		} else {
			f = new Feature(key);
			features.put(key, f);
		}

		if (StringUtils.isEmpty(segment)) {
			f.setEnabled(BooleanUtils.toBoolean(value));
		} else {
			EntitledSegment<?> entitledSegment = detectEntitledSegment(value);
			f.addSegment(segment, entitledSegment);
		}
	}

	@ManagedOperation(description="Get all feature entitlements")
	public String get() {
		StringBuffer ret = new StringBuffer();
		for (String key : features.keySet()) {
			ret.append(features.get(key).toString());
		}
		return ret.toString();
	}

	@ManagedOperation(description="Remove feature definition")
	@ManagedOperationParameters({
		@ManagedOperationParameter(name = "key", description = "Feature")
	})
	public String remove(String key) {
		if (features.containsKey(key)) {
			features.remove(key);
			return "Removed " + key;
		} else {
			return key + " feature does not exist.";
		}
	}

	// We only support Long and String right now
	private EntitledSegment<?> detectEntitledSegment(String value) {
		List<String> strings = extractStrings(value);
		List<Long> longs = extractLongs(value);
		if (longs.size() > 0 && longs.get(0) != null) {
			return new EntitledSegment<>(longs);
		} else {
			return new EntitledSegment<>(strings);
		}
	}

	private List<String> extractStrings(String value) {
		if (StringUtils.isBlank(value)) { return new ArrayList<>(); }
		return Lists.newArrayList(Splitter.onPattern("[\\s,]+")
				.trimResults()
				.omitEmptyStrings()
				.split(value));
	}

	private List<Long> extractLongs(String value) {
		return Lists.transform(extractStrings(value), new Function<String, Long>() {
			@Override
			public Long apply(@Nullable String s) {
				return StringUtilities.parseLong(s);
			}
		});
	}

	public void putAll(List<FeatureAggregate> results) {
		for (FeatureAggregate feature : results) {
			if (features.get(feature.getFeatureName()) == null) {
				put(feature.getFeatureName(), null, feature.isAllowed()?"true":"false");
			}

			if (feature.getSegmentName() != null) {
				put(feature.getFeatureName(), feature.getSegmentName(), feature.getReferenceValue());
			}
		}
	}

	public void clear() {
		features.clear();
	}

	@ManagedOperation(description="Reload configuration from properties")
	public void reload() throws IOException {
		SQLBuilder sql = new SQLBuilder();
		sql
			.addColumns(
				"f.feature_name as featureName",
				"f.is_allowed as isAllowed",
				"fs.segment_name as segmentName",
				"group_concat(fsr.reference_value separator ',') as referenceValue"
			)
			.addJoin("left join feature_segment fs on f.id = fs.feature_id")
			.addJoin("left join feature_segment_reference fsr on fs.id = fsr.feature_segment_id")
			.addTable("feature f")
			.addGroupColumns("featureName", "segmentName");

		RowMapper<FeatureAggregate> mapper = new RowMapper<FeatureAggregate>() {
			public FeatureAggregate mapRow(ResultSet rs, int rowNum) throws SQLException {
				FeatureAggregate feature = new FeatureAggregate();

				feature.setFeatureName(rs.getString("featureName"));
				feature.setAllowed(rs.getBoolean("isAllowed"));
				feature.setSegmentName(rs.getString("segmentName"));
				feature.setReferenceValue(rs.getString("referenceValue"));

				return feature;
			}
		};

		List<FeatureAggregate> results = jdbcTemplate.query(sql.build(), sql.getParams(), mapper);

		clear();
		putAll(results);
	}
}
