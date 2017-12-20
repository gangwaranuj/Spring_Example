<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app
	pagetitle="Send an Invitation"
	bodyclass="invitations"
	webpackScript="invitations"
>
	<sec:authorize var="shouldShowMDL" access="hasFeature('fea--mdl') and hasFeature('fea--mdl-invitations')" />
	<script>
		var data = context.data;
		data["mode"] = "send";
		data["type"] = "form";
		data["companyName"] = "${wmfmt:escapeJavaScript(currentUser.companyName)}";
		data["isEmptyCompanyAvatar"] = ${empty companyAvatars};
		data["isEmptyCompanyOverview"] = ${empty company.overview};
		data["shouldShowMDL"] = ${shouldShowMDL};
		context.data = data;
	</script>

	<div class="inner-container">
		<!--Landing Page and Invitations Tabs Navigations-->
		<c:import url="/WEB-INF/views/web/partials/recruiting/navigation.jsp"/>
		<form:form modelAttribute="form" method="post" id="sendForm" action= "/invitations/send" cssClass="form-horizontal">
			<wm-csrf:csrfToken />
			<input type="hidden" name="message" value="" id="message">

			<c:import url="/WEB-INF/views/web/partials/message.jsp"/>
			<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
				<c:param name="containerId" value="dynamic_message"/>
			</c:import>

			<div class="page-header clear">
				<h3 class="fl">Send an Invitation</h3>
				<div class="fr"><a class="button" href="/invitations">Back to list</a></div>
			</div>

			<div class="row_wide_sidebar_right">
				<div class="content">
					<ul class="inputs-list">
						<li>
							<label>
								<input id="show-individual" type="radio" name='bulk' value="false" checked="checked"/>
								Send to an individual
							</label>
						</li>
						<li>
							<label>
								<input id="show-bulk" type="radio" name='bulk' value="true" />
								Send to a list of contacts
							</label>
						</li>
					</ul>
					<div id="send-individual" class="active">
						<p>Send an invitation to invite a contractor to work with you on Work Market.</p>
						<fieldset>
							<div class="clearfix control-group">
								<label for="first_name" class="required control-label">First Name</label>
								<div class="input controls">
									<form:input type="text" id="first_name" path="first_name" required="true" value="" maxlength="255" class="span6"/>
								</div>
							</div>
							<div class="clearfix control-group">
								<label for="last_name" class="required control-label">Last Name</label>
								<div class="input controls">
									<form:input type="text" id="last_name" path="last_name" required="true" value="" maxlength="255" class="span6"/>
								</div>
							</div>
							<div class="clearfix control-group">
								<label for="email" class="required control-label">Email</label>
								<div class="input controls">
									<form:input type="text" id="email" path="email" value="${email}" required="true" maxlength="255" class="span6"/>
								</div>
							</div>
						</fieldset>
					</div>
					<div id="send-bulk" class="control-group" style="display: none;">
						<p>Send an invitation to invite your contacts to work with you on Work Market.</p>
						<label class="control-label">Upload your contacts <span class="help-block"><a href="http://help.workmarket.com/customer/portal/articles/1038935-csv-file-for-uploading-contacts-and-recruiting-campaigns">How to create CSV</a></span></label>
						<div class="controls">
							<div id="contact-uploader" class="input">
								<a href="javascript:void(0);" class="button">Upload contacts CSV file</a>
								<ul class="qq-upload-list"></ul>
							</div>
							<form:hidden path="uploadCsv" />
						</div>
					</div>

					<label>&nbsp;</label>
					<fieldset>
						<div class="control-group">
							<label class="control-label" for="recruitingCampaigns">Associate a Recruiting Landing Page</label>
							<div class="controls">
								<form:select path="recruitingCampaignId" id="recruitingCampaigns">
									<form:option value="">- Select -</form:option>
									<form:options items="${recruitingCampaigns}" />
								</form:select>
								<span><a href="/campaigns/new" >Create a Landing Page</a></span>
							</div>
						</div>
					</fieldset>
					<label>&nbsp;</label>
					<div class="text"><strong><a id="show-options">View Advanced Options &#9660;</a></strong></div>

					<div id="options" class="dn">
						<hr/>
						<div class="clearfix control-group">
							<label for="custom_message" class='strong control-label'>Custom Message</label>

							<div class="input controls">
								<ul class="inputs-list">
									<li>
										<label>
											<input type="checkbox" name='toggle_customize_invitation' id='toggle_customize_invitation'/>
											Add a personal note
										</label>
									</li>
								</ul>
							</div>
						</div>

						<div id="customize_invitation" class="dn">
							<div class="clearfix control-group">
								<div class="input controls">
									<textarea name="custom_message" rows="5" cols="50" id="custom_message" maxlength="500" class="span7">${sendForm.custom_message}</textarea>
								</div>
							</div>
						</div>

						<fieldset>
							<div class="clearfix control-group">
								<label for="user_groups_autosuggest" class="control-label">Invite to Talent Pool <span class="tooltipped tooltipped-n" aria-label="This field uses type-ahead to list your existing talent pools. Start typing a talent pool name and then select from the choices."><i class="wm-icon-question-filled"></i></span></label>

								<div class="controls clear">
									<select id="user_groups_autosuggest" class="wm-select"></select>
									<br/>
								</div>
								<div class="controls">
									<small class="meta">
										<a id="add-group-outlet" title="Create Private Group">Create a Private Talent Pool</a>
									</small>
								</div>
							</div>
						</fieldset>

						<fieldset>
							<div class="clearfix control-group">
								<label class="control-label">Company Logo</label>

								<div class="input controls">
									<ul class="inputs-list">
										<li>
											<label>
												<input type="radio" name='logo' value="none"
													   <c:if test="${empty sendForm.logo || sendForm.logo == '' }">checked="checked"</c:if> />
												None
											</label>
										</li>
										<li>
											<label>
												<input type="radio" name='logo' value="company" <c:if test="${sendForm.logo == 'company'}">checked="checked"</c:if> />
												Use my company logo
												<c:if test="${not empty companyAvatars}">
													<div style="margin-left:15px;">
														<img src="<c:out value="${wmfn:stripUriProtocol(wmfmt:stripXSS(companyAvatars.transformedSmallAsset.uri))}" />"
															 alt="Logo" class="dn" id="current_company_logo"/>
													</div>
												</c:if>
											</label>
										</li>
										<li>
											<label>
												<input type="radio" name='logo' value="upload" <c:if test="${sendForm.logo == 'upload' }">checked="checked"</c:if> />
												<div id="file-uploader" class="dib">
														<%-- <noscript><input type="file" name="company_logo_add"/></noscript> --%>
												</div>
											</label>

											<div id="onetime_logo_preview" class="dn" style="margin-left:15px;">
												<img src="" height="48" width="48" alt="Photo"/>
												<input type="hidden" name="logo_uuid" id="logo_uuid"/>
												<a id="remove_upload_logo_preview">Remove</a>
											</div>
										</li>
									</ul>
								</div>
							</div>

							<vr:rope>
								<vr:venue name="PRIVATE_NETWORK">
									<div class="clearfix control-group">
										<label class="control-label">Private Invitation?</label>
										<div class="controls">
											<input type="checkbox" id="private_invitation" name="private_invitation" />
											<span>If this professional joins Work Market via your invitation, then they will only be visible to your company.</span>
										</div>
									</div>
								</vr:venue>
							</vr:rope>

							<div class="clearfix control-group">
								<label for="company_overview_add" class="control-label">Company Overview</label>

								<div class="input controls">
									<ul class="inputs-list">
										<li>
											<textarea name="company_overview_add" id="company_overview_add" rows="5" cols="50" class="span7"><c:out value="${sendForm.company_overview_add}" /></textarea>
										</li>
										<li>
											<label>
												<input type="checkbox" id="company_overview" name="company_overview" <c:if test="${sendForm.company_overview}">checked="checked"</c:if> />
												Use my company overview
											</label>
										</li>
									</ul>
								</div>
							</div>
						</fieldset>
					</div>
				</div>

				<div class="sidebar">
					<div class="well-b2">
						<h3>Sending Invitations</h3>
						<div class="well-content">
							<p>Emails are sent immediately from "Work Market via <c:out value="${currentUser.firstName}"/> <c:out value="${currentUser.lastName}"/>" with the email address hi@myworkmarket.com</p>
							<p>By sending email invitations, you represent and warrant that you have the right to contact these email addresses. Please review the <a href="/tos" target="_blank">Terms of Use Agreement</a> for more details.</p>
						</div>
					</div>
				</div>
			</div>

			<div class="wm-action-container">
				<button type="submit" class="button" <c:if test="${not isInviteAllowed}">disabled="true"</c:if>>Send Invitation</button>
				<button type="button" class="button preview-action">Preview</button>
			</div>

		</form:form>

		<script type="text/x-jquery-tmpl" id="qq-uploader-tmpl">
			<div class="qq-uploader">
				<div class="qq-upload-drop-area"><span>Drop logo here to upload</span></div>
				<a href="javascript:void(0);" class="qq-upload-button">Upload new logo</a>
				<ul class="qq-upload-list"></ul>
			</div>
		</script>

		<div class="dn">
			<div id="preview-container">
				<div id="preview" class="previewtxt"></div>
			</div>

			<div id="popup_upload_photo">
				<form action="action/logoupload" id='form_upload_photo' enctype='multipart/form-data'>

					<p>You currently have no logo on file with Work Market. Please upload your company logo to use on this
						invitation and store for later. Your logo should be <strong>between 700kb and 2MB</strong> and <strong>at
							least 100px x 100px</strong>.</p>

					<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp"/>

					<div id="file-uploader-companylogo" class="actions form-stacked"></div>

				</form>
			</div>

			<div id="popup_overview">
				<form action="/account/updateoverview" id='form_overview' class='form-stacked' method="post">
					<wm-csrf:csrfToken />

					<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp"/>

					<p>You currently have no company overview on file with Work Market. Please provide your company overview to
						use on this invitation and store for later.</p>

					<div class="clearfix">
						<label for="overview_pop">Overview</label>

						<div class="input">
							<textarea name="overview" rows="10" cols="50" id="overview_pop" class="span7"><c:out value="${company.overview}" /></textarea>
						</div>
					</div>

					<button type="submit" class="button">Save</button>

				</form>
			</div>
		</div>

		<div style="display:none;" id="current_company_overview">${company.overview}</div>

		<script id="preview_template" type="text/x-jquery-tmpl">
			<c:out value="${previewTemplate}" escapeXml="false"/>
		</script>
	</div>

	<script type="text/x-jquery-tmpl" id="qq-uploader-contacts-tmpl">
		<div class="qq-uploader">
			<div class="qq-upload-drop-area"><span>Drop logo here to upload</span></div>
			<a href="javascript:void(0);" class="qq-upload-button button">Upload contacts CSV file</a>
			<ul class="qq-upload-list"></ul>
		</div>
	</script>

</wm:app>
