<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="translation.assignment_search" var="translation_assignement_search"/>
<wm:app pagetitle="${translation_assignement_search}" bodyclass="accountSettings" breadcrumbSection="Work" breadcrumbSectionURI="/assignments" breadcrumbPage="Assignment Search" webpackScript="search">

	<script>
		var config = {
			mode: 'assignments',
			defaultSearch: '${wmfmt:escapeJavaScript(defaultsearch)}}'
		};
	</script>

	<div class="inner-container">
		<div class="content">
			<div class="page-header clear">
				<h3 class="fl"><fmt:message key="translation.assignment_search"/></h3>
			</div>
			<div class="alert alert-info">
				<fmt:message key="translation.assignment_search_explanation"/>
				<strong><a href="http://help.workmarket.com/customer/portal/articles/1037109-assignment-search" target="_blank"><fmt:message key="translation.learn_more"/> <i class="icon-info-sign"></i></a></strong>
			</div>

			<div id="keywords">
					<span id="search-assignment-select">
						<select name="type" id="type_input" class="span2 search-query">
							<option value="all"><fmt:message key="translation.search_filter.all"/></option>
							<option value="title"><fmt:message key="translation.search_filter.title"/></option>
							<option value="description"><fmt:message key="translation.search_filter.description"/></option>
							<option value="instructions"><fmt:message key="translation.search_filter.instruction"/></option>
						</select>
					</span>
				<span id="search-assignment-input">
					<input type="text" placeholder="<fmt:message key="translation.search_assignment_placeholer"/>" autocomplete="off" name="keywords" value="<c:out value="${defaultsearch}"/>" id="keywords_input">
				</span>
				<button class="button" id="keyword_btn"><fmt:message key="translation.search_assignments"/></button>
			</div>

			<div class="results-meta">&nbsp;</div>
			<div id="search_results" class="results-list"></div>
			<div id="show_more_results"></div>
		</div>

		<div class="sidebar"></div>
	</div>

	<script id="results_item_assignments" type="text/x-jquery-tmpl">
		<div class="results-row work">
			<h4><a href="/assignments/details/\${work_number}">\${title}</a> <small>\${work_number} (\${work_status_type_code})</small></h4>
			{{html description}}
		</div>
	</script>

	<script id="more_results" type="text/x-jquery-tmpl">
		<button type="button" id="show_more_btn" name="show_more_btn" class="button" value="\${page}"><fmt:message key="translation.show_50_more_results"/></button>
	</script>

</wm:app>
