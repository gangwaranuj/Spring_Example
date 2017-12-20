<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<wm:app pagetitle="Forums" bodyclass="Forums" breadcrumbSection="Market" breadcrumbSectionURI="/search" breadcrumbPage="Forums" webpackScript="forums">

	<script>
		var config = {
			type: 'index',
			categoryId: ${categoryId}
		}
	</script>


<div class="forums-header">
	<a href="/forums" id="forumHomeLink">
		<jsp:include page="/WEB-INF/views/web/partials/svg-icons/forums/forums_title.jsp"/>
	</a>
	<h3 class="title">${categories[categoryId-1].categoryName}</h3>
	<h4 class="strong">${categories[categoryId-1].description}</h4>
</div>

<div class="row">

	<div class="span16">
		<h4 class="forums-category-header">
			Discussions
		</h4>
	</div>
	<div class="span11">
		<div class="inner-container-topic">
			<table id="categoryPosts">
				<thead class="forums-padded-row">
					<th>Post Title</th>
					<th>Most Recent Comment</th>
					<th>Thread Creator</th>
				</thead>
				<tbody>
					<tr><td colspan="3" class="dataTables_empty">Loading data from server</td></tr>
				</tbody>
			</table>
		</div>
		<c:import url="/WEB-INF/views/web/partials/forums/followed_posts.jsp" />
	</div>

	<c:import url="/WEB-INF/views/web/partials/forums/sidebar.jsp" />
</div>

</wm:app>
