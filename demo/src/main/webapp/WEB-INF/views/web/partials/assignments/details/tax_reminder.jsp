<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<div class="alert">
	<div class="media-body" style="text-align: center;">
		<c:choose>
			<c:when test="${needUSATaxEntity && hasRejectedTaxEntity}">
				<a href="/account/tax" class="alert-message-btn">Edit your Tax Information</a><br/>
				<small>Your Tax Identification Number was not matched with the Internal Revenue Service (I.R.S.).
					You will not be able to withdraw your earnings until your tax information is verified with the
					I.R.S. which can take up to 2 to 3 business days.
				</small>
			</c:when>
			<c:when test="${needUSATaxEntity && !hasRejectedTaxEntity}">
				Your Tax Information is currently pending verification with the Internal Revenue Service (I.R.S.).
			</c:when>
			<c:otherwise>
				<a href="/account/tax" class="alert-message-btn">Add your Tax Information</a><br/>
				<c:choose>
					<c:when test="${needUSATaxEntity && !hasTaxEntity}">
						<small>
							You will not be able to withdraw your earnings until your tax information is verified
							with the I.R.S. Please note that the tax verification process can take up to 2 to 3 business
							days.
						</small>
					</c:when>
					<c:when test="${!needUSATaxEntity && !hasTaxEntity}">
						<small>You will not be able to withdraw your earnings until your tax information is verified.</small>
					</c:when>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</div>
</div>
