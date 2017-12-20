<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<dl class="iconed-dl">
	<dt><jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/company_v2.jsp"/></dt>
	<dd>
		<strong>
			<c:choose>
				<c:when test="${buyerScoreCard.hasBadScore()}" >
					<span class="buyer-scorecard-warning"><a href="/profile/company/${work.company.companyNumber}">${work.company.name}</a></span>
				</c:when>
				<c:otherwise>
					<a href="/profile/company/${work.company.companyNumber}">${work.company.name}</a>
				</c:otherwise>
			</c:choose>

		</strong><br/>

		<c:if test="${not empty work.clientCompany}">
			For: ${work.clientCompany.name} <br/>
		</c:if>
		<c:if test="${is_admin && not empty work.project}">
			Project: <a href="/projects/view/${work.project.id}">${work.project.name}</a><br/>
		</c:if>
	</dd>
</dl>
