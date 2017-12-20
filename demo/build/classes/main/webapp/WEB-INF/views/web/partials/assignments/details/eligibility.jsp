<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<h5>
	Assignment Eligibility Requirements<br />
	<small>
		<a href="#" onclick="window.location.reload();return false;">refresh</a> to see your current eligibility
	</small>
</h5>

<div class="well-content">
	<ul class="media-list">
		<c:forEach items="${eligibility.criteria}" var="criterion">
			<li class="media">
				<i class="pull-left icon-large icon-${criterion.met ? 'ok text-success' : 'remove text-error'}"></i>
				<div class="media-body">
					<c:if test="${not criterion.met and criterion.url != null}">
						<a href="<c:out value="${criterion.url}" />"
							 class="button pull-right"
							 data-title="Eligibility Requirement: <c:out value='${criterion.typeName}' />"
							 target="_blank">Go <i class="icon-double-angle-right"></i></a>
					</c:if>
					<strong><c:out value="${criterion.typeName}" /></strong>
					<div><c:out value="${wmfmt:capitalize(criterion.name)}" /></div>
				</div>
			</li>
		</c:forEach>
	</ul>
</div>

