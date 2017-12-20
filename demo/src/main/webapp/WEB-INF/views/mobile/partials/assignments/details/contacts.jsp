<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:if test="${(work.status.code ne WorkStatusType.SENT) and (not empty work.locationContact or not empty work.supportContact or not empty work.buyer)}">
	<div data-theme="a" data-content-theme="a" data-iconpos="right" >
		<h3>Contacts</h3>
		<c:if test="${not empty work.locationContact}">
			<h5 class="mb0 mt0">Location Contact:</h5>
			<c:out value="${work.locationContact.name.firstName}" /> <c:out value="${work.locationContact.name.lastName}" /><br />
			<c:if test="${not empty work.locationContact.email}">
				<a data-ajax="false" href="mailto:<c:out value="${work.locationContact.email}"/>"><c:out value="${work.locationContact.email}"/></a><br />
			</c:if>
			<c:if test="${not empty work.locationContact.profile.phoneNumbers}">
				<c:forEach var="phone" items="${work.locationContact.profile.phoneNumbers}">
					<c:if test="${not empty phone.phone}">
						<a href="tel:<c:out value="${phone.phone}"/>"><c:out value="${wmfmt:phone(phone.phone)}"/></a>
						<c:if test="${phone.extension}">
							x<c:out value="${phone.extension}"/>
						</c:if>
						<br />
					</c:if>
				</c:forEach>
			</c:if>

		<div class="border-bottom gap2 br"></div>
		</c:if>

		<c:if test="${not empty work.supportContact}">
			<h5 class="mb0 mt0">Support Contact:</h5>
			<c:out value="${work.supportContact.name.firstName}" /> <c:out value="${work.supportContact.name.lastName}" /><br />
			<c:if test="${not empty work.supportContact.email}">
				<a data-ajax="false" href="mailto:<c:out value="${work.supportContact.email}"/>"><c:out value="${work.supportContact.email}"/></a><br />
			</c:if>
			<c:if test="${not empty work.supportContact.profile.phoneNumbers}">
				<c:forEach var="phone" items="${work.supportContact.profile.phoneNumbers}">
					<c:if test="${not empty phone.phone}">
						<a href="tel:<c:out value="${phone.phone}"/>"><c:out value="${wmfmt:phone(phone.phone)}"/></a>
						<c:if test="${phone.extension}">
							x<c:out value="${phone.extension}"/>
						</c:if>
						<br />
					</c:if>
				</c:forEach>
			</c:if>
			<div class="border-bottom gap2 br"></div>
		</c:if>

		<c:if test="${not empty work.buyer}">
			<h5 class="mb0 mt0">Owner:</h5>
			<c:out value="${work.buyer.name.firstName}" /> <c:out value="${work.buyer.name.lastName}" /><br />
			<c:if test="${not empty work.buyer.email}">
				<a data-ajax="false" href="mailto:<c:out value="${work.buyer.email}"/>"><c:out value="${work.buyer.email}"/></a><br />
			</c:if>
			<c:if test="${not empty work.buyer.profile.phoneNumbers}">
				<c:forEach var="phone" items="${work.buyer.profile.phoneNumbers}">
					<c:if test="${not empty phone.phone}">
						<a href="tel:<c:out value="${phone.phone}"/>"><c:out value="${wmfmt:phone(phone.phone)}"/></a>
						<c:if test="${phone.extension}">
							x<c:out value="${phone.extension}"/>
						</c:if>
						<br />
					</c:if>
				</c:forEach>
			</c:if>
		</c:if>
	</div>
</c:if>

