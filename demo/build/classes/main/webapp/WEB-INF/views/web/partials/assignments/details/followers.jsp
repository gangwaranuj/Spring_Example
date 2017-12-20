<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="well-b2">
	<h3>Followers
		<small class="pull-right sidebar-action">
			<c:if test="${is_admin && !is_owner}">
				<c:choose>
					<c:when test="${is_following}">
						<a href="javascript:void(0);" data-href="/assignments/toggleFollow/${work.workNumber}" class="js-follow is-following tooltipped tooltipped-n" aria-label="Unfollow this assignment">Unfollow</a>
					</c:when>
					<c:otherwise>
						<a href="javascript:void(0);" data-href="/assignments/toggleFollow/${work.workNumber}" class="js-follow tooltipped tooltipped-n" aria-label="Follow this assignment">Follow</a>
					</c:otherwise>
				</c:choose>
			</c:if>
		</small>
	</h3>
	<div class="well-content" id="followers_container">
		<p class="help-block">Select names of one or more people who should get notifications related to this assignment.</p>
		<div id="followers_list"></div>
		<br/>
		<div class="mr">
			<select name="followers" id="followers" data-placeholder="Begin Typing To Add Follower">
				<%--Here for placeholder text--%>
				<option>-</option>
				<c:forEach var="follower" items="${followers}">
					<option value="<c:out value="${follower.key}"/>"><c:out value="${follower.value}" /></option>
				</c:forEach>
			</select>
		</div>
		<div class="wm-action-container">
			<a href="/assignments/add_followers/${work.workNumber}" class="button" id="new_followers_save">Save Followers</a>
		</div>
	</div>
</div>