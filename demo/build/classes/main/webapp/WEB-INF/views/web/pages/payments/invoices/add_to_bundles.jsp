<%--TODO: move to partial - not a page--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form:form action="/payments/invoices/add_to_bundle_save" modelAttribute="InvoiceBundleAddForm" id="form_bundle_invoices" cssClass="form-stacked">
	<label for="bundle_description" class="required">Please select the bundle that you would like to add the invoices to:</label>
	<div class="input" id="add_to_bundle_invoice">
		<form:select path="bundle_id" id="bundle_description">
			<option value="" disabled selected>- Select Bundle -</option>
			<c:forEach var="bundles" items="${bundle}">
				<form:option path="bundle_id" value="${bundles.bundleIds}">${bundles.bundleNames}</form:option>
			</c:forEach>
		</form:select>
	</div>
</form:form>

