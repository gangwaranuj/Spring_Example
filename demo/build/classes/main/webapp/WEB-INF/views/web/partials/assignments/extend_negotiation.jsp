<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp"/>

<form action='/assignments/extend_negotiation/${workNumber}' method='POST' class='form-stacked'>
	<wm-csrf:csrfToken />
	<input type="hidden" name='id' value="${negotiationEncryptedId}">

	<div class="clearfix">
		<label name='extendBy'>Extend by</label>

		<div class="input">
			<input name="time" class="span2"/>

			<select name="unit" class="span2">
				<c:forEach items="${negotiationExtensionUnits}" var="unit">
					<option value="${unit.key}"><c:out value="${unit.value}" /></option>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Extend</button>
	</div>

</form>
