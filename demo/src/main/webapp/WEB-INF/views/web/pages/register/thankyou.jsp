<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<wm:whitelabel bodyClass="registration-landing" pageTitle="Thank you">

<div id="campaign_landing" class="content" style="margin-bottom: 40px">
	<div class="page-header title">
		<h2>
			<fmt:message key="global.thankyou" /> <c:if test="${not empty company.effectiveName}"> <fmt:message key="global.from" /> <c:out value="${company.effectiveName}" /></c:if>
		</h2> <fmt:message key="thankyou.steps" />
	</div>

	<div class="row_sidebar_right">
		<div class="content">
			<h3><fmt:message key="thankyou.next_steps" /></h3>
			<c:choose>
			<c:when test="${not empty user}">
				<div class="alert-message warning">
					<fmt:message key="thankyou.already_registered" /> <c:if test="${not empty company.effectiveName}"> <fmt:message key="thankyou.with" /> <c:out value="${company.effectiveName}" /></c:if> <fmt:message key="thankyou.anyone_can_invite" />.
				</div>
			</c:when>
			<c:otherwise>
				<p><fmt:message key="thankyou.email_complete" />.</p>
				<p><fmt:message key="thankyou.no_email" />.</p>
			</c:otherwise>
			</c:choose>
		</div>
		<div class="sidebar">
			<div class="tac">
				<c:choose>
					<c:when test="${not empty invitation}">
						<c:if test="${not empty invitation.companyLogo}">
							<img id="register-logo" src="<c:out value="${wmfn:stripUriProtocol(wmfmt:stripXSS(invitation.companyLogo.uri))}" />" alt="Logo" style="max-height: 80px; max-width: 160px;"/>
						</c:if>
					</c:when>
					<c:when test="${not empty campaign}">
						<c:if test="${not empty campaign.companyLogo}">
							<img id="register-logo" src="<c:out value="${wmfn:stripUriProtocol(wmfmt:stripXSS(campaign.companyLogo.uri))}" />" alt="Logo" style="max-height: 80px; max-width:160px;"/>
						</c:if>
					</c:when>
				</c:choose>
			</div>
		</div>
	</div>
</div>

<script>
(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  			(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  			m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  			})(window,document,'script','//www.google-analytics.com/analytics.js','ga');

	  			ga('create', 'UA-16961266-1', 'auto');

	  			ga('require', 'displayfeatures');
					ga('send', 'pageview');
</script>
</wm:whitelabel>
