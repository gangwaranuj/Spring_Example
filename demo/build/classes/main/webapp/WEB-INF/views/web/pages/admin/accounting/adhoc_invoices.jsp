<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Ad-Hoc Invoices" webpackScript="admin">

	<script>
		var config = {
			mode: 'details',
			nonSubscriptionInvoiceLineItemTypes: ${nonSubscriptionInvoiceLineItemTypes},
			noPlanSubscriptionInvoiceTypeCodes: ${noPlanSubscriptionInvoiceTypeCodes}
		};
	</script>

	<c:import url="/breadcrumb">
		<c:param name="pageId" value="adminAccountingAddHocInvoices" />
	</c:import>

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

	<div class="content">
		<h1>Issue Ad-hoc Invoices</h1>

		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}" />
		</c:import>

		<form:form modelAttribute="form" action="/admin/accounting/adhoc_invoices" method="post" cssClass="form-horizontal" id="adhoc_invoice_form" style="position: relative">
			<wm-csrf:csrfToken />
			<form:hidden path="companyId" />

			<div class="control-group field">
				<label for="companyName" class="required control-label">Company Name</label>
				<div class="controls">
					<form:input path="companyName" data-constraints='@NotEmpty(message="Company Name must not be empty")'/>
					&nbsp;
					<span id="selected_company" class="dn"></span>
				</div>
			</div>

			<div class="subscriptionInfo dn" style="
			    top: 0;
			    position: absolute;
			    width: 50%;
			    overflow: hidden;
			    margin: 0 0 0 auto;
			    left: 0;
			    right: 0;
			">
				<div>Company ID: <strong><span class="companyId"></span></strong></div>
				<br>
				<div>Company Name: <strong><span class="companyName"></span></strong></div>
				<br>
				<div>Period: <strong><span class="period">N/A</span></strong></div>
				<br>
				<div>Effective Date: <strong><span class="effectiveDate">N/A</span></strong></div>
				<br>
				<div>End Date: <strong><span class="endDate">N/A</span></strong></div>
				<br>
				<div>Next non-invoiced Service Period: <strong><span class="servicePeriod">N/A</span></strong></div>
				<br>
				<div>Current Tier amount: <strong><span class="tier">N/A</span></strong></div>
				<br>
				<div>Current VOR Tier amount: <strong><span class="vorTier">N/A</span></strong></div>
			</div>

			<div class="control-group field">
				<label for="dueDate" class="required control-label">Due Date</label>
				<div class="controls">
					<form:input id="invoiceDueDate" path="dueDate" cssClass="input-small" placeholder="MM/DD/YYYY" data-constraints='@Future(format="MDY", message="Due date must be in the future")'/>
				</div>
			</div>

			<div class="control-group field">
				<label for="subscriptionInvoiceTypeCode" class="required control-label">Subscription Invoice Type</label>
				<div class="controls">
					<form:select path="subscriptionInvoiceTypeCode" name="subscriptionInvoiceTypeCode" cssClass="" items="${subscriptionInvoiceTypeCodes}" />
				</div>
			</div>

			<div class="control-group field">
				<label for="paymentPeriod" class="required control-label">Subscription Service Period</label>
				<div class="controls">
					<select id="paymentPeriod" name="paymentPeriod"></select>
				</div>
			</div>

			<hr style="margin-top: 6em;">
			
			<h3>Invoices Items</h3>
			<div class="form-horizontal">
				<div class="row">
					<div class="span4">
						<h5>Type</h5>
					</div>
					<div class="span5">
						<h5>Description</h5>
					</div>
					<div class="span3">
						<h5>Amount</h5>
					</div>
				</div>

				<div id="invoiceLineItems"></div>

				<div class="row">
					<div class="span9">
						<button id="add_more_items" type="button" class="button -small">+ Add More Items</button>
					</div>

					<div id="invoice_total" class="span7">
						<b>Invoice total: $<span>0.00</span></b>
					</div>
				</div>
			</div>

			<div class="wm-action-container">
				<a class="button" href="/admin/accounting">Cancel</a>
				<button type="submit" class="button submit">Submit</button>
			</div>
		</form:form>
	</div>

	<script type="text/x-jquery-tmpl" id="invoice_line_item_template">
	<div class="field line_item row">
		<div class="span4">
			<form:select cssClass="invoice_line_item_type" path="form" name="invoiceLineItemList[\${idx}].invoiceLineItemType" items="${invoiceLineItemTypes}" itemValue="name" itemLabel="description"></form:select>
		</div>
		<div class="span5 description">
			<input type="text" name="invoiceLineItemList[\${idx}].description" placeholder="Enter description details for each additional invoice item" data-constraints='@NotEmpty(message="Description must not be empty")'/>
		</div>
		<div class="span3 input-prepend">
			<span class="add-on">$</span>
			<input type="text" name="invoiceLineItemList[\${idx}].amount" class="input-small currency" data-constraints='@Amount'/>

			{{if idx > 0 }}
				<a class="remove"><i class="wm-icon-trash"></i></a>
			{{/if}}
		</div>
	</div>
</script>

</wm:admin>
