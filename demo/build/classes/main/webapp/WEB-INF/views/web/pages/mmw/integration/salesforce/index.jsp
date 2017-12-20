<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Webhooks" bodyclass="accountSettings" breadcrumbSection="Work" breadcrumbSectionURI="/settings" breadcrumbPage="Webhooks" webpackScript="settings">

	<script>
		var config = {
			mode: 'integrations',
			webHookClientId: '${wmfmt:escapeJavaScript(webhookSettingsId)}'
		};
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:set var="selected_navigation_link" value="/mmw/integrations" scope="request"/>
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content webhooks-page">
			<div class="inner-container">
				<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
					<c:param name="bundle" value="${bundle}"/>
				</c:import>

				<div class="page-header">
					<h2>Salesforce Webhooks <span class="label label-warning beta-tag">BETA</span></h2>
					<div class="fr">
						<small class="meta"><a href="/mmw/integration">Back to Integrations</a></small>
					</div>
				</div>

				<div>
					<p>Work Market webhooks provide a simple way to communicate between Work Market and remote systems that employ REST-ful APIs.</p>
					<form:form class="webhooks-config" action="/mmw/integration/salesforce" method="POST" modelAttribute="salesforceIntegrationSettingsDTO">
						<wm-csrf:csrfToken />
						<h4>Authentication Scheme</h4>
						<c:choose>
							<c:when test="${!isSalesforceAuthenticated}">
								<a href="https://login.salesforce.com/services/oauth2/authorize?response_type=code&client_id=${salesforceClientId}&redirect_uri=${salesforceCallbackUrl}" class="button">Authorize Work Market with Salesforce</a>
								<br /><br />
								<a href="https://test.salesforce.com/services/oauth2/authorize?response_type=code&client_id=${salesforceClientId}&redirect_uri=${salesforceCallbackUrl}&state=sandbox" class="button">Authorize Work Market with Salesforce (Sandbox)</a>
							</c:when>
							<c:otherwise>
								<a href="/mmw/integration/salesforce_forget_authentication/${webhookSettingsId}" class="button">Forget Salesforce Authentication</a>
							</c:otherwise>
						</c:choose>

						<h4>Date/Time Format</h4>
						<em><small class="muted">Different systems use different date/time formats. Date/time values will be transformed into the format you specify below.</small></em><br />
						<br />
						<form:select path="dateFormat">
							<c:forEach var="dateFormat" items="${dateFormats}">
								<form:option value="${dateFormat}"><c:out value="${dateFormat.title}" /></form:option>
							</c:forEach>
						</form:select>

						<h4>Suppress Events Triggered by API</h4>
						<form:checkbox class="fl suppress-api-events" id="suppressApiEvents" path="suppressApiEvents"/>
						<label for="suppressApiEvents">Don't send webhooks for events triggered by calls to Work Market's API</label>
						<br />
						<div class="wm-action-container">
							<button type="submit" class="button">Save Settings</button>
						</div>
					</form:form>

					<hr/>
					<c:choose>
						<c:when test="${isSalesforceAuthenticated}">
							<div id="web_hooks_container">
								<input type="hidden" name="webHookClientId" value="${webhookSettingsId}" />
								<div class="web-hook-add navbar-form pull-right">
									<a class="btn fr" id="web_hook_add">Add</a>
									<select id="web_hook_event_type" class="span3 fr">
										<c:forEach var="event" items="${events}">
											<option value="${event.code}"><c:out value="${event.title}" /></option>
										</c:forEach>
									</select>
									<label class="fr" for="web_hook_event_type">Add hook for:</label>
								</div>
								<h4 class="clearfix">Webhooks</h4>
								<div id="web_hook_events"></div>
							</div>
						</c:when>
						<c:otherwise>
							<p>You must authenticate with Salesforce to create webhooks.</p>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</div>

	<script id="web_hook_event_template" type="text/html">
		<div class="well web-hook-event" rel="\${integrationEventTypeCode}">
			<div class="web-hook-event-name">\${eventTypeName}</div>
			<div class="web-hook-variables">
				<strong><a class="web-hook-toggle-variables">Show variables</a></strong><br />
				<div class="web-hook-variable-list dn">
					<em><small class="muted">Variables will be parsed in the URL, headers, and body. Null variables will be replaced with an empty string.</small></em><br /><br />
					<strong>General variables</strong>
					<ul>
						<c:forEach var="field" items="${generalFieldsMap}">
							<li><code>&#36;{<c:out value="${field}" />}</code></li>
						</c:forEach>
					</ul>
					<c:forEach var="fieldList" items="${eventFieldsMap}">
						{{if integrationEventTypeCode == '${fieldList.key}'}}
						<strong>Event variables</strong><br/>
						<ul>
							<c:forEach var="field" items="${fieldList.value}">
								<li><code>&#36;{<c:out value="${field}" />}</code></li>
							</c:forEach>
						</ul>
						{{/if}}
					</c:forEach>
					<strong>Custom fields</strong><br />
					<c:forEach var="customFieldsGroup" items="${customFieldsGroups}">
						<em><c:out value="${customFieldsGroup.key}" /></em><br />
						<ul>
							<c:forEach var="customField" items="${customFieldsGroup.value}">
								<li><c:out value="${customField.value}" /></li>
								<li><code>&#36;{<c:out value="${customField.key}" />}</code></li>
							</c:forEach>
						</ul>
					</c:forEach>
				</div>
				<br />
			</div>
			<div class="web-hooks">

			</div>
		</div>
	</script>

	<script id="web_hook_template" type="text/html">
		<div class="web-hook">
			<form class="form form-stacked" action="/mmw/integration/save_web_hook" method="POST">
				<input type="hidden" name="webHookClientId" value="${webhookSettingsId}" />
				<input type="hidden" name="integrationEventTypeCode" value="\${integrationEventTypeCode}" />
				<input type="hidden" name="id" value="\${id}" />
				<input type="hidden" name="" value="\${callOrder}" />
				<div class="row">
					<div class="span7">
						<strong>Base URL</strong> <em><small class="muted">Required</small></em>
					</div>
					<div class="span3">
						<strong>Method</strong></em>
						<a class="icon-sort web-hook-sort fr"></a>
						<a class="icon-delete web-hook-delete fr"></a>
					</div>
				</div>
				<div class="row">
					<div class="span7">
						<input name="url" type="url" class="span7" value="{{if url}}\${$.escapeHTML(url)}{{/if}}" />
					</div>
					<div class="span3">
						<select class="span2 web-hook-method-type" name="methodType">
							<c:forEach var="method" items="${methods}">
								<option value="${method}"><c:out value="${method}" /></option>
							</c:forEach>
						</select>
					</div>
				</div>
				<div class="row">
					<div class="span10">
						<strong>Headers</strong> <a class="button -small web-hook-add-header">Add</a>
					</div>
				</div>
				<div class="row">
					<div class="span4">
						<input type="text" class="span4" value="Content-type" disabled />
					</div>
					<div class="span5">
						<select class="span5 web-hook-content-type" name="contentType">
							<c:forEach var="contentType" items="${contentTypes}">
								<option value="${contentType}"><c:out value="${contentType.value}" /></option>
							</c:forEach>
						</select>
					</div>
					<div class="span1">
						<em><small class="muted">Required</small></em>
					</div>
				</div>
				<div class="web-hook-headers">
				</div>
				<div class="row">
					<div class="span10">
						<strong>Body</strong>
					</div>
				</div>
				<div class="row">
					<div class="span10">
						<textarea name="body" rows="5" class="span10">{{if body}}\${$.escapeHTML(body)}{{/if}}</textarea>
					</div>
				</div>
				<div class="actions">
					{{if id}}
					<a class="web-hook-save button">Save</a>
					<a class="web-hook-disable button {{if !enabled}}dn{{/if}}">Disable</a>
					<a class="web-hook-enable button {{if enabled}}dn{{/if}}">Enable</a>
					{{else}}
					<a class="web-hook-save button">Create</a>
					{{/if}}
				</div>
			</form>
		</div>
	</script>

	<script id="web_hook_header_template" type="text/html">
		<div class="row">
			<input rel="webHookHeader.id" type="hidden" value="\${id}" />
			<div class="span4">
				<input rel="webHookHeader.name" type="text" class="span4" value="{{if name}}\${$.escapeHTML(name)}{{/if}}" />
			</div>
			<div class="span5">
				<input rel="webHookHeader.value" type="text" class="span5" value="{{if value}}\${$.escapeHTML(value)}{{/if}}" />
			</div>
			<div class="span1">
				<a class="icon-delete web-hook-delete-header"></a>
			</div>
		</div>
	</script>

	<script id="web_hook_alert_template" type="text/html">
		<div class="alert \${type}">
			<button type="button" class="close" data-dismiss="alert">&times;</button>
			<ul>
				{{each messages}}
				<li>\${$value}</li>
				{{/each}}
			</ul>
		</div>
	</script>

</wm:app>
