<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:choose>
	<c:when test="${work.pricing.id == PricingStrategyType.PER_HOUR || work.pricing.id == PricingStrategyType.BLENDED_PER_HOUR}">
		<li class="hours-units-container">
			<div id="hours-units-label">Hours Worked:</div>
			<div class="work-done-inputs">
				<label for="hours">
					<input type="text" name="hours" id="hours" value="${hours > 0 ? hours : ''}" />
					hr
				</label>
				<label for="minutes">
					<input type="text" name="minutes" id="minutes" value="${minutes > 0 ? minutes : ''}" />
					min
				</label>
			</div>
		</li>
	</c:when>
	<c:when test="${work.pricing.id == PricingStrategyType.PER_UNIT}">
		<li class="hours-units-container">
			<div>Units:</div>
			<div class="work-done-inputs">
				<label for="units">
					<input type="text" name="units" id="units" value="${work.activeResource.unitsProcessed >  0 ? work.activeResource.unitsProcessed : ''}" />
					Units
				</label>
			</div>
		</li>
	</c:when>
</c:choose>