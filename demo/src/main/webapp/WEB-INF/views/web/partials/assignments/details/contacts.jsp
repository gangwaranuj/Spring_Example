<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<div class="well-b2">
	<h3>Support Contact
		<c:if test="${is_admin}">
			<small class="pull-right sidebar-action">
				<a class="edit_support_contact_action pull-right edit-special" title="Support Contact" href="/assignments/edit_support_contact/${work.workNumber}">Edit</a>
			</small>
		</c:if>
	</h3>
	<div class="well-content">
		<p>
			<strong><c:out value="${work.supportContact.name.firstName}" /> <c:out value="${work.supportContact.name.lastName}" /></strong>
			<br/>
			<c:forEach items="${work.supportContact.profile.phoneNumbers}" var="phone">
				<c:if test="${not empty phone.phone}">
					${wmfmt:phone(phone.phone)}
					<c:if test="${not empty phone.extension}">
						x<c:out value="${phone.extension}"/>
					</c:if>
					<br/>
				</c:if>
			</c:forEach>
			<small><c:out value="${work.supportContact.email}"/></small><br/>
		</p>
		<c:set var="hasLocationContacts" value="${not empty work.locationContact or not empty work.secondaryLocationContact}"/>

		<c:if test="${not empty work.location and hasLocationContacts}">
			<h6>Onsite Contacts
				<c:if test="${is_admin}">
					<a class="edit_location_contact_action pull-right edit-special"  title="Location Contact" style="text-transform: none;" href="/assignments/edit_location_contact/${work.workNumber}">Edit</a>
				</c:if>
			</h6>
			<c:if test="${not empty work.locationContact}">
				<p>
					<strong><c:out value="${work.locationContact.name.firstName}" /> <c:out value="${work.locationContact.name.lastName}" /></strong>
				</p>
				<p>
					<c:forEach items="${work.locationContact.profile.phoneNumbers}" var="phone">
						<c:if test="${not empty phone.phone}">
							${wmfmt:phone(phone.phone)}
							<c:if test="${not empty phone.extension}">
								x<c:out value="${phone.extension}"/>
							</c:if>
							<br/>
						</c:if>
					</c:forEach>
					<c:if test="${is_admin}"><c:out value="${work.locationContact.email}"/><br/></c:if>
				</p>
			</c:if>

			<c:if test="${not empty work.secondaryLocationContact}">
				<p>
					<strong><c:out value="${work.secondaryLocationContact.name.firstName}" /> <c:out value="${work.secondaryLocationContact.name.lastName}" /></strong>
				</p>
				<p>
					<c:forEach items="${work.secondaryLocationContact.profile.phoneNumbers}" var="phone">
						<c:if test="${not empty phone.phone}">
							${wmfmt:phone(phone.phone)}
							<c:if test="${phone.extension}">
								x<c:out value="${phone.extension}"/>
							</c:if>
							<br/>
						</c:if>
					</c:forEach>
					<c:if test="${is_admin}"><c:out value="${work.secondaryLocationContact.email}"/></c:if>
				</p>
			</c:if>
		</c:if>
		<c:if test="${is_admin}">
			<p>
				<c:if test="${!hasLocationContacts and not empty work.location}">
					<small>
						<a class="edit_location_contact_action" href="/assignments/edit_location_contact/${work.workNumber}">Add location contacts</a>
					</small>
				</c:if>
			</p>
		</c:if>
	</div>
</div>
