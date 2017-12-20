<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="End of Year Tax Service Status Update" webpackScript="admin">
	<script>
		var config = {
			mode: 'taxStatusUpdate',
			publishedYears: {
				vor: ${latestPublishedVorYear},
				tax: ${latestPublishedTaxYear},
				none: ${latestPublishedNoneYear}
			}
		}
	</script>

	<c:import url="/breadcrumb">
		<c:param name="pageId" value="adminVORNVORTaxStatusUpdate" />
	</c:import>

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

	<div class="content">
		<h1>End of Year Tax Service Status Update</h1>

		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}" />
		</c:import>

		<form:form modelAttribute="form" id="taxStatusUpdateForm" action="/admin/accounting/vor_nvor_status_update" method="post" class="form-horizontal">
			<wm-csrf:csrfToken />
			<form:hidden path="companyId" />

			<div class="control-group">
				<label class="required control-label">Company Name</label>
				<div class="controls">
					<form:input path="companyName" />
					&nbsp;
					<span id="selected_company" class="dn"></span>
				</div>
			</div>

			<div class="control-group">
				<label for="fiscalYear" class="required control-label">Tax Year</label>
				<div class="controls">
					<form:select path="fiscalYear" class="input-medium">
						<form:option value="">- Year -</form:option>
						<c:forEach begin="${minYear}" end="${maxYear}" varStatus="yearLoop">
							<form:option value='${yearLoop.index}'/>
						</c:forEach>
					</form:select>
				</div>
			</div>

			<div class="control-group">
				<label for="taxStatus" class="required control-label">Service Status</label>
				<div class="controls">
					<form:select path="taxStatus" class="input-medium">
						<form:option value="">- Service Status -</form:option>
						<form:option value="vor">Vor</form:option>
						<form:option value="none">None</form:option>
						<form:option value="tax">Tax</form:option>
					</form:select>
				</div>
			</div>

			<br />
			<div class="wm-action-container">
				<a class="button" href="/admin/accounting">Cancel</a>
				<button type="submit" disabled class="button accounting_submit">Submit</button>
			</div>
		</form:form>
	</div>
</wm:admin>
