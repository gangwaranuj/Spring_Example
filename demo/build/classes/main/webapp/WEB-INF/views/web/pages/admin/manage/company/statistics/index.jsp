<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script>
	var config = {
		mode: 'statistics',
		companyId: ${company.id}
	};
</script>

<div class="row_sidebar_left">
	<c:if test="${not empty companyView}">
		<div class="sidebar admin">
			<jsp:include page="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
		</div>
	</c:if>

	<div class="content" style="width: 80%;">
		<div id="dynamic_messages"></div>

		<c:if test="${not empty companyView}">
			<c:import url="/WEB-INF/views/web/partials/admin/manage/company/header.jsp"/>
		</c:if>

		<div class="inner-content">

			<div class="kpi-data-chart-group">
				<div class="caption unit whole">
					<div class ="kpi-header-div">
						<h3>Last Month Company Snapshot</h3>
					</div>
				</div>

				<div class="unit whole snapshot-minimized" id="min1">
					<div class="minimized-label-title"><p>Assignments Sent</p><p></p></div>
					<div class="minimized-label-value"><p></p></div>
					<div class="minimized-chart" id="snapshot-min-1-content"></div>
				</div>

				<div class="unit whole snapshot-minimized" id="min2">
					<div class="minimized-label-title"><p>Void Rate</p><p></p></div>
					<div class="minimized-label-value"><p></p></div>
					<div class="minimized-chart" id="snapshot-min-2-content"></div>
				</div>

				<div class="unit whole snapshot-minimized" id="min3">
					<div class="minimized-label-title"><p>Paid Amount</p><p></p></div>
					<div class="minimized-label-value"><p></p></div>
					<div class="minimized-chart" id="snapshot-min-3-content"></div>
				</div>

				<div class="unit whole snapshot-minimized" id="min4">
					<div class="minimized-label-title"><p>Average Price</p><p></p></div>
					<div class="minimized-label-value"><p></p></div>
					<div class="minimized-chart" id="snapshot-min-4-content"></div>
				</div>

				<div class="unit whole snapshot-minimized" id="min5">
					<div class="minimized-label-title"><p>Life Cycle Days</p><p></p></div>
					<div class="minimized-label-value"><p></p></div>
					<div class="minimized-chart" id="snapshot-min-5-content"></div>
				</div>
			</div>

			<div class="kpi-data-chart">
				<div class="caption unit whole">
					<div class ="kpi-header-div">
						<h3>Assignment Spend</h3>
						<span id="information-tooltip-throughput" class="information-tooltip"><img src="${mediaPrefix}/images/icons/i.svg" onmouseover="this.src='${mediaPrefix}/images/icons/i_o.svg'" onmouseout="this.src='${mediaPrefix}/images/icons/i.svg'"/></span>
					</div>

					<div class="btn-group btn-group-throughput">
						<div class="d3-tip s custom-tip" id="btn-group-throughput-tooltip"></div>
						<button id="button-throughput-daily" class="time-filter">Daily</button>
						<button id="button-throughput-weekly" class="time-filter">Weekly</button>
						<button id="button-throughput-monthly" class="time-filter active">Monthly</button>
					</div>
				</div>
				<div class="unit whole">
					<div class="legend unit golden-small kpi-legend">
						<div><span class="throughput-volume"></span><p>Spend Volume</p></div>
						<div><span class="average-price"></span><p class="average-price-p">Average Price</p></div>
					</div>
					<div id="assignmentsBarGraph"></div>
				</div>
			</div>

			<div class="kpi-data-chart life-cycle-chart">
				<div class="caption unit whole" >
					<div class ="kpi-header-div">
						<h3>Assignment Life</h3>
						<span id="information-tooltip-life-cycle" class="information-tooltip"><img src="${mediaPrefix}/images/icons/i.svg" onmouseover="this.src='${mediaPrefix}/images/icons/i_o.svg'" onmouseout="this.src='${mediaPrefix}/images/icons/i.svg'"/></span>
					</div>

					<div class="btn-group btn-group-life-cycle">
						<div class="d3-tip n custom-tip" id="btn-group-life-cycle-tooltip"></div>
						<div class="d3-tip s custom-tip" id="btn-group-life-cycle-tooltip-south"></div>
						<button id="button-life-cycle-monthly" class="time-filter">Month</button>
						<button id="button-life-cycle-quarterly" class="time-filter">Quarter</button>
						<button id="button-life-cycle-yearly" class="time-filter active">Year</button>
					</div>
				</div>

				<div class="unit whole life-cycle-unit-whole">
					<div class="chart-title">Days</div>
					<div id="ganttChart"></div>
				</div>
			</div>

			<div class="kpi-data-chart segmentation-chart">
				<div class="caption unit whole">
					<div class ="kpi-header-div">
						<h3>Segmentation</h3>
						<span id="information-tooltip-segmentation" class="information-tooltip"><img src="${mediaPrefix}/images/icons/i.svg" onmouseover="this.src='${mediaPrefix}/images/icons/i_o.svg'" onmouseout="this.src='${mediaPrefix}/images/icons/i.svg'"/></span>
					</div>
					<div class="btn-group btn-group-segmentation">
						<div class="d3-tip n custom-tip" id="btn-group-segmentation-tooltip"></div>
						<div class="d3-tip s custom-tip" id="btn-group-segmentation-tooltip-south"></div>
						<button id="button-segmentation-monthly" class="time-filter">Month</button>
						<button id="button-segmentation-quarterly" class="time-filter">Quarter</button>
						<button id="button-segmentation-yearly" class="time-filter active">Year</button>
					</div>
				</div>

				<div class="unit whole segmentation-chart-unit-whole">
					<h4>Assignment Breakdown</h4>
					<div class="pie-and-legend segmentation-chart-pie-and-legend">
						<div id="assignmentPieGraph" class="unit golden-large segmentation-assignment-pie-graph"></div>
						<div class="legend unit golden-small segmentation-assignment-legend ">

							<div><span class="cancel-rate"></span><p>Cancel</p></div>
							<div><span class="void-rate"></span><p>Void</p></div>
							<div><span class="paid-rate"></span><p>Paid</p></div>
							<div><span class="sent-remaining"></span><p>Active</p></div>

						</div>
					</div>
				</div>

				<div class="unit whole segmentation-chart-unit-whole">
					<h4>Routing Breakdown</h4>
					<div class="pie-and-legend segmentation-chart-pie-and-legend-second">
						<div id="assignmentPieGraphSecond" class="unit golden-large segmentation-assignment-pie-graph"></div>
						<div class="legend unit golden-small segmentation-assignment-legend segmentation-assignment-legend-second">
							<div><span class="work-send"></span><p>WorkSend</p></div>
							<div><span class="user-send"></span><p>User Send</p></div>
							<div><span class="groups"></span><p>Talent Pools</p></div>
							<div><span class="search-pie"></span><p>Search</p></div>
						</div>
					</div>
				</div>
			</div>

			<div class="kpi-data-chart">
				<div class="caption unit whole" >
					<div class ="kpi-header-div">
						<h3>Top Users and Projects</h3>
					</div>
					<div class="btn-group btn-group-clients">
						<div class="d3-tip n custom-tip" id="btn-group-clients-tooltip"></div>
						<button id="button-clients-monthly" class="time-filter">Month</button>
						<button id="button-clients-quarterly" class="time-filter">Quarter</button>
						<button id="button-clients-yearly" class="time-filter active">Year</button>
					</div>
				</div>
				<table class="simple table table-stripped" id="table-topClients">
				</table>

				<table class="simple table table-stripped" id="table-topProjects">
				</table>
			</div>

			<div class="kpi-data-chart">
				<div class="caption unit whole" >
					<div class ="kpi-header-div">
						<h3>Market Breakdown</h3>
					</div>
					<div class="btn-group btn-group-market">
						<div class="d3-tip n custom-tip" id="btn-group-market-tooltip"></div>
						<button id="button-market-monthly" class="time-filter">Month</button>
						<button id="button-market-quarterly" class="time-filter">Quarter</button>
						<button id="button-market-yearly" class="time-filter active">Year</button>
					</div>
				</div>
				<div id="marketDataMap"></div>
				<table class="simple table legend-market" id="table-legend-market"></table>
				<table id="marketDataMapZoomControls"></table>
				<table class="simple table table-stripped" id="table-topMarkets"></table>
				<table class="simple table table-stripped" id="table-topResources"></table>
			</div>
		</div>
	</div>
</div>
