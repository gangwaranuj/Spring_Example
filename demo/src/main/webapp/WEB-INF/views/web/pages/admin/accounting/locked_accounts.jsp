<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Locked Accounts">

<c:url value="/admin/manage/company/overview" var="overviewURL" />
<c:url value="/admin/accounting/load_locked_accounts" var="lockedAccountsURL" />

<c:set var="pageScript" value="wm.pages.admin.accounting.locked_accounts" scope="request" />
<c:set var="pageScriptParams" value="'${overviewURL}', '${lockedAccountsURL}'" scope="request" />

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<table cellpadding="0" id="locked_accounts" class="table table-striped">
		<thead>
			<tr>
				<th>Company Name</th>
				<th>Date Locked</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>
</div>

</wm:admin>
