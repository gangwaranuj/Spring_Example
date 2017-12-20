<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<form id="snippet-builder">
	<div class="sidebar-card">
		<h2 class="sidebar-card--title">Generate your Work Feed Code Snippet</h2>
		<div>
			<p>
				Copy and paste this code snippet to your site wherever you'd like to
				show your visitors your available work. You can customize the snippet
				by entering your own values below.
			</p>
			<div>
				<div>
					<input type="submit" value="Generate Snippet" class="button -primary" />
					<button id="preview" class="button -light">Preview</button>
				</div>
				<div>
					<textarea id="snippet" rows="2" style="font: 12px Consolas, Liberation Mono, Courier, monospace;"></textarea>
				</div>
			</div>
		</div>
	</div>

	<div class="custom-look sidebar-card">
		<h2 class="sidebar-card--title">Customize the look</h2>
		<p>
			You can customize the look and feel of your Work Feed
			by changing these settings. All fields are optional.
		</p>

		<div>
			<label class="help-block" for="feed-title">Title:</label>
			<input id="feed-title" name="title" type="text"/>
			<div class="default">Default: "Find work at Work Market"</div>
		</div>

		<div>
			<label class="help-block" for="feed-bg-color">Background color:</label>
			<input id="feed-bg-color" name="backgroundColor" type="text"/>
			<div class="default">
				Accepts any valid css background-color value.
				Example: #FFFFFF
			</div>
		</div>

		<div>
			<label class="help-block" for="feed-width">Width:</label>
			<input id="feed-width" name="width" type="text"/>
			<div class="default">
				Default: 100% of the width of its containing element.
				Accepts any valid css width value.
				Example: 200px
			</div>
		</div>

		<div>
			<label class="help-block" for="feed-font">Font:</label>
			<input id="feed-font" name="font" type="text"/>
			<div class="default">
				Default: the font style of your site.
				Accepts any valid css font value.
				Example: 12px Arial, Helvetica, Sans serif
			</div>
		</div>

		<div>
			<label class="help-block" for="feed-link-color">Link color:</label>
			<input id="feed-link-color" name="linkColor" type="text"/>
			<div class="default">
				Default: the link color of your site.
				Accepts any valid css color value.
				Example: #FE8707
			</div>
		</div>

		<div>
			<label class="help-block" for="feed-border">Border:</label>
			<input id="feed-border" name="border" type="text"/>
			<div class="default">
				Default: the border style of your site.
				Accepts any valid css border value.
				Example: 1px dashed #EEEEEE
			</div>
		</div>
		<div>
			<label class="help-block" for="feed-padding">Padding:</label>
			<input id="feed-padding" name="padding" type="text"/>
			<div class="default">
				Accepts any valid css padding value.
				Example: 4px 6px
			</div>
		</div>
	</div>

	<div class="custom-results sidebar-card">
		<h2 class="sidebar-card--title">Customize the results</h2>
		<p>
			You can customize the results displayed in your Work Feed
			by changing these settings. All fields are optional.
		</p>

		<div>
			<label class="help-block" for="feed-limit">Number of results:</label>
			<input id="feed-limit" name="limit" type="text"/>
			<div class="default">Default: 10</div>
		</div>

		<div class="location">
			<div class="help-block">Postal code and Distance:</div>
			<input id="feed-postal" name="postal" type="text" class="postal" />
			<input id="feed-distance" name="distance" type="text" class="distance" />
			<span id="postal-msg"></span>
			<div class="default">
				Postal Code Default: All postal codes<br />
				Distance Default: 50 (in miles)
			</div>
		</div>

		<div>
			<div class="help-block">Industry:</div>
			<select id="feed-industry" name="industry">
				<option value="">-- All --</option>
				<c:forEach items="${industries}" var="industry">
					<option value="${industry.id}"><c:out value="${industry.name}" /></option>
				</c:forEach>
			</select>
		</div>

	</div>

</form>

<script id="modal-tmpl" type="text/html">
	<div class="page-header">
		<h4>Sample Work Feed</h4>
		<div>
			<div id="sample"></div>
		</div>
		<span class="help-block" style="width:{{= width }}">
			This is an example of how the Work Market Work Feed will look on your site.
			It is shown with some of your live assignments.
		</span>
	</div>
</script>
