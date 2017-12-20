<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Landing Page Details"
	bodyclass="accountSettings"
	webpackScript="campaigns"
>
	<c:set var="req" value="${pageContext.request}" />
	<c:set var="uri" value="${req.requestURI}" />
	<c:set var="baseUrl" value="${fn:replace(req.requestURL, fn:substring(uri, 0, fn:length(uri)), req.contextPath)}"/>

	<script>
		var config = {
			name: 'campaigns',
			type: 'details',
			hasShortURL: ${not empty campaign.shortUrl},
			shortURL: '${wmfmt:escapeJavaScript(campaign.shortUrl)}',
			encryptedId: '${wmfmt:escapeJavaScript(campaign.encryptedId)}',
			baseUrl: '${baseUrl}',
			showStats: ${showStats},
			campaignId: ${campaign.id}
		}
	</script>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	 </c:import>

	<div class="content">
		<div class="inner-container">

		<!--Landing Page and Invitations Tabs Navigations-->
		<c:import url="/WEB-INF/views/web/partials/recruiting/navigation.jsp"/>

			<div class="page-header clear">
				<h3 class="fl"><c:out value="${campaign.title}" /></h3>
				<div class="fr"><a class="button" href="/campaigns">Back to list</a></div>
			</div>

			<div class="well-b2">
			<h3>Landing Page Description</h3>
				<div class="well-content">
					<p>${wmfmt:tidy(wmfmt:nl2br(campaign.description))}</p>
				</div>
			</div>

			<c:choose>
				<c:when test="${empty campaign.companyOverview}">
					<div class="well-b2">
					<h3>Company Overview</h3>
						<div class="well-content">
							<p>${wmfmt:tidy(wmfmt:nl2br(campaign.company.overview))}</p>
						</div>
					</div>
					</c:when>
					<c:otherwise>
					<div class="well-b2">
						<h3>Custom Company Overview</h3>
						<div class="well-content">
							<p>${wmfmt:tidy(wmfmt:nl2br(campaign.companyOverview))}</p>
						</div>
					</div>
				</c:otherwise>
			</c:choose>

			<div class="well-b2">
				<h3>Landing Page URL</h3>
				<div class="well-content">
					<p>Share your landing page with contractors and friends notifying them you're interested in doing business with them on Work Market.</p>
					<p style="word-wrap: break-word;"><a href="${baseUrl}/register/campaign/${campaign.encryptedId}">${baseUrl}/register/campaign/${campaign.encryptedId}</a></p>
				</div>
			</div>

			<c:if test="${not empty campaign.shortUrl}">
				<div class="well-b2">
					<h3>Landing Page Short URL</h3>
					<div class="well-content">
						<p><a class="overflow-uri" href="<c:out value="${campaign.shortUrl}"/>" class=""><c:out value="${campaign.shortUrl}" /></a></p>
					</div>
				</div>
			</c:if>

			<div class="wm-action-container">
				<a class="button" href="/campaigns/${campaign.id}/edit">Edit my landing page</a>
			</div>

		</div>
	</div>

	<div class="content">
		<div class="well-b2">
			<h3>Recruits registered via this landing page</h3>
			<div class="well-content">

				<form accept-charset="utf-8" id="recruit_actions_form" method="post" action="/campaigns/${campaign.id}/recruit_actions">
					<wm-csrf:csrfToken />
					<div id="table_recruits">
						<table id="recruits_list">
							<thead>
								<tr>
									<th class="sorting_disabled" rowspan="1" colspan="1"></th>
									<th class="sorting" rowspan="1" colspan="1">Name</th>
									<th class="sorting_disabled" rowspan="1" colspan="1">Company</th>
									<th class="sorting" rowspan="1" colspan="1">Email Confirmed</th>
									<th class="sorting_disabled" rowspan="1" colspan="1">Talent Pool Status</th>
									<th class="sorting" rowspan="1" colspan="1">Registration Date</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>
					<div class="table_recruits_msg"></div>
					<div>
						<select name="action" id="recruits_dropdown">
							<option value="add">Add to my network</option>
							<option value="remove">Remove from my network</option>
						</select>
						<a id="submit-recruit_actions" class="button">Submit</a>
					</div>
				</form>

			</div>
		</div>
		
	</div>

</wm:app>
