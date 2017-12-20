package com.workmarket.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class JedisConfig {
	@Value("${redis.persistent.hostname}")
	private String HOSTNAME;

	@Value("${redis.persistent.port}")
	private int PORT;

	@Value("${redis.persistent.database}")
	private int DATABASE;

	@Value("${redis.persistent.timeout}")
	private int TIMEOUT;

	@Bean
	JedisPool getJedisPool() {
			return new JedisPool(new JedisPoolConfig(),HOSTNAME, PORT, TIMEOUT, null, DATABASE);
	}
}
