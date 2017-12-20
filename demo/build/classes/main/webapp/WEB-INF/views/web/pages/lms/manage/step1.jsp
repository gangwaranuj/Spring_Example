<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Edit ${assessment.name}" bodyclass="lms" webpackScript="lms">

	<script>
		var config = {
			mode: 'manageDetails',
			assessmentSetId: ${assessment.setId},
			assessmentItemsLength: ${fn:length(assessment.items)}
		};
	</script>

	<c:choose>
		<c:when test="${assessment.type.value == AssessmentType.SURVEY}">
			<c:choose>
				<c:when test="${assessment.setId}">
					<c:set var="title" value="Edit Survey" />
				</c:when>
				<c:otherwise>
					<c:set var="title" value="New Survey" />
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${assessment.setId}">
					<c:set var="title" value="Edit Test" />
				</c:when>
				<c:otherwise>
					<c:set var="title" value="New Test" />
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>

	<div class="inner-container">
		<div class="page-header">
			<h2><c:out value="${title}" /></h2>
		</div>

		<div class="row_wide_sidebar_right">
			<div class="content" id="lms-manage-details-ui">
				<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
					<c:param name="containerId" value="dynamic_messages" />
				</c:import>

				<form:form action="/lms/manage/step1" modelAttribute="assessment" id="manage-details-form" class="form-stacked">
					<c:if test="${assessment.setId}">
						<form:hidden path="id" />
					</c:if>
					<form:hidden path="type" />
					<form:hidden path="status" />

					<div class="clearfix">
						<label for="test_name" class="required">Name <span class="tooltipped tooltipped-n" aria-label="Provide a descriptive name for your test. The name is displayed in emails and list views for test managers and takers.">
						<i class="wm-icon-question-filled"></i>
					</span></label>
						<div class="input">
							<form:input path="name" id="test_name" cssClass="span9" maxlength="255" />
						</div>
					</div>
					<div class="clearfix">
						<label for="test_description">Description
						<span class="tooltipped tooltipped-n" aria-label="Give your test a description that helps test takers understand the general content in your test and what to expect for questions.">
							<i class="wm-icon-question-filled"></i>
						</span>
						</label>
						<div class="input">
							<form:textarea data-richtext="wysiwyg" path="description" id="test_description" cssClass="span9" rows="5" />
						</div>
					</div>
					<div class="clearfix">
						<label for="test_industry" class="required">Industry
						<span class="tooltipped tooltipped-n" aria-label="The industry selected will suggest your test to users with same industry identified in their profile who are searching for tests in Learning Center.">
							<i class="wm-icon-question-filled"></i>
						</span>
						</label>
						<div class="input">
							<form:select path="industry" items="${industries}" id="test_industry" cssClass="span6" />
						</div>
					</div>



					<hr/>
					<p><strong><a id="show-options">View Advanced Options &#9660;</a></strong></p>

					<div id="options" class="dn">
						<c:if test="${assessment.type.value == AssessmentType.GRADED}">
							<div id="assessment-options">
								<div class="clearfix">
									<label for="test_passingscore">Passing Score</label>
									<div class="input">
										<div class="inline-inputs">
											<form:input path="configuration.passingScore" id="test_passingscore" cssClass="span1" maxlength="3" />%
											&nbsp;
											<form:checkbox path="configuration.passingScoreShared" value="1" />
											Show passing score to test takers
										</div>
									</div>
								</div>
								<div class="clearfix">
									<label>Options</label>
									<div class="input">
										<ul class="inputs-list">
											<li>
												<label class="dib">
													<input type="checkbox" name="durationMinutes" <c:if test="${assessment.configuration.durationMinutes > 0}">checked="checked"</c:if> />
													This test <em>should</em> take
												</label>
												<form:input path="approximateDurationMinutes" id="test_timerequired" cssClass="span1" maxlength="3" />
												minutes but <strong>must</strong> be completed in
												<form:input path="configuration.durationMinutes" cssClass="span1" maxlength="5" />
												minutes
												<span class="help-block"><span class="label label-warning">Note :</span> if a maximum time is specified, users who exceed this limit will automatically fail.</span>
											</li>
											<li>
												<label class="dib">
													<input type="checkbox" name="retakesAllowed" <c:if test="${assessment.configuration.retakesAllowed > 0}">checked="checked"</c:if> />
													Allow workers to retake this test up to
												</label>
												<form:input path="configuration.retakesAllowed" cssClass="span1" maxlength="3" />
												times
											</li>
											<li>
												<label>
													<form:checkbox path="configuration.resultsSharedWithPassers" value="1" />
													Allow those who passed to compare their answers with correct answers
												</label>
											</li>
											<li>
												<label>
													<form:checkbox path="configuration.resultsSharedWithFailers" value="1" />
													Allow those who failed to compare their answers with correct answers
												</label>
											</li>
										</ul>
									</div>
								</div>
							</div>
						</c:if>
						<div class="clearfix">
							<label>Visibility</label>
							<div class="input">
								<ul class="inputs-list">
									<li>
										<label>
											<form:checkbox path="configuration.featured" value="1" />
											<span>Feature this item in a list of available <c:out value="${(assessment.type.value == AssessmentType.SURVEY) ? 'surveys' : 'tests'}"/></span>
										</label>
										<span class="help-block">Your <c:out value="${(assessment.type.value == AssessmentType.SURVEY) ? 'survey' : 'test'}"/> may be promoted on the Work Market home page or other publicly visible areas of the platform.</span>
									</li>
								</ul>
							</div>
						</div>
						<div class="clearfix">
							<label>Emails</label>
							<div class="input">
								<ul class="inputs-list">
									<li><label>Send me emails when:</label></li>
									<li>
										<label>
											<input type="checkbox" name="configuration.notifications[0].type" value="${NotificationType.NEW_ATTEMPT_BY_INVITEE}" <c:if test="${not empty notificationTypeLookup[NotificationType.NEW_ATTEMPT_BY_INVITEE]}">checked="checked"</c:if> />
											An invited worker completes the <c:out value="${(assessment.type.value == AssessmentType.SURVEY) ? 'survey' : 'test'}"/>
										</label>
									</li>
									<li>
										<label>
											<input type="checkbox" name="configuration.notifications[1].type" value="${NotificationType.NEW_ATTEMPT}" <c:if test="${not empty notificationTypeLookup[NotificationType.NEW_ATTEMPT]}">checked="checked"</c:if> />
											Anyone completes the <c:out value="${(assessment.type.value == AssessmentType.SURVEY) ? 'survey' : 'test'}"/>
										</label>
									</li>
									<c:if test="${assessment.type.value != AssessmentType.SURVEY}">
										<li>
											<label class="dib">
												<input type="checkbox" name="configuration.notifications[2].type" value="${NotificationType.ATTEMPT_UNGRADED}" <c:if test="${not empty notificationTypeLookup[NotificationType.ATTEMPT_UNGRADED]}">checked="checked"</c:if> />
												There are ungraded questions for more than
											</label>
											<input type="text" name="configuration.notifications[2].days" <c:if test="${not empty notificationTypeLookup[NotificationType.ATTEMPT_UNGRADED]}">value="${notificationTypeLookup[NotificationType.ATTEMPT_UNGRADED].days}"</c:if> class="span1" maxlength="2" />
											days
										</li>
									</c:if>
								</ul>
							</div>
						</div>
						<div class="clearfix">
							<label>Email Recipients</label>
							<div class="input">
								<form:select path="configuration.notificationRecipients" items="${users}" id="notification_list" multiple="multiple" size="1" />
								<span class="help-block">As the owner, you will always receive the notifications selected above. Add additional users from your company who will receive the same notifications.</span>
							</div>
						</div>
					</div>
					<div class="wm-action-container">
						<c:choose>
							<c:when test="${assessment.setId}">
								<c:choose>
									<c:when test="${empty assessment.items}">
										<button type="button" id="cta-save-form" class="button">Save and Add Questions</button>
									</c:when>
									<c:otherwise>
										<button type="button" id="cta-save-form" class="button">Save</button>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${assessment.type.value == AssessmentType.SURVEY}">
										<button type="button" id="cta-save-form" class="button">Create Survey</button>
									</c:when>
									<c:otherwise>
										<button type="button" id="cta-save-form" class="button">Create Test</button>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
					</div>
				</form:form>
			</div>

			<div class="sidebar">
				<div class="well-b2">
					<c:choose>
						<c:when test="${assessment.type.value == AssessmentType.SURVEY}">
							<h3><i class="icon-info-sign info-blue"></i> About Surveys</h3>
							<div class="well-content">

								<h4>What are Surveys?</h4>
								<p>Surveys are a series of questions that can be attached to an assignment to capture key feedback such as closeout photos and information, perform onsite surveys, or ask a series of questions to gather data.</p>

								<h4>Options</h4>
								<p>As the owner, you can set options to control various aspects of your survey - including how long they have to complete it, how many questions, and what information a worker can see after completing the survey.</p>

								<h4>Eligibility and Visibility</h4>
								<p>Surveys can be invitation-only or available to talent pools. Invitation-only is the most restrictive setting while "Anyone Using Work Market" is the most open, posting your survey to all Work Market users.</p>

								<h4>Email Notifications</h4>
								<p>To more efficiently manage your survey process you can configure Work Market notifications to provide you updates when surveys are completed.</p>

							</div>
						</c:when>
						<c:otherwise>
							<h3><i class="icon-info-sign info-blue"></i> About Tests</h3>
							<div class="well-content">

								<h4>What are Tests?</h4>
								<p>Tests are a series of questions designed to qualify workers, train them on specific topics, or act as a qualifying tool for a talent pool.</p>

								<h4>Options</h4>
								<p>As the owner, you can set options to control various aspects of your test - including how long they have to complete it, how many questions, and what information a worker can see after completing the test.</p>

								<h4>Eligibility and Visibility</h4>
								<p>Tests can be invitation-only or available to talent pools. Invitation-only is the most restrictive setting while "Anyone Using Work Market" is the most open, posting your survey to all Work Market users.</p>

								<h4>Email Notifications</h4>
								<p>To more efficiently manage your testing process you can configure Work Market notifications to provide you updates when tests are completed.</p>

							</div>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>

	</div>

</wm:app>
