<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form action="/assignments/delegate/${work.workNumber}" id='form_delegate_assignment' class='form-stacked'>
	<input type="hidden" name="id" value="${work.workNumber}"/>

	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp"/>

	<div class="clearfix">
		<label name='user' class='required'>Select new worker to delegate the assignment to</label>

		<div class="input">
			<select name="user" id='delegate-selected'>
				<c:forEach items="${users}" var="user">
					<c:if test="${user.id != currentUser.id}">
						<option value="${user.id}" selected="selected"><c:out value="${user.fullName}" /></option>
					</c:if>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="wm-action-container">
		<c:choose>
			<c:when test="${not empty users}">
				<button type="submit" class="button">Delegate Assignment</button>
			</c:when>
			<c:otherwise>
				<button type="submit" class="button" disabled="true">Delegate Assignment</button>
			</c:otherwise>
		</c:choose>
	</div>
</form>
