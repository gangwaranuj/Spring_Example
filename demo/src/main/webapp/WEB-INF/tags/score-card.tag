<%@ tag description="Score Card" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="score" required="false" %>
<%@ attribute name="satisfaction" required="true" %>
<%@ attribute name="ontime" required="true" %>
<%@ attribute name="deliverables" required="true" %>
<%@ attribute name="paidassign" required="true" %>
<%@ attribute name="paidassignforcompany" required="false" %>
<%@ attribute name="cancelled" required="true" %>
<%@ attribute name="abandoned" required="true" %>
<%@ attribute name="classlist" required="false" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="showrecent" required="false" %>

<div class="score-card ${classlist} <c:if test="${not empty showrecent and showrecent}">-recent</c:if>" id="${id}">
	<h2 class="score-card--title" data-score-all="${satisfaction}%">
		<c:if test="${not empty showrecent and showrecent}"><wm:icon name="graph" active="true" /></c:if>
		Satisfaction
	</h2>
	<div class="score-card--details">
		<c:choose>
			<c:when test="${not empty showrecent and showrecent}">
				<wm:toggle name="score-card-toggle" value="all" text="All" isChecked="true" />
				<wm:toggle name="score-card-toggle" value="yours" text="Yours" />
				<table class="score-card--table">
					<thead>
						<tr>
							<th></th>
							<th>3 Mo.</th>
							<th>All</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<c:choose>
								<c:when test="${not empty isDispatch and isDispatch}">
									<td><a class="score-card--metrics" href="javascript:void(0);">Paid Assign</a></td>
								</c:when>
								<c:otherwise>
									<td><a class="score-card--metrics" href="javascript:void(0);">Paid Assign <span>(${paidassignforcompany} for you)</span></a></td>
								</c:otherwise>
							</c:choose>
							<td>n/a</td>
							<td>${paidassign}</td>
						</tr>
						<tr>
							<td><a class="score-card--metrics" href="javascript:void(0);">Cancelled</a></td>
							<td>n/a</td>
							<td>${cancelled}</td>
						</tr>
						<tr>
							<td><a class="score-card--metrics" href="javascript:void(0);">Abandoned</a></td>
							<td>n/a</td>
							<td>${abandoned}</td>
						</tr>
					</tbody>
				</table>
				<hr />
				<wm:completion classlist="score-card--overall" name="Satisfaction" min="0" max="100" unit="%" value="${satisfaction}" />
				<span class="completion-bar--name">On-Time</span>
				<span class="completion-bar--value">${ontime}%</span>
				<wm:progress-bar size="small" width="${ontime}"/>
				<span class="completion-bar--name">Deliverables</span>
				<span class="completion-bar--value">${deliverables}%</span>
				<wm:progress-bar size="small" width="${deliverables}"/>
			</c:when>
			<c:otherwise>
				<wm:completion classlist="score-card--overall" name="Satisfaction" min="0" max="100" unit="%" value="${satisfaction}" />
				<span class="completion-bar--name">On-Time</span>
				<span class="completion-bar--value">${ontime}%</span>
				<wm:progress-bar size="small" width="${ontime}"/>
				<span class="completion-bar--name">Deliverables</span>
				<span class="completion-bar--value">${deliverables}%</span>
				<wm:progress-bar size="small" width="${deliverables}"/>
				<c:choose>
					<c:when test="${not empty isDispatch and isDispatch}">
						<span class="score-card--metrics" data-score="${paidassign}">Paid Assign</span>
					</c:when>
					<c:otherwise>
						<span class="score-card--metrics" data-score="${paidassign}">Paid Assign <span>(${paidassignforcompany} for you)</span></span>
					</c:otherwise>
				</c:choose>
				<span class="score-card--metrics" data-score="${cancelled}">Cancelled</span>
				<span class="score-card--metrics" data-score="${abandoned}">Abandoned</span>
			</c:otherwise>
		</c:choose>
	</div>
</div>
