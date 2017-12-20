<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Forums" bodyclass="Forums" breadcrumbSection="Market" breadcrumbSectionURI="/search" breadcrumbPage="Forums" webpackScript="forums">

	<script>
		var config = {
			type: 'discussionPage',
			isInternal: ${isInternal},
			isUserBanned: ${isUserBanned}
		}
	</script>

<div class="forums-header">
	<a href="/forums" id="forumHomeLink">
		<jsp:include page="/WEB-INF/views/web/partials/svg-icons/forums/forums_title.jsp"/>
	</a>
	<h3 class="title">${wmfmt:escapeHtml(post.title)}</h3>
</div>
<div class="container">

	<div class="row">
		<div class="span11">
			<div class="inner-container-topic">

				<div class="forums-discussion-header">
					<c:choose>
						<c:when test="${isFollowing}">
							<a href="/forums/follow/${post.id}" id="followPostBtn" class="tooltipped tooltipped-n" aria-label="Unfollow this post"><i class="wm-icon-follow"></i></a>
						</c:when>
						<c:otherwise>
							<a href="/forums/follow/${post.id}" id="followPostBtn" class="tooltipped tooltipped-n" aria-label="Follow this post"><i class="wm-icon-unfollow"></i></a>
						</c:otherwise>
					</c:choose>
					<div class="forums-user-icon">
					<c:choose>
						<c:when test="${postCreatorAvatar != null }">
							<img src="${wmfn:stripUriProtocol(wmfmt:stripXSS(postCreatorAvatar))}"/>
						</c:when>
						<c:otherwise>
							<i class="wm-icon-user"></i>
						</c:otherwise>
					</c:choose>
					<c:if test="${post.admin}">
						<jsp:include page="/WEB-INF/views/web/partials/svg-icons/forums/wm_badge.jsp"/>
					</c:if>
					</div>
					<p class="header-creator-name"><c:out value="${wmfmt:escapeHtml(postCreatorName)}"/>
						<span class="category"> in <a href="/forums/${post.categoryId}"><c:out value="${categories[post.categoryId-1].categoryName}"/></a> </span>
					</p>
					<p class="forums-header-post-date">
						<c:choose>
							<c:when test="${post.edited}">
								<span>${wmfmt:formatCalendarWithTimeZone("MM/dd/yy h:mmaa z", post.createdOn, timezone)} - Edited</span>
							</c:when>
							<c:otherwise>
								<span>${wmfmt:formatCalendarWithTimeZone("MM/dd/yy h:mmaa z", post.createdOn, timezone)}</span>
							</c:otherwise>
						</c:choose>
					</p>
					<p class="tags">
						<c:forEach var="tag" items="${postTags}">
							<a class="forums-tag">${tag.displayString}</a>
						</c:forEach>
					</p>
				</div>
				<div id="discussionContent">
					<div class="forums-post-box">

						<p id="comment${post.id}" class="post-content forum-post">

							<c:choose>
								<c:when test="${post.deleted}"><strong>[Deleted]</strong></c:when>
								<c:otherwise><c:out escapeXml="false" value="${wmfmt:nl2br(post.comment)}"/></c:otherwise>
							</c:choose>
						</p>
						<div class="forums-post-controls">

								<c:if test="${isInternal}">
									<a class="admin-post-delete-btn" data-post-id="${post.id}">
										<c:choose>
											<c:when test="${post.deleted}">Reactivate</c:when>
											<c:otherwise>Delete Post</c:otherwise>
										</c:choose>
									</a>

									<c:choose>
										<c:when test="${bannedUsers.contains(post.creatorId)}">
											<a class="admin-unban-user-btn" href="javascript:void(0);" data-user-id="${post.creatorId}">Unban User</a>
										</c:when>
										<c:otherwise>
											<a class="admin-ban-user-btn" href="javascript:void(0);" data-user-id="${post.creatorId}">Ban User</a>
										</c:otherwise>
									</c:choose>
									<sec:authorize access="!principal.isMasquerading()">
										<c:if test="${!post.deleted}">
											<a class="user-post-edit-btn" data-post-id="${post.id}">Edit Post</a>
										</c:if>
									</sec:authorize>
									<c:if test="${post.flagged}">
										<a class="admin-unflag-post-btn" data-post-id="${post.id}">Unflag Post</a>
									</c:if>
									<c:if test="${not post.flagged}">
										<c:if test="${userId != post.creatorId && !post.deleted && !flaggedPosts.contains(post.id)}">
											<a class="post-flag-btn" data-post-id="${post.id}">Report Post</a>
										</c:if>
									</c:if>

								</c:if>

								<c:if test="${not isInternal}">
									<c:if test="${userId == post.creatorId && !post.deleted}">
										<a class="user-post-delete-btn" data-post-id="${post.id}">Delete Post</a>
										<sec:authorize access="!principal.isMasquerading()">
											<a class="user-post-edit-btn" data-post-id="${post.id}">Edit Post</a>
										</sec:authorize>
									</c:if>
									<c:if test="${userId != post.creatorId && !post.deleted && !flaggedPosts.contains(post.id)}">
										<a class="post-flag-btn" data-post-id="${post.id}">Report Post</a>
									</c:if>
									<c:if test="${userId != post.creatorId && !post.deleted && flaggedPosts.contains(post.id)}">
										Post Reported
									</c:if>
								</c:if>

							<sec:authorize access="!principal.isMasquerading()">
								<c:if test="${!isUserBanned}">
									<button type="button" class="button btn-focus add-comment-btn">Add Comment</button>
								</c:if>
							</sec:authorize>
						</div>
					</div>

					<h4 class="reply-left-padding">Comments</h4>
					<div id="replies">
						<c:forEach var="reply" items="${postReplies}" varStatus="loop">
							<div class="${reply.parentId==postId ? "forums-reply-box" : "forums-reply-reply-box"}">
								<div class="forums-hidden-comment-box" id="commentBox${reply.id}"></div>
								<div class="forums-user-icon">
									<c:choose>
										<c:when test="${postRepliesAvatars.get(reply.creatorId) != null }">
											<img src="${wmfn:stripUriProtocol(wmfmt:stripXSS(postRepliesAvatars.get(reply.creatorId)))}"/>
										</c:when>
										<c:otherwise>
											<i class="wm-icon-user"></i>
										</c:otherwise>
									</c:choose>
									<c:if test="${reply.admin}">
										<jsp:include page="/WEB-INF/views/web/partials/svg-icons/forums/wm_badge.jsp"/>
									</c:if>
								</div>
								<p class="creator-name"><c:out value="${postCreatorNames.get(reply.creatorId)}" /></p>
									<p class="forums-post-date">
										<c:choose>
											<c:when test="${reply.edited}">
												 ${wmfmt:formatCalendarWithTimeZone("MM/dd/yy h:mmaa z", reply.createdOn, timezone)} - <span class="orange-brand" style="color:#f7961D;">Edited</span>
											</c:when>
											<c:otherwise>
												${wmfmt:formatCalendarWithTimeZone("MM/dd/yy h:mmaa z", reply.createdOn, timezone)}
											</c:otherwise>
										</c:choose>
									</p>

								<p class="forums-post-comment" id="comment${reply.id}">
									<c:choose>
										<c:when test="${reply.deleted}"><strong>[Deleted]</strong></c:when>
										<c:otherwise>
											<c:out escapeXml="false" value="${wmfmt:nl2br(reply.comment)}"/>
										</c:otherwise>
									</c:choose>
								</p>
								<div class="forums-post-controls">
								<c:if test="${!isUserBanned}">
									<c:if test="${isInternal}">
										<a class="admin-post-delete-btn" data-post-id="${reply.id}">
											<c:choose>
												<c:when test="${reply.deleted}">Reactivate</c:when>
												<c:otherwise>Delete Post</c:otherwise>
											</c:choose>
										</a>

										<c:choose>
											<c:when test="${bannedUsers.contains(reply.creatorId)}">
												<a class="admin-unban-user-btn" data-user-id="${reply.creatorId}">Unban User</a>
											</c:when>
											<c:otherwise>
												<a class="admin-ban-user-btn" data-user-id="${reply.creatorId}">Ban User</a>
											</c:otherwise>
										</c:choose>

										<c:if test="${!reply.deleted}">
											<sec:authorize access="!principal.isMasquerading()">
												<a class="user-comment-edit-btn" data-post-id="${reply.id}">Edit Post</a>
											</sec:authorize>
										</c:if>

										<c:if test="${reply.flagged}">
											<a class="admin-unflag-post-btn" data-post-id="${reply.id}">Unflag Post</a>
										</c:if>

										<c:if test="${userId != reply.creatorId && !reply.deleted && !reply.flagged}">
											<a class="post-flag-btn" data-post-id="${reply.id}">Report Post</a>
										</c:if>

									</c:if>

									<c:if test="${not isInternal}">
										<c:if test="${userId == reply.creatorId && !reply.deleted}">
											<a class="user-post-delete-btn" data-post-id="${reply.id}">Delete Post</a>
											<sec:authorize access="!principal.isMasquerading()">
												<a class="user-comment-edit-btn" data-post-id="${reply.id}">Edit Post</a>
											</sec:authorize>
										</c:if>
										<c:if test="${userId != reply.creatorId && !reply.deleted && !flaggedPosts.contains(reply.id)}">
											<a class="post-flag-btn" data-post-id="${reply.id}">Report Post</a>
										</c:if>
										<c:if test="${userId != reply.creatorId && !reply.deleted && flaggedPosts.contains(reply.id)}">
											Post Reported
										</c:if>
									</c:if>

									<c:if test="${reply.parentId == post.id && !isUserBanned}">
										<button class="user-post-reply-btn button" id="commentReply${reply.id}">Reply</button>
									</c:if>

								</c:if>
									</div>
							</div>
						</c:forEach>
					</div>
				</div>

				<sec:authorize access="!principal.isMasquerading()">
				<c:if test="${!isUserBanned}">
					<div class="forums-add-comment-box" >
						<c:set value="${fn:length(post_replies)}" var="len" />
						<h4 class="forums-leave-comment">Leave a Comment</h4>

						<c:url value="/forums/post/reply" var="submit" />
						<form:form action="${submit}" method="POST" commandName="form" name="form" id="forumsPostReplyForm">

							<wm-csrf:csrfToken />

							<form:textarea path="comment" id="commentField" class="input-block-level" rows="5" maxLength="${isInternal ? '' : 1500}"/>
							<form:hidden path="parentId" />
							<form:hidden path="rootId" />
							<form:hidden path="categoryId" />

							<c:if test="${not isInternal}">
								<span class="post-char-countdown"></span>
							</c:if>
								<button type="submit" class="button" id="addCommentBtn" disabled>Add Comment</button>
						</form:form>
					</div>
				</c:if>
				</sec:authorize>
			</div>
		</div>
		<c:import url="/WEB-INF/views/web/partials/forums/sidebar.jsp" />
	</div>

</div>

<script id="tmplWMBadge" type="text/template">
	<jsp:include page="/WEB-INF/views/web/partials/svg-icons/forums/wm_badge.jsp"/>
</script>

</wm:app>
