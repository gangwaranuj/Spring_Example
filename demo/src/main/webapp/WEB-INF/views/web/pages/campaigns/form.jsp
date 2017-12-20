<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app
	pagetitle="New Landing Page"
	bodyclass="page-campaign-new accountSettings"
	webpackScript="campaigns"
>
	<script>
		var config = {
			name: 'campaigns',
			type: 'form',
			emptyCompanyAvatar: ${empty companyAvatars},
			emptyCompanyOverview: ${empty company.overview},
			emptyCustomCompanyOverview: ${empty form.customCompanyOverview},
			groupId: ${not empty form.groupId ? form.groupId : 0},
			groupName: ${not empty form.groupName ? form.groupName : "\'\'"}
		}
	</script>

	<c:import url="/WEB-INF/views/web/partials/message.jsp" />
	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
		<c:param name="containerId" value="dynamic_message" />
	</c:import>

	<div class="">

		<div class="" id="create-group">
			<div class="inner-container">
				<!--Landing Page and Invitations Tabs Navigations-->
				<c:import url="/WEB-INF/views/web/partials/recruiting/navigation.jsp"/>

				<form:form cssClass="form-horizontal" modelAttribute="form" method="post" id="recruitingForm">
				<wm-csrf:csrfToken />
				<form:errors path="*" element="div" cssClass="alert alert-error" />

				<div class="page-header clear">
					<c:choose>
						<c:when test="${not empty id}">
							<h3>Edit Landing Page: <c:out value="${form.title}"  /></h3>
						</c:when>
						<c:otherwise>
							<h3 class="fl">Create a Landing Page</h3>
							<div class="fr"><a class="button" href="/campaigns">Back to list</a></div>
						</c:otherwise>
					</c:choose>
				</div>

				<div class="row_wide_sidebar_right">

					<div class="content">
						<div class="clearfix control-group">
							<label class="control-label required">Title</label>
							<div class="controls">
								<form:input path="title" maxlength="255" cssClass="span6" htmlEscape="false" />
							</div>
						</div>
						<div class="clearfix control-group">
							<label class="control-label required">Description</label>
							<div class="controls">
								<form:textarea path="description" cssClass="span6" rows="10" />
							</div>
						</div>
						<div class="clearfix control-group">
							<label class="control-label required">Company Overview</label>
							<div class="controls">
								<form:checkbox path="useCompanyOverview" value="1" />
								<span>Use my current company overview</span>
							</div>
							<div class="controls">
								<form:textarea path="customCompanyOverview" cssClass="span6" rows="10" />
							</div>
						</div>
						<div class="clearfix control-group" style="width:350px;">
							<label class="control-label">Logo</label>
							<div class="controls">
								<label>
									<form:radiobutton path="assetType" value="none" />
									None
								</label>
								<label>
									<form:radiobutton path="assetType" value="company" />
									Use my company logo
									<c:if test="${not empty companyAvatars}">
										<div style="margin-left:15px;">
											<img src="<c:out value="${wmfn:stripUriProtocol(wmfmt:stripXSS(companyAvatars.transformedSmallAsset.uri))}" />" alt="Logo" class="dn" id="current_company_logo"/>
										</div>
									</c:if>
								</label>
								<label>
									<form:radiobutton path="assetType" value="upload" />
									<div id="file-uploader" class="dib"></div>
								</label>
								<div id="upload_preview" class="dn" style="margin-left:15px;">
									<img src="<c:out value="${wmfn:stripUriProtocol(wmfmt:stripXSS(campaign.companyLogo.uri))}" />" height="48" alt="Photo" />
									<form:hidden path="uploadUuid" />
									<a id="remove_upload_preview">Remove</a>
								</div>
							</div>
						</div>

						<fieldset>
							<div class="clearfix control-group">
								<label for="user_groups_autosuggest" class="control-label">Associate a Talent Pool <span class="tooltipped tooltipped-n" aria-label="This field uses type-ahead to list your existing talent pools. Start typing a talent pool name and then select from the choices."><i class="wm-icon-question-filled"></i></span></label>

								<div class="controls clear">
									<form:select path="groupId" id="user_groups_autosuggest" class="wm-select"></form:select>
									<br/>
								</div>
								<div class="controls">
									<small class="meta">
										<a id="add-group-outlet" title="Create Private Group">Create a Private Talent Pool</a>
									</small>
								</div>
							</div>
						</fieldset>

						<vr:rope>
							<vr:venue name="PRIVATE_NETWORK">
								<div class="clearfix control-group">
									<label class="control-label">Private Landing Page?</label>
									<div class="controls">
										<form:checkbox path="privateCampaign" id="privateCampaign" value="true"/>
										<span>Professionals who join Work Market via this landing page will only be visible to your company</span>
									</div>
								</div>
							</vr:venue>
						</vr:rope>

					</div>

					<div class="sidebar">
						<div class="well-b2">
							<h3>Title and Description</h3>
							<div class="well-content">Provide a clear title and description to help people understand the kind of worker you are looking for. Add in specific skills and requirements you might require to work with your company.</div>
						</div>

						<div class="well-b2">
							<h3>Associating a Talent Pool</h3>
							<div class="well-content">Quickly identify qualified candidates by attaching a talent pool to your landing page. If the talent pool is public, candidates will be immediately invited to your talent pool and presented with the talent pool requirements, including tests and other talent pool parameters. If you associate a private talent pool, all recruits will be tracked immediately into your private talent pool.</div>
						</div>

						<div class="well-b2">
							<h3>General Info</h3>
							<div class="well-content">Once you create your landing page, you will have a custom trackable link to your landing page. The landing page is hosted at Work Market, but customized with your company logo (if a logo is uploaded) and overview.</div>
						</div>
					</div>

				</div>

					<div class="wm-action-container">
						<button type="submit" class="button">${empty id ? 'Create' : 'Save'} Landing Page</button>
					</div>
				</form:form>
			
			</div>
		</div>

	</div>

	<c:import url="/WEB-INF/views/web/partials/filemanager/add_to_filemanager.jsp" />

	<script type="text/x-jquery-tmpl" id="qq-uploader-tmpl">
		<div class="qq-uploader">
			<div class="qq-upload-drop-area"><span>Drop logo here to upload</span></div>
			<a href="javascript:void(0);" class="qq-upload-button button">Upload new logo</a>
			<ul class="qq-upload-list"></ul>
		</div>
	</script>

	<script type="text/x-jquery-tmpl" id="qq-uploader-contacts-tmpl">
		<div class="qq-uploader">
			<div class="qq-upload-drop-area"><span>Drop logo here to upload</span></div>
			<a href="javascript:void(0);" class="qq-upload-button button">Upload contacts CSV file</a>
			<ul class="qq-upload-list"></ul>
		</div>
	</script>

	<div style="display: none;" id="company_overview">${company.overview}</div>

	<div id="image-allowed-extensions" class="dn">
		<c:forEach items="${imageUploadTypes}" var="type"><c:out value="${type}" />,</c:forEach>
	</div>

	<div id="csv-allowed-extensions" class="dn">
		<c:forEach items="${csvUploadTypes}" var="type"><c:out value="${type}" />,</c:forEach>
	</div>

</wm:app>
