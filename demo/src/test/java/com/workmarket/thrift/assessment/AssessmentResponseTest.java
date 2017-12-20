package com.workmarket.thrift.assessment;

import com.google.common.collect.ImmutableSet;
import org.apache.http.auth.AUTH;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentResponseTest {

	private Assessment assessment;
	private Set<RequestContext> requestContexts;
	private Set<AuthorizationContext> authorizationContexts;
	private Attempt latestAttempt;
	private Attempt requestedAttempt;

	@Before
	public void setup() {
		assessment = mock(Assessment.class);
		latestAttempt = mock(Attempt.class);
		requestedAttempt = mock(Attempt.class);

		authorizationContexts = new HashSet<>();
		requestContexts = new HashSet<>();
	}

	@Test
	public void mustHaveAuthorizedContexts() {
		authorizationContexts = ImmutableSet.of(AuthorizationContext.ADMIN, AuthorizationContext.ATTEMPT);

		AssessmentResponse assessmentResponse =
			new AssessmentResponse(assessment, requestContexts, authorizationContexts, latestAttempt, requestedAttempt);

		Set<AuthorizationContext> contexts = ImmutableSet.of(AuthorizationContext.ADMIN, AuthorizationContext.ATTEMPT);
		for (AuthorizationContext context : contexts) {
			assertTrue(assessmentResponse.isAuthorized(context));
		}

		contexts = ImmutableSet.of(AuthorizationContext.REATTEMPT);
		for (AuthorizationContext context : contexts) {
			assertFalse(assessmentResponse.isAuthorized(context));
		}
	}

	@Test
	public void shouldNotCheck_notInvitationOnly_notAdmin() {
		when(assessment.isInvitationOnly()).thenReturn(false);

		AssessmentResponse assessmentResponse =
			new AssessmentResponse(assessment, requestContexts, authorizationContexts, latestAttempt, requestedAttempt);

		assertFalse(assessmentResponse.shouldCheckIfInvited());
	}

	@Test
	public void shouldNotCheck_notInvitationOnly_isAdmin() {
		when(assessment.isInvitationOnly()).thenReturn(false);
		authorizationContexts = ImmutableSet.of(AuthorizationContext.ADMIN);

		AssessmentResponse assessmentResponse =
			new AssessmentResponse(assessment, requestContexts, authorizationContexts, latestAttempt, requestedAttempt);

		assertFalse(assessmentResponse.shouldCheckIfInvited());
	}

	@Test
	public void shouldNotCheck_invitationOnly_isAdmin() {
		when(assessment.isInvitationOnly()).thenReturn(true);
		authorizationContexts = ImmutableSet.of(AuthorizationContext.ADMIN);

		AssessmentResponse assessmentResponse =
			new AssessmentResponse(assessment, requestContexts, authorizationContexts, latestAttempt, requestedAttempt);

		assertFalse(assessmentResponse.shouldCheckIfInvited());
	}

	@Test
	public void shouldCheck_invitationOnly_notAdmin() {
		when(assessment.isInvitationOnly()).thenReturn(true);

		AssessmentResponse assessmentResponse =
			new AssessmentResponse(assessment, requestContexts, authorizationContexts, latestAttempt, requestedAttempt);

		assertTrue(assessmentResponse.shouldCheckIfInvited());
	}
}
