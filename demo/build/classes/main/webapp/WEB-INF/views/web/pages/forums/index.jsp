<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<wm:app pagetitle="Forums" bodyclass="Forums" breadcrumbSection="Market" breadcrumbSectionURI="/search" breadcrumbPage="Forums" webpackScript="forums">

	<script>
		var config = {
			type: 'index'
		}
	</script>

<div class="container">
	<div class="inner-container-topic">
		<div class="forums-index-page-description">
			<div class="forums-index-page-icon">
				<jsp:include page="/WEB-INF/views/web/partials/svg-icons/forums/forums_large.jsp"/>
				<h4>Forums</h4>
			</div>
			The Work Market community is a place for professionals to gather and discuss topics important to your business, assignments, and personal development. Connect with other professionals to share information, ask questions, and expand your horizons. Click on a community below to get started now.
			<div class="help-article">
				To review Work Market's Community Guidelines, see our <a href="https://workmarket.zendesk.com/hc/en-us/articles/219151517">Help Center article</a> for additional information.
			</div>
		</div>
	</div>

	<div class="row">

		<div class="span16">
			<h4 class="forums-category-header">
				<jsp:include page="/WEB-INF/views/web/partials/svg-icons/forums/categories_title.jsp"/>
				Category Index
			</h4>
		</div>

		<div class="span11" >
			<div class="inner-container-topic">
				<c:forEach var="category" items="${categories}" >
					<div class="forums-padded-row">
						<c:set var="stats" value="${categoryStats[category.id]}" />
						<c:set var="hasLastPost" value="${stats.lastPostId != null}" />
						<h4 class="forums-category-title">
							<a href="/forums/${category.id}"> ${category.categoryName} </a>
							<div class="stat-row">
								<span class="stat-number">${stats.postCount}</span> <span class="stat-label">discussions</span>
								<span class="stat-number">${stats.commentCount}</span> <span class="stat-label">comments</span>
							</div>
						</h4>
						<h5 class="forums-category-description"> <c:out value="${category.description}" /> </h5>
							<c:if test="${hasLastPost}">
								<div>
									<span class="stat-label pull-right">${stats.lastPostDate}</span>
									Most recent:
								 <p><a href="/forums/post/${stats.lastPostId}">${wmfmt:escapeHtml(stats.lastPostTitle)}</a></p>
								</div>
							</c:if>

					</div>
				</c:forEach>
			</div>
			<c:import url="/WEB-INF/views/web/partials/forums/followed_posts.jsp" />
		</div>

		<c:import url="/WEB-INF/views/web/partials/forums/sidebar.jsp">
			<c:param name="loadTopics" value="0" />
		</c:import>

	</div>

</div>

</wm:app>
