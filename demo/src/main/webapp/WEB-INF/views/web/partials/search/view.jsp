<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="content content-search">
	<div class="search-results-area" id="cart">
		<select id="page_size">
			<option value="25">25 Per Page</option>
			<option value="50">50 Per Page</option>
			<option value="100">100 Per Page</option>
			<option value="200">200 Per Page</option>
		</select>

		<wm:pagination max="1" />

		<div style="clear: both"></div>

		<div class="search-controls">
			<div class="search-controls--content">
				<wm:pagination max="1" />
				<c:if test="${not isDispatch}">
					<wm:checkbox name="select_all" id="select_all" value="1"> <span class="selection_count">0 Selected Talents</span></wm:checkbox>
				</c:if>

				<div id="cart-drawer"></div>
				<div class="dn"><div id="cart-modal"></div></div>

				<div id="sortby-container">
					<select id="sortby-control" class="action-menu search_sorting">
						<option value="relevance">Relevancy</option>
						<option value="distance_asc">Distance (Near-Far)</option>
						<option value="name_asc">Alphabetical (A-Z)</option>
						<option value="name_desc">Alphabetical (Z-A)</option>
						<option value="rating_desc">Rating (High-Low)</option>
						<option value="hourly_rate_asc">Hourly Rate (Low-High)</option>
						<option value="hourly_rate_desc">Hourly Rate (High-Low)</option>
						<option value="work_completed_desc">Total Assignments (High-Low)</option>
						<option value="created_on_desc">Member Since Date (Newest - Oldest)</option>
						<option value="created_on_asc">Member Since Date (Oldest - Newest)</option>
					</select>
				</div>
			</div>
		</div>
			<div class="search-filter-loadstate">
				<wm:spinner />Loading search results...
			</div>
		<div id="search_results" class="results-list">
		</div>
		<wm:pagination max="1" />
	</div>
</div>
