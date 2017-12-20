<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app pagetitle="Users" bodyclass="accountSettings" webpackScript="settings">

	<script>
		var config = {
			mode: 'usersManage',
			hasBusinessTaxInfo: ${not isWorkerCompany or hasBusinessTaxInfo}
		};
	</script>

	<div class="row_wide_sidebar_left">
		<div class="sidebar">
			<jsp:include page="../../partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header clear">
					<h3 class="pull-left">Employee Settings</h3>
					<c:choose>
						<c:when test="${not isWorkerCompany or hasBusinessTaxInfo}">
							<a id="add-new-emlpoyee-btn" href="javascript:void(0);" class="pull-right button">New Employee</a>
							<span id="add-new-emlpoyee-btn-bulk"></span>
						</c:when>
						<c:otherwise>
							<a id="add-new-emlpoyee-btn" href="javascript:void(0);" class="pull-right button tooltipped tooltipped-n" aria-label="To add users to your account you must update your tax information to use a EIN.">New Employee</a>
						</c:otherwise>
					</c:choose>
				</div>
				<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

				<form id="employee-settings-form">
					<h5>Team Price Visibility</h5>
					<label>
						<input id="hide-pricing" name="hide-pricing" type="checkbox" value="true" />
						Hide price information on assignments from employees. Admins,
						Managers, and Team Agents will still be able to see pricing.
					</label>
					<span class="help-block">
						If checked, users will not be able to accept or apply for non-internal assignments, a Team Agent must do so for them.
					</span>
					<hr />
				</form>

				<a class="toggle-active-outlet" href="javascript:void(0);">Show Inactive Employees</a>
				<div id="filters" class="pull-right">
					<input type="hidden" data-behavior="filter" name="inactive" value="0" />
					Sort by
					<select name="iSortCol_0" data-behavior="filter" class="span3">
						<option value="0">Name</option>
						<option value="1">Email</option>
						<option value="2">Roles</option>
						<option value="3">Last Active</option>
					</select>

					<select name="sSortDir_0" data-behavior="filter" class="span2">
						<option value="asc">Asc</option>
						<option value="desc">Desc</option>
					</select>
				</div>

				<table id="user_list">
					<thead>
						<tr>
							<th width="30%">Employee Information</th>
							<th width="20%">Roles</th>
							<th width="25%">Last Active</th>
							<th width="25%">Statistics (Previous Day)</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td colspan="5" class="dataTables_empty">Loading data from server</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<script id="name-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<p>
				<strong>\${data}</strong>
				{{if meta.status == 'deactivate'}}
					<span class="text-error">(inactive)</span>
				{{else meta.status == 'suspended'}}
					<span class="text-error">(suspended)</span>
				{{/if}}
				<br/>

				<small class="meta">
				\${meta.email}<br/>
				User ID: \${meta.id}<br/>
					<a class="profile_link" href="/profile/\${meta.id}">View</a> |
					<a href="/users/edit_user/\${meta.id}">Edit</a>
					{{if !meta.emailConfirmed}}
						| <a class="red" href="/users/resend_confirmation_email/\${meta.id}" title="User has not confirmed email address. Click to resend password setup email.">Resend Confirm Email</a>
					{{/if}}
				</small>
			</p>
		</div>
	</script>

	<script id="login-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			\${data}
			{{if meta.isOnline}}
				<span class="label success tooltipped tooltipped-n" aria-label="Active within 30 minutes">Online</span>
			{{/if}}
			{{if meta.latestActivityAddress}}
				<br/>IP: \${meta.latestActivityAddress}
			{{/if}}
		</div>
	</script>

	<script id="stats-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			Sent: \${meta.statsSentCount}<br/>
			Sent $: \${format_currency(meta.statsSentValue)}<br/>
			Approved: \${meta.statsApprovedCount}<br/>
			Approved $: \${format_currency(meta.statsApprovedValue)}<br/>
		</div>
	</script>

	<script type="text/javascript">
		function format_currency(num) {
			num = (num || 0).toString().replace(/\$|\,/g, '');
			if (!isFinite(num))
				num = "0";

			sign = (num == (num = Math.abs(num)));
			num = Math.floor(num * 100 + 0.50000000001);
			cents = num % 100;
			num = Math.floor(num / 100).toString();

			if (cents < 10)
				cents = "0" + cents;

			for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
				num = num.substring(0, num.length - (4 * i + 3)) + ',' + num.substring(num.length - (4 * i + 3));

			return (((sign) ? '' : '-') + '$' + num + '.' + cents);
		}
	</script>

</wm:app>
