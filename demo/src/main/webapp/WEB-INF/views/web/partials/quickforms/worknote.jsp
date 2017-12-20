<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<form action='assignments/add_note/${id}' id='create_quickform' class='form-stacked'>

	<input type="hidden" name="id" value="${id}" />
	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
		<c:param name="containerId" value="${quickformMessage}"/>
	</c:import>


<div class="clearfix">
	<div class="input">
		<textarea rows="5" cols="20" name="content" id="note_content" class="span5"></textarea>
	</div>
</div>

<div class="clearfix">
	<div class="input">
		<ul class="inputs-list">
			<li>
				<label>
					<input type="radio" name="is_private" value="0" />
					Everyone on assignment can see
				</label>
			</li>
			<li>
				<label>
					<input type="radio" name="is_private" value="1" />
					Private: Only my company can see
				</label>
			</li>
		</ul>
	</div>
</div>

<div class="wm-action-container">
	<a class="cancel button">Cancel</a>
	<button type="submit" class="button">Save</button>
</div>

</form>