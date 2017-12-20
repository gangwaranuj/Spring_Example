<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/fmt' prefix='fmt'%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

New <c:out value="${type}" /> - <c:out value="${summary}" /><br/>

<c:set var="now" value="<%=new java.util.Date()%>"/>
<fmt:formatDate type="both" value="${now}"/><br/><br/>

From: <c:out value="${currentUser.firstName}" /> <c:out value="${currentUser.lastName}" /> (<c:out value="${currentUser.userNumber}" />), <c:out value="${ not empty currentUser.companyEffectiveName ? currentUser.companyEffectiveName : currentUser.companyName}" /> -
<a href="<c:out value="${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}" />/profile/${currentUser.userNumber}">View profile</a><br/><br/>

<c:out value="${feedback}" />