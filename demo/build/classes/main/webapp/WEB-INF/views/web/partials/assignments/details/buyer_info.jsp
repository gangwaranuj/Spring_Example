<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:if test="${showContact}">
	<dl class="iconed-dl">
		<dt><jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/phone_v2.jsp"/></dt>
		<dd>
			<strong><c:out value="${work.buyer.name.firstName}" /> <c:out value="${work.buyer.name.lastName}" /></strong>
		<c:if test="${is_admin}">
			<small>
				<a class="edit_internal_owner_action edit-special" title="Internal Owner" href="/assignments/edit_internal_owner/${work.workNumber}">Edit</a>
			</small>
		</c:if>
		<br/>
			<c:if test="${is_resource || is_admin}">
				<c:if test="${not empty work.buyer.profile.phoneNumbers}">
					<c:forEach items="${work.buyer.profile.phoneNumbers}" var="phone">
						${wmfmt:phone(phone.phone)}
						<c:if test="${not empty phone.extension}">x ${phone.extension}</c:if><br/>
					</c:forEach>

				</c:if>
				<small><a href="mailto:${work.buyer.email}"><c:out value="${work.buyer.email}" /></a></small>
			</c:if>
		</dd>
	</dl>
</c:if>
