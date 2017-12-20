<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/template" id="nav-notifications-tmpl">
	<li class="dropdown-menu--header">
		<div class="row-fluid">
			<span class="muted span8"><fmt:message key="global.notifications" /></span>
			<a id="see-all" class="span8 text-right" href="/mysettings/notifications"><fmt:message key="global.settings" /></a>
		</div>
	</li>
	{{ _.each(notifications, function (notification) { }}
	<li class="dropdown-menu--item">{{= notification.display_message}}
		<small class="meta"><i class="icon-time muted"></i> {{= $.ago(new Date(notification.created_on).valueOf(), $.now()) }}<i class="icon-circle {{if (!notification.viewed_at) { }} power-on{{ } }}" id="notification_status"></i></small>
	</li>
	{{ }); }}
	<li class="dropdown-menu--footer">
		<a id="see-all" class="pull-left" href="/notifications/active"><fmt:message key="global.see_all" /></a>
	</li>
</script>

<script type="text/template" id="growl-alert-tmpl">
	<div class="wm-alert -{{if (type) { }}{{= type}}{{ } else { }}notice{{ } }}">
		<i class="wm-icon-x" data-notify="dismiss"></i>
		<div class="wm-alert--text">
			<i class="wm-icon-checkmark"></i>
			<span class="wm-alert--callout">{{= callout}}</span>
			{{= text}}
		</div>
		<button class="button -primary">{{= button}}</button>
	</div>
</script>