<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<jsp:useBean id="now" class="java.util.Date" />

<wm:app pagetitle="Account Overview" bodyclass="accountSettings" webpackScript="settings">

<script src="//maps.google.com/maps/api/js?key=AIzaSyAWD12qVRbpnGyNF_fmYMERR0gyvdbHNvE&libraries=places" type="text/javascript"></script>

	<script>
		var config = {
			mode: 'account'
		};
	</script>

	<c:set var="isSuperuser" value="0"/>
	<sec:authorize access="hasRole('ROLE_SUPERUSER')">
		<c:set var="isSuperuser" value="1"/>
	</sec:authorize>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}"/>
	</c:import>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>
		<div class="content">
			<div class="inner-container">
				<div class="page-header">
					<h3><fmt:message key="account.my_company_overview" /></h3>
				</div>
				<form:form modelAttribute="company_form" id="company_form" method="post" class="form-horizontal left">
					<wm-csrf:csrfToken />

					<div class="control-group">
						<label class="control-label"><fmt:message key="account.company_logo" /></label>

						<div class="controls">
							<c:if test="${not empty company_form.avatar}">
								<img src="<c:out value="${wmfmt:stripXSS(company_form.avatar)}" />" alt="Photo" class="avatar_thumbnail"/>
							</c:if>

							<div class="${empty company_form.avatar ? 'dn' : ''}">
								<a href="javascript:void(0);" class="remove-logo"><fmt:message key="account.remove_logo" /></a>
							</div>

							<div id="file-uploader">
								<noscript>
									<input type="file" name="avatar" id="avatar"/>
								</noscript>
							</div>
							<span class="help-block"><i class="icon-info-sign"></i><fmt:message key="account.logo_size_requirements" /></span>
						</div>
					</div>

					<fieldset>
						<div class="control-group">
							<label path="name" class="control-label"><fmt:message key="global.company_name" /></label>
							<div class="controls">
								<form:input path="name" readonly="true" Class="input-xlarge"/>
								<c:if test="${not empty companyNumber}"><span class="help-block">ID: ${companyNumber}</span></c:if>

							</div>
						</div>

					</fieldset>

					<fieldset>
						<div class="control-group">
							<label path="website" Class="control-label"><fmt:message key="global.company_website" /></label>
							<div class="controls">
								<form:input path="website" maxlength="255" size="40" class="input-xlarge" placeholder="e.g. http://www.workmarket.com"/>
							</div>
						</div>

						<div class="control-group">
							<label path="overview" Class="control-label"><fmt:message key="global.company_overview" /></label>
							<div class="controls">
								<form:textarea path="overview" maxlength="1000" rows="10" class="input-block-level" />
							</div>
						</div>

						<br/>

						<div id="address-entry" class="alert alert-info">
							<p><i class="icon-globe icon-2x"></i> <fmt:message key="global.update_location" />:</p>
							<input type="text" size="500"  id="addressTyper" placeholder="e.g. 370 Beech St, Highland Park, IL, 60035" />
							<span class="help-block-signup"><i class="icon-info-sign"></i><fmt:message key="global.accurate_location_message" /></span>
						</div>

						<div id="addressBox">

							<div class="control-group">
								<form:label path="address1" cssClass="control-label"> <fmt:message key="global.address" /></form:label>
								<div class="controls">
									<form:input path="address1" maxlength="100" id="address1" cssClass="span5" />
								</div>
							</div>

							<div class="control-group">
								<form:label path="city" cssClass="control-label"><fmt:message key="global.city_town" /></form:label>
								<div class="controls">
									<form:input path="city" maxlength="255" id="city" cssClass="span5" readonly="true"/>
								</div>
							</div>

							<div class="control-group">
								<form:label cssClass="control-label" path="state"> <fmt:message key="global.state_province" /></form:label>
								<div class="controls">
									<form:input path="state" maxlength="255" id="state" cssClass="span5" readonly="true"/>
								</div>
							</div>

							<div class="control-group">
								<form:label cssClass="control-label" path="postalCode"> <fmt:message key="global.postal_code" /></form:label>
								<div class="controls">
									<form:input path="postalCode" maxlength="11" id="postalCode" cssClass="span5" readonly="true"/>
								</div>
							</div>


							<div class="control-group">
								<form:label path="country" cssClass="control-label"><fmt:message key="global.country" /></form:label>
								<div class="controls">
									<form:input path="country" maxlength="255" id="country" cssClass="span5" readonly="true"/>
								</div>
							</div>

							<form:hidden path="longitude" maxlength="255" id="longitude"/>
							<form:hidden path="latitude" maxlength="255" id="latitude"/>
						</div>

						<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
							<c:param name="containerId" value="photo_upload_messages"/>
						</c:import>

						<div class="control-group">
							<label path="yearfounded" class="control-label"> <fmt:message key="global.year_founded" /></label>
							<div class="controls">
								<form:select path="yearfounded">
									<form:option value="">- <fmt:message key="global.select" /> -</form:option>
									<c:forEach var="year" begin="1900" end="${wmfmt:formatDate('yyyy', now)}">
										<form:option value="${year}" />
									</c:forEach>
								</form:select>
							</div>
						</div>
					</fieldset>

					<div class="vendor-search-status"></div>

					<div class="wm-action-container">
						<button type="submit" class="button" id="submit_action"> <fmt:message key="global.save_changes" /></button>
					</div>
				</form:form>
			</div>
		</div>
	</div>

	<script type="text/x-jquery-tmpl" id="qq-uploader-tmpl">
		<div class="qq-uploader">
			<div class="qq-upload-drop-area"><span><fmt:message key="global.drop_upload_photo" /></span></div>
			<c:set var="uploadLogoText">
				<c:choose>
					<c:when test="${not empty avatar}">
						<fmt:message key="global.upload_new_logo" />
					</c:when>
					<c:otherwise>
						<fmt:message key="global.upload_logo" />
					</c:otherwise>
				</c:choose>
			</c:set>
			<a href="javascript:void(0);" class="qq-upload-button submit small">
				<c:out value="${uploadLogoText}"/></a>
			<ul class="qq-upload-list"></ul>
		</div>
	</script>

</wm:app>
