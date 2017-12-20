<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>

<div class="clear">
	<ul class="tabs">
		<li ${(requestScope.selected_navigation_link eq 'groups.search') ? 'class="active"' : '""'}><a href="/search-groups" name="groups.search">Talent Pool Directory</a></li>
		<c:if test="${currentUser.buyer}">
			<li ${(requestScope.selected_navigation_link eq 'groups.view.index') ? 'class="active"' : '""'}><a href="/groups" name="groups.view.index">${currentUser.companyName} Talent Pools</a></li>
		</c:if>
		<c:if test="${currentUser.seller || currentUser.dispatcher}">
			<li ${(requestScope.selected_navigation_link eq 'groups.view.memberships') ? 'class="active"' : '""'}><a href="/groups/memberships" name="groups.view.memberships">Memberships</a></li>
		</c:if>

		<c:if test="${not empty(requestScope.group_sort_options)}">
			<div class="fr">
				<select id="sortby" name="sortby">
					<c:forEach items="${requestScope.group_sort_options}" var="option">
						<option value='${option.key}'><c:out value="${option.value}" /></option>
					</c:forEach>
				</select>
			</div>
		</c:if>
	</ul>
</div>