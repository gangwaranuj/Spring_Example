<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div id="display-messages" class="wm-tab--content -active">
    <table id="group-messages" class="group-list">
        <thead>
        <tr>
            <th style="width: 495px">Message</th>
            <th style="width: 75px">Date</th>
        </tr>
        </thead>
        <tbody class="group-wrapper group-wrapper-members"></tbody>
    </table>
</div>

<script id="message-tmpl" type="text/x-jquery-tmpl">
	<div>
			<div class="fl mr">
				{{if avatarImage}}
					<img src="\${avatarImage}" class="imageMax_48_48 image"/>
				{{else}}
					<img src="${mediaPrefix}/images/no_picture.png" width="48" height="48" class="image"/>
				{{/if}}
			</div>
			<div class="span6 fl">
				<p>
					<strong>\${subject}</strong><br/>
					<small>From: <strong>\${sender_full_name}</strong> </small>
				</p>

				{{if has_short_content}}
					<div class="summary">
						<p>{{html $.escapeHTMLAndnl2br(short_content)}}</p>
						<a class="summary-toggle show">Show more</a>
					</div>
					<div class="fulltext dn">
						<p>{{html $.escapeHTMLAndnl2br(content)}}</p>
						<a class="summary-toggle hide">Show less</a>
					</div>
				{{else}}
					\${content}
				{{/if}}
			</div>
	</div>
</script>
