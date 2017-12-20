<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${not empty bundle}">
	<c:if test="${not empty bundle.success}">
		<div class="alert-message success" data-alert="alert">
			<ul>
				<c:forEach items="${bundle.success}" var="message" varStatus="status">
					<c:choose>
						<c:when test="${status.first}">
							<li><c:out value="${message}" escapeXml="false" /></li>
						</c:when>
						<c:otherwise>
							<li><c:out value="${message}" escapeXml="false"/></li>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</ul>
		</div>
	</c:if>

	<c:if test="${not empty bundle.errors}">
		<div class="alert-message error" data-alert="alert">
			<ul class="unstyled">
				<c:forEach items="${bundle.errors}" var="message" varStatus="status">
					<c:choose>
						<c:when test="${status.first}">
							<li><c:out value="${message}" escapeXml="false" /></li>
						</c:when>
						<c:otherwise>
							<li><c:out value="${message}" escapeXml="false"/></li>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</ul>
		</div>
	</c:if>

	<c:if test="${not empty bundle.notices}">
		<div class="alert-message notice" data-alert="alert">
			<ul class="unstyled">
				<c:forEach items="${bundle.notices}" var="message" varStatus="status">
					<c:choose>
						<c:when test="${status.first}">
							<li><c:out value="${message}" escapeXml="false" /></li>
						</c:when>
						<c:otherwise>
							<li><c:out value="${message}" escapeXml="false"/></li>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</ul>
		</div>
	</c:if>

	<c:if test="${not empty bundle.warnings}">
		<div class="alert-message warning">
			<ul class="unstyled">
				<c:forEach items="${bundle.warnings}" var="message" varStatus="status">
					<c:choose>
						<c:when test="${status.first}">
							<li><c:out value="${message}" escapeXml="false" /></li>
						</c:when>
						<c:otherwise>
							<li><c:out value="${message}" escapeXml="false"/></li>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</ul>
		</div>
	</c:if>
</c:if>

<script id="error-notices-template" type="text/template">
	{{ _.each( messages, function ( message ) { }}
	<div class="alert-message warning">
		<ul class="unstyled">
			<li>{{= message }}</li>
		</ul>
	</div>
	{{ });  }}
</script>

<script id="success-notices-template" type="text/template">
	{{ _.each( messages, function ( message ) { }}
	<div class="alert-message success">
		<ul class="unstyled">
			<li>{{= message }}</li>
		</ul>
	</div>
	{{ });  }}
</script>