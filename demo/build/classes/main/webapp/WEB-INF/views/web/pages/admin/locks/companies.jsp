<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Locked Companies" webpackScript="admin">
	<script>
		var config = {
			mode: 'lockedCompanies'
		};
	</script>

<div class="dn">
	<div id="unlock_form_container" class="unlock">
		<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/unlock.jsp"/>
	</div>
</div>

<div class="sidebar admin">
	<jsp:include page="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
</div>

<div class="content">
	<div class="row-fluid">
		<div class="span8">
			<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

			<h1>Locked Companies</h1>

			<div id="companies_list_container">
				<table id="companies_list" class="table table-striped">
					<thead>
						<tr>
							<th>Company</th>
							<th>&nbsp;</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td colspan="2" class="dataTables_empty">Loading data from server</td>
						</tr>
					</tbody>
				</table>
			</div>

			<div class="companies_list_msg alert-message warning dn"></div>
		</div>
	</div>
</div>

<c:import url="/WEB-INF/views/web/partials/admin/manage/company/unlock_header.jsp">
	<c:param name="hideBar" value="true"/>
</c:import>

</wm:admin>
