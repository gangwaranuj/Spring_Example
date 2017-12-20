<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Settle">

<c:import url="/breadcrumb">
	<c:param name="pageId" value="adminAccountingNachaSettle" />
	<c:param name="admin" value="true" />
</c:import>

<div class="row_sidebar_left">
	<div class="sidebar">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

	<div class="content">

		<c:import url="/WEB-INF/views/web/partials/message.jsp" />

		<form action="/admin/accounting/settle_transaction_pending_status" method="post" class="form-stacked" id="update_status_form">
			<wm-csrf:csrfToken />
			<input type="hidden" name="returnTo" value="<c:url value="/admin/accounting/settle/${request.id}"/>" id="return_to" />
			<input type="hidden" name="updateStatus" value="" id="update_status" />
			<input type="hidden" name="updateNote" value="" id="update_note" />

			<table id="data_list" class="table table-striped">
				<thead>
					<tr>
						<th><input type="checkbox" name="select_all" id="select_all" /></th>
						<th>Company Name</th>
						<th>Name on Account</th>
						<th>Bank Name</th>
						<th>Institution Number</th>
						<th>Account Number</th>
						<th>Amount</th>
						<th>Status</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="value" items="${transactions}">
					<tr>
						<td class="titlecase">
							<c:choose>
								<c:when test="${value.bankAccountTransactionStatus.code == 'processed' && value.pendingFlag}">
									<input type="checkbox" name="transactionIds[]" value="${value.id}" />
								</c:when>
								<c:otherwise>
									<input type="checkbox" name="transactionIds[]" value="${value.id}" disabled="disabled" />
								</c:otherwise>
							</c:choose>
						</td>
						<td><c:out value="${value.bankAccount.company.name}"/></td>
						<td><c:out value="${value.bankAccount.nameOnAccount}"/></td>
						<td><c:out value="${value.bankAccount.bankName}"/></td>
						<td><c:out value="${value.bankAccount.routingNumber}"/></td>
						<td><c:out value="${value.bankAccount.accountNumber}"/></td>
						<td><fmt:formatNumber value="${value.amount}" currencySymbol="$" type="currency"/></td>
						<td class="titlecase"><c:out value="${value.bankAccountTransactionStatus.code}"/></td>
					</tr>
					</c:forEach>
				</tbody>
			</table>

			<hr/>

			<div class="form-actions">
				<a href="javascript:void(0);" class="btn danger" id="reject-outlet">Reject</a>
				<a href="javascript:void(0);" class="btn success" id="approve-outlet">Approve</a>
			</div>
		</form>

	</div>
</div>

<div class="dn">
	<div id="note-entry">
		<form action="#" class="form-stacked">

			<div class="clearfix">
				<label>Note:</label>
				<div class="input">
					<textarea name="note"></textarea>
				</div>
			</div>

			<div class="form-actions">
				<a href="javascript:void(0);" class="button submit">Reject</a>
				<a href="javascript:void(0);" class="button" onclick="javascript:$.colorbox.close();">Cancel</a>
			</div>

		</form>
	</div>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		$('#data_list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': false,
			'bFilter': false,
			'iDisplayLength': 100,
			'aoColumnDefs': [
				{'aTargets': [0], 'bSortable': false}
			]
		});

		$('#select_all').change(function () {
			$('#data_list tbody input[type="checkbox"]:not(:disabled)').prop('checked', $(this).prop('checked'));
		});

		$('#approve-outlet').click(function () {
			$('#update_status').val('approved');
			$(this).closest('form').trigger('submit');
		});
		$('#reject-outlet').colorbox({
			inline: true,
			href: '#note-entry',
			title:'Note',
			transition:'none',
			onComplete: function() {
				$('#note-entry .submit').click(function() {
					$('#update_status').val('rejected');
					$('#update_note').val($('#note-entry textarea[name=note]').val());
					$('#reject-outlet').closest('form').trigger('submit');
				});
				$('#note-entry .cancel').click(function() {
					$.colorbox.close();
				});
			}
		});
	});
</script>

</wm:admin>
