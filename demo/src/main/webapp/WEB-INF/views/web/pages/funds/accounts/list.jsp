<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Manage Payment Accounts" bodyclass="accountSettings" breadcrumbSection="Payments" breadcrumbSectionURI="/payments" breadcrumbPage="Accounts" webpackScript="payments">

	<script>
		var config = {
			'payments': ${contextJson}
		};
	</script>

	<c:import url="/WEB-INF/views/web/partials/addgcc.jsp"/>

	<div class="inner-container">
		<div class="page-header clear">
			<h3 class="fl">
				Manage Payment Accounts <small class="ml">
				<a href="https://workmarket.zendesk.com/hc/en-us/articles/214799148-How-do-I-add-a-payment-account" target="_blank">Learn more <i class="icon-info-sign"></i></a></small>
			</h3>
			<sec:authorize access="!principal.isMasquerading()">
				<a href="<c:url value="/funds/accounts/new"/>" class="button pull-right">New Account</a>
			</sec:authorize>
		</div>
		<c:import url='/WEB-INF/views/web/partials/message.jsp'/>

		<table id="datatable">
			<thead>
			<tr>
				<th width="200">Method</th>
				<th>Account</th>
				<th>Type</th>
				<th width="100">Country</th>
				<th>Status</th>
				<th width="100">Action</th>
			</tr>
			</thead>
			<tbody></tbody>
		</table>
	</div>

	<script id="cell-method-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if meta.type == 'ACH'}}
			Bank Account (ACH)
			{{else meta.type == 'GCC'}}
			Work Market Visa Card<br/>
			<small class="gcc-phone">Questions? Call 866-395-9200</small>
			{{else meta.type == 'PPA'}}
			PayPal
			{{/if}}
		</div>
	</script>

	<script id="cell-type-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if meta.type == 'ACH'}}
			\${meta.accountType}
			{{else  meta.type == 'GCC'}}
			Visa Card
			{{else meta.type == 'PPA'}}
			PayPal
			{{/if}}
		</div>
	</script>

	<script id="cell-status-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if meta.type == 'ACH' && meta.country == 'USA'}}
				{{if meta.confirmed}}
					Verified<br/>
					<small class="meta">on \${meta.confirmedOn}</small>
				{{else}}
					Not Verified<br/>
					<small class="meta">Requested on \${meta.createdOn}</small>
				{{/if}}
			{{else meta.type == 'ACH' && meta.country != 'USA'}}
				Activated<br/>
				<small class="meta">on \${meta.createdOn}</small>
			{{else meta.type == 'PPA'}}
				Activated<br/>
				<small class="meta">on \${meta.createdOn}</small>
			{{else meta.type == 'GCC'}}
				{{if meta.confirmed }}
					Active<br/>
					<small>automatic withdrawal: <a href="/funds/accounts/auto_withdrawal/\${meta.id}" data-behavior="modal" title="Automatic Withdrawal Settings">{{if meta.isAutoWithdraw}}On{{else}}Off{{/if}}</a></small>
				{{else}}
					Pending<br/>
					<small class="meta">Requested on \${meta.createdOn}</small>
				{{/if}}
			{{/if}}
		</div>
	</script>

	<script id="cell-action-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if meta.type == 'ACH' && !meta.confirmed}}
				<a href="/funds/accounts/verify/\${meta.id}" data-behavior="verify-modal" title="Verify Account">Verify</a> /
			{{/if}}

			{{if meta.type == 'GCC'}}
				<a href="https://workmarket.zendesk.com/hc/en-us/articles/214799148-How-do-I-add-a-payment-account" target="_blank">How to Deactivate</a>
			{{else}}
				<a href="/funds/accounts/delete/\${meta.id}" data-behavior="remove-modal" title="Remove Account">Remove</a>
			{{/if}}
		</div>
	</script>

</wm:app>
