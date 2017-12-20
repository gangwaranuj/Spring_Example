<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:choose>
	<c:when test="${not empty work.questionAnswerPairs}">
		<c:forEach var="qa" items="${work.questionAnswerPairs}">
			<c:if test="${(qa.questioner == currentUser.userNumber) || is_admin || isInternal}">
				<div class="question">
					<c:choose>
						<c:when test="${qa.questioner == currentUser.userNumber}">
							<strong>You asked:</strong>
						</c:when>
						<c:otherwise>
							<strong class="questioner-${qa.questioner}"></strong> asked:
						</c:otherwise>
					</c:choose>
					<p>
						${wmfmt:escapeHtmlAndnl2br(qa.question)}
						<small class="meta">&mdash; ${wmfmt:formatMillisWithTimeZone("MMM d, YYYY h:mmaa z", qa.questionedOn, work.timeZone)}</small>
					</p>
				</div>

				<c:if test="${not empty qa.answer}">
					<div class="answer">
						<em>
							<c:choose>
								<c:when test="${qa.questioner == currentUser.userNumber}">
									<strong><c:out value="${work.company.name}" /> answered:</strong>
								</c:when>
								<c:otherwise>
									<strong class="answerer-${qa.answerer}"></strong> answered:
								</c:otherwise>
							</c:choose>
						</em>
						<p>
							${wmfmt:escapeHtmlAndnl2br(qa.answer)}
							<small class="meta">&mdash; ${wmfmt:formatMillisWithTimeZone("MMM d, YYYY h:mmaa z", qa.answeredOn, work.timeZone)}</small>
						</p>
					</div>
				</c:if>
			</c:if>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<p>There are 0 questions.</p>
	</c:otherwise>
</c:choose>

<c:if test="${is_resource}">
	<a href="/assignments/ask_question/${work.workNumber}" class="ask_question_action button">Ask Question</a>
</c:if>
