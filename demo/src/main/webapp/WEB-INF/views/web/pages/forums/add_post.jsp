<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Forums" bodyclass="Forums" breadcrumbSection="Market" breadcrumbSectionURI="/search" breadcrumbPage="Forums" webpackScript="forums">

	<script>
		var config = {
			type: 'post'
		}
	</script>

	<div class="container">

	<div class="forums-header">
		<a href="/forums" id="forumHomeLink">
			<jsp:include page="/WEB-INF/views/web/partials/svg-icons/forums/forums_title.jsp"/>
		</a>
		<h3 class="title">Add Post</h3>
	</div>

	<div class="row">
		<div class="span11">
			<div class="inner-container-topic">
				<sec:authorize access="!principal.isMasquerading()">
				<c:if test="${!isUserBanned}">
					<c:url value="/forums/post" var="submit" />
					<form:form action="${submit}" method="post" commandName="form" name="form" id="forumsPostForm" >

						<wm-csrf:csrfToken />
						<fieldset>
							<div class="forums-post-field">
								<form:label path="title" cssClass="control-label required title">Discussion Title:</form:label>
								<form:label path="categoryId" cssClass="control-label required category">Category: </form:label>

							</div>
							<div class="forums-post-field">
								<form:input id="titleField" path="title" maxlength="70" />
								<form:select path="categoryId">
									<c:forEach var="category" items="${categories}">
										<form:option value="${category.id}" label="${category.categoryName}"></form:option>
									</c:forEach>
								</form:select>
							</div>

							<div class="forums-post-field">
								<form:label path="tags" cssClass="control-label tag">Tags:
									<span class="tooltipped tooltipped-e" aria-label="Use tags to help you map your discussion posts to various topics.">
										<i class="wm-icon-question-filled"></i>
									</span>
								</form:label>
								<form:select id="tagField" path="tags" multiple="multiple" class="chzn-select" items="${tags}">
								</form:select>
							</div>

							<div class="forums-post-field">
								<form:label path="comment" cssClass="control-label required">Discussion:</form:label>
								<form:textarea path="comment" id="commentField" class="input-block-level" rows="5" maxLength="${isInternal ? '' : 1500}" />
							</div>
						</fieldset>
						<button type="submit" class="button" disabled>Create Discussion</button>
						<c:if test="${not isInternal}">
							<span class="post-char-countdown"/>
						</c:if>
					</form:form>
				</c:if>
				</sec:authorize>
			</div>
		</div>
		<c:import url="/WEB-INF/views/web/partials/forums/sidebar.jsp">
			<c:param name="loadButton" value="0" />
		</c:import>
	</div>
</div>

</wm:app>
