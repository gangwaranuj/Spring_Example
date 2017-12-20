package com.workmarket.domains.velvetrope.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.velvetrope.TokenService;
import com.workmarket.velvetrope.Venue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TokenServiceImplTest {
	public static final long SOME_COMPANY_ID = 99999L;
	public static final int TOKEN = 12345;
	private static final String STALE_CHECKSUM = "1111111";

	@Mock RedisAdapter redis;
	@Mock AdmissionService admissionService;
	@InjectMocks TokenService service = new TokenServiceImpl();

	Admission admission;

	List<Admission> admissions;
	Map<String, String> tokenMap;

	@Before
	public void setUp() throws Exception {
		tokenMap = Maps.newHashMap();
		when(
			redis.getMap(String.format(TokenServiceImpl.VELVET_ROPE_TOKEN_KEY, SOME_COMPANY_ID))
		).thenReturn(tokenMap);

		admission = mock(Admission.class);
		when(admission.mask()).thenReturn(Venue.LOBBY.mask() + Venue.SHARED_GROUPS.mask());

		admissions = Lists.newArrayList();
		when(admissionService.findAllAdmissionsByCompanyIdForVenue(SOME_COMPANY_ID, Venue.values())).thenReturn(admissions);
	}

	@Test
	public void tokenFor_WithCompanyId_getsCachedToken() {
		service.tokenFor(SOME_COMPANY_ID);
		verify(redis).getMap(String.format(TokenServiceImpl.VELVET_ROPE_TOKEN_KEY, SOME_COMPANY_ID));
	}

	@Test
	public void tokenFor_WithCompanyId_ContinuesWhenInvalidDataAccessApiUsageExceptionIsThrown() {
		when(
			redis.getMap(String.format(TokenServiceImpl.VELVET_ROPE_TOKEN_KEY, SOME_COMPANY_ID))
		).thenThrow(InvalidDataAccessApiUsageException.class);

		service.tokenFor(SOME_COMPANY_ID);
	}

	@Test
	public void getToken_WhenCachedAndValid_ReturnsTheToken() {
		// Valid checksum
		tokenMap.put(TokenServiceImpl.CHECKSUM_KEY, String.valueOf(Venue.CHECKSUM));
		tokenMap.put(TokenServiceImpl.TOKEN_KEY, String.valueOf(TOKEN));
		int token = service.tokenFor(SOME_COMPANY_ID);
		assertThat(token, is(TOKEN));
	}

	@Test
	public void getToken_WhenCachedAndValid_NeverFindsAllAdmissionsForCompanyId() {
		// Valid checksum
		tokenMap.put(TokenServiceImpl.CHECKSUM_KEY, String.valueOf(Venue.CHECKSUM));
		tokenMap.put(TokenServiceImpl.TOKEN_KEY, String.valueOf(TOKEN));
		service.tokenFor(SOME_COMPANY_ID);
		verify(admissionService, never()).findAllAdmissionsForCompanyId(SOME_COMPANY_ID);
	}

	@Test
	public void tokenFor_WhenTokenNotCached_findsAllAdmissionsForCompanyId() {
		service.tokenFor(SOME_COMPANY_ID);
		verify(admissionService).findAllAdmissionsByCompanyIdForVenue(SOME_COMPANY_ID, Venue.values());
	}

	@Test
	public void tokenFor_WhenCachedWithNullToken_findsAllAdmissionsForCompanyId() {
		tokenMap.put(TokenServiceImpl.CHECKSUM_KEY, String.valueOf(Venue.CHECKSUM));
		service.tokenFor(SOME_COMPANY_ID);
		verify(admissionService).findAllAdmissionsByCompanyIdForVenue(SOME_COMPANY_ID, Venue.values());
	}

	@Test
	public void tokenFor_WhenCachedWithNullChecksum_findsAllAdmissionsForCompanyId() {
		tokenMap.put(TokenServiceImpl.TOKEN_KEY, String.valueOf(TOKEN));
		service.tokenFor(SOME_COMPANY_ID);
		verify(admissionService).findAllAdmissionsByCompanyIdForVenue(SOME_COMPANY_ID, Venue.values());
	}

	@Test
	public void tokenFor_WhenCachedWithStaleChecksum_findsAllAdmissionsForCompanyId() {
		tokenMap.put(TokenServiceImpl.TOKEN_KEY, String.valueOf(TOKEN));
		tokenMap.put(TokenServiceImpl.CHECKSUM_KEY, String.valueOf(STALE_CHECKSUM));
		service.tokenFor(SOME_COMPANY_ID);
		verify(admissionService).findAllAdmissionsByCompanyIdForVenue(SOME_COMPANY_ID, Venue.values());
	}

	@Test
	public void tokenFor_WhenTokenNotCachedAndAnAdmissionIsFound_getsTheAdmissionsMask() {
		admissions.add(admission);
		service.tokenFor(SOME_COMPANY_ID);
		verify(admission).mask();
	}

	@Test
	public void tokenFor_WhenTokenCachedWithNullTokenAndAnAdmissionIsFound_getsTheAdmissionsMask() {
		tokenMap.put(TokenServiceImpl.CHECKSUM_KEY, String.valueOf(Venue.CHECKSUM));
		admissions.add(admission);
		service.tokenFor(SOME_COMPANY_ID);
		verify(admission).mask();
	}

	@Test
	public void tokenFor_WhenTokenCachedWithNullChecksumAndAnAdmissionIsFound_getsTheAdmissionsMask() {
		tokenMap.put(TokenServiceImpl.TOKEN_KEY, String.valueOf(TOKEN));
		admissions.add(admission);
		service.tokenFor(SOME_COMPANY_ID);
		verify(admission).mask();
	}

	@Test
	public void tokenFor_WhenTokenCachedWithStaleChecksumAndAnAdmissionIsFound_getsTheAdmissionsMask() {
		tokenMap.put(TokenServiceImpl.TOKEN_KEY, String.valueOf(TOKEN));
		tokenMap.put(TokenServiceImpl.CHECKSUM_KEY, String.valueOf(STALE_CHECKSUM));
		admissions.add(admission);
		service.tokenFor(SOME_COMPANY_ID);
		verify(admission).mask();
	}

	@Test
	public void tokenFor_WhenTokenNotCachedAndAnAdmissionIsNotFound_doesNotGetTheAdmissionMask() {
		service.tokenFor(SOME_COMPANY_ID);
		verify(admission, never()).mask();
	}

	@Test
	public void tokenFor_WhenTokenCachedWithNullTokenAndAnAdmissionIsNotFound_doesNotGetTheAdmissionMask() {
		tokenMap.put(TokenServiceImpl.CHECKSUM_KEY, String.valueOf(Venue.CHECKSUM));
		service.tokenFor(SOME_COMPANY_ID);
		verify(admission, never()).mask();
	}

	@Test
	public void tokenFor_WhenTokenCachedWithNullChecksumAndAnAdmissionIsNotFound_doesNotGetTheAdmissionMask() {
		tokenMap.put(TokenServiceImpl.TOKEN_KEY, String.valueOf(TOKEN));
		service.tokenFor(SOME_COMPANY_ID);
		verify(admission, never()).mask();
	}

	@Test
	public void tokenFor_WhenTokenCachedWithStaleChecksumAndAnAdmissionIsNotFound_doesNotGetTheAdmissionMask() {
		tokenMap.put(TokenServiceImpl.TOKEN_KEY, String.valueOf(TOKEN));
		tokenMap.put(TokenServiceImpl.CHECKSUM_KEY, String.valueOf(STALE_CHECKSUM));
		service.tokenFor(SOME_COMPANY_ID);
		verify(admission, never()).mask();
	}

	@Test
	public void tokenFor_WhenTokenNotCachedAndAnAdmissionIsFound_cachesToken() {
		admissions.add(admission);
		service.tokenFor(SOME_COMPANY_ID);

		// Thanks mockito for making things easy!
		ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
		verify(redis).set(
			eq(String.format(TokenServiceImpl.VELVET_ROPE_TOKEN_KEY, SOME_COMPANY_ID)),
			mapCaptor.capture(),
			anyLong()
		);

		Map<String,String> map = mapCaptor.getValue();
		assertThat(map, hasEntry(TokenServiceImpl.TOKEN_KEY, String.valueOf(Venue.LOBBY.mask() + Venue.SHARED_GROUPS.mask())));
		assertThat(map, hasEntry(TokenServiceImpl.CHECKSUM_KEY, String.valueOf(Venue.CHECKSUM)));
	}

	@Test
	public void tokenFor_WhenTokenNotCachedAndAnAdmissionIsNotFound_doesNotCacheToken() {
		service.tokenFor(SOME_COMPANY_ID);
		verify(redis, never()).set(any(String.class), any(int.class));
	}

	@Test
	public void tokenFor_WhenTokenNotCachedAndAnAdmissionIsFound_returnsToken() {
		admissions.add(admission);
		int token = service.tokenFor(SOME_COMPANY_ID);
		assertThat(token, is(Venue.LOBBY.mask() + Venue.SHARED_GROUPS.mask()));
	}

	@Test
	public void tokenFor_WhenTokenNotCachedAndAnAdmissionIsNotFound_returnsDefaultToken() {
		int token = service.tokenFor(SOME_COMPANY_ID);
		assertThat(token, is(Venue.LOBBY.mask()));
	}
}
