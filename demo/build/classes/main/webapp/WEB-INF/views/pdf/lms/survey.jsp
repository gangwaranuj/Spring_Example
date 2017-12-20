<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<style type="text/css">
		html, body { background-color: #ffffff; margin: 0; padding: 0; }

		body { margin: 0; font-family: "Helvetica Neue", Helvetica, Arial, sans-serif; font-size: 12px; font-weight: normal; line-height: 18px; color: #000; }

		input[type=checkbox] {margin-right: 6px; padding-top: 2px;}

		.input-text {border: 1px solid #999; height: 175px; width:500px; position:relative; }
		.input-text-one-line {border: 1px solid #999; height: 50px; width:500px; position:relative; }

		.question-container { width: 75%; padding: 8px 10px 12px 45px; background-color: #fff; border: 1px solid #666; border-radius: 5px; box-shadow: 0 0 4px #cccccc; margin-bottom: 15px; position: relative; }

		@media print {
			.question-container { page-break-inside: avoid }
		}
		.question-container .question-number { width: 25px; text-align: center; padding: 7px 0; background-color: #fe8707; top: -3px; left: 10px; color: #fff; position: absolute; font-size: 18px; border-radius: 2px 2px 6px 6px; }

		.question-container label { text-align: left; }

		.question p {margin-bottom: 20px; font-size:10px;}

		.question small { color: #333; }

		.page-header { margin-bottom: 17px; border-bottom: 1px solid #ddd; -webkit-box-shadow: 0 1px 0 rgba(255, 255, 255, 0.5); -moz-box-shadow: 0 1px 0 rgba(255, 255, 255, 0.5); box-shadow: 0 1px 0 rgba(255, 255, 255, 0.5); }

		.page-header h1 { margin-bottom: 8px; }

	</style>
</head>
<body class="lms">
<h1><c:out value="${assessment.name}"/></h1>
<hr/>
<div class="content">
	<c:if test="${not empty work}">
		<p>This survey is to be completed as part of your assignment, "<c:out value="${work.work.title}"/>".
			When you finish the survey, you will be automatically taken
			back to the assignment to complete your notes and payment request.</p>
	</c:if>
</div>


<c:forEach items="${assessment.items}" var="item">
		<div class="question-container">
			<div class="question-number"><c:out value="${item.position + 1}"/></div>
			<div class="page-header">
				<p class="question_prompt"><strong><c:out value="${item.prompt}"/></strong></p>
			</div>
			<c:if test="${not empty item.description}">
				<p class="question_description"><c:out value="${item.description}"/></p>
			</c:if>
			<div class="question">
				<c:choose>
					<c:when test="${item.type.value == AssessmentItemType['SINGLE_LINE_TEXT']}">

						<div class="input-text"><br/>&nbsp;</div>

						<c:if test="${not empty item.hint}">
							<small><c:out value="${item.hint}"/></small>
						</c:if>

					</c:when>
					<c:when test="${item.type.value == AssessmentItemType['MULTIPLE_LINE_TEXT']}">
						<div class="input-text"><br/>&nbsp;</div>
						<c:if test="${not empty item.hint}">
							<small><c:out value="${item.hint}"/></small>
						</c:if>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType['SINGLE_CHOICE_RADIO']}">
						<div>
							<p>(Select <strong>one answer only</strong>)</p>
							<c:forEach items="${item.choices}" var="choice">
								<div><label><input type="checkbox"/><c:out value="${choice.value}"/></label></div>
						</c:forEach>
						<c:if test="${item.otherAllowed}">
							<div><label><input type="checkbox"/> Other:</label></div>
							<div class="input-text-one-line"><br/>&nbsp;</div>
						</c:if>
						</div>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType['MULTIPLE_CHOICE']}">
						<div>
							<p>(Select all that apply)</p>
							<c:forEach items="${item.choices}" var="choice">
							<div><label><input type="checkbox"/><c:out value="${choice.value}"/></label></div>
						</c:forEach>
						<c:if test="${item.otherAllowed}">
							<div>
								<label><input type="checkbox"/> Other:</label>
								<div class="input-text-one-line"><br/>&nbsp;</div>
							</div>
						</c:if>
						</div>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType['SINGLE_CHOICE_LIST']}">
						<div>
							<p>(Select <strong>one answer only</strong>)</p>
							<c:forEach items="${item.choices}" var="choice">
							<div><label><input type="checkbox"/><c:out value="${choice.value}"/></label></div>
						</c:forEach>
						<c:if test="${item.otherAllowed}">
							<div><label><input type="checkbox"/> Other:</label></div>
							<div class="input-text-one-line"><br/>&nbsp;</div>
						</c:if>
						</div>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType['DIVIDER']}">
						<hr/>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType['DATE']}">
						<div class="input-text-one-line"><br/>&nbsp;</div>
						<c:if test="${not empty item.hint}">
							<small><c:out value="${item.hint}"/></small>
						</c:if>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType['PHONE']}">
						<div class="input-text-one-line"><br/>&nbsp;</div>
						<c:if test="${not empty item.hint}">
							<small><c:out value="${item.hint}"/></small>
						</c:if>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType['EMAIL']}">
						<div class="input-text-one-line"><br/>&nbsp;</div>
						<c:if test="${not empty item.hint}">
							<small><c:out value="${item.hint}"/></small>
						</c:if>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType['NUMERIC']}">
						<div class="input-text-one-line"><br/>&nbsp;</div>
						<c:if test="${not empty item.hint}">
							<small><c:out value="${item.hint}"/></small>
						</c:if>
					</c:when>
				</c:choose>
			</div>
	</div>
</c:forEach>

</body>
</html>