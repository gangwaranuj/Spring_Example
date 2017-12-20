<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set scope="request" var="isInlineEditingAllowed" value="${work.status.code eq workStatusTypes['INVOICED'] || work.status.code eq workStatusTypes['CANCELLED_WITH_PAY'] || work.status.code eq workStatusTypes['CANCELLED_PAYMENT_PENDING'] || work.status.code eq workStatusTypes['PAYMENT_PENDING'] || work.status.code eq workStatusTypes['PAID']}"/>

	<c:choose>
		<c:when test="${isWorkBundle}">
			<div class="bundle_accordion media completion" id="overviewAccordion">
		</c:when>
		<c:otherwise>
			<div class="accordion media completion" id="overviewAccordion">
		</c:otherwise>
	</c:choose>
	<div class="completion-icon">
		<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/overview_v2.jsp"/>
	</div>
	<c:if test="${!isInlineEditingAllowed && is_admin}">
		<small class="inline-edit meta inline-contain">
			<span class="inline_editors" data-container="#overviewAccordion" data-type="description">Edit</span>
		</small>
	</c:if>
	<div class="media-body">
		<div class="accordion-heading">
			<a id="accordion_overview" data-toggle="collapse" href="#overview_toggle">
				<h4>Overview
					<i class="toggle-icon pull-right"></i>
				</h4>
			</a>
		</div>
		<div id="overview_toggle" class="accordion-body collapse <c:if test="${is_active_resource || (work.status.code == workStatusTypes['DRAFT'] ||work.status.code == workStatusTypes['SENT'])}">in</c:if>">
			<div id="description_div" class="summarize unstylize_all scroll-box tall description-container"></div>
		</div>
		<form:textarea data-richtext="wysiwyg" path="work.description" id="desc-text" class="input-block-level" />
		<div class="form-inline pull-right inline-contain">
			<button class="button inline_editors" data-container="#overviewAccordion" data-type="description">Cancel</button>
			<button class="button inline-update"
					data-div="description-container"
					data-type="description"
					data-editor="#desc-text"
					data-container="#overviewAccordion">Save Changes
			</button>
		</div>
	</div>
</div>

<c:if test="${is_admin && not empty work.uniqueExternalIdDisplayName}">
	<div class="accordion media completion" id="externalIdAccordion">
		<c:if test="${!isInlineEditingAllowed && is_admin}">
			<small class="inline-edit inline-edit-special inline-contain meta">
				<span class="inline-editing-toggle inline_editors" data-container="#externalIdAccordion" data-type="externalId">Edit</span>
			</small>
		</c:if>
		<div class="completion-icon">
			<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/overview_v2.jsp"/>
		</div>
		<div class="media-body">
			<div class="accordion-heading">
				<a data-toggle="collapse" data-parent="#externalIdAccordion" href="#externalId">
					<h4>Unique ID
						<i class="toggle-icon pull-right"></i>
					</h4>
				</a>
			</div>
			<div id="externalId" class="accordion-body collapse <c:if test="${!(is_admin || isInternal)}">in</c:if>">
				<div class="summarize unstylize_all scroll-box tall externalId-container">
					<strong><c:out value="${work.uniqueExternalIdDisplayName}"></c:out></strong>:
					<c:out value="${work.uniqueExternalIdValue}"></c:out>
				</div>
			</div>
			<span style="margin-top:10px; display: none;" class="dn" aria-label="An unique value is required.">
				<table>
					<tr>
						<td>
							<label class="span3"><c:out value="${work.uniqueExternalIdDisplayName}"></c:out></label>
						</td>
						<td>
							<span class="tooltipped tooltipped-nw" aria-label="An unique value is required.">
								<form:input path="work.uniqueExternalIdValue" id="externalId-text" class="input-block-level"/>
							</span>
						</td>
					</tr>
				</table>
			</span>
			<div class="form-inline pull-right inline-contain">
				<button class="button inline_editors" data-container="#externalIdAccordion" data-type="externalId">Cancel</button>
				<button class="button inline-update"
						data-div="externalId-container"
						data-type="externalId"
						data-editor="#externalId-text"
						data-container="#externalIdAccordion">Save Changes
				</button>
			</div>
		</div>
	</div>
</c:if>


<c:if test="${(not empty work.instructions) && (!work.privateInstructions || is_admin || is_active_resource || isInternal)}">
	<div class="accordion media completion" id="specialAccordion">
		<c:if test="${!isInlineEditingAllowed && is_admin}">
			<small class="inline-edit inline-edit-special inline-contain meta">
				<span class="inline-editing-toggle inline_editors" data-container="#specialAccordion" data-type="description">Edit</span>
			</small>
		</c:if>
		<div class="completion-icon">
			<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/special_instructions_v2.jsp"/>
		</div>
		<div class="media-body">
			<div class="accordion-heading">
				<a data-toggle="collapse" data-parent="#specialAccordion" href="#special-instructions">
					<h4>Special Instructions
						<i class="toggle-icon pull-right"></i>
					</h4>
				</a>
			</div>
			<div id="special-instructions" class="accordion-body collapse <c:if test="${!(is_admin || isInternal)}">in</c:if>">
				<div class="summarize unstylize_all scroll-box tall special-container">
					<c:choose>
						<c:when test="${not is_autotask}">
							<c:out escapeXml="false" value="${work.instructions}"/>
						</c:when>
						<c:otherwise>
							<c:out escapeXml="false" value="${wmfmt:tidy(wmfmt:nl2br(work.instructions))}"/>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<form:textarea data-richtext="wysiwyg" path="work.instructions" id="instructions-text" class="input-block-level"/>
			<div class="form-inline pull-right inline-contain">
				<button class="button inline_editors" data-container="#specialAccordion" data-type="instructions">Cancel</button>
				<button class="button inline-update"
						data-div="special-container"
						data-type="instructions"
						data-editor="#instructions-text"
						data-container="#specialAccordion">Save Changes
				</button>
			</div>
		</div>
	</div>
</c:if>

<c:if test="${not empty work.desiredSkills}">
	<div class="accordion media completion" id="skillsAccordion">
		<img src="${mediaPrefix}/images/live_icons/assignments/skills_specialty_v2.svg">
		<div class="media-body">
			<div class="accordion-heading">
				<a data-toggle="collapse" data-parent="#skillsAccordion" href="#skills">
					<h4>Skills and Specialty
						<i class="toggle-icon pull-right"></i>
					</h4>
				</a>
			</div>
			<div id="skills" class="accordion-body collapse <c:if test="${is_active_resource || (work.status.code == workStatusTypes['DRAFT'] ||work.status.code == workStatusTypes['SENT'])}">in</c:if>">
				<p><c:out value='${work.desiredSkills.split("--")[0].replaceAll(",", ", ")}'></c:out></p>
			</div>
		</div>
	</div>
</c:if>


<c:if test="${showDocuments}">
	<c:import url='/WEB-INF/views/web/partials/assignments/details/documents.jsp'/>
</c:if>


<c:if test="${(is_admin || is_active_resource) && is_rating_shown}">
	<div class="accordion media completion" id="specialAccordion">
		<img id="ratings-img" src="${mediaPrefix}/images/live_icons/assignments/star.svg">
		<div class="media-body">
			<div class="accordion-heading">
				<a data-toggle="collapse" data-parent="#ratingAccordion" href="#ratings">
					<h4>Ratings <i class="toggle-icon pull-right"></i></h4>
				</a>
			</div>

			<div id="ratings" class="accordion-body collapse in">
				<c:import url='/WEB-INF/views/web/partials/assignments/details/ratings.jsp'/>
			</div>
		</div>
	</div>
</c:if>
