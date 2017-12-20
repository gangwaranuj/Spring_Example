<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Feedback" bodyclass="accountSettings">

<div class="row_sidebar_left">
	<div class="sidebar">
		<div class="well-b2">
			<h3>Links</h3>
			<div class="well-content">
				<ul class="stacked-nav">
					<li><a href="https://workmarket.zendesk.com/hc/en-us" target="_blank">Help Center</a></li>
				</ul>
			</div>
		</div>
	</div>


	<div class="content">
		<div class="inner-container">
			<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
				<c:param name="bundle" value="${bundle}"/>
			</c:import>
			<div class="page-header">
				<h3>Feedback</h3>
			</div>
				<div id="contact-us">
					<p>We love to hear from you. Thanks for taking the time to share your very important feedback with us.</p>
					</br>

					<form:form cssClass="form-horizontal form-feedback" action="/feedback" method="POST" modelAttribute="feedback">
						<wm-csrf:csrfToken />
						<div class="alert alert-success dn">Thanks for your feedback. We appreciate your continued participation in our marketplace.</div>
						<fieldset>
							<div><p class="required">I have a:</p>
								<div class="row-fluid">
									<div class="span8">
										<label class="radio inline">
											<form:radiobutton path="type" value="product" checked="checked"></form:radiobutton> <strong>Product Idea: </strong>
											<p>New features or functions we can add to better meet your needs.</p>
										</label>
									</div>
									<div class="span8">
										<label class="radio inline">
											<form:radiobutton path="type" value="platform"></form:radiobutton> <strong>Platform Bug: </strong>
											<p>You've found an error in the Work Market platform.</p>
										</label>
									</div>
								</div>
								<div class="row-fluid">
									<div class="span8">
										<label class="radio inline">
											<form:radiobutton path="type" value="business"></form:radiobutton> <strong>Business Issue: </strong>
											<p>Process challenges you're facing such as payment inquiries or assignment questions.</p>
										</label>
									</div>
									<div class="span8">
										<label class="radio inline">
											<form:radiobutton path="type" value="enhancement"></form:radiobutton><strong>User Experience Enhancement: </strong>
											<p>Feedback on how we can improve the Work Market experience through design changes.</p>
										</label>
									</div>
								</div>
							</div>

							<div class="control-group">
								<label class="required control-label" for="title">Title</label>
								<div class="controls">
									<form:input cssClass="span8" path="title" placeholder="Feedback Topic" maxlength="255" required="true"></form:input>
								</div>
							</div>

							<div class="control-group">
								<label class="required control-label" for="concerns">Work Market<br/>Product Area</label>
								<div class="controls controls-row">
									<div class="input">
										<form:select path="concern" cssClass="span3" id="concerns" multiple="false">
											<form:options items="${concern}" itemValue="code" itemLabel="description"/>
										</form:select>
										<span class="help-inline">The Work Market product area is the portion of the website that is most impacted by your feedback.</span>
									</div>
								</div>
							</div>

							<div class="control-group">
								<label class="control-label" for="priorities">Business Need</label>
								<div class="controls controls-row">
									<div class="input">
										<form:select path="priority" cssClass="span3" id="priorities" multiple="false">
											<form:option value="-1">Select Priority</form:option>
											<form:options items="${priority}" itemValue="code" itemLabel="description"/>
										</form:select>
										<span class="help-inline">Business need is the degree to which this feedback impacts your Work Market experience.</span>
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="required control-label" for="description">Description</label>
								<div class="controls controls-row">
									<div class="input">
										<form:textarea path="description" cssClass="required input-block-level" rows="5" placeholder="Enter detailed description of feedback here." required="true"></form:textarea>
									</div>
								</div>
							</div>
							<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
								<c:param name="containerId" value="attachment_messages" />
							</c:import>
							<div class="control-group">
								<label class="control-label">Add Attachment</label>
								<div class="controls controls-row">
									<div class="input">
										<div id="file-uploader" class="dib"></div>
									</div>
								</div>
								<div id="attachments_list">
									<c:forEach var="attachment" items="${feedback.attachments}" varStatus="status">
										<div id="attachments_inner_${status.count - 1}" class="attachment-list-item clearfix" data-uuid="${attachment.uuid}">
											<i class="icon-file icon-large icon-gray"></i>
											<input type="hidden" name="attachments[${status.count - 1}].uuid" value="${attachment.uuid}">
											<input type="hidden" name="attachments[${status.count - 1}].name" value="<c:out value="${attachment.name}" />">
											<input type="hidden" name="attachments[${status.count - 1}].isUpload" value="1">
											<span>${attachment.name}</span>
											<input type="text" name="attachments[${status.count - 1}].description" value="${attachment.description}" placeholder="Description" class="span3" maxlength="255" />
											<i class="remove wm-icon-trash icon-gray"></i>
										</div>
									</c:forEach>
								</div>
							</div>
							<div class="clearfix">
								<span class="required small"><em>Denotes a required field</em></span>
							</div>
							<button type="submit" class="button">Submit</button>
						</fieldset>
					</form:form>
				</div>
		</div>
	</div>
</div>


<script type="text/x-jquery-tmpl" id="qq-uploader-attachment-tmpl">
	<div class="qq-uploader">
		<div class="qq-upload-drop-area"><span>Drop attachment here to upload</span></div>
		<a href="javascript:void(0);" class="qq-upload-button button">Upload File</a>
		<ul class="qq-upload-list unstyled"></ul>
	</div>
</script>

<script type="text/html" id="attachment-list-item-tmpl">
	<div id="attachments_inner_{{= index }}" class="attachment-list-item clearfix" data-uuid="{{= uuid }}">
		<i class="icon-file icon-large icon-gray"></i>

		<input type="hidden" name="attachments[{{= index }}].uuid" value="{{= uuid }}"/>
		<input type="hidden" name="attachments[{{= index }}].name" value="{{= file_name }}"/>
		<input type="hidden" name="attachments[{{= index }}].isUpload" value="1"/>

		<span>{{= file_name }}</span>

		<input type="text" name="attachments[{{= index }}].description" value="{{= description }}" placeholder="Description" class="span3" maxlength="255" />
		<i class="remove wm-icon-trash icon-gray"></i>
	</div>
</script>

<div id="allowed-extensions" class="dn">
	<c:forEach items="${uploadTypes}" var="type"><c:out value="${type}" />,</c:forEach>
</div>
</wm:app>
