<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="s" uri = "http://www.springframework.org/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app
	pagetitle="${requestScope.group.name}"
	bodyclass="page-group-detail"
	webpackScript="search"
>

	<script>
		var config = {
			mode: 'group-detail',
			group_id: ${wmfmt:escapeJavaScript(group.id)},
			userNumber: '${currentUser.userNumber}',
			isGroupAdmin: ${is_group_admin},
			missingContractVersions:  ${wmfn:boolean(not empty missing_contract_versions, 'true', 'false')},
			clientIdToBlock: '${wmfmt:escapeJavaScript(group.company.id)}',
			companyBlocked: ${wmfn:boolean(companyBlocked, 'true', 'false')},
			isGroupCompanyViewer: ${wmfn:boolean(is_group_company_viewer, 'true', 'false')},
			searchType: '${preferences}',
			disableDeepLinking: true
		};
	</script>

	<div class="groups-toggle">
		<div class="groups-page-header">
			<h2>
				<c:choose>
					<c:when test="${is_group_company_viewer}">
						<span class="group-icon shared-by-me <c:if test="${isGroupSharingActive eq false}">dn</c:if>">
							<span class="tooltipped tooltipped-n" aria-label="Sharing enabled">
								<jsp:include page="/WEB-INF/views/web/partials/svg-icons/groups/shared-by-me.jsp"/>
							</span>
						</span>
					</c:when>
					<c:otherwise>
					<span class="group-icon shared-with-me <c:if test="${isGroupSharingActive eq false}">dn</c:if>">
						<span class="tooltipped tooltipped-n" aria-label="This group is shared with you">
							<jsp:include page="/WEB-INF/views/web/partials/svg-icons/groups/shared-with-me.jsp"/>
						</span>
					</span>
					</c:otherwise>
				</c:choose>
				<span class="group-icon default <c:if test="${isGroupSharingActive}">dn</c:if>">
					<jsp:include page="/WEB-INF/views/web/partials/svg-icons/groups/group-default.jsp"/>
				</span>
				<c:out value="${group.name}" />
			</h2>
		</div>

		<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
			<c:param name="containerId" value="dynamic_messages"/>
		</c:import>

		<jsp:include page="/WEB-INF/views/web/partials/general/notices.jsp"/>

		<div id="group-membership-overview">
			<div class="overview-content">
				<div class="well-b2">
					<h3>Overview</h3>
					<div id="requirements" class="well-content">
						<jsp:include page="/WEB-INF/views/web/partials/groups/details/overview.jsp"/>
					</div>
				</div>

				<c:if test="${is_group_member and group.openMembership}">
					<div class="well-b2">
						<h3>Messages</h3>
						<div id="messages" class="well-content">
							<jsp:include page="/WEB-INF/views/web/partials/groups/details/messages.jsp"/>
						</div>
					</div>
				</c:if>

				<vr:rope>
					<vr:venue name="SHARED_GROUPS">
						<div class="well-b2" id="group-sharing-manage">
							<h3>Settings</h3>
							<div class="well-content">
								<ul id="shared-groups-tab" class="unstyled">
									<label>
										<input type="checkbox" data-group-id="${group.id}" name="group_shared" <c:if test="${isGroupSharingActive}">checked</c:if> /> Enable sharing of this talent pool<br/>
										<span class="help-inline" style="margin-left: 1em;">By enabling sharing you can allow other companies to send work to this talent pool.</span>
									</label>
								</ul>
							</div>
						</div>
					</vr:venue>
				</vr:rope>
			</div>

			<div class="sidebar-actions">
				<c:if test="${(is_group_admin and !group.activeFlag) or (currentUser.seller || currentUser.dispatcher)}">
					<div class="well-b2">
						<div class="well-content tac">
							<c:if test="${group.openMembership and group.activeFlag}">
								<c:set var="applyUrl">
									<c:url value="/groups/${group.id}/apply"/>
								</c:set>
								<sec:authorize access="hasFeature('VendorPools')">
									<c:if test="${currentUser.dispatcher}">
										<c:set var="applyUrl">
											<c:url value="/groups/${group.id}/vendor_apply"/>
										</c:set>
									</c:if>
								</sec:authorize>
								<form action="${applyUrl}" id="group_apply_form" method="post">
									<wm-csrf:csrfToken />
									<input type="hidden" id="redirect_to" name="redirect_to" value=""/>
								</form>
							</c:if>
							<c:if test="${group.openMembership and group.activeFlag and (currentUser.seller || currentUser.dispatcher)}">
								<c:set var="submit_application_link_id" scope="page">submit_application_link</c:set>
								<c:choose>
									<c:when test="${currentUser.dispatcher}">
										<c:choose>
											<c:when test="${vendorTalentPoolMembershipStatus == 'MEMBER'}">
												<p>Your company is a member of this talent pool.</p>
											</c:when>
											<c:when test="${vendorTalentPoolMembershipStatus == 'APPLIED'}">
												<p>Your company applied to this talent pool and your membership request is pending.</p>
											</c:when>
											<c:otherwise>
												<p>You meet all the requirements for joining this talent pool. As a Team Agent, you will apply on behalf of your <strong>entire company</strong>.</p>
												<p><a class="button" id="${submit_application_link_id}">Apply &raquo;</a></p>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:when test="${empty association or association.deleted}">
											<c:choose>
												<c:when test="${validation.eligible}">
													<p>You meet all the requirements for joining this talent pool. You will apply as an <strong>individual worker</strong>.</p>
													<p><a class="button" id="${submit_application_link_id}">Apply &raquo;</a></p>
												</c:when>
												<c:otherwise>
													<p>You do not meet all the requirements for joining this talent pool, but you can apply anyway. You will apply as an <strong>individual worker</strong>.</p>
													<p><a class="button show-apply-confirmation" id="${submit_application_link_id}">Apply &raquo;</a></p>
												</c:otherwise>
											</c:choose>
										<c:if test="${has_invitation and not empty latest_invite}">
											<div class="alert alert-box">
												<p>You were invited to this talent pool on <fmt:formatDate value="${latest_invite.requestDate.time}" pattern="MM/dd/yyyy"/>.</p>
												<p><a class="button -small" onclick="return confirm('Are you sure you want to decline this invitation?');" href="<c:url value="/groups/${group.id}/decline"/>">Decline Invitation</a></p>
											</div>
										</c:if>
									</c:when>
									<c:when test="${association.active}">
										<div class="alert alert-box">
											<p>You are a member of this talent pool and have been active since <fmt:formatDate value="${association.dateApproved.time}" pattern="MM/dd/yyyy"/>.</p>
											<a class="button -small" onclick="return confirm('Are you sure you want to leave this talent pool?');" href="<c:url value="/groups/${group.id}/leave"/>">Leave Talent Pool</a>
										</div>
									</c:when>
									<c:when test="${association.declined}">
										<div class="alert alert-box">
											<p>Your application to this talent pool has been declined.</p>
										</div>
									</c:when>
									<c:when test="${not association.declined}">
										<div class="alert alert-box">
											<p>You applied on <fmt:formatDate value="${association.dateApplied.time}" pattern="MM/dd/yyyy"/> and your membership request is pending.</p>
											<a class="button -small" onclick="return confirm('Are you sure you want to cancel your request to join this talent pool?');" href="<c:url value="/groups/${group.id}/leave"/>">Cancel Request</a>
										</div>
									</c:when>
								</c:choose>
							</c:if>
						</div>
					</div>
				</c:if>
				<div class="well-b2">
					<h3>About</h3>
					<div class="well-content">
						<c:if test="${not empty(group.industry)}">
							<p>
								<strong>Industry Focus:</strong>
								<br/>
								<c:out value="${group.industry.name}"/>
							</p>
						</c:if>
						<p>
							<strong>Managed by:</strong>
							<br/>
							<c:out value="${group.owner.fullName}"/>
							<br/>
							<c:out value="${group.company.effectiveName}" />
						</p>
						<c:if test="${not empty(avatar_large)}">
							<p><img class="test-detail-logo" src="<c:out value="${wmfmt:stripXSS(avatar_large)}" />"/></p>
						</c:if>
					</div>
				</div>
				<div class="well-b2">
					<h3>Stats</h3>
					<div class="stats clear">
						<ul>
							<li>
								<span><fmt:formatDate value="${group.createdOn.time}" pattern="MM/dd/yyyy"/></span>
								<br/>
								Created
							</li>
							<li class="last" id="actions">
								<span>${memberCount}</span>
								<br/>
								Members
							</li>
						</ul>
					</div>
				</div>

				<c:if test="${isInternal}">
					<div class="well-b2">
						<h3>
							<i class="icon-laptop"></i> WM
						</h3>
						<div class="well-content">
							<ul>
								<li>
									<a href="<c:url value="/groups/reindex_members/${group.id}"/>">Reindex Members</a>
								</li>
							</ul>
						</div>
					</div>
				</c:if>

				<c:if test="${!is_group_company_viewer}">
					<c:set var="clientName" value="${group.company.name}"/>
					<a id="unblock-client" <c:out value="class=${companyBlocked ? '' : 'dn'}"/>>Unblock <c:out value="${clientName}"/></a>
					<a id="block-client" <c:out value="class=${companyBlocked ? 'dn' : ''}"/>>Block <c:out value="${clientName}"/></a>
					<jsp:include page="/WEB-INF/views/web/partials/general/block_client.jsp"/>
				</c:if>
				<c:if test="${not(is_group_admin) or not(is_group_company_viewer)}">
					<br/>
					<a class="report-concern" title="Report a Concern">Report a Concern</a>
				</c:if>
			</div>
		</div>
	</div>

	<c:if test="${not empty missing_contract_versions}">
		<div class="dn">
			<div id="confirm_agreements">
				<c:forEach var="version" items="${missing_contract_versions}" varStatus="status">
					<%-- contractVersionAssets is a Set. We only want the "first" one. --%>
					<c:forEach var="asset" items="${version.contractVersionAssets}" end="0">
						<div class="agreement <c:if test="${status.index gt 0}">dn</c:if>" data-id="${version.id}">
							<input type="hidden" name="signed_contract_agreements" value="${version.id}"/>

							<h2><c:out value="${asset.name}" /></h2>

							<div class="instructions"><c:out value="${asset.content}" escapeXml="false"/></div>
						</div>
					</c:forEach>
				</c:forEach>
			</div>
		</div>
	</c:if>

</wm:app>
