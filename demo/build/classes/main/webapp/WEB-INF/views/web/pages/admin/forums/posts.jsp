<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Posts" webpackScript="admin">

	<script>
		var config = {
			mode: 'latestForumPosts'
		};
	</script>

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

	<div class="content latest-posts">
		<h1>Latest Posts</h1>
		<input type="hidden" class="page-number" value="0"/>

		<div class="search-controls">
			<div class="search-field-big start-date">
				<input id="from" class="span2 date" placeholder="Select Date" type="text" value="">
			</div>
			<span class="date-separator">to</span>
			<div class="search-field-big">
				<input id="to" class="span2 date" placeholder="Select Date" type="text" value="">
			</div>
			<input id="user-filter" placeholder="User filter" value=""/>

			<button type="submit" class="button search">Filter</button>
			<a class="clear-search">Clear Search</a>
		</div>

		<div class="pagination">
			<a class="previous">Previous</a>
			<a class="next">Next</a>
			<div>
				Page <span class="page-number">1</span> of <span class="number-of-pages">?</span>
			</div>
		</div>

		<table id="posts-table" class="table">
			<thead>
				<tr>
					<th style="max-width: 40em; min-width: 40em; width: 40em;">Comment</th>
					<th style="width: 15em;">Discussion</th>
					<th>User</th>
					<th>Date</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="5">Loading data from server</td>
				</tr>
			</tbody>
		</table>

		<div class="pagination">
			<a class="previous">Previous</a>
			<a class="next">Next</a>
			<div>
				Page <span class="page-number">1</span> of <span class="number-of-pages">?</span>
			</div>
		</div>

	</div>

</wm:admin>
