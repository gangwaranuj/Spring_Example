<%@ page import="com.workmarket.configuration.Constants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<c:set var="defaultWorkFeePercentage"><%= Constants.DEFAULT_WORK_FEE_PERCENTAGE.intValue() %></c:set>

<wm:admin pagetitle="Plans" webpackScript="plans">
	<script>
		var config= {
			defaultWorkFeePercentage: ${defaultWorkFeePercentage}
		};
	</script>

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
	</div>

	<div class="content">
		<div class="page-header">
			<h3>Plans <a href="#new" class="button pull-right">New Plan</a></h3>
		</div>

		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}"/>
		</c:import>

		<div id="plan-form" class="dn">
			<div class="row">
				<div class="span12">
					<div id="plan-title-form">
							<%-- placeholder --%>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="span6">
					<div id="venues-form">
						<div class="well-b2">
							<h5>Add Features</h5>
							<div class="well-content">
								<div class="control-group">
									<label for="venues" class="control-label">Select a feature to add to your plan</label>
									<div class="controls">
										<select id="venues" class="input-block-level" data-toggle="form"></select>
									</div>
								</div>
								<button type="submit" class="button" data-action="add">Add Venue</button>
							</div>
						</div>
					</div>
				</div>

				<div class="span2">
					<div class="segue">
						<i class="icon-arrow-right icon-4x muted"></i>
					</div>
				</div>

				<div class="span6">
					<div class="well-b2">
						<h5>Current Features</h5>
						<div class="well-content">
							<div id="venues-cart">
									<%-- placeholder --%>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="row">
				<div id="plan-transaction-fee" class="span12">
						<%-- placeholder --%>
				</div>
			</div>
			<hr />
		</div>

		<div id="plans">
			<table>
				<thead>
				<tr>
					<th class="code">Code</th>
					<th class="description">Description</th>
					<th class="actions">Edit</th>
					<th class="actions">Delete</th>
				</tr>
				</thead>
				<tbody id="plans-index">
					<%-- placeholder for rows --%>
				</tbody>
			</table>
		</div>
	</div>

</wm:admin>
