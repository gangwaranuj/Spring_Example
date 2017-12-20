<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="showSaveButton" value="${is_admin || (is_active_resource && hasResourceFields)}"/>

<div id="pane-custom-fields">
	<form action="/assignments/save_custom_fields/${work.workNumber}" id="custom_fields_form" method="post">
		<wm-csrf:csrfToken />

		<c:choose>
			<%--Company custom fields--%>
			<c:when test="${is_admin}">
				<div class="group_set_id_container"></div>

				<c:if test="${hasOwnerFields}">
					<input type="hidden" name="onComplete" value="false">
					<input type="hidden" name="isPendingSets" id="is-pending-sets" value="false">

					<div class="media completion">
						<img class="custom-fields" src="${mediaPrefix}/images/live_icons/assignments/custom_fields_v2.svg"/>
						<div class="media-body" id="pane-buyer-custom-fields">
							<h4>Custom Fields for <c:out value="${companyName}"/></h4>
						</div>

						<c:if test="${showSaveButton}">
							<div class="actions custom-fields-save" id="submit_customfields_cta">
								<c:import url='/WEB-INF/views/web/partials/assignments/details/add-cfs-in-progress.jsp'/>
								<div class="save-fields">
									<button class="button completion_button">Save Custom Fields</button>
									<div class="help-block">Press <strong>Shift + Enter</strong> to save.</div>
								</div>
							</div>
						</c:if>
					</div>
				</c:if>

				<c:if test="${hasResourceFields}">
					<div class="media-body completion" id="resource_completion_cf">
						<h4 id="resource_cf_header">Custom Fields for Worker</h4>
						<p>These fields are requested to be completed by the worker.</p>
						<div id="pane-resource-custom-fields" class="pane-resource"></div>

						<c:if test="${showSaveButton}">
							<div class="actions form-stacked custom-fields-save pull-right tar" id="submit_customfields_cta">
								<button class="button completion_button">Save Custom Fields</button>
								<div class="help-block">Press <strong>Shift + Enter</strong> to save.</div>
							</div>
						</c:if>
					</div>
				</c:if>
			</c:when>

			<%--Worker Custom Fields--%>
			<c:otherwise>
				<div class="media completion">
					<img class="custom-fields" src="${mediaPrefix}/images/live_icons/assignments/custom_fields_v2.svg"/>
					<div class="pane-resource media-body">
						<h4>Custom Fields
							<div class="completed fr dn" id="custom_fields_complete">
								<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/complete-icon.jsp"/>
								<small class="meta">completed</small>
							</div>
							<span class="incomplete meta dn" id="custom_fields_incomplete">
								<span class="label label-important">Required</span>
							</span>
						</h4>
						<c:choose>
							<c:when test="${!work.status.code == workStatusTypes['SENT']}">
								<p><strong><c:out value="${companyName}"/> has requested the following data. Please review and update as necessary:</strong></p>
							</c:when>
							<c:otherwise>
								<p><strong><c:out value="${companyName}"/> would like to share the following important information:</strong></p>
							</c:otherwise>
						</c:choose>

						<div id="<c:out value="${param.containerId}" default="pane"/>-custom-fields">
							<form action="/assignments/save_custom_fields/${work.workNumber}" id='custom_fields_form' method='post'>
								<wm-csrf:csrfToken/>

								<div class="media-body" id="pane-buyer-custom-fields" style="margin-bottom: -18px;"></div>
								<div class="group_set_id_container"></div>

								<input type="hidden" name="onComplete" value="false">

								<div id="<c:out value="${param.containerId}" default="pane"/>-resource-custom-fields"></div>

								<c:if test="${showSaveButton || (is_active_resource && hasResourceFields)}">
									<a class="button resource-save-custom-fields-and-complete pull-right completion_button">Save Custom Fields</a>
									<br/>
								</c:if>
							</form>
						</div>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</form>
</div>
