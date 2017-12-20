<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="pageScript" value="wm.pages.mobile.assignments.documents" scope="request"/>
<c:set var="pageScriptParams" value="${work.workNumber}" scope="request"/>

<div class="wrap assets-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Assignment Documents"></jsp:param>
	</jsp:include>

	<div class="grid content">
		<div class="unit whole" id="public-message">
			<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
		</div><%--unit whole--%>
		<div class="unit whole">
			<ul class="buyer-assets-container asset-list"></ul>
		</div>
	</div>
</div>

<%-- Underscore templates --%>
<script id="buyer-documents-list-template" type="text/html">
	{{ _.each( assets, function ( asset ) { }}
	<li data-asset-id="{{= asset.uuid }}">
		{{ if ((asset.mimeType).length > 0 && asset.mimeType.toLowerCase().indexOf('image') != -1) { }}
			<a href="/asset/download/{{= asset.uuid }}" style="background-image:url('/asset/download/{{= asset.thumbnailUuid}}');" class="asset-thumb"></a>
		{{ } else { }}
			<a href="/asset/download/{{= asset.uuid }}" class="asset-thumb no-thumb" style="background-image:url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZlcnNpb249IjEuMSIgY2xhc3M9ImF0dGFjaG1lbnQtaWNvbiIgeD0iMHB4IiB5PSIwcHgiIHZpZXdCb3g9IjAgMCA3NS43MDcgNzQuMzYyIiBlbmFibGUtYmFja2dyb3VuZD0ibmV3IDAgMCA3NS43MDcgNzQuMzYyIiB4bWw6c3BhY2U9InByZXNlcnZlIj48cGF0aCBmaWxsPSJsaWdodGdyYXkiIGQ9Ik03My45ODEgMjUuNTA0QzY4LjAyOCAxNS4wNzEgNjQuNzUxIDkuODQ3IDUzLjgxNSA0LjYyIDUyLjAzNyAyMi4zNDYgNTcuNjI1IDI5LjE0NyA3My45ODEgMjUuNTA0eiIvPjxwYXRoIG9wYWNpdHk9IjAuOCIgZmlsbD0ibGlnaHRncmF5IiBkPSJNNTEuNjI2IDMuOTYyYy0xLjI5Mi0wLjE4Mi0yLjYxLTAuMjgzLTMuOTUyLTAuMjgzSDI1LjYyNWMtNi4wMDggMC0xMC44NzkgNC44Ny0xMC44NzkgMTAuODc5djQ4LjkyNmMwIDYuMDA4IDQuODcgMTAuODc5IDEwLjg3OSAxMC44NzloMzkuMjAzYzYuMDA4IDAgMTAuODc5LTQuODcgMTAuODc5LTEwLjg3OVYzMS43MTJjMC0xLjU1Ny0wLjEzMy0zLjA4Mi0wLjM3Ny00LjU3QzU3Ljk1MiAzMS42MzMgNDguNTY5IDIzLjI5OCA1MS42MjYgMy45NjJ6Ii8+PHBhdGggZmlsbD0iIzlCOUI5QiIgZD0iTTE5LjA1MyAyOC42NGMtMC4zMDQtMC4yMi0wLjU4NS0wLjM3My0wLjgwMy0wLjU5IC00LjUxNy00LjUwMy05LjAzMS05LjAxLTEzLjUzMi0xMy41MjkgLTAuNTI4LTAuNTMtMS4wNC0xLjA5NS0xLjQ1NS0xLjcxNEMwLjY3MiA4LjkzOSAyLjI5MyAzLjc0OCA2LjU2MSAyLjM4OWMyLjk4NC0wLjk1IDUuNTYtMC4wNzcgNy43MzYgMi4wOTYgNS4zMDEgNS4yOTYgMTAuNTk4IDEwLjU5NiAxNS44OTYgMTUuODk1IDAuMTQ3IDAuMTQ3IDAuMjkxIDAuMjk2IDAuNDI5IDAuNDUxIDEuNzUyIDEuOTc0IDEuNjc1IDQuOS0wLjE3NiA2Ljc2MSAtMS44NTQgMS44NjQtNC45MjMgMS45ODktNi44MTEgMC4xMzIgLTQuNTMtNC40NTMtOS04Ljk2Ny0xMy40ODgtMTMuNDYyIC0wLjU4My0wLjU4NC0wLjgyNy0xLjMxLTAuNzE3LTIuMTMxIDAuMTQxLTEuMDQ2IDAuNzI0LTEuNzc2IDEuNzEyLTIuMTE4IDEuMDgzLTAuMzc0IDIuMDMxLTAuMDc5IDIuODM3IDAuNzI5IDMuMDk1IDMuMTAxIDYuMTk3IDYuMTk1IDkuMjkgOS4yOTggMC4xOTkgMC4xOTkgMC4zNDIgMC40NTQgMC41MDEgMC42NyAwLjU4Ni0wLjU1OSAxLjA0OC0xIDEuNjE4LTEuNTQ0IC0wLjY4OC0wLjY0My0xLjM0Ni0xLjIyNS0xLjk2OC0xLjg0NCAtMi42MjktMi42MTUtNS4yNTgtNS4yMjktNy44NjMtNy44NjcgLTEuMzUxLTEuMzY5LTIuOTI5LTEuOTQzLTQuODAxLTEuNDM4IC0zLjI0OCAwLjg3Ny00LjQ0NyA0Ljg2NC0yLjI2MSA3LjQyNyAwLjEwMSAwLjExOCAwLjIwNCAwLjIzNSAwLjMxNCAwLjM0NSA0LjQxOCA0LjQyIDguODE5IDguODU1IDEzLjI2MSAxMy4yNSAyLjExOCAyLjA5NSA1LjEzNiAyLjU5NCA3Ljc2IDEuMzk4IDQuNDU3LTIuMDMxIDUuNDczLTcuNzQ5IDEuOTQ2LTExLjMxNkMyNi4zOTggMTMuNjgyIDIwLjk2OSA4LjI5NCAxNS41NTIgMi44OTRjLTAuNDM4LTAuNDM3LTAuOTI3LTAuODM0LTEuNDM1LTEuMTg5QzkuMzU1LTEuNjI1IDIuOTA1IDAuMTA3IDAuNzA0IDUuMjk1Yy0xLjQ3OSAzLjQ4Ny0wLjU5NiA3LjQ1OSAyLjM0NSAxMC40MDggNC42NjggNC42ODIgOS4zNSA5LjM1MSAxNC4wMjcgMTQuMDI0IDAuMTQzIDAuMTQzIDAuMjk1IDAuMjc3IDAuNDI3IDAuNEMxNy45NzIgMjkuNjc3IDE4LjQxNyAyOS4yNSAxOS4wNTMgMjguNjR6Ii8+PHBhdGggZmlsbD0iIzlCOUI5QiIgZD0iTTE5LjA1MyAyOC42NGMtMC42MzYgMC42MS0xLjA4MSAxLjAzOC0xLjU1IDEuNDg4IC0wLjEzMi0wLjEyMy0wLjI4NC0wLjI1Ny0wLjQyNy0wLjQgLTQuNjc3LTQuNjc0LTkuMzU4LTkuMzQyLTE0LjAyNy0xNC4wMjRDMC4xMDggMTIuNzU0LTAuNzc1IDguNzgyIDAuNzA0IDUuMjk1YzIuMi01LjE4OCA4LjY1LTYuOTIgMTMuNDEzLTMuNTkgMC41MDggMC4zNTUgMC45OTYgMC43NTIgMS40MzUgMS4xODkgNS40MTcgNS40IDEwLjg0NiAxMC43ODkgMTYuMjIzIDE2LjIyOCAzLjUyNiAzLjU2NyAyLjUxIDkuMjg1LTEuOTQ2IDExLjMxNiAtMi42MjQgMS4xOTYtNS42NDIgMC42OTctNy43Ni0xLjM5OCAtNC40NDItNC4zOTUtOC44NDQtOC44My0xMy4yNjEtMTMuMjUgLTAuMTEtMC4xMS0wLjIxMy0wLjIyNy0wLjMxNC0wLjM0NSAtMi4xODYtMi41NjQtMC45ODctNi41NSAyLjI2MS03LjQyNyAxLjg3MS0wLjUwNSAzLjQ0OSAwLjA2OSA0LjgwMSAxLjQzOCAyLjYwNSAyLjYzOCA1LjIzNSA1LjI1MiA3Ljg2MyA3Ljg2NyAwLjYyMiAwLjYxOSAxLjI4IDEuMjAxIDEuOTY4IDEuODQ0IC0wLjU3IDAuNTQ0LTEuMDMyIDAuOTg1LTEuNjE4IDEuNTQ0IC0wLjE1OS0wLjIxNi0wLjMwMi0wLjQ3LTAuNTAxLTAuNjcgLTMuMDkzLTMuMTAzLTYuMTk1LTYuMTk4LTkuMjktOS4yOTggLTAuODA2LTAuODA4LTEuNzU0LTEuMTAzLTIuODM3LTAuNzI5IC0wLjk4OCAwLjM0MS0xLjU3MSAxLjA3Mi0xLjcxMiAyLjExOCAtMC4xMSAwLjgyMSAwLjEzNCAxLjU0NyAwLjcxNyAyLjEzMSA0LjQ4OSA0LjQ5NSA4Ljk1OSA5LjAwOSAxMy40ODggMTMuNDYyIDEuODg4IDEuODU2IDQuOTU3IDEuNzMyIDYuODExLTAuMTMyIDEuODUxLTEuODYgMS45MjctNC43ODcgMC4xNzYtNi43NjEgLTAuMTM4LTAuMTU1LTAuMjgyLTAuMzA0LTAuNDI5LTAuNDUxQzI0Ljg5NSAxNS4wODEgMTkuNTk4IDkuNzgxIDE0LjI5NyA0LjQ4NWMtMi4xNzYtMi4xNzMtNC43NTItMy4wNDctNy43MzYtMi4wOTYgLTQuMjY4IDEuMzU5LTUuODg5IDYuNTUtMy4yOTcgMTAuNDE3IDAuNDE1IDAuNjE5IDAuOTI3IDEuMTg0IDEuNDU1IDEuNzE0IDQuNTAxIDQuNTE5IDkuMDE1IDkuMDI2IDEzLjUzMiAxMy41MjlDMTguNDY4IDI4LjI2NiAxOC43NSAyOC40MTkgMTkuMDUzIDI4LjY0eiIvPjwvc3ZnPg==');"></a>
		{{ } }}
		<a href="/asset/download/{{= asset.uuid }}" class="asset-details buyer">
			<p class="asset-name">{{= asset.name }}</p>
			{{ if (asset.description !== null) {  }}<p class="asset-description">{{= asset.description }}</p>{{ } }}
		</a>
	</li>
	{{ });  }}
</script>
