<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Insurance" bodyclass="accountSettings" webpackScript="profileedit">

	<script>
		var config = {
			type: 'insurance'
		}
	</script>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp" />
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header">
					<h3>Insurance</h3>
				</div>

				<c:import url="/WEB-INF/views/web/partials/profile/insurance_table.jsp?current_or_pending=current"/>
				<c:import url="/WEB-INF/views/web/partials/profile/insurance_table.jsp?current_or_pending=pending"/>

				<div class="page-header">
					<h4>Add Insurance</h4>
				</div>

				<div class="alert">
					<button type="button" class="close" data-dismiss="alert">&times;</button>
					<strong>Need to purchase insurance?</strong> Work Market has a relationship with Insureon, an online insurance
					agent that specializes in insuring small and micro businesses.<br/>
					<a href="/profile-edit/insureon" target="_blank"><i class="icon-info-sign"></i> Learn more and apply for Insureon insurance</a>
				</div>

				<form id="insuranceForm" class="form-horizontal" action="/profile-edit/insurancesave" method="post" enctype="multipart/form-data">
					<wm-csrf:csrfToken />

					<input type="hidden" id="workers_comp_insurance_id" value="${workers_comp_insurance_id}"/>
					<div class="control-group">
						<label class="control-label" for="industry">Industry</label>
						<div class="controls">
							<select id="industry" name="industry">
								<option value="">- Select -</option>
								<c:forEach items="${industry}" var="an_industry">
									<option value="${an_industry.id}"  ${(prefill_industry == an_industry.id)?'selected':''} ><c:out value="${an_industry.name}" /></option>
								</c:forEach>
							</select>
						</div>
					</div>
					<div id="select_insurance" class="control-group dn">
						<label class="control-label">Insurance Type</label>
						<div class="controls" id="insurance_type"></div>
					</div>
					<div id="insurance_details" class="dn">
						<div class="control-group">
							<div id="override-section" class="controls dn">
								<input type="checkbox" name="notApplicableOverride" id="override">
								<span>I attest that my state does not require me to carry workers compensation insurance.</span>
							</div>
						</div>
						<div id="affirm">
							<div class="control-group">
								<label class="control-label required">Provider</label>
								<div class="controls">
									<input type="text" name="provider" maxlength="255"/>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label required" >Policy Number</label>
								<div class="controls">
									<input type="text" name="policyNumber" maxlength="255"/>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label required">Coverage</label>
								<div class="controls">
									<input type="text" id="coverage" name="coverage" maxlength="10"/>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">Issue Date</label>
								<div class="controls">
									<input type="text" id="issueDate" name="issueDate" maxlength="10"/>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label">Expiration Date</label>
								<div class="controls">
									<input type="text" id="expirationDate" name="expirationDate" maxlength="10"/>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label required">Attachment</label>
								<div class="controls">
									<input type="file" id="attachment" name="file" />
								</div>
							</div>
						</div>
						<div class="wm-action-container">
							<button type="submit" class="button">Add Insurance</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>

</wm:app>
