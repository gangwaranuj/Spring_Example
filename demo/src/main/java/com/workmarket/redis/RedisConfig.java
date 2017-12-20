package com.workmarket.redis;

import com.google.common.collect.ImmutableMap;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class RedisConfig {
	@Value("${redis.persistent.hostname}")
	private String PERSISTENT_HOSTNAME;
	@Value("${redis.persistent.port}")
	private String PERSISTENT_PORT;
	@Value("${redis.persistent.database}")
	private String PERSISTENT_DATABASE;

	@Value("${redis.cacheonly.hostname}")
	private String CACHEONLY_HOSTNAME;
	@Value("${redis.cacheonly.port}")
	private String CACHEONLY_PORT;
	@Value("${redis.cacheonly.database}")
	private String CACHEONLY_DATABASE;

	public static final String
		// STATIC CACHES
		INDUSTRIES = "industries:v1",
		UNIQUE_ACTIVE_CALLING_CODE_IDS = "uniqueActiveCallingCodeIds:v1",
		LOCATION_TYPES = "locationTypes:v1",
		STATES = "states:v1",
		COUNTRIES = "countries:v1",
		NATIONAL_IDS = "nationalIds:v1",

		// DYNAMIC CACHES
		COMPANY_NETWORK_IDS = "companyNetworkIds:v1:",
		INDUSTRIES_FOR_PROFILE = "industriesForProfile:v1:",
		PROFILE = "profile:v3:",
		AVERAGE_USER_RATING_FOR_COMPANY = "averageUserRatingForCompany:v1:",
		GEOCODE = "geocode:v1:",
		SEARCH_USER = "searchUser:v1:",
		BLOCKED_USER_IDS = "blockedUserIds:v1:",
		WORK_TEMPLATE = "workTemplate:v1:",
		POSTAL_CODE_DISTANCE = "postalCodeDistance:v1:";

	private static final long EIGHT_HOURS_IN_SECONDS = TimeUnit.HOURS.toSeconds(8);
	private static final long TEN_DAYS_IN_SECONDS = TimeUnit.DAYS.toSeconds(10);
	private static final Map<String, Long> EXPIRY_OVERRIDES = ImmutableMap.<String, Long>builder()
		.put(INDUSTRIES, TEN_DAYS_IN_SECONDS)
		.put(SEARCH_USER, TEN_DAYS_IN_SECONDS)
		.put(LOCATION_TYPES, TEN_DAYS_IN_SECONDS)
		.put(STATES, TEN_DAYS_IN_SECONDS)
		.put(AVERAGE_USER_RATING_FOR_COMPANY, TEN_DAYS_IN_SECONDS)
		.build();

	@Bean
	@Qualifier("redisCacheOnly")
	RedisAdapter redisAdapterCacheOnly() {
		return new RedisAdapterImpl(redisTemplateCacheOnly());
	}

	@Bean
	@Primary
	@Qualifier("redisPersistent")
	RedisAdapter redisAdapterPersistent() {
		return new RedisAdapterImpl(redisTemplatePersistent());
	}

	@Bean
	PreviewStoreAdapter previewStoreAdapter() {
		return new PreviewStoreAdapterImpl(
			redisPushPreviewTemplate(),
			redisPullPreviewTemplate()
		);
	}

	@Bean
	JedisConnectionFactory persistentJedisConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setHostName(PERSISTENT_HOSTNAME);
		jedisConnectionFactory.setPort(Integer.valueOf(PERSISTENT_PORT));
		jedisConnectionFactory.setDatabase(Integer.valueOf(PERSISTENT_DATABASE));
		return jedisConnectionFactory;
	}

	@Bean
	JedisConnectionFactory cacheOnlyJedisConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setHostName(CACHEONLY_HOSTNAME);
		jedisConnectionFactory.setPort(Integer.valueOf(CACHEONLY_PORT));
		jedisConnectionFactory.setDatabase(Integer.valueOf(CACHEONLY_DATABASE));
		return jedisConnectionFactory;
	}

	@Bean
	RedisTemplate<String, Object> redisTemplateCacheOnly() {
		final RedisTemplate<String, Object> template =  new RedisTemplate<>();
		template.setConnectionFactory(cacheOnlyJedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		return template;
	}

	@Bean
	@Primary
	RedisTemplate<String, Object> redisTemplatePersistent() {
		final RedisTemplate<String, Object> template =  new RedisTemplate<>();
		template.setConnectionFactory(persistentJedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
		template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
		return template;
	}

	@Bean
	RedisTemplate<String, PreviewDTO> redisPushPreviewTemplate() {
		final RedisTemplate<String, PreviewDTO> template =  new RedisTemplate<>();
		template.setConnectionFactory(persistentJedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new JacksonJsonRedisSerializer<>(PreviewDTO.class));
		return template;
	}

	@Bean
	RedisTemplate<String, PreviewDTO.Builder> redisPullPreviewTemplate() {
		final RedisTemplate<String, PreviewDTO.Builder> template =  new RedisTemplate<>();
		template.setConnectionFactory(persistentJedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new JacksonJsonRedisSerializer<>(PreviewDTO.Builder.class));
		return template;
	}

	@Bean
	public CacheManager cacheManager() {
		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplateCacheOnly());
		cacheManager.setDefaultExpiration(EIGHT_HOURS_IN_SECONDS);
		cacheManager.setExpires(EXPIRY_OVERRIDES);
		return cacheManager;
	}
}
