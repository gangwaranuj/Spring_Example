<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<p>To deactivate this user you must reassign active talent pools, tests, and assignments to a currently active user.</p>

<form:form method="POST" action="/users/reassign" modelAttribute="reassignForm" class="form-horizontal">
	<wm-csrf:csrfToken />
<form:hidden path="currentOwner"/>
	<div class="control-group">
		<label class="control-label required">Talent Pools</label>
		<div class="controls">
			<form:select path="newGroupsOwner" id="group_owner_dropdown" items="${users}">
			<c:forEach items="${users}" var="user">
				<option value="${user.key}"
					<c:out value="${(user.key == reassignForm.currentOwner) ? 'selected=selected' : ''}"/>><c:out value="${user.value}" /></option>
			</c:forEach>
			</form:select>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required">Tests</label>
		<div class="controls">
			<form:select path="newAssessmentsOwner" id="group_owner_dropdown" items="${users}">
				<c:forEach items="${users}" var="user">
					<option value="${user.key}"
						<c:out value="${(user.key == reassignForm.currentOwner) ? 'selected=selected' : ''}"/>><c:out value="${user.value}" /></option>
				</c:forEach>
			</form:select>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label required">Assignments</label>
		<div class="controls">
			<form:select path="newWorkOwner" id="group_owner_dropdown" items="${users}">
			<c:forEach items="${users}" var="user">
				<option value="${user.key}"
					<c:out value="${(user.key == reassignForm.currentOwner) ? 'selected=selected' : ''}"/>><c:out value="${user.value}" /></option>
			</c:forEach>
			</form:select>
			<span class="tooltipped tooltipped-n" aria-label="Only Sent and In Progress assignments will be changed">
				<i class="wm-icon-question-filled"></i>
			</span>
		</div>
		
	</div>

	<div class="wm-action-container">
		<button type="submit" id="btn_action" class="button">Deactivate</button>
	</div>
</form:form>