<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<p>This assignment is part of a bundle. Only the client who creatd the assingment can cancel it.</p>

<p>Please use the information below to contact the client for this assignment.</p>

<!-- client -->
<dl class="iconed-dl">
	<dt><i class="icon-building icon-gray icon-large"></i></dt>
	<dd>
		<strong><c:out value="${work.company.name}" /></strong><br/>

		<c:if test="${not empty work.clientCompany}">
			For: <c:out value="${work.clientCompany.name}" /><br/>
		</c:if>
		<c:if test="${is_admin && not empty work.project}">
			<a href="/projects/view/${work.project.id}"><c:out value="${work.project.name}" /></a><br/>
		</c:if>
	</dd>
</dl>

<dl class="iconed-dl">
	<dt><i class="icon-phone icon-gray icon-large"></i></dt>
	<dd>
		<strong><c:out value="${work.buyer.name.firstName}" /> <c:out value="${work.buyer.name.lastName}" /></strong><br/>
		<c:if test="${is_active_resource || is_admin}">
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
