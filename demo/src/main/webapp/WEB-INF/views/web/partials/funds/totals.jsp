<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="row account-status-overview">
	<div class="span3">
		<div class="tile tile-group">
			<h2>
				<span class="important total-text">Total Payables</span>
				<span class="total-value"><fmt:formatNumber value="${buyerSums.pastDue}" type="currency" /></span>
			</h2>
		</div>
	</div>
	<div class="span3">
		<div class="tile tile-group">
		<h2>
			<span class="warning total-text">Current Payables</span>
			<span class="total-value"><fmt:formatNumber value="${buyerSums.upcomingDue}" type="currency" /></span>
		</h2>
		</div>
	</div>
	<div class="span3">
		<div class="tile tile-group">
			<h2>
				<span class="total-text">Cash Balance</span>
				<span class="total-value"><fmt:formatNumber value="${spendLimit}" type="currency" /></span>
			</h2>
		</div>
	</div>
</div>

