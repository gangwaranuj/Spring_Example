<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>
<%@ taglib prefix="html" uri="http://struts.apache.org/tags-html" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<fmt:message key="home.home" var="home"/>
<wm:app
	pagetitle="${home}"
	bodyclass="page-home loggedin"
	webpackScript="home"
	fluid="true"
>

	<c:import url="/WEB-INF/views/web/partials/message.jsp"/>
	<fmt:formatDate value="${last90Start.time}" pattern="MM/dd/yyyy" var="last90StartParam" />
	<fmt:formatDate value="${last90End.time}" pattern="MM/dd/yyyy" var="last90EndParam" />
	<sec:authorize access="hasFeature('workerservices_promo')" var="isWorkerServicesPromoFeature" />
	<c:set var="isWorkerServicesPromoFeatureEnabled" value="${isWorkerServicesPromoFeature && currentUser.country == 'USA'}" />

	<c:set var="hasAnyRoleAdminManagerDispatcher" value="false" />
	<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
		<c:set var="hasAnyRoleAdminManagerDispatcher" value="true" />
	</sec:authorize>

	<c:choose>
		<c:when test="${isWorkerCompany}" >
			<c:set var="hasDispatchEnabled" value="true" />
		</c:when>
		<c:otherwise>
			<c:set var="hasDispatchEnabled" value="false" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test="${currentUser.seller or currentUser.dispatcher}" >
			<c:set var="groupInvitationsCount" value="${groupInvitationsCount}" />
		</c:when>
		<c:otherwise>
			<c:set var="groupInvitationsCount" value="0" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test="${renderCompanyOnboardingProgress and hasAnyRoleAdminManagerDispatcher and currentUser.buyer}" >
			<c:set var="renderCompanyOnboardingProgress" value="true" />
		</c:when>
		<c:otherwise>
			<c:set var="renderCompanyOnboardingProgress" value="false" />
		</c:otherwise>
	</c:choose>

	<script>
		var config = {
			feedTrackerEl: '.home-find-work-container',
			isWorker: ${currentUser.seller || false},
			isDispatcher: ${currentUser.dispatcher || false},
			isWorkerServicesPromoFeatureEnabled: ${isWorkerServicesPromoFeatureEnabled},
			backgroundImageUri: '${wmfmt:escapeJavaScript(currentUser.backgroundImageUri)}',
			firstName: '${currentUser.firstName}',
			isBuyer: ${currentUser.buyer || false},
			limit: '25',
			distance: '${currentUser.maxTravelDistance}',
			postalCode: '${not empty currentUser.postalCode ? currentUser.postalCode : 10001}',
			constants: ${feedValidationConstants},
			companyHidesPricing: ${currentUser.companyHidesPricing},
			hasAnyRoleAdminManagerDispatcher: ${hasAnyRoleAdminManagerDispatcher},
			hasDispatchEnabled: ${hasDispatchEnabled},
			promoDismissed: ${promoDismissed},
			email: '${currentUser.email}',
			renderOnboardingProgress: ${renderCompanyOnboardingProgress},
			groupInvitationsCount: ${groupInvitationsCount}
		};
	</script>

	<c:set var="rating" value="${buyerScoreCard.valuesWithStringKey['PERCENTAGE_RATINGS_OVER_4_STARS'].net90}" />
	<c:set var="paidCount" value="${buyerScoreCard.valuesWithStringKey['PAID_WORK'].net90}" />
	<c:set var="approvalTime" value="${buyerScoreCard.valuesWithStringKey['AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS'].net90}" />
	<c:set var="paymentTime" value="${buyerScoreCard.valuesWithStringKey['AVERAGE_TIME_TO_PAY_WORK_IN_DAYS'].net90}" />
	<c:set var="satisfactionOverall" value="${vendorScoreCard.valuesWithStringKey['SATISFACTION_OVER_ALL'].net90 * 100}" />
	<c:set var="quality" value="${vendorScoreCard.valuesWithStringKey['QUALITY'].net90 * 100}" />
	<c:set var="professionalism" value="${vendorScoreCard.valuesWithStringKey['PROFESSIONALISM'].net90 * 100}" />
	<c:set var="communication" value="${vendorScoreCard.valuesWithStringKey['COMMUNICATION'].net90 * 100}" />

	<div id="top-container" class="container">
		<c:if test="${currentUser.seller || currentUser.dispatcher}">
			<!-- React Root Container for Worker and Dispatcher for HomePage -->
			<div id="home__container"></div>

			<a id="modal-background-changer" href="/home/change_background" class="tooltipped tooltipped-n" aria-label="<fmt:message key="home.change_background_image"/>" data-behavior="modal">
				<i class="wm-icon-upload muted"></i>
			</a>
		</c:if>
		<sec:authorize access="hasFeature('workerservices_promo')">
			<c:if test="${currentUser.country == 'USA'}">
				<div class="promo-banner-bucket"></div>
			</c:if>
		</sec:authorize>
		<c:if test="${currentUser.seller and !currentUser.buyer and not empty vendorScoreCard}">
			<div class="scorecard -company" >
				<ul>
					<li>
						My Scorecard
					</li>
					<li>
						<c:choose>
							<c:when test="${satisfactionOverall >= 90}">
								<span class="label -good"><fmt:formatNumber value="${satisfactionOverall}" maxFractionDigits="1"/>% </span>
							</c:when>
							<c:when test="${satisfactionOverall < 90 and satisfactionOverall >= 75 }">
								<span class="label -neutral"><fmt:formatNumber value="${satisfactionOverall}" maxFractionDigits="1"/>% </span>
							</c:when>
							<c:otherwise>
								<span class="label -bad"><fmt:formatNumber value="${satisfactionOverall}" maxFractionDigits="1"/>% </span>
							</c:otherwise>
						</c:choose>
						Overall
					</li>
					<li>
						<c:choose>
							<c:when test="${quality >= 90}">
								<span class="label -good"><fmt:formatNumber value="${quality}" maxFractionDigits="1"/>% </span>
							</c:when>
							<c:when test="${quality < 90 and quality >= 75 }">
								<span class="label -neutral"><fmt:formatNumber value="${quality}" maxFractionDigits="1"/>% </span>
							</c:when>
							<c:otherwise>
								<span class="label -bad"><fmt:formatNumber value="${quality}" maxFractionDigits="1"/>% </span>
							</c:otherwise>
						</c:choose>
						Quality
					</li>
					<li>
						<c:choose>
							<c:when test="${professionalism >= 90}">
								<span class="label -good"><fmt:formatNumber value="${professionalism}" maxFractionDigits="1"/>% </span>
							</c:when>
							<c:when test="${professionalism < 90 and professionalism >= 75 }">
								<span class="label -neutral"><fmt:formatNumber value="${professionalism}" maxFractionDigits="1"/>% </span>
							</c:when>
							<c:otherwise>
								<span class="label -bad"><fmt:formatNumber value="${professionalism}" maxFractionDigits="1"/>% </span>
							</c:otherwise>
						</c:choose>
						Professionalism
					</li>
					<li>
						<c:choose>
							<c:when test="${communication >= 90}">
								<span class="label -good"><fmt:formatNumber value="${communication}" maxFractionDigits="1"/>% </span>
							</c:when>
							<c:when test="${communication < 90 and professionalism >= 75 }">
								<span class="label -neutral"><fmt:formatNumber value="${communication}" maxFractionDigits="1"/>% </span>
							</c:when>
							<c:otherwise>
								<span class="label -bad"><fmt:formatNumber value="${communication}" maxFractionDigits="1"/>% </span>
							</c:otherwise>
						</c:choose>
						Communication
					</li>
				</ul>
			</div>
		</c:if>
	</div>

	<c:if test="${currentUser.seller || currentUser.dispatcher}">
		<div class="container seller -home-page">
			<div class="tile -home">
				<a href="/profile?ref=box" class="tile-content -find">
					<c:choose>
						<c:when test="${not empty currentUser.largeAvatarUri}">
							<img class="worker-profile-image" src="<c:out value="${wmfn:stripUriProtocol(wmfmt:stripXSS(currentUser.largeAvatarUri))}" />" alt="Photo"/>
						</c:when>
						<c:otherwise>
							<jsp:include page="/WEB-INF/views/web/partials/svg-icons/home/icon-home-one.jsp"/>
						</c:otherwise>
					</c:choose>
					<h2><c:out value="${currentUser.fullName}"/></h2>
					<hr/>
					<fmt:message key="home.your_profile_is_percent_complete" var="home_your_profile_is_percent_complete">
            <fmt:param value="${profileCompleteness.completedPercentage}"/>
          </fmt:message>
					<small class="tooltipped tooltipped-n" aria-label="${home_your_profile_is_percent_complete}"><fmt:message key="home.profile_completeness"/></small>
					<div class="progress -home">
						<div class="bar bar-warning" style="width:${profileCompleteness.completedPercentage}%;"></div>
					</div>
				</a>
				<a id="manage-profile" class="tile-cta" href="/profile-edit">
					<fmt:message key="home.manage_profile"/>
				</a>
			</div>
			<div class="tile -home">
				<div class="tile-content">
					<a id="groups-home" href="/search-groups" class="-groups">
						<jsp:include page="/WEB-INF/views/web/partials/svg-icons/home/icon-home-two.jsp"/>
						<h2><fmt:message key="global.talent_pools"/></h2>
						<hr/>
						<small><fmt:message key="home.find_work_by"/></small>
					</a>
					<c:if test="${groupInvitationsCount > 0}">
						<a class="count-box" href="/groups/invitations">
							<span class="count"><c:out value="${groupInvitationsCount}"/></span>
							<c:choose>
								<c:when test="${groupInvitationsCount eq 1}">
									<small><fmt:message key="home.invitation"/></small>
								</c:when>
								<c:otherwise>
                	<small><fmt:message key="home.invitations"/></small>
                </c:otherwise>
              </c:choose>
						</a>
					</c:if>
					<a id="find-clients" class="tile-cta" href="/search-groups">
						<fmt:message key="home.browse_talent_pools"/>
					</a>
				</div>
			</div>
			<div class="tile -home">
				<c:set var="doWorkStatus">
					<c:choose>
						<c:when test="${workAvailableCount > 0}">#status/available/working</c:when>
					</c:choose>
				</c:set>

				<a class="tile-content" href="/assignments${doWorkStatus}">
					<jsp:include page="/WEB-INF/views/web/partials/svg-icons/home/icon-home-three.jsp"/>
					<h2><fmt:message key="global.assignments"/></h2>
					<hr/>
					<small><fmt:message key="home.manage_your_work"/></small>

					<c:if test="${workAvailableCount > 0}">
						<div class="count-box">
							<span class="count"><c:out value="${workAvailableCount}"/></span>
							<small><fmt:message key="global.available"/></small>
						</div>
					</c:if>
				</a>
				<a id="resource-dashboard" class="tile-cta" href="/assignments#status/inprogress/working">
					<fmt:message key="home.my_work"/>
				</a>
			</div>
			<div class="tile -home">
				<div class="tile-content">
					<a href="/payments/invoices/receivables">
						<jsp:include page="/WEB-INF/views/web/partials/svg-icons/home/icon-home-four.jsp"/>
						<h2><fmt:message key="home.payment_center"/></h2>
						<hr/>
						<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_CONTROLLER')">
							<c:if test="${available_balance > 0}">
								<small>
									<fmt:message key="home.available_to_withdraw"/><span class="currency"><fmt:formatNumber value="${available_balance}" currencySymbol="$" type="currency"/></span>
								</small>
							</c:if>
						</sec:authorize>
					</a>
					<c:if test="${hasFastFunds}">
						<br />
						<small>
							<fmt:message key="global.new" var="globalNew"/>
              <wm:label status="label label-success">${fn:toUpperCase(globalNew)}</wm:label>
							<fmt:message key="home.get_payed_now_fastfunds"/><span class="tooltipped tooltipped-n" aria-label="<fmt:message key="home.with_fastfunds"/>"><i class="wm-icon-information-filled"></i></span>
						</small>
					</c:if>
				</div>
				<a class="tile-cta" href="/payments/invoices/receivables">
					<fmt:message key="home.view_assignments_and_invoice"/>
				</a>
			</div>
		</div>
	</c:if>

	<c:if test="${currentUser.buyer}">
		<div class="container buyer -home-page">
			<!-- BG Upload -->
			<a id="modal-background-changer" href="/home/change_background" class="tooltipped tooltipped-n" aria-label="<fmt:message key="home.change_background_image"/>" data-behavior="modal">
				<i class="wm-icon-upload muted"></i>
			</a>

			<!-- Company Scorecard -->
			<c:if test="${currentUser.buyer and paidCount > 0 and rating > 0}">
				<div class="scorecard -company" >
					<ul>
						<li>
							<fmt:message key="home.my_company_scorecard"/>
						</li>
						<li>
							<c:choose>
								<c:when test="${rating >= 90}">
									<span class="label -good"><fmt:formatNumber value="${rating}" maxFractionDigits="1"/>% </span>
								</c:when>
								<c:when test="${rating < 90 and rating >= 75 }">
									<span class="label -neutral"><fmt:formatNumber value="${rating}" maxFractionDigits="1"/>% </span>
								</c:when>
								<c:otherwise>
									<span class="label -bad"><fmt:formatNumber value="${rating}" maxFractionDigits="1"/>% </span>
								</c:otherwise>
							</c:choose>
							<a href="/reports/assignment_feedback?filters.from_date=${last90StartParam}&filters.to_date=${last90EndParam}">
								<fmt:message key="home.satisfaction_rating"/>
							</a>
						</li>
						<li>
							<c:choose>
								<c:when test="${paymentTime < 0}">
									<span class="label -good">
										<fmt:formatNumber value="${paymentTime * -1}" maxFractionDigits="1" var="days_early"/>
										<fmt:message key="home.days_early" var="home_days_early">
                      <fmt:param value="${days_early}"/>
                    </fmt:message>
                    ${home_days_early}
									</span>
								</c:when>
								<c:when test="${paymentTime >= 0 and paymentTime < 1}">
									<span class="label -neutral"><fmt:message key="home.on_time"/></span>
								</c:when>
								<c:otherwise>
									<span class="label -bad">
										<fmt:formatNumber value="${paymentTime}" maxFractionDigits="1" var="days_late"/>
										<fmt:message key="home.days_late" var="home_days_late">
                      <fmt:param value="${days_late}"/>
                    </fmt:message>
                    ${home_days_late}
									</span>
								</c:otherwise>
							</c:choose>
							<fmt:message key="home.payment_timeliness"/>
						</li>
						<li>
							<c:choose>
								<c:when test="${approvalTime <= 1}">
									<span class="label -good">
										<fmt:message key="home.less_than"/>
									</span>
								</c:when>
								<c:when test="${approvalTime > 1 and approvalTime <= 5}">
									<span class="label -good">
										<fmt:formatNumber value="${approvalTime}" maxFractionDigits="1" var="days_good"/>
										<fmt:message key="home.days" var="home_days">
                      <fmt:param value="${days_good}"/>
                    </fmt:message>
                    ${home_days}
									</span>
								</c:when>
								<c:otherwise>
									<span class="label -bad">
										<fmt:formatNumber value="${approvalTime}" maxFractionDigits="1" var="days_bad"/>
										<fmt:message key="home.days" var="home_days">
                      <fmt:param value="${days_bad}"/>
                    </fmt:message>
                    ${home_days}
									</span>
								</c:otherwise>
							</c:choose>
							<fmt:message key="home.approval_time"/>
						</li>
					</ul>
				</div>
			</c:if>

			<!-- React Root Container for Employer for HomePage -->
			<div id="home__container"></div>
		</div>
	</c:if>

	<c:if test="${currentUser.seller || currentUser.dispatcher}">
		<vr:rope>
			<vr:venue name="HIDE_WORKFEED" bypass="true">
				<div class="container">
					<div class="home-find-work-container">
						<h3 class="page-header">
							<wm:branding name="Feed" /> - <span class="gray-brand small"><fmt:message key="home.find_great_work"/></span>
						</h3>
						<c:import url="/WEB-INF/views/web/partials/feed/shared/feed.jsp" />
					</div>
				</div>
			</vr:venue>
		</vr:rope>
	</c:if>

</wm:app>
