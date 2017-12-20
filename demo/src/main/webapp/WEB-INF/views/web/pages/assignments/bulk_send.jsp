<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app pagetitle="WorkUpload" bodyclass="page-assignment-bulk-send" webpackScript="uploader">

	<script>
		var config = {
			mode: 'bulk_send',
			ids: ${ids}
		};
	</script>

	<div class="sidebar-card">
		<h3 class="sidebar-card--title">Batch Send Assignments</h3>

		<div>
			<p>Route your assignments by selecting them and attaching a Talent Pool or by using <strong><span class="orange-brand" style="color:#f7961D;">Work</span><span class="gray-brand">Send</span>&trade;</strong></p>
		</div>

		<form action="/assignments/bulk_send" id="bulk-send-form" method="post">
			<wm-csrf:csrfToken/>

			<table id="work-table" class="pagination-selection">
				<thead>
					<tr>
						<th colspan="6" id="actions-header"></th>
					</tr>
					<tr>
						<th><input type="checkbox" name="select-all" value="1" class="select-all-visible-outlet"/></th>
						<th width="40%">Title</th>
						<th>Start Date</th>
						<th>City</th>
						<th class="text-right">Max Spend</th>
						<th>Send to</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
		</form>
	</div>

	<script id="cell-select-tmpl" type="text/x-jquery-tmpl">
		<div>
			<input type="checkbox" name="ids" value="\${meta.work_number}"/>
		</div>
	</script>

	<script id="cell-send-to-tmpl" type="text/x-jquery-tmpl">
		<div>
			<span class="selected-send-outlet"></span>
		</div>
	</script>

	<script id="custom-header-outlet-tmpl" type="text/x-jquery-tmpl">

		<div class="pull-left" id="summary-actions">
			<span><span class="selected-count">0</span> of <span class="total-count">0</span> selected.</span>
			<a class="select-all-outlet">Select all <span class="total-count">0</span> assignments</a> /
			<a class="clear-selection-outlet">Clear</a>
		</div>
		<div class="pull-right">
			<select name="groups" disabled="disabled">
				<option value="">&mdash; Attach Talent Pool to Selected &mdash;</option>
				<option value="clear">&mdash; Remove Talent Pool from Selected &mdash;</option>
				<optgroup label="Talent Pools">
					<c:forEach var="item" items="${groups}">
						<option value="${item.key}"><c:out value="${item.value}"/></option>
					</c:forEach>
				</optgroup>
			</select>

			<button type="submit" class="button" name="auto-send" disabled="disabled">
			Use <strong><span class="orange-brand" style="color:#f7961D;">Work</span><span class="gray-brand">Send</span>&trade;</strong>
			</button>

			<button type="submit" class="button" data-action="send" disabled="disabled">Send</button>
			<a class="button" href="/assignments#status/draft/managing">Go to Drafts</a>
		</div>

	</script>
</wm:app>
