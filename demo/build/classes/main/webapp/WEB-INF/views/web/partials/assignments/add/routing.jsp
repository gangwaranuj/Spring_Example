<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<div class="pull-left sending-options-container">
	<a name="routing"></a>
	<div class="inner-container <c:out value="${(not isTemplate) ? 'routing-shrink' : 'template-save-container'}"></c:out>">
		<div class="page-header routing-page-header">
			<h4>
				<c:choose>
					<c:when test="${not isTemplate}">
						Routing Options
					</c:when>
					<c:otherwise>
						Template Save Options
					</c:otherwise>
				</c:choose>

				<div class="pull-right">
					<c:if test="${not isWorkBundle}">
						<label>
							<form:checkbox class="" path="show_in_feed" id="show_in_feed" />
							Post to <strong><span class="orange-brand" style="color:#f7961D;">Work</span><span class="gray-brand">Feed</span></strong><span style="color:#717A82">&#8482</span>
							<span class="tooltipped tooltipped-n" aria-label="Distribute your work to the Work Market Feed so that more workers can find and apply to the assignment.">
								<i class="wm-icon-question-filled"></i>
							</span>
						</label>
						<div class="help-block">Jobs are public when posted to WorkFeed.</div>
					</c:if>
				</div>
			</h4>
		</div>

		<div class="section-wrapper br" id="routing-container">
			<input type="hidden" name="routing.lanes" value="1" />
			<input type="hidden" name="routing.lanes" value="2" />
			<input type="hidden" name="routing.lanes" value="3" />
			<input type="hidden" name="resourceSelections" id="resourceSelections" value=""/>
			<c:if test="${is_admin}">
				<div class="wm-alert  -notice  -alert">
					<div class="wm-alert--text -notice">
						We've made some changes to Routing that we think you'll like. <strong><a href="#" class="activate-routing-beta">Try the Beta</a></strong>
					</div>
					<button class="button -primary"></button>
				</div>
			</c:if>

			<c:if test="${not isTemplate}">
				<p>Know the workers or talent pools you want to invite to perform this work? Invite them directly below and click send.</p>
			</c:if>
			<c:if test="${not isTemplate and empty assignment_for and not empty routable_groups}">
				<div>
					<div>
						<label for="routing_groups_ids">Talent Pools:</label>
						<div>
							<form:select path="routing.groupIds" id="routing_groups_ids" multiple="multiple" placeholder="Type any part of talent pool name" tabindex="4" items="${routable_groups}"/>
						</div>
					</div>
				</div>
			</c:if>

			<c:if test="${not isTemplate and empty assignment_for}">
				<div>
					<div id="routing_workers">
						<label for="routing_resource_ids">Workers:</label>
						<div>
							<form:input path="routing.resourceNumbers" type="text" id="routing_resource_ids" placeholder="Type any part of workers name (minimum of 3 characters)"/>
						</div>
					</div>
				</div>
			</c:if>
			<div class="control-group routing-checkboxes">
				<c:choose>
					<c:when test="${hideFirstToAccept}">
						<form:hidden path="assign_to_first_resource" id="assign_to_first_resource"/>
					</c:when>
					<c:otherwise>
						<label>First to Accept:</label>
						<div>
							<label>
								<form:checkbox path="assign_to_first_resource" id="assign_to_first_resource"/>
								<span>Automatically assign to the first applicant</span>
								<span class="tooltipped tooltipped-n" aria-label="If unchecked, you will have the opportunity to review workers who respond and choose whom to assign.">
									<i class="wm-icon-question-filled"></i>
								</span>
							</label>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
			<c:if test="${isTemplate}">
				<div>
					<div>
						<label for="routing_groups_ids">Talent Pools: <span class="tooltipped tooltipped-n" aria-label="Save talent pool(s) to this template as the default routing option."><i class="wm-icon-question-filled"></i></span></label>
						<div>
							<form:select path="groupIds" id="routing_groups_ids" multiple="multiple" class="chzn-select" tabindex="4" items="${routable_groups}"/>
						</div>
					</div>
				</div>
			</c:if>
		</div>

		<div class="send-section">
			<c:choose>
				<c:when test="${isTemplate}">
					<div class="wm-action-container">
						<a href="/settings/manage/templates" class="submit button">Cancel</a>
						<c:choose>
							<c:when test="${not empty form.id}">
								<button type="button" id="submit-form" class="button">Save Template Changes</button>
							</c:when>
							<c:otherwise>
								<button type="button" id="submit-form" class="submit button">Create Template</button>
							</c:otherwise>
						</c:choose>
					</div>
				</c:when>
				<c:otherwise>
					<div id="dynamic-submit">
						<div class="form-actions">
							<div class="pull-left control-group">
								<div class="controls">
									<button type="button" id="submit-form" class="button">Send</button>
									<c:if test="${empty assignment_for}">
										<c:if test="${empty bundleParent}">
											<button type="button" id="search-resources" class="button">Search People</button>
										</c:if>
									</c:if>
								</div>
							</div>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>

	</div>

	<c:if test="${not isTemplate}">
		<div class="or-container orange-brand" style="color:#f7961D;">or</div>
		<div class="button-container">
			<a class="sendworkbutton"><img src="${mediaPrefix}/images/worksend-button-txt.png"/></a>
			<input type="hidden" name="smart_route" value="false">
		</div>
	</c:if>
</div>
