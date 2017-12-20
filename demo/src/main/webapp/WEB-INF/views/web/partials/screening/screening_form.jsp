<%@ page import="java.util.Calendar" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form:form modelAttribute="screeningForm" action="${screeningFormUri}" method="post" id="screeningForm" class="form-horizontal screeningDetailsForm">
<wm-csrf:csrfToken />
<form:hidden path="addressTypeCode" value="profile" />

<div class="messages"></div>

<div class="control-group">
		<label class="control-label">First Name</label>
		<div class="controls">
			<form:input path="firstName" maxlength="50" value="${screeningForm.firstName}" />
		</div>
</div>

<div class="control-group">
	<label class="control-label">Middle Name</label>
	<div class="controls">
		<form:input path="middleName" maxlength="50" />
	</div>
</div>

<div class="control-group">
	<label class="control-label">Last Name</label>
	<div class="controls">
		<form:input path="lastName" mathlength="50" value="${screeningForm.lastName}" />
	</div>
</div>

<div class="control-group">
	<label class="control-label">Maiden Name</label>
	<div class="controls">
		<form:input path="maidenName" maxlength="50" />
	</div>
</div>

<div class="control-group">
	<label class="required control-label">Date of Birth</label>
	<div class="controls">
		<div class="inline-inputs">
			<form:select path="birthMonth" cssClass="span2">
				<form:option value="">- Month -</form:option>
				<c:forEach begin="1" end="12" var="i">
					<form:option value="${i}">${i}</form:option>
				</c:forEach>
			</form:select>
			<form:select path="birthDay" cssClass="span2">
				<form:option value="">- Day -</form:option>
				<c:forEach begin="1" end="31" var="i">
					<form:option value="${i}">${i}</form:option>
				</c:forEach>
			</form:select>
			<form:select path="birthYear" cssClass="span2">
				<form:option value="">- Year -</form:option>
				<c:forEach begin="1912" end="<%= Calendar.getInstance().get(Calendar.YEAR) - 18 %>" var="i">
					<form:option value="${i}">${i}</form:option>
				</c:forEach>
			</form:select>
		</div>
	</div>
</div>

<c:if test="${intlMessage}">
	<div class="control-group">
		<label class="required control-label">Government ID Number</label>
		<div class="controls">
			<form:input path="workIdentificationNumber" maxlength="25" autocomplete="off" />
		</div>
	</div>
</c:if>

<c:if test="${!intlMessage}">
	<div class="control-group">
		<label id="ssn-label" class="required control-label" >SSN</label>
		<div class="controls">
			<form:input path="workIdentificationNumber" maxlength="11" autocomplete="off" />
		</div>
	</div>
</c:if>

<div class="control-group">
	<label class="required control-label">Address</label>
	<div class="controls">
		<form:input path="address1" size="30" maxlength="255" />
	</div>
</div>

<div class="control-group">
	<label class="control-label">Address 2</label>
	<div class="controls">
		<form:input path="address2" size="30" maxlength="255" />
	</div>
</div>

<div class="control-group">
	<label class="required control-label">City</label>
	<div class="controls">
		<form:input path="city" maxlength="255" />
	</div>
</div>

<c:if test="${intlMessage}">
	<div class="control-group">
		<label class="control-label">State/Province</label>
		<div class="controls">
			<form:input path="state" size="30" />
		</div>
	</div>
</c:if>

<c:if test="${!intlMessage}">
	<div class="control-group">
		<label class="required control-label">State/Province</label>
		<div class="controls">
			<form:select path="state">
				<form:option value="">- Select -</form:option>
				<form:options items="${states}" />
			</form:select>
		</div>
	</div>
</c:if>


<div class="control-group">
	<label class="control-label">Postal Code</label>
	<div class="controls">
		<form:input path="postalCode" maxlength="7" />
	</div>
</div>



<div class="control-group">
	<label class="required control-label">Country</label>
	<div class="controls">
		<form:select path="country">
			<form:option value="">- Select -</form:option>
			<form:option value="USA">United States</form:option>
			<form:option value="CAN">Canada</form:option>
		</form:select>
	</div>
</div>

<div class="control-group">
	<label class="required control-label">Email</label>
	<div class="controls">
		<form:input path="email" maxlength="255" />
	</div>
</div>

<c:out value="${formBody}" escapeXml="false" />

</form:form>
