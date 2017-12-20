package com.workmarket.service.featuretoggle;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.feature.FeatureToggleClient;
import com.workmarket.feature.gen.Messages;
import com.workmarket.feature.gen.Messages.DimensionValuePair;
import com.workmarket.feature.gen.Messages.FeatureToggles;
import com.workmarket.feature.vo.FeatureToggleAndStatus;
import com.workmarket.service.business.UserServiceImpl;
import com.workmarket.service.web.WebRequestContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;
import rx.functions.Action1;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link FeatureEntitlementService} Test(s).
 */
@RunWith(MockitoJUnitRunner.class)
public class FeatureEntitlementServiceTest {
	@Mock
	private FeatureToggleClient client;

	@Mock
	private WebRequestContextProvider requestContext;

	@Mock
	private UserServiceImpl userService;

	@Mock
	private MetricRegistry metricRegistry;

	@InjectMocks
	private FeatureEntitlementService entitlementService;

	@Before
	public void setup() {
		when(metricRegistry.meter(anyString())).thenReturn(new Meter());
		entitlementService.init();
	}

	@Test
	public void testGettingSingleFeatureToggleWithExceptionInClient() {
		String expectedErrorMessage = "error in FeatureToggleClient";
		when(client.get(anyString(), any(Collection.class), any(RequestContext.class)))
				.thenThrow(new IllegalStateException(expectedErrorMessage));

		final User user = mock(User.class);
		when(user.getUuid()).thenReturn("uuid!");
		final Company company = mock(Company.class);
		when(user.getCompany()).thenReturn(company);
		when(company.getUuid()).thenReturn("company-uuid");
		final AtomicReference<Throwable> error = new AtomicReference<>();

		entitlementService.getFeatureToggle(user, "toggle")
				.subscribe(
						new Action1<FeatureToggleAndStatus>() {
							@Override
							public void call(FeatureToggleAndStatus toggles) {
								fail("Should not emit a result.");
							}
						}, new Action1<Throwable>() {
							@Override
							public void call(final Throwable throwable) {
								error.set(throwable);
							}
						});

		assertEquals(expectedErrorMessage, error.get().getMessage());
	}

	@Test
	public void testGettingFeatureTogglesByIdWithExceptionInUserService() {
		when(userService.findUserById(anyLong())).thenThrow(new IllegalStateException("foobar"));
		final AtomicReference<Throwable> errMsg = new AtomicReference<>();
		entitlementService.getFeatureToggles(1)
				.subscribe(
						new Action1<FeatureToggles>() {
							@Override
							public void call(FeatureToggles toggles) {
								fail("Should not emit a result.");
							}
						}, new Action1<Throwable>() {
							@Override
							public void call(final Throwable throwable) {
								errMsg.set(throwable);
							}
						});
		assertEquals("foobar", errMsg.get().getMessage());
	}

	@Test
	public void testGettingFeatureTogglesByIdWithErrorInClient() {
		final User user = mock(User.class);
		final Company company = mock(Company.class);
		when(client.getAllToggles(anyCollectionOf(DimensionValuePair.class), any(RequestContext.class)))
				.thenThrow(new IllegalStateException("foobar"));
		when(company.getUuid()).thenReturn("1234");
		when(user.getCompany()).thenReturn(company);
		when(user.getUuid()).thenReturn("4321");
		when(userService.findUserById(anyLong())).thenReturn(user);

		final AtomicReference<Throwable> errMsg = new AtomicReference<>();
		entitlementService.getFeatureToggles(1)
				.subscribe(
						new Action1<FeatureToggles>() {
							@Override
							public void call(FeatureToggles toggles) {
								fail("Should not emit a result.");
							}
						}, new Action1<Throwable>() {
							@Override
							public void call(final Throwable throwable) {
								errMsg.set(throwable);
							}
						});
		assertEquals("foobar", errMsg.get().getMessage());
	}

	@Test
	public void testGettingFeatureToggleByIdSpecificWithErrorInUserService() {
		when(userService.findUserById(anyLong())).thenThrow(new IllegalStateException("foobar"));
		final AtomicReference<Throwable> errMsg = new AtomicReference<>();
		entitlementService.getFeatureToggle(1L, "featureBaz")
				.subscribe(new Action1<FeatureToggleAndStatus>() {
					@Override
					public void call(final FeatureToggleAndStatus featureToggleAndStatus) {
						fail("should not return successfully");
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(final Throwable throwable) {
						errMsg.set(throwable);
					}
				});
		assertEquals("foobar", errMsg.get().getMessage());
	}

	@Test
	public void testGettingFeatureTogglesByIdSpecificWithErrorInClient() {
		final User user = mock(User.class);
		final Company company = mock(Company.class);
		when(client.get(eq("featureBaz"), anyCollectionOf(DimensionValuePair.class), any(RequestContext.class)))
				.thenThrow(new IllegalStateException("foobar"));
		when(company.getUuid()).thenReturn("1234");
		when(user.getCompany()).thenReturn(company);
		when(user.getUuid()).thenReturn("4321");
		when(userService.findUserById(anyLong())).thenReturn(user);

		final AtomicReference<Throwable> errMsg = new AtomicReference<>();
		entitlementService.getFeatureToggle(1L, "featureBaz")
				.subscribe(new Action1<FeatureToggleAndStatus>() {
					@Override
					public void call(final FeatureToggleAndStatus featureToggleAndStatus) {
						fail("should not return successfully");
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(final Throwable throwable) {
						errMsg.set(throwable);
					}
				});
		assertEquals("foobar", errMsg.get().getMessage());
	}

	@Test
	public void testHasFeatureToggleValueTrueWithErrorInUserService() {
		when(userService.findUserById(anyLong())).thenThrow(new IllegalStateException("foobar"));

		assertFalse(entitlementService.hasFeatureToggle(1L, "featureBaz"));
	}

	@Test
	public void testHasFeatureToggleValueTrueWithErrorInClient() {
		final User user = mock(User.class);
		final Company company = mock(Company.class);
		when(client.get(eq("featureBaz"), anyCollectionOf(DimensionValuePair.class), any(RequestContext.class)))
				.thenThrow(new IllegalStateException("foobar"));
		when(company.getUuid()).thenReturn("1234");
		when(user.getCompany()).thenReturn(company);
		when(user.getUuid()).thenReturn("4321");
		when(userService.findUserById(anyLong())).thenReturn(user);

		assertFalse(entitlementService.hasFeatureToggle(1L, "featureBaz"));
	}

	@Test
	public void testHasFeatureToggleValueTrueNoErrors() {
		final User user = mock(User.class);
		final Company company = mock(Company.class);
		when(client.get(eq("featureBaz"), anyCollectionOf(DimensionValuePair.class), any(RequestContext.class)))
				.thenReturn(Observable.just(
						new FeatureToggleAndStatus(
								Messages.Status.getDefaultInstance(), Messages.FeatureToggle.newBuilder().setValue("true").build())));
		when(company.getUuid()).thenReturn("1234");
		when(user.getCompany()).thenReturn(company);
		when(user.getUuid()).thenReturn("4321");
		when(userService.findUserById(anyLong())).thenReturn(user);

		assertTrue(entitlementService.hasFeatureToggle(1L, "featureBaz"));
	}

	@Test
	public void testHasFeatureToggleFalseOnNullUserId() {
		final User user = mock(User.class);
		final Company company = mock(Company.class);
		when(client.get(eq("featureBaz"), anyCollectionOf(DimensionValuePair.class), any(RequestContext.class)))
				.thenReturn(Observable.just(
						new FeatureToggleAndStatus(
								Messages.Status.getDefaultInstance(), Messages.FeatureToggle.newBuilder().setValue("true").build())));
		when(company.getUuid()).thenReturn("1234");
		when(user.getCompany()).thenReturn(company);
		when(user.getUuid()).thenReturn("4321");
		when(userService.findUserById(anyLong())).thenReturn(null);

		assertFalse(entitlementService.hasFeatureToggle(null, "featureBaz"));
	}

	@Test
	public void testGeneratingQueryDimensionsFromUser() {
		final User user = new User();
		user.setUuid("1234");

		final Company company = new Company();
		company.setUuid("4321");
		user.setCompany(company);

		final List<DimensionValuePair> dimensionValuePairs =
				FeatureEntitlementService.genDimensionQueriesFromUser(user);
		asserter(dimensionValuePairs, "4321", "1234");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGeneratingQueryDimensionsFromNullUser() {
		FeatureEntitlementService.genDimensionQueriesFromUser(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGeneratingQueryDimensionsFromUserWithNullCompany() {
		final User user = new User();
		user.setUuid("1234");

		FeatureEntitlementService.genDimensionQueriesFromUser(user);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGeneratingQueryDimensionsFromUserWithMissingCompanyUuid() {
		final User user = new User();
		user.setUuid("1234");
		user.setCompany(new Company());

		FeatureEntitlementService.genDimensionQueriesFromUser(user);
	}

	private void asserter(final List<DimensionValuePair> dimensions, final String companyUuid, final String userUuid) {
		assertEquals(2, dimensions.size());

		boolean companyFound = false;
		boolean userFound = false;
		for (final DimensionValuePair pair : dimensions) {
			switch (pair.getDimension()) {
				case COMPANY:
					assertFalse(companyFound);
					companyFound = true;
					assertEquals(companyUuid, pair.getObjectId());
					break;
				case USER:
					assertFalse(userFound);
					userFound = true;
					assertEquals(userUuid, pair.getObjectId());
					break;
				default:
					fail("Got dimension " + pair.getDimension());
			}
		}
	}
}
