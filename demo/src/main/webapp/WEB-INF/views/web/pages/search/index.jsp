<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<wm:app pagetitle="Find Workers"
	bodyclass="page-search"
	webpackScript="search"
>

	<script>
		var config = {
			mode: 'people-search',
			isBuyer: ${currentUser.buyer},
			userNumber: ${currentUser.userNumber},
			filterOrder: '${filters}',
			searchKeywords: '${wmfmt:escapeJavaScript(defaultsearch)}',
			searchType: 'workers',
			keyword: '${param.keyword}',
			hasVendorPoolsFeature: ${hasVendorPoolsFeature},
			hasESignatureEnabled: ${hasESignatureEnabled or false},
			hasMarketplace: ${hasMarketplace or false}
		};
	</script>

	<div class="search-filter-ui">
		<div class="search-filter-banner">
			<div id="unified_search_banner"></div>
			<h1>Find Talent</h1>
		</div>
		<div class="clearfix"></div>
		<div class="search-header">
			<div class="count-overview">
				<wm:button id="clear_facets">CLEAR THIS SEARCH</wm:button>
			</div>
			<div class="clearfix"></div>
		</div>
		<input type="hidden" name="sortby" id="sortby"/>
		<div class="search-filter-bucket"></div>
		<div class="search-filter-full-results">
			<%@ include file="/WEB-INF/views/web/partials/search/view.jsp" %>
		</div>
	</div>
</wm:app>
