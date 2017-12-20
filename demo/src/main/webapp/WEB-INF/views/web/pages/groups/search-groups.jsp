<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app
	pagetitle="Browse Talent Pools"
	bodyclass="groups page-search-groups"
	webpackScript="groups"
>

	<script>
		var config = ${contextJson};
	</script>

	<form action="/search-groups/retrieve" method="POST" id="filter_form">
		<wm-csrf:csrfToken />
		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}"/>
		</c:import>

		<div class="page-header clear">
			<h2>Browse Talent Pools</h2>
			<c:if test="${not empty(requestScope.group_sort_options)}">
                <select id="sortby" name="sortby">
                    <c:forEach items="${requestScope.group_sort_options}" var="option">
                        <option value='${option.name}'>${option.description}</option>
                    </c:forEach>
                </select>
			</c:if>
		</div>

		<div class="row">
			<div class="span12">
				<div class="row">
					<ul id="search_results" class="unstyled">Loading results...</ul>
				</div>

				<div class="results-meta row">
					<div class="span4 fl">
						<p>Showing <span class="search_result_start_index">1</span>-<span
								class="search_result_end_index">10</span> of <span class="search_result_count">0</span>
							talent pool(s)</p>
					</div>
					<div class="span5 offset3">
						<div class="pagination fr">
							<ul>
								<li class="prev"><a>&laquo; Previous</a></li>
								<li class="status"><span>Page <span class="current_page">1</span> of <span class="num_pages">1</span></span></li>
								<li class="next"><a>Next &raquo;</a></li>
							</ul>
						</div>
					</div>
				</div>
			</div>
			<div id="facets" class="span4 search-facets">
				<c:import url="/WEB-INF/views/web/partials/groups/view/sidebar.jsp"/>

				<div class="well-b2">
					<div id="keywords">
						<h3>Keyword</h3>
						<div class="well-content keyword-search">
							<input name="keyword_fix" disabled class="dn"/>
							<input id="keyword" type="text" name="group_keyword" value="<c:out value="${defaultsearch}"/>" class="filter_suggest" />
							<button id="search-submit-action" class="button">Go</button>
							<a id="clear_facets" class="submit" href="javascript:void(0);">Clear search</a>
						</div>
					</div>
				</div>

				<div id="industries" class="well-b2">
					<h3>Industry</h3>
					<div class="well-content">
						<div><a href="javascript:void(0);" class=""></a></div>
						<ul class="inputs-list">
							<li>
								<label>
									<input type="checkbox" id="industry_showall" class="show-all" checked/>
									<span>Show All</span>
								</label>
							</li>
						</ul>
						<ul class="filter_items inputs-list"></ul>
					</div>
				</div>
			</div>
		</div>

		<script id="filter_item_checkbox" type="text/x-jquery-tmpl">
			<li>
				<label>
					<input type="checkbox" id="\${id}" name="\${name}" value="\${value}"/>
				<span>\${label}
				{{if count}}
					<small>(\${count})</small>
				{{/if}}
				</span>
				</label>
			</li>
		</script>
	</form>

</wm:app>
