<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Reserve Funds" webpackScript="payments">

	<script>
		var config = {
			'payments': ${contextJson}
		};
	</script>

	<div id="allocateBudget">
		<c:import url="/WEB-INF/views/web/partials/message.jsp" />
		<sf:form action="/funds/allocate-budget" method="POST" modelAttribute="allocateBudgetForm" class="form-horizontal">
			<wm-csrf:csrfToken />
			<table>
				<thead>
					<tr>
						<th>Name</th>
						<th>Balance</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>Unreserved Cash</td>
						<td><fmt:formatNumber value="${general_cash}" currencySymbol="$" type="currency"/></td>
					</tr>
					<c:forEach var="project" items="${project_list}">
						<tr>
							<td><c:out value="${project.name}" /></td>
							<td><fmt:formatNumber value="${project.reservedFunds}" currencySymbol="$" type="currency"/></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

			<div class="control-group">
				<label for="transfer_from" class="control-label required">Transfer From:</label>
				<div class="controls">
					<sf:select id="transfer_from" name="transfer_from" path="transfer_from" value="${transfer_from}">
						<option name="" value="">- Select -</option>
						<sf:option value="general_cash" label="Unreserved Cash" />
						<sf:options items="${transfer_from}"/>
					</sf:select>
				</div>
			</div>

			<div class="control-group">
				<label for="transfer_to" class="control-label required">Transfer To:</label>
				<div class="controls">
					<sf:select id="transfer_to" name="transfer_to" path="transfer_to" value="${transfer_to}">
						<option name="" value="">- Select -</option>
						<sf:option value="general_cash" label="Unreserved Cash" />
						<sf:options items="${transfer_to}"/>
					</sf:select>
				</div>
			</div>

			<div class="control-group">
				<label for='amount' class="control-label required">Transfer Amount:</label>
				<div class="controls">
					<div class="input-prepend">
						<span class="add-on">$</span>
						<sf:input path="amount" value="${amount}" id="amount" maxlength="10" class="span2"/>
					</div>
					<input id="percentage" type="hidden" value="0.00">
				</div>
			</div>

			<div class="wm-action-container">
				<button id="closeBtn" type="button" class="button" >Cancel</button>
				<button id="allocate-funds-submit" type="submit" class="button" disabled="disabled">Reserve</button>
			</div>
		</sf:form>
	</div>

</wm:app>
