<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="pageScript" value="wm.pages.mobile.assignments.customField" scope="request"/>
<c:set var="backUrl" value="/mobile/assignments/details/${work.workNumber}" scope="request"/>

<span class="custom-fields-page">
	<div class="wrap">
		<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
			<jsp:param name="title" value="${title}" />
		</jsp:include>

		<c:import url="/WEB-INF/views/mobile/partials/panel.jsp"/>

		<div class="content grid">
			<div class="unit whole" id="public-message">
				<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
			</div><%--unit whole--%>

			<form action="/mobile/assignments/customfields/${work.workNumber}" id="custom_fields_form" method="post">
				<wm-csrf:csrfToken />

				<c:forEach var="group" items="${work.customFieldGroups}" varStatus="groupStatus">
					<input type="hidden" name="customFieldGroupSet[${groupStatus.index}].id" value="${group.id}">
					<input type="hidden" name="customFieldGroupSet[${groupStatus.index}].position" value="${group.position}">
					<input type="hidden" name="customFieldGroupSet[${groupStatus.index}].name" value="<c:out value="${group.name}" />">
				</c:forEach>

				<%-- list client custom fields --%>
				<c:if test="${(isAdmin or isOwner) and hasBuyerCustomFields}">
					<c:import url="/WEB-INF/views/mobile/partials/assignments/buyer_custom_fields.jsp" />
				</c:if>

				<%-- list worker custom fields --%>
				<c:if test="${(isAdmin or isOwner or isActiveResource) and hasResourceCustomFields}">
					<div class="grid">
						<div class="unit whole">
							<h3>Fields for ${isActiveResource ? 'you' : 'worker'} to complete:</h3>
						</div><%--unit whole--%>
						<c:forEach var="group" items="${work.customFieldGroups}" varStatus="groupStatus">
							<c:forEach var="field" items="${group.fields}" varStatus="status">
								<c:if test="${(isAdmin or isOwner or isActiveResource) and field.type eq 'resource'}">
									<div class="unit whole seller-fields">
										<label <c:if test="${field.isRequired and isActiveResource}">class="required"</c:if> for="customFieldGroupSet[${groupStatus.index}].fields[${status.index}].value"><c:out value="${field.name}" /> :</label>
										<input type="hidden" name="customFieldGroupSet[${groupStatus.index}].fields[${status.index}].id" value="${field.id}"/>
										<c:choose>
											<c:when test="${fn:contains(field.defaultValue, ',')}">
												<select name="customFieldGroupSet[${groupStatus.index}].fields[${status.index}].value" value="${wmfmt:tidy(field.value)}">
													<option value="">Custom Field Options</option>
													<c:forEach var="option" items="${fn:split(field.defaultValue, ',')}">
														<option value="${wmfmt:tidy(option)}" <c:if test="${option eq field.value}">selected = "selected"</c:if>>${wmfmt:tidy(option)}</option>
													</c:forEach>
													<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-select-arrow-down.jsp"/>
												</select>
												<c:if test="${field.isRequired and isActiveResource}">
													<div class="required-field">
														<small><c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-asterisk.jsp"/>Required</small>
													</div>
												</c:if>
												<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-select-arrow-down.jsp"/>
											</c:when>
											<c:otherwise>
												<input type="text" name="customFieldGroupSet[${groupStatus.index}].fields[${status.index}].value" value="<c:out value="${field.value}" />" >
												<c:if test="${field.isRequired and isActiveResource}">
													<div class="required-field">
														<small><c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-asterisk.jsp"/>Required</small>
													</div>
												</c:if>
											</c:otherwise>
										</c:choose>
									</div><%--unit whole--%>
								</c:if>
							</c:forEach>
						</c:forEach>
					</c:if>

					<c:if test="${(isAdmin and hasBuyerCustomFields) or (isActiveResource and hasResourceCustomFields)}">
						<div class="unit whole">
							<button type="submit" class="save-custom-fields-button" value="Save Custom Fields">Save Custom Fields</button>
						</div><%--"unit-whole"--%>
					</c:if>

					<%-- list client custom fields, but only if user is not ALSO owner/admin --%>
					<c:if test="${isResource and hasBuyerCustomFields and not (isOwner or isAdmin)}">
						<div class="unit whole">
							<h3>These are additional fields that are relevant to this assignment:</h3>
							<c:import url="/WEB-INF/views/mobile/partials/assignments/buyer_custom_fields.jsp" />
						</div><%--unit-whole--%>
					</c:if>
				</div><%--grid--%>
			</form>
		</div><%--content--%>
	</div><%--wrap--%>
</span><%--custom-fields-page--%>
