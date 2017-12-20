package com.workmarket.domains.work.cache;

import com.google.api.client.util.Maps;
import com.google.common.base.Optional;
import com.workmarket.BaseUnitTest;
import com.workmarket.domains.work.model.part.ShippingProvider;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.dto.PartDTO;
import com.workmarket.service.external.TrackingStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartCacheImplTest extends BaseUnitTest {

	@Mock RedisAdapter redisAdapter;
	@InjectMocks PartCacheImpl partCache;

	PartDTO partDTO;
	Map<String, String> mapOfPartDTO;

	private static final String PART_ID = ANY_STRING;

	private static final String KEY = RedisFilters.partKey(PART_ID),
		SHIPPING_STATUS = "Sent",
		TRACKING_STATUS_PROPERTY = PartCacheImpl.TRACKING_STATUS_PROPERTY;

	private static final long FORTNIGHT_IN_SECONDS = PartCacheImpl.A_FORTNIGHT_IN_SECONDS;

	@Before
	public void setUp() {
		partDTO = new PartDTO();
		partDTO.setUuid(PART_ID);
		partDTO.setName(ANY_STRING);
		partDTO.setPartValue(BigDecimal.ONE);
		partDTO.setPartGroupId(ANY_LONG_2);
		partDTO.setPartGroupUuid(ANY_STRING_2);
		partDTO.setTrackingStatus(TrackingStatus.DELIVERED);
		partDTO.setShippingProvider(ShippingProvider.UPS);

		try {
			mapOfPartDTO = PartCacheImpl.getPropertiesAsMap(partDTO);
		} catch (InvocationTargetException|NoSuchMethodException|IllegalAccessException e) {
			mapOfPartDTO = Maps.newHashMap();
		}

		when(redisAdapter.getMap(KEY)).thenReturn(mapOfPartDTO);
	}

	@Test
	public void getPart_idAndCacheEmpty_cacheMiss() {
		Map<String, String> emptyMap = Maps.newHashMap();
		when(redisAdapter.getMap(KEY)).thenReturn(emptyMap);

		Optional<PartDTO> result = partCache.getPart(PART_ID);

		assertFalse(result.isPresent());
	}

	@Test
	public void getPart_id_cacheHit() {
		Optional<PartDTO> result = partCache.getPart(PART_ID);

		assertTrue(result.isPresent());
	}

	@Test
	public void getPart_id_mapCorrectlyTranslatedToDTO() {
		Optional<PartDTO> result = partCache.getPart(PART_ID);

		PartDTO resultPartDTO = result.get();

		assertTrue(partDTO.equals(resultPartDTO));
	}

	@Test
	public void putPart_partDTO_cacheSet() {
		partCache.putPart(partDTO);

		verify(redisAdapter).setAll(KEY, mapOfPartDTO, FORTNIGHT_IN_SECONDS);
	}

	@Test
	public void updateTrackingStatus_idAndStatus_cacheUpdated() {
		partCache.updateTrackingStatus(PART_ID, SHIPPING_STATUS);

		verify(redisAdapter).set(KEY, TRACKING_STATUS_PROPERTY, SHIPPING_STATUS, FORTNIGHT_IN_SECONDS);
	}
}
