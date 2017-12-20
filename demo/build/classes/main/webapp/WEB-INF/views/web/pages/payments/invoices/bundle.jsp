<%--TODO: move to partail - not a page--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form action="/payments/invoices/bundle" modelAttribute="invoiceBundleForm" id="form_bundle_invoices" cssClass="form-stacked">
	<div class="clearfix">
		<label for="bundle_description" class="required">Please provide a title for this bundle of invoices.</label>
		<div class="input">
			<form:input path="description" id="bundle_description" cssClass="span8" maxlength="255" />
		</div>
	</div>
</form:form>
