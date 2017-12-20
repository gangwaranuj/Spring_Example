<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div class="well-b2">
	<div class="well-content">
		<h4>
			Answer a Question
			<span class="label label-success">Action Required</span>
		</h4>
	<form action='/assignments/answer_question/${work.workNumber}' method="POST">
	<wm-csrf:csrfToken />
	<input type="hidden" name='id' value="${work.workNumber}">
	<input type="hidden" name='question_id' value="${qa.id}">

	<p class="question-div" data-id="${qa.questioner}">
		<strong class="questioner-${qa.questioner}"></strong>asked: on
		<small>${wmfmt:formatMillisWithTimeZone("MMM d, YYYY h:mmaa z", qa.questionedOn, work.timeZone)}</small>
	</p>
	<blockquote><em>${wmfmt:escapeHtmlAndnl2br(qa.question)}</em></blockquote>

	<textarea name="answer" class=${isWorkBundle ? "span15" : "span5"} rows="4" placeholder="Please enter your response here"></textarea>

	<div class="alert-actions">
		<button type="submit" class="button">Answer</button>
	</div>
    </form>
</div>
</div>
