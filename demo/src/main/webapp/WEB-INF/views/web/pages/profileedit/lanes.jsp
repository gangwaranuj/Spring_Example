<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Lanes" bodyclass="accountSettings">

<div class="row_sidebar_left">
	<div class="sidebar">
		<c:set var="selected_navigation_link" value="myprofile.lanes" scope="request"/>
		<jsp:include page="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp"/>
	</div>

	<div class="content">
		<h2>Getting Listed in Search</h2>

		<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

		<form action="/profile-edit/lanes" method="post" accept-charset="utf-8">
			<wm-csrf:csrfToken />

			<p>
				Professionals who are part of an extended workforce and are seeking assignments from other
				companies using Work Market must opt in to be listed in the global Work Market search results.
				Search results include independent contractors, temporary workers, professional consultants and
				other non-employees.
			</p>

			<p>
				By opting in to be listed in search results, you are letting people know that you are
				available to work for companies other than your own.
			</p>

			<c:choose>
				<c:when test="${requestScope.has_shared_worker_role and requestScope.is_lane3_pending}">
					<div class="alert tac"><p>
						Your request is pending Work Market approval.
					</p></div>
				</c:when>
				<c:when test="${requestScope.has_shared_worker_role and requestScope.is_lane3_approved}">
					<div class="alert"><p>
						If you cancel participation in search results,
						your relationships with Work Market users as a Third Party worker will also be
						cancelled. Use the vacation/on hold feature if you want to temporarily stop receiving
						assignments, or block specific clients if you want to stop assignments only from certain
						clients.
					</p></div>
				</c:when>
			</c:choose>

			<div class="actions">
				<c:choose>
					<c:when test="${not requestScope.is_lane3_pending && not requestScope.is_lane3_approved}">
						<input type="hidden" name="shared_worker_role" value="1"/>
						<button type="submit" class="button">List me in search results</button>
					</c:when>
					<c:when test="${requestScope.is_lane3_approved}">
						<input type="hidden" name="shared_worker_role" value="0"/>
						<button type="submit" class="button">Remove me from search results</button>
					</c:when>
				</c:choose>
			</div>
		</form>


	</div>
</div>
</wm:app>
