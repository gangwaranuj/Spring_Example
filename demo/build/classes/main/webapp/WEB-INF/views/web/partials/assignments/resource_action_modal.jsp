<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:if test="${not empty param.showHistory}">
	<script id="tmpl-resource_history" type="text/x-jquery-tmpl">
		<table class="zebra-striped">
		{{if notes}}
			{{each notes}}
				<tr>
					<td>
					Date: \${date}<br/>
					Code: \${action_code}<br/>
					Note: \${note}<br/>
					{{if notes.deputy}}
						(Action Taken by \${notes.deputy.first_name} \${notes.deputy.last_name}
						{{if notes.deputy.is_employee}}
							<span class="label warning tooltipped tooltipped-n" aria-label="Action by WM employee">WM</span>
						{{/if}})
					{{/if}}
					</td>
				</tr>
			{{/each}}
		{{/if}}
		</table>
	</script>
</c:if>
