<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div class="forums-sidebar-container">
	<c:if test="${param.loadButton != 0}">
		<div class="inner-container-topic">
			<h4 class="sidebar-block-header">
				<jsp:include page="/WEB-INF/views/web/partials/svg-icons/forums/search_sidebar.jsp"/>
				Search Forums
			</h4>
			<form:form id="forumsSearchForm" commandName="search-form" action="/forums/search">
				<wm-csrf:csrfToken />

				<div class="search-field">
					<form:input cssClass="keywords" path="keywords" title="keywords" placeholder="Keywords"/>
				</div>

				<div class="search-field">
					<form:select path="categoryId" cssClass="categories">
						<form:option value="0" label="All Categories" />
						<form:options items="${categories}" itemValue="id" itemLabel="categoryName" />
					</form:select>
				</div>

				<div class="search-field">
					<button id="forumSearchSideBtn" type="submit" class="button" disabled="true">Search</button>
				</div>

				<div class="search-field" id="forumsAdvancedSearchBtn">
					<a href="/forums/search">Advanced Search</a>
				</div>

			</form:form>
		</div>

		<sec:authorize access="!principal.isMasquerading()">
		<c:if test="${!isUserBanned}">
			<div class="inner-container-topic">
				<c:if test="${param.loadButton != 0}">
					<div>
						<a class="button" href="/forums/post" >New Discussion</a>
					</div>
				</c:if>
			</div>
		</c:if>
		</sec:authorize>
	</c:if>
	<c:if test="${param.loadTopics != 0}">
		<div class="inner-container-topic">
			<h4 class="sidebar-block-header">
				<jsp:include page="/WEB-INF/views/web/partials/svg-icons/forums/categories_sidebar.jsp"/>
				Categories
			</h4>
			<div>
				<h5 class="forums-sidebar-header">
					<a href="/forums"> Forum Home </a>
				</h5>
				<span class="forums-category-number">
					${categoryStats[0].postCount}
				</span>
			</div>
			<c:forEach var="cat" items="${categories}" >
				<div class="top-border">
					<h5 class="forums-sidebar-header">
						<a href="/forums/${cat.id}"> ${cat.categoryName} </a>
					</h5>
					<span class="forums-category-number">
						${categoryStats[cat.id].postCount}
					</span>
				</div>
			</c:forEach>
		</div>
	</c:if>
	<div class="inner-container-topic">
		<h4 class="sidebar-block-header">
			<jsp:include page="/WEB-INF/views/web/partials/svg-icons/forums/tag.jsp"/>
			Popular Tags
		</h4>
		<c:set var="count" value="0"/>
		<form:form id="forumsSearchFormTag" commandName="search-form" action="/forums/search">
			<wm-csrf:csrfToken />
			<form:input id="keywordsTag" cssClass="keywords-tag" path="keywords" title="keywords" value=""/>
			<form:select id="categoriesTag" path="categoryId" cssClass="categories">
				<form:option value="0" label="All" />
			</form:select>
			<form:select id="tagsTag" path="tag" cssClass="categories">
				<form:option class="tag-form" value="0" label="All"/>
				<form:options class="tag-form" items="${tags}"/>
			</form:select>
		</form:form>
		<c:forEach var="tag" items="${tags}" >
			<a class="forums-tag" href="javascript:void(0);">${tagStats[count].tag}</a>
			<c:set var="count" value="${count + 1}"/>
		</c:forEach>
	</div>

</div>


