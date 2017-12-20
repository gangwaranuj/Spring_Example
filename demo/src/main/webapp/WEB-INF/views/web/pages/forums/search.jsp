<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Forums" bodyclass="Forums" breadcrumbSection="Market" breadcrumbSectionURI="/search" breadcrumbPage="Forums" webpackScript="forums">

	<script>
		var config = {
			type: 'search'
		}
	</script>

<div class="container">

	<div class="forums-header">
		<a href="/forums" id="forumHomeLink">
			<jsp:include page="/WEB-INF/views/web/partials/svg-icons/forums/forums_title.jsp"/>
		</a>
		<h3 class="title">Forums Search</h3>
	</div>

	<div class="row">

		<div class="span16">
			<div class="inner-container-topic">
				<h4 class="sidebar-block-header">
					<jsp:include page="/WEB-INF/views/web/partials/svg-icons/forums/search_sidebar.jsp"/>
					Search Forums
				</h4>
				<form:form id="forumsSearchForm" commandName="search-form" action="/forums/search">
					<wm-csrf:csrfToken />

					<div class="search-field-big">
						<form:input cssClass="keywords" path="keywords" title="keywords" placeholder="Keywords"/>
					</div>

					<div class="search-field-big">
						<form:select path="categoryId" cssClass="categories">
							<form:option value="0" label="All Categories" />
							<form:options items="${categories}" itemValue="id" itemLabel="categoryName" />
						</form:select>
					</div>

					<div class="search-field-big">
						<form:select path="tag" cssClass="tags">
							<form:option  class="tag-form" value="All" label="All Tags" />
							<form:options class="tag-form" items="${tags}"/>
						</form:select>
					</div>

					<div class="search-field-big start-date">
						<form:input path="fromDate" id="from" cssClass="span2 date" placeholder="Select Date"/>
					</div>

					<span class="date-separator">
						to
					</span>

					<div class="search-field-big">
						<form:input path="toDate" id="to" cssClass="span2 date" placeholder="Select Date"/>
					</div>

					<div class="search-field-big">
						<button id="forumSearchMainBtn" type="submit" class="button">Search</button>
						<a id="forumsSearchClear" href="javascript:void(0);" >Clear Search</a>
					</div>

				</form:form>
			</div>
		</div>

		<div class="span16">
			<h4 class="forums-header forums-search-results">
				<jsp:include page="/WEB-INF/views/web/partials/svg-icons/forums/search_title.jsp"/>
				Search Results
			</h4>
		</div>

		<div class="span16">
			<div class="inner-container-topic" >
				<table id="searchTable">
					<thead class="forums-padded-row forum-header-row">
						<th class="forums-search-discussion">Discussion</th>
						<th class="forums-search-category">Category</th>
						<th class="forums-search-tags">Tags</th>
						<th class="forums-search-comments">Comments</th>
						<th class="forums-search-created">Created</th>
						<th class="forums-search-creator">Discussion Owner</th>
					</thead>
					<tbody>
						<tr><td colspan="7" class="dataTables_empty">Loading data from server</td></tr>
					</tbody>
				</table>
			</div>
		</div>

	</div>

</div>

</wm:app>
