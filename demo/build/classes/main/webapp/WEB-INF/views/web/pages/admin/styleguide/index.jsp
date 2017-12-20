<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<wm:admin bodyclass="page-styleguide" pagetitle="Style Guide" webpackScript="styleguide">
	<c:set var="shouldShowMDL" value="true" scope="request" />

	<script>
		var context = {
			name: 'styleguide',
			data: {
				shouldShowMDL: true
			},
			features: {}
		};
	</script>

	<nav>
		<wm:logo color="orange" />
	</nav>
	<ul class="nav-drawer">
		<li><a href="#alerts">Alerts</a></li>
		<li><a href="#avatars">Avatars</a></li>
		<li><a href="#badges">Badges</a></li>
		<li><a href="#buttons">Buttons</a></li>
		<li><a href="#colors">Colors</a></li>
		<li><a href="#filters">Filters</a></li>
		<li><a href="#form-elements">Form Elements</a></li>
		<li><a href="#spinners">Loading Spinners</a></li>
		<li><a href="#tables">Tables</a></li>
		<li><a href="#pagination">Pagination</a></li>
		<li><a href="#horizontal-tabs">Horizontal Tabs</a></li>
		<li><a href="#modals">Modals</a></li>
		<li><a href="#logos-icons">Logos and Icons</a></li>
		<li><a href="#progress-indicators">Progress Indicators</a></li>
		<li><a href="#form-elements-tags">Tags</a></li>
		<li><a href="#tooltips">Tooltips</a></li>
		<li><a href="#typography">Typography</a></li>
	</ul>
	<div class="main-content">

		<header>
			<h1>Work Market Style Guide</h1>
		</header>

		<section class="element" id="alerts">
			<h2 class="element--heading">Alerts</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:button classlist="trigger-snackbar">Press Me</wm:button>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				wmNotify({<br>
				&nbsp;message: 'You successfully read this important alert message.',<br>
				&nbsp;actionHandler: (event) => {},<br>
				&nbsp;actionText: 'Do Something'<br>
				});
			</code>
		</section>

		<section class="element" id="avatars">
			<h2 class="element--heading">Avatars</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:avatar src="http://placekitten.com/g/36/36" />
				<wm:avatar src="http://placekitten.com/g/36/36" type="admin" />
				<wm:avatar hash="${currentUser.userNumber}" />
				<wm:avatar hash="${currentUser.userNumber}" type="admin" />
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm-avatar src="..." /&gt;<br>
				&lt;wm-avatar src="..." type="admin" /&gt;<br>
				&lt;wm-avatar hash="..." /&gt;<br>
				&lt;wm-avatar hash="..." type="admin" /&gt;<br><br>
				{{> avatar src="..." }}<br>
				{{> avatar src="..." type="admin" }}<br>
				{{> avatar hash="..." }}<br>
				{{> avatar hash="..." type="admin" }}
			</code>
		</section>

		<section class="element" id="badges">
			<h2 class="element--heading">Badges & Labels</h2>
			<h3 class="element--subheading">Usage</h3>
			<p>Badges? We don't need no stinkin' badges! But if you do, badges are used to show new, or unread items. They are also used to indicate the number of items within a group.</p>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<div class="mdl-badge" data-badge="300">Some sort of text</div>
			</div>
			<code class="source">
				&lt;div class="mdl-badge" data-badge=&quot;300&quot;&gt;Some sort of text&lt;/div&gt;
			</code>
			<div class="demo">
				<div class="mdl-badge" data-badge="60 ml" class="-selected">Some sort of text</div>
			</div>
			<code class="source">
				&lt;div class="mdl-badge" data-badge=&quot;60 ml&quot; class=&quot;-selected&quot;&gt;Some sort of text&lt;/div&gt;
			</code>
			<div class="demo">
				<div class="-draft mdl-badge" data-badge="25">Assignment in draft</div>
			</div>
			<code class="source">
				&lt;div class="-draft mdl-badge" data-badge="25"&gt;Assignment in draft&lt;/div&gt;
			</code>
			<div class="demo">
				<div class="-sent mdl-badge" data-badge="25">Assignment in sent</div>
			</div>
			<code class="source">
				&lt;div class="-sent mdl-badge" data-badge="25"&gt;Assignment in sent&lt;/div&gt;
			</code>
			<div class="demo">
				<div class="-assigned mdl-badge" data-badge="25">Assignment in assigned</div>
			</div>
			<code class="source">
				&lt;div class="-assigned mdl-badge" data-badge="25"&gt;Assignment in assigned&lt;/div&gt;
			</code>
			<div class="demo">
				<div class="-in-progress mdl-badge" data-badge="25">Assignment in in-progress</div>
			</div>
			<code class="source">
				&lt;div class="-in-progress mdl-badge" data-badge="25"&gt;Assignment in in-progress&lt;/div&gt;
			</code>
			<div class="demo">
				<div class="-pending-approval mdl-badge" data-badge="25">Assignment in pending-approval</div>
			</div>
			<code class="source">
				&lt;div class="-pending-approval mdl-badge" data-badge="25"&gt;Assignment in pending-approval&lt;/div&gt;
			</code>
			<div class="demo">
				<div class="-invoiced mdl-badge" data-badge="25">Assignment in invoiced</div>
			</div>
			<code class="source">
				&lt;div class="-invoiced mdl-badge" data-badge="25"&gt;Assignment in invoiced&lt;/div&gt;
			</code>
			<div class="demo">
				<div class="-paid mdl-badge" data-badge="25">Assignment in paid</div>
			</div>
			<code class="source">
				&lt;div class="-paid mdl-badge" data-badge="25"&gt;Assignment in paid&lt;/div&gt;
			</code>
			<div class="demo">
				<div class="-cancelled mdl-badge" data-badge="25">Assignment in cancelled</div>
			</div>
			<code class="source">
				&lt;div class="-cancelled mdl-badge" data-badge="25"&gt;Assignment in cancelled&lt;/div&gt;
			</code>
			<div class="demo">
				<h2 class="heading-title mdl-badge" data-badge="4">Some sort of title text</h2>
			</div>
			<code class="source">
				&lt;h2 class="heading-title mdl-badge" data-badge=&quot;4&quot;&gt;Some sort of title text&lt;/h2&gt;
			</code>
			<div class="demo">
				<div class="mdl-badge" data-badge="300">
					<span data-badge-content>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam eget tempor urna, tincidunt efficitur mi. Ut diam ipsum, bibendum at lacus et, sagittis dictum diam. Ut aliquet sodales dui, vitae interdum enim tempus at. Donec hendrerit, turpis at ullamcorper luctus, metus neque interdum justo, nec volutpat eros metus nec neque. Nam eleifend est eu erat venenatis laoreet. Nam gravida ex tempor rhoncus rutrum. Suspendisse eros massa, blandit in odio vel, fringilla mattis augue. Sed tincidunt justo sit amet nisi interdum tincidunt. Sed eleifend metus at fringilla lobortis. Aenean lectus tellus, aliquam eget malesuada in, posuere eu ex. Quisque ut consequat ipsum, quis vulputate felis.</span>
				</div>
			</div>
			<div class="note">
				When content becomes too long, you will need to add the `data-badge-content` attribute to the content in order use ellipsis.
			</div>
			<code class="source">
				&lt;div class="mdl-badge" data-badge=&quot;300&quot;&gt;<br>
				&nbsp;&lt;span data-badge-content&gt;Lorem ipsum dolor sit amet...&lt;span&gt;<br>
				&lt;/div&gt;
			</code>
			<h2 class="element--heading">Assignment Status Labels</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:label status="draft" />
				<wm:label status="sent" />
				<wm:label status="assigned" />
				<wm:label status="in-progress" />
				<wm:label status="pending-approval" />
				<wm:label status="invoiced" />
				<wm:label status="paid" />
				<wm:label status="cancelled" />
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:label status="draft" /&gt;<br>
				&lt;wm:label status="sent" /&gt;<br>
				&lt;wm:label status="assigned" /&gt;<br>
				&lt;wm:label status="in-progress" /&gt;<br>
				&lt;wm:label status="pending-approval" /&gt;<br>
				&lt;wm:label status="invoiced" /&gt;<br>
				&lt;wm:label status="paid" /&gt;<br>
				&lt;wm:label status="cancelled" /&gt;<br><br>
				{{> label status="draft" }}<br>
				{{> label status="sent" }}<br>
				{{> label status="assigned" }}<br>
				{{> label status="in-progress" }}<br>
				{{> label status="pending-approval" }}<br>
				{{> label status="invoiced" }}<br>
				{{> label status="paid" }}<br>
				{{> label status="cancelled" }}
			</code>
			<h2 class="element--heading">Generic Labels</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:label status="neutral">Neutral</wm:label>
				<wm:label status="success">Success</wm:label>
				<wm:label status="notice">Notice</wm:label>
				<wm:label status="warning">Warning</wm:label>
				<wm:label status="danger">Danger</wm:label>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:label status="neutral"&gt;Neutral&lt;/wm:label&gt;<br>
				&lt;wm:label status="success"&gt;Success&lt;/wm:label&gt;<br>
				&lt;wm:label status="notice"&gt;Notice&lt;/wm:label&gt;<br>
				&lt;wm:label status="warning"&gt;Warning&lt;/wm:label&gt;<br>
				&lt;wm:label status="danger"&gt;Danger&lt;/wm:label&gt;<br><br>
				{{> label status="neutral" text="Neutral" }}<br>
				{{> label status="success" text="Success" }}<br>
				{{> label status="notice" text="Notice" }}<br>
				{{> label status="warning" text="Warning" }}<br>
				{{> label status="danger" text="Danger" }}
			</code>
			<h2 class="element--heading">Custom Labels</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:label color="#00fff8">Custom Label 1</wm:label>
				<wm:label color="#a1fc38">Custom Label 2</wm:label>
				<wm:label color="#ff006b">Custom Label 3</wm:label>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:label color="#00fff8"&gt;Custom Label 1&lt;/wm:label&gt;<br>
				&lt;wm:label color="#a1fc38"&gt;Custom Label 2&lt;/wm:label&gt;<br>
				&lt;wm:label color="#ff006b"&gt;Custom Label 3&lt;/wm:label&gt;<br><br>
				{{> label color="#00fff8" text="Custom Label 1" }}<br>
				{{> label color="#a1fc38" text="Custom Label 2" }}<br>
				{{> label color="#ff006b" text="Custom Label 3" }}
			</code>
		</section>

		<section class="element" id="buttons">
			<h2 class="element--heading">Buttons</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:button primary="true">Primary</wm:button>
				<wm:button accent="true">Accent</wm:button>
				<wm:button>Button</wm:button>
				<wm:button raised="true">Raised</wm:button>
				<wm:button disabled="true">Disabled</wm:button>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:button&gt;Button&lt;/wm:button&gt;<br><br>
				{{> button primary="true" text="Primary" }}<br>
				{{> button accent="true" text="Accent" }}<br>
				{{> button text="Button" }}<br>
				{{> button raised="true" text="Raised" }}<br>
				{{> button disabled="true" text="Disabled" }}
			</code>
		</section>

		<section class="element" id="colors">
			<h2 class="element--heading">Colors</h2>
			<div class="demo color-box -dark-grey">
				<h3>$dark-grey</h3>
				<p>#4a5254</p>
			</div>
			<div class="demo color-box -charcoal-grey">
				<h3>$charcoal-grey</h3>
				<p>#646b6f</p>
				<p>Charcoal grey should be used for typography aside from links.</p>
			</div>
			<div class="demo color-box -grey">
				<h3>$grey</h3>
				<p>#8d9092</p>
				<p>Grey should be used for <strong>inactive form field borders, helper text that appears in unpopulated form fields, and small body text.</strong></p>
			</div>
			<div class="demo color-box -light-grey">
				<h3>$light-grey</h3>
				<p>#cecece</p>
				<p>Light grey should be used to indicate <strong>inactive/disabled state for interactive elements</strong> and <strong>container borders</strong>.</p>
			</div>
			<div class="demo color-box -off-white">
				<h3>$off-white</h3>
				<p>#f1f1f1</p>
				<p>Off-white should be used for the application's <strong>global background.</strong></p>
			</div>
			<div class="demo color-box -white">
				<h3>$white</h3>
				<p>#ffffff</p>
				<p>White should be used for <strong>containers that sit on top of the global background,</strong> including <strong>pages, tables, modals, etc.</strong></p>
				<p>White should also be used for text within <strong>buttons, tags, and labels.</strong></p>
			</div>
			<div class="demo color-box -blue">
				<h3>$blue</h3>
				<p>#1890e0</p>
				<p>Blue should be used for the <strong>hover state for any element using light blue as its primary color</strong></p>
			</div>
			<div class="demo color-box -light-blue">
				<h3>$light-blue</h3>
				<p>#53b3f3</p>
				<p>Light blue should be used for <strong>elements that have an interaction associated with them</strong>, including <strong>standard buttons, links, pagination, accordions, etc.</strong></p>
			</div>
			<div class="demo color-box -green">
				<h3>$green</h3>
				<p>#5eb65f</p>
				<p>Green should be used as the <strong>hover state for primary action buttons.</strong></p>
			</div>
			<div class="demo color-box -light-green">
				<h3>$light-green</h3>
				<p>#5bc75d</p>
				<p>Light Green should be used to <strong>indicate successful actions, including completed list items and progress indicators.</strong> This should also be used as the color for <strong>primary action buttons</strong>.</p>
			</div>
			<div class="demo color-box -orange">
				<h3>$orange</h3>
				<p>#f7961d</p>
				<p>Orange should be used to indicate <strong>active/selected states</strong>, including <strong>form fields, dropdown items, tabs,</strong> etc. This should also be used to call attention to informational content and elements requiring action, such as <strong>notifications, alerts, and required form field indicators.</strong></p>
			</div>
			<div class="demo color-box -red">
				<h3>$red</h3>
				<p>#fb0000</p>
				<p>Red should be used for critical elements, <strong>including error notifications, invalid form fields,</strong> etc.</p>
			</div>
			<div class="demo color-box -purple">
				<h3>$purple</h3>
				<p>#bd10e0</p>
			</div>
		</section>

		<section class="element" id="filters">
			<h2 class="element--heading">Filters</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:filter name="colors" value="red" text="Red" />
				<wm:filter name="colors" value="green" text="Green" />
				<wm:filter name="colors" value="blue" text="Blue" />
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:filter name="colors" value="red" text="Red" /&gt;<br><br>
				{{> filter name="colors" value="red" text="Red" }}
			</code>
		</section>

		<section class="element" id="form-elements">
			<h2 class="element--heading">Form Elements - Text Fields</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:input id="text" label="Text" />
				<wm:input id="textarea" label="I'm a textarea!" textarea="true" />
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:input id="foo" label="Text" /&gt;<br>
				&lt;wm:input id="bar" label="I'm a textarea!" textarea="true" /&gt;<br><br>
				{{> input id="foo" label="Text" }}<br>
				{{> input id="bar" label="I'm a textarea!" textarea=true }}
			</code>
			<h2 class="element--heading">Form Elements - Radio Buttons</h2>
			<h3 class="element--subheading">Usage</h3>
			<p>Buttons are used when users can take an action. All buttons use the default light blue when not in use and use dark blue when hovered over. Buttons are grey when disabled.</p>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:radio name="color" value="red" isChecked="true">Red</wm:radio>
				<wm:radio name="color" value="green">Green</wm:radio>
				<wm:radio name="color" value="blue">Blue</wm:radio>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:radio name="color" value="red" isChecked="true"&gt;Red&lt;/wm:radio&gt;<br><br>
				{{&gt; radio name="color" value="red" isChecked=true text="Red"}}
			</code>
			<h2 class="element--heading">Form Elements - Checkboxes</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:checkbox name="topping" value="pepperoni" isChecked="true" badge="7">Pepperoni</wm:checkbox>
				<wm:checkbox name="topping" value="mushrooms" badge="6">Mushrooms</wm:checkbox>
				<wm:checkbox name="topping" value="peppers" isChecked="true" badge="13">Peppers</wm:checkbox>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:checkbox name="topping" value="pepperoni" isChecked="true" badge="7"&gt;Pepperoni&lt;/wm:checkbox&gt;<br><br>
				{{&gt; checkbox name="topping" value="pepperoni" isChecked=true badge="7" text="Pepperoni"}}
			</code>
			<h2 class="element--heading">From Elements - Select Menus</h2>
			<h3 class="element--subheading">Usage</h3>
			<p>There are two types of dropdowns used. The element starts out grey, but is highlighted with WM orange when active.</p>
			<p>The first type is a gear icon dropdown and is used for manipulating a table view. The second type is for viewing pre-populated lists of items within a form.</p>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<select class="wm-select -single">
					<option value="Rick">Rick</option>
					<option value="Carol">Carol</option>
					<option value="Carl">Carl</option>
					<option value="Daryl">Daryl</option>
					<option value="Maggie">Maggie</option>
				</select>
				<select class="wm-select -multiple">
					<option value="Rick">Rick</option>
					<option value="Carol">Carol</option>
					<option value="Carl">Carl</option>
					<option value="Daryl">Daryl</option>
					<option value="Maggie">Maggie</option>
				</select>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;select class="wm-select"&gt;<br>
				&nbsp;&lt;option value="Rick"&gt;Rick&lt;/option&gt;<br>
				&nbsp;&lt;option value="Carol"&gt;Carol&lt;/option&gt;<br>
				&nbsp;&lt;option value="Carl"&gt;Carl&lt;/option&gt;<br>
				&nbsp;&lt;option value="Daryl"&gt;Daryl&lt;/option&gt;<br>
				&nbsp;&lt;option value="Maggie"&gt;Maggie&lt;/option&gt;<br>
				&lt;/select&gt;<br><br>
				wmSelect();
			</code>
			<h2 class="element--heading">Form Elements - Action Menus</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:action-menu id="action-menu-button">
					<wm:action-menu-item value="add">Add</wm:action-menu-item>
					<wm:action-menu-item value="edit">Edit</wm:action-menu-item>
					<wm:action-menu-item value="delete">Delete</wm:action-menu-item>
				</wm:action-menu>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:action-menu id="action-menu-button"&gt;<br>
				&nbsp;&lt;wm:action-menu-item value="add"&gt;Add&lt;/wm:action-menu-item&gt;<br>
				&nbsp;&lt;wm:action-menu-item value="edit"&gt;Edit&lt;/wm:action-menu-item&gt;<br>
				&nbsp;&lt;wm:action-menu-item value="delete"&gt;Delete&lt;/wm:action-menu-item&gt;<br>
				&lt;/wm:action-menu&gt;<br><br>
				wmActionMenu();
			</code>
			<h2 class="element--heading">Form Elements - Slider</h2>
			<h3 class="element--subheading">Usage</h3>
			<p>Sliders are used in conjunction with search. They allow users to narrow down the scope of the search results, mostly around location.</p>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:slider name="slider" min="0" max="40" step="2" units="assignments" />
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:slider name="slider" min="0" max="40" step="2" units="assignments" /&gt;<br><br>
				{{> slider name="slider" min="0" max="40" step="2" units="assignment" }}
			</code>
			<h2 class="element--heading">Settings Switch</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:settings-switch name="settings" value="foo" id="foo-settings" checked="true" on="On" off="Off" />
				<br>
				<wm:settings-switch name="settings" value="bar" id="bar-settings" checked="true" on="Yes" off="No" />
				<br>
				<p>Default State:</p>
				<wm:settings-switch name="settings" value="baz" id="baz-settings" />
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:settings-switch name="settings" value="foo" id="foo-settings" checked="true" on="On" off="Off" /&gt;<br>
				{{&gt; settings-switch name="settings" value="foo" id="foo-settings" checked=true on="On" off="Off" }}<br><br>

				&lt;wm:settings-switch name="settings" value="foo" id="foo-settings" checked="true" /&gt;<br>
				{{&gt; settings-switch name="settings" value="foo" id="foo-settings" checked=true }}
			</code>
		</section>

		<section class="element" id="spinners">
			<h2 class="element--heading">Loading Spinners</h2>
			<h3 class="element--subheading">Usage</h3>
			<p>The standard spinner should be used when an entire page, interface or data set is being loaded. The background layer between the content and the spinner should be white at 50% opacity.</p>
			<div class="demo">
				<wm:spinner />
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:spinner /&gt;<br><br>
				{{> spinner }}
			</code>
		</section>

		<section class="element" id="tables">
			<h2 class="element--heading">Tables</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<div class="table">
					<div class="table--header">
						<h1 class="table--title wm-icon-users">Group Index</h1>
						<div class="table--filters filter-group">
							<wm:filter name="table-filters" value="red" text="Red" />
							<wm:filter name="table-filters" value="green" text="Green" />
							<wm:filter name="table-filters" value="blue" text="Blue" />
						</div>
						<wm:button>Button</wm:button>
						<wm:pagination min="1" max="10" />
						<select class="action-menu">
							<option value="add">Add</option>
							<option value="edit">Edit</option>
							<option value="delete">Delete</option>
						</select>
					</div>
					<table>
						<thead>
							<tr>
								<th class="table--bulk-actions">
									<wm:checkbox name="assignments" value="all" />
								</th>
								<th class="sorting_asc">Assign. Name</th>
								<th>Assign. ID</th>
								<th>Assign. Location</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td class="table--bulk-actions"><wm:checkbox name="assignments" value="0123456789" /></td>
								<td><a href="#">Upgrade windows machines at local elementary school</a></td>
								<td>0123456789</td>
								<td>Bronx, New York, NY</td>
							</tr>
							<tr>
								<td class="table--bulk-actions"><wm:checkbox name="assignments" value="0123456789" /></td>
								<td><a href="#">Destroy that stupid office printer that everyone hates</a></td>
								<td>0123456789</td>
								<td>Logan Square, Chicago, IL</td>
							</tr>
							<tr>
								<td class="table--bulk-actions"><wm:checkbox name="assignments" value="0123456789" /></td>
								<td><a href="#">Build a cool arcade console for the dev area</a></td>
								<td>0123456789</td>
								<td>Hawthorne, Los Angeles, CA</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;div class="table"&gt;<br>
				&nbsp;&lt;div class="table--header"&gt;<br>
				&nbsp;&nbsp;&lt;h1 class="table--title wm-icon-users"&gt;Group Index&lt;/h1&gt;<br>
				&nbsp;&nbsp;&lt;div class="table--filters filter-group"&gt;<br>
				&nbsp;&nbsp;&nbsp;&lt;wm:filter name="table-filters" value="red" text="Red" /&gt;<br>
				&nbsp;&nbsp;&nbsp;&lt;wm:filter name="table-filters" value="green" text="Green" /&gt;<br>
				&nbsp;&nbsp;&nbsp;&lt;wm:filter name="table-filters" value="blue" text="Blue" /&gt;<br>
				&nbsp;&nbsp;&lt;/div&gt;<br>
				&nbsp;&nbsp;&lt;wm:button&gt;Button&lt;/wm:button&gt;<br>
				&nbsp;&nbsp;&lt;wm:pagination min="1" max="10" /&gt;<br>
				&nbsp;&nbsp;&lt;select class="action-menu"&gt;<br>
				&nbsp;&nbsp;&nbsp;&lt;option&gt;Add&lt;/option&gt;<br>
				&nbsp;&nbsp;&nbsp;&lt;option&gt;Edit&lt;/option&gt;<br>
				&nbsp;&nbsp;&nbsp;&lt;option&gt;Delete&lt;/option&gt;<br>
				&nbsp;&nbsp;&lt;/select&gt;<br>
				&nbsp;&lt;/div&gt;<br>
				&nbsp;&lt;table&gt;<br>
				&nbsp;&nbsp;&lt;thead&gt;<br>
				&nbsp;&nbsp;&nbsp;&lt;tr&gt;<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;th class="table--bulk-actions"&gt;<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;wm:checkbox name="assignments" value="all" /&gt;<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;/th&gt;<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;th class="sorting_asc"&gt;Assign. Name&lt;/th&gt;<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;th&gt;Assign. ID&lt;/th&gt;<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;th&gt;Assign. Location&lt;/th&gt;<br>
				&nbsp;&nbsp;&nbsp;&lt;/tr&gt;<br>
				&nbsp;&nbsp;&lt;/thead&gt;<br>
				&nbsp;&nbsp;&lt;tbody&gt;<br>
				&nbsp;&nbsp;&nbsp;&lt;tr&gt;<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;td class="table--bulk-actions"&gt;<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;wm:checkbox name="assignments" value="0123456789" /&gt;<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;/td&gt;<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;td&gt;<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;a href="#"&gt;Upgrade windows machines at local elementary school&lt;/a&gt;<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;/td&gt;<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;td&gt;0123456789&lt;/td&gt;<br>
				&nbsp;&nbsp;&nbsp;&nbsp;&lt;td&gt;New York&lt;/td&gt;<br>
				&nbsp;&nbsp;&nbsp;&lt;/tr&gt;<br>
				&nbsp;&nbsp;&lt;/tbody&gt;<br>
				&nbsp;&lt;/table&gt;<br>
				&lt;/div&gt;
			</code>
		</section>

		<section class="element" id="filters">
			<h2 class="element--heading">Filters</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo filter-group">
				<wm:filter name="colors" value="all" text="All" />
				<wm:filter name="colors" value="red" text="Red" />
				<wm:filter name="colors" value="green" text="Green" />
				<wm:filter name="colors" value="blue" text="Blue" />
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;div class="filter-group"&gt;
				&lt;wm:filter name="colors" value="red" text="Red" /&gt;&lt;/div&gt; <br>
				&lt;div class="filter-group"&gt;{{> filter name="colors" value="red" text="Red" }}&lt;/div&gt;
			</code>
		</section>

		<section class="element" id="pagination">
			<h2 class="element--heading">Pagination</h2>
			<h3 class="element--subheading">Usage</h3>
			<p>Pagination uses a multi-directional button. This element is used for any page which has multiple pages of results available. Users can either go forward one page or go back one page. The default state uses orange. On hover a light orange is used.</p>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:pagination min="1" max="10" />
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:pagination min="1" max="10" /&gt;<br><br>
				{{&gt; pagination min="1" max="10" }}
			</code>
		</section>

		<section class="element" id="horizontal-tabs">
			<h2 class="element--heading">Horizontal Tabs</h2>
			<h3 class="element--subheading">Usage</h3>
			<p>Tabs are use when we want to group information on one page, but not making everything visible at once. As a design pattern, tabs are commonly used to break up information and keep the user from endlessly scrolling.</p>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<ul class="wm-tabs">
					<li class="wm-tab -active">Overview</li>
					<li class="wm-tab" data-badge="394">Qualifications</li>
				</ul>
				<br>
				<ul class="wm-tabs">
					<li class="wm-tab -active">Overview</li>
					<li class="wm-tab" data-badge="394">Qualifications</li>
					<li class="wm-tab">Ratings</li>
				</ul>
				<br>
				<ul class="wm-tabs">
					<li class="wm-tab -active">Overview</li>
					<li class="wm-tab">Qualifications</li>
					<li class="wm-tab">Ratings</li>
					<li class="wm-tab">Comments</li>
					<li class="wm-tab" data-badge="394">Tags</li>
				</ul>
				<br>
				<ul class="wm-tabs" style="width: 670px;">
					<li class="wm-tab -active">Overview</li>
					<li class="wm-tab">Qualifications</li>
					<li class="wm-tab">Ratings</li>
					<li class="wm-tab">Comments</li>
					<li class="wm-tab" data-badge="394">Tags</li>
					<li class="wm-tab" data-badge="394">Media</li>
				</ul>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;ul class="wm-tabs"&gt;<br>
				&nbsp;&lt;li class="wm-tab -active" data-badge="3" data-content="#overview"&gt;Overview&lt;/li&gt;<br>
				&nbsp;&lt;li class="wm-tab" data-badge="4" data-content="#qualifications"&gt;Qualifications&lt;/li&gt;<br>
				&nbsp;&lt;li class="wm-tab" data-badge="13" data-content="#ratings"&gt;Ratings&lt;/li&gt;<br>
				&lt;/ul&gt;<br><br>
				&lt;div class="wm-tab--content -active"&gt;<br>
				&nbsp;&hellip;<br>
				&lt;/div&gt;
			</code>
		</section>

		<section class="element" id="modals">
			<h2 class="element--heading">Modals</h2>
			<h3 class="element--subheading">Usage</h3>
			<p>Modals are used for an extra level of emphasis over a simple alert. Used when a decision is required of the user.</p>
			<h3>API</h3>
			<p><strong>show()</strong> - Display the modal.</p>
			<p><strong>hide()</strong> - Hide the modal.</p>
			<p><strong>toggle()</strong> - Toggle the modal.</p>
			<p><strong>destroy()</strong> - Destroy the modal.</p>
			<p><strong>slideForward()</strong> - If there is a next slide, navigate to it.</p>
			<p><strong>slideBack()</strong> - If there is a previous slide, navigate to it.</p>
			<h3>Configuration Options</h3>
			<p><strong>slides</strong> - an optional array containing modal slides with per-slide configurations. <i>Default: null</i></p>
			<p><strong>autorun</strong> - a flag that if set will show the modal upon instantiation. <i>Default: false</i></p>
			<p><strong>root</strong> - the element to which the modal will be attached. <i>Default: body</i></p>
			<p><strong>closeSelector</strong> - a selector to attach the modal close event to on instantiation. <i>Default: [data-modal-close]</i></p>
			<p><strong>activeClass</strong> - a class to denote an active (displayed) modal. <i>Default: -active</i></p>
			<p><strong>template</strong> - an HTML template to render the modal element. <i>Default: wmTemplates.modal</i></p>
			<p><strong>destroyOnClose</strong> - a flag denoting whether or not to destroy the modal element upon close. <i>Default: false</i></p>
			<p><strong>nextSelector</strong> - a selector to attach the modal forward navigation event to. <i>Default: [data-modal-next]</i></p>
			<p><strong>prevSelector</strong> - a selector to attach the modal backward navigation event to. <i>Default: [data-modal-prev]</i></p>
			<p><strong>showProgress</strong> - set true to display a progress indicator for multi-slide modals. <i>Default: false</i></p>
			<h3>Content Options</h3>
			<p><i>Note: the following configuration options can define slides in the optional slides[] array. See example below.</i></p>
			<p><strong>title</strong> - The title of the modal/slide.</p>
			<p><strong>content</strong> - The content of the modal/slide. Can include HTML.</p>
			<p><strong>id</strong> - An optional ID to attach to the slide element.</p>
			<p><strong>isActive</strong> - Set a specific slide to be the first slide.</p>
			<p><strong>fixedScroll</strong> - Flag to limit the content window to a 440px height, with overflow scrolled in the modal.</p>
			<p><strong>controls</strong> - An array of control objects to create buttons in the modal.</p>
			<h3>Control Options</h3>
			<p><strong>text</strong> - The button text.</p>
			<p><strong>primary</strong> - Set true to make the button green (primary action).</p>
			<p><strong>close</strong> - Set true to have the control dismiss the modal.</p>
			<p><strong>forward</strong> - Set true to have the control advance to the next slide (if applicable).</p>
			<p><strong>back</strong> - Set true to have the control advance to the next slide (if applicable).</p>
			<p><strong>classList</strong> - A string of classes to be added to the control. Useful for attaching custom events.</p>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<button class="button test-modal">Open Basic Modal</button>
				<button class="button test-slides">Open Multi-Slide Modal</button>
			</div>
			<h3 class="element--subheading">Source (Basic)</h3>
			<code class="source preformat">
var modal = wmModal({
	title: 'Work Market Modal',
	content: 'Lorem ipsum dolor ...'
	controls: [
		{
			text: 'Cancel',
			close: true,
			classList: ''
		},
		{
			text: 'Save',
			primary: true,
			classList: ''
		}
	]
});
$('#modals').find('.simple-example').on('click', modal.toggle);
			</code>
			<h3 class="element--subheading">Source (Multi)</h3>
			<code class="source preformat">
var multiModal = wmModal({
	showProgress: true,
	slides: [
		{
			title: 'Slide 1',
			content: 'Lorem ipsum dolor ...'
			controls: [
				{
					text: 'Cancel',
					close: true,
					classList: ''
				},
				{
					text: 'Continue',
					primary: true,
					forward: true,
					classList: ''
				}
			]
		},
		{
			title: 'Slide 2',
			content: 'Lorem ipsum dolor ...'
			controls: [
				{
					text: 'Back',
					back: true,
					classList: ''
				},
				{
					text: 'Finish',
					primary: true,
					classList: ''
				}
			]
		}
	]
});
$('#modals').find('.multi-example').on('click', multiModal.toggle);
			</code>
		</section>

		<section class="element" id="logos-icons">
			<h2 class="element--heading">Logos</h2>
			<h3 class="element--subheading">Usage</h3>
			<p>The logo comes in three varieties: grey, white and orange. The orange logo is the primary one and should be used whenever possible. The grey logo is next, followed by the white. The white logo can only appear on a dark grey background. The logo begins with the WM shorthand followed by the phrase "workmarket" in all lowercase. The second half of the phrase is a lower opacity of the same color used elsewhere. The "WM" mark is called the "work wave."</p>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:logo />
				<wm:logo color="orange"/>
				<div id="white-logo">
					<wm:logo color="white"/>
				</div>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:logo /&gt;<br>
				&lt;wm:logo color="orange"/&gt;<br>
				&lt;wm:logo color="white"/&gt;<br><br>
				{{> logo }}<br>
				{{> logo color="orange" }}<br>
				{{> logo color="white" }}
			</code>
			<h2 class="element--heading">Icons</h2>
			<h3 class="element--subheading">Usage</h3>
			<p>The WM Icon set includes over 68 glyphs in font format from the iconmoon app. WM icons are available via our icon font, but there are more to come. We only ask that you use them wisely.</p>
			<p>Product icons are the visual expression of our brand's products, services and tools. Simple, bold and friendly, they communicate the core idea and intent of an action in our product. While each icon is visually distinct, all icons should be unified through context and style.</p>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<i class="wm-icon-work"></i>
				<i class="wm-icon-users"></i>
				<i class="wm-icon-payments"></i>
				<i class="wm-icon-chart"></i>
				<i class="wm-icon-graph"></i>
				<i class="wm-icon-house"></i>
				<i class="wm-icon-search"></i>
				<i class="wm-icon-bell"></i>
				<i class="wm-icon-phone-filled"></i>
				<i class="wm-icon-edit"></i>
				<i class="wm-icon-trash"></i>
				<i class="wm-icon-attachment"></i>
				<i class="wm-icon-undo"></i>
				<i class="wm-icon-checkmark"></i>
				<i class="wm-icon-filter"></i>
				<i class="wm-icon-link"></i>
				<i class="wm-icon-share"></i>
				<i class="wm-icon-eye"></i>
				<i class="wm-icon-follow"></i>
				<i class="wm-icon-unfollow"></i>
				<i class="wm-icon-download"></i>
				<i class="wm-icon-upload"></i>
				<i class="wm-icon-location"></i>
				<i class="wm-icon-down-arrow"></i>
				<i class="wm-icon-up-arrow"></i>
				<i class="wm-icon-minus"></i>
				<i class="wm-icon-plus"></i>
				<i class="wm-icon-reports"></i>
				<i class="wm-icon-assignments"></i>
				<i class="wm-icon-user"></i>
				<i class="wm-icon-add-user"></i>
				<i class="wm-icon-delete-user"></i>
				<i class="wm-icon-users"></i>
				<i class="wm-icon-follow-users"></i>
				<i class="wm-icon-gear"></i>
				<i class="wm-icon-pill"></i>
				<i class="wm-icon-envelope"></i>
				<i class="wm-icon-tag"></i>
				<i class="wm-icon-speech"></i>
				<i class="wm-icon-buildings"></i>
				<i class="wm-icon-note"></i>
				<i class="wm-icon-payments"></i>
				<i class="wm-icon-minus-filled"></i>
				<i class="wm-icon-plus-filled"></i>
				<i class="wm-icon-information-filled"></i>
				<i class="wm-icon-question-filled"></i>
				<i class="wm-icon-checkmark-circle"></i>
				<i class="wm-icon-exclamation-sign"></i>
				<i class="wm-icon-remove-sign"></i>
				<i class="wm-icon-clock"></i>
				<i class="wm-icon-calendar"></i>
				<i class="wm-icon-headset"></i>
				<i class="wm-icon-globe-circle"></i>
				<i class="wm-icon-lock-circle"></i>
				<i class="wm-icon-left-arrow"></i>
				<i class="wm-icon-right-arrow"></i>
				<i class="wm-icon-test"></i>
				<i class="wm-icon-page-out"></i>
				<i class="wm-icon-work"></i>
				<i class="wm-icon-copy"></i>
				<i class="wm-icon-list"></i>
				<i class="wm-icon-move"></i>
				<i class="wm-icon-x"></i>
				<i class="wm-icon-map"></i>
				<i class="wm-icon-show-thumbnails"></i>
				<i class="wm-icon-facebook"></i>
				<i class="wm-icon-google-plus"></i>
				<i class="wm-icon-linked-in"></i>
				<i class="wm-icon-twitter"></i>
				<i class="wm-icon-wm"></i>
				<i class="wm-icon-wm-filled"></i>

			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;i class="wm-icon-work"&gt;&lt;/i&gt;
			</code>
			<h2 class="element--heading">Native Icons</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<p>Used in the Android and iOS native applications only.</p>
				<table>
					<thead>
						<th>iOS</th>
						<th>Android</th>
					</thead>
					<tr>
						<td>
							<img src="${mediaPrefix}/images/native-icons/ios/hdpi/ic_launcher_APP.png" alt="Work Market Icon" />
							<img src="${mediaPrefix}/images/native-icons/ios/xhdpi/ic_launcher_APP.png" alt="Work Market Icon" />
							<img src="${mediaPrefix}/images/native-icons/ios/xxhdpi/ic_launcher_APP.png" alt="Work Market Icon" />
						</td>
						<td>
							<img src="${mediaPrefix}/images/native-icons/android/Icon-40@2x.png" alt="Work Market Icon" />
							<img src="${mediaPrefix}/images/native-icons/android/Icon-40@3x.png" alt="Work Market Icon" />
							<img src="${mediaPrefix}/images/native-icons/android/Icon-60@3x.png" alt="Work Market Icon" />
						</td>
					</tr>
				</table>
			</div>
			<h2 class="element--heading">Favicon</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<img src="//www.workmarket.com/favicon.ico" alt="Work Market Favicon" />
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;img src="//www.workmarket.com/favicon.ico" alt="Work Market Favicon" /&gt;
			</code>
		</section>

		<section class="element" id="progress-indicators">
			<h2 class="element--heading">Progress Bar</h2>
			<h3 class="element--subheading">Usage</h3>
			<p>The Work Market color scheme uses four primary colors: grey, orange, blue and green. All text uses various opacities of grey.</p>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<wm:progress-bar id="progress-bar" width="80" />
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;wm:progress-bar width="46" /&gt;<br>
				{{> progress-bar width="46" }}
			</code>
			<h2 class="element--heading">Progress Indicator</h2>
			<h3 class="element--subheading">Usage</h3>
			<p>The progress indicator is used to visualize the progression through specific steps or tasks. The user must add a minimum of 3 to a maximum of 6 progress indicator steps (tasks) represented by
				the progress-indicator tag attribute "steps", as an integer, to each progress indicator tag. In addition, in order for the horizontal line to properly appear, it must be added above the progress indicator
				tags and include the total step count as the data attribute "data-step-count", also expressed as an integer. The "steps" attribute and the "data-step-count" attribute must be the same value.</p>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<div class="wm-progress-indicator">
					<wm:progress-indicator title="Profile Information" />
					<wm:progress-indicator title="Location" status="active" />
					<wm:progress-indicator title="Work Categories" />
					<wm:progress-indicator title="More Work Categories"/>
					<wm:progress-indicator title="Almost Finished"/>
					<wm:progress-indicator title="Done!" />
				</div>
				<div class="wm-progress-indicator">
					<wm:progress-indicator title="Profile Information" />
					<wm:progress-indicator title="Location" status="active" />
					<wm:progress-indicator title="Done!" />
				</div>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;div class="wm-progress-indicator" &gt;<br>
				&nbsp;&lt;wm:progress-indicator title="Profile Information" status="active" /&gt;<br>
				&lt;/div&gt;<br><br>
				&lt;div class="wm-progress-indicator" &gt;<br>
				&nbsp;{{&gt; progress-indicator title="Profile Information" status="active" }}<br>
				&lt;/div&gt;
			</code>
		</section>

		<section class="element" id="form-elements-tags">
			<h2 class="element--heading">Tags</h2>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<input class="wm-tags" type="text" value="Foo, Bar, Baz" />
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;input class="wm-tags" type="text" /&gt;<br><br>
				wmTags();
			</code>
		</section>

		<section class="element" id="tooltips">
			<h2 class="element--heading">Tooltips</h2>
			<h3 class="element--subheading">Usage</h3>
			<p>Tooltips are contextual help in areas where a user might need just a bit more of a description on what an action they are about to take might do. Tooltips with titles are only used for the AppCues used in introducing new features to users.</p>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<span id="tooltip-top">Top</span>
				<wm:tooltip forHtml="tooltip-top" direction="top">Top!</wm:tooltip>
				<span id="tooltip-right">Right</span>
				<wm:tooltip forHtml="tooltip-right" direction="right">Right!</wm:tooltip>
				<span id="tooltip-bottom">Bottom</span>
				<wm:tooltip forHtml="tooltip-bottom" direction="bottom">Bottom!</wm:tooltip>
				<span id="tooltip-left">Left</span>
				<wm:tooltip forHtml="tooltip-left" direction="left">Left!</wm:tooltip>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;span id="tooltip-top"&gt;Top&lt;/span&gt;<br>
				&lt;wm:tooltip forHtml="tooltip-top" direction="top"&gt;Top!&lt;/wm:tooltip&gt;<br>
				&lt;span id="tooltip-right"&gt;Right&lt;/span&gt;<br>
				&lt;wm:tooltip forHtml="tooltip-right" direction="right"&gt;Right!&lt;/wm:tooltip&gt;<br>
				&lt;span id="tooltip-bottom"&gt;Bottom&lt;/span&gt;<br>
				&lt;wm:tooltip forHtml="tooltip-bottom" direction="bottom"&gt;Bottom!&lt;/wm:tooltip&gt;<br>
				&lt;span id="tooltip-left"&gt;Left&lt;/span&gt;<br>
				&lt;wm:tooltip forHtml="tooltip-left" direction="left"&gt;Left!&lt;/wm:tooltip&gt;<br><br>
				&lt;span id="tooltip-left-2"&gt;Left&lt;/span&gt;<br>
				{{> tooltip forHtml="tooltip-left-2" text="Left!" }}<br>
			</code>
		</section>

		<section class="element" id="typography">
			<h2 class="element--heading">Typography</h2>
			<h3 class="element--subheading">Usage</h3>
			<p>H1 tags should only be used once on a page, and should reflect the title of the page or general theme of hte current interface.</p>
			<p>Subsequent heading tags should be used to determine page hierarchy, where a heading must appear under/within the next largest heading. (eg. Content using an H3 heading must exist inside an H2 tag.)</p>
			<p>When displaying typography on mobile devices, font sizes listed above should increase by 2px.</p>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<h1>Title - Regular, 24px</h1>
				<h2>Subtitle - Semibold, 20px</h2>
				<h3>3rd Level Heading - Semibold, 18px</h3>
				<h4>4th Level Heading - Regular, 18px</h4>
				<h5>5th Level Heading - Light, 18px</h5>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;h1&gt;Title - Regular, 24px&lt;/h1&gt;<br>
				&lt;h2&gt;Subtitle - Semibold, 20px&lt;h2&gt;<br>
				&lt;h3&gt;3rd Level Heading - Semibold, 18px&lt;h3&gt;<br>
				&lt;h4&gt;4th Level Heading - Regular, 18px&lt;h4&gt;<br>
				&lt;h5&gt;5th Level Heading - Light, 18px&lt;h5&gt;
			</code>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				Body Text - Regular, 14px<br>
				<strong>Strong Text - Semi Bold, 14px</strong><br>
				<small>Small - Semi Bold, 10px</small>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				Body Text - Regular, 14px<br>
				&lt;strong&gt;Strong Text - Semi Bold, 14px&lt;/strong&gt;<br>
				&lt;small&gt;Small Text - Semi Bold, 10px&lt;/small&gt;
			</code>
			<h3 class="element--subheading">Example</h3>
			<div class="demo">
				<a href="javascript:void(0);">Text Link - Semibold, 14px</a>
			</div>
			<h3 class="element--subheading">Source</h3>
			<code class="source">
				&lt;a href="javascript:void(0);"&gt;Text Link - Semibold, 14px&lt;/a&gt;
			</code>
		</section>

		<section class="element" id="search-filter">
			<h2 class="element--heading">POC - Search</h2>
			<h3 class="element--subheading">Usage</h3>
			<h3 class="element--subheading">Example</h3>
			<div class="search-demo">
			</div>
			<div class="table-rich-row" data-fixed-column="Column 0" filtered-properties="id"></div>
			<a class="clear-search-filter" href="#">Reset Filters</a>
			<br />
			<a class="show-search-filter" href="#">Show Filter Object (in console)</a>

			<h3 class="element--subheading">Source</h3>
			<code class="source">

			</code>
		</section>
	</div>

</wm:admin>
