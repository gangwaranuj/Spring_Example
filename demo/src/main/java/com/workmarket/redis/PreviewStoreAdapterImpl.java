package com.workmarket.redis;

import com.google.api.client.util.Lists;
import com.google.common.base.Optional;
import com.workmarket.api.v2.employer.uploads.models.PreviewDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

@Component
public class PreviewStoreAdapterImpl implements PreviewStoreAdapter {
	private static final Log logger = LogFactory.getLog(PreviewStoreAdapterImpl.class);

	private RedisTemplate<String, PreviewDTO> pushTemplate;
	private RedisTemplate<String, PreviewDTO.Builder> pullTemplate;

	@Autowired
	public PreviewStoreAdapterImpl(
		RedisTemplate<String, PreviewDTO> pushTemplate,
		RedisTemplate<String, PreviewDTO.Builder> pullTemplate
	) {
		this.pushTemplate = pushTemplate;
		this.pullTemplate = pullTemplate;
	}

	@Override
	public void add(String key, PreviewDTO previewDTO) {
		try {
			pushTemplate.opsForList().rightPush(key, previewDTO);
		} catch (Exception e) {
			logger.error("[redis] Error adding item to list: ", e);
		}
	}

	@Override
	public void addAll(String key, List<PreviewDTO> previewDTOs) {
		try {
			pushTemplate.opsForList().rightPushAll(key,  previewDTOs.toArray(new PreviewDTO[previewDTOs.size()]));
		} catch (Exception e) {
			logger.error("[redis] Error adding items to list: ", e);
		}
	}

	@Override
	public Optional<PreviewDTO> get(String key, long index) {
		Assert.hasText(key);
		try {
			return Optional.fromNullable(pullTemplate.opsForList().index(key, index).build());
		} catch (Exception e) {
			logger.error("[redis] Error fetching item from list: ", e);
		}
		return Optional.absent();
	}

	@Override
	public List<PreviewDTO> subList(String key, long startIndex, long endIndex) {
		List<PreviewDTO> previewDTOs = Lists.newArrayList();
		try {
			List<PreviewDTO.Builder> builders = pullTemplate.opsForList().range(key, startIndex, endIndex);
			for (PreviewDTO.Builder builder : builders) {
				previewDTOs.add(builder.build());
			}
		} catch (Exception e) {
			logger.error("[redis] Error fetching items from list: ", e);
		}
		return previewDTOs;
	}

	@Override
	public void set(String key, long index, PreviewDTO previewDTO) {
		try {
			pushTemplate.opsForList().set(key, index, previewDTO);
		} catch (Exception e) {
			logger.error("[redis] Error setting item in list: ", e);
		}
	}

	@Override
	public void destroy(String key) {
		try {
			pushTemplate.delete(key);
		} catch (Exception e) {
			logger.error("[redis] Error destroying list: ", e);
		}
	}

	@Override
	public long size(String key) {
		try {
			return pullTemplate.opsForList().size(key);
		} catch (Exception e) {
			logger.error("[redis] Error sizing list: ", e);
		}
		return 0;
	}

	@Override
	public boolean isEmpty(String key) {
		try {
			// redis automatically destroy keys with empty lists
			return pullTemplate.hasKey(key);
		} catch (Exception e) {
			logger.error("[redis] Error accessing list: ", e);
		}
		return true;
	}

	@Override
	public void expire(String key, long seconds) {
		pushTemplate.expire(key, seconds, SECONDS);
	}
}
