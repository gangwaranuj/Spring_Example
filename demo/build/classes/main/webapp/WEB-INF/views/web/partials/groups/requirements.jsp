<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<div class="inner-container">
	<ul class="requirements">
		<c:forEach items="${validation.criteria}" var="crit">
			<c:choose>
				<c:when test="${is_group_admin}">
					<li>
						<p>${crit.typeName}: <strong>${crit.name}</strong></p>
					</li>
				</c:when>
				<c:otherwise>
					<c:set var="criterion" value="${crit}" scope="request"/>
					<c:set var="met_requirement" value="${wmfn:toYesNo(criterion.met)}" scope="request"/>
					<c:set var="company" value="${group.company}" scope="request"/>
					<c:if test="${not is_dispatcher}">
						<c:if test="${met_requirement eq param.validation_type && not empty criterion.typeName}">
							<c:import url="/WEB-INF/views/web/partials/groups/${criterion.typeClassName}.jsp"/>
						</c:if>
					</c:if>
					<c:if test="${is_dispatcher}">
						<c:if test="${not empty criterion.typeName}">
							<style>
								ul.requirements a.button {
									display: none;
								}
								ul.requirements li p {
									color: #646b6f;
								}
								ul.requirements li i {
									display: none;
								}
							</style>
							<c:import url="/WEB-INF/views/web/partials/groups/${criterion.typeClassName}.jsp"/>
						</c:if>
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</ul>
</div>
