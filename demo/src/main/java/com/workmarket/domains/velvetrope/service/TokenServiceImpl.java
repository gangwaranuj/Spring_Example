package com.workmarket.domains.velvetrope.service;

import com.google.api.client.util.Maps;
import com.google.common.collect.ImmutableMap;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.velvetrope.TokenService;
import com.workmarket.velvetrope.Venue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class TokenServiceImpl implements TokenService {
	public static final String VELVET_ROPE_TOKEN_KEY = "companyId:%s:velvetRopeToken";
	public static final String CHECKSUM_KEY = "checksum";
	public static final String TOKEN_KEY = "token";
	public static final long TWO_WEEKS_IN_SECONDS = TimeUnit.DAYS.toSeconds(14);

	@Autowired private RedisAdapter redis;
	@Autowired private AdmissionService admissionService;

	@Override
	public int tokenFor(Long companyId) {
		Map<String, String> token;
		try {
			token = redis.getMap(String.format(VELVET_ROPE_TOKEN_KEY, companyId));
		} catch (InvalidDataAccessApiUsageException e) {
			token = Maps.newHashMap();
		}

		if (token.get(CHECKSUM_KEY) != null && token.get(TOKEN_KEY) != null) {
			if (Long.valueOf(String.valueOf(token.get(CHECKSUM_KEY))).equals(Venue.CHECKSUM)) {
				return Integer.valueOf(String.valueOf(token.get(TOKEN_KEY)));
			}
		}

		List<Admission> admissions = admissionService.findAllAdmissionsByCompanyIdForVenue(companyId, Venue.values());
		int calculatedToken = 0;
		for (Admission admission : admissions) {
			if (!admission.getDeleted()) {
				calculatedToken |= admission.mask();
			}
		}

		if (calculatedToken != 0) {
			cacheToken(companyId, calculatedToken);
			return calculatedToken;
		}

		return Venue.LOBBY.id();
	}

	@Override
	public void cacheToken(Long companyId, int token) {
		Map<String, String> tokenMap = ImmutableMap.of(
			CHECKSUM_KEY, String.valueOf(Venue.CHECKSUM),
			TOKEN_KEY, String.valueOf(token)
		);
		redis.set(String.format(VELVET_ROPE_TOKEN_KEY, companyId), tokenMap, TWO_WEEKS_IN_SECONDS);
	}

	@Override
	public void deleteTokenFor(Long companyId) {
		redis.delete(String.format(VELVET_ROPE_TOKEN_KEY, companyId));
	}
}
