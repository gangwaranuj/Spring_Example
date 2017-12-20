<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Manage Funds" webpackScript="admin">
	<script>
		var config = {
			mode: 'manageFunds'
		}
	</script>

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

	<div class="content">
		<h1>Manage Company Funds</h1>

		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}" />
		</c:import>

		<form:form modelAttribute="form" action="/admin/accounting/managefunds" method="post" class="form-horizontal">
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
				<label class="required control-label">Amount</label>
				<div class="controls">
					<form:input path="amount" />
				</div>
			</div>

			<div class="control-group">
				<label class="required control-label">Type</label>
				<div class="controls">
					<form:select path="type">
						<form:option value="">- Select -</form:option>
						<form:option value="credit">Credit</form:option>
						<form:option value="cash_out">Debit</form:option>
					</form:select>
				</div>
			</div>

			<div class="control-group">
				<label class="required control-label">Description</label>
				<div class="controls">
					<form:select path="description">
						<form:option value="">- Select -</form:option>

						<c:forEach items="${credit_options}" var="option">
							<form:option value="${option.key}" data-tx="credit"><c:out value="${option.value}"/></form:option>
						</c:forEach>
						<c:forEach items="${debit_options}" var="option">
							<form:option value="${option.key}" data-tx="cash_out"><c:out value="${option.value}"/></form:option>
						</c:forEach>
					</form:select>
				</div>
			</div>

			<div class="control-group">
				<label for="note_input" class="required control-label">Note</label>
				<div class="controls">
					<div id="note_input">
						<form:textarea path="note" cssClass="span7" />
					</div>
				</div>
			</div>

			<div class="wm-action-container">
				<button class="button accounting_submit">Submit</button>
				<a class="button" href="/admin/accounting">Cancel</a>
			</div>
		</form:form>
	</div>

</wm:admin>
