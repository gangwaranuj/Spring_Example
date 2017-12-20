<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app pagetitle="Profile - ${facade.firstName} ${facade.lastName}" bodyclass="page-profile" breadcrumbSection="Profile" breadcrumbSectionURI="/profile" breadcrumbPage="Overview" webpackScript="profile">

	<script>
		var config = {
			name: 'profile',
			userNumber: '${facade.userNumber}',
			userId: ${facade.id},
			allScorecard: ${allScorecard},
			companyScorecard: ${companyScorecard},
			paidassignforcompany: ${paidassignforcompany},
			facade: ${facadeJSON},
			isDispatch: ${isDispatch},
			isOwner: ${isOwner},
			isUserTelaidPrivate:${isUserTelaidPrivate}
		}
	</script>


<c:set var="backgroundStatusImage" value="${mediaPrefix}${facade.backgroundCheckStatus eq 'failed' ? '/images/live_icons/assignments/failed_checks_2.svg' : (facade.backgroundCheckStatus eq 'passed' ? '/images/live_icons/assignments/passed_check_2.svg' : '/images/live_icons/assignments/pending_check_2.svg')}" />
<c:set var="drugStatusImage" value="${mediaPrefix}${facade.drugTestStatus eq 'failed' ? '/images/live_icons/assignments/failed_checks_2.svg' : (facade.drugTestStatus eq 'passed' ? '/images/live_icons/assignments/passed_check_2.svg' : '/images/live_icons/assignments/pending_check_2.svg')}" />

<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

<c:if test="${facade.blocked}">
	<div class="alert" style="background-color: #f4f4f4; border: 1px solid #a54f4f; color: #000000; margin-top: 1em">
		<div style="display: block; float: left">
			<i class="material-icons" style="top: 0px; margin-top: -3px;">block</i>
		</div>
		<span style="display: block; padding-left: 3em;">
			Your company blocked this worker. Please contact your administrator for more information.
		</span>
	</div>
</c:if>

<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
	<c:param name="containerId" value="dynamic_messages"/>
</c:import>

	<c:if test="${isOwner && empty facade.certifications}">
		<c:set var="industryIdsForbanner" value="1000," />
		<c:set var="showBanner" value="false" />
		<c:forEach var="i" items="${facade.industries}">
			<c:if test="${!showBanner && fn:contains(industryIdsForbanner, i.id)}">
				<c:set var="showBanner" value="true" />
			</c:if>
		</c:forEach>

		<c:if test="${showBanner}">
			<div id="profile_onboarding_banner"></div>
		</c:if>
	</c:if>

<div class="user-profile profile">
	<div class="profile--messages">
		<c:if test="${isSuspended}">
			<h4>
				This user is currently suspended.
				<sec:authorize access="hasRole('ROLE_INTERNAL')">
					<a href="<c:url value="/admin/manage/profiles/unsuspend/${facade.id}"/>" class="button">Unsuspend</a>
				</sec:authorize>
			</h4>
		</c:if>
		<c:if test="${isDeactivated}">
			<h4>
				This user is currently deactivated.
			</h4>
		</c:if>

		<c:if test="${isOwner and facade.changedEmail}">
			<p>
				You have changed your email address to <em><c:out value="${facade.changedEmail}"/></em>. Until you confirm
				the new address, please use <c:out value="${facade.email}"/> to log in to the site.
			</p>
		</c:if>
	</div>


	<header class="profile--header bkg-<c:out value="${(Math.abs(randomValue.nextInt())%5)+1}"/>">
		<div class="profile--personal-info">
			<div class="profile--avatar">
				<c:choose>
					<c:when test="${not empty facade.avatarLargeAssetUri}">
						<wm:avatar src="${wmfn:stripUriProtocol(wmfmt:stripXSS(facade.avatarLargeAssetUri))}"/>
					</c:when>
					<c:otherwise>
						<wm:avatar hash="${facade.userNumber}"/>
					</c:otherwise>
				</c:choose>
			</div>

			<h1 class="profile--name">
				<c:out value="${facade.firstName}"/>
				<c:choose>
					<c:when test="${isLane4LimitedVisibility}">
						<c:out value="${fn:substring(facade.lastName, 0, 1)}"/>.
					</c:when>
					<c:otherwise>
						<c:out value="${facade.lastName}"/>
					</c:otherwise>
				</c:choose>
			</h1>

			<div class="user-personal--job-title">
				<c:if test="${facade.jobTitle != null}">
					<c:out value="${facade.jobTitle}"/> |
				</c:if>
				${facade.companyName}
			</div>
			<address class="profile--address">
				<c:choose>
					<c:when test="${facade.address != null}">
						<c:out value="${facade.address.shortAddress}"/>
					</c:when>
					<c:when test="${facade.companyAddress != null}">
						<br/>
						<c:out value="${facade.companyAddress.shortAddress}"/>
					</c:when>
				</c:choose>
			</address>
		</div>

		<c:if test="${not isLane4LimitedVisibility}">
			<div class="profile--contact">
				<a class="user-contact--email" href="<c:out value="${wmfmt:spamSlayer('mailto:')}" escapeXml="false"/>
					<c:out value="${wmfmt:spamSlayer(facade.email)}" escapeXml="false"/>">
					<i class="icon-envelope"></i>
					<c:out value="${wmfmt:spamSlayer(facade.email)}" escapeXml="false"/>
				</a>
				<c:if test="${(not empty laneType or isInternal) and (not empty facade.workPhone or not empty facade.mobilePhone)}">
					<c:if test="${not empty facade.mobilePhone}">
						<a href="tel:${wmfmt:spamSlayer(wmfmt:phone(facade.mobilePhone))}" class="user-contact--phone"><i class="icon-phone"></i>M: <c:out value="${wmfmt:spamSlayer(wmfmt:phone(facade.mobilePhone))}" escapeXml="false"/></a>
					</c:if>
					<a href="tel:${wmfmt:spamSlayer(wmfmt:phone(facade.workPhone))}" class="user-contact--phone">
						<i class="icon-phone"></i>W: ${wmfmt:spamSlayer(wmfmt:phone(facade.workPhone))}
						<c:if test="${not empty facade.workPhoneExtension}">
							ext. ${wmfmt:spamSlayer(facade.workPhoneExtension)}
						</c:if>
					</a>
				</c:if>
			</div>
		</c:if>

		<c:if test="${isOwner}">
			<a class="button -toggle profile--edit" href="/profile-edit">Edit Profile</a>
		</c:if>
	</header>

	<section class="profile--details">
		<c:if test="${!isOwner}">
			<div class="profile--quick-actions">
				<c:if test="${!isOwner}">
					<select class="action-menu">
						<option value=""></option>
						<c:if test="${!isSuspended and !facade.blocked}">
							<c:if test="${(laneType == 1 or laneType == 2 or laneType == 3) && hasPhoto}">
								<option value="download photos">Download Photos</option>
							</c:if>
						</c:if>
						<c:if test="${isAdmin and ! (laneType == 0 or laneType == 1)}">
							<c:choose>
								<c:when test="${facade.blocked}">
									<option value="unblock" data-href="/profile-edit/unblockresource?resource_id=${facade.userNumber}">Unblock</option>
								</c:when>
								<c:otherwise>
									<option value="block">Block Worker</option>
								</c:otherwise>
							</c:choose>
						</c:if>
						<c:if test="${!facade.blocked}">
							<option value="report">Report a Concern</option>
						</c:if>
					</select>
				</c:if>
				<c:if test="${!isSuspended and !facade.blocked and !isOwner}">
					<c:choose>
						<c:when test="${laneType == 4}">
							<wm:switch id="add-to-network-submit" name="" classlist="profile--quick-action tooltipped tooltipped-n" text="<i class='wm-icon-plus'></i>" attributes="aria-label='Add to Network'" />
						</c:when>
						<c:when test="${laneType == 2 or laneType == 3}">
							<wm:switch id="remove-from-network-submit" name="" classlist="profile--quick-action remove-from-network tooltipped tooltipped-n" text="<i class='wm-icon-checkmark-circle'></i>" attributes="aria-label='Remove from Network'" />
						</c:when>
					</c:choose>
				</c:if>
				<c:if test="${!facade.blocked}">
					<wm:switch name="profile-action" classlist="profile--quick-action mainToggle tooltipped tooltipped-n" isUnique="true" text="<i class='wm-icon-speech'></i>" value="collapseComment" attributes="data-toggle='collapse' href='#collapseComment' aria-label='Add Comments'" />
					<wm:switch name="profile-action" classlist="profile--quick-action mainToggle tooltipped tooltipped-n" isUnique="true" text="<i class='wm-icon-tag'></i>" value="collapseTags" attributes="data-toggle='collapse' href='#collapseTags' aria-label='Add Tags'" />
					<wm:switch name="profile-action" classlist="profile--quick-action mainToggle tooltipped tooltipped-n" isUnique="true" text="<i class='wm-icon-test'></i>" value="collapseTests" attributes="data-toggle='collapse' href='#collapseTests' aria-label='Invite to Test'" />
					<wm:switch name="profile-action" classlist="profile--quick-action mainToggle tooltipped tooltipped-n" isUnique="true" text="<i class='wm-icon-users'></i>" value="collapseGroup" attributes="data-toggle='collapse' href='#collapseGroup' aria-label='Invite to Talent Pool'" />
					<wm:switch name="profile-action" classlist="profile--quick-action send-assignment tooltipped tooltipped-n" text="<i class='wm-icon-page-out'></i>" attributes="aria-label='Send Assignment'" />
				</c:if>
			</div>


			<%--Add to group drawer--%>
			<div id="collapseGroup" class="accordion-body collapse profile--quick-actions-content">
				<div class="collapse--inner">
					<form action="/groups/manage/add_to_group" id="add_to_group_form" method="post">
						<wm-csrf:csrfToken />
						<p>If the talent pool is public, the worker will receive an invitation via email with information regarding your talent pool. If the talent pool is private, the worker will be added immediately.</p>
						<label>
							Select Talent Pool:
							<select class="wm-select group-select" name="groups-select">
								<option value=""></option>
								<c:forEach var="i" items="${inviteGroups}">
									<option value="<c:out value="${i.key}"/>"><c:out value="${i.value}"/></option>
								</c:forEach>
							</select>
						</label>
						<button class="button -primary" id="group_submit">Invite to Talent Pool</button>
						<button class="button profile--invite-group profile--quick-actions-close" data-toggle="collapse" href="#collapseGroup">Close</button>
					</form>
				</div>
			</div>

			<%--Add test drawer--%>
			<div id="collapseTests" class="accordion-body collapse profile--quick-actions-content">
				<div class="collapse--inner">
					<p>The worker will receive notifications informing them of a pending test invitation including any instructions outlined in the tests description.</p>
					<span class="test-holder"></span>
					<button class="button -primary profile--invite-test" id="invite_to_test_btn">Invite to Test</button>
					<button class="button profile--quick-actions-close" data-toggle="collapse" href="#collapseTests">Close</button>
				</div>
			</div>

			<%--Add tag drawer--%>
			<div id="collapseTags" class="accordion-body collapse profile--quick-actions-content">
				<div class="collapse--inner">
					<form action="/tags/tag_user" id="edit_company_tags_form" method="post">
						<wm-csrf:csrfToken />
						<p>Add tag below...</p>
						<input type="hidden" name="resource_id" id="tags-user-id" value="${facade.id}" />
						<input class="wm-tags tags-input" name="tags" type="text" placeholder="Add a tag..." /><br>

						<div class="pull-right drawer-footer">
							<button class="button -primary profile--add-tag" id="tag-submit">Save Tags</button>
							<button class="button profile--quick-actions-close" data-toggle="collapse" href="#collapseTags">Close</button>
						</div>
					</form>
				</div>
			</div>

			<%--Add comment drawer--%>
			<div id="collapseComment" class="accordion-body collapse profile--quick-actions-content">
				<div class="collapse--inner">
					<form action="/profile/add_comment_to_user" id="add_comment_to_user_form" method="post">
						<wm-csrf:csrfToken />
						<input type="hidden" name="id" id="comment-user-id" value="${facade.userNumber}">

						<textarea name="comment" class="comment-input" placeholder="Add Comment here..."></textarea>

						<div class="pull-right drawer-footer">
							<button type="submit" class="button -primary profile--add-comment">Add Comment</button>
							<button class="button profile--quick-actions-close" data-toggle="collapse" href="#collapseComment">Close</button>
						</div>
					</form>
				</div>
			</div>
		</c:if>

		<ul class="wm-tabs <c:if test="${!isOwner}">full_page_tabs</c:if>">
			<li class="wm-tab -active" data-content="#work">Overview</li>
			<li class="wm-tab" data-content="#qualifications">Qualifications</li>
			<li class="wm-tab" data-content="#ratings">Ratings</li>
			<c:if test="${!isOwner}">
				<li class="wm-tab" data-content="#comments">Comments</li>
			</c:if>
			<c:if test="${!isOwner}">
			<li class="wm-tab" data-content="#tags" data-badge="${fn:length(facade.privateTags)}">Tags</li>
			</c:if>
			<c:if test="${hasVideo || hasPhoto|| isOwner}">
				<li class="wm-tab" data-content="#media">Media</li>
			</c:if>
		</ul>

		<div class="wm-tab--content -active" id="work">
			<div class="user-overview-left">
				<p>
					User ID: <strong><c:out value="${facade.userNumber}"/></strong>
				</p>
				<p>URL: <a class="break-word" href="${baseurl}/profile/${facade.userNumber}">${baseurl}/profile/${facade.userNumber}</a></p>

				<c:if test="${not empty facade.drugTestStatus}">
					<p class="user-drug-test">
						<img class="test-status-image" src="${drugStatusImage}"/>
						<c:choose>
							<c:when test="${facade.drugTestStatus eq 'requested' && facade.priorPassedDrugTest}">
								Drug Test PASSED
							</c:when>
							<c:otherwise>
								Drug Test <c:out value="${fn:toUpperCase(facade.drugTestStatus)}"/>
							</c:otherwise>
						</c:choose>
						<small>(
							<c:choose>
								<c:when test="${not empty facade.lastDrugTestResponseDate}">
									<fmt:formatDate value="${facade.lastDrugTestResponseDate}" dateStyle="medium" type="date" timeZone="${currentUser.timeZoneId}"/>
								</c:when>
								<c:otherwise>
									<fmt:formatDate value="${facade.lastDrugTestRequestDate}" dateStyle="medium" type="date"  timeZone="${currentUser.timeZoneId}"/>
								</c:otherwise>
							</c:choose>
						)</small>
						<c:choose>
							<c:when test="${(isOwner || isInternal) && !(facade.drugTestStatus eq 'requested')}">
								<a href="/screening/drug" class="renew-screening">(Retake)</a>
							</c:when>
							<c:otherwise>
								<c:if test="${facade.priorPassedDrugTest && facade.drugTestStatus eq 'requested'}">
									<span class="renew-screening">(new test results pending)</span>
								</c:if>
							</c:otherwise>
						</c:choose>
					</p>
				</c:if>

				<c:if test="${not empty facade.backgroundCheckStatus}">
					<p class="user-background-check">
						<img class="test-status-image" src="${backgroundStatusImage}"/>
						<c:choose>
							<c:when test="${facade.backgroundCheckStatus eq 'requested' && facade.priorPassedBackgroundCheck}">
								Background Check PASSED
							</c:when>
							<c:otherwise>
								Background Check <c:out value="${fn:toUpperCase(facade.backgroundCheckStatus)}"/>
							</c:otherwise>
						</c:choose>
						<small>(
							<c:choose>
								<c:when test="${not empty facade.lastBackgroundCheckResponseDate}">
									<fmt:formatDate value="${facade.lastBackgroundCheckResponseDate}" dateStyle="medium" type="date"  timeZone="${currentUser.timeZoneId}"/>
								</c:when>
								<c:otherwise>
									<fmt:formatDate value="${facade.lastBackgroundCheckRequestDate}" dateStyle="medium" type="date"  timeZone="${currentUser.timeZoneId}"/>
								</c:otherwise>
							</c:choose>
						)</small>
						<c:choose>
							<c:when test="${(isOwner || isInternal) && !(facade.backgroundCheckStatus eq 'requested')}">
								<a href="/screening/bkgrnd" class="renew-screening">(Renew)</a>
							</c:when>
							<c:otherwise>
								<c:if test="${facade.priorPassedBackgroundCheck && facade.backgroundCheckStatus eq 'requested'}">
									<span class="renew-screening">(new check results pending)</span>
								</c:if>
							</c:otherwise>
						</c:choose>
					</p>
				</c:if>
			</div>


			<div class="user-overview-right">
				<p>Since: <fmt:formatDate value="${memberSince.time}" pattern="MMM yyyy" /></p>
				<c:choose>
					<c:when test="${hasVerifiedTaxEntity}">
						<c:choose>
							<c:when test="${not empty taxEntityCountry && taxEntityCountry eq 'other'}">
								<p>Tax Information: <b>Signed Form W-8</b></p>
							</c:when>
							<c:otherwise>
								<p>Tax Information: <b>Verified</b></p>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:when test="${not hasVerifiedTaxEntity || not hasTaxEntity}">
						<p>Tax Information: <b>Unverified</b></p>
					</c:when>
				</c:choose>
				<c:choose>
					<c:when test="${confirmedBank}">
						<p>Bank Account: <b>Confirmed</b></p>
					</c:when>
					<c:otherwise>
						<p>Bank Account: <b>Unconfirmed</b></p>
					</c:otherwise>
				</c:choose>
				<c:if test="${facade.linkedInVerified}">
					<p>
						<i class="icon-linkedin icon-gray"></i>
						<a target="_blank" href="<c:out value="${facade.linkedInPublicProfileUrl}"/>">LinkedIn</a>
					</p>
				</c:if>
				<c:if test="${not empty facade.companyWebsite}">
					<p>
						<i class="icon-globe icon-gray"></i>
						<c:choose>
							<c:when test="${fn:startsWith(facade.companyWebsite, 'http://')}"><c:set var="companyWebsite" value="${facade.companyWebsite}"/></c:when>
							<c:otherwise><c:set var="companyWebsite" value="http://${facade.companyWebsite}"/></c:otherwise>
						</c:choose>
						<a target="_blank" href="<c:out value="${companyWebsite}"/>"><c:out value="${facade.companyWebsite}"/></a>
					</p>
				</c:if>
			</div>

			<c:choose>
				<c:when test="${not empty facade.overview}">
					<p class="overview-description"><c:out value="${facade.overview}"/></p>
				</c:when>
				<c:otherwise>
					<c:if test="${not empty facade.companyOverview}">
						<p class="overview-description"><c:out value="${facade.companyOverview}"/></p>
					</c:if>
				</c:otherwise>
			</c:choose>
			<%-- /groups panel--%>
			<hr />
			<c:if test="${not isLane4LimitedVisibility}">
				<h2>Talent Pools</h2>
				<sf:select path="inviteGroups" name="id" id="group_id" cssClass="dn" items="${inviteGroups}"/>
				<c:choose>
					<c:when test="${not empty facade.publicGroups or not empty facade.privateGroups}">
						<c:if test="${not empty facade.publicGroups}">
							<div class="user-public-groups">
								<p>Your Public Talent Pools</p>
								<c:forEach var="item" items="${facade.publicGroups}">
									<a class="user-group wm-label -neutral" href="<c:url value='/groups/${item.id}'/>"><c:out value="${item.name}" /></a>
								</c:forEach>
							</div>
						</c:if>
						<c:if test="${not empty facade.privateGroups}">
							<div class="user-private-groups">
								<p>Your Private Talent Pools</p>
								<c:forEach var="item" items="${facade.privateGroups}">
									<a class="user-group wm-label -neutral" href="<c:url value='/groups/${item.id}'/>"><c:out value="${item.name}" /></a>
								</c:forEach>
							</div>
						</c:if>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${isOwner}">
								<p>You are not a member of any talent pools. Join talent pools to be eligible for more
									assignments.</p>
								<a href="/search-groups" class="button -primary">Join talent pools</a>
							</c:when>
							<c:otherwise>
								<p>This worker is not a member of any of your talent pools.</p>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</c:if>
			<hr />
			<h2>Tests</h2>
			<c:choose>
				<c:when test="${not empty facade.assessments and fn:length(facade.assessments) < 6}">
					<table class="user--tests-table">
						<tbody>
							<c:forEach var="item" items="${facade.assessments}">
								<tr>
									<td><a href="<c:url value='/lms/grade/${item.id}/${item.secondaryId}'/>"><c:out value="${item.name}" /></a></td>
									<td>
										<c:choose>
											<c:when test="${item.verificationStatus == 'VERIFIED'}">
												Passed
											</c:when>
											<c:otherwise>
												Failed
											</c:otherwise>
										</c:choose>
										(<fmt:formatNumber value="${item.notes}"/>%)
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:when test="${not empty facade.assessments and fn:length(facade.assessments) >= 6}">
					<table class="user--tests-table" id="tests_limited">
						<tbody>
							<c:forEach var="item" items="${facade.assessments}">
								<tr>
									<td><a href="<c:url value='/lms/grade/${item.id}/${item.secondaryId}'/>"><c:out value="${item.name}" /></a></td>
									<td>
										<c:choose>
											<c:when test="${item.verificationStatus == 'VERIFIED'}">
												Passed
											</c:when>
											<c:otherwise>
												Failed
											</c:otherwise>
										</c:choose>
										(<fmt:formatNumber value="${item.notes}"/>%)
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					<p>No tests have been completed.</p>
					<c:if test="${isOwner}">
						<a href="/lms/view" class="button -primary">Take Tests</a>
					</c:if>
				</c:otherwise>
			</c:choose>

			<hr>

			<c:if test="${hasProfileCustomField}">
				<h2>Custom fields</h2>
				<c:choose>
					<c:when test="${not empty profileCustomFields}">
						<c:forEach var="customField" items="${profileCustomFields}">
							<p><strong>${customField.key}</strong>: ${customField.value}</p>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<p>No custom fields found.</p>
					</c:otherwise>
				</c:choose>
			</c:if>

			<c:if test="${not isOwner and isCompanyAdmin}">
				<h2>
					Roles
					<c:if test="${isCompanyAdmin}">
						<small><a href="<c:url value="/users/edit_user/${facade.userNumber}"/>">Edit</a></small>
					</c:if>
				</h2>

				<p>
					<strong>Roles:</strong> <c:out value="${roleNames}"/></p>
				<p>
					<strong>Role Types:</strong> <c:out value="${laneAccess}"/>
				</p>
			</c:if>

			<c:if test="${not empty facade.esignatures}">
				<h2>
					Signed contracts
				</h2>
				<c:forEach var="esignature" items="${facade.esignatures}">
					<p>
						<a id="esignature" class="esignature-link" data-templateuuid="${esignature.templateUuid}" href="#">
							<c:out value='${esignature.name}'/>
						</a>
					</p>
				</c:forEach>
			</c:if>

			<c:if test="${facade.minOnsiteHourlyRate > 0 || facade.minOffsiteHourlyRate > 0}">
				<article class="user-hourly-rates">
					<h2>Hourly Rates</h2>
					<div>
						<c:if test="${facade.minOnsiteHourlyRate > 0}">
							<p>On-Site: <b><fmt:formatNumber value="${facade.minOnsiteHourlyRate}" type="currency"/> /hr</b></p>
						</c:if>
						<c:if test="${facade.minOffsiteHourlyRate > 0}">
							<p>Virtual: <b><fmt:formatNumber value="${facade.minOffsiteHourlyRate}" type="currency"/> /hr</b></p>
						</c:if>
					</div>
				</article>
			</c:if>
		</div>

		<div class="wm-tab--content" id="qualifications">
			<h2>Industries</h2>
			<p>
				<c:out value='${wmfn:joinPropertyHuman(facade.industries, "name", " , ", " , ")}'/>
			</p>

			<c:if test="${not empty facade.skills}">
				<h2>Skills</h2>
				<c:choose>
					<c:when test="${currentUser.buyer}">
						<c:forEach var="skill" items="${facade.skills}">
							<a class="wm-tag" href='/search?keyword="${skill.name}"'><c:out value="${skill.name}" escapeXml="false"/></a>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<p>
							<c:out value='${wmfn:joinPropertyHuman(facade.skills, "name", " , ", " , ")}'/>
						</p>
					</c:otherwise>
				</c:choose>
			</c:if>
			<c:if test="${not empty facade.specialties}">
				<h2>Products</h2>
				<c:if test="${not empty facade.specialties}">
					<c:choose>
						<c:when test="${currentUser.buyer}">
							<c:forEach var="specialty" items="${facade.specialties}">
								<a class="wm-tag" href="/search?keyword=${specialty.name}"><c:out value="${specialty.name}" escapeXml="false"/></a>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<p>
								<c:out value='${wmfn:joinPropertyHuman(facade.specialties, "name", " , ", " , ")}'/>
							</p>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:if>
			<c:if test="${not empty facade.tools}">
				<h2>Tools</h2>
				<c:if test="${not empty facade.tools}">
					<c:choose>
						<c:when test="${currentUser.buyer}">
							<c:forEach var="tool" items="${facade.tools}">
								<a class="wm-tag" href="/search?keyword=${tool.name}"><c:out value="${tool.name}" escapeXml="false"/></a>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<p>
								<c:out value='${wmfn:joinPropertyHuman(facade.tools, "name", " , ", " , ")}'/>
							</p>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:if>

			<c:if test="${not empty facade.certifications}">
				<h2>Certifications</h2>
				<table class="user--certifications-table">
					<tbody>
						<c:forEach var="certification" items="${facade.certifications}">
							<tr>
								<td>
									<c:out value="${certification.name}"/> (<c:out value="${certification.description}"/>)
									<c:if test="${certification.verificationStatus == 'VERIFIED'}">
										<small> - WM Verified</small>
									</c:if>
								</td>
								<td width="40%">
									<c:if test="${(isOwner or isInternal) and not empty certification.assets}">
										<c:forEach var="asset" items="${certification.assets}">
											<span><a href="<c:url value='/asset/download/${asset.uuid}'/>" title="Download"><i class="icon-download"></i></a></span>
										</c:forEach>
									</c:if>
									<c:if test="${certification.verificationStatus == 'PENDING' and isInternal}">
										<span><a href="<c:url value='/admin/certifications/review'/>"><i class="icon-bullhorn"></i></a></span>
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>

			<c:if test="${not empty facade.licenses}">
				<h2>Licenses</h2>
				<table class="user--licenses-table">
					<tbody>
						<c:forEach var="license" items="${facade.licenses}">
							<tr>
								<td>
									<c:out value="${license.name}"/> (<c:out value="${license.description}"/>)
									<c:if test="${license.verificationStatus == 'VERIFIED'}">
										<small> - WM Verified</small>
									</c:if>
								</td>
								<td width="40%">
									<c:if test="${(isOwner or isInternal) and not empty license.assets}">
										<c:forEach var="asset" items="${license.assets}">
													<a href="<c:url value='/asset/download/${asset.uuid}'/>" title=<c:out value="${asset.name}"/>><i class="icon-download"></i></a>
										</c:forEach>
									</c:if>

									<c:if test="${license.verificationStatus == 'PENDING' and isInternal}">
										<a href="<c:url value="/admin/licenses/review"/>"><i class="icon-bullhorn"></i></a>
									</c:if>
									<c:if test="${license.verificationStatus == 'VERIFIED' and isInternal}">
										<a href="<c:url value="/admin/licenses/edit_userlicense?id=${license.id}&user_id=${facade.id}"/>"><i class="icon-edit"></i></a>
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>

			<c:if test="${not empty facade.insurance}">
				<h2>Insurance Coverage</h2>
				<table class="user--insurance-table">
					<tbody>
						<c:forEach var="insurance" items="${facade.insurance}">
							<tr>
								<td>
									<c:out value="${insurance.name}"/> - <c:out
										value="${insurance.notes}"/>
									<c:if test="${insurance.verificationStatus == 'VERIFIED'}">
										<small> - WM Verified</small>
									</c:if>
								</td>
								<td width="40%">
									<c:if test="${(isOwner or isInternal) and not empty insurance.assets}">
										<c:forEach var="asset" items="${insurance.assets}">
											<a href="<c:url value='/asset/download/${asset.uuid}'/>" title="Download"><i class="icon-download"></i></a>
										</c:forEach>
									</c:if>

									<c:if test="${insurance.verificationStatus == 'PENDING' and isInternal}">
										<a href="<c:url value='/admin/insurance/review'/>" title="Review this"><i class="icon-bullhorn"></i></a>
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>

			<c:if test="${facade.linkedInVerified and not empty facade.linkedInPositions}">
				<h2>Employment</h2>
				<table class="user--positions-table">
					<tbody>
						<c:forEach var="h" items="${facade.linkedInPositions}">
							<tr>
								<td>
									<strong><c:out value="${h.title}"/></strong>
									<c:out value="${h.companyName}"/>
								</td>
								<td width="40%">
									<c:choose>
										<c:when test="${not empty h.dateFromMonth}">
											<fmt:parseDate value="${h.dateFromMonth}" pattern="M" var="monthName"/>
											<fmt:formatDate value="${monthName}" pattern="MMM"/>
											<c:out value="${h.dateFromYear}"/>
										</c:when>
										<c:otherwise>
											<c:out value="${h.dateFromYear}"/>
										</c:otherwise>
									</c:choose>
									&ndash;
									<c:choose>
										<c:when test="${h.current}">
											Present
										</c:when>
										<c:when test="${not empty h.dateToMonth}">
											<c:out value="${wmfmt:monthName(h.dateToMonth)}"/>
											<c:out value="${h.dateToYear}"/>
										</c:when>
										<c:otherwise>
											<c:out value="${h.dateToYear}"/>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>

			<c:if test="${facade.linkedInVerified and not empty facade.linkedInEducation}">
				<h2>Education</h2>
				<table class="user--education-table">
					<tbody>
						<c:forEach var="h" items="${facade.linkedInEducation}">
							<tr>
								<td>
									<strong><c:out value="${h.schoolName}"/></strong>
									<c:if test="${not empty h.degree}">
									<c:out value="${h.degree}"/>,</c:if>
										<c:if test="${not empty h.fieldOfStudy}"><c:out value="${h.fieldOfStudy}"/>
									</c:if>
								</td>
								<td width="40%">
									<c:choose>
										<c:when test="${not empty h.dateFromMonth}">
											<fmt:parseDate value="${h.dateFromMonth}" pattern="M" var="monthName"/>
											<fmt:formatDate value="${monthName}" pattern="MMM"/>
											<c:out value="${h.dateFromYear}"/>
										</c:when>
										<c:otherwise>
											<c:out value="${h.dateFromYear}"/>
										</c:otherwise>
									</c:choose>
									&ndash;
									<c:choose>
										<c:when test="${empty h.dateToYear}">
											Present
										</c:when>
										<c:when test="${not empty h.dateToMonth}">
											<c:out value="${wmfmt:monthName(h.dateToMonth)}"/>
											<c:out value="${h.dateToYear}"/>
										</c:when>
										<c:otherwise>
											<c:out value="${h.dateToYear}"/>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>

			<c:if test="${not empty facade.resumes}">
				<h2>Resume Download</h2>
				<ul>
					<c:forEach var="resume" items="${facade.resumes}">
						<li>
							<i class="icon-file-alt"></i>
							<a href="<c:url value="/asset/download/${resume.meta.uuid}"/>">Download Resume</a>
						</li>
					</c:forEach>
				</ul>
			</c:if>

			<c:if test="${not empty facade.languages}">
				<h2>Languages</h2>
				<ul>
					<c:forEach var="item" items="${facade.languages}">
						<li><strong><c:out value="${item.name}" /></strong></li>
					</c:forEach>
				</ul>
			</c:if>

			<c:if test="${not empty facade.workingHours}">
				<h2>Working Hours</h2>
				<table class="user--working-hours-table">
					<tbody>
						<c:forEach var="item" items="${facade.workingHours}">
							<c:if test="${not item.deleted}">
								<tr>
									<td><c:out value="${item.weekDayName}" /></td>
									<td>
										<strong>
											<c:choose>
												<c:when test="${item.allDayAvailable}">
													All Day
												</c:when>
												<c:otherwise>
													<fmt:formatDate value="${item.fromTime.time}" pattern="h:mmaa" timeZone="${currentUser.timeZoneId}"/>
													-
													<fmt:formatDate value="${item.toTime.time}" pattern="h:mmaa" timeZone="${currentUser.timeZoneId}"/>
												</c:otherwise>
											</c:choose>
										</strong>
									</td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
		</div>

		<div class="wm-tab--content" id="ratings">
			<h2>Ratings</h2>
			<c:if test="${!isOwner}">
				<div class="pull-right">
					<button type="button" name="scopeToCompany" value="false" class="button">All Ratings</button>
					<button type="button" name="scopeToCompany" value="true" class="button">Your Ratings</button>
				</div>
			</c:if>
			<div class="ratings-well well-content">
				<c:import url="/profile/${facade.userNumber}/ratings"/>
			</div>
		</div>

		<c:if test="${!isOwner}">
			<div class="wm-tab--content" id="comments">
				<h2>Comments</h2>
				<div class="no_comment">You have no comments recorded for this user.</div>
				<div class="has_comments">
					<table id="user_comment_table" class="user-comments--table">
						<thead>
						<tr>
							<th id="date">Date</th>
							<th id="comment">Comment</th>
							<th id="from">From</th>
							<th class="text-center" id="action">Actions</th>
						</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>
			</div>
		</c:if>

		<div class="wm-tab--content" id="tags">
			<c:choose>
				<c:when test="${allowTagging and not isOwner and not isLane4LimitedVisibility}">
					<h2>Tags</h2>
					<c:choose>
						<c:when test="${not empty facade.privateTags}">
							<c:forEach var="tag" items="${facade.privateTags}">
								<span class="wm-tag"><c:out value="${tag}"/></span>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<p class="no-results-copy">There are currently no tags on this profile. Add one above.</p>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<p class="no-results-copy">Add the worker to your network above to utilize the Tags feature.</p>
				</c:otherwise>
			</c:choose>
		</div>

		<div class="wm-tab--content" id="media">
			<c:if test="${isOwner}">
				<button class="button -active add-media">Upload Photo/Video</button>
				<p>Feature up to 10 photos or videos to feature on your profile. These could be from your work portfolio, promotional images, or other professional work.</p>
			</c:if>
			<c:set var="totalImages" scope="session" value="0"/>
			<section class="gallery">
				<c:forEach var="data" items="${ImageOutput}">
					<c:if test="${data.assetResourceType == 'ASSET'}">
						<c:if test="${isOwner || (isGroupOwner && (data.availability.code == 'group')) || data.availability.code == 'all'}">
							<c:choose>
								<c:when test="${data.media}">
									<div class="profile_video" id="videoplayer_${data.id}" data-order="${totalImages}" data-id="${data.id}" style="width: 100%;  margin-bottom: 1em;"></div>
								</c:when>
								<c:otherwise>
									<a class="gallery--image"
									   data-uri="${data.uri}"
									   data-total="${totalImages}"
									   data-desc="${data.description}"
									   data-code="${data.availability.code}"
									   data-bytes="${data.byteCountToDisplaySize}"
									   data-name="${data.name}"
									   data-id="${data.id}"
									   data-uuid="${data.UUID}"
									   data-type="asset"
										>
										<img src="${ImageLargeOutput[totalImages].relativeUri}" default="${mediaPrefix}/images/no_picture.png" alt="Photo"/>
									</a>
								</c:otherwise>
							</c:choose>
						</c:if>
					</c:if>
					<c:if test="${data.assetResourceType == 'LINK'}">
						<video id="videoplayer_video_${data.id}" class="video-js vjs-default-skin" controls preload="auto" width="120" height="120" poster="http://video-js.zencoder.com/oceans-clip.png">
							<source src="${data.remoteUri}" type="video/youtube" />
						</video>
					</c:if>
					<c:set var="totalImages" scope="session" value="${totalImages + 1}"/>
				</c:forEach>
			</section>
		</div>
	</section>

	<aside class="sidebar-card">
		<div id="scorecard-holder"></div>
		<button id="show-ratings" class="sidebar-card--button">See Ratings</button>
	</aside>

	<c:if test="${not isUserTelaidPrivate}">
		<aside class="profile--todo-messages">
			<c:if test="${facade.lane3Active and isOwner and displayGetWork }">
				<div class="alert alert-success">
					<input type="hidden" name="shared_worker_role" value="0" />
					<div class="tac">Your profile is listed in search results. <a href="/profile-edit/lanes">(edit)</a></div>
				</div>
			</c:if>

			<c:if test="${isOwner and not facade.lane3Active}">
				<form action='/profile-edit/lanes' method="post">
					<wm-csrf:csrfToken />
					<c:choose>
						<c:when test="${facade.lane3Pending and displayGetWork}">
							<div class="alert alert-success">
								<p>Your profile is pending approval for search listing. You will be notified when you are listed in search results or if profile changes are required.</p>
							</div>
						</c:when>
						<c:otherwise>
							<vr:rope>
								<vr:venue bypass="true" name="HIDE_SEARCH_OPT_IN">
									<div class="alert alert-success">
										<strong>Promote Your Profile in Search Results</strong>
										<input type="hidden" name="shared_worker_role" value="1" />
										<p>Professionals who are part of an extended workforce and are seeking assignments from other companies using WorkMarket must opt in to be listed in the global WorkMarket search results. Search results include independent contractors, temporary workers, professional consultants and other non-employees.</p>
										<button type="submit" class="button">List me in search</button>
									</div>
								</vr:venue>
							</vr:rope>
						</c:otherwise>
					</c:choose>
				</form>
			</c:if>
		</aside>
	</c:if>
	<c:if test="${isOwner}">
		<aside class="sidebar-card profile--todo">
			<h2 class="sidebar-card--title">Profile To Do</h2>
			<jsp:include page="/WEB-INF/views/web/partials/profile/completeness-list.jsp"/>
		</aside>
	</c:if>

	<%-- /modals on page --%>

	<%-- /block worker modal--%>
	<c:if test="${!isOwner}">
		<div class="dn">
			<c:if test="${isAdmin}">
				<div id="do_blocking">
					<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp"/>
					<form action="/profile-edit/blockresource" id="form_block-resource" method="post" class="form-stacked">
						<wm-csrf:csrfToken />
						<input type="hidden" name="resource_id" value="<c:out value="${facade.id}"/>"/>
						<p>This worker will not be able to take on work for your company and will be removed from all of your talent pools. Are you sure you want to block this worker?</p>

						<div class="wm-action-container">
							<button type="submit" id="block-resource" class="button">Block Worker</button>
						</div>
					</form>
				</div>
			</c:if>
		</div>
	</c:if>


	<%-- /delete comment --%>
	<div id="delete_comment_modal" class="modal hide" tabindex="-1" role="dialog" aria-hidden="true">

		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
			<h3>Delete Comment</h3>
		</div>
		<div class="modal-body">
			<p>Are you sure you want to delete this comment?</p>
		</div>
		<div class="wm-action-container">
			<button class="button" data-dismiss="modal" aria-hidden="true">Close</button>
			<button id="delete_comment_confirm" class="button"><i class="wm-icon-trash"></i> Delete</button>
		</div>
	</div>
</div>

<sf:select path="inviteGroups" id="company_groups" cssClass="dn" items="${inviteGroups}"/>
<script id="cell-checkbox-tmpl" type="text/x-jquery-tmpl">
	<div>
		{{if (meta.attemptStatusTypeCode == null || (meta.completed && !meta.passed))}}
			<input type="checkbox" name="assessment_ids[]" value="\${meta.assessmentId}" />
		{{/if}}
	</div>
</script>

<script id="cell-name-tmpl" type="text/x-jquery-tmpl">
	<div>
		<a href="/lms/view/details/\${meta.assessmentId}">\${data}</a>
		<span class="gray">
		{{if meta.attemptStatusTypeCode == 'inprogress'}}
			(in progress)
		{{else meta.completed && meta.passed}}
			(passed)
		{{else meta.completed && !meta.passed}}
			(failed)
		{{else meta.invitationStatus == 'sent'}}
			(invited)
		{{/if}}
		</span>
	</div>
</script>

<script type="text/x-jquery-tmpl" id="qq-uploader-tmpl">
	<div class="qq-uploader">
		<ul class="qq-upload-list dn"></ul>
		<a href="javascript:void(0);" class="qq-upload-button button -primary" data-loading-text="Uploading..."><span>Upload File</span></a>
		<div class="qq-upload-drop-area">
			<span>Drop your background image here</span>
		</div>
	</div>
</script>
</wm:app>
