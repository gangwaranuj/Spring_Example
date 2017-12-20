<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="page-header">
	<h2 class="assignment-header">
		<c:out value="${work.title}" />
		<small>(ID: <c:out value="${work.workNumber}"/>)</small>
		<c:if test="${work.status.code == workStatusTypes['SENT'] && ((is_admin or is_owner) || is_work_feed)}">
			<a id="linkedin" href="#"
			   data-socialize="linkedIn"
			   data-title="<c:out value="${work.title}" />"
					<c:choose>
						<c:when test="${is_work_feed}">
							data-url="http://www.workmarket.com/work/${work.workNumber}"
						</c:when>
						<c:otherwise>
							data-url="${work.shortUrl}"
						</c:otherwise>
					</c:choose>
			   data-summary="${wmfmt:escapeJavaScript(wmfmt:stripHTML(work.description))}">
				<i class="icon-linkedin-sign"></i>
			</a>
			<a id="facebook" href="#"
			   data-socialize="facebook"
			   data-title="<c:out value="${work.title}" />"
					<c:choose>
						<c:when test="${is_work_feed}">
							data-url="http://www.workmarket.com/work/${work.workNumber}"
						</c:when>
						<c:otherwise>
							data-url="${work.shortUrl}"
						</c:otherwise>
					</c:choose>
			   data-summary="${wmfmt:escapeJavaScript(wmfmt:stripHTML(work.description))}">
				<i class="icon-facebook-sign"></i>
			</a>
			<a id="twitter" href="#"
			data-socialize="twitter"
			data-text="Find work @workmarket <c:out value="${work.title}" />"
			<c:choose>
				<c:when test="${is_work_feed}">
					data-url="http://www.workmarket.com/work/${work.workNumber}"
				</c:when>
				<c:otherwise>
					data-url="<c:out value="${work.shortUrl}"/>"
				</c:otherwise>
			</c:choose>
			<i class="icon-twitter-sign"></i>
			</a>
			<c:choose>
				<c:when test="${is_work_feed}">
					<a id="mailto" href="mailto:?subject=Check out this job on Work Market&body=I thought you'd be interested in this job I found on Work Market.%0D%0D<c:out value="${work.title}"/>%0D%0Dhttp://www.workmarket.com/work/${work.workNumber}">
						<i class="icon-envelope-alt"></i>
					</a>
				</c:when>
				<c:otherwise>
					<a id="mailto" href="mailto:?subject=Check out this job on Work Market&body=I thought you'd be interested in this job I found on Work Market.%0D%0D<c:out value="${work.title}"/>%0D%0D${work.shortUrl}">
						<i class="icon-envelope-alt"></i>
					</a>
				</c:otherwise>
			</c:choose>

		</c:if>
	</h2>

	<c:if test="${not empty headerDisplayFields}">
		<div class="clear">
			<span class="fl mr"><strong>Custom fields:</strong></span>
			<c:forEach items="${headerDisplayFields}" var="field">
				<div class="fl cust-field">
					<c:out value="${field.name}"/>: <c:out value="${field.value}"/>
				</div>
			</c:forEach>
		</div>
	</c:if>

	<c:if test="${is_autotask && (is_admin or is_owner or isInternal)}">
		<c:set var="autotaskZoneNumber" value="${fn:substring(zoneUrl, 19, 20)}" />

		<div>
			<small class="meta fl" style="padding:2px 3px;">
				<a href="https://ww${autotaskZoneNumber}.autotask.net/Autotask/AutotaskExtend/ExecuteCommand.aspx?Code=OpenTicketDetail&TicketID=${autotaskID}" target="_blank">
					View on Autotask (${autotaskID})
				</a>
			</small>
		</div>
	</c:if>

	<div class="clear">
		<p class="assignment_labels">
			<c:forEach items="${work.subStatuses}" var="s">
			<span class="label nowrap" <c:if test="${not empty s.colorRgb}">style="background-color:\#${s.colorRgb};"</c:if>>
				<a href="/assignments#substatus/${s.id}/managing"><c:out value="${s.description}" escapeXml="false" /></a>
				<%--Only an admin or owner is able to remove labels (if they are user resolvable)--%>
				<c:if test="${((is_owner || is_admin) && s.userResolvable)}">
					<a href="/assignments/remove_label/${work.workNumber}?label_id=${s.id}" title="Remove Label" class="remove remove_label_action"><i class="icon-remove"></i></a>
				</c:if>
			</span>
			<span class="separator">/</span>
			</c:forEach>
			<%--Only an admin, owner or active_resource is able to add labels (this excludes resources not assigned to this assignment)--%>
			<c:if test="${not empty available_labels && (is_active_resource or is_admin or is_owner)}">
				<a href="/assignments/add_label/${work.workNumber}" title="Add Label" class="add_label_action nowrap">Add Label</a>
			</c:if>
		</p>
	</div>
</div>
