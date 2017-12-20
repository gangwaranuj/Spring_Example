<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="well-b2">
	<h3>Explore</h3>
	<div class="well-content">
		<ul class="unstyled">
			<c:if test="${currentUser.seller || currentUser.dispatcher}">
				<li ${(requestScope.selected_navigation_link eq 'groups.search') ? 'class="active"' : '""'}><a href="/search-groups" name="groups.search">Browse Talent Pools</a></li>
			</c:if>
			<c:if test="${currentUser.buyer}">
				<li ${(requestScope.selected_navigation_link eq 'groups.view.index') ? 'class="active"' : '""'}><a href="/groups" name="groups.view.index"><c:out value="${currentUser.companyName}" /> Talent Pools</a></li>
			</c:if>
			<li id="invitation-count"><a href="/groups/invitations">Pending Invitations (<span><c:out value="${not empty groupInvitationsCount ? groupInvitationsCount : 0}"/></span>)</a></li>
			<li id="membership-count"><a href="/groups/memberships">Memberships (<span><c:out value="${not empty groupMembershipsCount ? groupMembershipsCount : 0}"/></span>)</a></li>
		</ul>
	</div>
</div>
