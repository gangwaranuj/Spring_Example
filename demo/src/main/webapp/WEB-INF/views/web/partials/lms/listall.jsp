<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="count" value="0" scope="page"/>
<c:forEach var="entry" items="${responseItems}">
	<c:set var="item" value="${entry.value}"/>
	<c:set var="count" value="${count + 1}" scope="page"/>

	<div class="row">
		<div class="span7">
			<div class="question-container form-stacked">
				<div class="question-number">${count}</div>
				<div class="page-header question_prompt"><p><c:out value="${item.prompt}"/></p></div>
				<c:if test="${not empty item.description}">
					<p><c:out value="${item.description}"/></p>
				</c:if>
				<c:choose>
					<c:when test="${item.type.value == AssessmentItemType.SINGLE_LINE_TEXT}">
						<div class="text-answer"><c:out value="${gradingForm.responses[item.id].answer}"/></div>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType.MULTIPLE_LINE_TEXT}">
						<div class="text-answer"><c:out value="${gradingForm.responses[item.id].answer}"/></div>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType.SINGLE_CHOICE_RADIO}">
						<div class="choice-answer">
							<c:forEach var="choice" items="${item.choices}">
								<c:set var="selected" value="${gradingForm.responses[item.id].answer == choice.id}"/>
								<div>
									<label>
										<input type="radio" name="answer[${item.id}]" value="<c:out value="${choice.id}"/>" <c:if test="${selected}">checked="checked"</c:if> disabled="disabled"/>
										<span><c:out value="${choice.value}"/></span></label></div>
							</c:forEach>
							<c:if test="${item.otherAllowed}">
								<c:set var="checked" value="${fn:length(gradingForm.responses[item.id].other) > 0}"/>
								<label>
									<input type="radio" name="answer[${item.id}]" value="other" <c:if test="${checked}">checked="checked"</c:if>disabled="disabled"/><span> Other:</span>
								</label>
								<div><input type="text" name="other" value="<c:out value="${gradingForm.responses[item.id].other}"/>" readonly="readonly"/></div>
							</c:if>
						</div>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType.MULTIPLE_CHOICE}">
						<div class="choice-answer">
							<p class="xsmall gray brs">(Select all that apply)</p>
							<c:forEach var="choice" items="${item.choices}">
								<c:set var="choiceId">${choice.id}</c:set>
								<c:set var="checked" value="${fn:contains(gradingForm.responses[item.id].answers, choiceId)}"/>
								<div><label>
									<input type="checkbox" name="answer[${item.id}]" value="<c:out value="${choice.id}"/>" <c:if test="${checked}">checked="checked" style="font-weight: normal"</c:if> disabled="disabled"/>
									<span><c:out value="${choice.value}"/></span></label></div>
							</c:forEach>
							<c:if test="${item.otherAllowed}">
								<c:set var="checked" value="${fn:length(gradingForm.responses[item.id].other) > 0}" />
								<label><input type="checkbox" name="answer[${item.id}]" value="other" <c:if test="${checked}">checked="checked"</c:if> disabled="disabled"/> Other:</label>
								<div><input type="text" name="other" value="<c:out value="${gradingForm.responses[item.id].other}"/>" readonly="readonly"/></div>
							</c:if>
						</div>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType.SINGLE_CHOICE_LIST}">
						<div class="choice-answer">
							<select name="answer" disabled="disabled">
								<option value=""></option>
								<c:forEach var="choice" items="${item.choices}">
									<c:set var="selected" value="${gradingForm.responses[item.id].answer == choice.id}"/>
									<option value="<c:out value="${choice.id}"/>" <c:if test="${selected}">selected="selected"</c:if>><c:out value="${choice.value}"/></option>
								</c:forEach>
								<c:if test="${item.otherAllowed}">
									<option value="other" <c:if test="${gradingForm.responses[item.id].answer == 'other'}">selected="selected"</c:if>>Other:
									</option>
								</c:if>
							</select>
							<c:if test="${item.otherAllowed}">
								<div><c:out value="${gradingForm.responses[item.id].other}"/>"</div>
							</c:if>
						</div>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType.DIVIDER}">
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType.DATE}">
						<div class="text-answer"><c:out value="${gradingForm.responses[item.id].answer}"/></div>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType.PHONE}">
						<div class="text-answer"><c:out value="${gradingForm.responses[item.id].answer}"/></div>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType.EMAIL}">
						<div class="text-answer"><c:out value="${gradingForm.responses[item.id].answer}"/></div>
					</c:when>
					<c:when test="${item.type.value == AssessmentItemType.NUMERIC}">
						<div class="text-answer"><c:out value="${gradingForm.responses[item.id].answer}"/></div>
					</c:when>
				</c:choose>

				<c:if test="${not empty gradingForm.responses[item.id].assets}">
					<ul id="attachment-list" class="unstyled">
						<c:forEach var="asset" items="${gradingForm.responses[item.id].assets}">
							<li>
								<a href="/asset/download/${asset.uuid}"><c:out value="${asset.name}"/></a><br/>
								<c:out value="${asset.description}"/>
							</li>
						</c:forEach>
					</ul>
				</c:if>

				<c:if test="${not empty item.incorrectFeedback}">
					<h6>Answer Feedback:</h6>

					<p><c:out value="${item.incorrectFeedback}"/></p>
				</c:if>

				<c:if test="${not empty item.assets}">
					<h6>Attachments:</h6>
					<ul class="unstyled">
						<c:forEach var="asset" items="${item.assets}">
							<li><a href="/asset/${asset.uuid}" class="wordwrap" target="_blank"><c:out
									value="${asset.name}"/></a></li>
						</c:forEach>
					</ul>
				</c:if>
			</div>
		</div>

		<c:if test="${gradingResponse.assessment.type.value == AssessmentType.GRADED}">
			<c:choose>
				<c:when test="${isGrader and item.graded and item.manuallyGraded}">
					<div class="span3">
						<div class="well-b2">
							<form action="/lms/manage/grade_response/${gradingResponse.assessment.id}/${gradingResponse.requestedAttempt.id}"
							      class="score-item" method="post">
								<wm-csrf:csrfToken />
								<input type="hidden" name="itemId" value="${item.id}"/>

								<h3>Grading</h3>

								<div class="well-content">
									<p class="action <c:if test="${gradingForm.responses[item.id].graded}">dn</c:if>">
										<a href="javascript:void(0);" class="cta-grade-response-correct button">Correct</a>
										<a href="javascript:void(0);" class="cta-grade-response-incorrect button">Incorrect</a>
									</p>
								</div>

								<div class="result <c:if test="${not gradingForm.responses[item.id].graded}">dn</c:if>">
									<div class="well-content">
										<span class="score">
											<c:out value="${gradingForm.responses[item.id].correct ? 'Correct' : 'Incorrect'}"/>
										</span>
										<c:if test="${gradingResponse.requestedAttempt.status.code != 'graded'}">
											<a href="javascript:void(0);" class="cta-edit-response">(Edit)</a>
										</c:if>
									</div>
								</div>
							</form>
						</div>
					</div>
				</c:when>
				<c:when test="${(item.graded && showQuestionResults) || isGrader}">
					<div class="span3">
						<div class="result">
							<div class="well-b2">
								<h3>Score</h3>

								<div class="well-content">
									<span class="score">
										<c:out value="${gradingForm.responses[item.id].correct ? 'Correct' : 'Incorrect'}"/>
									</span>
								</div>
							</div>
						</div>
					</div>
				</c:when>
			</c:choose>
		</c:if>
	</div>
</c:forEach>
