<%@ page import="com.workmarket.service.business.dto.UserLicenseDTO" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pending" value="<%= UserLicenseDTO.VERIFICATION_STATUS.PENDING.ordinal() %>"/>
<c:set var="pending_information" value="<%= UserLicenseDTO.VERIFICATION_STATUS.PENDING_INFORMATION.ordinal() %>"/>
<c:set var="on_hold" value="<%= UserLicenseDTO.VERIFICATION_STATUS.ON_HOLD.ordinal() %>"/>

<select id="${(not empty param.id) ? param.id : 'verification_status'}" name="${(not empty param.name) ? param.name : 'verification_status'}">
<option value=""><fmt:message key="global.all" /></option>
<option value="<c:out value="${pending}"/>" ${(not empty verification_status && verification_status == pending) ? 'selected' : '' } ><fmt:message key="verification_statuses.pending" /></option>
<option value="<c:out value="${pending_information}"/>" ${(not empty verification_status && verification_status == pending_information) ? 'selected' : '' } ><fmt:message key="verification_statuses.pending_information" /></option>
<option value="<c:out value="${on_hold}"/>" ${(not empty verification_status && verification_status == on_hold) ? 'selected' : '' } ><fmt:message key="verification_statuses.on_hold" /></option>
</select>
