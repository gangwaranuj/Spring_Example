<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="buyer-custom-fields-partial">
	<ul>
		<li>
			<h3>Fields for <c:out value="${work.company.name}" /> to complete</h3>
		</li>
		<c:forEach var="group" items="${work.customFieldGroups}" varStatus="groupStatus">
			<c:forEach var="field" items="${group.fields}" varStatus="status">
				<c:if test="${field.type eq 'owner'}">
					<li>
						<label for="customFieldGroupSet[${group.position}].fields[${status.index}].value"><c:out value="${field.name}" /></label>
						<input type="hidden" name="customFieldGroupSet[${group.position}].fields[${status.index}].id" value="${field.id}"/>
						<c:choose>
							<c:when test="${fn:contains(field.defaultValue, ',')}">
								<select name="customFieldGroupSet[${group.position}].fields[${status.index}].value" value="${wmfmt:tidy(field.value)}" <c:if test="${not (isAdmin or isOwner)}"> disabled class="disabled-select"</c:if> >
									<option value="">Custom Field Options</option>
									<c:forEach var="option" items="${fn:split(field.defaultValue, ',')}">
										<option value="${wmfmt:tidy(option)}" <c:if test="${option eq field.value}"> selected="selected" </c:if>>${wmfmt:tidy(option)}</option>
									</c:forEach>
								</select>
								<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-select-arrow-down.jsp"/>
							</c:when>
							<c:otherwise>
								<input type="text" id="bigCf" name="customFieldGroupSet[${group.position}].fields[${status.index}].value" value="${field.value}" <c:if test="${not (isAdmin or isOwner)}"> disabled </c:if> >
								<a href="javascript:void(0);" class="link-cf" name="customFieldGroupSet[${group.position}].fields[${status.index}].link">Go to Link</a>
							</c:otherwise>
						</c:choose>
					</li>
					<c:if test="${not (isAdmin or isOwner)}"><small class="notice">* <c:out value="${work.company.name}" /> has provided the following additional information for reference.  You can not edit these values.</small></c:if>
				</c:if>
			</c:forEach>
		</c:forEach>
	</ul>
</div><%--buyer custom fields partial--%>