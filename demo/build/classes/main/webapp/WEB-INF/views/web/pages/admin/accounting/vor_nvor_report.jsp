<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Detailed VOR/NVOR Report">

<c:import url="/breadcrumb">
	<c:param name="pageId" value="adminVORNVORDetailedReport" />
	<c:param name="admin" value="true" />
</c:import>

<div class="content">
	<div class="info unit whole">
		<p>Below dates are optional. When blank the report will start at the beginning of current fiscal year.</p>
	</div>
	<form action="<c:url value="/admin/accounting/export_end_of_year_taxes_report"/>" id="filter-form" method="get">
		<input type="text" id="from" name="from" class="span2" placeholder="MM/DD/YYYY" > to
		<input type="text" id="to" name="to" class="span2" placeholder="MM/DD/YYYY"/>
		<input type="submit" value="Run" class="button" />
	</form>
</div>

<script>
	$(function() {
		$( "#from" ).datepicker();
		$( "#to" ).datepicker();
	});
</script>

</wm:admin>