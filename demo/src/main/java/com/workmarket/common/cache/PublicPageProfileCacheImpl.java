package com.workmarket.common.cache;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.workmarket.redis.RedisFilters;
import com.workmarket.configuration.Constants;
import com.workmarket.dto.PublicPageProfileDTO;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.beanutils.BeanUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PublicPageProfileCacheImpl implements PublicPageProfileCache {

	private static final Log logger = LogFactory.getLog(PublicPageProfileCacheImpl.class);

	@Autowired private JedisPool jedisPool;

	private static int A_DAY_IN_SECONDS = Constants.DAY_IN_SECONDS.intValue();

	@Override
	public List<PublicPageProfileDTO> putNewPublicPageProfiles(String industry, List<PublicPageProfileDTO> profiles) {
		Jedis jedis = null;

		try {
			jedis = jedisPool.getResource();
			Pipeline pipeline = jedis.pipelined();
			Map<String,String> properties;
			String industrySetKey = RedisFilters.publicPageProfileIndustryKey(industry);
			for (PublicPageProfileDTO profile : profiles) {
				try {
					properties = BeanUtilsBean.getInstance().describe(profile);
				} catch (InvocationTargetException|NoSuchMethodException|IllegalAccessException e) {
					logger.error("", e);
					continue;
				}
				String userCacheKey = RedisFilters.publicPageProfileData(profile.getUserNumber());
				pipeline.hmset(userCacheKey, properties);
				pipeline.expire(userCacheKey, A_DAY_IN_SECONDS);
				// When we update jedis to at least v2.2.0 we can store the userCache keys in a collection and then perform one sadd operation outside the loop
				pipeline.sadd(industrySetKey, userCacheKey);
			}
			pipeline.expire(industrySetKey, A_DAY_IN_SECONDS);
			pipeline.sync();
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			jedisPool.returnResource(jedis);
		}

		return profiles;
	}

	@Override
	public Multimap<String,PublicPageProfileDTO> getPublicPageProfiles(List<String> industries, int numberOfProfiles) {
		Jedis jedis = null;
		Multimap<String,PublicPageProfileDTO> results = ArrayListMultimap.create();

		try {
			jedis = jedisPool.getResource();
			Pipeline pipeline = jedis.pipelined();
			for(String industry : industries) {
				// Ideally we wouldn't need to randomize and truncate the set of keys ourselves
				// We should be using srandmembers(key, [count]), available in jedis 2.2.0
				// Since jedis 2.2.0 breaks compatibility with spring-data-redis 1.1.0, we're stuck doing it ourselves
				// Open Jira Ticket: https://jira.springsource.org/browse/DATAREDIS-237
				Set<String> profileKeysSet = jedis.smembers(RedisFilters.publicPageProfileIndustryKey(industry));
				List<String> profileKeys = Lists.newArrayList(profileKeysSet);
				profileKeys = CollectionUtilities.randomizeAndTruncate(profileKeys, numberOfProfiles);
				List<Response<Map<String,String>>> responseList = Lists.newArrayList();
				for (String key : profileKeys) {
					responseList.add(pipeline.hgetAll(key));
				}
				pipeline.sync();
				for (Response<Map<String,String>> response : responseList) {
					try {
						PublicPageProfileDTO pageProfileDTO = new PublicPageProfileDTO();
						BeanUtils.populate(pageProfileDTO, response.get());
						if (BeanUtilities.hasAnyNullProperties(pageProfileDTO)) {
							// One of the profiles is missing a property, cache is invalid
							logger.info("Public profile cache was corrupt, missing property or cache miss on accessing user profile data, "
									+ pageProfileDTO.toString());
							results.removeAll(industry);
							break;
						}
						results.put(industry,pageProfileDTO);
					} catch (InvocationTargetException|IllegalAccessException e) {
						logger.error("", e);
					}
				}
				Collection<PublicPageProfileDTO> resultsForIndustry = results.get(industry);
				if (resultsForIndustry.size() != numberOfProfiles && CollectionUtils.isNotEmpty(resultsForIndustry)) {
					// Cache for this industry doesn't hold enough profiles for the public site, cache is invalid
					logger.info("Public profile cache doesn't hold enough profiles for the public site, industry: " + industry);
					results.removeAll(industry);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			jedisPool.returnResource(jedis);
		}

		return results;
	}
}
