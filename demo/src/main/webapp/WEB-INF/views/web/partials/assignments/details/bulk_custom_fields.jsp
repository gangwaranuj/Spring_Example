<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<%--needs to be first node as it's passed in to modal--%>
<div id="work-encoded" style="display: none;"><c:if test="${not empty work_encoded}">${work_encoded}</c:if></div>

<c:if test="${not empty work.customFieldGroups && showForm}">
	<div id="custom-fields" class="bulk-custom-field-popup">
		<input type="hidden" name="millisecond" value="${assignment_tz_millis_offset}">

		<form action="/assignments/save_custom_fields" id="custom_fields_form" method="post">
			<wm-csrf:csrfToken/>
			<input type="hidden" name="assignment_id_custom_field" id="assignment_id_custom_field"/>
			<input type="hidden" name="<c:out value="${param.prefix}"/>id" value="${work.customFieldGroups[0].id}">
			<input type="hidden" name="custom_field_group_ids" id="custom_field_group_ids">

	<div id="buyer-custom-fields">
				<h5>Custom Client Fields<div class="pull-right">Update</div></h5>
				<br/>
			</div>
			<div id="resource-custom-fields">
				<h5>Custom Worker Fields</h5>
			</div>
			<div class="wm-action-container">
				<button class="button" id="close-cf-modal" data-modal-close>Cancel</button>
				<button class="button" id="save_custom_field">Save Custom Fields</button>
			</div>
		</form>
	</div>
</c:if>

<c:if test="${empty work.customFieldGroups || not showForm}">
	<div class="alert alert-info">
		<ul class="unstyled">
			<li>To edit custom fields using bulk action, all selected assignments must have the same custom field set.</li>
			<br/>
			<li>${errorMessage}</li>
		</ul>
	</div>
</c:if>
