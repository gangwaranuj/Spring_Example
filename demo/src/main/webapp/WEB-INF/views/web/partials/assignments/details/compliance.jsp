<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:if test="${work.configuration.standardTermsFlag || work.configuration.standardInstructionsFlag}">
	<c:if test="${work.configuration.standardTermsFlag}">
		<h4>Terms of Agreement</h4>

		<div class="scroll-box">
				${wmfmt:tidy(wmfmt:nl2br(work.configuration.standardTerms))}
		</div>
	</c:if>

	<c:if test="${work.configuration.standardInstructionsFlag}">
		<h4>Code of Conduct</h4>

		<div class="scroll-box">
				${wmfmt:tidy(wmfmt:nl2br(work.configuration.standardInstructions))}
		</div>
	</c:if>
</c:if>
