<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Landing Page"
	bodyclass="accountSettings page-campaigns"
	webpackScript="campaigns"
>
	<script>
		var config = {
			name: 'campaigns',
			type: 'list'
		};
	</script>

	<%@ page isELIgnored="false" %>

	<div class="inner-container">
		<!--Landing Page and Invitations Tabs Navigations-->
		<c:import url="/WEB-INF/views/web/partials/recruiting/navigation.jsp"/>
		<div class="page-header clear">
			<h3 class="fl">Landing Page</h3>
			<sec:authorize url="/campaigns/new">
				<a href="/campaigns/new" class="button pull-right">New Landing Page</a>
			</sec:authorize>
		</div>

		<div class="alert alert-info">
			<p>
				Landing pages are an effective tool to bring new workers and companies into Work Market. With a landing page your company can maintain an external presence which you can promote through your own marketing channels or by attaching it to a .csv upload
				<span
					aria-label="If inviting a company, only one user should be invited to setup the company's account. That user then invites subsequent users as employees."
					class="tooltipped tooltipped-n"
				>
					<i class="wm-icon-question-filled"></i>
				</span>
				of invitations.
				<a href="https://workmarket.zendesk.com/hc/en-us/articles/209336448" target="_blank"><strong>Learn More</strong></a>
			</p>
		</div>

		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}"/>
		</c:import>


		<table id="campaigns_list">
			<thead>
				<tr>
					<th>Landing Page</th>
					<th>Launch Date</th>
					<th>Clicks</th>
					<th>Signups</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="5" class="dataTables_empty">Loading data from server</td>
				</tr>
			</tbody>
		</table>
	</div>

</wm:app>
