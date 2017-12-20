<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="well">
	<h5>Company Actions</h5>
	<ul>
		<c:choose>
			<c:when test="${not empty requestScope.company.suspended and requestScope.company.suspended eq true}">
				<li><a href="/admin/manage/company/unsuspend/${requestScope.company.id}" onclick="return confirm('Are you sure you want to unsuspend this company?');">Unsuspend account</a></li>
			</c:when>
			<c:otherwise>
				<li><a id="suspend" href="/admin/manage/company/suspend/${requestScope.company.id}">Suspend account</a></li>
			</c:otherwise>
		</c:choose>
	</ul>
</div>

<script>
	$('#suspend').click(function() {
		$.colorbox({
			href: $(this).attr('href'),
			title: $(this).text(),
			innerWidth: 600,
			innerHeight: 200,
			onComplete: function() {
				$('#company-suspend-form .submit').click(function() {
					$.get($(this).attr('href'), function(data) {
						if (data.successful) {
							$('div .message').hide();
							table.fnDraw();
							$.colorbox.close();
						} else {
							var list = $('<ul>');
							$.each(data.messages, function(i, item) {
								$('<li>' + item + '</li>').appendTo(list);
							});
							$('#concern_resolve_messages div').replaceWith(list);
							$('#concern_resolve_messages').removeClass('success').addClass('error').show();
							$('#concern-resolve-form .submit').removeClass('disabled');
							$.colorbox.resize();
						}
					});
				})
			}
		});
		return false;
	});
</script>