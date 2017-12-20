<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app pagetitle="Assignment Bundle - ${parent.title}" bodyclass="viewBundle page-assignment-bundle" webpackScript="bundles">

	<c:set var="isWorkActive" value="${work.status.code == workStatusTypes['ACTIVE']}"/>
	<c:set var="canSeeSendTab" value="${(!isWorkActive && ((is_internal && !is_view_only) || is_owner || (is_in_work_company && (is_admin || is_manager))))}"/>
	<c:set var="canSeeWorkersTab" value="${is_internal || is_owner || (is_dispatcher && (work.status.code == workStatusTypes['SENT'] || work.status.code == workStatusTypes['DECLINED'])) || (is_in_work_company && (is_admin || is_manager))}"/>
	<c:set var="canSeeVendorsTab" value="${is_owner && currentUser.buyer && hasInvitedAtLeastOneVendor}"/>
	<c:set var="canSeeApplicationTab" value="${is_resource && !isWorkActive}"/>
	<c:set var="canSeeNotesTab" value="${is_internal || is_owner || is_dispatcher || (is_in_work_company && (is_admin || is_manager)) || is_resource || is_active_resource}"/>

	<script>
		var config = {
			form: {
				isAssignmentBundle: true,
				googleAPIKey: '${google_api_key}',
				workEncoded: ${work_encoded},
				authEncoded: ${authEncoded},
				isDispatcher: ${currentUser.dispatcher},
				companyName: '${wmfmt:escapeJavaScript(companyName)}',
				currentUserCompanyName: '${wmfmt:escapeJavaScript(currentUser.companyName)}',
				bundleParentId: ${parent.id},
				isEligibleToTakeAction: ${is_owner || (is_in_work_company && (is_admin || is_manager)) || is_internal},
				isWorkActive: ${isWorkActive},
				isSent: ${work.status.code == workStatusTypes['SENT']},
				<c:if test="${not empty hasInvitedAtLeastOneVendor}">
					hasInvitedAtLeastOneVendor: ${hasInvitedAtLeastOneVendor},
				</c:if>
				workNumber: ${parent.workNumber},
				isAdmin: ${is_admin}
			}
		};
	</script>

	<c:set var="isInternal" value="false" scope="request"/>
	<sec:authorize access="hasRole('ROLE_INTERNAL')">
		<c:set var="isInternal" value="true" scope="request"/>
	</sec:authorize>

	<c:if test="${is_declined_resource}">
		<div class="alert">
			<p>You previously declined this bundle.</p>
		</div>
	</c:if>
	<c:if test="${!is_declined_resource}">
		<div class="inner-container" id="bundle">
			<div class="page-header clear">
				<h2 class="fl">${parent.title} <small>(ID: ${parent.workNumber})</small></h2>
				<c:import url="/WEB-INF/views/web/partials/assignments/details/nav_helper.jsp"/>
			</div>

			<jsp:include page="/WEB-INF/views/web/partials/message.jsp" />

			<input id="parentId" type="hidden" name="parentId" value="${parent.id}">
			<input id="parentWorkNumber" type="hidden" name="parentWorkNumber" value="${parent.workNumber}">
			<input id="industry" name="industry" type="hidden" value="${parent.industry.id}">

			<div id="bundle_overview"></div>
			<c:if test="${is_admin and empty work.activeResource and not empty work.questionAnswerPairs}">
				<c:forEach var="qa" items="${work.questionAnswerPairs}">
					<c:if test="${empty qa.answer}">
						<c:set var="qa" value="${qa}" scope="request"/>
						<c:import url="/WEB-INF/views/web/partials/assignments/details/callouts/buyer_qa_answer.jsp"/>
						<c:remove var="qa" scope="request"/>
					</c:if>
				</c:forEach>
				<c:set var="qa" value="" scope="request"/>
			</c:if>
			<c:import url='/WEB-INF/views/web/partials/assignments/details/description.jsp'/>
			<div id="bundle_data" class="media completion"></div>
			<div id="bundle_map">
				<div id="ampAccordion" class="bundle_accordion">
					<img class="accordion-icon" src="/media/images/live_icons/assignments/location.svg">
					<div class="accordion-heading media-body">
						<a id="accordion_map" data-toggle="collapse" data-parent="#mapAccordion" href="#map_new">
							<h4>Bundle Map  <i class="toggle-icon pull-right icon-minus-sign"></i></h4>
						</a>
					</div>
					<div id="map_new" class="accordion-body collapse in">
						<div id="map-canvas"></div>
					</div>
				</div>
			</div>
		</div>

		<div class="dn">
			<div id="confirm_unbundle_container"></div>
		</div>

		<div id="bundle-container">
			<ul class="wm-tabs">
				<c:if test="${canSeeNotesTab}">
					<li class="wm-tab intro-notes-tab <c:out value="${canSeeSendTab || canSeeWorkersTab || canSeeApplicationTab ? '' : '-active'}"/>" data-content="#notes" id="notes_tab">Messages</li>
				</c:if>
				<c:if test="${canSeeSendTab}">
					<li class="wm-tab -active" data-content="#send_options">Send Options</li>
				</c:if>
				<c:if test="${canSeeWorkersTab}">
					<li class="wm-tab <c:out value="${canSeeSendTab ? '' : '-active'}"/>" data-content="#workers">
						<c:out value="${is_dispatcher ? 'Candidates' : 'Talent'}"/>
					</li>
				</c:if>
				<c:if test="${canSeeApplicationTab}">
					<li class="wm-tab icon-envelope -active" data-content="#application">Application</li>
				</c:if>
			</ul>
		</div>

		<c:if test="${canSeeSendTab || canSeeWorkersTab || canSeeApplicationTab || canSeeNotesTab}">
			<div class="tab-content tab-dark">
				<c:if test="${canSeeNotesTab}">
					<div id="notes" class="wm-tab--content <c:out value="${canSeeSendTab || canSeeWorkersTab || canSeeApplicationTab? '' : '-active'}"/>">
						<c:import url='/WEB-INF/views/web/partials/assignments/details/notes.jsp'/>
						<h4>Questions</h4>
						<c:import url='/WEB-INF/views/web/partials/assignments/details/qa.jsp'/>
					</div>
				</c:if>
				<c:if test="${canSeeSendTab}">
					<div id="send_options" class="wm-tab--content -active">
						
						<form:form action="/assignments/save_bundle/${parent.workNumber}" method="POST" commandName="form" name="form" id="assignments_form" class="form-horizontal">
							<wm-csrf:csrfToken />
							<form:hidden path="id" id="assignment_id"/>
							<c:if test="${isTemplate}">
								<c:import url='/WEB-INF/views/web/partials/assignments/add/routing.jsp'/>
							</c:if>
						</form:form>
						<c:if test="${not isTemplate}">
							<div class="routing-bucket"></div>
						</c:if>
					</div>
				</c:if>
				<c:if test="${canSeeWorkersTab}">
					<div id="workers" class="wm-tab--content assignment-workers <c:out value="${canSeeSendTab ? '' : '-active'}"/>">
						<div id="workers-bucket"></div>
						<div id="vendors-bucket"></div>
					</div>
				</c:if>
				<c:if test="${canSeeApplicationTab}">
					<div id="application" class="wm-tab--content -active">
						<c:choose>
							<c:when test="${workResponse.viewingResource.pendingNegotiation.approvalStatus.code != 'pending' || workResponse.viewingResource.pendingNegotiation.isExpired}">
								<div class="well-b2">
									<div class="well-content">
										<c:import url='/WEB-INF/views/web/partials/assignments/details/compliance.jsp'/>
										<c:import url='/WEB-INF/views/web/partials/assignments/details/callouts/resource_accept_or_apply.jsp'>
											<c:param name="isAcceptableOrApplyable" value="${isAcceptableOrApplyable}"/>
										</c:import>
									</div>
								</div>
							</c:when>
							<c:otherwise>
								<c:import url='/WEB-INF/views/web/partials/assignments/details/nav.jsp'/>
							</c:otherwise>
						</c:choose>
					</div>
				</c:if>
			</div>
		</c:if>

		<jsp:include page="/WEB-INF/views/web/partials/general/block_client.jsp"/>
		<jsp:include page="/WEB-INF/views/web/partials/assignments/details/callouts/decline_negotiation.jsp"/>
	</c:if>

</wm:app>
