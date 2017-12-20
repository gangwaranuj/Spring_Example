<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix = "s" uri = "http://www.springframework.org/tags" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="${assessment.name}" bodyclass="lms" webpackScript="lms">

	<script>
		var config = {
			mode: 'viewDetails',
			id: '${wmfmt:escapeJavaScript(assessment.id)}',
			name: '${wmfmt:escapeJavaScript(assessment.name)}',
			userId: '${wmfmt:escapeJavaScript(userId)}',
			userCompanyId: '${wmfmt:escapeJavaScript(userCompanyId)}',
			companyBlocked: ${wmfn:boolean(companyBlocked, '1', '0')},
			clientIdToBlock: '${wmfmt:escapeJavaScript(assessment.company.id)}',
			blockedClientTooltip: "<s:message code="lms.view.blocked_client"/>"
		};
	</script>

	<c:set var="isSurvey" value="${assessment.type.value eq AssessmentType.SURVEY}" />
	<div class="inner-container">
		<div class="page-header">
			<h2><c:out value="${assessment.name}" /></h2>
		</div>

		<jsp:include page="/WEB-INF/views/web/partials/message.jsp" />

		<div id="dynamic_messages"></div>

		<div class="row_wide_sidebar_right">
			<div class="content">
				<div class="well-b2">
					<h3>Description</h3>
					<div id="description" class="well-content">
						<p>${wmfmt:tidy(wmfmt:autoLink(assessment.description))}</p>
						<c:if test="${fn:contains(authorizationContexts, 'ADMIN')}">
							<div class="share-test">
								<small class="meta">Share on:
									<ul class="inline-nav">
										<li class="mr">
											<a href="javascript:void(0);" class="linkedin">LinkedIn</a>
										</li>
										<li class="mr">
											<a href="javascript:void(0);" class="twitter">Twitter</a>
										</li>
										<li class="mr">
											<a href="javascript:void(0);" class="facebook">Facebook</a>
										</li>
									</ul>
								</small>
							</div>
						</c:if>
					</div>
				</div>
				<c:if test="${fn:contains(authorizationContexts, 'ADMIN')}">
					<div class="well-b2">
						<h3>Respondents
							<small class="meta">
								(<a href="<c:url value="/lms/manage/export/${assessment.id}"/>" title="Export these records to a CSV file">Export to CSV</a>)
								<c:if test="${assessment.hasAssetItems}">
									(<a href="<c:url value="/lms/manage/assets/${assessment.id}"/>">View Survey Attachments</a>)
								</c:if>
							</small>
						</h3>
						<table id="attempts_list" class="group-list">
							<thead>
								<tr>
									<th>Name</th>
									<th width="120">Status</th>
									<th width="100">Completed</th>
									<c:if test="${survey}">
										<th>Assignment</th>
									</c:if>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>

					<script id="attempts-cell-user-tmpl" type="text/x-jquery-tmpl">
					<div>
						<a target="_blank" href="/profile/\${meta.user_number}">\${data}</a><br/>
						<small class="meta">\${meta.company_name}</small>
					</div>
				</script>

					<script id="attempts-cell-status-tmpl" type="text/x-jquery-tmpl">
					<div>
						{{if meta.status == 'graded'}}
							{{if meta.passed}}
								Passed
							{{else}}
								Failed
							{{/if}}
							(\${meta.score}%)<br/>
							<a href="/lms/grade/${assessment.id}/\${meta.attempt_id}">Results</a>
						{{else meta.status == 'complete'}}
							<a href="/lms/grade/${assessment.id}/\${meta.attempt_id}">Results</a>
						{{else meta.status == 'gradePending'}}
							<a href="/lms/grade/${assessment.id}/\${meta.attempt_id}">Pending</a>
						{{else meta.status == 'gradePending'}}
							In Progress
						{{else}}
							\${data}
						{{/if}}
					</div>
				</script>
				</c:if>
			</div>
			<div class="sidebar" id="lms_actions">
				<div class="well-b2 <c:if test="${latestAttempt.passed}">alert-success</c:if>">
					<div class="well-content tac">
						<c:choose>
							<c:when test="${assessment.status.code eq 'active'}">
								<c:if test="${fn:contains(authorizationContexts, 'ATTEMPT')}">
									<c:choose>
										<c:when test="${survey}">
											<c:choose>
												<c:when test="${not empty(latestAttempt)}">
													<c:choose>
														<c:when test="${latestAttempt.status.code eq 'inprogress'}">
															<h4>In Progress</h4>
															<p>Started: <fmt:formatDate value="${latestAttemptCreatedOn}" pattern="MM/dd/yyyy" timeZone="${timeZoneId}"/></p>
															<a href="<c:url value="/lms/view/take/${assessment.id}"/>" class="button">Resume</a>
														</c:when>
														<c:when test="${latestAttempt.status.code eq 'complete'}">
															<h4>Complete</h4>
															<p>You took this survey on <fmt:formatDate value="${latestAttemptCompleteOn}" pattern="MM/dd/yyyy" timeZone="${timeZoneId}"/>.</p>
														</c:when>
													</c:choose>
												</c:when>
												<c:otherwise>
													<h4>Available</h4>
													<a href="<c:url value="/lms/view/take/${assessment.id}"/>" class="button test_action_button">Take Survey</a>
												</c:otherwise>
											</c:choose>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${not empty(latestAttempt)}">
													<c:choose>
														<c:when test="${latestAttempt.status.code eq 'inprogress'}">
															<h4>In Progress</h4>
															<a href="<c:url value="/lms/view/take/${assessment.id}"/>" class="button test_action_button">Finish Test</a>
														</c:when>
														<c:when test="${latestAttempt.status.code eq 'gradePending'}">
															<h4>Grade Pending</h4>
														</c:when>
														<c:when test="${(latestAttempt.status.code eq 'graded') or (latestAttempt.status.code eq 'complete')}">
															<h4>Complete</h4>
															<p>
																<c:choose>
																	<c:when test="${latestAttempt.passed}">Congratulations! You passed this test.</c:when>
																	<c:otherwise>You failed this test.</c:otherwise>
																</c:choose>
															</p>
															<a class="button" href="<c:url value="/lms/grade/${assessment.id}/${latestAttempt.id}"/>">View Results</a>

															<c:if test="${not(latestAttempt.passed) and fn:contains(authorizationContexts, 'REATTEMPT')}">
																<a href="<c:url value="/lms/view/take/${assessment.id}"/>" class="button test_action_button">Take Again</a>
															</c:if>
														</c:when>
													</c:choose>
												</c:when>
												<c:otherwise>
													<h4>Available</h4>
													<a href="<c:url value="/lms/view/take/${assessment.id}"/>" class="button test_action_button">Take Test</a>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
								</c:if>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${assessment.itemsSize == 0}" >
										<p>You must have at least one question in the <c:out value="${(assessment.type.value eq AssessmentType.SURVEY) ? 'survey' : 'test'}"/> to activate it</p>
									</c:when>
									<c:otherwise>
										<p>You must activate this <c:out value="${(assessment.type.value eq AssessmentType.SURVEY) ? 'survey' : 'test'}"/> to continue.</p>
										<a data-id="${assessment.id}" data-action="activate" class="activate-action button">Activate</a>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</div>
				</div>

				<div class="well-b2">
					<h3>Stats</h3>
					<div class="stats clear">
						<ul>
							<li>
								<span>${fn:length(assessment.items)}</span>
								<br/>
								Questions
							</li>
							<c:if test="${graded and assessment.configuration.statisticsShared}">
								<li>
									<span><c:out value="${statistics.numberOfPassed}"/></span>
									<br/>
									Passed
								</li>
								<li class="last">
									<span><c:out value="${statistics.numberOfFailed}"/></span>
									<br/>
									Failed
								</li>
							</c:if>
						</ul>
					</div>
					<c:if test="${graded && (assessment.configuration.passingScoreShared || assessment.configuration.statisticsShared || assessment.setApproximateDurationMinutes)}">
						<div class="stats clear">
							<ul>
								<c:if test="${graded and assessment.configuration.passingScoreShared}">
									<li>
										<span><c:out value="${assessment.configuration.passingScore}"/></span>
										<br/>
										% to pass
									</li>
								</c:if>
								<c:if test="${graded and assessment.configuration.statisticsShared}">
									<li>
										<span><fmt:formatNumber value="${statistics.averageScore}" maxFractionDigits="1"/></span>
										<br/>
										% Avg
									</li>
								</c:if>
								<c:if test="${assessment.setApproximateDurationMinutes}">
									<li class="last">

										<span><c:out value="${assessment.approximateDurationMinutes}"/></span>
										<br/>
										Min to take
									</li>
								</c:if>
							</ul>
						</div>
					</c:if>
				</div>
				<c:if test="${!fn:contains(authorizationContexts, 'ADMIN')}">
					<div class="well-b2">
						<h3>About the Owner</h3>
						<div class="well-content">
							<p><strong><c:out value="${assessment.company.name}" /></strong></p>
							<c:if test="${not empty assessment.company.avatarLarge.uri}">
								<p><img class="test-detail-logo" src="<c:out value="${wmfmt:stripXSS(assessment.company.avatarLarge.uri)}" />"/></p>
							</c:if>
						</div>
					</div>
				</c:if>
				<c:if test="${fn:contains(authorizationContexts, 'ADMIN')}">
					<div class="well-b2">
						<h3>Admin</h3>
						<div class="well-content">
							<ul>
								<li><a href="/lms/manage/step1/${assessment.id}">Edit Details</a></li>
								<li><a href="/lms/manage/step2/${assessment.id}" class="lastA">Add/Edit Questions</a></li>
								<li><a data-id="${assessment.id}" data-action="copy">Copy</a></li>
								<c:choose>
									<c:when test="${assessment.status.code eq 'active'}">
										<li><a data-id="${assessment.id}" data-action="deactivate" class="deactivate-action">Deactivate</a></li>
									</c:when>
									<c:otherwise>
										<li><a data-id="${assessment.id}" data-action="activate" class="activate-action">Activate</a></li>
									</c:otherwise>
								</c:choose>
								<li><a data-id="${assessment.id}" data-action="delete" class="delete-action lastA">Delete</a></li>
							</ul>

							<small class="meta">
								Eligibility and Visibility:<br/>
								<c:choose>
									<c:when test="${assessment.configuration.featured}">Anyone using Work Market, and anyone I invite</c:when>
									<c:otherwise>Invitation only</c:otherwise>
								</c:choose>
							</small>
						</div>
					</div>
				</c:if>

				<c:if test="${(userCompanyId != assessment.company.id)}">
					<small class="meta">
						<c:set var="clientName" value="${assessment.company.name}"/>
						<a id="unblock-client" <c:out value="class=${companyBlocked ? '' : 'dn'}"/>>Unblock <c:out value="${clientName}"/></a>
						<a id="block-client" <c:out value="class=${companyBlocked ? 'dn' : ''}"/>>Block <c:out value="${clientName}"/></a>
					</small>
					<jsp:include page="/WEB-INF/views/web/partials/general/block_client.jsp"/>
				</c:if>
			</div>
		</div>
	</div>
</wm:app>
