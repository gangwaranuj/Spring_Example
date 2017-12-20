<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="well">
	<h5>Payments &amp; Funds</h5>
	<ul class="stacked-nav">
		<sec:authorize access="hasRole('PERMISSION_MANAGEBANK')">
		
		<li><a href="/funds/accounts/new">Add Financial Account</a></li>
		<li><a href="/settings/alerts">Low Balance Alerts</a></li>
		</sec:authorize>

		<sec:authorize access="hasRole('PERMISSION_ADDFUNDS')">
			<li><a href="/funds/add">Add Funds</a></li>
		</sec:authorize>

		<sec:authorize access="hasRole('PERMISSION_WITHDRAW')">
			<li><a href="/funds/withdraw">Withdraw Funds</a></li>
		</sec:authorize>
	</ul>
</div>