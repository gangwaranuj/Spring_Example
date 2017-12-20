<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<sec:authorize access="hasRole('ROLE_INTERNAL')" var="internal"/>

<c:if test="${(is_admin || is_active_resource || internal)}">
	<form action="/assignments/add_note/${work.workNumber}" class="form-stacked" style="padding-left:0px" method="POST">
		<wm-csrf:csrfToken />
		<input type="hidden" id="${work.workNumber}"/>

		<div class="add_note_container">
			<div class="messages"></div>
			<div class="clearfix">
				<textarea rows="3" cols="50" id="note_content" name="content" class=${isWorkBundle ? "span15" : "span9"}></textarea>
				<div class="row">
					<div class="help-block pull-right">Press <strong>Shift + Enter</strong> to save your message.</div>
					<div class="help-block pull-left span5">
						<c:if test="${is_active_resource && work.status.code == workStatusTypes['PAID']}">
							<c:set var="checkedAndHidden" value="checked='checked' class='dn'" />
						</c:if>
						<label>
							<input type="checkbox" name="is_private" value="1" <c:out value="${checkedAndHidden}"/> />
							<input type="hidden" name="is_privileged" value="${work.isSetActiveResource() && !is_private}" />
							<input type="hidden" name="company_name" value="${work.company.name}" />

							<c:choose>
								<c:when test="${is_active_resource && work.status.code == workStatusTypes['PAID']}">
									<small class="nowrap">
										<i class="wm-icon-lock-circle icon-gray icon-large"></i>
										Only private messages can be added in Paid status
									</small>
								</c:when>
								<c:otherwise>
									<small class="nowrap">
										<i class="wm-icon-lock-circle icon-gray icon-large"></i>
										Only visible to my company
									</small>
								</c:otherwise>
							</c:choose>
						</label>
					</div>
				</div>
			</div>
			<p><button type="submit" class="button -small">Add message</button></p>
		</div>
	</form>
</c:if>

<table class="<c:if test="${empty work.notes}">dn</c:if>">
	<tbody>
	<c:forEach var="note" items="${work.notes}">
		<tr class="note">
			<td>
				<div  class="note_text">
					<span><strong><c:out value="${note.creator.name.firstName}" /> <c:out value="${note.creator.name.lastName}" /></strong></span>
					<small class="meta">
						&mdash; ${wmfmt:formatMillisWithTimeZone("MM/dd/yy h:mmaa z", note.createdOn, work.timeZone)}
						<c:if test="${note.isPrivate}"><i class="icon-lock icon-gray icon-large"></i> &mdash; Viewable by your company only</c:if>
						<c:if test="${note.isPrivileged && is_admin || internal}">&mdash; Viewable by your company and <c:out value="${note.replyToName}" /> </c:if>
						<c:if test="${note.isPrivileged && is_active_resource}">&mdash; Viewable by you and <c:out value="${work.company.name}"/> </c:if>
					</small><br/>
				</div>
				<div>
					<span>${wmfmt:escapeHtmlAndnl2br(note.text)}</span>
					<c:if test="${not empty note.onBehalfOf}">
						<br/>
						(in reference to
						<c:out value="${note.onBehalfOf.name.firstName}"/>
						<c:out value="${note.onBehalfOf.name.lastName}"/>
						<c:if test="${note.onBehalfOf.isWorkMarketEmployee}">
							<span class="label warning tooltip" title="Action by WM employee">WM</span>
						</c:if>)
					</c:if>
				</div>
			</td>
		</tr>
	</c:forEach>
	</tbody>
</table>

<c:if test="${empty work.notes}">
	<p class="empty-notes-message">There are 0 messages.</p>
	<hr/>
</c:if>

<script id="tmpl-note" type="text/x-jquery-tmpl">
	<tr>
		<td>
			<div class="note_text">
				<span style="color:#666"><strong>\${creator}</strong></span>
				<small class="meta"> &mdash; \${created_on}
					{{if is_private}}<i class="wm-icon-lock-circle icon-gray icon-large"></i>&mdash; Viewable by your company only
					{{else is_privileged && !is_resource}} &mdash; Viewable by your company and \${reply_to_name}
					{{else is_privileged}} &mdash; Viewable by you and \${company_name} {{/if}}
				</small>
				<br/>
			</div>
			<span style="color:#666">{{html $.escapeHTMLAndnl2br(content)}}</span>
		</td>
	</tr>
</script>
