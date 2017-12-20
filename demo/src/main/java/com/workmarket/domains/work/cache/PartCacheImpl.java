package com.workmarket.domains.work.cache;

import com.google.common.base.Optional;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.dto.PartDTO;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Service
public class PartCacheImpl implements PartCache {
	private static final Log logger = LogFactory.getLog(PartCacheImpl.class);

	@Autowired RedisAdapter redisAdapter;

	public static final long A_FORTNIGHT_IN_SECONDS = 1209999L;
	public static final String TRACKING_STATUS_PROPERTY = "trackingStatus";
	private final BeanUtilsBean BEAN_UTILS_BEAN;

	public PartCacheImpl() {
		BEAN_UTILS_BEAN = new BeanUtilsBean(new ConvertUtilsBean() {
			@Override
			public Object convert(String value, Class clazz) {
				if (clazz.isEnum()) {
					return Enum.valueOf(clazz, value);
				}
				return super.convert(value, clazz);
			}
		});
		LongConverter converter = new LongConverter(null);
		BEAN_UTILS_BEAN.getConvertUtils().register(converter, Long.class);
	}

	@Override
	public Optional<PartDTO> getPart(String id) {
		final String key = RedisFilters.partKey(id);
		final Map<String, String> result = redisAdapter.getMap(key);
		Optional<PartDTO> absentResponse = Optional.absent();
		PartDTO partDTO = new PartDTO();

		if (result == null || result.isEmpty()) {
			return absentResponse;
		} else {
			try {
				populateDTOWithProperties(partDTO, result);
			} catch (InvocationTargetException|IllegalAccessException e) {
				logger.error("Error occurred retrieving PartWithTracking with id: " + id, e);
				return absentResponse;
			}
		}
		return Optional.of(partDTO);
	}

	@Override
	public PartDTO putPart(PartDTO partDTO) {
		final String key = RedisFilters.partKey(partDTO.getUuid());

		try {
			final Map<String, String> partMap = getPropertiesAsMap(partDTO);
			redisAdapter.setAll(key, partMap, A_FORTNIGHT_IN_SECONDS);
		} catch (InvocationTargetException|NoSuchMethodException|IllegalAccessException e) {
			logger.error("Error occurred putting PartWithTracking with id: " + partDTO.getId(), e);
			return null;
		}

		return partDTO;
	}

	@Override
	public void updateTrackingStatus(String uuid, String status) {
		final String key = RedisFilters.partKey(uuid);
		redisAdapter.set(key, TRACKING_STATUS_PROPERTY, status, A_FORTNIGHT_IN_SECONDS);
	}

	@Override
	public void deletePart(String uuid) {
		final String key = RedisFilters.partKey(uuid);
		redisAdapter.delete(key);
	}

	public static Map<String, String> getPropertiesAsMap(PartDTO partDTO) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		final Map<String, String> partMap = BeanUtils.describe(partDTO);
		for (Map.Entry<String, String> entry : partMap.entrySet()) {
			if (entry.getValue() == null) {
				entry.setValue("");
			}
		}
		return partMap;
	}

	public void populateDTOWithProperties(PartDTO partDTO, Map<String, String> properties) throws InvocationTargetException, IllegalAccessException {
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			if (entry.getValue().equals("")) {
				entry.setValue(null);
			}
		}
		BEAN_UTILS_BEAN.populate(partDTO, properties);
	}
}
