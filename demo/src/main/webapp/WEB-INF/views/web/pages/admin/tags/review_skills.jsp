<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Skills" webpackScript="admin">

	<script>
		var config = {
			mode: 'tags',
			tagType: 'skill'
		};
	</script>

	<div class="row-fluid" id="tags">
		<div class="span3 admin">
			<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
		</div>

		<div class="span13">
			<ul class="nav nav-tabs">
				<li <c:if test="${current_type == 'tools'}">class="active"</c:if>>
					<a href="/admin/tags/review_tools">Tools</a>
				</li>
				<li <c:if test="${current_type == 'skills'}">class="active"</c:if>>
					<a href="/admin/tags/review_skills">Skills</a>
				</li>
				<li <c:if test="${current_type == 'products'}">class="active"</c:if>>
					<a href="/admin/tags/review_specialties">Products</a>
				</li>
			</ul>

			<h3>Review Skills</h3>

			<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
				<c:param name="bundle" value="${bundle}" />
			</c:import>

			<table id="tags_list" class="table table-hover">
				<thead>
				<tr>
					<th>Name</th>
					<th>Industry</th>
					<th>Creator</th>
					<th>Date Created</th>
					<th>&nbsp;</th>
					<th>&nbsp;</th>
					<th>&nbsp;</th>
				</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
		<div class="dn">
			<div id="modify_tag_modal">
				<div class="control-group">
					<p>Selecting an existing name from the suggestions will result in the skill being merged into the selected skill.</p>
					<div class="messages"></div>

					<input type="hidden" id="mergeIntoTagId" name="mergeIntoTagId" />
					<input type="hidden" id="tagId" name="tagId" />

					<label for="tagName" class="control-label required">Name</label>
					<div class="controls">
						<input type="text" name="tagName" id="tagName" />
					</div>

					<label for="industryId" class="control-label required">Industry</label>
					<div class="controls">
						<select class="input-xlarge" name="industryId" id="industryId">
							<c:forEach var="item" items="${industries}">
								<option value="<c:out value="${item.id}" />"><c:out value="${item.name}" /></option>
							</c:forEach>
						</select>
					<span id="mergeMessage" class="dn">
						The tag will be merged. <a data-action="cancel_merge">Cancel merge.</a>
					</span>
					</div>
				</div>
				<div class="wm-action-container">
					<a data-action="save_tag" type="submit" class="button">Save</a>
				</div>
			</div>
		</div>
	</div>

</wm:admin>
