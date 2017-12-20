<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script id="tmpl-resource" type="text/x-jquery-tmpl">
	<div class="fl resource_\${resource.user_number}" style="width: 33%;">
		{{if resource.icons.employee}}
		<span class="tooltipped tooltipped-n label" aria-label="Employee">E</span>
		{{/if}}
		{{if resource.icons.question}}
		<span class="tooltipped tooltipped-n label label-warning" aria-label="Question">Q</span>
		{{/if}}
		{{if resource.icons.offer_open}}
		<span class="label label-success tooltipped tooltipped-n" aria-label="Offer">O</span>
		{{/if}}
		{{if resource.icons.offer_declined}}
		<span class="label label-important tooltipped tooltipped-n" aria-label="Declined">D</span>
		{{/if}}
		{{if resource.icons.viewed_on_mobile || resource.icons.viewed_on_web}}
		<img src="${mediaPrefix}/images/icons/eye-icon-16x16.png" class="tooltipped tooltipped-n" aria-label="Viewed" style="vertical-align: top;"/>
		{{/if}}
		{{if resource.icons.note}}
		<img src="${mediaPrefix}/images/icons/pencil.png" aria-label="Note" class="tooltipped tooltipped-n" style="vertical-align: top;"/>
		{{/if}}

		<c:choose>
			<c:when test="${has_deputy_role}">
				<a href="/profile/\${resource.user_number}" data-usernumber="_\${resource.user_number}" class="resource-link">\${resource.name}</a>
			</c:when>
			<c:otherwise>
				<a href="/profile/\${resource.user_number}" class="profile_link">\${resource.name}</a>
			</c:otherwise>
		</c:choose>

		{{if resource.distance != null}}
		(\${resource.distance} mi)
		{{/if}}
	</div>
</script>
