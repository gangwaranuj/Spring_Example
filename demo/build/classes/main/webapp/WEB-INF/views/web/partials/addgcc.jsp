<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:if test="${showGccBanner and currentUser.seller}">
	<div class="row">
		<div class="span16">
			<div id="gcc_banner" class="alert alert-info clear">
				<div class="row">
					<div class="span12">
						<p>
							<span class="label success"><fmt:message key="global.new" /></span>
							<strong><fmt:message key="accounts.get_wm_visa" /></strong>
						</p>
						<p>
							<fmt:message key="accounts.apply_today" />
						</p>
						<a class="button" href="/funds/accounts/gcc"><fmt:message key="accounts.get_it_now" /></a>
					</div>
					<div class="pull-right span3">
						<img id="gcc_card" src="${mediaPrefix}/images/wm_card_website.png" height="160" width=""/>
					</div>
				</div>

			</div>
		</div>
	</div>
</c:if>
