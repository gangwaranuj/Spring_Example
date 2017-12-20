this["wm"] = this["wm"] || {};
this["wm"]["templates"] = this["wm"]["templates"] || {};

Handlebars.registerPartial("alert", this["wm"]["templates"]["alert"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper;

  return this.escapeExpression(((helper = (helper = helpers.type || (depth0 != null ? depth0.type : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"type","hash":{},"data":data}) : helper)));
},"3":function(depth0,helpers,partials,data) {
    return "notice";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<div class=\"wm-alert "
    + alias3(((helper = (helper = helpers.classlist || (depth0 != null ? depth0.classlist : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"classlist","hash":{},"data":data}) : helper)))
    + " -"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.type : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.program(3, data, 0),"data":data})) != null ? stack1 : "")
    + "\" id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\">\n	<i class=\"wm-icon-x\" data-notify=\"dismiss\"></i>\n	<div class=\"wm-alert--text\">\n		<i class=\"wm-icon-checkmark\"></i>\n		<span class=\"wm-alert--callout\">"
    + alias3(((helper = (helper = helpers.callout || (depth0 != null ? depth0.callout : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"callout","hash":{},"data":data}) : helper)))
    + "</span>\n		"
    + ((stack1 = ((helper = (helper = helpers.text || (depth0 != null ? depth0.text : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"text","hash":{},"data":data}) : helper))) != null ? stack1 : "")
    + "\n	</div>\n	<button class=\"button -primary\">"
    + alias3(((helper = (helper = helpers.button || (depth0 != null ? depth0.button : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"button","hash":{},"data":data}) : helper)))
    + "</button>\n</div>";
},"useData":true}));

Handlebars.registerPartial("avatar", this["wm"]["templates"]["avatar"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper;

  return "-"
    + this.escapeExpression(((helper = (helper = helpers.type || (depth0 != null ? depth0.type : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"type","hash":{},"data":data}) : helper)));
},"3":function(depth0,helpers,partials,data) {
    var helper;

  return "		<img src=\""
    + this.escapeExpression(((helper = (helper = helpers.src || (depth0 != null ? depth0.src : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"src","hash":{},"data":data}) : helper)))
    + "\" />\n";
},"5":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "		<canvas width=\"100\" height=\"100\" data-jdenticon-hash=\""
    + alias3(((helper = (helper = helpers.hash || (depth0 != null ? depth0.hash : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"hash","hash":{},"data":data}) : helper)))
    + alias3(((helper = (helper = helpers.hash || (depth0 != null ? depth0.hash : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"hash","hash":{},"data":data}) : helper)))
    + "\"></canvas>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper;

  return "<div class=\"wm-avatar "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.type : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " "
    + this.escapeExpression(((helper = (helper = helpers.classlist || (depth0 != null ? depth0.classlist : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"classlist","hash":{},"data":data}) : helper)))
    + "\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.src : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.program(5, data, 0),"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true}));

Handlebars.registerPartial("button", this["wm"]["templates"]["button"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "-primary";
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return "tooltipped tooltipped-"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.tooltipDirection : depth0),{"name":"if","hash":{},"fn":this.program(4, data, 0),"inverse":this.program(6, data, 0),"data":data})) != null ? stack1 : "");
},"4":function(depth0,helpers,partials,data) {
    var helper;

  return this.escapeExpression(((helper = (helper = helpers.tooltipDirection || (depth0 != null ? depth0.tooltipDirection : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"tooltipDirection","hash":{},"data":data}) : helper)));
},"6":function(depth0,helpers,partials,data) {
    return "n";
},"8":function(depth0,helpers,partials,data) {
    var helper;

  return "aria-label=\""
    + this.escapeExpression(((helper = (helper = helpers.tooltip || (depth0 != null ? depth0.tooltip : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"tooltip","hash":{},"data":data}) : helper)))
    + "\"";
},"10":function(depth0,helpers,partials,data) {
    return "disabled";
},"12":function(depth0,helpers,partials,data) {
    var helper;

  return "wm-icon-"
    + this.escapeExpression(((helper = (helper = helpers.icon || (depth0 != null ? depth0.icon : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"icon","hash":{},"data":data}) : helper)));
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<button class=\"button -new "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.primary : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " "
    + alias3(((helper = (helper = helpers.classlist || (depth0 != null ? depth0.classlist : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"classlist","hash":{},"data":data}) : helper)))
    + " "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.tooltip : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.tooltip : depth0),{"name":"if","hash":{},"fn":this.program(8, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.disabled : depth0),{"name":"if","hash":{},"fn":this.program(10, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">\n	<div class=\"button--content "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.icon : depth0),{"name":"if","hash":{},"fn":this.program(12, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">"
    + ((stack1 = ((helper = (helper = helpers.text || (depth0 != null ? depth0.text : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"text","hash":{},"data":data}) : helper))) != null ? stack1 : "")
    + "</div>\n</button>\n";
},"useData":true}));

Handlebars.registerPartial("checkbox", this["wm"]["templates"]["checkbox"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper;

  return "data-badge=\""
    + this.escapeExpression(((helper = (helper = helpers.badge || (depth0 != null ? depth0.badge : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"badge","hash":{},"data":data}) : helper)))
    + "\"";
},"3":function(depth0,helpers,partials,data) {
    return "checked";
},"5":function(depth0,helpers,partials,data) {
    return "data-badge-content";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<label class=\"wm-checkbox\" id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.badge : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " "
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.badge : depth0),0,{"name":"eq","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">\n	<input type=\"checkbox\" name=\""
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "\" value=\""
    + alias3(((helper = (helper = helpers.value || (depth0 != null ? depth0.value : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"value","hash":{},"data":data}) : helper)))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isChecked : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " />\n	<div class=\"wm-checkbox--skin wm-icon-checkmark\"></div>\n	<span class=\"wm-checkbox--text\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.badge : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " >"
    + alias3(((helper = (helper = helpers.text || (depth0 != null ? depth0.text : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"text","hash":{},"data":data}) : helper)))
    + "</span>\n</label>\n";
},"useData":true}));

Handlebars.registerPartial("completion", this["wm"]["templates"]["completion"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper;

  return "-"
    + this.escapeExpression(((helper = (helper = helpers.status || (depth0 != null ? depth0.status : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"status","hash":{},"data":data}) : helper)));
},"3":function(depth0,helpers,partials,data) {
    var helper;

  return this.escapeExpression(((helper = (helper = helpers.unit || (depth0 != null ? depth0.unit : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"unit","hash":{},"data":data}) : helper)));
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<div class=\"completion-bar "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.status : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " "
    + alias3(((helper = (helper = helpers.classlist || (depth0 != null ? depth0.classlist : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"classlist","hash":{},"data":data}) : helper)))
    + "\" data-completion-min=\""
    + alias3(((helper = (helper = helpers.min || (depth0 != null ? depth0.min : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"min","hash":{},"data":data}) : helper)))
    + "\" data-completion-max=\""
    + alias3(((helper = (helper = helpers.max || (depth0 != null ? depth0.max : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"max","hash":{},"data":data}) : helper)))
    + "\" data-completion-value=\""
    + alias3(((helper = (helper = helpers.value || (depth0 != null ? depth0.value : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"value","hash":{},"data":data}) : helper)))
    + "\">\n	<span class=\"completion-bar--name\">"
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "</span>\n	<span class=\"completion-bar--value\">"
    + alias3(((helper = (helper = helpers.value || (depth0 != null ? depth0.value : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"value","hash":{},"data":data}) : helper)))
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.unit : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</span>\n	<div class=\"completion-bar--bar\"></div>\n</div>\n";
},"useData":true}));

Handlebars.registerPartial("deliverablePreviewTemplate", this["wm"]["templates"]["deliverablePreviewTemplate"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "		<div class=\"modal-reject-container\">\n			<div class=\"rejectDeliverableModalError\"></div>\n			<textarea class=\"rejectDeliverableModalReason\" placeholder=\"Please input your reason for rejecting the file\" name=\"rejection_reason\" maxlength=\"200\"></textarea>\n		</div>\n		<div class=\"default-buttons\">\n			<button class=\"remove\">Delete</button>\n			<button class=\"reject-button\">\n				<!--icon-rejection.jsp-->\n				<svg version=\"1.1\" class=\"rejection-icon\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 12 12\" enable-background=\"new 0 0 12 12\" xml:space=\"preserve\">\n						<g>\n							<path d=\"M10.8,2.5L11.3,2c0.4-0.4,0.4-0.9,0-1.3s-0.9-0.4-1.3,0L9.5,1.2C8.5,0.4,7.3,0,6,0C2.7,0,0,2.7,0,6\n								c0,1.3,0.4,2.5,1.2,3.5L0.7,10c-0.4,0.4-0.4,0.9,0,1.3c0.2,0.2,0.4,0.3,0.7,0.3s0.5-0.1,0.7-0.3l0.4-0.4C3.5,11.6,4.7,12,6,12\n								c3.3,0,6-2.7,6-6C12,4.7,11.6,3.5,10.8,2.5z M1.8,6c0-2.3,1.9-4.2,4.2-4.2c0.8,0,1.6,0.2,2.2,0.6L2.5,8.2C2.1,7.6,1.8,6.8,1.8,6z\n								 M6,10.2c-0.8,0-1.6-0.2-2.2-0.6l5.7-5.7c0.4,0.6,0.6,1.4,0.6,2.2C10.2,8.3,8.3,10.2,6,10.2z\"/>\n						</g>\n					</svg>\n				Reject File\n			</button>\n		</div>\n		<div class=\"is-rejecting-buttons\">\n			<button class=\"cancel-rejection\">Cancel</button>\n			<button class=\"reject-asset-post\">\n				<!--icon-rejection.jsp-->\n				<svg version=\"1.1\" class=\"rejection-icon\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 12 12\" enable-background=\"new 0 0 12 12\" xml:space=\"preserve\">\n						<g>\n							<path d=\"M10.8,2.5L11.3,2c0.4-0.4,0.4-0.9,0-1.3s-0.9-0.4-1.3,0L9.5,1.2C8.5,0.4,7.3,0,6,0C2.7,0,0,2.7,0,6\n								c0,1.3,0.4,2.5,1.2,3.5L0.7,10c-0.4,0.4-0.4,0.9,0,1.3c0.2,0.2,0.4,0.3,0.7,0.3s0.5-0.1,0.7-0.3l0.4-0.4C3.5,11.6,4.7,12,6,12\n								c3.3,0,6-2.7,6-6C12,4.7,11.6,3.5,10.8,2.5z M1.8,6c0-2.3,1.9-4.2,4.2-4.2c0.8,0,1.6,0.2,2.2,0.6L2.5,8.2C2.1,7.6,1.8,6.8,1.8,6z\n								 M6,10.2c-0.8,0-1.6-0.2-2.2-0.6l5.7-5.7c0.4,0.6,0.6,1.4,0.6,2.2C10.2,8.3,8.3,10.2,6,10.2z\"/>\n						</g>\n					</svg>\n				Reject File\n			</button>\n		</div>\n";
},"3":function(depth0,helpers,partials,data) {
    return "		<div class=\"default-buttons\">\n			<button class=\"remove\">Delete</button>\n		</div>\n";
},"5":function(depth0,helpers,partials,data) {
    return "		<div class=\"update-deliverable\">\n			<button class=\"remove\">Delete</button>\n			<form class=\"update-deliverable-form\">\n				<button type=\"button\" class=\"update-deliverable-button wm-icon-upload\">\n					Update Deliverable\n					<input class=\"update-deliverable-input\" type=\"file\" name=\"files[]\">\n				</button>\n			</form>\n		</div>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"preview-not-available\">No preview available</div>\n<div class=\"deliverable-state-ribbon\">\n	<div class=\"updatedState\">\n		<!--icon-deliverables-updated.jsp-->\n		<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 64 64\" enable-background=\"new 0 0 64 64\" xml:space=\"preserve\">\n			<polygon fill=\"#198FCE\" points=\"62.6,64 0,1.4 31.2,1.4 62.6,32.8 \"/>\n			<text transform=\"matrix(0.7071 0.7071 -0.7071 0.7071 19.3148 10.6369)\" fill=\"#FFFFFF\" font-family=\"'Helvetica'\" font-size=\"12\">Updated</text>\n		</svg>\n	</div>\n	<div class=\"rejectedState\">\n		<!--icon-deliverables-rejected.jsp-->\n		<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 64 64\" enable-background=\"new 0 0 64 64\" xml:space=\"preserve\">\n			<polygon fill=\"#DF4343\" points=\"0,1.4 62.6,64 62.6,32.8 31.2,1.4 \"/>\n			<text transform=\"matrix(0.7071 0.7071 -0.7071 0.7071 20.0145 11.784)\" fill=\"#FFFFFF\" font-family=\"'Helvetica'\" font-size=\"12\">Rejected</text>\n		</svg>\n	</div>\n</div>\n\n<a class=\"download-button\" href=\"javascript:void(0);\">\n	<!--icon-deliverable-download.jsp-->\n	<svg class=\"deliverable-download\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.0\" x=\"0px\" y=\"0px\" viewBox=\"0 0 32 24\" enable-background=\"new 0 0 32 24\" xml:space=\"preserve\">\n	<path d=\"M29.167 24H2.833C1.269 24 0 22.7 0 21.2V2.8C0 1.3 1.3 0 2.8 0h26.333C30.731 0 32 1.3 32 2.8 v18.4C32 22.7 30.7 24 29.2 24z\" class=\"background\"/>\n		<path d=\"M18.025 16l-1.373 1.362c-0.261 0.259-0.608 0.402-0.977 0.4 c-0.369 0-0.716-0.143-0.976-0.401L13.323 16H8v3.528h16V16H18.025z M22.553 18.646h-2.465c-0.3 0-0.543-0.582-0.543-0.882 c0-0.3 0.243-0.882 0.543-0.882h2.465c0.3 0 0.5 0.6 0.5 0.882C23.095 18.1 22.9 18.6 22.6 18.646z\" class=\"foreground\"/>\n		<path d=\"M18.331 3h-5.582c-0.423 0-0.766 0.34-0.766 0.759v4.047l-1.677-0.002c-0.854 0-1.145 0.478-1.231 0.7 C8.99 8.7 8.9 9.2 9.5 9.835l5.048 5.002c0.276 0.3 0.6 0.4 1 0.425c0.391 0 0.759-0.151 1.035-0.426 l5.044-5.002c0.603-0.597 0.468-1.14 0.382-1.346c-0.086-0.206-0.378-0.684-1.231-0.684l-1.676 0.002V3.759 C19.096 3.3 18.8 3 18.3 3L18.331 3z\" class=\"foreground\"/>\n	</svg>\n</a>\n\n<div class=\"modal-button-box\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isAdminOrOwner : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.program(3, data, 0),"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.includeUpdateButton : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true}));

Handlebars.registerPartial("file-input", this["wm"]["templates"]["file-input"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "-inline";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<label class=\"wm-file-input "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.inline : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " "
    + alias3(((helper = (helper = helpers.classlist || (depth0 != null ? depth0.classlist : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"classlist","hash":{},"data":data}) : helper)))
    + "\">\n	<input type=\"file\" name=\""
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "\" />\n	<div class=\"wm-file-input--name\"></div>\n	"
    + ((stack1 = ((helper = (helper = helpers.text || (depth0 != null ? depth0.text : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"text","hash":{},"data":data}) : helper))) != null ? stack1 : "")
    + "\n</label>\n";
},"useData":true}));

Handlebars.registerPartial("filter", this["wm"]["templates"]["filter"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "checked";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<label class=\"filter\" id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\">\n	<input type=\"checkbox\" name=\""
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "\" value=\""
    + alias3(((helper = (helper = helpers.value || (depth0 != null ? depth0.value : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"value","hash":{},"data":data}) : helper)))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isChecked : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " "
    + alias3(((helper = (helper = helpers.checked || (depth0 != null ? depth0.checked : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"checked","hash":{},"data":data}) : helper)))
    + " />\n	<div class=\"filter--skin "
    + alias3(((helper = (helper = helpers.classlist || (depth0 != null ? depth0.classlist : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"classlist","hash":{},"data":data}) : helper)))
    + "\">"
    + alias3(((helper = (helper = helpers.text || (depth0 != null ? depth0.text : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"text","hash":{},"data":data}) : helper)))
    + "</div>\n</label>\n";
},"useData":true}));

Handlebars.registerPartial("icon", this["wm"]["templates"]["icon"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "<span class=\"active\">";
},"3":function(depth0,helpers,partials,data) {
    return "	<svg class=\"icon-graph\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 16 18\" enable-background=\"new 0 0 16 18\" xml:space=\"preserve\">\n		<image class=\"icon__shadow\" xlink:href=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABYAAAAYCAYAAAD+vg1LAAAACXBIWXMAAAsSAAALEgHS3X78AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAelJREFUeNq0ldlOwzAQReM2aQO0lJ0HViGE2CkSr/w8T/wAAsomsYl9q0pLgIQ76DoyEXFaJCwdxfFybc+MPcqxF2WgS2RgnZhWcsAFPvD4L2LvoAU++K9+W9C1LCh9fWAMDHMBEXsA5+DFWNwxFpRvaBP2KFglQ+AN7INtcMOF+znnHpzxG7gponnQBQbBDFgD46DJ/lNQBstglvNqYAs0ZNdJYREscJKYYJ4TR0GPHJEnKXLcAlg3fLLDfuUmRLspMk3RKncagEvwCC5AHfSCkmGKMhfLmc7L8eiyy1UKToEKeKaznojY+Jbjtdm0RhxlbiICxJ4rFJWwOaGYDqMmPR9mxH+84zzt5nPyAbgCdzyuOGmSgkWKe7Z7oIU/abdj7lDHqwgtgRGwyLEBoyJTOKJAkzts8F/vKqI9K5xTopNUlim0A8TLE4xdhxHwSvF8mpOyhH2KboI59h0xLv12hNKEPe5UYneDfR4d6P1VWB9P37hkwHcsmnzdFBfoyJa2N/dfipvIDCFj2mE9MuqdtMfCIQO/zjBzWG8Z9XbaA33dXSPdyAO9Z7wDh4wKKbscY2uvUUP+I50oCww384LIlb5mXZ7SgYz2HxlEGU70jMSpc1jAeqHN9jjnKUuqN1N8J+3ffV8CDACZ2peZC9axgQAAAABJRU5ErkJggg==\" transform=\"matrix(1 0 0 1 -3 -3)\"></image>\n		<path class=\"icon-graph__third icon--base\" d=\"M12,1.858v15.19c0,0.224,0.223,0.405,0.498,0.405h1.998c0.275,0,0.498-0.181,0.498-0.405V1.858c0-0.224-0.223-0.405-0.498-0.405h-1.998C12.223,1.453,12,1.634,12,1.858z\"/>\n		<path class=\"icon-graph__second icon--base\" d=\"M6,6.858v10.19c0,0.224,0.223,0.405,0.498,0.405h1.998c0.275,0,0.498-0.181,0.498-0.405V6.858c0-0.224-0.223-0.405-0.498-0.405H6.498C6.223,6.453,6,6.634,6,6.858z\"/>\n		<path class=\"icon-graph__first icon--base\" d=\"M0,10.858l0,6.19c0,0.224,0.223,0.405,0.498,0.405h1.998c0.275,0,0.498-0.181,0.498-0.405v-6.19c0-0.224-0.223-0.405-0.498-0.405H0.498C0.223,10.453,0,10.634,0,10.858z\"/>\n		<polygon class=\"icon-graph__arrow icon--highlight\" points=\"0.346,6.453 7.399,3.001 8.149,4.102 9.5,0 5.373,0 6.124,1.175\"/>\n	</svg>\n";
},"5":function(depth0,helpers,partials,data) {
    return "	<svg class=\"icon-team-agent\" version=\"1.1\" id=\"Layer_1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 24 24\" enable-background=\"new 0 0 24 24\" xml:space=\"preserve\">\n		<g id=\"blue_dot_54_\">\n			<linearGradient id=\"SVGID_1_\" gradientUnits=\"userSpaceOnUse\" x1=\"0\" y1=\"12\" x2=\"24\" y2=\"12\">\n				<stop  offset=\"0\" style=\"stop-color:#0A9FF3\"/>\n				<stop  offset=\"1\" style=\"stop-color:#158BE8\"/>\n			</linearGradient>\n			<circle fill=\"url(#SVGID_1_)\" cx=\"12\" cy=\"12\" r=\"12\"/>\n		</g>\n		<circle id=\"wh_dot_52_\" fill=\"#FFFFFF\" cx=\"12\" cy=\"12\" r=\"9\"/>\n		<g>\n			<path fill=\"#118FCE\" d=\"M9.8,15.7H8.2V9.6h-2V8.3h5.7v1.3h-2V15.7z\"/>\n			<path fill=\"#118FCE\" d=\"M17.1,15.7L16.5,14h-2.7l-0.5,1.8h-1.7l2.6-7.5h1.9l2.7,7.5H17.1z M16.2,12.6c-0.5-1.6-0.8-2.5-0.8-2.7 c-0.1-0.2-0.1-0.4-0.1-0.5c-0.1,0.4-0.4,1.5-1,3.2H16.2z\"/>\n		</g>\n	</svg>\n";
},"7":function(depth0,helpers,partials,data) {
    return "<svg version=\"1.1\" class=\"icon-internal\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 24 24\" enable-background=\"new 0 0 24 24\" xml:space=\"preserve\">\n	<g>\n		<path opacity=\"0.68\" fill=\"#4C5355\" d=\"M15.8,10c0-0.3,0-0.7,0-1.1c0-0.3,0-0.5,0-0.8C15.8,5.8,14,4,11.7,4C9.5,4,7.7,5.8,7.7,8.1\n		c0,0.8,0,1.4,0,1.9H9c0-0.5,0-1.1,0-1.9c0-1.5,1.3-2.8,2.8-2.8c1.5,0,2.8,1.3,2.8,2.8c0,0.3,0,0.6,0,0.8c0,0.4,0,0.7,0,1.1H15.8z\"\n		/>\n		<path fill=\"#4C5355\" d=\"M7.1,10c-0.5,0-0.9,0.4-0.9,0.9v6.4c0,0.5,0.4,0.9,0.9,0.9h9.2c0.5,0,0.9-0.4,0.9-0.9v-6.4\n		c0-0.5-0.4-0.9-0.9-0.9H7.1z M12.6,14.1v2.3c0,0.3-0.2,0.5-0.5,0.5h-0.7c-0.3,0-0.5-0.2-0.5-0.5v-2.3c-0.4-0.3-0.7-0.7-0.7-1.3\n		c0-0.8,0.7-1.5,1.5-1.5c0.8,0,1.5,0.7,1.5,1.5C13.3,13.3,13,13.8,12.6,14.1z\"/>\n	</g>\n</svg>\n\n";
},"9":function(depth0,helpers,partials,data) {
    return "	<svg version=\"1.1\" class=\"icon-assigned-worker\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\"\n	 viewBox=\"0 0 24 24\" enable-background=\"new 0 0 24 24\" xml:space=\"preserve\">\n	<g>\n		<path opacity=\"0.68\" fill=\"#4C5355\" d=\"M18.1,9.9l0-1.7C18.1,8.1,18,8,17.8,8l-0.7,0c-0.1,0-0.2,0.1-0.2,0.2l0,1.7l-1.7,0\n			c-0.1,0-0.2,0.1-0.2,0.2l0,0.7c0,0.1,0.1,0.2,0.2,0.2l1.7,0l0,1.7c0,0.1,0.1,0.2,0.2,0.2l0.7,0c0.1,0,0.2-0.1,0.2-0.2l0-1.7l1.7,0\n			c0.1,0,0.2-0.1,0.2-0.2l0-0.7c0-0.1-0.1-0.2-0.2-0.2L18.1,9.9z\"/>\n		<path fill=\"#4C5355\" d=\"M10.7,7c-1.4,0-2.5,1.3-2.5,3.1c0,0.9,0.4,1.8,1,2.5l0.2,0.2l-0.3,0.1c-0.7,0.2-1.5,0.6-2.3,1.1\n			C6,14.5,5,15.3,5,15.5v1C5,16.8,5.2,17,5.5,17h10.3c0.3,0,0.5-0.2,0.5-0.5v-1c-0.1-0.3-2-1.8-4.1-2.5l-0.3-0.1l0.2-0.2\n			c0.7-0.7,1-1.7,1-2.6C13.2,8.3,12.1,7,10.7,7z\"/>\n	</g>\n	</svg>\n";
},"11":function(depth0,helpers,partials,data) {
    return "	<svg version=\"1.1\" class=\"icon-public\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 24 24\" enable-background=\"new 0 0 24 24\" xml:space=\"preserve\">\n	<path fill=\"#4A4748\" d=\"M12,4c1.1,0,2.1,0.2,3.1,0.6s1.8,1,2.5,1.7c0.7,0.7,1.3,1.6,1.7,2.6c0.4,1,0.6,2,0.6,3.1\n		c0,1.1-0.2,2.1-0.6,3.1c-0.4,1-1,1.8-1.7,2.5c-0.7,0.7-1.6,1.3-2.5,1.7c-1,0.4-2,0.6-3.1,0.6c-1.1,0-2.1-0.2-3.1-0.6\n		c-1-0.4-1.8-1-2.5-1.7c-0.7-0.7-1.3-1.6-1.7-2.5c-0.4-1-0.6-2-0.6-3.1c0-1.1,0.2-2.1,0.6-3.1c0.4-1,1-1.8,1.7-2.6\n		c0.7-0.7,1.6-1.3,2.5-1.7C9.9,4.2,10.9,4,12,4 M8.8,5.7C8.5,5.7,8.3,5.8,8,6C7.7,6.2,7.4,6.4,7.2,6.7C6.9,7,6.6,7.3,6.4,7.6\n		C6.2,7.8,6,8.1,5.9,8.1h0c0,0,0.1,0.1,0.1,0.1c0.1,0,0.1,0,0.1,0.1c0,0,0,0.1,0,0.1c0,0,0,0.1,0.1,0.1c0,0,0,0,0.1-0.1c0,0,0,0,0,0\n		L6.2,8.6v0c0,0,0.1,0.1,0.1,0.1c0,0,0,0,0,0.1c0,0,0.1,0.2,0.1,0.2h0.1l0-0.2l0,0c0,0,0,0.1,0.1,0.1c0,0,0.1,0.1,0.1,0.1h0\n		c0-0.1,0-0.1,0-0.1c0,0,0,0,0,0.1L7,9v0L7,9.3c0,0,0,0.1,0,0.1c0,0,0,0.1-0.1,0.1c0,0,0,0,0,0c0,0,0,0,0-0.1c0,0,0-0.1,0-0.1\n		c0,0-0.1,0-0.2,0c0,0,0,0-0.1,0c0,0,0,0,0,0.1l0.1,0.3l0,0l0,0c-0.1,0-0.1,0.1-0.1,0.2c0,0.2,0,0.3,0,0.3l0,0.2L7,10.4v0l-0.4,0.2\n		L7,11.5H7c0-0.4,0-0.3,0-0.3c0,0,0,0.1,0,0.1l0.1,0.1c0,0.1,0,0.1,0,0.1c0,0,0.1,0.1,0.1,0.1c0,0.1,0.1,0.2,0.2,0.2\n		C7.6,11.9,7.7,12,7.7,12c0.1,0.2,0.1,0.3,0.2,0.5c0.1,0.2,0.2,0.3,0.3,0.4l0,0.1c0,0,0,0-0.1,0s0,0,0,0.1l0.2,0.1c0,0,0.1,0,0.1,0.1\n		c0,0.1,0.1,0.1,0.1,0.2l0,0.1l0.1,0.2l0.1,0l0-0.1c0-0.1-0.1-0.2-0.1-0.3c-0.1-0.1-0.1-0.2-0.2-0.3c-0.1-0.1-0.1-0.2-0.2-0.3\n		c0-0.1-0.1-0.1-0.1-0.1c0,0,0-0.1,0-0.2c0-0.1,0-0.1,0-0.2c0,0,0.1,0.1,0.2,0.1c0.1,0,0.1,0.1,0.1,0.1c0,0.2,0.1,0.3,0.2,0.4\n		c0.1,0.1,0.2,0.2,0.3,0.3c0,0,0,0,0,0c0,0,0.1,0,0.1,0c0,0,0,0.1,0,0.1c0.1,0.1,0.2,0.3,0.4,0.4c0.2,0.2,0.2,0.3,0.2,0.4v0l-0.1,0.1\n		c0,0.1,0.1,0.2,0.2,0.3c0.1,0.1,0.2,0.1,0.3,0.3h0c0.1,0,0.3,0,0.4,0.1c0.1,0.1,0.3,0.1,0.4,0.2l0.2-0.1c0.1,0,0.1,0,0.2,0.1\n		c0.1,0.1,0.1,0.1,0.2,0.2c0.1,0.1,0.2,0.1,0.3,0.2c0.1,0.1,0.2,0.1,0.4,0.1c0.1-0.1,0,0,0,0v0l0.4,0.3l0,0.1c0.1,0,0.1,0.1,0.2,0.2\n		c0.1,0.1,0.1,0.1,0.2,0.2h0c0.1,0,0.1,0,0.2,0c0,0.1,0.1,0.1,0.2,0.1c0,0,0.1,0,0.1-0.1c0-0.1,0-0.2,0-0.2c0,0,0-0.1,0.1-0.1\n		c0,0,0,0,0.1,0c0,0,0,0,0,0l0-0.1c0,0,0,0-0.1,0.1c0,0,0,0-0.1,0l-0.1,0.1l-0.2,0L12.8,16l0.1-0.6c0,0,0-0.1-0.1-0.1\n		c-0.1,0-0.1-0.1-0.1-0.1c-0.1-0.1-0.2-0.1-0.3-0.1c0,0-0.1,0-0.2,0c-0.1,0-0.2,0-0.2,0c0,0,0-0.1,0-0.2c0-0.1,0-0.1,0.1-0.2\n		c0-0.1,0-0.1,0.1-0.2c0-0.1,0-0.1,0-0.1l0.1-0.3l0,0l-0.2,0c0,0-0.1,0-0.1,0.1c-0.1,0-0.1,0.1-0.2,0.1c-0.1,0.1-0.1,0.1-0.1,0.2\n		c0,0.1-0.1,0.1-0.1,0.1l-0.4,0.1c-0.1,0-0.2,0-0.2-0.1c0-0.1-0.1-0.2-0.2-0.4c-0.1-0.1-0.1-0.2-0.1-0.3c0-0.2,0-0.3,0.1-0.4\n		c0.1-0.1,0-0.3-0.1-0.4c0,0,0,0,0.1,0c0,0,0,0,0-0.1l0.1-0.1l0,0l0,0c0.1-0.1,0.2-0.1,0.4-0.1c0.2,0,0.2,0,0.3-0.1l0.2,0.1\n		c0,0,0.1,0,0.1,0c0,0,0.1-0.1,0.1-0.1l-0.1,0l0.4-0.1l0,0.1l0.2,0l0.2,0.1c0,0,0.1,0,0.1,0c0,0,0.1,0,0.1,0l0.2,0.2\n		c0,0.1,0,0.1,0,0.1c0,0,0,0.1,0,0.1c0,0,0,0.1,0.1,0.3c0.1,0.1,0.1,0.2,0.2,0.2c0.1,0,0.1,0,0.1-0.1c0-0.1,0-0.1,0-0.2\n		c0-0.1,0-0.3-0.1-0.4c-0.1-0.1-0.1-0.3-0.4-0.4v-0.1c0-0.1,0.2-0.1,0.3-0.2c0.1-0.1,0.1-0.1,0.1-0.1c0.1-0.1,0.1-0.1,0.2-0.2\n		c0.1-0.1,0.1-0.1,0.2-0.2l0-0.2v0h0.2c0,0,0-0.1,0-0.1c0,0,0,0,0,0c0,0,0,0-0.1-0.1c0,0-0.1,0-0.1-0.1l0.1,0c0,0,0-0.1,0.1-0.2\n		c0-0.1,0-0.1,0-0.2l0.2-0.1c0,0,0,0.1,0,0.1c0,0,0.1,0,0.1,0l0.1-0.2c0-0.1,0-0.1-0.1-0.1c0,0,0,0,0.1-0.1c0.1,0,0.1-0.1,0.2-0.1\n		c0.1,0,0.1-0.1,0.2-0.1c0,0,0,0,0,0c0,0,0.1,0,0.1,0c0-0.1,0-0.1,0-0.1l0.1-0.2c0.1,0,0.2,0,0.2-0.1l0.2,0c0,0,0.2,0,0.2-0.1v0\n		l0.2-0.1l0-0.1l-0.1-0.1c0,0,0,0,0,0c0,0,0,0,0-0.1c0,0-0.1,0-0.1,0c0,0,0,0-0.1,0l0,0l0-0.3h0.1h0.1c0.1,0,0.1,0.3,0.1,0.2\n		c0-0.1,0-0.1-0.1-0.1c-0.1,0-0.3,0-0.4,0.1c-0.2,0.1-0.3,0.2-0.3,0.3l-0.1,0.1l0.2-0.2l0-0.1c0,0,0,0-0.1-0.1c-0.1,0-0.1,0-0.1,0\n		c0.1,0,0.2,0,0.3,0c0.1,0,0.1-0.1,0.2-0.1c0,0,0.1-0.1,0.1-0.1c0,0,0.1-0.1,0.2-0.1c0.2,0,0.3,0,0.4,0c0.1,0,0.3,0,0.4,0\n		c0,0,0.1-0.1,0.1-0.1c0,0,0.1-0.1,0.1-0.1l0.2,0c0,0,0.1,0,0.1,0C17,8.6,17,8.6,17,8.5c0-0.1,0-0.1-0.1-0.1c-0.1,0-0.1-0.1-0.1-0.1\n		c0,0,0,0,0-0.1c0,0,0,0,0,0c0,0-0.1,0-0.2,0.1c-0.1,0-0.2,0.1-0.2,0.1c0,0,0,0-0.1,0c0,0,0,0,0-0.1l0,0l0.1,0l0.2-0.1l0,0\n		c0,0,0-0.1-0.1-0.1c-0.1,0-0.1,0-0.1,0c0,0-0.1,0-0.1,0c-0.1,0-0.1,0-0.1,0l0,0c-0.1-0.1-0.2-0.1-0.2-0.2C16.1,7.7,16,7.6,16,7.6\n		c0,0,0-0.1,0-0.1c0,0,0,0-0.1,0c0,0-0.1,0-0.1,0c0,0,0,0,0-0.1c0,0,0-0.1-0.1-0.2c0-0.1-0.1-0.1-0.1-0.1l-0.1,0.1c0,0,0,0.1-0.1,0.1\n		c0,0-0.1,0-0.1,0h0l-0.2,0.2c0,0,0,0-0.1,0c0,0,0-0.2-0.1-0.2h0l0,0.2c0.1,0,0.1,0,0.1-0.1c0-0.1,0-0.1-0.1-0.1l-0.2,0c0,0,0,0,0,0\n		c0,0,0,0,0,0c0,0,0,0,0-0.1c0,0,0-0.1,0-0.1c0,0,0,0-0.1,0c0,0,0,0,0,0.2h0.1l0.1-0.2c0,0,0,0,0-0.1c0,0,0-0.1-0.1-0.1l-0.2,0\n		l-0.1-0.1c0,0,0,0-0.1,0c0,0-0.1-0.1-0.1-0.1l-0.2,0.1l-0.4-0.1c0,0-0.1,0-0.1,0c0,0,0,0,0,0.1c0,0,0,0,0,0.1c0,0,0,0,0,0.1\n		c0,0,0,0.1,0,0.2c0,0.1,0,0.2-0.1,0.1l-0.1,0.1c0,0,0,0,0.1,0.1c0,0,0.1,0,0.1,0.1c0,0,0.1,0.1,0.1,0.1c0,0,0,0.1,0,0.2l-0.1,0.3v0\n		c0,0.1-0.3,0.1-0.3,0.2c0,0,0.1,0.1,0.1,0.2c0.1,0,0.1,0.1,0.1,0.1c0,0,0,0-0.1,0.1c0,0-0.1,0-0.1,0.1c0,0-0.1,0-0.1,0s0,0.3,0,0.3\n		h-0.1h0c0-0.3,0-0.3,0-0.3c0,0,0,0,0,0l-0.4-0.1v0L13,8.4c0-0.1,0-0.1,0-0.1c0,0,0-0.1,0-0.1C13,8,13,8,12.9,8s-0.2,0-0.3,0\n		c0,0,0,0,0-0.1c0,0-0.1,0-0.1,0c-0.1,0-0.2,0-0.4-0.1c-0.1-0.1-0.3-0.1-0.4-0.1c0,0-0.1,0-0.1,0c-0.1,0-0.1,0-0.2,0c0,0,0,0,0.1-0.1\n		l-0.1-0.2l0,0c0,0-0.1,0-0.1,0c-0.1,0-0.1,0-0.1-0.1c0,0,0,0,0,0c0,0,0,0,0,0c0-0.1,0-0.2,0.1-0.2c0-0.1,0.1-0.1,0.1-0.2\n		c0,0,0-0.1,0-0.1c0,0,0,0,0,0c0.1,0,0.1,0,0.2,0c0.1,0,0.1-0.1,0.2-0.1l0-0.1c0,0-0.1,0-0.2-0.1c-0.1,0-0.2,0-0.2-0.1l0,0\n		c0.1,0,0.2,0.1,0.3,0.1c0.1,0,0.1,0,0.2,0c0,0,0.1,0,0.2-0.1c0.1,0,0.2-0.1,0.3-0.1c0,0-0.1-0.1-0.2-0.1c-0.1,0-0.2-0.1-0.3,0.3h0.1\n		c0,0,0.1-0.4,0.1-0.4c0,0,0.1,0,0.1,0c0,0,0,0,0.1,0.1c0,0,0.1,0,0.1,0l-0.2-0.1V6l0.3-0.1l0.2,0c0,0,0,0,0,0c0,0,0,0,0.1,0\n		c0,0,0.1,0,0.1,0.1c0,0,0.1,0.1,0.1,0.1l0.2-0.1c0,0,0,0,0.1,0c0.1,0,0.1,0,0-0.1L13,5.6c0,0,0,0,0,0c0,0,0,0,0,0\n		c0.1,0,0.1,0,0.1-0.1c-0.1,0-0.1-0.1-0.2-0.1c-0.1,0-0.1-0.1-0.2-0.1c0,0-0.1,0-0.1,0c0,0,0,0,0,0.1c0,0,0,0,0,0c0,0,0.1,0,0.1,0\n		c0,0,0,0,0,0c0,0-0.1,0-0.1,0c-0.1,0-0.1,0-0.2,0.1c0,0.1-0.1,0.1-0.2,0.2c0,0,0,0,0,0c0,0,0,0,0,0c0,0,0,0-0.1-0.1\n		c0,0-0.1,0-0.1-0.1c0,0,0-0.1,0.1-0.1c0-0.1,0-0.1-0.1-0.1c-0.1,0-0.1,0-0.1,0.1c0,0,0,0.1-0.1,0.1l-0.2-0.3l-0.2,0\n		c0-0.1,0-0.1,0-0.1c0,0,0-0.1-0.1-0.2c0,0-0.1-0.1-0.1-0.1c0,0-0.1,0-0.1,0c0,0,0,0-0.1,0C11,5,11,5,10.9,5.1c0,0-0.1,0.1-0.1,0.1\n		c0,0,0,0.5,0.1,0.5h0c0,0-0.1-0.4-0.1-0.3c0,0,0,0,0.1,0c0.1,0,0.1,0,0.1,0c0,0,0,0,0.1,0c0,0,0,0,0.1,0l0,0v0.1c0,0,0,0,0,0l0,0.1\n		l-0.2,0.1c0,0,0,0,0,0c0,0,0,0-0.1,0c0,0,0,0.1,0.1,0.1c0,0,0,0.1-0.1,0.1l-0.1,0c0-0.1-0.1-0.1-0.2-0.1c-0.1,0-0.2-0.1-0.4-0.1\n		c-0.2,0-0.3,0-0.5,0c-0.2,0-0.3,0-0.4,0L9.2,5.7l0.1,0.2c0,0,0,0-0.1,0c0,0,0,0,0,0.1c0,0-0.1-0.1-0.2-0.1C9,5.7,9,5.7,8.9,5.7\n		L8.8,5.7z M10,5.2c0,0-0.1,0-0.1,0c0,0-0.1,0-0.1,0c0,0-0.1,0-0.2,0c-0.1,0-0.2,0.1-0.3,0.1c-0.1,0-0.2,0.1-0.3,0.1\n		C9,5.5,8.9,5.5,8.9,5.5c0.1,0,0.2,0,0.3-0.1c0.1,0,0.2-0.1,0.2-0.1l0.1,0c0,0,0.1,0,0.1,0c0,0,0.1,0,0.1,0c0.1,0,0.1,0,0.2,0l-0.2,0\n		V5.4l0.3,0l0.1,0C10,5.4,10,5.3,10,5.2L10,5.2z M13.3,19C13.3,19,13.3,19.1,13.3,19c0.7-0.1,1.5-0.3,2.2-0.7\n		c0.7-0.4,1.3-0.8,1.8-1.8h0c-0.1,0.4-0.1,0.4-0.1,0.4L17,17l-0.1-0.4h0l0,0.5l0-0.1c0-0.1-0.1-0.1-0.1-0.2l0,0c0,0,0,0,0,0.1\n		c0-0.1,0-0.2-0.1-0.3c-0.1-0.1-0.2-0.1-0.3-0.1c0,0,0,0.1,0,0.1h0h-0.1h0.1l0-0.2l-0.1-0.1l0,0c-0.1,0-0.2-0.1-0.2-0.2l0,0l0,0l0,0\n		c-0.1,0-0.2,0.1-0.2,0.1c-0.1,0-0.1-0.1-0.2-0.1l-0.3,0c0,0,0-0.1,0-0.1c0,0-0.1-0.1-0.1-0.1c-0.1,0-0.1,0-0.2,0\n		c-0.1,0-0.1,0.1-0.1,0.1c0,0,0,0.1,0,0.1c0,0,0,0-0.1,0.1v0.1l0.1,0.1l0,0.2h0l-0.1-0.3l0.1-0.1c0,0,0-0.1,0-0.1c0,0,0-0.1,0-0.1\n		l0-0.2h-0.1h-0.2h-0.1l0,0.3c0,0,0,0,0,0c0,0,0,0,0,0l0-0.4h-0.1c-0.1,0.4-0.1,0.4-0.1,0.5l0,0L14,16.3l0,0l-0.1,0c0,0,0,0,0,0\n		c0,0-0.1,0-0.1,0v0v0.1v0v0l0,0c0-0.1-0.1-0.2-0.2,0h0.2v-0.2c-0.2,0-0.2,0.1-0.2,0.1c0,0,0.1,0.1,0.1,0.1c0,0,0,0-0.1,0\n		c0,0,0.1,0,0.1,0v0v0.2v0.4l0,0.1c0,0.1-0.1,0.2,0,0.3v0l-0.2,0l0,0l0,0.1l0.2,0v0l-0.2,0l0,0.1l-0.1,0c0,0-0.1,0.1-0.1,0.1\n		c0,0,0,0.1-0.1,0.1l0,0l-0.1,0.1c0,0,0,0,0,0.1c0,0,0,0,0,0.1l0,0.1l0.1,0l0,0l0-0.1l0,0.1c0,0,0,0.1,0,0.1c0,0-0.1,0.1-0.1,0.1\n		c0,0-0.1,0.1-0.1,0.2C13.2,18.9,13.2,18.9,13.3,19C13.2,19,13.3,19,13.3,19\"/>\n	</svg>\n";
},"13":function(depth0,helpers,partials,data) {
    return "<svg class=\"icon-fedex\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 36 36\" enable-background=\"new 0 0 36 36\" xml:space=\"preserve\">\n	<path class=\"icon--base\" d=\"M0,13.3h6.6v1.6H1.9V17h4.1v1.6H1.9v3.9H0V13.3z\"/>\n	<path class=\"icon--base\" d=\"M12.3,15.8c0.5,0.2,0.9,0.6,1.2,1c0.3,0.4,0.5,0.9,0.5,1.4c0,0.3,0.1,0.8,0.1,1.4H9c0,0.7,0.3,1.2,0.7,1.5c0.3,0.2,0.6,0.3,1,0.3c0.4,0,0.7-0.1,1-0.3c0.1-0.1,0.3-0.3,0.4-0.5H14c0,0.4-0.3,0.8-0.7,1.2c-0.6,0.7-1.5,1-2.6,1c-0.9,0-1.7-0.3-2.4-0.8c-0.7-0.6-1.1-1.5-1.1-2.8c0-1.2,0.3-2.1,0.9-2.7c0.6-0.6,1.5-1,2.5-1C11.2,15.5,11.8,15.6,12.3,15.8zM9.6,17.4c-0.3,0.3-0.4,0.6-0.5,1.1h3.1c0-0.5-0.2-0.8-0.5-1.1c-0.3-0.2-0.6-0.4-1.1-0.4C10.2,17,9.8,17.1,9.6,17.4z\"/>\n	<path class=\"icon--base\" d=\"M18.6,15.8c0.3,0.2,0.6,0.4,0.8,0.8v-3.3h3.5v9.3h-3.4v-1c-0.3,0.4-0.5,0.7-0.9,0.9c-0.3,0.2-0.7,0.3-1.2,0.3c-0.8,0-1.5-0.3-2-1c-0.5-0.7-0.8-1.5-0.8-2.5c0-1.2,0.3-2.1,0.8-2.8c0.5-0.7,1.3-1,2.2-1C17.9,15.5,18.3,15.6,18.6,15.8z M19.1,20.7c0.3-0.4,0.4-0.9,0.4-1.5c0-0.8-0.2-1.4-0.6-1.8c-0.3-0.2-0.6-0.3-0.9-0.3c-0.5,0-0.9,0.2-1.2,0.6s-0.4,0.9-0.4,1.5c0,0.6,0.1,1.1,0.4,1.5c0.3,0.4,0.6,0.6,1.1,0.6C18.4,21.3,18.8,21.1,19.1,20.7z\"/>\n	<path class=\"icon--highlight\" d=\"M28.3,14.9h-4.9v2h4.5v1.6h-4.5v2.4h5.2v1.7h-7.1v-9.3h6.8V14.9z\"/>\n	<path class=\"icon--highlight\" d=\"M29.2,22.6l2.4-3.5l-2.3-3.4h2.2l1.2,2l1.1-2h2.1L33.6,19l2.4,3.5h-2.2l-1.2-2.1l-1.2,2.1H29.2z\"/>\n</svg>\n";
},"15":function(depth0,helpers,partials,data) {
    return "<svg class=\"icon-dhl\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 36 36\" enable-background=\"new 0 0 36 36\" xml:space=\"preserve\">\n	<rect x=\"0\" y=\"9.2\" class=\"icon--base\" width=\"36\" height=\"18.8\"/>\n	<path class=\"icon--highlight\" d=\"M36,21.8c-0.1,0-0.1,0-0.2,0c-1.7,0-3.4,0-5.1,0c0,0,0,0-0.1,0c0-0.1,0.1-0.1,0.1-0.2c0.1-0.2,0.1-0.2,0.3-0.2c1.6,0,3.2,0,4.9,0C36,21.5,36,21.6,36,21.8z\"/>\n	<path class=\"icon--highlight\" d=\"M36,21.1c0,0-0.1,0-0.1,0c-1.5,0-3.1,0-4.6,0c0,0-0.1,0-0.1,0c0.1-0.1,0.1-0.2,0.2-0.3c0.1-0.1,0.2-0.1,0.3-0.1c1.4,0,2.9,0,4.3,0c0,0,0.1,0,0.1,0C36,20.8,36,21,36,21.1z\"/>\n	<path class=\"icon--highlight\" d=\"M36,20.4C36,20.4,35.9,20.4,36,20.4c-1.5,0-2.8,0-4.2,0c0,0-0.1,0-0.1,0c0.1-0.1,0.1-0.1,0.1-0.2c0.1-0.2,0.1-0.2,0.4-0.2c1.3,0,2.6,0,3.9,0C36,20.2,36,20.3,36,20.4z\"/>\n	<path class=\"icon--highlight\" d=\"M25.2,16.7c-0.1,0.1-0.1,0.2-0.2,0.3c-0.1,0.2-0.3,0.4-0.4,0.6c-0.3,0.4-0.6,0.8-0.9,1.2c-0.2,0.3-0.4,0.6-0.6,0.9c0,0.1-0.1,0.1-0.2,0.1c-0.4,0-0.7,0-1.1,0c-2.3,0-4.5,0-6.8,0c0,0-0.1,0-0.1,0c0-0.1,0.1-0.1,0.1-0.1c0.4-0.5,0.7-1,1.1-1.4c0.3-0.4,0.7-0.9,1-1.3c0.1-0.1,0.1-0.1,0.2-0.1c1,0,2,0,3.1,0c0,0,0.1,0,0.1,0c-0.4,0.6-0.8,1.1-1.3,1.7c0,0,0.1,0,0.1,0c0.4,0,0.9,0,1.3,0c0.1,0,0.1,0,0.1-0.1c0.3-0.4,0.7-0.9,1-1.3c0.1-0.1,0.1-0.2,0.2-0.2c0-0.1,0.1-0.1,0.2-0.1c0.9,0,1.7,0,2.6,0C24.8,16.6,25,16.6,25.2,16.7C25.2,16.6,25.2,16.7,25.2,16.7z\"/>\n	<path class=\"icon--highlight\" d=\"M5.2,18.2C5.3,18.1,5.3,18,5.4,18c0.2-0.3,0.4-0.6,0.7-0.9c0.1-0.1,0.2-0.2,0.3-0.4c0,0,0.1,0,0.1,0c0,0,0,0,0.1,0c2.1,0,4.3,0,6.4,0c0.3,0,0.7,0,1,0.2c0.3,0.1,0.5,0.3,0.7,0.6c0.2,0.3,0.2,0.5,0.2,0.8c0,0.1-0.1,0.3-0.1,0.4c-0.2,0.3-0.5,0.7-0.7,1c0,0.1-0.1,0.1-0.1,0.1c-0.4,0-0.9,0-1.3,0c-0.4,0-0.8,0-1.2,0c-0.1,0-0.2,0-0.2,0c-0.1,0-0.1-0.1-0.1-0.2c0.1-0.1,0.2-0.2,0.2-0.3c0.2-0.2,0.3-0.4,0.5-0.6c0.1-0.2,0-0.2-0.1-0.2c-0.1,0-0.1,0-0.2,0c-2,0-4,0-6.1,0C5.3,18.2,5.2,18.2,5.2,18.2z\"/>\n	<path class=\"icon--highlight\" d=\"M13.6,20c-0.1,0.1-0.1,0.1-0.1,0.2c-0.3,0.3-0.6,0.6-0.9,0.9c-0.2,0.2-0.5,0.3-0.8,0.4c-0.3,0.1-0.6,0.2-0.9,0.2c-0.3,0-0.5,0-0.8,0c-1.7,0-3.4,0-5.2,0c0,0-0.1,0-0.1,0c0-0.1,0.1-0.1,0.1-0.2c0.5-0.7,1-1.3,1.5-2C6.5,19.3,6.7,19,7,18.7c0.1-0.1,0.1-0.1,0.2-0.1c0.9,0,1.8,0,2.7,0c0.1,0,0.1,0,0.2,0c0.1,0,0.1,0.1,0.1,0.2c0,0.1-0.1,0.1-0.1,0.2c-0.2,0.3-0.4,0.5-0.6,0.8c0,0.1-0.1,0.1,0,0.2c0,0,0.1,0.1,0.2,0.1c0.1,0,0.2,0,0.2,0c1.2,0,2.4,0,3.6,0C13.5,20,13.5,20,13.6,20z\"/>\n	<path class=\"icon--highlight\" d=\"M31.3,20c0,0.1-0.1,0.1-0.1,0.2c-0.4,0.5-0.7,1-1.1,1.5c0,0.1-0.1,0.1-0.1,0.1c-1.2,0-2.5,0-3.7,0c-0.5,0-1,0-1.6,0c-0.3,0-0.6,0-0.8-0.1c-0.1-0.1-0.3-0.1-0.4-0.2c-0.2-0.2-0.2-0.5,0-0.8c0.1-0.2,0.2-0.3,0.3-0.5c0-0.1,0.1-0.1,0.1-0.1c2.3,0,4.6,0,6.9,0c0.1,0,0.3,0,0.4,0C31.2,20,31.3,20,31.3,20z\"/>\n	<path class=\"icon--highlight\" d=\"M24.1,19.6c0-0.1,0.1-0.1,0.1-0.2c0.3-0.3,0.5-0.7,0.8-1c0.3-0.4,0.5-0.7,0.8-1.1c0.2-0.2,0.3-0.4,0.5-0.7c0,0,0.1-0.1,0.1-0.1c1.1,0,2.2,0,3.3,0c0,0,0.1,0,0.1,0c-0.1,0.1-0.1,0.2-0.2,0.3c-0.4,0.5-0.8,1.1-1.2,1.6c-0.3,0.3-0.5,0.7-0.8,1c-0.1,0.1-0.1,0.1-0.3,0.1c-1.1,0-2.1,0-3.2,0C24.2,19.7,24.1,19.7,24.1,19.6C24.1,19.7,24.1,19.7,24.1,19.6z\"/>\n	<path class=\"icon--highlight\" d=\"M22.7,20.1c-0.1,0.1-0.2,0.2-0.2,0.3c-0.3,0.4-0.6,0.9-1,1.3c0,0-0.1,0.1-0.1,0.1c-1,0-2.1,0-3.1,0c0,0,0,0-0.1,0c0,0,0-0.1,0.1-0.1c0.2-0.3,0.5-0.7,0.8-1c0.1-0.2,0.3-0.4,0.4-0.6c0,0,0.1,0,0.1,0C20.5,20,21.6,20,22.7,20.1C22.7,20,22.7,20,22.7,20.1C22.7,20,22.7,20,22.7,20.1z\"/>\n	<path class=\"icon--highlight\" d=\"M18,20c0,0.1-0.1,0.1-0.1,0.1c-0.3,0.4-0.6,0.8-0.9,1.2c-0.1,0.1-0.1,0.2-0.2,0.3c0,0,0,0-0.1,0c0,0-0.1,0-0.1,0c-1,0-2,0-3,0c0,0-0.1,0-0.1,0c0,0,0-0.1,0-0.1c0.3-0.4,0.6-0.8,0.8-1.1c0.1-0.1,0.2-0.3,0.3-0.4c0-0.1,0.1-0.1,0.1-0.1c1,0,2,0,3,0C17.8,20,17.9,20,18,20z\"/>\n	<path class=\"icon--highlight\" d=\"M5.4,20c-0.1,0.1-0.2,0.2-0.3,0.4c0,0,0,0-0.1,0c-1.7,0-3.3,0-5,0c-0.1,0-0.1,0-0.1-0.1c0-0.1,0-0.2,0-0.2C0,20,0,20,0.1,20c0,0,0,0,0.1,0c1.7,0,3.4,0,5.1,0C5.3,20,5.3,20,5.4,20z\"/>\n	<path class=\"icon--highlight\" d=\"M4.9,20.7c-0.1,0.1-0.2,0.2-0.2,0.3c0,0,0,0-0.1,0c0,0-0.1,0-0.1,0c-1.4,0-2.9,0-4.3,0C0,21.1,0,21.1,0,21c0-0.1,0-0.1,0-0.2c0-0.1,0-0.1,0.1-0.1c0,0,0,0,0,0c1.5,0,3.1,0,4.6,0C4.8,20.7,4.8,20.7,4.9,20.7z\"/>\n	<path class=\"icon--highlight\" d=\"M4.4,21.4c-0.1,0.1-0.2,0.2-0.3,0.4c0,0-0.1,0-0.1,0c-0.5,0-1,0-1.6,0c-0.8,0-1.6,0-2.4,0c0,0-0.1,0-0.1,0c0-0.1,0-0.2,0-0.3c0,0,0,0,0,0c0,0,0.1,0,0.1,0c1.4,0,2.8,0,4.1,0C4.3,21.4,4.3,21.4,4.4,21.4z\"/>\n</svg>\n";
},"17":function(depth0,helpers,partials,data) {
    return "<svg class=\"icon-usps\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 36 36\" enable-background=\"new 0 0 36 36\" xml:space=\"preserve\">\n	<path class=\"icon--highlight\" d=\"M35.9,7.4c-0.1,0.3-0.1,0.6-0.2,0.9c-0.6,2.6-1.1,5.3-1.6,7.9c-0.7,3.4-1.4,6.8-2.1,10.2c-0.2,0.9-0.4,1.7-0.5,2.6c-0.1,0.3,0,0.3-0.3,0.3c-7.4,0-14.9,0-22.3,0c-2,0-4,0-6,0c-0.1,0-0.1,0-0.2,0c0,0,0,0,0,0c0.2-0.1,0.5-0.2,0.7-0.3c3.2-1.2,6.4-2.5,9.5-3.7c3.1-1.2,6.1-2.4,9.2-3.6c1.8-0.7,3.5-1.4,5.3-2.1c0.1,0,0.2-0.1,0.3-0.2c0.3-0.2,0.7-0.4,1.1-0.4c0.3,0,0.6-0.1,0.9-0.3c1.3-0.8,1.9-1.9,2-3.4c0-0.3,0-0.7-0.2-1c-0.1-0.3-0.4-0.5-0.7-0.5c-0.9-0.1-1.7-0.1-2.6,0c0,0,0,0-0.1,0c-0.2,0-0.4,0-0.6-0.2c-0.2-0.3-0.5-0.5-0.8-0.7c-0.7-0.4-1.4-0.5-2.1-0.7c-1.9-0.5-3.9-1-5.8-1.4c-4-1-8.1-2-12.1-3c-0.7-0.2-1.3-0.3-2-0.5c0.1,0,0.2,0,0.3,0c10.2,0,20.4,0,30.6,0C35.8,7.3,35.9,7.3,35.9,7.4C35.9,7.3,35.9,7.3,35.9,7.4z\"/>\n	<path class=\"icon--base\" d=\"M13.7,14.4c0.7,2,1.4,4,2.2,6c0.1,0,0.1,0,0.1,0c1.5-0.7,3-1.3,4.5-1.7c2.4-0.7,4.9-1.4,7.3-1.8c0.2,0,0.4-0.1,0.6-0.1c0.3,0,0.5,0.1,0.6,0.3c0.1,0.3-0.1,0.5-0.4,0.5c-0.3,0-0.6,0.1-0.9,0.1c-1.6,0.3-3.2,0.7-4.7,1.2c-3.4,1.1-6.7,2.4-9.9,3.9c-2.8,1.3-5.5,2.6-8.2,3.9c-1.6,0.8-3.1,1.6-4.7,2.4c-0.1,0.1-0.2,0.1-0.4,0.2c0.1-0.3,0.1-0.5,0.1-0.7c0.4-2,0.8-4,1.3-6c0.7-3.1,1.3-6.3,2-9.4c0-0.2,0.1-0.3,0.3-0.3c0.6,0,1.2-0.1,1.9-0.1c2.2,0,4.5,0,6.7,0c2.1,0,4.1,0,6.2,0.1c1.6,0,3.2,0.1,4.8,0.2c1,0.1,2,0.2,3,0.6c0.4,0.2,0.8,0.4,1.1,0.8c0,0.1,0.1,0.1,0.2,0.1c0.8,0,1.6,0,2.4,0c0.2,0,0.5,0,0.7,0.1c0.3,0.1,0.4,0.3,0.4,0.5c0,0.2,0,0.4-0.1,0.5c-0.2,0.6-0.4,1.2-0.8,1.8c0,0.1-0.1,0.1-0.2,0.2c-0.1,0-0.2,0-0.2-0.1c0-0.1,0-0.2,0-0.2c0.1-0.3,0.2-0.7,0.3-1c0-0.2,0-0.4,0-0.6c0-0.1-0.1-0.2-0.2-0.2c-0.2,0-0.3,0-0.5,0c-0.9,0-1.8,0.1-2.7,0.2c-0.7,0.1-1.4,0.2-2.2,0.2c-0.2,0-0.3,0-0.5-0.1c-0.2-0.1-0.2-0.2,0-0.3c0.1,0,0.1,0,0.2-0.1c0.5-0.1,0.9-0.3,1.4-0.4c0.1,0,0.3-0.2,0.3-0.3c0.1-0.1,0.1-0.2-0.1-0.2c-0.1,0-0.1,0-0.2,0c-3.9,0-7.9,0-11.8,0C13.9,14.4,13.9,14.4,13.7,14.4z\"/>\n</svg>\n";
},"19":function(depth0,helpers,partials,data) {
    return "<svg class=\"icon-ups\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 36 36\" enable-background=\"new 0 0 36 36\" xml:space=\"preserve\">\n	<path class=\"icon--base\" d=\"M34,12.8c0,2.9,0.1,5.8,0,8.6c-0.1,4-2,7.1-5.4,9.2c-3,1.8-6.2,3.2-9.4,4.6c-0.2,0.1-0.4,0.1-0.6,0c-3.2-1.4-6.5-2.8-9.5-4.7c-3.6-2.2-5.3-5.4-5.4-9.5c-0.1-5.8,0-11.6,0-17.3c0-0.3,0.1-0.5,0.4-0.6c2.8-1.4,5.9-2.2,9-2.7c2.9-0.4,5.9-0.5,8.9-0.3c4.1,0.3,8,1.2,11.7,3C33.9,3.2,34,3.4,34,3.8C34,6.8,34,9.8,34,12.8C34,12.8,34,12.8,34,12.8zM33.6,12.6c0-2.8,0-5.7,0-8.5c0-0.5-0.2-0.7-0.6-0.9C30,1.8,26.7,1,23.4,0.6c-3.3-0.4-6.7-0.4-10,0.1c-3.1,0.4-6.2,1.2-9,2.6C4.1,3.5,3.9,3.7,3.9,4c0,5.6,0,11.2,0,16.8c0,4.2,1.7,7.5,5.4,9.5c3,1.6,6.1,3,9.1,4.5c0.2,0.1,0.5,0,0.7-0.1c2.3-1.1,4.6-2.1,6.9-3.2c1.5-0.8,3-1.6,4.3-2.8c2.2-2.1,3.2-4.8,3.2-7.9C33.6,18.2,33.6,15.4,33.6,12.6z\"/>\n	<path class=\"icon--highlight\" d=\"M33.6,12.6c0,2.8,0,5.5,0,8.3c0,3-1,5.7-3.2,7.9c-1.2,1.2-2.7,2-4.3,2.8c-2.3,1.1-4.6,2.2-6.9,3.2c-0.2,0.1-0.5,0.2-0.7,0.1c-3-1.5-6.1-2.8-9.1-4.5C5.7,28.4,4,25,4,20.9C3.9,15.2,4,9.6,3.9,4c0-0.3,0.1-0.5,0.4-0.7c2.8-1.4,5.9-2.2,9-2.6c3.3-0.5,6.7-0.5,10-0.1C26.7,1,30,1.8,33.1,3.3c0.4,0.2,0.6,0.4,0.6,0.9C33.6,7,33.6,9.8,33.6,12.6zM32.5,3.8c-0.7-0.1-1.4-0.1-2-0.1c-4.4-0.3-8.8-0.2-13.1,0.9c-4.4,1-8.3,3-11.8,5.9c-0.3,0.3-0.5,0.6-0.5,1.1c0,3,0,6,0,9.1c0,3.9,1.4,7,4.9,8.9c2.8,1.5,5.7,2.8,8.5,4.2c0.2,0.1,0.4,0.1,0.6,0c2.6-1.3,5.3-2.5,7.9-3.8c3.7-1.9,5.5-5.1,5.5-9.3c0-5.4,0-10.7,0-16.1C32.5,4.2,32.5,4,32.5,3.8z\"/>\n	<path class=\"icon--base\" d=\"M32.5,3.8c0,0.2,0,0.5,0,0.7c0,5.4,0,10.7,0,16.1c0,4.2-1.7,7.3-5.5,9.3c-2.6,1.3-5.3,2.6-7.9,3.8c-0.2,0.1-0.4,0.1-0.6,0c-2.9-1.4-5.8-2.7-8.5-4.2c-3.5-1.9-4.9-5-4.9-8.9c0-3,0-6,0-9.1c0-0.5,0.2-0.8,0.5-1.1C9,7.5,13,5.6,17.4,4.5c4.3-1,8.7-1.2,13.1-0.9C31.1,3.7,31.8,3.7,32.5,3.8z M18.2,23.7c0.1,0,0.2,0,0.2,0c2.5,0.5,4.3-0.9,5-3.1c0.6-1.9,0.7-3.9,0.1-5.8c-0.4-1.5-1.3-2.6-2.8-3.1c-1.7-0.5-3.3-0.3-4.8,0.4c-0.3,0.2-0.3,0.4-0.3,0.7c0,5.2,0,10.4,0,15.6c0,0.2,0,0.4,0,0.5c0.9,0,1.7,0,2.5,0C18.2,27.2,18.2,25.5,18.2,23.7z M11.7,11.7c0,0.3,0,0.6,0,0.9c0,2.8,0,5.6,0,8.3c0,0.2-0.1,0.5-0.3,0.6c-1.2,0.6-2.2,0-2.3-1.4c0-0.2,0-0.3,0-0.5c0-2.4,0-4.9,0-7.3c0-0.2,0-0.4,0-0.6c-0.9,0-1.7,0-2.5,0c0,0.2,0,0.4,0,0.6c0,1.8,0,3.6,0,5.5c0,0.9,0,1.9,0,2.8c0.1,1.6,1,2.8,2.5,3.1c1.7,0.4,3.3,0.2,4.8-0.6c0.4-0.2,0.5-0.4,0.5-0.8c0-3.3,0-6.7,0-10c0-0.2,0-0.4,0-0.6C13.4,11.7,12.6,11.7,11.7,11.7z M31,14.4c0-0.7,0-1.2,0-1.7c0-0.3-0.1-0.5-0.4-0.7c-0.9-0.5-1.8-0.6-2.8-0.5c-1.9,0.2-3.3,1.7-3.1,3.7c0.1,1.3,0.8,2.1,1.8,2.8c0.6,0.4,1.2,0.7,1.7,1.1c0.6,0.5,0.8,1.2,0.5,1.9c-0.2,0.6-0.9,0.9-1.7,0.8c-0.8-0.2-1.5-0.5-2.3-0.7c0,0.5,0,1.1,0,1.7c0,0.2,0.2,0.4,0.3,0.5c1,0.5,2.1,0.7,3.2,0.6c1.7-0.2,2.9-1.5,3-3.2c0.1-1.5-0.5-2.6-1.7-3.5c-0.7-0.5-1.4-0.8-2-1.3c-0.6-0.4-0.7-1-0.5-1.6c0.2-0.5,0.6-0.8,1.3-0.8C29.4,13.4,30.2,13.8,31,14.4z\"/>\n	<path class=\"icon--highlight\" d=\"M18.2,23.7c0,1.8,0,3.5,0,5.3c-0.9,0-1.7,0-2.5,0c0-0.2,0-0.4,0-0.5c0-5.2,0-10.4,0-15.6c0-0.3,0-0.5,0.3-0.7c1.6-0.7,3.2-1,4.8-0.4c1.5,0.5,2.4,1.6,2.8,3.1c0.6,1.9,0.5,3.9-0.1,5.8c-0.7,2.2-2.5,3.6-5,3.1C18.4,23.7,18.4,23.7,18.2,23.7z M18.3,17.6c0,1.2,0,2.4,0,3.6c0,0.4,0.1,0.5,0.5,0.5c1.1,0.1,1.9-0.2,2.2-1.3c0.6-1.9,0.6-3.8,0-5.6c-0.3-1-1.2-1.4-2.3-1.2c-0.3,0.1-0.4,0.2-0.4,0.6C18.3,15.2,18.3,16.4,18.3,17.6z\"/>\n	<path class=\"icon--highlight\" d=\"M11.7,11.7c0.9,0,1.7,0,2.5,0c0,0.2,0,0.4,0,0.6c0,3.3,0,6.7,0,10c0,0.4-0.1,0.6-0.5,0.8c-1.5,0.7-3.2,0.9-4.8,0.6c-1.4-0.3-2.3-1.5-2.5-3.1c-0.1-0.9,0-1.9,0-2.8c0-1.8,0-3.6,0-5.5c0-0.2,0-0.4,0-0.6c0.9,0,1.7,0,2.5,0c0,0.2,0,0.4,0,0.6c0,2.4,0,4.9,0,7.3c0,0.2,0,0.3,0,0.5c0.1,1.4,1.1,1.9,2.3,1.4c0.2-0.1,0.3-0.4,0.3-0.6c0-2.8,0-5.6,0-8.3C11.7,12.3,11.7,12,11.7,11.7z\"/>\n	<path class=\"icon--highlight\" d=\"M31,14.4c-0.8-0.6-1.6-1-2.5-1c-0.6,0-1.1,0.3-1.3,0.8c-0.2,0.6-0.1,1.2,0.5,1.6c0.6,0.5,1.4,0.8,2,1.3c1.2,0.8,1.8,2,1.7,3.5c-0.1,1.7-1.4,3-3,3.2c-1.1,0.1-2.2,0-3.2-0.6c-0.2-0.1-0.3-0.3-0.3-0.5c0-0.6,0-1.2,0-1.7c0.8,0.3,1.5,0.6,2.3,0.7c0.8,0.2,1.4-0.2,1.7-0.8c0.3-0.7,0.1-1.4-0.5-1.9c-0.6-0.4-1.2-0.8-1.7-1.1c-1-0.7-1.7-1.5-1.8-2.8c-0.1-1.9,1.2-3.5,3.1-3.7c1-0.1,1.9,0.1,2.8,0.5c0.3,0.1,0.4,0.3,0.4,0.7C31,13.2,31,13.7,31,14.4z\"/>\n	<path class=\"icon--base\" d=\"M18.3,17.6c0-1.2,0-2.3,0-3.5c0-0.3,0.1-0.5,0.4-0.6c1.1-0.2,2,0.2,2.3,1.2c0.6,1.9,0.6,3.8,0,5.6c-0.4,1.1-1.1,1.5-2.2,1.3c-0.3,0-0.5-0.2-0.5-0.5C18.3,20,18.3,18.8,18.3,17.6z\"/>\n</svg>\n";
},"21":function(depth0,helpers,partials,data) {
    return "<svg class=\"icon-truck\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 36 36\" enable-background=\"new 0 0 36 36\" xml:space=\"preserve\">\n	<g class=\"-is-sent\">\n		<path fill=\"#939799\" d=\"M34.4,15.9c-1.3-1.7-3.2-2.8-4.8-3.4c-0.8-0.3-1.8-0.5-2.6-0.5h-0.3c-0.7,0-0.5,0.6-0.5,1.3v9.4c0,0.7-0.2,2.2,0.5,2.2h6.8c0.7,0,1.7-1.5,1.7-2.2v-5.1C35.2,17.1,34.9,16.5,34.4,15.9z\"/>\n		<circle fill=\"#5D5D5D\" stroke=\"#FFFFFF\" stroke-miterlimit=\"10\" cx=\"30.2\" cy=\"24.2\" r=\"2.4\"/>\n		<path fill=\"#B4B7B7\" d=\"M22.4,9H11.3c-1.1,0-2,0.9-2,2v11.2c0,1.1,0.9,2,2,2h11.2c1.1,0,2-0.9,2-2V11C24.4,9.9,23.5,9,22.4,9z\"/>\n		<circle fill=\"#5D5D5D\" stroke=\"#FFFFFF\" stroke-miterlimit=\"10\" cx=\"16.8\" cy=\"24.2\" r=\"2.4\"/>\n		<path fill=\"#FFFFFF\" d=\"M32.5,16.4c-1.1-2-2.8-2.6-4-2.6h-0.1c-0.5,0-1,0.4-1,1v1.5c0,0.3,0.3,0.6,0.6,0.6h4.2C32.6,16.9,32.7,16.6,32.5,16.4L32.5,16.4z\"/>\n		<line fill=\"none\" stroke=\"#939799\" stroke-linecap=\"square\" stroke-miterlimit=\"10\" x1=\"7.2\" y1=\"12.6\" x2=\"1.2\" y2=\"12.6\"/>\n		<line fill=\"none\" stroke=\"#939799\" stroke-linecap=\"square\" stroke-miterlimit=\"10\" x1=\"7.2\" y1=\"15.6\" x2=\"3.2\" y2=\"15.6\"/>\n		<line fill=\"none\" stroke=\"#939799\" stroke-linecap=\"square\" stroke-miterlimit=\"10\" x1=\"7.2\" y1=\"18.6\" x2=\"5.2\" y2=\"18.6\"/>\n	</g>\n	<g class=\"-is-return\">\n		<line fill=\"none\" stroke=\"#939799\" stroke-linecap=\"square\" stroke-miterlimit=\"10\" x1=\"25\" y1=\"3.7\" x2=\"33\" y2=\"3.7\"/>\n		<line fill=\"none\" stroke=\"#939799\" stroke-linecap=\"square\" stroke-miterlimit=\"10\" x1=\"25\" y1=\"6.7\" x2=\"31\" y2=\"6.7\"/>\n		<line fill=\"none\" stroke=\"#939799\" stroke-linecap=\"square\" stroke-miterlimit=\"10\" x1=\"25\" y1=\"9.7\" x2=\"27\" y2=\"9.7\"/>\n		<path fill=\"#939799\" d=\"M0,9.3v5.1c0,0.7,0.9,1.8,1.7,1.8h6.8c0.7,0,0.5-1.1,0.5-1.8V5c0-0.7,0.2-1.8-0.5-1.8H8.2c-0.9,0-1.8,0.4-2.6,0.7C4,4.4,2.1,5.7,0.8,7.5C0.4,8,0,8.6,0,9.3z\"/>\n		<circle fill=\"#5D5D5D\" stroke=\"#FFFFFF\" stroke-miterlimit=\"10\" cx=\"5\" cy=\"15.7\" r=\"2.4\"/>\n		<path fill=\"#B4B7B7\" stroke=\"#FFFFFF\" stroke-miterlimit=\"10\" d=\"M9.8,2.5v11.2c0,1.1,0.9,2,2,2H23c1.1,0,2-0.9,2-2V2.5c0-1.1-0.9-2-2-2H11.8C10.7,0.5,9.8,1.4,9.8,2.5z\"/>\n		<circle fill=\"#5D5D5D\" stroke=\"#FFFFFF\" stroke-miterlimit=\"10\" cx=\"17.4\" cy=\"15.7\" r=\"2.4\"/>\n		<path fill=\"#FFFFFF\" d=\"M2.7,7.9c1.1-2,2.8-2.6,4-2.6h0.1c0.5,0,1,0.4,1,1v1.5c0,0.3-0.3,0.6-0.6,0.6H3C2.7,8.4,2.5,8.1,2.7,7.9L2.7,7.9z\"/>\n	</g>\n</svg>\n";
},"23":function(depth0,helpers,partials,data) {
    return "</span>";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.active : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.name : depth0),"graph",{"name":"eq","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.name : depth0),"team-agent",{"name":"eq","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.name : depth0),"internal",{"name":"eq","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.name : depth0),"assigned-worker",{"name":"eq","hash":{},"fn":this.program(9, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.name : depth0),"public",{"name":"eq","hash":{},"fn":this.program(11, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.name : depth0),"fedex",{"name":"eq","hash":{},"fn":this.program(13, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.name : depth0),"dhl",{"name":"eq","hash":{},"fn":this.program(15, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.name : depth0),"usps",{"name":"eq","hash":{},"fn":this.program(17, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.name : depth0),"ups",{"name":"eq","hash":{},"fn":this.program(19, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.name : depth0),"truck",{"name":"eq","hash":{},"fn":this.program(21, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.active : depth0),{"name":"if","hash":{},"fn":this.program(23, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"useData":true}));

Handlebars.registerPartial("label", this["wm"]["templates"]["label"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper;

  return "style=\"background-color: "
    + this.escapeExpression(((helper = (helper = helpers.color || (depth0 != null ? depth0.color : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"color","hash":{},"data":data}) : helper)))
    + "\"";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<div class=\"wm-label -"
    + alias3(((helper = (helper = helpers.status || (depth0 != null ? depth0.status : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"status","hash":{},"data":data}) : helper)))
    + "\" id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.color : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">"
    + ((stack1 = ((helper = (helper = helpers.text || (depth0 != null ? depth0.text : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"text","hash":{},"data":data}) : helper))) != null ? stack1 : "")
    + "</div>\n";
},"useData":true}));

Handlebars.registerPartial("logo", this["wm"]["templates"]["logo"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper;

  return "-"
    + this.escapeExpression(((helper = (helper = helpers.color || (depth0 != null ? depth0.color : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"color","hash":{},"data":data}) : helper)));
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<svg class=\"logo-work-market "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.color : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\" version=\"1.1\" xmlns:sketch=\"http://www.bohemiancoding.com/sketch/ns\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"4 77 208 48\" enable-background=\"new 4 77 208 48\" xml:space=\"preserve\">\n	<g class=\"logo-work-market--market\" transform=\"translate(120.000000, 9.000000)\" opacity=\"0.68\">\n		<path d=\"M21.7,83.8h-3c-0.5,0-0.6,0.2-0.8,0.7l-2.1,7.2c-0.2,0.8-0.4,1.5-0.4,1.6c0-0.2-0.1-1-0.3-1.6l-1.7-7.2c-0.1-0.5-0.2-0.7-0.8-0.7h-3c-0.5,0-0.6,0.2-0.8,0.7L5,98.3c-0.2,0.5,0.2,0.8,0.7,0.8h2c0.6,0,0.7-0.3,0.9-0.8l2.1-8c0.2-0.6,0.3-1.4,0.3-1.4c0,0.1,0.1,0.7,0.3,1.4l1.9,8c0.1,0.5,0.3,0.8,0.9,0.8h2.4c0.5,0,0.7-0.3,0.8-0.8l2.2-8c0.2-0.5,0.4-1.4,0.4-1.5c0,0.1,0.1,0.8,0.2,1.5l2.1,8c0.1,0.5,0.3,0.8,0.9,0.8h2.4c0.6,0,0.9-0.2,0.7-0.8l-3.7-13.7C22.4,84.1,22.2,83.8,21.7,83.8\"/>\n		<path d=\"M32.7,96.1c-0.9,0-1.5-0.3-1.5-1.2v-0.5c0-0.8,0.3-1.4,1.5-1.4h3.4v1.7C35.4,95.3,34.1,96.1,32.7,96.1 M33.9,83.6c-4.4,0-6.2,1.7-6.2,4.4v0.4c0,0.5,0.2,0.8,0.7,0.8h2.3c0.5,0,0.7-0.2,0.7-0.8v-0.2c0-1.1,0.6-1.6,2.4-1.6c1.8,0,2.3,0.6,2.3,1.7v2h-4.2c-3,0-4.6,1.8-4.6,3.8v1.4c0,2,1.4,3.8,4.4,3.8c2.1,0,3.6-0.9,4.5-1.7l0.1,0.6c0.1,0.5,0.2,0.8,0.7,0.8h2.3c0.5,0,0.7-0.2,0.7-0.8V88C40,85.3,38.3,83.6,33.9,83.6\"/>\n		<path d=\"M51,83.6c-2.2,0-3.7,1.2-4.5,2l-0.1-1.1c0-0.5-0.2-0.7-0.7-0.7h-2.4c-0.6,0-0.8,0.2-0.8,0.7v13.7c0,0.5,0.2,0.8,0.8,0.8h2.5c0.5,0,0.7-0.2,0.7-0.8v-9.4c0.7-0.6,2.2-1.6,4.4-1.8c0.6,0,0.8-0.2,0.8-0.8v-2C51.8,83.8,51.5,83.6,51,83.6\"/>\n		<path d=\"M56.8,77.7h-2.5c-0.6,0-0.8,0.2-0.8,0.8v19.8c0,0.5,0.2,0.8,0.8,0.8h2.5c0.5,0,0.7-0.2,0.7-0.8v-6.2h0.7l4.3,6.2c0.3,0.4,0.6,0.8,1.1,0.8h2.8c0.5,0,0.6-0.4,0.4-0.8l-5.4-7.9l5-5.8c0.3-0.4,0.2-0.8-0.4-0.8h-2.8c-0.5,0-0.9,0.4-1.2,0.8L58.2,89h-0.7V78.5C57.5,78,57.3,77.7,56.8,77.7\"/>\n		<path d=\"M71.6,89.6v-1.3c0-1.1,0.6-1.7,2.4-1.7c1.7,0,2.4,0.6,2.4,1.7v1.3H71.6 M74,83.6c-4.5,0-6.3,1.8-6.3,4.5v6.7c0,2.7,1.8,4.5,6.3,4.5c4.5,0,6.1-1.8,6.1-4.5v-0.3c0-0.5-0.2-0.8-0.8-0.8h-2.1c-0.6,0-0.8,0.2-0.8,0.8v0.2c0,1.1-0.6,1.7-2.4,1.7c-1.8,0-2.4-0.6-2.4-1.7v-2.3h7.8c0.5,0,0.7-0.2,0.7-0.7v-3.5C80.1,85.4,78.5,83.6,74,83.6\"/>\n		<path d=\"M87.2,80.2h-2.5c-0.6,0-0.8,0.2-0.8,0.8v2.9h-0.1l-1.4,0.2c-0.5,0.1-0.7,0.2-0.7,0.7v1.5c0,0.5,0.2,0.8,0.7,0.8h1.5v7.5c0,3,1.4,4.5,5.9,4.5h1.3c0.6,0,0.8-0.2,0.8-0.8v-1.8c0-0.5-0.2-0.8-0.8-0.8h-0.7c-1.7,0-2.4-0.5-2.4-1.6v-7h2.8c0.5,0,0.8-0.2,0.8-0.8v-1.7c0-0.5-0.2-0.7-0.8-0.7H88V81C88,80.4,87.8,80.2,87.2,80.2\"/>\n	</g>\n	<g class=\"logo-work-market--work\" transform=\"translate(57.000000, 9.000000)\">\n		<path d=\"M21.8,98.4c-0.2,0.5-0.2,0.7-0.8,0.7h-3c-0.6,0-0.7-0.2-0.8-0.7l-1.7-7.2c-0.2-0.6-0.3-1.5-0.3-1.6c0,0.1-0.2,0.8-0.4,1.6l-2.1,7.2c-0.1,0.5-0.2,0.7-0.8,0.7H9c-0.6,0-0.7-0.2-0.8-0.7L4.5,84.6c-0.2-0.5,0.2-0.8,0.7-0.8h2.4c0.6,0,0.7,0.3,0.9,0.8l2.1,8c0.2,0.6,0.2,1.4,0.2,1.5c0-0.1,0.2-0.9,0.4-1.5l2.2-8c0.1-0.5,0.3-0.8,0.8-0.8h2.4c0.6,0,0.8,0.3,0.9,0.8l1.9,8c0.2,0.7,0.2,1.3,0.3,1.4c0-0.1,0.2-0.9,0.3-1.4l2.1-8c0.1-0.5,0.3-0.8,0.9-0.8h2c0.5,0,0.9,0.2,0.7,0.8L21.8,98.4\"/>\n		<path d=\"M27,88.1c0-2.7,2.1-4.5,6.6-4.5s6.6,1.8,6.6,4.5v6.7c0,2.7-2.1,4.5-6.6,4.5S27,97.5,27,94.8V88.1L27,88.1z M36.2,88.4c0-1.1-0.9-1.6-2.6-1.6c-1.7,0-2.6,0.5-2.6,1.6v6c0,1.2,0.9,1.7,2.6,1.7c1.7,0,2.6-0.5,2.6-1.7V88.4L36.2,88.4z\"/>\n		<path d=\"M51.9,84.3v2c0,0.5-0.2,0.8-0.8,0.8c-2.2,0.2-3.7,1.2-4.4,1.8v9.4c0,0.5-0.2,0.8-0.7,0.8h-2.5c-0.6,0-0.8-0.2-0.8-0.8V84.6c0-0.5,0.2-0.7,0.8-0.7h2.4c0.6,0,0.7,0.2,0.7,0.7l0.1,1.1c0.8-0.9,2.3-2,4.5-2C51.6,83.6,51.9,83.8,51.9,84.3\"/>\n		<path d=\"M66.8,98.3c0.2,0.4,0.2,0.8-0.4,0.8h-2.8c-0.5,0-0.8-0.4-1.1-0.8l-4.3-6.2h-0.7v6.2c0,0.5-0.2,0.8-0.7,0.8h-2.5c-0.6,0-0.8-0.2-0.8-0.8V78.5c0-0.5,0.2-0.8,0.8-0.8h2.5c0.5,0,0.7,0.2,0.7,0.8V89h0.7l3.8-4.4c0.3-0.4,0.7-0.8,1.2-0.8h2.8c0.5,0,0.7,0.4,0.4,0.8l-5,5.8L66.8,98.3\"/>\n	</g>\n	<path class=\"logo-work-market--wave\" d=\"M47.3,77H8.7C6.1,77,4,79.1,4,81.7v38.6c0,2.6,2.1,4.7,4.7,4.7h38.6c2.6,0,4.7-2.1,4.7-4.7V81.7C52,79.1,49.9,77,47.3,77L47.3,77z M46.4,107.9c-0.2,0.1-0.5,0.2-0.7,0.2c-0.7,0-1.3-0.4-1.6-1l-3.5-7.7l-3.5,7.7c-0.3,0.6-0.9,1-1.6,1c-0.7,0-1.3-0.4-1.6-1l-3.5-7.7L27,107c-0.3,0.6-0.9,1-1.6,1c-0.7,0-1.3-0.4-1.6-1l-3.5-7.7l-3.5,7.7c-0.3,0.6-0.9,1.1-1.6,1.1c-0.7,0-1.3-0.5-1.6-1.1L8.7,95.9c-0.4-0.9,0-1.9,0.9-2.3c0.9-0.4,1.9,0,2.3,0.9l3.5,7.7l3.5-7.7c0.3-0.6,0.9-1,1.6-1s1.3,0.4,1.6,1l3.5,7.7l3.5-7.7c0.3-0.6,0.9-1,1.6-1s1.3,0.4,1.6,1l3.5,7.7l3.5-7.7c0.3-0.6,0.9-1,1.6-1s1.3,0.4,1.6,1l5.1,11.2C47.7,106.5,47.3,107.5,46.4,107.9L46.4,107.9z\"/>\n</svg>\n";
},"useData":true}));

Handlebars.registerPartial("message", this["wm"]["templates"]["message"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return " -has-response";
},"3":function(depth0,helpers,partials,data) {
    return " -is-sent";
},"5":function(depth0,helpers,partials,data) {
    return " -is-received";
},"7":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.recentlyAdded : depth0)) != null ? stack1.id : stack1),(depth0 != null ? depth0.id : depth0),{"name":"eq","hash":{},"fn":this.program(8, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"8":function(depth0,helpers,partials,data) {
    return " -is-new'";
},"10":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.avatar,depth0,{"name":"avatar","hash":{"hash":((stack1 = (depth0 != null ? depth0.creator : depth0)) != null ? stack1.id : stack1),"src":((stack1 = (depth0 != null ? depth0.creator : depth0)) != null ? stack1.thumbnail : stack1),"type":"worker"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"12":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.avatar,depth0,{"name":"avatar","hash":{"hash":((stack1 = (depth0 != null ? depth0.creator : depth0)) != null ? stack1.id : stack1),"src":((stack1 = (depth0 != null ? depth0.creator : depth0)) != null ? stack1.thumbnail : stack1),"type":"company"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"14":function(depth0,helpers,partials,data) {
    var stack1;

  return " in reference to "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.onBehalfUser : depth0)) != null ? stack1.fullName : stack1), depth0));
},"16":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isPublic : depth0),{"name":"if","hash":{},"fn":this.program(17, data, 0),"inverse":this.program(20, data, 0),"data":data})) != null ? stack1 : "");
},"17":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.unlesseq || (depth0 && depth0.unlesseq) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.activeFilter : depth0),"public-messages",{"name":"unlesseq","hash":{},"fn":this.program(18, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"18":function(depth0,helpers,partials,data) {
    return "						<span class=\"message--privacy tooltipped tooltipped-w\" aria-label=\"This messages is visible to all applied workers.\">\n							<i class=\"wm-icon-globe-circle\"></i>\n						</span>\n";
},"20":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isPrivileged : depth0),{"name":"if","hash":{},"fn":this.program(21, data, 0),"inverse":this.program(24, data, 0),"data":data})) != null ? stack1 : "");
},"21":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.unlesseq || (depth0 && depth0.unlesseq) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.activeFilter : depth0),"privileged-messages",{"name":"unlesseq","hash":{},"fn":this.program(22, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"22":function(depth0,helpers,partials,data) {
    return "							<span class=\"message--privacy tooltipped tooltipped-w\" aria-label=\"This message is visible to the assigned worker.\">\n								<i class=\"wm-icon-add-user-circle\"></i>\n							</span>\n";
},"24":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isPrivate : depth0),{"name":"if","hash":{},"fn":this.program(25, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"25":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.unlesseq || (depth0 && depth0.unlesseq) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.activeFilter : depth0),"private-messages",{"name":"unlesseq","hash":{},"fn":this.program(26, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"26":function(depth0,helpers,partials,data) {
    return "								<span class=\"message--privacy tooltipped tooltipped-w\" aria-label=\"This message is only visible to your company.\">\n									<i class=\"wm-icon-lock-circle\"></i>\n								</span>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"message"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.responses : depth0)) != null ? stack1.length : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.creator : depth0)) != null ? stack1.isCurrentUser : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.program(5, data, 0),"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.recentlyAdded : depth0),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\" data-id=\""
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\">\n	<a class=\"message--avatar open-user-profile-popup\" href=\"/profile/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.creator : depth0)) != null ? stack1.id : stack1), depth0))
    + "\" data-number=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.creator : depth0)) != null ? stack1.id : stack1), depth0))
    + "\">\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.creator : depth0)) != null ? stack1.isWorker : stack1),{"name":"if","hash":{},"fn":this.program(10, data, 0),"inverse":this.program(12, data, 0),"data":data})) != null ? stack1 : "")
    + "	</a>\n	<div class=\"message--new-label\">New</div>\n	<div class=\"message--details\">\n		<div class=\"message--meta\">\n			<a class=\"message--name open-user-profile-popup\" href=\"/profile/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.creator : depth0)) != null ? stack1.id : stack1), depth0))
    + "\" data-number=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.creator : depth0)) != null ? stack1.id : stack1), depth0))
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.creator : depth0)) != null ? stack1.fullName : stack1), depth0))
    + "</a> posted"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.onBehalfUser : depth0),{"name":"if","hash":{},"fn":this.program(14, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ":\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isQuestion : depth0),{"name":"unless","hash":{},"fn":this.program(16, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			<span class=\"message--date\">"
    + alias2(alias1((depth0 != null ? depth0.createdOnDate : depth0), depth0))
    + "</span>\n		</div>\n		<div class=\"message--content\">\n			<div class=\"message--text\" title=\""
    + alias2(alias1((depth0 != null ? depth0.content : depth0), depth0))
    + "\">"
    + alias2(alias1((depth0 != null ? depth0.content : depth0), depth0))
    + "</div>\n		</div>\n	</div>\n	<div class=\"message--response\">\n		<textarea name=\"new-response-content\" class=\"message--new-response-content\" placeholder=\"Write a reply&hellip;\" rows=\"4\"></textarea>\n		<button class=\"button -primary message--post-response\">Reply</button>\n	</div>\n</div>\n";
},"usePartial":true,"useData":true}));

Handlebars.registerPartial("modal", this["wm"]["templates"]["modal"] = Handlebars.template({"1":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<div id=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.intro : depth0)) != null ? stack1.id : stack1), depth0))
    + "\" class=\"wm-modal--slide "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.intro : depth0)) != null ? stack1.isActive : stack1),{"name":"if","hash":{},"fn":this.program(2, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n			<div class=\"wm-modal--toolbar "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.intro : depth0)) != null ? stack1.fixedScroll : stack1),{"name":"if","hash":{},"fn":this.program(4, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n				<div class=\"wm-modal--close wm-icon-x\" data-modal-close></div>\n				<h1 class=\"wm-modal--header\">\n					<span class=\"wm-modal--title\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.intro : depth0)) != null ? stack1.title : stack1), depth0))
    + "</span>\n				</h1>\n			</div>\n			<div class=\"wm-modal--viewport "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.intro : depth0)) != null ? stack1.fixedScroll : stack1),{"name":"if","hash":{},"fn":this.program(6, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n				<div class=\"wm-modal--content\">"
    + ((stack1 = alias1(((stack1 = (depth0 != null ? depth0.intro : depth0)) != null ? stack1.content : stack1), depth0)) != null ? stack1 : "")
    + "</div>\n			</div>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.intro : depth0)) != null ? stack1.controls : stack1),{"name":"if","hash":{},"fn":this.program(8, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</div>\n";
},"2":function(depth0,helpers,partials,data) {
    return "-active";
},"4":function(depth0,helpers,partials,data) {
    return "-shadow";
},"6":function(depth0,helpers,partials,data) {
    return "-fixed-scroll";
},"8":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return "				<div class=\"wm-modal--controls\">\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.intro : depth0)) != null ? stack1.controls : stack1),{"name":"each","hash":{},"fn":this.program(9, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</div>\n";
},"9":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "						<button class=\"wm-modal--control "
    + alias3(((helper = (helper = helpers.classList || (depth0 != null ? depth0.classList : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"classList","hash":{},"data":data}) : helper)))
    + " "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.primary : depth0),{"name":"if","hash":{},"fn":this.program(10, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " "
    + ((stack1 = helpers['if'].call(depth0,(depths[1] != null ? depths[1].fixedScroll : depths[1]),{"name":"if","hash":{},"fn":this.program(4, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.forward : depth0),{"name":"if","hash":{},"fn":this.program(12, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.back : depth0),{"name":"if","hash":{},"fn":this.program(14, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.close : depth0),{"name":"if","hash":{},"fn":this.program(16, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.close : depth0),{"name":"if","hash":{},"fn":this.program(18, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "							"
    + alias3(((helper = (helper = helpers.text || (depth0 != null ? depth0.text : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"text","hash":{},"data":data}) : helper)))
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.primary : depth0),{"name":"if","hash":{},"fn":this.program(20, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "						</button>\n";
},"10":function(depth0,helpers,partials,data) {
    return "-primary";
},"12":function(depth0,helpers,partials,data) {
    return "data-modal-slide=\"next\" ";
},"14":function(depth0,helpers,partials,data) {
    return "data-modal-slide=\"prev\"";
},"16":function(depth0,helpers,partials,data) {
    return "data-modal-close";
},"18":function(depth0,helpers,partials,data) {
    return "								<span class=\"wm-icon-x\"></span>\n";
},"20":function(depth0,helpers,partials,data) {
    return "								<span class=\"wm-icon-right-arrow\"></span>\n";
},"22":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<div id=\""
    + alias2(alias1(((stack1 = blockParams[0][0]) != null ? stack1.id : stack1), depth0))
    + "\" class=\"wm-modal--slide "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = blockParams[0][0]) != null ? stack1.isActive : stack1),{"name":"if","hash":{},"fn":this.program(2, data, 0, blockParams, depths),"inverse":this.noop,"data":data,"blockParams":blockParams})) != null ? stack1 : "")
    + "\">\n			<div class=\"wm-modal--toolbar "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = blockParams[0][0]) != null ? stack1.fixedScroll : stack1),{"name":"if","hash":{},"fn":this.program(4, data, 0, blockParams, depths),"inverse":this.noop,"data":data,"blockParams":blockParams})) != null ? stack1 : "")
    + "\">\n				<div class=\"wm-modal--close wm-icon-x\" data-modal-close></div>\n				<h1 class=\"wm-modal--header\">\n					<span class=\"wm-modal--title\">"
    + alias2(alias1(((stack1 = blockParams[0][0]) != null ? stack1.title : stack1), depth0))
    + "</span>\n				</h1>\n			</div>\n			<div class=\"wm-modal--viewport "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = blockParams[0][0]) != null ? stack1.fixedScroll : stack1),{"name":"if","hash":{},"fn":this.program(6, data, 0, blockParams, depths),"inverse":this.noop,"data":data,"blockParams":blockParams})) != null ? stack1 : "")
    + "\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depths[1] != null ? depths[1].showProgress : depths[1]),{"name":"if","hash":{},"fn":this.program(23, data, 0, blockParams, depths),"inverse":this.noop,"data":data,"blockParams":blockParams})) != null ? stack1 : "")
    + "				<div class=\"wm-modal--content\">\n					"
    + ((stack1 = alias1(((stack1 = blockParams[0][0]) != null ? stack1.content : stack1), depth0)) != null ? stack1 : "")
    + "\n				</div>\n			</div>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = blockParams[0][0]) != null ? stack1.controls : stack1),{"name":"if","hash":{},"fn":this.program(30, data, 0, blockParams, depths),"inverse":this.noop,"data":data,"blockParams":blockParams})) != null ? stack1 : "")
    + "		</div>\n";
},"23":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return ((stack1 = (helpers.gt || (depth0 && depth0.gt) || helpers.helperMissing).call(depth0,((stack1 = (depths[2] != null ? depths[2].slides : depths[2])) != null ? stack1.length : stack1),1,{"name":"gt","hash":{},"fn":this.program(24, data, 0, blockParams, depths),"inverse":this.noop,"data":data,"blockParams":blockParams})) != null ? stack1 : "");
},"24":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return "						<div class=\"wm-progress-indicator\">\n"
    + ((stack1 = helpers.each.call(depth0,(depths[3] != null ? depths[3].slides : depths[3]),{"name":"each","hash":{},"fn":this.program(25, data, 2, blockParams, depths),"inverse":this.noop,"data":data,"blockParams":blockParams})) != null ? stack1 : "")
    + "						</div>\n";
},"25":function(depth0,helpers,partials,data,blockParams) {
    var stack1;

  return ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,blockParams[0][1],blockParams[3][1],{"name":"eq","hash":{},"fn":this.program(26, data, 0, blockParams),"inverse":this.program(28, data, 0, blockParams),"data":data,"blockParams":blockParams})) != null ? stack1 : "");
},"26":function(depth0,helpers,partials,data,blockParams) {
    var stack1;

  return ((stack1 = this.invokePartial(partials['progress-indicator'],depth0,{"name":"progress-indicator","hash":{"status":"active","title":((stack1 = blockParams[1][0]) != null ? stack1.title : stack1)},"data":data,"blockParams":blockParams,"indent":"\t\t\t\t\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"28":function(depth0,helpers,partials,data,blockParams) {
    var stack1;

  return ((stack1 = this.invokePartial(partials['progress-indicator'],depth0,{"name":"progress-indicator","hash":{"title":((stack1 = blockParams[1][0]) != null ? stack1.title : stack1)},"data":data,"blockParams":blockParams,"indent":"\t\t\t\t\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"30":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return "				<div class=\"wm-modal--controls\">\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = blockParams[1][0]) != null ? stack1.controls : stack1),{"name":"each","hash":{},"fn":this.program(9, data, 0, blockParams, depths),"inverse":this.noop,"data":data,"blockParams":blockParams})) != null ? stack1 : "")
    + "				</div>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return "<div class=\"wm-modal\" data-modal>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.intro : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0, blockParams, depths),"inverse":this.noop,"data":data,"blockParams":blockParams})) != null ? stack1 : "")
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.slides : depth0),{"name":"each","hash":{},"fn":this.program(22, data, 2, blockParams, depths),"inverse":this.noop,"data":data,"blockParams":blockParams})) != null ? stack1 : "")
    + "</div>\n";
},"usePartial":true,"useData":true,"useDepths":true,"useBlockParams":true}));

Handlebars.registerPartial("notification", this["wm"]["templates"]["notification"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return " data-badge=\""
    + ((stack1 = (helpers.gt || (depth0 && depth0.gt) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.count : depth0),99,{"name":"gt","hash":{},"fn":this.program(2, data, 0),"inverse":this.program(4, data, 0),"data":data})) != null ? stack1 : "")
    + "\"";
},"2":function(depth0,helpers,partials,data) {
    return "99+";
},"4":function(depth0,helpers,partials,data) {
    var helper;

  return this.escapeExpression(((helper = (helper = helpers.count || (depth0 != null ? depth0.count : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"count","hash":{},"data":data}) : helper)));
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"wm-notification\""
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.count : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">\n	<i class=\"wm-icon-bell\"></i>\n</div>\n";
},"useData":true}));

Handlebars.registerPartial("page-header", this["wm"]["templates"]["page-header"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function";

  return "<div class=\"wm-page-header\">\n    <h1>"
    + this.escapeExpression(((helper = (helper = helpers.title || (depth0 != null ? depth0.title : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"title","hash":{},"data":data}) : helper)))
    + "</h1>\n    <div class=\"wm-page-header--content\">\n        "
    + ((stack1 = ((helper = (helper = helpers.body || (depth0 != null ? depth0.body : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"body","hash":{},"data":data}) : helper))) != null ? stack1 : "")
    + "\n    </div>\n</div>";
},"useData":true}));

Handlebars.registerPartial("pagination", this["wm"]["templates"]["pagination"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper;

  return this.escapeExpression(((helper = (helper = helpers.min || (depth0 != null ? depth0.min : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"min","hash":{},"data":data}) : helper)));
},"3":function(depth0,helpers,partials,data) {
    return "1";
},"5":function(depth0,helpers,partials,data) {
    var helper;

  return this.escapeExpression(((helper = (helper = helpers.previous || (depth0 != null ? depth0.previous : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"previous","hash":{},"data":data}) : helper)));
},"7":function(depth0,helpers,partials,data) {
    return "javascript:void(0)";
},"9":function(depth0,helpers,partials,data) {
    var helper;

  return this.escapeExpression(((helper = (helper = helpers.next || (depth0 != null ? depth0.next : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"next","hash":{},"data":data}) : helper)));
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper;

  return "<div class=\"wm-pagination\" data-min=\""
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.min : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.program(3, data, 0),"data":data})) != null ? stack1 : "")
    + "\" data-max=\""
    + this.escapeExpression(((helper = (helper = helpers.max || (depth0 != null ? depth0.max : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"max","hash":{},"data":data}) : helper)))
    + "\">\n	<a class=\"wm-pagination--back wm-icon-left-arrow\" href=\""
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.previous : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.program(7, data, 0),"data":data})) != null ? stack1 : "")
    + "\"></a>\n	<span class=\"wm-pagination--label\">of</span>\n	<a class=\"wm-pagination--next wm-icon-right-arrow\" href=\""
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.next : depth0),{"name":"if","hash":{},"fn":this.program(9, data, 0),"inverse":this.program(7, data, 0),"data":data})) != null ? stack1 : "")
    + "\"></a>\n</div>\n";
},"useData":true}));

Handlebars.registerPartial("progress-bar", this["wm"]["templates"]["progress-bar"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper;

  return this.escapeExpression(((helper = (helper = helpers.size || (depth0 != null ? depth0.size : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"size","hash":{},"data":data}) : helper)));
},"3":function(depth0,helpers,partials,data) {
    return "large";
},"5":function(depth0,helpers,partials,data) {
    var helper;

  return this.escapeExpression(((helper = (helper = helpers.width || (depth0 != null ? depth0.width : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"width","hash":{},"data":data}) : helper)));
},"7":function(depth0,helpers,partials,data) {
    return "0";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<div class=\"progress-bar -"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.size : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.program(3, data, 0),"data":data})) != null ? stack1 : "")
    + " "
    + alias3(((helper = (helper = helpers.classlist || (depth0 != null ? depth0.classlist : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"classlist","hash":{},"data":data}) : helper)))
    + "\" id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\" data-width=\""
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.width : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.program(7, data, 0),"data":data})) != null ? stack1 : "")
    + "%\">\n	<div class=\"progress-bar--skin\">\n		<div class=\"progress-bar--progress\" style=\"width: "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.width : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.program(7, data, 0),"data":data})) != null ? stack1 : "")
    + "%;\"></div>\n	</div>\n</div>\n";
},"useData":true}));

Handlebars.registerPartial("progress-indicator", this["wm"]["templates"]["progress-indicator"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<div class=\"indicator -"
    + alias3(((helper = (helper = helpers.status || (depth0 != null ? depth0.status : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"status","hash":{},"data":data}) : helper)))
    + "\" data-step-title=\""
    + alias3(((helper = (helper = helpers.title || (depth0 != null ? depth0.title : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"title","hash":{},"data":data}) : helper)))
    + "\">\n	<span class=\"indicator--icon\"></span>\n</div>\n";
},"useData":true}));

Handlebars.registerPartial("radio", this["wm"]["templates"]["radio"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "checked";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<label class=\"wm-radio\" id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\">\n	<input type=\"radio\" name=\""
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "\" value=\""
    + alias3(((helper = (helper = helpers.value || (depth0 != null ? depth0.value : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"value","hash":{},"data":data}) : helper)))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isChecked : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " "
    + alias3(((helper = (helper = helpers.attributes || (depth0 != null ? depth0.attributes : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"attributes","hash":{},"data":data}) : helper)))
    + " />\n	<div class=\"wm-radio--skin\"></div>\n	<span class=\"wm-radio--text\">"
    + ((stack1 = ((helper = (helper = helpers.text || (depth0 != null ? depth0.text : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"text","hash":{},"data":data}) : helper))) != null ? stack1 : "")
    + "</span>\n</label>\n";
},"useData":true}));

Handlebars.registerPartial("ribbon", this["wm"]["templates"]["ribbon"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper;

  return "<div class=\"wm-ribbon -"
    + this.escapeExpression(((helper = (helper = helpers.status || (depth0 != null ? depth0.status : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"status","hash":{},"data":data}) : helper)))
    + "\">\n	<div class=\"wm-ribbon--skin\"></div>\n</div>\n";
},"useData":true}));

Handlebars.registerPartial("score-card", this["wm"]["templates"]["score-card"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "-recent";
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"active":true,"name":"graph"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"5":function(depth0,helpers,partials,data) {
    var stack1;

  return " "
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isDispatch : depth0),{"name":"unless","hash":{},"fn":this.program(6, data, 0),"inverse":this.program(9, data, 0),"data":data})) != null ? stack1 : "")
    + " ";
},"6":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "\n"
    + ((stack1 = this.invokePartial(partials.toggle,depth0,{"name":"toggle","hash":{"classlist":"","isChecked":"true","text":"All","value":"all","name":"score-card-toggle"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.toggle,depth0,{"name":"toggle","hash":{"classlist":"","text":"Yours","value":"yours","name":"score-card-toggle"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			<table class=\"score-card--table\">\n				<thead>\n					<tr>\n						<th></th>\n						<th>3 Mo.</th>\n						<th>All</th>\n					</tr>\n				</thead>\n				<tbody>\n					<tr>\n						<td><span class=\"score-card--metrics\">Paid Assign "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.paidAssignmentsForCompany : depth0),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</span></td>\n						<td class=\"-all\">"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.paidAssignments : stack1)) != null ? stack1.all : stack1)) != null ? stack1.net90 : stack1), depth0))
    + "</td>\n						<td class=\"-company\">"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.paidAssignments : stack1)) != null ? stack1.company : stack1)) != null ? stack1.net90 : stack1), depth0))
    + "</td>\n						<td class=\"-all\">"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.paidAssignments : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1), depth0))
    + "</td>\n						<td class=\"-company\">"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.paidAssignments : stack1)) != null ? stack1.company : stack1)) != null ? stack1.all : stack1), depth0))
    + "</td>\n					</tr>\n					<tr>\n						<td><span class=\"score-card--metrics\">Cancelled</span></td>\n						<td class=\"-all\">"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.cancelled : stack1)) != null ? stack1.all : stack1)) != null ? stack1.net90 : stack1), depth0))
    + "</td>\n						<td class=\"-company\">"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.cancelled : stack1)) != null ? stack1.company : stack1)) != null ? stack1.net90 : stack1), depth0))
    + "</td>\n						<td class=\"-all\">"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.cancelled : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1), depth0))
    + "</td>\n						<td class=\"-company\">"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.cancelled : stack1)) != null ? stack1.company : stack1)) != null ? stack1.all : stack1), depth0))
    + "</td>\n					</tr>\n					<tr>\n						<td><span class=\"score-card--metrics\">Abandoned</span></td>\n						<td class=\"-all\">"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.abandoned : stack1)) != null ? stack1.all : stack1)) != null ? stack1.net90 : stack1), depth0))
    + "</td>\n						<td class=\"-company\">"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.abandoned : stack1)) != null ? stack1.company : stack1)) != null ? stack1.net90 : stack1), depth0))
    + "</td>\n						<td class=\"-all\">"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.abandoned : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1), depth0))
    + "</td>\n						<td class=\"-company\">"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.abandoned : stack1)) != null ? stack1.company : stack1)) != null ? stack1.all : stack1), depth0))
    + "</td>\n					</tr>\n				</tbody>\n			</table>\n			<hr />\n"
    + ((stack1 = this.invokePartial(partials.completion,depth0,{"name":"completion","hash":{"value":((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.satisfaction : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1),"unit":"%","max":100,"min":0,"name":"Satisfaction","classlist":"score-card--overall -all"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.completion,depth0,{"name":"completion","hash":{"value":((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.satisfaction : stack1)) != null ? stack1.company : stack1)) != null ? stack1.all : stack1),"unit":"%","max":100,"min":0,"name":"Satisfaction","classlist":"score-card--overall -company"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			On-Time\n"
    + ((stack1 = this.invokePartial(partials['progress-bar'],depth0,{"name":"progress-bar","hash":{"width":((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.onTime : stack1)) != null ? stack1.all : stack1)) != null ? stack1.net90 : stack1),"size":"small","classlist":"-all"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials['progress-bar'],depth0,{"name":"progress-bar","hash":{"width":((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.onTime : stack1)) != null ? stack1.company : stack1)) != null ? stack1.net90 : stack1),"size":"small","classlist":"-company"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			Deliverables\n"
    + ((stack1 = this.invokePartial(partials['progress-bar'],depth0,{"name":"progress-bar","hash":{"width":((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.deliverables : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1),"size":"small","classlist":"-all"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials['progress-bar'],depth0,{"name":"progress-bar","hash":{"width":((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.deliverables : stack1)) != null ? stack1.company : stack1)) != null ? stack1.all : stack1),"size":"small","classlist":"-company"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"7":function(depth0,helpers,partials,data) {
    var helper;

  return "<span>("
    + this.escapeExpression(((helper = (helper = helpers.paidAssignmentsForCompany || (depth0 != null ? depth0.paidAssignmentsForCompany : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"paidAssignmentsForCompany","hash":{},"data":data}) : helper)))
    + " for you)</span>";
},"9":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "			<table class=\"score-card--table\">\n				<thead>\n				<tr>\n					<th></th>\n					<th>3 Mo.</th>\n					<th>All</th>\n				</tr>\n				</thead>\n				<tbody>\n				<tr>\n					<td><span class=\"score-card--metrics\">Paid Assign</span></td>\n					<td>"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.paidAssignments : stack1)) != null ? stack1.all : stack1)) != null ? stack1.net90 : stack1), depth0))
    + "</td>\n					<td>"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.paidAssignments : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1), depth0))
    + "</td>\n				</tr>\n				<tr>\n					<td><span class=\"score-card--metrics\">Cancelled</span></td>\n					<td>"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.cancelled : stack1)) != null ? stack1.all : stack1)) != null ? stack1.net90 : stack1), depth0))
    + "</td>\n					<td>"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.cancelled : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1), depth0))
    + "</td>\n				</tr>\n				<tr>\n					<td><span class=\"score-card--metrics\">Abandoned</span></td>\n					<td>"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.abandoned : stack1)) != null ? stack1.all : stack1)) != null ? stack1.net90 : stack1), depth0))
    + "</td>\n					<td>"
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.abandoned : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1), depth0))
    + "</td>\n				</tr>\n				</tbody>\n			</table>\n			<hr />\n"
    + ((stack1 = this.invokePartial(partials.completion,depth0,{"name":"completion","hash":{"value":((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.satisfaction : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1),"unit":"%","max":100,"min":0,"name":"Satisfaction","classlist":"score-card--overall -all"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			On-Time\n"
    + ((stack1 = this.invokePartial(partials['progress-bar'],depth0,{"name":"progress-bar","hash":{"width":((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.onTime : stack1)) != null ? stack1.all : stack1)) != null ? stack1.net90 : stack1),"size":"small","classlist":"-all"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			Deliverables\n"
    + ((stack1 = this.invokePartial(partials['progress-bar'],depth0,{"name":"progress-bar","hash":{"width":((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.deliverables : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1),"size":"small","classlist":"-all"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "		";
},"11":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "\n"
    + ((stack1 = this.invokePartial(partials.completion,depth0,{"name":"completion","hash":{"value":((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.satisfaction : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1),"unit":"%","max":100,"min":0,"name":"Satisfaction","classlist":"score-card--overall -all"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			On-Time\n"
    + ((stack1 = this.invokePartial(partials['progress-bar'],depth0,{"name":"progress-bar","hash":{"width":((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.onTime : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1),"size":"small","classlist":"-all"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			Deliverables\n"
    + ((stack1 = this.invokePartial(partials['progress-bar'],depth0,{"name":"progress-bar","hash":{"width":((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.deliverables : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1),"size":"small","classlist":"-all"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isDispatcher : depth0),{"name":"if","hash":{},"fn":this.program(12, data, 0),"inverse":this.program(14, data, 0),"data":data})) != null ? stack1 : "")
    + "			<span class=\"score-card--metrics -all\" data-score=\""
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.cancelled : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1), depth0))
    + "\">Cancelled</span>\n			<span class=\"score-card--metrics -all\" data-score=\""
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.abandoned : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1), depth0))
    + "\">Abandoned</span>\n";
},"12":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<span class=\"score-card--metrics -all\" data-score=\""
    + this.escapeExpression(this.lambda(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.paidAssignments : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1), depth0))
    + "\">Paid Assign</span>\n";
},"14":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "				<span class=\"score-card--metrics -all\" data-score=\""
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.paidAssignments : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1), depth0))
    + "\">Paid Assign <span>("
    + alias2(alias1(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.paidAssignments : stack1)) != null ? stack1.company : stack1)) != null ? stack1.all : stack1), depth0))
    + " for you)</span></span>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression, alias4=this.lambda;

  return "<div class=\"score-card "
    + alias3(((helper = (helper = helpers.classlist || (depth0 != null ? depth0.classlist : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"classlist","hash":{},"data":data}) : helper)))
    + " "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.showrecent : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\" id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\">\n	<h2 class=\"score-card--title\" data-score-all=\""
    + alias3(alias4(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.satisfaction : stack1)) != null ? stack1.all : stack1)) != null ? stack1.all : stack1), depth0))
    + "%\" data-score-company=\""
    + alias3(alias4(((stack1 = ((stack1 = ((stack1 = (depth0 != null ? depth0.values : depth0)) != null ? stack1.satisfaction : stack1)) != null ? stack1.company : stack1)) != null ? stack1.all : stack1), depth0))
    + "%\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.showrecent : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		Satisfaction\n	</h2>\n	<div class=\"score-card--details\">\n		"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.showrecent : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.program(11, data, 0),"data":data})) != null ? stack1 : "")
    + "	</div>\n</div>\n";
},"usePartial":true,"useData":true}));

Handlebars.registerPartial("search-score-card", this["wm"]["templates"]["search-score-card"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper;

  return "			<span class=\"score-card--metrics\" data-score=\""
    + this.escapeExpression(((helper = (helper = helpers.paidassign || (depth0 != null ? depth0.paidassign : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"paidassign","hash":{},"data":data}) : helper)))
    + "\">Paid Assign</span>\n";
},"3":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "			<span class=\"score-card--metrics\" data-score=\""
    + alias3(((helper = (helper = helpers.paidassign || (depth0 != null ? depth0.paidassign : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"paidassign","hash":{},"data":data}) : helper)))
    + "\">Paid Assign <span>("
    + alias3(((helper = (helper = helpers.paidassignforcompany || (depth0 != null ? depth0.paidassignforcompany : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"paidassignforcompany","hash":{},"data":data}) : helper)))
    + " for you)</span></span>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<div class=\"score-card profile-card--score-card\">\n	<h2 class=\"score-card--title\" data-score-all=\""
    + alias3(((helper = (helper = helpers.satisfaction || (depth0 != null ? depth0.satisfaction : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"satisfaction","hash":{},"data":data}) : helper)))
    + "\">Satisfaction</h2>\n	<div class=\"score-card--details\">\n"
    + ((stack1 = this.invokePartial(partials.completion,depth0,{"name":"completion","hash":{"value":(depth0 != null ? depth0.satisfaction : depth0),"unit":"%","max":100,"min":0,"name":"Satisfaction","classlist":"score-card--overall"},"data":data,"indent":"\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "		<span class=\"completion-bar--name\">On-Time</span><br/>\n"
    + ((stack1 = this.invokePartial(partials['progress-bar'],depth0,{"name":"progress-bar","hash":{"width":(depth0 != null ? depth0.ontime : depth0),"size":"small"},"data":data,"indent":"\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "		<span class=\"completion-bar--name\">Deliverables</span><br/>\n"
    + ((stack1 = this.invokePartial(partials['progress-bar'],depth0,{"name":"progress-bar","hash":{"width":(depth0 != null ? depth0.deliverables : depth0),"size":"small"},"data":data,"indent":"\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.isDispatch : depth0),true,{"name":"eq","hash":{},"fn":this.program(1, data, 0),"inverse":this.program(3, data, 0),"data":data})) != null ? stack1 : "")
    + "		<span class=\"score-card--metrics\" data-score=\""
    + alias3(((helper = (helper = helpers.cancelled || (depth0 != null ? depth0.cancelled : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"cancelled","hash":{},"data":data}) : helper)))
    + "\">Cancelled</span>\n		<span class=\"score-card--metrics\" data-score=\""
    + alias3(((helper = (helper = helpers.abandoned || (depth0 != null ? depth0.abandoned : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"abandoned","hash":{},"data":data}) : helper)))
    + "\">Abandoned</span>\n	</div>\n</div>\n";
},"usePartial":true,"useData":true}));

Handlebars.registerPartial("settings-switch", this["wm"]["templates"]["settings-switch"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "checked";
},"3":function(depth0,helpers,partials,data) {
    var helper;

  return this.escapeExpression(((helper = (helper = helpers.on || (depth0 != null ? depth0.on : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"on","hash":{},"data":data}) : helper)));
},"5":function(depth0,helpers,partials,data) {
    return "On";
},"7":function(depth0,helpers,partials,data) {
    var helper;

  return this.escapeExpression(((helper = (helper = helpers.off || (depth0 != null ? depth0.off : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"off","hash":{},"data":data}) : helper)));
},"9":function(depth0,helpers,partials,data) {
    return "Off";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<label class=\"settings-switch "
    + alias3(((helper = (helper = helpers.classlist || (depth0 != null ? depth0.classlist : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"classlist","hash":{},"data":data}) : helper)))
    + "\" id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\" "
    + alias3(((helper = (helper = helpers.attributes || (depth0 != null ? depth0.attributes : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"attributes","hash":{},"data":data}) : helper)))
    + ">\n	<input type=\"checkbox\" name=\""
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "\" value=\""
    + alias3(((helper = (helper = helpers.value || (depth0 != null ? depth0.value : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"value","hash":{},"data":data}) : helper)))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.checked : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "/>\n	<div class=\"settings-switch--skin\" data-on=\""
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.on : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.program(5, data, 0),"data":data})) != null ? stack1 : "")
    + "\" data-off=\""
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.off : depth0),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.program(9, data, 0),"data":data})) != null ? stack1 : "")
    + "\">\n		<div class=\"settings-switch--slider\"></div>\n	</div>\n</label>\n";
},"useData":true}));

Handlebars.registerPartial("single-row-invoice", this["wm"]["templates"]["single-row-invoice"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.editable : depth0),{"name":"unless","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"2":function(depth0,helpers,partials,data) {
    return "			<span class=\"tooltipped tooltipped-n\" aria-label=\"This invoice is locked\"><i class=\"wm-icon-lock-circle\"></i></span>\n";
},"4":function(depth0,helpers,partials,data) {
    return " "
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.invoiceSummaryNumber : depth0), depth0))
    + " &bull; ";
},"6":function(depth0,helpers,partials,data) {
    return " &bull; "
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.workNumber : depth0), depth0));
},"8":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "		<strong><a href=\"/payments/invoices/print_service_invoice/"
    + alias2(alias1((depth0 != null ? depth0.invoiceId : depth0), depth0))
    + "\" class=\"print-invoice-outlet\" target=\"_blank\">"
    + alias2(alias1((depth0 != null ? depth0.invoiceDescription : depth0), depth0))
    + "</a></strong><br/>\n";
},"10":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isInvoiceTypeAdHoc : depth0),{"name":"if","hash":{},"fn":this.program(11, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"11":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "			<strong>\n				<a href=\"/payments/invoices/print_service_invoice/"
    + alias2(alias1((depth0 != null ? depth0.invoiceId : depth0), depth0))
    + "\" class=\"print-invoice-outlet\" target=\"_blank\">"
    + alias2(alias1((depth0 != null ? depth0.invoiceDescription : depth0), depth0))
    + "</a>\n			</strong>\n";
},"13":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeAdHoc : depth0),{"name":"unless","hash":{},"fn":this.program(14, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"14":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "			<strong><a target=\"_blank\" href=\"/assignments/details/"
    + alias2(alias1((depth0 != null ? depth0.workNumber : depth0), depth0))
    + "\">"
    + alias2(alias1((depth0 != null ? depth0.workTitle : depth0), depth0))
    + "</a></strong>\n";
},"16":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.workCountry : depth0),{"name":"if","hash":{},"fn":this.program(17, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"17":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<div class=\"tooltipped tooltipped-n\" aria-label=\"Work Location and Worker Details\">\n				"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.workCountry : depth0),{"name":"if","hash":{},"fn":this.program(18, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n				"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.workResourceName : depth0),{"name":"if","hash":{},"fn":this.program(20, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			</div>\n";
},"18":function(depth0,helpers,partials,data) {
    return this.escapeExpression(this.lambda((depth0 != null ? depth0.formattedAddressShort : depth0), depth0));
},"20":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.workCountry : depth0),{"name":"if","hash":{},"fn":this.program(21, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " "
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.workResourceName : depth0), depth0));
},"21":function(depth0,helpers,partials,data) {
    return "&bull;";
},"23":function(depth0,helpers,partials,data) {
    return "		<div class=\"tooltipped tooltipped-n\" aria-label=\"Print Date\">Downloaded: "
    + this.escapeExpression((helpers.formattedDate || (depth0 && depth0.formattedDate) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.downloadedOn : depth0),{"name":"formattedDate","hash":{},"data":data}))
    + "</div>\n";
},"25":function(depth0,helpers,partials,data) {
    var alias1=helpers.helperMissing, alias2=this.escapeExpression;

  return "			<div class=\"row-fluid\">\n				<div class=\"span5\">Created</div>\n				<div class=\"span11\">"
    + alias2((helpers.formattedDate || (depth0 && depth0.formattedDate) || alias1).call(depth0,(depth0 != null ? depth0.invoiceCreatedDate : depth0),{"name":"formattedDate","hash":{},"data":data}))
    + "</div>\n			</div>\n			<dt>Voided</dt>\n			<dd>"
    + alias2((helpers.formattedDate || (depth0 && depth0.formattedDate) || alias1).call(depth0,(depth0 != null ? depth0.invoiceVoidDate : depth0),{"name":"formattedDate","hash":{},"data":data}))
    + " ("
    + alias2((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias1).call(depth0,(depth0 != null ? depth0.invoiceBalance : depth0),{"name":"formatCurrency","hash":{},"data":data}))
    + ")</dd>\n";
},"27":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeSubscription : depth0),{"name":"unless","hash":{},"fn":this.program(28, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"28":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeAdHoc : depth0),{"name":"unless","hash":{},"fn":this.program(29, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"29":function(depth0,helpers,partials,data) {
    return "					<div class=\"row-fluid\">\n						<div class=\"span5\">Approved</div>\n						<div class=\"span11\">"
    + this.escapeExpression((helpers.formattedDate || (depth0 && depth0.formattedDate) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.workCloseDate : depth0),{"name":"formattedDate","hash":{},"data":data}))
    + "</div>\n					</div>\n";
},"31":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "			<div class=\"row-fluid\">\n				<div class=\"span5\">\n					<div class=\"custom-field\">"
    + alias2(alias1((depth0 != null ? depth0.fieldName : depth0), depth0))
    + ":</div>\n				</div>\n				<div class=\"span11\">\n					<div class=\"custom-field\">"
    + alias2(alias1((depth0 != null ? depth0.fieldValue : depth0), depth0))
    + "</div>\n				</div>\n			</div>\n";
},"33":function(depth0,helpers,partials,data) {
    return "				<li><a href=\"/payments/invoices/print_service_invoice/"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.invoiceId : depth0), depth0))
    + "\" class=\"print-invoice-outlet\" target=\"_blank\">Print</a></li>\n";
},"35":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isInvoiceTypeAdHoc : depth0),{"name":"if","hash":{},"fn":this.program(36, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"36":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "					<li><a href=\"/payments/invoices/print_service_invoice/"
    + alias2(alias1((depth0 != null ? depth0.invoiceId : depth0), depth0))
    + "\" class=\"print-invoice-outlet\" target=\"_blank\">Print</a></li>\n					<li><a href=\"/payments/invoices/email/"
    + alias2(alias1((depth0 != null ? depth0.invoiceId : depth0), depth0))
    + "\" class=\"email-invoice-outlet\">Email</a></li>\n";
},"38":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeAdHoc : depth0),{"name":"unless","hash":{},"fn":this.program(39, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"39":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "					<li><a href=\"/payments/invoices/print/"
    + alias2(alias1((depth0 != null ? depth0.invoiceId : depth0), depth0))
    + "\" class=\"print-invoice-outlet\" target=\"_blank\">Print</a></li>\n					<li><a href=\"/payments/invoices/email/"
    + alias2(alias1((depth0 != null ? depth0.invoiceId : depth0), depth0))
    + "\" class=\"email-invoice-outlet\">Email</a></li>\n					<li><a href=\"/payments/invoices/export/"
    + alias2(alias1((depth0 != null ? depth0.invoiceId : depth0), depth0))
    + "\">Export CSV</a></li>\n";
},"41":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "				<li><a class=\"js-remove-from-bundle tooltipped tooltipped-n\" href=\"javascript:void(0);\" data-invoice-id=\""
    + alias2(alias1((depth0 != null ? depth0.invoiceId : depth0), depth0))
    + "\" data-invoice-summary=\""
    + alias2(alias1((depth0 != null ? depth0.invoiceSummaryId : depth0), depth0))
    + "\" aria-label=\"You cannot have an empty bundle.\">Remove From Bundle</a></li>\n";
},"43":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodePending : depth0),{"name":"if","hash":{},"fn":this.program(44, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"44":function(depth0,helpers,partials,data) {
    return "					<li><a class=\"js-unlock-invoice\" data-invoice=\""
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.invoiceId : depth0), depth0))
    + "\" href=\"javascript:void(0);\">Unlock</a></li>\n";
},"46":function(depth0,helpers,partials,data) {
    return "		<em>Processing</em>\n";
},"48":function(depth0,helpers,partials,data) {
    var stack1;

  return "		"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodePaid : depth0),{"name":"if","hash":{},"fn":this.program(49, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"49":function(depth0,helpers,partials,data) {
    return "Paid";
},"51":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodePaid : depth0),{"name":"unless","hash":{},"fn":this.program(52, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"52":function(depth0,helpers,partials,data) {
    var stack1;

  return "			"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"if","hash":{},"fn":this.program(53, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"53":function(depth0,helpers,partials,data) {
    return "Void";
},"55":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodePaid : depth0),{"name":"unless","hash":{},"fn":this.program(56, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"56":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"unless","hash":{},"fn":this.program(57, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"57":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<small>\n					Due: "
    + this.escapeExpression((helpers.formattedDate || (depth0 && depth0.formattedDate) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.invoiceDueDate : depth0),{"name":"formattedDate","hash":{},"data":data}))
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoicePastDue : depth0),{"name":"if","hash":{},"fn":this.program(58, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoicePastDue : depth0),{"name":"unless","hash":{},"fn":this.program(60, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</small>\n";
},"58":function(depth0,helpers,partials,data) {
    return "						<br/><span class=\"text-error\">Past Due</span>\n";
},"60":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceDueWithinWeek : depth0),{"name":"if","hash":{},"fn":this.program(61, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"61":function(depth0,helpers,partials,data) {
    return "							<br/><span class=\"text-error\">Coming Due</span>\n";
},"63":function(depth0,helpers,partials,data) {
    return "$0.00";
},"65":function(depth0,helpers,partials,data) {
    var stack1;

  return "		"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceIsSubscriptionOrAdhoc : depth0),{"name":"if","hash":{},"fn":this.program(66, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"66":function(depth0,helpers,partials,data) {
    return this.escapeExpression((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.invoiceBalance : depth0),{"name":"formatCurrency","hash":{},"data":data}));
},"68":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceIsSubscriptionOrAdhoc : depth0),{"name":"unless","hash":{},"fn":this.program(69, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"69":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.owner : depth0),{"name":"if","hash":{},"fn":this.program(70, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"70":function(depth0,helpers,partials,data) {
    return "				"
    + this.escapeExpression((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.buyerTotalCost : depth0),{"name":"formatCurrency","hash":{},"data":data}))
    + "\n";
},"72":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceIsSubscriptionOrAdhoc : depth0),{"name":"unless","hash":{},"fn":this.program(73, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"73":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.owner : depth0),{"name":"unless","hash":{},"fn":this.program(74, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"74":function(depth0,helpers,partials,data) {
    return "				"
    + this.escapeExpression((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.amountEarned : depth0),{"name":"formatCurrency","hash":{},"data":data}))
    + "\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<td class=\"invoice-check check\"></td>\n\n<td class=\"invoice-detail\">\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isReceivables : depth0),{"name":"unless","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	<span>"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceSummaryNumber : depth0),{"name":"if","hash":{},"fn":this.program(4, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.invoiceNumber : depth0), depth0))
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.workNumber : depth0),{"name":"if","hash":{},"fn":this.program(6, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</span>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isInvoiceTypeSubscription : depth0),{"name":"if","hash":{},"fn":this.program(8, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeSubscription : depth0),{"name":"unless","hash":{},"fn":this.program(10, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeSubscription : depth0),{"name":"unless","hash":{},"fn":this.program(13, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.workNumber : depth0),{"name":"if","hash":{},"fn":this.program(16, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.downloadedOn : depth0),{"name":"if","hash":{},"fn":this.program(23, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n	<div class=\"help-block\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"if","hash":{},"fn":this.program(25, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"unless","hash":{},"fn":this.program(27, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.customFields : depth0),{"name":"each","hash":{},"fn":this.program(31, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		<hr/>\n		<ul class=\"invoice-actions\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isInvoiceTypeSubscription : depth0),{"name":"if","hash":{},"fn":this.program(33, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeSubscription : depth0),{"name":"unless","hash":{},"fn":this.program(35, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeSubscription : depth0),{"name":"unless","hash":{},"fn":this.program(38, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.editable : depth0),{"name":"if","hash":{},"fn":this.program(41, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.editable : depth0),{"name":"unless","hash":{},"fn":this.program(43, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</ul>\n	</div>\n</td>\n<td class=\"invoice-status\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.pendingPaymentFulfillment : depth0),{"name":"if","hash":{},"fn":this.program(46, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.pendingPaymentFulfillment : depth0),{"name":"unless","hash":{},"fn":this.program(48, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.pendingPaymentFulfillment : depth0),{"name":"unless","hash":{},"fn":this.program(51, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.pendingPaymentFulfillment : depth0),{"name":"unless","hash":{},"fn":this.program(55, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</td>\n\n<td class=\"invoice-amount amount nowrap\">\n	"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"if","hash":{},"fn":this.program(63, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"unless","hash":{},"fn":this.program(65, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"unless","hash":{},"fn":this.program(68, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"unless","hash":{},"fn":this.program(72, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</td>\n";
},"useData":true}));

Handlebars.registerPartial("slider", this["wm"]["templates"]["slider"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper;

  return "		<span class=\"wm-slider--title\">"
    + this.escapeExpression(((helper = (helper = helpers.title || (depth0 != null ? depth0.title : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"title","hash":{},"data":data}) : helper)))
    + "</span>\n";
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.sliderTicks || (depth0 && depth0.sliderTicks) || helpers.helperMissing).call(depth0,depth0,{"name":"sliderTicks","hash":{},"fn":this.program(4, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"4":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "			<datalist class=\"wm-slider--ticks\" style=\"width: calc(100% + "
    + alias3(((helper = (helper = helpers.ticksWidth || (data && data.ticksWidth)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"ticksWidth","hash":{},"data":data}) : helper)))
    + "%); margin-left: "
    + alias3(((helper = (helper = helpers.ticksMargin || (data && data.ticksMargin)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"ticksMargin","hash":{},"data":data}) : helper)))
    + "%; margin-right: "
    + alias3(((helper = (helper = helpers.ticksMargin || (data && data.ticksMargin)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"ticksMargin","hash":{},"data":data}) : helper)))
    + "%;\">\n"
    + ((stack1 = helpers.each.call(depth0,(data && data.arrayOfTicks),{"name":"each","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</datalist>\n";
},"5":function(depth0,helpers,partials,data) {
    var helper;

  return "					<option style=\"width: calc(100% / "
    + this.escapeExpression(((helper = (helper = helpers.numberOfSteps || (data && data.numberOfSteps)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"numberOfSteps","hash":{},"data":data}) : helper)))
    + ");\">&bull;</option>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<div class=\"wm-slider\" data-slider-value=\""
    + alias3(((helper = (helper = helpers.value || (depth0 != null ? depth0.value : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"value","hash":{},"data":data}) : helper)))
    + " "
    + alias3(((helper = (helper = helpers.units || (depth0 != null ? depth0.units : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"units","hash":{},"data":data}) : helper)))
    + "\" data-slider-unit=\""
    + alias3(((helper = (helper = helpers.units || (depth0 != null ? depth0.units : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"units","hash":{},"data":data}) : helper)))
    + "\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.title : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	<input id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\" type=\"range\" name=\""
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "\" value=\""
    + alias3(((helper = (helper = helpers.value || (depth0 != null ? depth0.value : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"value","hash":{},"data":data}) : helper)))
    + "\" min=\""
    + alias3(((helper = (helper = helpers.min || (depth0 != null ? depth0.min : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"min","hash":{},"data":data}) : helper)))
    + "\" max=\""
    + alias3(((helper = (helper = helpers.max || (depth0 != null ? depth0.max : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"max","hash":{},"data":data}) : helper)))
    + "\" step=\""
    + alias3(((helper = (helper = helpers.step || (depth0 != null ? depth0.step : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"step","hash":{},"data":data}) : helper)))
    + "\" data-slider/>\n	<div class=\"wm-slider--progress\">\n		<div class=\"wm-slider--progress-bar\"></div>\n	</div>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.step : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true}));

Handlebars.registerPartial("spinner", this["wm"]["templates"]["spinner"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<div class=\"wm-spinner "
    + alias3(((helper = (helper = helpers.classlist || (depth0 != null ? depth0.classlist : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"classlist","hash":{},"data":data}) : helper)))
    + "\" id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\">\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n	<div class=\"wm-spinner--blade\"></div>\n</div>\n";
},"useData":true}));

Handlebars.registerPartial("switch", this["wm"]["templates"]["switch"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper;

  return "data-usernumber="
    + this.escapeExpression(((helper = (helper = helpers.attributes || (depth0 != null ? depth0.attributes : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"attributes","hash":{},"data":data}) : helper)));
},"3":function(depth0,helpers,partials,data) {
    return "radio";
},"5":function(depth0,helpers,partials,data) {
    return "checkbox";
},"7":function(depth0,helpers,partials,data) {
    return "checked";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<label class=\"switch "
    + alias3(((helper = (helper = helpers.classlist || (depth0 != null ? depth0.classlist : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"classlist","hash":{},"data":data}) : helper)))
    + "\" id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\" aria-label=\""
    + alias3(((helper = (helper = helpers.tooltip || (depth0 != null ? depth0.tooltip : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"tooltip","hash":{},"data":data}) : helper)))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.attributes : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">\n	<input type=\""
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isUnique : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.program(5, data, 0),"data":data})) != null ? stack1 : "")
    + "\" name=\""
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "\" value=\""
    + alias3(((helper = (helper = helpers.value || (depth0 != null ? depth0.value : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"value","hash":{},"data":data}) : helper)))
    + "\" class=\"switch--checkbox\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.checked : depth0),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " />\n	<div class=\"switch--skin\">"
    + ((stack1 = ((helper = (helper = helpers.text || (depth0 != null ? depth0.text : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"text","hash":{},"data":data}) : helper))) != null ? stack1 : "")
    + "</div>\n</label>\n";
},"useData":true}));

Handlebars.registerPartial("toggle", this["wm"]["templates"]["toggle"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper;

  return "-"
    + this.escapeExpression(((helper = (helper = helpers.nameIdentifier || (depth0 != null ? depth0.nameIdentifier : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"nameIdentifier","hash":{},"data":data}) : helper)));
},"3":function(depth0,helpers,partials,data) {
    return "checked";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<label class=\"toggle\" id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\">\n	<input type=\"radio\" name=\""
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.nameIdentifier : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\" value=\""
    + alias3(((helper = (helper = helpers.value || (depth0 != null ? depth0.value : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"value","hash":{},"data":data}) : helper)))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isChecked : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " "
    + alias3(((helper = (helper = helpers.checked || (depth0 != null ? depth0.checked : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"checked","hash":{},"data":data}) : helper)))
    + " />\n	<div class=\"toggle--skin "
    + alias3(((helper = (helper = helpers.classlist || (depth0 != null ? depth0.classlist : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"classlist","hash":{},"data":data}) : helper)))
    + "\">"
    + alias3(((helper = (helper = helpers.text || (depth0 != null ? depth0.text : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"text","hash":{},"data":data}) : helper)))
    + "</div>\n</label>\n";
},"useData":true}));

this["wm"]["templates"]["account/vendor-search-status"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "		<span class=\"label label-success\">LISTED</span>\n		<a class=\"vendor-search-status--remove\" href=\"javascript:void(0);\">Remove from Search</a>\n";
},"3":function(depth0,helpers,partials,data) {
    return "		<span class=\"label label-warning\">NOT LISTED</span>\n";
},"5":function(depth0,helpers,partials,data) {
    return "wm-icon-checkmark";
},"7":function(depth0,helpers,partials,data) {
    return "wm-icon-x";
},"9":function(depth0,helpers,partials,data) {
    return " disabled";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"alert alert-success\">\n	<strong>Vendor Search Listing Status:</strong>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isInVendorSearch : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.program(3, data, 0),"data":data})) != null ? stack1 : "")
    + "	<p>Did you know you can list your <em>company</em> as a vendor is search results, not just your employees? Get listed now and expand your opportunities.</p>\n	<ul class=\"vendor-search-status--requirements\">\n		<li class=\"vendor-search-status--requirement "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.hasAtLeastOneWorker : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.program(7, data, 0),"data":data})) != null ? stack1 : "")
    + "\">Have at least one user available for work</li>\n		<li class=\"vendor-search-status--requirement "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.hasAtLeastOneDispatcher : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.program(7, data, 0),"data":data})) != null ? stack1 : "")
    + "\">Have at least one user with <em>Team Agent</em> role</li>\n	</ul>\n	<button class=\"button -primary vendor-search-status--list\""
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.canList : depth0),{"name":"unless","hash":{},"fn":this.program(9, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">List in Vendor Search</button>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["addressbook/bulk-import"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<form id=\"form-import\">\n	<div id=\"custom_form_message\">\n		<div class=\"message alert alert-error error dn\">\n			<a class=\"close\">x</a>\n			<div></div>\n		</div>\n	</div>\n	<div>\n		This import functionality enables you to quickly upload multiple locations and contacts\n		<div id=\"import-locations-help\" class=\"alert alert-info\">\n			<strong>Step 1:</strong> Download the sample CSV for either <a href=\"/download/Sample%20Locations.csv\" download=\"Locations_sam.csv\">Locations</a> or\n			<a href=\"/download/Sample%20Contact.csv\" download=\"contacts_sample.csv\">Contacts</a>\n			<br>\n			<br>\n			<strong>Step 2:</strong> Replace the sample CSV data with your locations or contacts data. Please do not remove the column headers\n			<br>\n			<br>\n			<strong>Step 3:</strong> Select either Locations or Contacts radio button and then upload your CSV file\n		</div>\n		<a class=\"fr\" href=\"http://help.workmarket.com/customer/portal/articles/1430361-how-to-bulk-upload-locations-and-contacts\">Learn more in our help center!</a>\n	</div>\n	<br>\n\n	<div>Are you importing contacts or locations?</div><br>\n	<input class=\"controls\" type=\"radio\" name=\"import_type\" value=\"location\"> <strong>Locations</strong> </input>\n	<input class=\"controls\" type=\"radio\" name=\"import_type\" value=\"contact\" style=\"margin-left: 20px\"> <strong>Contacts</strong> </input>\n	<div id=\"addressbook-import-uploader\">\n		<noscript>\n			<input type=\"file\" name=\"qqfile\" id=\"qqfile\"/>\n		</noscript>\n	</div>\n\n	<div id=\"addressbook-import-uploaded\">\n		<a class=\"uploaded\"></a>\n	</div>\n\n	<a class=\"remove-upload dn\">remove</a>\n\n	<div class=\"wm-action-container\">\n		<button type=\"button\" class=\"button\" data-modal-close >Cancel</button>\n		<button type=\"button\"  class=\"button\" id=\"submit_upload_content\" disabled>Save Changes</button>\n	</div>\n</form>\n";
},"useData":true});

this["wm"]["templates"]["addressbook/client_filter_row_for_contacts"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "	<option value=\""
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isSelected : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">"
    + alias2(alias1((depth0 != null ? depth0.name : depth0), depth0))
    + "</option>\n";
},"2":function(depth0,helpers,partials,data) {
    return "selected";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<option></option>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.clientList : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"useData":true});

this["wm"]["templates"]["addressbook/client_filter_row_for_locations"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "	<option value=\""
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" name=\""
    + alias2(alias1((depth0 != null ? depth0.name : depth0), depth0))
    + "\" count=\""
    + alias2(alias1((depth0 != null ? depth0.locationCount : depth0), depth0))
    + "\"  "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isSelected : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">"
    + alias2(alias1((depth0 != null ? depth0.name : depth0), depth0))
    + " ("
    + alias2(alias1((depth0 != null ? depth0.locationCount : depth0), depth0))
    + " location)</option>\n";
},"2":function(depth0,helpers,partials,data) {
    return "selected";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<option></option>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.clientList : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"useData":true});

this["wm"]["templates"]["addressbook/group_list_row"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "	<option value=\""
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" "
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.memberCount : depth0),0,{"name":"eq","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">"
    + alias2(alias1((depth0 != null ? depth0.name : depth0), depth0))
    + " ("
    + alias2(alias1((depth0 != null ? depth0.memberCount : depth0), depth0))
    + ")</option>\n";
},"2":function(depth0,helpers,partials,data) {
    return "disabled";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<option></option>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.groupsList : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"useData":true});

this["wm"]["templates"]["addressbook/location_pin_info"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "("
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.number : depth0), depth0))
    + ")";
},"3":function(depth0,helpers,partials,data) {
    return "(+"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.moreContacts : depth0), depth0))
    + ")";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return alias2(alias1((depth0 != null ? depth0.name : depth0), depth0))
    + " "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.number : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n<br>\n"
    + alias2(alias1((depth0 != null ? depth0.client : depth0), depth0))
    + "\n<br>\n"
    + alias2(alias1((depth0 != null ? depth0.address : depth0), depth0))
    + "\n<br>\n"
    + alias2(alias1((depth0 != null ? depth0.contact : depth0), depth0))
    + " "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.moreContacts : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"useData":true});

this["wm"]["templates"]["addressbook/worker_pin_info"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "<a href=\"/profile/"
    + alias2(alias1((depth0 != null ? depth0.userNumber : depth0), depth0))
    + "\" data-number=\""
    + alias2(alias1((depth0 != null ? depth0.userNumber : depth0), depth0))
    + "\" class=\"open-user-profile-popup\">"
    + alias2(alias1((depth0 != null ? depth0.resourceName : depth0), depth0))
    + "</a>\n<br>\n"
    + alias2(alias1((depth0 != null ? depth0.companyName : depth0), depth0))
    + "\n\n";
},"useData":true});

this["wm"]["templates"]["admin/forums/posts"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "	<tr>\n		<td>\n			<div\n				style=\"max-width: 40em; min-width: 40em; width: 40em; word-wrap: break-word; overflow: hidden;\"\n				class=\"comment -collapsed\"\n			>\n				"
    + alias2(alias1((depth0 != null ? depth0.comment : depth0), depth0))
    + "\n			</div>\n		</td>\n		<td style=\"word-wrap: break-word; word-break: break-word;\">\n			<a href=\"/forums/post/"
    + alias2(alias1((depth0 != null ? depth0.discussionId : depth0), depth0))
    + "\" target=\"blank\">"
    + alias2(alias1((depth0 != null ? depth0.title : depth0), depth0))
    + "</a>\n		</td>\n		<td>"
    + alias2(alias1((depth0 != null ? depth0.creatorName : depth0), depth0))
    + "</td>\n		<td>"
    + alias2(alias1((depth0 != null ? depth0.modifiedDate : depth0), depth0))
    + "</td>\n	</tr>\n";
},"3":function(depth0,helpers,partials,data) {
    return "	No results!\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.posts : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.program(3, data, 0),"data":data})) != null ? stack1 : "");
},"useData":true});

this["wm"]["templates"]["admin/overridePaytermsModal"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<form id=\"override_payterms_form\" action=\"/admin/manage/company/override_payterms/"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.companyId : depth0), depth0))
    + "\" method=\"POST\">\n\n	<p>You are enabling payment terms for this company without their bank account information.\n		The override allows for an initial $1,000 on payment terms which can be increased once enabled.\n		A note is required&mdash;provide info on why you are overriding.</p>\n\n	<fieldset>\n		<div class=\"clearfix\">\n			<label>Note</label>\n			<div class=\"input\">\n				<textarea name=\"note\" rows=\"5\" cols=\"30\"></textarea>\n			</div>\n		</div>\n	</fieldset>\n	<div class=\"wm-action-container\">\n		<a class=\"button cancel\">Cancel</a>\n		<button type=\"submit\" class=\"button\">Submit</button>\n	</div>\n</form>\n";
},"useData":true});

this["wm"]["templates"]["admin/plans/cart-venue"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "<span><i class=\"wm-icon-trash icon-large muted\" data-action=\"trash\"></i></span>\n<span><strong>"
    + alias2(alias1((depth0 != null ? depth0.displayName : depth0), depth0))
    + "</strong>"
    + alias2(alias1((depth0 != null ? depth0.description : depth0), depth0))
    + "</span>\n";
},"useData":true});

this["wm"]["templates"]["admin/plans/plan-row"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "<td class=\"code\">"
    + alias2(alias1((depth0 != null ? depth0.code : depth0), depth0))
    + "</td>\n<td class=\"description\">"
    + alias2(alias1((depth0 != null ? depth0.description : depth0), depth0))
    + "</td>\n<td class=\"actions\">\n	<a href=\"#edit/"
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" class=\"tooltipped tooltipped-n\" aria-label=\"Edit\">\n		<i class=\"wm-icon-edit icon-large muted\"></i>\n	</a>\n</td>\n<td class=\"actions\">\n	<a class=\"tooltipped tooltipped-n\" aria-label=\"Delete\">\n		<i class=\"wm-icon-trash icon-large muted\" data-action=\"trash\"></i>\n	</a>\n</td>\n";
},"useData":true});

this["wm"]["templates"]["admin/plans/plan-title-form"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "<form class=\"form-inline\">\n	<label for=\"code\">Code</label>\n	<input type=\"text\" id=\"code\" name=\"code\" placeholder=\"Give your plan a code\" value=\""
    + alias2(alias1((depth0 != null ? depth0.code : depth0), depth0))
    + "\" />\n\n	<label for=\"description\">Description</label>\n	<input type=\"text\" id=\"description\" name=\"description\" placeholder=\"Just a simple description\" value=\""
    + alias2(alias1((depth0 != null ? depth0.description : depth0), depth0))
    + "\" />\n\n	<a href=\"#\" class=\"button\" data-action=\"save\">Save</a>\n	<a href=\"#\" class=\"button\" data-action=\"cancel\">Cancel</a>\n</form>\n";
},"useData":true});

this["wm"]["templates"]["admin/plans/plan-transaction-fee"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "		<option value=\""
    + alias2(alias1(depth0, depth0))
    + "\">"
    + alias2(alias1(depth0, depth0))
    + "\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<form class=\"form-inline transaction-fee-plan-config\">\n	Default Transaction Fee\n	<select id=\"percentage\" name=\"percentage\" class=\"span3\">\n		<option value=\""
    + alias2(alias1((depth0 != null ? depth0.defaultWorkFeePercentage : depth0), depth0))
    + "\">WM Default ("
    + alias2(alias1((depth0 != null ? depth0.defaultWorkFeePercentage : depth0), depth0))
    + ")\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.availablePercentages : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</select> %\n</form>\n";
},"useData":true});

this["wm"]["templates"]["assignments/bundles/bundleData"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "						<th class=\"short\">Actions</th>\n";
},"3":function(depth0,helpers,partials,data) {
    return "						<th class=\"short\"></th>\n";
},"5":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					<tr>\n						<td><a href=\"/assignments/details/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.workNumber : stack1), depth0))
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.title : stack1), depth0))
    + "</a></td>\n						<td>"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.location : stack1), depth0))
    + "</td>\n						<td>"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.due : stack1), depth0))
    + "</td>\n						<td>"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.budget : stack1), depth0))
    + "</td>\n						<td>"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.status : stack1), depth0))
    + "</td>\n						<td>\n"
    + ((stack1 = helpers['if'].call(depth0,(depths[1] != null ? depths[1].isEligibleToTakeAction : depths[1]),{"name":"if","hash":{},"fn":this.program(6, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "						</td>\n					</tr>\n";
},"6":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "								<a class=\"delete\" id=\"confirm-unbundle-template\" data-work=\""
    + alias2(alias1(((stack1 = ((stack1 = (depth0 != null ? depth0.collection : depth0)) != null ? stack1.options : stack1)) != null ? stack1.parentId : stack1), depth0))
    + "/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.workNumber : stack1), depth0))
    + "\" href=\"#\">unbundle</a>\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.status : stack1),"sent",{"name":"eq","hash":{},"fn":this.program(7, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"7":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depths[3] != null ? depths[3].isWorkActive : depths[3]),{"name":"if","hash":{},"fn":this.program(8, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"8":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "											<a class=\"accept\" data-work=\""
    + alias2(alias1(((stack1 = ((stack1 = (depth0 != null ? depth0.collection : depth0)) != null ? stack1.options : stack1)) != null ? stack1.parentId : stack1), depth0))
    + "/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.workNumber : stack1), depth0))
    + "\" href=\"#\">accept</a>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return "<div class=\"bundle_accordion\" id=\"assignmentsAccordion\">\n	<img class=\"accordion-icon\" src=\"/media/images/live_icons/assignments/overview.svg\">\n	<div class=\"accordion-heading media-body\">\n		<a id=\"accordion_assignments\" data-toggle=\"collapse\" href=\"#assignments_new\">\n			<h4>Assignments  <i class=\"toggle-icon pull-right icon-minus-sign\"></i></h4>\n		</a>\n	</div>\n	<div id=\"assignments_new\" class=\"accordion-body collapse in\">\n		<table id=\"bundle_list\">\n			<thead>\n				<tr>\n					<th class=\"long\">Title</th>\n					<th class=\"medium\">Location</th>\n					<th class=\"medium\">Start Date</th>\n					<th class=\"medium\">Budget</th>\n					<th class=\"short\">Status</th>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isEligibleToTakeAction : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0, blockParams, depths),"inverse":this.program(3, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "")
    + "				</tr>\n			</thead>\n			<tbody>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.assignments : depth0),{"name":"each","hash":{},"fn":this.program(5, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</tbody>\n		</table>\n	</div>\n</div>\n";
},"useData":true,"useDepths":true});

this["wm"]["templates"]["assignments/bundles/bundleOverview"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<!--check that overview is passed in properly here-->\n<div class=\"row\">\n	<div class=\"well-b2 span3\">\n		<h3 class=\"text-center\">Assignments</h3>\n		<div class=\"lead text-center well-content\" id=\"assignments\">\n			"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.overview : depth0)) != null ? stack1.assignments : stack1), depth0))
    + "\n		</div>\n	</div>\n\n	<div class=\"well-b2 span6\">\n		<h3 class=\"text-center\">Date Range</h3>\n		<div class=\"lead text-center well-content\" id=\"date_range\">\n			"
    + alias2(alias1(((stack1 = ((stack1 = (depth0 != null ? depth0.overview : depth0)) != null ? stack1.dates : stack1)) != null ? stack1.from : stack1), depth0))
    + " <i class=\"icon-arrow-right muted\"></i> "
    + alias2(alias1(((stack1 = ((stack1 = (depth0 != null ? depth0.overview : depth0)) != null ? stack1.dates : stack1)) != null ? stack1.to : stack1), depth0))
    + "\n		</div>\n	</div>\n\n	<div class=\"well-b2 span3\">\n		<h3 class=\"text-center\">Total Budget</h3>\n		<div class=\"lead text-center well-content\" id=\"total_budget\">\n			"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.overview : depth0)) != null ? stack1.budget : stack1), depth0))
    + "\n		</div>\n	</div>\n\n	<div class=\"well-b2 span3\">\n		<h3 class=\"text-center\">Owner</h3>\n		<div class=\"lead text-center well-content\" id=\"owner\">\n			"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.overview : depth0)) != null ? stack1.owner : stack1), depth0))
    + "\n		</div>\n	</div>\n</div>";
},"useData":true});

this["wm"]["templates"]["assignments/bundles/unbundleAction"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"modal-content\">\n	<p>Are you sure you want to unbundle this assignment?</p>\n	<div class=\"wm-action-container\">\n		<button type=\"button\" id=\"cancel_unbundle_button\" class=\"button\">Cancel</button>\n		<button type=\"button\" id=\"confirm_unbundle_button\" class=\"button\">Unbundle</button>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/creation/checkbox_cell"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<input type=\"checkbox\" name=\"asset[]\" id=\"asset_"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.id : stack1), depth0))
    + "\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.id : stack1), depth0))
    + "\" />\n";
},"useData":true});

this["wm"]["templates"]["assignments/creation/deliverableOptionsTemplate"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "	<option value=\""
    + alias2(alias1((depth0 != null ? depth0.code : depth0), depth0))
    + "\">"
    + alias2(alias1((depth0 != null ? depth0.description : depth0), depth0))
    + "</option>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.typeOptions : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"useData":true});

this["wm"]["templates"]["assignments/creation/deliverable_requirement"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return " required ";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<li>\n	<div class=\"controls-row\">\n		<div class=\"-"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.deliverableType : depth0)) != null ? stack1.code : stack1), depth0))
    + "-instance icon-instance\" title=\""
    + alias2(alias1((depth0 != null ? depth0.deliverableTypeCodeReplaced : depth0), depth0))
    + "\" >\n			<svg version=\"1.1\" class=\"deliverables-live-icon icon-position\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\"\n				 viewBox=\"0 0 29 29\" enable-background=\"new 0 0 29 29\" xml:space=\"preserve\">\n				<g class=\"deliverables-document-icon\">\n					<g>\n						<g>\n							<path fill=\"#4C5355\" d=\"M20.034,1.5c1.814,0,7.466,6.073,7.466,8.022v14.505c0,1.915-1.558,3.473-3.473,3.473H4.974\n								c-1.916,0-3.474-1.558-3.474-3.473V4.973C1.5,3.058,3.059,1.5,4.974,1.5H20.034 M20.034,0.5H4.974C2.507,0.5,0.5,2.507,0.5,4.973\n								v19.053c0,2.467,2.007,4.473,4.474,4.473h19.053c2.467,0,4.473-2.007,4.473-4.473V9.522C28.5,6.993,22.409,0.5,20.034,0.5\n								L20.034,0.5z\"/>\n						</g>\n					</g>\n					<g>\n						<g opacity=\"0.3\">\n							<path d=\"M19.62,1.219v4.62c0,2.388,1.936,4.325,4.325,4.325H28\"/>\n							<path d=\"M28,10.381h-4.055c-2.505,0-4.542-2.038-4.542-4.543V1.219h0.438v4.619c0,2.264,1.842,4.106,4.105,4.106H28V10.381z\"/>\n						</g>\n						<g>\n							<g>\n								<path fill=\"#FFFFFF\" d=\"M20.12,0.719v4.62c0,2.388,1.936,4.325,4.325,4.325H28.5\"/>\n								<path fill=\"#4C5355\" d=\"M28.5,9.881h-4.055c-2.505,0-4.542-2.038-4.542-4.543V0.719h0.438v4.619\n									c0,2.264,1.842,4.106,4.105,4.106H28.5V9.881z\"/>\n							</g>\n						</g>\n					</g>\n					<path fill=\"#FFFFFF\" d=\"M20.216,0.84c1.872,0,8.179,6.775,8.179,8.695c-2.281-0.997-5.384-3.567-5.384-3.567\n						S20.375,1.584,20.216,0.84z\"/>\n					</g>\n\n				<g class=\"deliverables-photo-icon\">\n				<g>\n					<path fill=\"#FFFFFF\" d=\"M4.474,28C2.283,28,0,25.718,0,23.527V4.474c0-0.19,0.279-0.372,0.306-0.554\n					c0.013-0.081,0.142-0.132,0.145-0.184c0.147-0.757,0.554-1.412,1.061-1.958l0.109-0.095l0.179-0.14\n					c0.54-0.491,1.191-0.825,1.867-0.963c0.131-0.01,0.187,0.236,0.239,0.229C4.132,0.776,4.301,1,4.473,1h19.053\n					C25.718,1,27,2.283,27,4.474v19.052C27,25.718,25.718,28,23.527,28H4.474z\"/>\n					<g>\n						<path fill=\"#4C5355\" d=\"M23.763,1C25.679,1,27,2.559,27,4.474v4.547v10.513v3.992C27,25.442,25.442,27,23.528,27h-4.549H4.473\n						C2.558,27,1,25.442,1,23.527V4.473c0-0.141,0.024-0.3,0.055-0.511c0.01-0.064,0.015-0.13,0.015-0.188\n						C1.195,3.17,1.495,2.597,1.945,2.113C1.971,2.085,1.996,2.055,2.024,2.02C2.067,1.986,2.11,1.952,2.149,1.916\n						c0.476-0.433,1.038-0.725,1.637-0.844C3.851,1.069,3.916,1.063,3.98,1.054l0.061-0.009C4.207,1.021,4.349,1,4.474,1h15.06H24\n						 M23.527,0h-3.992H4.474H4.473C4.255,0,4.046,0.034,3.837,0.064C3.75,0.077,3.659,0.074,3.573,0.091\n						C2.772,0.255,2.059,0.646,1.476,1.177C1.424,1.224,1.362,1.262,1.312,1.312C1.273,1.351,1.244,1.398,1.207,1.438\n						C0.66,2.028,0.259,2.756,0.091,3.574c-0.015,0.077-0.012,0.16-0.024,0.239C0.034,4.03,0,4.247,0,4.473v0.001v19.052v0.001\n						C0,25.994,2.006,28,4.473,28h0.001h14.504h4.549C25.995,28,28,25.993,28,23.527v-3.992V9.022V4.474C28,2.007,25.994,0,23.527,0\n						L23.527,0z\"/>\n					</g>\n				</g>\n				<path fill=\"#828788\" d=\"M4.417,27h14.451h4.713c1.881,0,3.42-1.539,3.42-3.42v-1.732l-4.906-2.423l-2.846-3.782l-3.815,2.232\n				l-4.63-5.625L1,19.92v3.664C1,25.463,2.537,27,4.417,27z\"/>\n				<circle opacity=\"0.4\" fill=\"#828788\" cx=\"7.764\" cy=\"7.684\" r=\"2.717\"/>\n			</g>\n\n				<g class=\"deliverables-signoff-icon\">\n				<g>\n					<g>\n						<path fill=\"#FFFFFF\" d=\"M4.474,28C2.283,28,0,25.718,0,23.527V4.474c0-0.19,0.279-0.372,0.306-0.554\n							c0.013-0.081,0.142-0.132,0.145-0.184c0.147-0.757,0.554-1.412,1.061-1.958l0.109-0.095l0.179-0.14\n							c0.54-0.491,1.191-0.825,1.867-0.963c0.131-0.01,0.187,0.236,0.239,0.229C4.132,0.776,4.301,1,4.473,1h19.053\n							C25.718,1,27,2.283,27,4.474v19.052C27,25.718,25.718,28,23.527,28H4.474z\"/>\n						<g>\n							<path fill=\"#4C5355\" d=\"M23.763,1C25.679,1,27,2.559,27,4.474v4.547v10.513v3.992C27,25.442,25.442,27,23.528,27h-4.549H4.473\n								C2.558,27,1,25.442,1,23.527V4.473c0-0.141,0.024-0.3,0.055-0.511c0.01-0.064,0.015-0.13,0.015-0.188\n								C1.195,3.17,1.495,2.597,1.945,2.113C1.971,2.085,1.996,2.055,2.024,2.02C2.067,1.986,2.11,1.952,2.149,1.916\n								c0.476-0.433,1.038-0.725,1.637-0.844C3.851,1.069,3.916,1.063,3.98,1.054l0.061-0.009C4.207,1.021,4.349,1,4.474,1h15.06H24\n								 M23.527,0h-3.992H4.474H4.473C4.255,0,4.046,0.034,3.837,0.064C3.75,0.077,3.659,0.074,3.573,0.091\n								C2.772,0.255,2.059,0.646,1.476,1.177C1.424,1.224,1.362,1.262,1.312,1.312C1.273,1.351,1.244,1.398,1.207,1.438\n								C0.66,2.028,0.259,2.756,0.091,3.574c-0.015,0.077-0.012,0.16-0.024,0.239C0.034,4.03,0,4.247,0,4.473v0.001v19.052v0.001\n								C0,25.994,2.006,28,4.473,28h0.001h14.504h4.549C25.995,28,28,25.993,28,23.527v-3.992V9.022V4.474C28,2.007,25.994,0,23.527,0\n								L23.527,0z\"/>\n						</g>\n					</g>\n				</g>\n				<path opacity=\"0.4\" fill=\"#828788\" d=\"M7.358,17.387c-0.334-0.172-1.173-1.385-2.024-2.743c0.852-1.358,1.69-2.57,2.024-2.743\n					c0.25-0.128,0.348-0.434,0.219-0.684c-0.128-0.25-0.433-0.347-0.683-0.219c-0.499,0.257-1.337,1.41-2.153,2.681\n					c-0.572-0.949-1.098-1.869-1.415-2.468c-0.132-0.248-0.438-0.342-0.687-0.211c-0.247,0.131-0.342,0.438-0.21,0.686\n					c0.015,0.029,0.784,1.441,1.709,2.957c-0.925,1.515-1.694,2.928-1.709,2.957c-0.132,0.248-0.037,0.555,0.21,0.686\n					c0.076,0.04,0.158,0.059,0.238,0.059c0.182,0,0.358-0.098,0.449-0.27c0.317-0.599,0.842-1.519,1.415-2.468\n					c0.816,1.271,1.654,2.424,2.153,2.681c0.074,0.039,0.154,0.057,0.232,0.057c0.184,0,0.361-0.1,0.451-0.275\n					C7.706,17.822,7.608,17.516,7.358,17.387z\"/>\n				<g>\n					<path fill=\"#5D5D5D\" d=\"M17.554,16.115c-0.641,0.774-1.011,0.848-1.509,0.436c-0.489,0.05-0.86,0.088-1.23,0.126\n						c0.062-0.407,0.123-0.815,0.195-1.285c-0.436,0.141-0.598,0.574-0.809,0.949c-0.162,0.289-0.301,0.595-0.782,0.327\n						c0.198-0.384,0.392-0.76,0.587-1.137c-0.038-0.024-0.076-0.048-0.115-0.071c-0.24,0.309-0.463,0.633-0.726,0.922\n						c-0.257,0.283-0.533,0.576-0.989,0.321c-0.086-0.048-0.23-0.022-0.34,0.003c-0.711,0.162-0.869,0.014-0.751-0.714\n						c0.009-0.056,0.001-0.115,0.001-0.175c-0.788-0.142-0.631,1.262-1.575,0.909c0.073-0.233,0.141-0.452,0.21-0.673\n						c-0.397-0.233-0.682-0.23-1.001,0.154c-0.589,0.708-1.263,0.812-2.123,0.534c-0.293-0.095-0.644-0.001-0.968-0.011\n						c-0.245-0.008-0.197-0.215-0.16-0.167c0.364-0.214,0.752-0.397,1.086-0.652c0.334-0.255,0.642-0.559,0.906-0.885\n						c0.132-0.163,0.215-0.432,0.194-0.641c-0.096-0.982,0.128-1.347,1.007-1.733c0.206-0.091,0.37-0.394,0.445-0.633\n						c0.086-0.275,0.052-0.588,0.07-0.885c0.052,0.003,0.114-0.009,0.124,0.008c0.45,0.807,0.804,0.906,1.626,0.423\n						c0.328-0.193,0.638-0.417,1.002-0.581c-0.412,0.545-0.814,1.098-1.239,1.633c-0.43,0.542-0.474,1.078,0.017,1.713\n						c0.753-1.082,1.473-2.179,2.706-2.839c-0.876,1.325-1.722,2.605-2.616,3.958c0.911-0.216,1.662-0.395,2.414-0.573\n						c0.044,0.068,0.088,0.136,0.132,0.204c-0.209,0.407-0.418,0.814-0.627,1.22c0.052,0.035,0.104,0.069,0.156,0.104\n						c0.243-0.299,0.482-0.601,0.729-0.897c0.291-0.349,0.574-0.709,1.113-0.396c0.079,0.046,0.225-0.041,0.339-0.037\n						c0.206,0.008,0.548-0.021,0.589,0.072c0.078,0.18,0.014,0.441-0.043,0.654c-0.048,0.18-0.168,0.34-0.256,0.509\n						c0.055,0.046,0.109,0.091,0.164,0.137c0.213-0.229,0.415-0.468,0.64-0.683c0.271-0.261,0.531-0.562,0.855-0.729\n						c0.193-0.1,0.501,0.023,0.678,0.039c-0.381,0.327-0.722,0.583-1.009,0.889c-0.087,0.093-0.024,0.327-0.029,0.497\n						c0.159-0.042,0.327-0.063,0.474-0.131c0.143-0.066,0.265-0.179,0.396-0.272C17.527,16.074,17.541,16.094,17.554,16.115z\n						 M8.507,15.196c0.342-0.587,0.678-1.131,0.974-1.696c0.068-0.129-0.004-0.331-0.011-0.499c-0.176,0.069-0.424,0.087-0.516,0.217\n						c-0.267,0.374-0.452,0.804-0.706,1.188C8.03,14.737,8.097,14.957,8.507,15.196z M6.536,16.342\n						c0.673,0.285,1.278,0.061,1.647-0.595c0.244-0.433-0.106-0.539-0.311-0.709C7.406,15.493,6.971,15.918,6.536,16.342z\n						 M9.196,15.496c0.035,0.083,0.07,0.167,0.105,0.25c0.243-0.058,0.603-0.037,0.707-0.19c0.347-0.51,0.374-1.096,0.026-1.728\n						C9.755,14.384,9.475,14.94,9.196,15.496z M9.399,11.952c-0.098,0.316-0.386,0.617,0.119,0.802\n						c0.672,0.246,0.622-0.457,1.006-0.673C10.11,12.034,9.781,11.996,9.399,11.952z M8.865,12.923c-0.9,0.191-1.145,0.667-0.862,1.527\n						C8.293,13.936,8.541,13.496,8.865,12.923z M11.845,16.543c0.274-0.416,0.548-0.831,0.822-1.247\n						C11.933,15.393,11.599,15.904,11.845,16.543z\"/>\n					<path fill=\"#5D5D5D\" d=\"M17.512,16.054c0.269-0.295,0.49-0.674,0.82-0.86c0.267-0.15,0.792-0.216,0.976-0.057\n						c0.335,0.291,0.538,0.209,0.845,0.049c0.207-0.109,0.453-0.19,0.683-0.192c0.157-0.001,0.316,0.155,0.465,0.344\n						c-0.598-0.254-0.819,0.134-1.006,0.509c-0.088,0.176-0.074,0.402-0.106,0.605c0.194-0.057,0.454-0.053,0.572-0.18\n						c0.573-0.617,1.119-1.262,1.655-1.912c0.536-0.651,1.024-1.345,1.587-1.972c0.215-0.239,0.579-0.343,0.948-0.549\n						c0.129,0.707-0.262,0.981-0.613,1.2c-0.675,0.42-1.163,0.98-1.502,1.686c-0.04,0.084-0.064,0.175-0.114,0.318\n						c0.135-0.024,0.211-0.057,0.281-0.047c0.25,0.036,0.499,0.088,0.748,0.133c-0.145,0.254-0.241,0.564-0.448,0.746\n						c-0.192,0.168-0.504,0.198-0.768,0.291c0.353,0.465,0.671,0.928,1.04,1.345c0.144,0.163,0.404,0.225,0.613,0.328\n						c0.572,0.279,1.169,0.517,1.704,0.855c0.176,0.111,0.336,0.581,0.248,0.703c-0.14,0.194-0.502,0.339-0.756,0.32\n						c-0.759-0.056-1.304-0.539-1.686-1.15c-0.298-0.477-0.692-0.633-1.204-0.636c-1.369-0.006-2.738-0.013-4.107-0.02\n						c-0.198-0.001-0.395,0-0.593,0c-0.003-0.069-0.006-0.139-0.008-0.208c1.698-0.069,3.395-0.137,5.171-0.209\n						c-0.26-0.41-0.519-0.818-0.757-1.194c-0.358,0.223-0.634,0.395-0.91,0.566c-0.043-0.045-0.086-0.09-0.13-0.136\n						c0.057-0.195,0.115-0.389,0.127-0.432c-0.433,0.13-0.931,0.318-1.444,0.388c-0.073,0.01-0.224-0.552-0.364-0.914\n						c-0.087,0.121-0.201,0.278-0.314,0.436c-0.578,0.808-1.164,0.775-1.609-0.091C17.541,16.094,17.527,16.074,17.512,16.054z\n						 M24.234,18.154c-0.043,0.078-0.087,0.156-0.13,0.234c0.377,0.316,0.73,0.669,1.141,0.932c0.175,0.112,0.47,0.037,0.711,0.048\n						c-0.118-0.227-0.178-0.553-0.364-0.661C25.172,18.465,24.69,18.331,24.234,18.154z M18.218,16.534\n						c0.791-0.286,1.005-0.69,0.716-1.297C18.487,15.54,18.149,15.87,18.218,16.534z M22.235,15.795\n						c0.684,0.021,0.777-0.052,0.847-0.665C22.741,15.397,22.488,15.596,22.235,15.795z\"/>\n				</g>\n			</g>\n\n				<g class=\"deliverables-other-icon\">\n						<g>\n							<g>\n								<path fill=\"#FFFFFF\" d=\"M4.474,28C2.283,28,0,25.718,0,23.527V4.474c0-0.19,0.279-0.372,0.306-0.554\n							c0.013-0.081,0.142-0.132,0.145-0.184c0.147-0.757,0.554-1.412,1.061-1.958l0.109-0.095l0.179-0.14\n							c0.54-0.491,1.191-0.825,1.867-0.963c0.131-0.01,0.187,0.236,0.239,0.229C4.132,0.776,4.301,1,4.473,1h19.053\n							C25.718,1,27,2.283,27,4.474v19.052C27,25.718,25.718,28,23.527,28H4.474z\"/>\n								<g>\n									<path fill=\"#4C5355\" d=\"M23.763,1C25.679,1,27,2.559,27,4.474v4.547v10.513v3.992C27,25.442,25.442,27,23.528,27h-4.549H4.473\n								C2.558,27,1,25.442,1,23.527V4.473c0-0.141,0.024-0.3,0.055-0.511c0.01-0.064,0.015-0.13,0.015-0.188\n								C1.195,3.17,1.495,2.597,1.945,2.113C1.971,2.085,1.996,2.055,2.024,2.02C2.067,1.986,2.11,1.952,2.149,1.916\n								c0.476-0.433,1.038-0.725,1.637-0.844C3.851,1.069,3.916,1.063,3.98,1.054l0.061-0.009C4.207,1.021,4.349,1,4.474,1h15.06H24\n								 M23.527,0h-3.992H4.474H4.473C4.255,0,4.046,0.034,3.837,0.064C3.75,0.077,3.659,0.074,3.573,0.091\n								C2.772,0.255,2.059,0.646,1.476,1.177C1.424,1.224,1.362,1.262,1.312,1.312C1.273,1.351,1.244,1.398,1.207,1.438\n								C0.66,2.028,0.259,2.756,0.091,3.574c-0.015,0.077-0.012,0.16-0.024,0.239C0.034,4.03,0,4.247,0,4.473v0.001v19.052v0.001\n								C0,25.994,2.006,28,4.473,28h0.001h14.504h4.549C25.995,28,28,25.993,28,23.527v-3.992V9.022V4.474C28,2.007,25.994,0,23.527,0\n								L23.527,0z\"/>\n								</g>\n							</g>\n						</g>\n						<path opacity=\"0.5\" fill=\"#828788\" d=\"M13.342,18c-0.453,0-1.03,1-1.58,2H23v-2H13.342z\"/>\n						<path fill=\"#828788\" d=\"M7.659,23.039c-0.319,0-0.621-0.152-0.81-0.413c-0.433-0.596-1.168-1.448-1.706-2.069\n					c-0.852-0.986-1.08-1.25-0.803-1.858c0.163-0.356,0.521-0.585,0.912-0.585c0.378,0,0.707,0.209,0.877,0.519\n					c0.098,0.123,0.291,0.344,0.527,0.617c0.257,0.297,0.558,0.646,0.854,1c1.034-1.808,2.896-4.884,3.873-5.388\n					c0.489-0.253,1.094-0.06,1.347,0.431c0.253,0.491,0.061,1.094-0.431,1.347c-0.584,0.426-2.857,4.167-3.758,5.868\n					c-0.161,0.305-0.469,0.505-0.813,0.53C7.706,23.038,7.683,23.039,7.659,23.039z\"/>\n						<path opacity=\"0.5\" fill=\"#828788\" d=\"M13.342,9c-0.453,0-1.03,1-1.58,2H23V9H13.342z\"/>\n						<path fill=\"#828788\" d=\"M7.659,14.289c-0.319,0-0.621-0.152-0.81-0.413c-0.433-0.596-1.168-1.448-1.706-2.069\n					c-0.852-0.986-1.08-1.25-0.803-1.858c0.163-0.356,0.521-0.585,0.912-0.585c0.378,0,0.707,0.209,0.877,0.519\n					c0.098,0.123,0.291,0.344,0.527,0.617c0.257,0.297,0.558,0.646,0.854,1c1.034-1.808,2.896-4.884,3.873-5.388\n					c0.489-0.252,1.094-0.06,1.347,0.431s0.061,1.094-0.431,1.347c-0.584,0.426-2.857,4.167-3.758,5.868\n					c-0.161,0.305-0.469,0.505-0.813,0.53C7.706,14.288,7.683,14.289,7.659,14.289z\"/>\n					</g>\n			</svg>\n		</div>\n		<input type=\"hidden\" class=\"deliverable-id\"  name=\"resourceCompletionForm.deliverableRequirements["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.deliverableRequirement : depth0)) != null ? stack1.priority : stack1), depth0))
    + "].id\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.deliverableRequirement : depth0)) != null ? stack1.id : stack1), depth0))
    + "\" />\n		<input type=\"hidden\" class=\"deliverable-type\"  name=\"resourceCompletionForm.deliverableRequirements["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.deliverableRequirement : depth0)) != null ? stack1.priority : stack1), depth0))
    + "].type\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.deliverableRequirement : depth0)) != null ? stack1.type : stack1), depth0))
    + "\"/>\n		<div class=\"drag-drop-content\">\n			<a href=\"javascript:void(0);\" class=\"remove-deliverable wm-icon-x\"></a>\n			<div class=\"input-append deliverables-controls\">\n				<input type=\"text\" class=\"deliverable-number-of-files only-numbers span1 text-center\" name=\"resourceCompletionForm.deliverableRequirements["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.deliverableRequirement : depth0)) != null ? stack1.priority : stack1), depth0))
    + "].numberOfFiles\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.deliverableRequirement : depth0)) != null ? stack1.number_of_files : stack1), depth0))
    + "\" maxlength=\"2\"/>\n				<span class=\"add-on\"># of Files</span>\n			</div>\n			<textarea rows=\"1\" cols=\"30\" maxlength=\"1000\" class=\"quick-description"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.deliverableRequirement : depth0)) != null ? stack1.instructions : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\" placeholder=\"Enter a short description\" name=\"resourceCompletionForm.deliverableRequirements["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.deliverableRequirement : depth0)) != null ? stack1.priority : stack1), depth0))
    + "].instructions\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.deliverableRequirement : depth0)) != null ? stack1.instructions : stack1), depth0))
    + "</textarea>\n		</div>\n	</div>\n</li>\n";
},"useData":true});

this["wm"]["templates"]["assignments/creation/file_manager_table"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<form action=\"/filemanager/get_available_assets\" id=\"attach_assets_to_assignment_form\" method=\"POST\">\n	<table id=\"documents_list\">\n		<thead>\n			<tr>\n				<th>Select</th>\n				<th>Name</th>\n				<th>Type</th>\n			</tr>\n		</thead>\n		<tbody></tbody>\n	</table>\n\n	<div class=\"wm-action-container\">\n		<button data-modal-close class=\"button\">Cancel</button>\n		<button id=\"attach_assets_to_assignment\" class=\"button\">Add to Assignment</button>\n	</div>\n</form>\n";
},"useData":true});

this["wm"]["templates"]["assignments/creation/part_row"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "-untracked";
},"3":function(depth0,helpers,partials,data) {
    return "-detecting-provider";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<tr>\n	<td>\n		<input type=\"hidden\" name=\""
    + alias2(alias1((depth0 != null ? depth0.formPropertyId : depth0), depth0))
    + "\" value=\""
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" class=\"part-id\" />\n		<input type=\"text\" maxlength=\""
    + alias2(alias1((depth0 != null ? depth0.nameMax : depth0), depth0))
    + "\" name=\""
    + alias2(alias1((depth0 != null ? depth0.formPropertyName : depth0), depth0))
    + "\" value=\""
    + alias2(alias1((depth0 != null ? depth0.name : depth0), depth0))
    + "\" class=\"name\" placeholder=\"Enter name\" />\n	</td>\n	<td>\n		<a class=\"tooltipped tooltipped-n parts-table--tracking-label "
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.shippingProvider : depth0),"OTHER",{"name":"eq","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\"\n		   aria-label=\""
    + alias2(alias1((depth0 != null ? depth0.partsLabel : depth0), depth0))
    + "\"\n		   href=\""
    + alias2(alias1((depth0 != null ? depth0.providerUrl : depth0), depth0))
    + "\">\n			"
    + alias2(alias1((depth0 != null ? depth0.displayTrackingNumber : depth0), depth0))
    + "\n		</a>\n		<input type=\"hidden\" name=\""
    + alias2(alias1((depth0 != null ? depth0.formPropertyNumber : depth0), depth0))
    + "\" value=\""
    + alias2(alias1((depth0 != null ? depth0.trackingNumber : depth0), depth0))
    + "\" class=\"number\" />\n	</td>\n	<td class=\"_provider_ "
    + alias2(alias1((depth0 != null ? depth0.partsClasses : depth0), depth0))
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isPending : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n"
    + ((stack1 = this.invokePartial(partials.spinner,depth0,{"name":"spinner","data":data,"indent":"\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"name":"fedex"},"data":data,"indent":"\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"name":"dhl"},"data":data,"indent":"\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"name":"usps"},"data":data,"indent":"\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"name":"ups"},"data":data,"indent":"\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"name":"truck"},"data":data,"indent":"\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "		<input type=\"hidden\" name=\""
    + alias2(alias1((depth0 != null ? depth0.formPropertyProvider : depth0), depth0))
    + "\" value=\""
    + alias2(alias1((depth0 != null ? depth0.shippingProvider : depth0), depth0))
    + "\" class=\"shipping-provider\" />\n	</td>\n	<td>\n		<input type=\"number\" step=\"0.01\" max=\""
    + alias2(alias1((depth0 != null ? depth0.valueMax : depth0), depth0))
    + "\" min=\""
    + alias2(alias1((depth0 != null ? depth0.valueMin : depth0), depth0))
    + "\" name=\""
    + alias2(alias1((depth0 != null ? depth0.formPropertyPrice : depth0), depth0))
    + "\" value=\""
    + alias2(alias1((depth0 != null ? depth0.partValue : depth0), depth0))
    + "\" placeholder=\"Enter price\" class=\"item-price\" pattern=\"[0-9,.]*\" />\n		<input type=\"hidden\" name=\""
    + alias2(alias1((depth0 != null ? depth0.formPropertyIsReturn : depth0), depth0))
    + "\" value=\""
    + alias2(alias1((depth0 != null ? depth0.isReturn : depth0), depth0))
    + "\" class=\"is-return\" />\n		<i class=\"wm-icon-trash\"></i>\n	</td>\n</tr>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["assignments/creation/requirement"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "		<li>"
    + alias2(alias1((depth0 != null ? depth0.$humanTypeName : depth0), depth0))
    + ": <strong>"
    + alias2(alias1((depth0 != null ? depth0.displayName : depth0), depth0))
    + "</strong></li>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<ul>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.requirements : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</ul>\n<div><small class=\"muted\"><em>* Mandatory</em></small></div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/creation/requirement_set"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "			<i class=\"wm-icon-trash icon-large muted\" data-action=\"trash\"></i>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"cart-item\" data-item=\"requirement-set\">\n	<span>\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.requirementSet : depth0)) != null ? stack1.required : stack1),{"name":"unless","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</span>\n	<span><strong>"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.requirementSet : depth0)) != null ? stack1.name : stack1), depth0))
    + "</strong></span>\n	<input type=\"hidden\" name=\"requirementSetIds\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.requirementSet : depth0)) != null ? stack1.id : stack1), depth0))
    + "\" />\n</div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/actionModalContent"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return "				("
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.distance : stack1), depth0))
    + " mi)\n			";
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return "				W: "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.work_phone : stack1), depth0))
    + "<br/>\n";
},"5":function(depth0,helpers,partials,data) {
    var stack1;

  return "				M: "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.mobile_phone : stack1), depth0))
    + "<br/>\n";
},"7":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "				E: <a href=\"mailto:"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.email : stack1), depth0))
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.email : stack1), depth0))
    + "</a>\n";
},"9":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "				<span class=\"star-rating static "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.rating : stack1), depth0))
    + "\" title=\"Rating\"></span>\n				("
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.num_ratings : stack1), depth0))
    + ")\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div>\n	<small>\n		<span class=\"text-left\">\n			"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.company_name : stack1), depth0))
    + ", "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.address : stack1), depth0))
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.distance : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "<br/>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.work_phone : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.mobile_phone : stack1),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.email : stack1),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</span>\n\n		<span class=\"text-right\">\n			Sent on: "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.sent_on : stack1), depth0))
    + "<br/>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.num_ratings : stack1),{"name":"if","hash":{},"fn":this.program(9, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</span>\n	</small>\n</div>\n\n<hr />\n\n<ul id=\"action-tabs\" class=\"wm-tabs\">\n	<li class=\"wm-tab -active\" data-content=\"#tab-accept\" id=\"action-tab-accept\">Accept</li>\n	<li class=\"wm-tab\" data-content=\"#tab-decline\" id=\"action-tab-decline\">Decline</li>\n	<li class=\"wm-tab\" data-content=\"#tab-question\" id=\"action-tab-question\">Ask Question</li>\n	<li class=\"wm-tab\" data-content=\"#tab-addnote\" id=\"action-tab-addnote\">Add Note</li>\n</ul>\n\n<div id=\"tab-accept\" class=\"-active wm-tab--content\">\n	<form action=\"/assignments/accept_work_on_behalf/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.work_number : stack1), depth0))
    + "\" method=\"post\" class=\"accept_work_form form-stacked\">\n		<input type=\"hidden\" name=\"workerNumber\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.user_number : stack1), depth0))
    + "\" />\n		<div>\n			<label class=\"required\">Note</label>\n			<div class=\"input\">\n				<textarea name=\"note\" class=\"span8\"></textarea>\n			</div>\n		</div>\n		<div class=\"wm-action-container\">\n			<button type=\"button\" class=\"button\" onclick=\"javascript:$.colorbox.close();\">Cancel</button>\n			<button type=\"submit\" class=\"button -primary\">Submit</button>\n		</div>\n	</form>\n</div>\n<div id=\"tab-decline\" class=\"wm-tab--content\">\n	<form action=\"/assignments/decline_work_on_behalf/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.work_number : stack1), depth0))
    + "\" method=\"post\" class=\"decline_work_form form-stacked\">\n		<input type=\"hidden\" name=\"workerNumber\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.user_number : stack1), depth0))
    + "\" />\n		<div>\n			<label class=\"required\">Action Code</label>\n			<div class=\"input\">\n				<select name=\"action_code\" class=\"span8\">\n					<option value=\"0\">Spend Limit too Low</option>\n					<option value=\"1\">Scope is not clear</option>\n					<option value=\"2\">Not qualified</option>\n					<option value=\"3\">Lack needed tools</option>\n					<option value=\"4\">Location too far</option>\n					<option value=\"5\">Unavailable</option>\n					<option value=\"6\">Severe Weather</option>\n					<option value=\"7\">Client Reputation</option>\n					<option value=\"9\">Other</option>\n				</select>\n			</div>\n		</div>\n		<div>\n			<label class=\"required\">Note</label>\n			<div class=\"input\">\n				<textarea name=\"note\" class=\"span8\"></textarea>\n			</div>\n		</div>\n		<div class=\"wm-action-container\">\n			<button type=\"button\" class=\"button\" onclick=\"javascript:$.colorbox.close();\">Cancel</button>\n			<button type=\"submit\" class=\"button -primary\">Submit</button>\n		</div>\n	</form>\n</div>\n<div id=\"tab-question\" class=\"wm-tab--content\">\n	<form class=\"ask_question_form form-stacked\" id=\"ask_question_form\">\n		<input type=\"hidden\" name=\"workerNumber\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.user_number : stack1), depth0))
    + "\" />\n		<input type=\"hidden\" name=\"assignmentNumber\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.work_number : stack1), depth0))
    + "\" />\n		<div>\n			<label class=\"required\">Question</label>\n			<div class=\"input\">\n				<textarea name=\"question\" class=\"span8\"></textarea>\n			</div>\n		</div>\n		<div class=\"wm-action-container\">\n			<button type=\"button\" class=\"button\" onclick=\"javascript:$.colorbox.close();\">Cancel</button>\n			<button type=\"submit\" class=\"button -primary\">Submit</button>\n		</div>\n	</form>\n</div>\n<div id=\"tab-addnote\" class=\"wm-tab--content\">\n	<form class=\"add_note_form form-stacked\" id=\"add_note_form\">\n		<input type=\"hidden\" name=\"workerNumber\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.worker : depth0)) != null ? stack1.user_number : stack1), depth0))
    + "\" />\n		<input type=\"hidden\" name=\"assignmentNumber\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.work_number : stack1), depth0))
    + "\" />\n		<div>\n			<label class=\"required\">Action Code</label>\n			<div class=\"input\">\n				<select name=\"action_code\" class=\"span8\">\n					<option value=\"0\">Interested - will look at it</option>\n					<option value=\"1\">Needs to check schedule</option>\n					<option value=\"2\">Phone Number Not valid</option>\n					<option value=\"3\">Looking into Additional Resource</option>\n					<option value=\"4\">Left message</option>\n					<option value=\"5\">Other</option>\n				</select>\n			</div>\n		</div>\n		<div>\n			<label class=\"required\">Note</label>\n			<div class=\"input\">\n				<textarea name=\"note\" class=\"span8\"></textarea>\n			</div>\n		</div>\n		<div class=\"wm-action-container\">\n			<button type=\"button\" class=\"button\" onclick=\"javascript:$.colorbox.close();\">Cancel</button>\n			<button type=\"submit\" class=\"button -primary\">Submit</button>\n		</div>\n	</form>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/activities"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return "	<div class=\"activity\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isWorkStatusChange : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isWorkSubStatusChange : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isWorkProperty : depth0),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isWorkResourceStatusChange : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isWorkCreated : depth0),{"name":"if","hash":{},"fn":this.program(9, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isWorkNegotiationRequested : depth0),{"name":"if","hash":{},"fn":this.program(11, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isWorkRescheduleRequested : depth0),{"name":"if","hash":{},"fn":this.program(17, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isWorkNegotiationExpired : depth0),{"name":"if","hash":{},"fn":this.program(17, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isWorkNoteCreated : depth0),{"name":"if","hash":{},"fn":this.program(19, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isWorkQuestionAsked : depth0),{"name":"if","hash":{},"fn":this.program(21, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		<small class=\"activity--time\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.onBehalfOfUser : depth0),{"name":"if","hash":{},"fn":this.program(23, data, 0),"inverse":this.program(25, data, 0),"data":data})) != null ? stack1 : "")
    + "		</small>\n		<p class=\"activity--description\">\n			"
    + ((stack1 = this.lambda((depth0 != null ? depth0.text : depth0), depth0)) != null ? stack1 : "")
    + "\n		</p>\n	</div>\n";
},"2":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<span class=\"activity--badge -work-status-change "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isAlert : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.status : depth0), depth0))
    + "</span>\n";
},"3":function(depth0,helpers,partials,data) {
    return "-alert";
},"5":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "			<span class=\"activity--badge -work-sub-status-change\" style=\"background-color:#"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.subStatus : depth0)) != null ? stack1.colorRgb : stack1), depth0))
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.subStatus : depth0)) != null ? stack1.description : stack1), depth0))
    + "</span>\n";
},"7":function(depth0,helpers,partials,data) {
    return "			<i class=\"activity--icon wm-icon-information-filled\"></i>\n";
},"9":function(depth0,helpers,partials,data) {
    return "			<i class=\"activity--icon wm-icon-assignments\"></i>\n";
},"11":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.scheduleNegotiationOnly : depth0),{"name":"if","hash":{},"fn":this.program(12, data, 0),"inverse":this.program(14, data, 0),"data":data})) != null ? stack1 : "");
},"12":function(depth0,helpers,partials,data) {
    return "				<i class=\"activity--icon wm-icon-calendar\"></i>\n";
},"14":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<i class=\"activity--icon wm-icon-payments-filled "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.rejectAction : depth0),{"name":"if","hash":{},"fn":this.program(15, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\"></i>\n";
},"15":function(depth0,helpers,partials,data) {
    return "-reject";
},"17":function(depth0,helpers,partials,data) {
    return "			<i class=\"activity--icon wm-icon-calendar\"></i>\n";
},"19":function(depth0,helpers,partials,data) {
    return "			<i class=\"activity--icon wm-icon-note\"></i>\n";
},"21":function(depth0,helpers,partials,data) {
    return "			<i class=\"activity--icon wm-icon-question-filled\"></i>\n";
},"23":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "				(Action taken on behalf of "
    + alias2(alias1((depth0 != null ? depth0.onBehalfOfUser : depth0), depth0))
    + " at "
    + alias2(alias1((depth0 != null ? depth0.timestampDate : depth0), depth0))
    + ")\n";
},"25":function(depth0,helpers,partials,data) {
    return "				("
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.timestampDate : depth0), depth0))
    + ")\n";
},"27":function(depth0,helpers,partials,data) {
    return "	<div class=\"activity--empty\">There are no activities on this assignment.</div>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.activities : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.program(27, data, 0),"data":data})) != null ? stack1 : "");
},"useData":true});

this["wm"]["templates"]["assignments/details/assignment_label_header"] = Handlebars.template({"1":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "	<span class=\"label nowrap\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.colorRgb : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">\n		<a href=\"/assignments#substatus/"
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "/managing\">"
    + alias2(alias1((depth0 != null ? depth0.description : depth0), depth0))
    + "</a>\n"
    + ((stack1 = helpers['if'].call(depth0,(depths[1] != null ? depths[1].isOwnerOrAdmin : depths[1]),{"name":"if","hash":{},"fn":this.program(4, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</span>\n	<span class=\"separator\">/</span>\n";
},"2":function(depth0,helpers,partials,data) {
    return " style=\"background-color:\\# "
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.colorRgb : depth0), depth0))
    + ";\"";
},"4":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.userResolvable : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"5":function(depth0,helpers,partials,data,blockParams,depths) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "				<a href=\"/assignments/remove_label/"
    + alias2(alias1((depths[1] != null ? depths[1].workNumber : depths[1]), depth0))
    + "?label_id="
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" title=\"Remove Label\" class=\"remove remove_label_action\"><i class=\"icon-remove\"></i></a>\n";
},"7":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isActiveResourceIsAdminIsOwner : depth0),{"name":"if","hash":{},"fn":this.program(8, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"8":function(depth0,helpers,partials,data) {
    return "		<a href=\"/assignments/add_label/"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.workNumber : depth0), depth0))
    + "\" title=\"Add Label\" class=\"add_label_action nowrap\">Add Label</a>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.subStatuses : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.available_labels : depth0),{"name":"if","hash":{},"fn":this.program(7, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"useData":true,"useDepths":true});

this["wm"]["templates"]["assignments/details/buyerScorecardWarning"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<ul class=\"buyer-scorecard-bad-ratings\">\n	<li data-badge=\""
    + alias3(((helper = (helper = helpers.pendingApprovalWorkPercentage || (depth0 != null ? depth0.pendingApprovalWorkPercentage : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"pendingApprovalWorkPercentage","hash":{},"data":data}) : helper)))
    + "\">Pending Approval</li>\n	<li data-badge=\""
    + alias3(((helper = (helper = helpers.pastDueWorkPercentage || (depth0 != null ? depth0.pastDueWorkPercentage : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"pastDueWorkPercentage","hash":{},"data":data}) : helper)))
    + "\">Past Due Work</li>\n	<li data-badge=\""
    + alias3(((helper = (helper = helpers.rating || (depth0 != null ? depth0.rating : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"rating","hash":{},"data":data}) : helper)))
    + "\">Satisfaction Rating</li>\n</ul>\n\n<button type=\"submit\" class=\"button -primary buyer-scorecard-submit-button\">Confirm</button>\n<button class=\"button\" data-modal-close>Cancel</button>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/custom_field_group_set_id"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<input type=\"hidden\" name=\"customFieldGroupSet["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.parent : depth0)) != null ? stack1.groupIndex : stack1), depth0))
    + "].id\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.parent : depth0)) != null ? stack1.id : stack1), depth0))
    + "\">\n<input type=\"hidden\" name=\"customFieldGroupSet["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.parent : depth0)) != null ? stack1.groupIndex : stack1), depth0))
    + "].position\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.parent : depth0)) != null ? stack1.position : stack1), depth0))
    + "\">\n<input type=\"hidden\" name=\"customFieldGroupSet["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.parent : depth0)) != null ? stack1.groupIndex : stack1), depth0))
    + "].name\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.parent : depth0)) != null ? stack1.name : stack1), depth0))
    + "\">\n<input type=\"hidden\" name=\"customFieldGroupSet["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.parent : depth0)) != null ? stack1.groupIndex : stack1), depth0))
    + "].isRequired\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.parent : depth0)) != null ? stack1.isRequired : stack1), depth0))
    + "\">\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/custom_field_group_table"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<table>\n	<tbody class=\"fields\"></tbody>\n</table>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/custom_field_input"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "required";
},"3":function(depth0,helpers,partials,data) {
    return "disabled=\"disabled\"";
},"5":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.isRequired : stack1),{"name":"if","hash":{},"fn":this.program(6, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"6":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.type : stack1),"resource",{"name":"eq","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"7":function(depth0,helpers,partials,data) {
    return "				<small class=\"meta\">Required</small>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<td><label class=\"span3 "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isRequiredCustomField : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.name : stack1), depth0))
    + "</label></td>\n\n<td>\n	<input type=\"hidden\" name=\"customFieldGroupSet["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.groupIndex : stack1), depth0))
    + "].fields["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.index : stack1), depth0))
    + "].id\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.id : stack1), depth0))
    + "\"/>\n	<textarea data-icon=\"check-"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.index : stack1), depth0))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isDisabled : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " maxlength=\"1000\" name=\"customFieldGroupSet["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.groupIndex : stack1), depth0))
    + "].fields["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.index : stack1), depth0))
    + "].value\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.value : stack1), depth0))
    + "\" class=\"field_value "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isRequiredCustomFieldAndTypeWorker : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.value : stack1), depth0))
    + "</textarea>\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.isAdmin : stack1),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n	<a class=\"cf-link tooltipped tooltipped-n dn\" target=\"_blank\" href=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.value : stack1), depth0))
    + "\" aria-label=\"Open Link in New Tab\">\n		<i class=\"link-lock wm-icon-lock-circle muted\"></i>\n	</a>\n</td>\n\n<td><i id=\"check-"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.index : stack1), depth0))
    + "\"></i></td>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/custom_field_select"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "required";
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return "	<input type=\"text\" disabled=\"disabled\" value=\""
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.value : stack1),{"name":"if","hash":{},"fn":this.program(4, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\" class='span5'/>\n";
},"4":function(depth0,helpers,partials,data) {
    var stack1;

  return this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.value : stack1), depth0));
},"6":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<input type=\"hidden\" name=\"customFieldGroupSet["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.groupIndex : stack1), depth0))
    + "].fields["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.index : stack1), depth0))
    + "].id\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.id : stack1), depth0))
    + "\"/>\n	<select name=\"customFieldGroupSet["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.groupIndex : stack1), depth0))
    + "].fields["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.index : stack1), depth0))
    + "].value\" class=\"span5\n		"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isRequiredAndTypeWorker : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n		\" data-icon=\"check-"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.index : stack1), depth0))
    + "\">\n		<option value=\"\">Select</option>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.options : stack1),{"name":"each","hash":{},"fn":this.program(7, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</select>\n";
},"7":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "			<option value=\""
    + alias2(alias1(depth0, depth0))
    + "\" "
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,depth0,((stack1 = (depths[2] != null ? depths[2].model : depths[2])) != null ? stack1.value : stack1),{"name":"eq","hash":{},"fn":this.program(8, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">"
    + alias2(alias1(depth0, depth0))
    + "</option>\n";
},"8":function(depth0,helpers,partials,data) {
    return "selected=\"selected\"";
},"10":function(depth0,helpers,partials,data) {
    return "	<small class=\"meta\">Required</small>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<td>\n	<label class=\"span3"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isRequiredCustomField : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.name : stack1), depth0))
    + "</label>\n</td>\n<td>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isReadOnly : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0, blockParams, depths),"inverse":this.program(6, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isAdminRequiredAndWorkerField : depth0),{"name":"if","hash":{},"fn":this.program(10, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</td>\n<td>\n	<i id=\"check-"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.index : stack1), depth0))
    + "\"></i>\n</td>";
},"useData":true,"useDepths":true});

this["wm"]["templates"]["assignments/details/deliverableDropzone"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"upload-zone\">\n	<div class=\"upload-zone-states\">\n		<div class=\"states\">\n			<div class=\"drag-and-drop-state\">\n				<div class=\"drag-and-drop-here\">\n					<span class=\"drag-and-drop-text\"> Drag &amp; Drop </span>\n				</div>\n			</div>\n			<div class=\"upload-state\">\n				<div class=\"drag-and-drop-icon\"></div>\n			</div>\n		</div>\n	</div>\n</div>";
},"useData":true});

this["wm"]["templates"]["assignments/details/deliverableModalTemplate"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "			<h3 id=\"myModalLabel\">Review/Reject Deliverables</h3>\n";
},"3":function(depth0,helpers,partials,data) {
    return "			<h3 id=\"myModalLabel\">View Your Deliverables</h3>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div id=\"rejectDeliverableForm\" class=\"form-modal\" >\n	<div class=\"modal-header\">\n		<button type=\"button\" class=\"close wm-icon-x\" data-dismiss=\"modal\" aria-hidden=\"true\"></button>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isAdminOrOwner : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.program(3, data, 0),"data":data})) != null ? stack1 : "")
    + "	</div>\n	<div class=\"deliverables-big-preview\">\n		<div class=\"spinner-zone\">\n"
    + ((stack1 = this.invokePartial(partials.spinner,depth0,{"name":"spinner","data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "		</div>\n		<div class=\"image-container\">\n			<div class=\"asset-preview primary\">\n"
    + ((stack1 = this.invokePartial(partials.deliverablePreviewTemplate,depth0,{"name":"deliverablePreviewTemplate","hash":{"includeUpdateButton":true},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			</div>\n			<div class=\"asset-preview secondary\">\n"
    + ((stack1 = this.invokePartial(partials.deliverablePreviewTemplate,depth0,{"name":"deliverablePreviewTemplate","hash":{"includeUpdateButton":false},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			</div>\n		</div>\n	</div>\n	<div class=\"rejection-notes\">\n		<!--rejection-icon.jsp-->\n		<h3>\n			<svg version=\"1.1\" class=\"rejection-icon\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 12 12\" enable-background=\"new 0 0 12 12\" xml:space=\"preserve\">\n				<g>\n					<path d=\"M10.8,2.5L11.3,2c0.4-0.4,0.4-0.9,0-1.3s-0.9-0.4-1.3,0L9.5,1.2C8.5,0.4,7.3,0,6,0C2.7,0,0,2.7,0,6\n						c0,1.3,0.4,2.5,1.2,3.5L0.7,10c-0.4,0.4-0.4,0.9,0,1.3c0.2,0.2,0.4,0.3,0.7,0.3s0.5-0.1,0.7-0.3l0.4-0.4C3.5,11.6,4.7,12,6,12\n						c3.3,0,6-2.7,6-6C12,4.7,11.6,3.5,10.8,2.5z M1.8,6c0-2.3,1.9-4.2,4.2-4.2c0.8,0,1.6,0.2,2.2,0.6L2.5,8.2C2.1,7.6,1.8,6.8,1.8,6z\n						 M6,10.2c-0.8,0-1.6-0.2-2.2-0.6l5.7-5.7c0.4,0.6,0.6,1.4,0.6,2.2C10.2,8.3,8.3,10.2,6,10.2z\"/>\n				</g>\n			</svg>\n			Rejection Notes\n		</h3>\n	</div>\n	<div class=\"modal-body\">\n		<div class=\"modal-thumbnail-carousel\"></div>\n		<div class=\"paging-buttons\"></div>\n	</div>\n	<div class=\"confirmation-to-delete\" style=\"display: none\">\n		<h4 class=\"title\">Are you sure you want to delete this file?</h4>\n		<div class=\"body\">\n			<p>\n				<strong>Note</strong>: if you need to request an updated file from the assigned worker,\n				<strong>consider <a href=\"#\" class=\"switch-to-reject\">Rejecting</a> the deliverable instead of deleting it.</strong>\n				This has the following added benefits:\n			</p>\n			<ul>\n				<li>allows you to specify the issue and notifies the worker</li>\n				<li>adds a \"Rejected\" flag to the deliverable</li>\n				<li>adds a \"Deliverable Rejected\" label to the assignment</li>\n				<li>enables side-by-side comparison of the updated file once provided</li>\n			</ul>\n		</div>\n		<div class=\"actions\">\n			<a href=\"#\" class=\"switch-to-reject\"><strong>Use Reject instead</strong></a>\n			<button class=\"cancel-deletion-of-asset action\">Cancel</button>\n			<button class=\"delete-file action\">Delete File</button>\n		</div>\n	</div>\n</div>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["assignments/details/deliverablePlaceholder"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<li data-deliverable-requirement-id=\""
    + alias3(((helper = (helper = helpers.deliverableRequirementId || (depth0 != null ? depth0.deliverableRequirementId : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"deliverableRequirementId","hash":{},"data":data}) : helper)))
    + "\" data-position=\""
    + alias3(((helper = (helper = helpers.position || (depth0 != null ? depth0.position : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"position","hash":{},"data":data}) : helper)))
    + "\" class=\"placeholder still-needed-placeholder\">\n<label>\n	<div class=\"-"
    + alias3(((helper = (helper = helpers.type || (depth0 != null ? depth0.type : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"type","hash":{},"data":data}) : helper)))
    + "-instance deliverableIcon\">\n	 <svg version=\"1.1\" class=\"deliverables-live-icon icon-position\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 29 29\" enable-background=\"new 0 0 29 29\" xml:space=\"preserve\">\n	<g class=\"deliverables-document-icon\">\n		<g>\n			<g>\n				<path fill=\"#4C5355\" d=\"M20.034,1.5c1.814,0,7.466,6.073,7.466,8.022v14.505c0,1.915-1.558,3.473-3.473,3.473H4.974\n					c-1.916,0-3.474-1.558-3.474-3.473V4.973C1.5,3.058,3.059,1.5,4.974,1.5H20.034 M20.034,0.5H4.974C2.507,0.5,0.5,2.507,0.5,4.973\n					v19.053c0,2.467,2.007,4.473,4.474,4.473h19.053c2.467,0,4.473-2.007,4.473-4.473V9.522C28.5,6.993,22.409,0.5,20.034,0.5\n					L20.034,0.5z\"/>\n			</g>\n		</g>\n		<g>\n			<g opacity=\"0.3\">\n				<path d=\"M19.62,1.219v4.62c0,2.388,1.936,4.325,4.325,4.325H28\"/>\n				<path d=\"M28,10.381h-4.055c-2.505,0-4.542-2.038-4.542-4.543V1.219h0.438v4.619c0,2.264,1.842,4.106,4.105,4.106H28V10.381z\"/>\n			</g>\n			<g>\n				<g>\n					<path fill=\"#FFFFFF\" d=\"M20.12,0.719v4.62c0,2.388,1.936,4.325,4.325,4.325H28.5\"/>\n					<path fill=\"#4C5355\" d=\"M28.5,9.881h-4.055c-2.505,0-4.542-2.038-4.542-4.543V0.719h0.438v4.619\n						c0,2.264,1.842,4.106,4.105,4.106H28.5V9.881z\"/>\n				</g>\n			</g>\n		</g>\n		<path fill=\"#FFFFFF\" d=\"M20.216,0.84c1.872,0,8.179,6.775,8.179,8.695c-2.281-0.997-5.384-3.567-5.384-3.567\n			S20.375,1.584,20.216,0.84z\"/>\n		</g>\n\n	<g class=\"deliverables-photo-icon\">\n	<g>\n		<path fill=\"#FFFFFF\" d=\"M4.474,28C2.283,28,0,25.718,0,23.527V4.474c0-0.19,0.279-0.372,0.306-0.554\n		c0.013-0.081,0.142-0.132,0.145-0.184c0.147-0.757,0.554-1.412,1.061-1.958l0.109-0.095l0.179-0.14\n		c0.54-0.491,1.191-0.825,1.867-0.963c0.131-0.01,0.187,0.236,0.239,0.229C4.132,0.776,4.301,1,4.473,1h19.053\n		C25.718,1,27,2.283,27,4.474v19.052C27,25.718,25.718,28,23.527,28H4.474z\"/>\n		<g>\n			<path fill=\"#4C5355\" d=\"M23.763,1C25.679,1,27,2.559,27,4.474v4.547v10.513v3.992C27,25.442,25.442,27,23.528,27h-4.549H4.473\n			C2.558,27,1,25.442,1,23.527V4.473c0-0.141,0.024-0.3,0.055-0.511c0.01-0.064,0.015-0.13,0.015-0.188\n			C1.195,3.17,1.495,2.597,1.945,2.113C1.971,2.085,1.996,2.055,2.024,2.02C2.067,1.986,2.11,1.952,2.149,1.916\n			c0.476-0.433,1.038-0.725,1.637-0.844C3.851,1.069,3.916,1.063,3.98,1.054l0.061-0.009C4.207,1.021,4.349,1,4.474,1h15.06H24\n			 M23.527,0h-3.992H4.474H4.473C4.255,0,4.046,0.034,3.837,0.064C3.75,0.077,3.659,0.074,3.573,0.091\n			C2.772,0.255,2.059,0.646,1.476,1.177C1.424,1.224,1.362,1.262,1.312,1.312C1.273,1.351,1.244,1.398,1.207,1.438\n			C0.66,2.028,0.259,2.756,0.091,3.574c-0.015,0.077-0.012,0.16-0.024,0.239C0.034,4.03,0,4.247,0,4.473v0.001v19.052v0.001\n			C0,25.994,2.006,28,4.473,28h0.001h14.504h4.549C25.995,28,28,25.993,28,23.527v-3.992V9.022V4.474C28,2.007,25.994,0,23.527,0\n			L23.527,0z\"/>\n		</g>\n	</g>\n	<path fill=\"#828788\" d=\"M4.417,27h14.451h4.713c1.881,0,3.42-1.539,3.42-3.42v-1.732l-4.906-2.423l-2.846-3.782l-3.815,2.232\n	l-4.63-5.625L1,19.92v3.664C1,25.463,2.537,27,4.417,27z\"/>\n	<circle opacity=\"0.4\" fill=\"#828788\" cx=\"7.764\" cy=\"7.684\" r=\"2.717\"/>\n</g>\n\n	<g class=\"deliverables-signoff-icon\">\n	<g>\n		<g>\n			<path fill=\"#FFFFFF\" d=\"M4.474,28C2.283,28,0,25.718,0,23.527V4.474c0-0.19,0.279-0.372,0.306-0.554\n				c0.013-0.081,0.142-0.132,0.145-0.184c0.147-0.757,0.554-1.412,1.061-1.958l0.109-0.095l0.179-0.14\n				c0.54-0.491,1.191-0.825,1.867-0.963c0.131-0.01,0.187,0.236,0.239,0.229C4.132,0.776,4.301,1,4.473,1h19.053\n				C25.718,1,27,2.283,27,4.474v19.052C27,25.718,25.718,28,23.527,28H4.474z\"/>\n			<g>\n				<path fill=\"#4C5355\" d=\"M23.763,1C25.679,1,27,2.559,27,4.474v4.547v10.513v3.992C27,25.442,25.442,27,23.528,27h-4.549H4.473\n					C2.558,27,1,25.442,1,23.527V4.473c0-0.141,0.024-0.3,0.055-0.511c0.01-0.064,0.015-0.13,0.015-0.188\n					C1.195,3.17,1.495,2.597,1.945,2.113C1.971,2.085,1.996,2.055,2.024,2.02C2.067,1.986,2.11,1.952,2.149,1.916\n					c0.476-0.433,1.038-0.725,1.637-0.844C3.851,1.069,3.916,1.063,3.98,1.054l0.061-0.009C4.207,1.021,4.349,1,4.474,1h15.06H24\n					 M23.527,0h-3.992H4.474H4.473C4.255,0,4.046,0.034,3.837,0.064C3.75,0.077,3.659,0.074,3.573,0.091\n					C2.772,0.255,2.059,0.646,1.476,1.177C1.424,1.224,1.362,1.262,1.312,1.312C1.273,1.351,1.244,1.398,1.207,1.438\n					C0.66,2.028,0.259,2.756,0.091,3.574c-0.015,0.077-0.012,0.16-0.024,0.239C0.034,4.03,0,4.247,0,4.473v0.001v19.052v0.001\n					C0,25.994,2.006,28,4.473,28h0.001h14.504h4.549C25.995,28,28,25.993,28,23.527v-3.992V9.022V4.474C28,2.007,25.994,0,23.527,0\n					L23.527,0z\"/>\n			</g>\n		</g>\n	</g>\n	<path opacity=\"0.4\" fill=\"#828788\" d=\"M7.358,17.387c-0.334-0.172-1.173-1.385-2.024-2.743c0.852-1.358,1.69-2.57,2.024-2.743\n		c0.25-0.128,0.348-0.434,0.219-0.684c-0.128-0.25-0.433-0.347-0.683-0.219c-0.499,0.257-1.337,1.41-2.153,2.681\n		c-0.572-0.949-1.098-1.869-1.415-2.468c-0.132-0.248-0.438-0.342-0.687-0.211c-0.247,0.131-0.342,0.438-0.21,0.686\n		c0.015,0.029,0.784,1.441,1.709,2.957c-0.925,1.515-1.694,2.928-1.709,2.957c-0.132,0.248-0.037,0.555,0.21,0.686\n		c0.076,0.04,0.158,0.059,0.238,0.059c0.182,0,0.358-0.098,0.449-0.27c0.317-0.599,0.842-1.519,1.415-2.468\n		c0.816,1.271,1.654,2.424,2.153,2.681c0.074,0.039,0.154,0.057,0.232,0.057c0.184,0,0.361-0.1,0.451-0.275\n		C7.706,17.822,7.608,17.516,7.358,17.387z\"/>\n	<g>\n		<path fill=\"#5D5D5D\" d=\"M17.554,16.115c-0.641,0.774-1.011,0.848-1.509,0.436c-0.489,0.05-0.86,0.088-1.23,0.126\n			c0.062-0.407,0.123-0.815,0.195-1.285c-0.436,0.141-0.598,0.574-0.809,0.949c-0.162,0.289-0.301,0.595-0.782,0.327\n			c0.198-0.384,0.392-0.76,0.587-1.137c-0.038-0.024-0.076-0.048-0.115-0.071c-0.24,0.309-0.463,0.633-0.726,0.922\n			c-0.257,0.283-0.533,0.576-0.989,0.321c-0.086-0.048-0.23-0.022-0.34,0.003c-0.711,0.162-0.869,0.014-0.751-0.714\n			c0.009-0.056,0.001-0.115,0.001-0.175c-0.788-0.142-0.631,1.262-1.575,0.909c0.073-0.233,0.141-0.452,0.21-0.673\n			c-0.397-0.233-0.682-0.23-1.001,0.154c-0.589,0.708-1.263,0.812-2.123,0.534c-0.293-0.095-0.644-0.001-0.968-0.011\n			c-0.245-0.008-0.197-0.215-0.16-0.167c0.364-0.214,0.752-0.397,1.086-0.652c0.334-0.255,0.642-0.559,0.906-0.885\n			c0.132-0.163,0.215-0.432,0.194-0.641c-0.096-0.982,0.128-1.347,1.007-1.733c0.206-0.091,0.37-0.394,0.445-0.633\n			c0.086-0.275,0.052-0.588,0.07-0.885c0.052,0.003,0.114-0.009,0.124,0.008c0.45,0.807,0.804,0.906,1.626,0.423\n			c0.328-0.193,0.638-0.417,1.002-0.581c-0.412,0.545-0.814,1.098-1.239,1.633c-0.43,0.542-0.474,1.078,0.017,1.713\n			c0.753-1.082,1.473-2.179,2.706-2.839c-0.876,1.325-1.722,2.605-2.616,3.958c0.911-0.216,1.662-0.395,2.414-0.573\n			c0.044,0.068,0.088,0.136,0.132,0.204c-0.209,0.407-0.418,0.814-0.627,1.22c0.052,0.035,0.104,0.069,0.156,0.104\n			c0.243-0.299,0.482-0.601,0.729-0.897c0.291-0.349,0.574-0.709,1.113-0.396c0.079,0.046,0.225-0.041,0.339-0.037\n			c0.206,0.008,0.548-0.021,0.589,0.072c0.078,0.18,0.014,0.441-0.043,0.654c-0.048,0.18-0.168,0.34-0.256,0.509\n			c0.055,0.046,0.109,0.091,0.164,0.137c0.213-0.229,0.415-0.468,0.64-0.683c0.271-0.261,0.531-0.562,0.855-0.729\n			c0.193-0.1,0.501,0.023,0.678,0.039c-0.381,0.327-0.722,0.583-1.009,0.889c-0.087,0.093-0.024,0.327-0.029,0.497\n			c0.159-0.042,0.327-0.063,0.474-0.131c0.143-0.066,0.265-0.179,0.396-0.272C17.527,16.074,17.541,16.094,17.554,16.115z\n			 M8.507,15.196c0.342-0.587,0.678-1.131,0.974-1.696c0.068-0.129-0.004-0.331-0.011-0.499c-0.176,0.069-0.424,0.087-0.516,0.217\n			c-0.267,0.374-0.452,0.804-0.706,1.188C8.03,14.737,8.097,14.957,8.507,15.196z M6.536,16.342\n			c0.673,0.285,1.278,0.061,1.647-0.595c0.244-0.433-0.106-0.539-0.311-0.709C7.406,15.493,6.971,15.918,6.536,16.342z\n			 M9.196,15.496c0.035,0.083,0.07,0.167,0.105,0.25c0.243-0.058,0.603-0.037,0.707-0.19c0.347-0.51,0.374-1.096,0.026-1.728\n			C9.755,14.384,9.475,14.94,9.196,15.496z M9.399,11.952c-0.098,0.316-0.386,0.617,0.119,0.802\n			c0.672,0.246,0.622-0.457,1.006-0.673C10.11,12.034,9.781,11.996,9.399,11.952z M8.865,12.923c-0.9,0.191-1.145,0.667-0.862,1.527\n			C8.293,13.936,8.541,13.496,8.865,12.923z M11.845,16.543c0.274-0.416,0.548-0.831,0.822-1.247\n			C11.933,15.393,11.599,15.904,11.845,16.543z\"/>\n		<path fill=\"#5D5D5D\" d=\"M17.512,16.054c0.269-0.295,0.49-0.674,0.82-0.86c0.267-0.15,0.792-0.216,0.976-0.057\n			c0.335,0.291,0.538,0.209,0.845,0.049c0.207-0.109,0.453-0.19,0.683-0.192c0.157-0.001,0.316,0.155,0.465,0.344\n			c-0.598-0.254-0.819,0.134-1.006,0.509c-0.088,0.176-0.074,0.402-0.106,0.605c0.194-0.057,0.454-0.053,0.572-0.18\n			c0.573-0.617,1.119-1.262,1.655-1.912c0.536-0.651,1.024-1.345,1.587-1.972c0.215-0.239,0.579-0.343,0.948-0.549\n			c0.129,0.707-0.262,0.981-0.613,1.2c-0.675,0.42-1.163,0.98-1.502,1.686c-0.04,0.084-0.064,0.175-0.114,0.318\n			c0.135-0.024,0.211-0.057,0.281-0.047c0.25,0.036,0.499,0.088,0.748,0.133c-0.145,0.254-0.241,0.564-0.448,0.746\n			c-0.192,0.168-0.504,0.198-0.768,0.291c0.353,0.465,0.671,0.928,1.04,1.345c0.144,0.163,0.404,0.225,0.613,0.328\n			c0.572,0.279,1.169,0.517,1.704,0.855c0.176,0.111,0.336,0.581,0.248,0.703c-0.14,0.194-0.502,0.339-0.756,0.32\n			c-0.759-0.056-1.304-0.539-1.686-1.15c-0.298-0.477-0.692-0.633-1.204-0.636c-1.369-0.006-2.738-0.013-4.107-0.02\n			c-0.198-0.001-0.395,0-0.593,0c-0.003-0.069-0.006-0.139-0.008-0.208c1.698-0.069,3.395-0.137,5.171-0.209\n			c-0.26-0.41-0.519-0.818-0.757-1.194c-0.358,0.223-0.634,0.395-0.91,0.566c-0.043-0.045-0.086-0.09-0.13-0.136\n			c0.057-0.195,0.115-0.389,0.127-0.432c-0.433,0.13-0.931,0.318-1.444,0.388c-0.073,0.01-0.224-0.552-0.364-0.914\n			c-0.087,0.121-0.201,0.278-0.314,0.436c-0.578,0.808-1.164,0.775-1.609-0.091C17.541,16.094,17.527,16.074,17.512,16.054z\n			 M24.234,18.154c-0.043,0.078-0.087,0.156-0.13,0.234c0.377,0.316,0.73,0.669,1.141,0.932c0.175,0.112,0.47,0.037,0.711,0.048\n			c-0.118-0.227-0.178-0.553-0.364-0.661C25.172,18.465,24.69,18.331,24.234,18.154z M18.218,16.534\n			c0.791-0.286,1.005-0.69,0.716-1.297C18.487,15.54,18.149,15.87,18.218,16.534z M22.235,15.795\n			c0.684,0.021,0.777-0.052,0.847-0.665C22.741,15.397,22.488,15.596,22.235,15.795z\"/>\n	</g>\n</g>\n\n	<g class=\"deliverables-other-icon\">\n			<g>\n				<g>\n					<path fill=\"#FFFFFF\" d=\"M4.474,28C2.283,28,0,25.718,0,23.527V4.474c0-0.19,0.279-0.372,0.306-0.554\n				c0.013-0.081,0.142-0.132,0.145-0.184c0.147-0.757,0.554-1.412,1.061-1.958l0.109-0.095l0.179-0.14\n				c0.54-0.491,1.191-0.825,1.867-0.963c0.131-0.01,0.187,0.236,0.239,0.229C4.132,0.776,4.301,1,4.473,1h19.053\n				C25.718,1,27,2.283,27,4.474v19.052C27,25.718,25.718,28,23.527,28H4.474z\"/>\n					<g>\n						<path fill=\"#4C5355\" d=\"M23.763,1C25.679,1,27,2.559,27,4.474v4.547v10.513v3.992C27,25.442,25.442,27,23.528,27h-4.549H4.473\n					C2.558,27,1,25.442,1,23.527V4.473c0-0.141,0.024-0.3,0.055-0.511c0.01-0.064,0.015-0.13,0.015-0.188\n					C1.195,3.17,1.495,2.597,1.945,2.113C1.971,2.085,1.996,2.055,2.024,2.02C2.067,1.986,2.11,1.952,2.149,1.916\n					c0.476-0.433,1.038-0.725,1.637-0.844C3.851,1.069,3.916,1.063,3.98,1.054l0.061-0.009C4.207,1.021,4.349,1,4.474,1h15.06H24\n					 M23.527,0h-3.992H4.474H4.473C4.255,0,4.046,0.034,3.837,0.064C3.75,0.077,3.659,0.074,3.573,0.091\n					C2.772,0.255,2.059,0.646,1.476,1.177C1.424,1.224,1.362,1.262,1.312,1.312C1.273,1.351,1.244,1.398,1.207,1.438\n					C0.66,2.028,0.259,2.756,0.091,3.574c-0.015,0.077-0.012,0.16-0.024,0.239C0.034,4.03,0,4.247,0,4.473v0.001v19.052v0.001\n					C0,25.994,2.006,28,4.473,28h0.001h14.504h4.549C25.995,28,28,25.993,28,23.527v-3.992V9.022V4.474C28,2.007,25.994,0,23.527,0\n					L23.527,0z\"/>\n					</g>\n				</g>\n			</g>\n			<path opacity=\"0.5\" fill=\"#828788\" d=\"M13.342,18c-0.453,0-1.03,1-1.58,2H23v-2H13.342z\"/>\n			<path fill=\"#828788\" d=\"M7.659,23.039c-0.319,0-0.621-0.152-0.81-0.413c-0.433-0.596-1.168-1.448-1.706-2.069\n		c-0.852-0.986-1.08-1.25-0.803-1.858c0.163-0.356,0.521-0.585,0.912-0.585c0.378,0,0.707,0.209,0.877,0.519\n		c0.098,0.123,0.291,0.344,0.527,0.617c0.257,0.297,0.558,0.646,0.854,1c1.034-1.808,2.896-4.884,3.873-5.388\n		c0.489-0.253,1.094-0.06,1.347,0.431c0.253,0.491,0.061,1.094-0.431,1.347c-0.584,0.426-2.857,4.167-3.758,5.868\n		c-0.161,0.305-0.469,0.505-0.813,0.53C7.706,23.038,7.683,23.039,7.659,23.039z\"/>\n			<path opacity=\"0.5\" fill=\"#828788\" d=\"M13.342,9c-0.453,0-1.03,1-1.58,2H23V9H13.342z\"/>\n			<path fill=\"#828788\" d=\"M7.659,14.289c-0.319,0-0.621-0.152-0.81-0.413c-0.433-0.596-1.168-1.448-1.706-2.069\n		c-0.852-0.986-1.08-1.25-0.803-1.858c0.163-0.356,0.521-0.585,0.912-0.585c0.378,0,0.707,0.209,0.877,0.519\n		c0.098,0.123,0.291,0.344,0.527,0.617c0.257,0.297,0.558,0.646,0.854,1c1.034-1.808,2.896-4.884,3.873-5.388\n		c0.489-0.252,1.094-0.06,1.347,0.431s0.061,1.094-0.431,1.347c-0.584,0.426-2.857,4.167-3.758,5.868\n		c-0.161,0.305-0.469,0.505-0.813,0.53C7.706,14.288,7.683,14.289,7.659,14.289z\"/>\n		</g>\n	</svg>\n	</div>\n	<form class=\"deliverable-form\">\n		<input type=\"file\" class=\"deliverable-upload\" data-deliverable-requirement-id=\""
    + alias3(((helper = (helper = helpers.deliverableRequirementId || (depth0 != null ? depth0.deliverableRequirementId : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"deliverableRequirementId","hash":{},"data":data}) : helper)))
    + "\" data-position=\""
    + alias3(((helper = (helper = helpers.position || (depth0 != null ? depth0.position : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"position","hash":{},"data":data}) : helper)))
    + "\" name=\"files[]\"/>\n	</form>\n</label>\n</li>";
},"useData":true});

this["wm"]["templates"]["assignments/details/deliverableRequirementHeader"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<p class=\"orange-requirement-square\"></p>\n<div class=\"all-requirements-fulfilled\">\n	<svg version=\"1.1\" class=\"green-checkmark-icon\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 18 18\" enable-background=\"new 0 0 18 18\" xml:space=\"preserve\">\n		<path class=\"background\" d=\"M18,16.4c0,0.9-0.7,1.6-1.6,1.6H1.6C0.7,18,0,17.3,0,16.4V1.6C0,0.7,0.7,0,1.6,0h14.8C17.3,0,18,0.7,18,1.6\n		V16.4z\"/>\n		<path class=\"checkmark\" d=\"M14.7,5.4c-1.2,0.5-3.1,4.2-4.2,6.4c-0.6,1.2-1.1,2.2-1.6,3c-0.3,0.4-0.7,0.7-1.2,0.7c0,0,0,0,0,0\n		c-0.5,0-0.9-0.2-1.2-0.6c-0.7-1-2.8-3.1-3.5-3.7C2.2,10.6,2.2,9.6,2.8,9C3.3,8.4,4.3,8.4,4.9,9c0.2,0.2,1.4,1.3,2.5,2.4\n		c0.2-0.3,0.3-0.6,0.5-1c2.1-4.1,3.7-7,5.8-7.8c0.8-0.3,1.6,0.1,1.9,0.9C15.8,4.2,15.5,5.1,14.7,5.4z\"/>\n	</svg>\n</div>\n<h6>\n	<span id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\"> "
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + " Required </span>\n</h6>\n<p class=\"still-needed-reqs\"></p>\n<hr>\n<p class=\"instructions-for-group\">"
    + alias3(((helper = (helper = helpers.instructions || (depth0 != null ? depth0.instructions : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"instructions","hash":{},"data":data}) : helper)))
    + "</p>";
},"useData":true});

this["wm"]["templates"]["assignments/details/deliverableUnorderedList"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper;

  return "<ul class=\""
    + this.escapeExpression(((helper = (helper = helpers.cssClass || (depth0 != null ? depth0.cssClass : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"cssClass","hash":{},"data":data}) : helper)))
    + "\"></ul>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/deliverableUploaderContainer"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<div class=\"upload-bottom-bar\">\n	<a title=\"Download All From This Requirement\" class=\"download-all-button\" id=\"bulk_download_deliverable_assets\" href=\"/assignments/download_deliverable_assets/"
    + alias3(((helper = (helper = helpers.workNumber || (depth0 != null ? depth0.workNumber : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"workNumber","hash":{},"data":data}) : helper)))
    + "/deliverable_requirement/"
    + alias3(((helper = (helper = helpers.deliverableRequirementId || (depth0 != null ? depth0.deliverableRequirementId : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"deliverableRequirementId","hash":{},"data":data}) : helper)))
    + "\">\n		<svg version=\"1.1\" class=\"download-all-icon\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\"\n	 viewBox=\"0 0 28 28\" enable-background=\"new 0 0 28 28\" xml:space=\"preserve\">\n	<g class=\"foreground\">\n		<g>\n			<path d=\"M15,18.8c-0.3,0.3-0.6,0.4-1,0.4s-0.7-0.1-1-0.4l-1.4-1.4H7V21h14v-3.5h-4.7L15,18.8z M17.1,18.4h2.4\n				c0.3,0,0.5,0.6,0.5,0.9c0,0.3-0.2,0.9-0.5,0.9h-2.4c-0.3,0-0.5-0.6-0.5-0.9C16.6,19,16.8,18.4,17.1,18.4z\"/>\n		</g>\n	</g>\n	<g class=\"foreground\">\n		<path d=\"M13,17.1c0.3,0.3,0.6,0.4,1,0.4c0.4,0,0.7-0.1,1-0.4l4.7-4.7c0.6-0.6,0.4-1.1,0.4-1.3\n			c-0.1-0.2-0.5-0.6-1.3-0.6l-1.7,0V6.7C17,6.3,17,6,16.6,6h-5.2C11,6,11,6.3,11,6.7v3.8l-1.7,0c-0.8,0-1.2,0.4-1.2,0.6\n			c-0.1,0.2-0.2,0.7,0.3,1.3L13,17.1z\"/>\n	</g>\n\n	<g class=\"background\">\n		<g>\n			<path d=\"M23.5,0h-4H4.5h0C4.3,0,4,0,3.8,0.1c-0.1,0-0.2,0-0.3,0C2.8,0.3,2.1,0.6,1.5,1.2c-0.1,0-0.1,0.1-0.2,0.1\n				c0,0-0.1,0.1-0.1,0.1C0.7,2,0.3,2.8,0.1,3.6c0,0.1,0,0.2,0,0.2C0,4,0,4.2,0,4.5v0v19.1v0C0,26,2,28,4.5,28h0H19h4.5\n				c2.5,0,4.5-2,4.5-4.5v-4V9V4.5C28,2,26,0,23.5,0z M7.9,11.1C8,11,8.3,10.5,9.1,10.5l1.6,0V6.7C10.7,6.3,11,6,11.4,6h5.2\n				c0.4,0,0.7,0.3,0.7,0.7v3.8l1.6,0c0.8,0,1.1,0.4,1.2,0.6c0.1,0.2,0.2,0.7-0.4,1.3L15,17.1c-0.3,0.3-0.6,0.4-1,0.4\n				c-0.4,0-0.7-0.1-1-0.4l-4.7-4.7C7.7,11.8,7.9,11.3,7.9,11.1z M21,21H7v-3.5h4.7l1.4,1.4c0.3,0.3,0.6,0.4,1,0.4s0.7-0.1,1-0.4\n				l1.4-1.4H21V21z\"/>\n		</g>\n	</g>\n	<g class=\"background\">\n		<path d=\"M17.1,20h2.4c0.3,0,0.5-0.7,0.5-1c0-0.3-0.2-1-0.5-1h-2.4c-0.3,0-0.5,0.7-0.5,1C16.6,19.3,16.8,20,17.1,20\n			z\"/>\n	</g>\n</svg>\n\n\n	</a>\n	<div class=\"deliverableUploader -v2\">\n		<div id=\""
    + alias3(((helper = (helper = helpers.containerId || (depth0 != null ? depth0.containerId : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"containerId","hash":{},"data":data}) : helper)))
    + "\" class=\"attachment-uploader\">\n			<form>\n				<a href=\"javascript:void(0);\" class=\"qq-upload-button wm-icon-upload\">\n					Upload Files\n					<input type=\"file\" name=\"files[]\" multiple>\n				</a>\n			</form>\n		</div>\n	</div>\n</div>";
},"useData":true});

this["wm"]["templates"]["assignments/details/deliverable_asset"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "			<div class=\"thumbnail-background\" data-position=\""
    + alias2(alias1(((stack1 = ((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.attributes : stack1)) != null ? stack1.position : stack1), depth0))
    + "\" data-uri=\""
    + alias2(alias1(((stack1 = ((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.attributes : stack1)) != null ? stack1.uri : stack1), depth0))
    + "\" style=\"background-image: url("
    + alias2(alias1(((stack1 = ((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.attributes : stack1)) != null ? stack1.uri : stack1), depth0))
    + ");\"></div>\n";
},"3":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<div class=\"-"
    + alias2(alias1(((stack1 = ((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.attributes : stack1)) != null ? stack1.type : stack1), depth0))
    + "-instance deliverableIcon\">\n			<svg version=\"1.1\"  class=\"deliverables-live-icon icon-position file-type-icon\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 29 29\" enable-background=\"new 0 0 29 29\" xml:space=\"preserve\">\n					<g>\n						<path fill=\"#949899\" d=\"M16.744,1C18.264,1,23,6.097,23,7.733v12.433C23,21.729,21.729,23,20.166,23H3.835\n								C2.272,23,1,21.729,1,20.166V3.834C1,2.271,2.272,1,3.835,1H16.744 M16.744,0H3.835C1.721,0,0,1.72,0,3.834v16.331\n								C0,22.28,1.721,24,3.835,24h16.331C22.281,24,24,22.28,24,20.166V7.733C24,5.565,18.779,0,16.744,0L16.744,0z\"/>\n						<g opacity=\"0.3\">\n							<path d=\"M16.318,0.687v3.96c0,2.047,1.66,3.707,3.707,3.707H23.5\"/>\n							<path d=\"M23.5,8.541h-3.476c-2.147,0-3.894-1.747-3.894-3.894V0.688h0.375v3.959c0,1.94,1.579,3.519,3.519,3.519H23.5V8.541z\"/>\n						</g>\n						<path fill=\"#FFFFFF\" d=\"M16.818,0.187v3.96c0,2.047,1.66,3.707,3.707,3.707H24\"/>\n						<path fill=\"#4C5355\" d=\"M24,8.041h-3.476c-2.147,0-3.894-1.747-3.894-3.894V0.188h0.375v3.959c0,1.94,1.579,3.519,3.519,3.519\n									H24V8.041z\"/>\n						<text fill=\"#4C5355\" font-weight=\"600\" font-family=\"Open Sans\" "
    + ((stack1 = alias1((depth0 != null ? depth0.extensionSize : depth0), depth0)) != null ? stack1 : "")
    + ">"
    + alias2(alias1((depth0 != null ? depth0.upperExtension : depth0), depth0))
    + "</text>\n					</g>\n				</svg>\n		</div>\n";
},"5":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<a href=\"/asset/download/"
    + this.escapeExpression(this.lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.attributes : stack1)) != null ? stack1.uuid : stack1), depth0))
    + "\">\n			<svg class=\"deliverable-download\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.0\" x=\"0px\" y=\"0px\" viewBox=\"0 0 32 24\" enable-background=\"new 0 0 32 24\" xml:space=\"preserve\">\n				<path d=\"M29.167 24H2.833C1.269 24 0 22.7 0 21.2V2.8C0 1.3 1.3 0 2.8 0h26.333C30.731 0 32 1.3 32 2.8 v18.4C32 22.7 30.7 24 29.2 24z\" class=\"background\"/>\n				<path d=\"M18.025 16l-1.373 1.362c-0.261 0.259-0.608 0.402-0.977 0.4 c-0.369 0-0.716-0.143-0.976-0.401L13.323 16H8v3.528h16V16H18.025z M22.553 18.646h-2.465c-0.3 0-0.543-0.582-0.543-0.882 c0-0.3 0.243-0.882 0.543-0.882h2.465c0.3 0 0.5 0.6 0.5 0.882C23.095 18.1 22.9 18.6 22.6 18.646z\" class=\"foreground\"/>\n				<path d=\"M18.331 3h-5.582c-0.423 0-0.766 0.34-0.766 0.759v4.047l-1.677-0.002c-0.854 0-1.145 0.478-1.231 0.7 C8.99 8.7 8.9 9.2 9.5 9.835l5.048 5.002c0.276 0.3 0.6 0.4 1 0.425c0.391 0 0.759-0.151 1.035-0.426 l5.044-5.002c0.603-0.597 0.468-1.14 0.382-1.346c-0.086-0.206-0.378-0.684-1.231-0.684l-1.676 0.002V3.759 C19.096 3.3 18.8 3 18.3 3L18.331 3z\" class=\"foreground\"/>\n			</svg>\n		</a>\n		<svg class=\"remove\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.0\" x=\"0px\" y=\"0px\" viewBox=\"0 0 32 24\" enable-background=\"new 0 0 32 24\" xml:space=\"preserve\">\n			<path d=\"M29.167 24H2.833C1.269 24 0 22.7 0 21.2V2.8C0 1.3 1.3 0 2.8 0h26.333C30.731 0 32 1.3 32 2.8 v18.4C32 22.7 30.7 24 29.2 24z\" class=\"background\"/>\n			<polygon points=\"19,4 19,3 14,3 14,4 10,4 10,6 23,6 23,4\" class=\"foreground\"/>\n			<polygon points=\"11.1,7 12.1,19 20.7,19 21.7,7\" class=\"foreground\"/>\n		</svg>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isAdmin : depth0),{"name":"if","hash":{},"fn":this.program(6, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"6":function(depth0,helpers,partials,data) {
    return "			<svg class=\"rejection-button\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 32 24\" enable-background=\"new 0 0 32 24\" xml:space=\"preserve\">\n				<path class=\"background\" d=\"M29.2,24H2.8C1.3,24,0,22.7,0,21.2V2.8C0,1.3,1.3,0,2.8,0h26.3C30.7,0,32,1.3,32,2.8v18.4 C32,22.7,30.7,24,29.2,24z\"/>\n				<path class=\"foreground\" d=\"M22.4,7.3L23,6.7c0.5-0.5,0.5-1.2,0-1.7c-0.5-0.5-1.2-0.5-1.7,0l-0.6,0.6c-1.3-1-3-1.6-4.7-1.6 c-4.4,0-8,3.6-8,8c0,1.8,0.6,3.4,1.6,4.7L9,17.3c-0.5,0.5-0.5,1.2,0,1.7c0.2,0.2,0.5,0.4,0.8,0.4c0.3,0,0.6-0.1,0.8-0.4l0.6-0.6 c1.3,1,3,1.6,4.7,1.6c4.4,0,8-3.6,8-8C24,10.2,23.4,8.6,22.4,7.3z M10.4,12c0-3.1,2.5-5.6,5.6-5.6c1.1,0,2.1,0.3,3,0.9L11.3,15 C10.7,14.1,10.4,13.1,10.4,12z M16,17.6c-1.1,0-2.1-0.3-3-0.9L20.7,9c0.6,0.9,0.9,1.9,0.9,3C21.6,15.1,19.1,17.6,16,17.6z\"/>\n			</svg>\n";
},"8":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<a class=\"asset-name\" href=\"/asset/download/"
    + this.escapeExpression(this.lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.attributes : stack1)) != null ? stack1.uuid : stack1), depth0))
    + "\">\n			<svg class=\"deliverable-download\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.0\" x=\"0px\" y=\"0px\" viewBox=\"0 0 32 24\" enable-background=\"new 0 0 32 24\" xml:space=\"preserve\">\n				<path d=\"M29.167 24H2.833C1.269 24 0 22.7 0 21.2V2.8C0 1.3 1.3 0 2.8 0h26.333C30.731 0 32 1.3 32 2.8 v18.4C32 22.7 30.7 24 29.2 24z\" class=\"background\"/>\n				<path d=\"M18.025 16l-1.373 1.362c-0.261 0.259-0.608 0.402-0.977 0.4 c-0.369 0-0.716-0.143-0.976-0.401L13.323 16H8v3.528h16V16H18.025z M22.553 18.646h-2.465c-0.3 0-0.543-0.582-0.543-0.882 c0-0.3 0.243-0.882 0.543-0.882h2.465c0.3 0 0.5 0.6 0.5 0.882C23.095 18.1 22.9 18.6 22.6 18.646z\" class=\"foreground\"/>\n				<path d=\"M18.331 3h-5.582c-0.423 0-0.766 0.34-0.766 0.759v4.047l-1.677-0.002c-0.854 0-1.145 0.478-1.231 0.7 C8.99 8.7 8.9 9.2 9.5 9.835l5.048 5.002c0.276 0.3 0.6 0.4 1 0.425c0.391 0 0.759-0.151 1.035-0.426 l5.044-5.002c0.603-0.597 0.468-1.14 0.382-1.346c-0.086-0.206-0.378-0.684-1.231-0.684l-1.676 0.002V3.759 C19.096 3.3 18.8 3 18.3 3L18.331 3z\" class=\"foreground\"/>\n			</svg>\n		</a>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"deliverable-asset-thumbnail\">\n	<span class=\"modal-opener\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isImage : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isImage : depth0),{"name":"unless","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</span>\n	<div class=\"updatedState\">\n		<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 64 64\" enable-background=\"new 0 0 64 64\" xml:space=\"preserve\">\n			<polygon fill=\"#198FCE\" points=\"62.6,64 0,1.4 31.2,1.4 62.6,32.8 \"/>\n			<text transform=\"matrix(0.7071 0.7071 -0.7071 0.7071 19.3148 10.6369)\" fill=\"#FFFFFF\" font-family=\"'Helvetica'\" font-size=\"12\">Updated</text>\n		</svg>\n	</div>\n	<div class=\"rejectedState\">\n		<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 64 64\" enable-background=\"new 0 0 64 64\" xml:space=\"preserve\">\n			<polygon fill=\"#DF4343\" points=\"0,1.4 62.6,64 62.6,32.8 31.2,1.4 \"/>\n			<text transform=\"matrix(0.7071 0.7071 -0.7071 0.7071 20.0145 11.784)\" fill=\"#FFFFFF\" font-family=\"'Helvetica'\" font-size=\"12\">Rejected</text>\n		</svg>\n	</div>\n</div>\n<div class=\"options\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isPermission : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isPermission : depth0),{"name":"unless","hash":{},"fn":this.program(8, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/desktopPartsHeaderTemplate"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "-active-worker-exists";
},"3":function(depth0,helpers,partials,data) {
    return " -is-company";
},"5":function(depth0,helpers,partials,data) {
    return " -is-supplied-by-worker";
},"7":function(depth0,helpers,partials,data) {
    return " returned to";
},"9":function(depth0,helpers,partials,data) {
    return " sent to";
},"11":function(depth0,helpers,partials,data) {
    return " -isReturn";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\""
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isNotSentOrDraft : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isOwnerOrAdmin : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isSuppliedByWorker : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n<div>\n	<div class=\"parts-table--part-information\">\n		<!--truck icon-->\n		<svg class=\"icon-truck\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 36 36\" enable-background=\"new 0 0 36 36\" xml:space=\"preserve\">\n		  <g class=\"-is-sent\">\n			<path fill=\"#939799\" d=\"M34.4,15.9c-1.3-1.7-3.2-2.8-4.8-3.4c-0.8-0.3-1.8-0.5-2.6-0.5h-0.3c-0.7,0-0.5,0.6-0.5,1.3v9.4c0,0.7-0.2,2.2,0.5,2.2h6.8c0.7,0,1.7-1.5,1.7-2.2v-5.1C35.2,17.1,34.9,16.5,34.4,15.9z\"/>\n			<circle fill=\"#5D5D5D\" stroke=\"#FFFFFF\" stroke-miterlimit=\"10\" cx=\"30.2\" cy=\"24.2\" r=\"2.4\"/>\n			<path fill=\"#B4B7B7\" d=\"M22.4,9H11.3c-1.1,0-2,0.9-2,2v11.2c0,1.1,0.9,2,2,2h11.2c1.1,0,2-0.9,2-2V11C24.4,9.9,23.5,9,22.4,9z\"/>\n			<circle fill=\"#5D5D5D\" stroke=\"#FFFFFF\" stroke-miterlimit=\"10\" cx=\"16.8\" cy=\"24.2\" r=\"2.4\"/>\n			<path fill=\"#FFFFFF\" d=\"M32.5,16.4c-1.1-2-2.8-2.6-4-2.6h-0.1c-0.5,0-1,0.4-1,1v1.5c0,0.3,0.3,0.6,0.6,0.6h4.2C32.6,16.9,32.7,16.6,32.5,16.4L32.5,16.4z\"/>\n			<line fill=\"none\" stroke=\"#939799\" stroke-linecap=\"square\" stroke-miterlimit=\"10\" x1=\"7.2\" y1=\"12.6\" x2=\"1.2\" y2=\"12.6\"/>\n			<line fill=\"none\" stroke=\"#939799\" stroke-linecap=\"square\" stroke-miterlimit=\"10\" x1=\"7.2\" y1=\"15.6\" x2=\"3.2\" y2=\"15.6\"/>\n			<line fill=\"none\" stroke=\"#939799\" stroke-linecap=\"square\" stroke-miterlimit=\"10\" x1=\"7.2\" y1=\"18.6\" x2=\"5.2\" y2=\"18.6\"/>\n		  </g>\n		  <g class=\"-is-return\">\n			<line fill=\"none\" stroke=\"#939799\" stroke-linecap=\"square\" stroke-miterlimit=\"10\" x1=\"25\" y1=\"3.7\" x2=\"33\" y2=\"3.7\"/>\n			<line fill=\"none\" stroke=\"#939799\" stroke-linecap=\"square\" stroke-miterlimit=\"10\" x1=\"25\" y1=\"6.7\" x2=\"31\" y2=\"6.7\"/>\n			<line fill=\"none\" stroke=\"#939799\" stroke-linecap=\"square\" stroke-miterlimit=\"10\" x1=\"25\" y1=\"9.7\" x2=\"27\" y2=\"9.7\"/>\n			<path fill=\"#939799\" d=\"M0,9.3v5.1c0,0.7,0.9,1.8,1.7,1.8h6.8c0.7,0,0.5-1.1,0.5-1.8V5c0-0.7,0.2-1.8-0.5-1.8H8.2c-0.9,0-1.8,0.4-2.6,0.7C4,4.4,2.1,5.7,0.8,7.5C0.4,8,0,8.6,0,9.3z\"/>\n			<circle fill=\"#5D5D5D\" stroke=\"#FFFFFF\" stroke-miterlimit=\"10\" cx=\"5\" cy=\"15.7\" r=\"2.4\"/>\n			<path fill=\"#B4B7B7\" stroke=\"#FFFFFF\" stroke-miterlimit=\"10\" d=\"M9.8,2.5v11.2c0,1.1,0.9,2,2,2H23c1.1,0,2-0.9,2-2V2.5c0-1.1-0.9-2-2-2H11.8C10.7,0.5,9.8,1.4,9.8,2.5z\"/>\n			<circle fill=\"#5D5D5D\" stroke=\"#FFFFFF\" stroke-miterlimit=\"10\" cx=\"17.4\" cy=\"15.7\" r=\"2.4\"/>\n			<path fill=\"#FFFFFF\" d=\"M2.7,7.9c1.1-2,2.8-2.6,4-2.6h0.1c0.5,0,1,0.4,1,1v1.5c0,0.3-0.3,0.6-0.6,0.6H3C2.7,8.4,2.5,8.1,2.7,7.9L2.7,7.9z\"/>\n		  </g>\n		</svg>\n		<h5>Parts being"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isReturn : depth0),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.program(9, data, 0),"data":data})) != null ? stack1 : "")
    + "</h5>\n	</div>\n	<div class=\"parts-table--part-creation"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isReturn : depth0),{"name":"if","hash":{},"fn":this.program(11, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n		<input maxlength=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.partsConstants : depth0)) != null ? stack1.NAME_MAX : stack1), depth0))
    + "\" class=\"parts-table--item-name\" type=\"text\" placeholder=\"Part Name\" />\n		<input maxlength=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.partsConstants : depth0)) != null ? stack1.TRACKING_NUMBER_MAX : stack1), depth0))
    + "\" class=\"parts-table--tracking-number-input\" type=\"text\" placeholder=\"Tracking Number\"/>\n		<input max=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.partsConstants : depth0)) != null ? stack1.PART_VALUE_MAX : stack1), depth0))
    + "\" min=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.partsConstants : depth0)) != null ? stack1.PART_VALUE_MIN : stack1), depth0))
    + "\" step=\"0.01\" class=\"parts-table--item-price\" placeholder=\"Part Value\" pattern=\"[0-9$,.]*\" type=\"number\" />\n		<span class=\"parts-table--hidden-dollar-sign\">$</span>\n		<button disabled class=\"button parts-table--add\">Add</button>\n	</div>\n</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/dispatcher/accept-apply_modal"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "		<h4>By Applying, I understand that if my application is accepted:</h4>\n";
},"3":function(depth0,helpers,partials,data) {
    return "		<h4>By Accepting, I understand that:</h4>\n";
},"5":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "			<li><strong>"
    + alias3(((helper = (helper = helpers.currentUserCompanyName || (depth0 != null ? depth0.currentUserCompanyName : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"currentUserCompanyName","hash":{},"data":data}) : helper)))
    + "</strong> will be paid within <strong>"
    + alias3(((helper = (helper = helpers.paymentTime || (depth0 != null ? depth0.paymentTime : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"paymentTime","hash":{},"data":data}) : helper)))
    + "</strong> days of approval of my work\n";
},"7":function(depth0,helpers,partials,data) {
    var helper;

  return "			<li><strong>"
    + this.escapeExpression(((helper = (helper = helpers.currentUserCompanyName || (depth0 != null ? depth0.currentUserCompanyName : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"currentUserCompanyName","hash":{},"data":data}) : helper)))
    + "</strong> will be paid upon approval of my work</li>\n";
},"9":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing;

  return "		<div class=\"resource-apply-toggle -dispatcher\">\n			<strong>\n				<i class=\"toggler\"></i>\n				&nbsp; Optional - Propose Alternate Price and/or Date\n			</strong>\n		</div>\n\n		<div ref=\"configuration\" class=\"clearfix dn\">\n			<input type=\"hidden\" name=\"pricing\" value=\""
    + this.escapeExpression((helpers.setPricingStrategyId || (depth0 && depth0.setPricingStrategyId) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.type : stack1),{"name":"setPricingStrategyId","hash":{},"data":data}))
    + "\"/>\n			<fieldset>\n				<label>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.disablePriceNegotiation : depth0),{"name":"if","hash":{},"fn":this.program(10, data, 0),"inverse":this.program(12, data, 0),"data":data})) != null ? stack1 : "")
    + "				</label>\n\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.disablePriceNegotiation : depth0),{"name":"unless","hash":{},"fn":this.program(14, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</fieldset>\n\n			<!-- Schedule Negotiation -->\n			<fieldset>\n				<label>\n					<input type=\"checkbox\" name=\"schedule_negotiation\" value=\"true\" id=\"schedule_negotiation\"/>\n					Propose a new <strong>date</strong> or <strong>time</strong>\n				</label>\n\n				<div id=\"schedule_negotiation_config\" class=\"dn\">\n					<table>\n						<tr>\n							<td>Current "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.schedule : stack1)) != null ? stack1.range : stack1),{"name":"if","hash":{},"fn":this.program(27, data, 0),"inverse":this.program(29, data, 0),"data":data})) != null ? stack1 : "")
    + "</td>\n							<td>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.schedule : stack1)) != null ? stack1.from : stack1),{"name":"if","hash":{},"fn":this.program(31, data, 0),"inverse":this.program(33, data, 0),"data":data})) != null ? stack1 : "")
    + "							</td>\n						</tr>\n\n						<tr>\n							<td>\n								<div>\n									<label>\n										<input type=\"radio\" id=\"new_time\" name=\"reschedule_option\" value=\"time\" "
    + ((stack1 = helpers.unless.call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.schedule : stack1)) != null ? stack1.range : stack1),{"name":"unless","hash":{},"fn":this.program(35, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " />\n										Propose a new <strong>time</strong>\n									</label>\n								</div>\n							</td>\n							<td>\n								<div>\n									<input type=\"text\" id=\"from\" name=\"from\" placeholder=\"Select Date\" />\n									<input type=\"text\" id=\"fromtime\" name=\"fromtime\" placeholder=\"Select Time\"/>\n								</div>\n							</td>\n						</tr>\n\n						<tr>\n							<td>\n								<div>\n									<label>\n										<input type=\"radio\" id=\"new_window\" name=\"reschedule_option\" value=\"window\" "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.schedule : stack1)) != null ? stack1.range : stack1),{"name":"if","hash":{},"fn":this.program(35, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " />\n										Propose a new <strong>time window</strong>\n									</label>\n								</div>\n							</td>\n							<td>\n								<div>\n									<span class=\"to-date"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.schedule : stack1)) != null ? stack1.range : stack1),{"name":"unless","hash":{},"fn":this.program(37, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">to</br>\n										<input type=\"text\" id=\"to\" name=\"to\" placeholder=\"Select Date\"/>\n										<input type=\"text\" id=\"totime\" name=\"totime\" placeholder=\"Select Time\"/>\n									</span>\n								</div>\n							</td>\n						</tr>\n\n					</table>\n				</div>\n			</fieldset>\n\n			<!-- Expiration Configuration -->\n			<fieldset>\n				<label>\n					<input type=\"checkbox\" value=\"true\" name=\"offer_expiration\" id=\"offer_expiration\"/>\n					Set an offer expiration date\n				</label>\n				<div id=\"offer_expiration_config\" class=\"dn\">\n					<div>\n						<label>Expiration Date</label>\n						<div>\n							<input type=\"text\" id=\"expires_on\" name=\"expires_on\" placeholder=\"Select Date\" />\n							<input type=\"text\" id=\"expires_on_time\" name=\"expires_on_time\" placeholder=\"Select Time\"/>\n						</div>\n					</div>\n				</div>\n			</fieldset>\n\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.mode : depth0),"accept",{"name":"eq","hash":{},"fn":this.program(39, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</div>\n";
},"10":function(depth0,helpers,partials,data) {
    return "						<input type=\"checkbox\" name=\"price_negotiation\" id=\"price_negotiation\" disabled=\"disabled\"/>\n						<font color=\"gray\"><strong>The client has chosen to disable price counteroffers</strong></font>\n";
},"12":function(depth0,helpers,partials,data) {
    return "						<input type=\"checkbox\" name=\"price_negotiation\" id=\"price_negotiation\" value=\"true\"/>\n						Propose a new <strong>price</strong>\n";
},"14":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing;

  return "					<div id=\"price_negotiation_config\" class=\"pricing_configuration dn\">\n\n						<table>\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.type : stack1),"INTERNAL",{"name":"eq","hash":{},"fn":this.program(15, data, 0),"inverse":this.program(17, data, 0),"data":data})) != null ? stack1 : "")
    + "\n\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.type : stack1),"FLAT",{"name":"eq","hash":{},"fn":this.program(19, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.type : stack1),"PER_HOUR",{"name":"eq","hash":{},"fn":this.program(21, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.type : stack1),"PER_UNIT",{"name":"eq","hash":{},"fn":this.program(23, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.type : stack1),"BLENDED_PER_HOUR",{"name":"eq","hash":{},"fn":this.program(25, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n							<tr id=\"offer-expenses\" >\n								<td>Additional Expenses</td>\n								<td></td>\n								<td>\n									<span class=\"add-on\">$</span>\n									<input type=\"text\" id=\"additional_expenses\" name=\"additional_expenses\"/>\n								</td>\n							</tr>\n						</table>\n					</div>\n";
},"15":function(depth0,helpers,partials,data) {
    return "								<tr>\n									<th></th>\n									<th>Assignment Budget</th>\n									<th>Internal Assignment</th>\n								</tr>\n";
},"17":function(depth0,helpers,partials,data) {
    return "								<tr>\n									<th></th>\n									<th>Current</th>\n									<th>Proposed</th>\n								</tr>\n";
},"19":function(depth0,helpers,partials,data) {
    var stack1;

  return "								<tr>\n									<td>Assignment Budget</td>\n									<td>"
    + this.escapeExpression((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || helpers.helperMissing).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.flatPrice : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n									<td>\n										<span class=\"add-on\">$</span>\n										<input type=\"text\" id=\"flat_price\" name=\"flat_price\"/>\n									</td>\n								</tr>\n";
},"21":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.escapeExpression, alias2=this.lambda;

  return "								<tr>\n									<td>Rate <small class=\"meta\">(per hour)</small></td>\n									<td>"
    + alias1((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || helpers.helperMissing).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.perHourPrice : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n									<td>\n										<span class=\"add-on\">$</span>\n										<input type=\"text\" id=\"per_hour_price\" name=\"per_hour_price\" value=\""
    + alias1(alias2(((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.perHourPrice : stack1), depth0))
    + "\"/>\n									</td>\n								</tr>\n								<tr>\n									<td>Hours</td>\n									<td>"
    + alias1(alias2(((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.maxNumberOfHours : stack1), depth0))
    + "</td>\n									<td><input type=\"text\" id=\"max_number_of_hours\" name=\"max_number_of_hours\" value=\""
    + alias1(alias2(((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.maxNumberOfHours : stack1), depth0))
    + "\"/></td>\n								</tr>\n								<tr>\n									<td>Assignment Budget</td>\n									<td></td>\n									<td>\n										<span class=\"add-on\">$</span>\n										<input type=\"text\" readonly=\"readonly\" name=\"spend_max\"/>\n									</td>\n								</tr>\n";
},"23":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing, alias2=this.escapeExpression, alias3=this.lambda;

  return "								<tr>\n									<td>Rate <small class=\"meta\">(per unit)</small></td>\n									<td>"
    + alias2((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.perUnitPrice : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n									<td>\n										<span class=\"add-on\">$</span>\n										<input type=\"text\" id=\"per_unit_price\" name=\"per_unit_price\" value=\""
    + alias2(alias3(((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.perUnitPrice : stack1), depth0))
    + "\"/>\n									</td>\n								</tr>\n								<tr>\n									<td>Units</td>\n									<td>"
    + alias2((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.maxNumberOfUnits : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n									<td><input type=\"text\" id=\"max_number_of_units\" name=\"max_number_of_units\" value=\""
    + alias2(alias3(((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.maxNumberOfUnits : stack1), depth0))
    + "\"/><td>\n								</tr>\n								<tr>\n									<td>Assignment Budget</td>\n									<td></td>\n									<td>\n										<span class=\"add-on\">$</span>\n										<input type=\"text\" readonly=\"readonly\" name=\"spend_max\"/>\n									</td>\n								</tr>\n";
},"25":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing, alias2=this.escapeExpression, alias3=this.lambda;

  return "								<tr>\n									<td>Initial Rate</td>\n									<td>"
    + alias2((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.initialPerHourPrice : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n									<td>\n										<span class=\"add-on\">$</span>\n										<input type=\"text\" id=\"initial_per_hour_price\" name=\"initial_per_hour_price\" value=\""
    + alias2(alias3(((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.initialPerHourPrice : stack1), depth0))
    + "\"/>\n									</td>\n								</tr>\n								<tr>\n									<td>Initial Hours</td>\n									<td>"
    + alias2((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.initialNumberOfHours : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n									<td><input type=\"text\" id=\"initial_number_of_hours\" name=\"initial_number_of_hours\" value=\""
    + alias2(alias3(((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.initialNumberOfHours : stack1), depth0))
    + "\"/></td>\n								</tr>\n								<tr>\n									<td>Secondary Rate</td>\n									<td>"
    + alias2((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.additionalPerHourPrice : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n									<td>\n										<span class=\"add-on\">$</span>\n										<input type=\"text\" id=\"additional_per_hour_price\" name=\"additional_per_hour_price\" value=\""
    + alias2(alias3(((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.additionalPerHourPrice : stack1), depth0))
    + "\"/>\n									</td>\n								</tr>\n								<tr>\n									<td>Secondary Hours</td>\n									<td>"
    + alias2((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.maxBlendedNumberOfHours : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n									<td><input type=\"text\" id=\"max_blended_number_of_hours\" name=\"max_blended_number_of_hours\" value=\""
    + alias2(alias3(((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.maxBlendedNumberOfHours : stack1), depth0))
    + "\"/></td>\n								</tr>\n								<tr>\n									<td>Assignment Budget</td>\n									<td></td>\n									<td>\n										<span class=\"add-on\">$</span>\n										<input type=\"text\" readonly=\"readonly\" name=\"spend_max\"/>\n									</td>\n								</tr>\n";
},"27":function(depth0,helpers,partials,data) {
    return "window";
},"29":function(depth0,helpers,partials,data) {
    return "time";
},"31":function(depth0,helpers,partials,data) {
    var stack1;

  return "									"
    + this.escapeExpression((helpers.displayWorkSchedule || (depth0 && depth0.displayWorkSchedule) || helpers.helperMissing).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.schedule : stack1)) != null ? stack1.through : stack1),((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.schedule : stack1)) != null ? stack1.from : stack1),((stack1 = ((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.schedule : stack1)) != null ? stack1.range : stack1),{"name":"displayWorkSchedule","hash":{},"data":data}))
    + "\n";
},"33":function(depth0,helpers,partials,data) {
    return "									Not set.\n";
},"35":function(depth0,helpers,partials,data) {
    return "checked=\"checked\"";
},"37":function(depth0,helpers,partials,data) {
    return " dn";
},"39":function(depth0,helpers,partials,data) {
    return "				<fieldset>\n					<div>\n						<label><strong>Include a message with your application</strong> <small class=\"meta\">(recommended)</small></label>\n						<textarea id=\"note\" name=\"note\" class=\"input-block-level\" rows=\"3\"></textarea>\n					</div>\n				</fieldset>\n";
},"41":function(depth0,helpers,partials,data) {
    return "		<fieldset>\n			<div>\n				<label><strong>Include a message with your application</strong> <small class=\"meta\">(recommended)</small></label>\n				<textarea id=\"note\" name=\"note\" class=\"input-block-level\" rows=\"3\"></textarea>\n			</div>\n		</fieldset>\n";
},"43":function(depth0,helpers,partials,data) {
    return "			<button id=\"dispatcher-apply\" class=\"button -primary\">Apply on Behalf</button>\n";
},"45":function(depth0,helpers,partials,data) {
    var helper;

  return "			<button id=\"dispatcher-accept\" class=\"button -primary\" data-usernumber=\""
    + this.escapeExpression(((helper = (helper = helpers.userNumber || (depth0 != null ? depth0.userNumber : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"userNumber","hash":{},"data":data}) : helper)))
    + "\">Accept on Behalf</button>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<form id=\"apply-form\" class=\"dispatcher_apply-accept_modal\">\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.mode : depth0),"apply",{"name":"eq","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.mode : depth0),"accept",{"name":"eq","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n	<ul>\n		<li><strong>"
    + alias3(((helper = (helper = helpers.currentUserCompanyName || (depth0 != null ? depth0.currentUserCompanyName : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"currentUserCompanyName","hash":{},"data":data}) : helper)))
    + "</strong> will be contracting for <strong>"
    + alias3(((helper = (helper = helpers.companyName || (depth0 != null ? depth0.companyName : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"companyName","hash":{},"data":data}) : helper)))
    + "</strong></li>\n		<li><strong>"
    + alias3(((helper = (helper = helpers.currentUserCompanyName || (depth0 != null ? depth0.currentUserCompanyName : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"currentUserCompanyName","hash":{},"data":data}) : helper)))
    + "</strong> agrees to the terms of this assignment</li>\n		<li>Payment is the responsibility of <strong>"
    + alias3(((helper = (helper = helpers.companyName || (depth0 != null ? depth0.companyName : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"companyName","hash":{},"data":data}) : helper)))
    + "</strong></li>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.paymentTime : depth0)) != null ? stack1.length : stack1),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.program(7, data, 0),"data":data})) != null ? stack1 : "")
    + "	</ul>\n\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isParentBundle : depth0),{"name":"unless","hash":{},"fn":this.program(9, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.mode : depth0),"apply",{"name":"eq","hash":{},"fn":this.program(41, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n	<input type=\"hidden\" name=\"workerNumber\" id=\"workerNumber\" value=\""
    + alias3(((helper = (helper = helpers.userNumber || (depth0 != null ? depth0.userNumber : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"userNumber","hash":{},"data":data}) : helper)))
    + "\"/>\n\n	<div ref=\"actions\">\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.mode : depth0),"apply",{"name":"eq","hash":{},"fn":this.program(43, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.mode : depth0),"accept",{"name":"eq","hash":{},"fn":this.program(45, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</div>\n\n	<p>\n		<small><a href=\"javascript:void(0);\" class=\"dispatcher-show-block-client\">Block this company from sending you more assignments</a></small>\n	</p>\n</form>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/dispatcher/block-company"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div>\n	<p>Blocking this company will remove you from their groups and block future assignment, group, and test invites.</p>\n	<p>Click OK to block.</p>\n\n	<form id=\"block_client_form\" action=\"/user/block_client/\" method=\"post\">\n		<button type=\"submit\" id=\"dispatcher-block-client\" class=\"button -primary\">Ok</button>\n	</form>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/document_asset"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "		<div class=\"deliverable-icon\" style=\"background-image: url('"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.uri : depth0), depth0))
    + "');\"></div>\n";
},"3":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda;

  return "		<div class=\"no-background deliverable-icon\">\n			<svg version=\"1.1\"  class=\"deliverables-live-icon icon-position file-type-icon\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 29 29\" enable-background=\"new 0 0 29 29\" xml:space=\"preserve\">\n					<g>\n						<path fill=\"#949899\" d=\"M16.744,1C18.264,1,23,6.097,23,7.733v12.433C23,21.729,21.729,23,20.166,23H3.835\n								C2.272,23,1,21.729,1,20.166V3.834C1,2.271,2.272,1,3.835,1H16.744 M16.744,0H3.835C1.721,0,0,1.72,0,3.834v16.331\n								C0,22.28,1.721,24,3.835,24h16.331C22.281,24,24,22.28,24,20.166V7.733C24,5.565,18.779,0,16.744,0L16.744,0z\"/>\n						<g opacity=\"0.3\">\n							<path d=\"M16.318,0.687v3.96c0,2.047,1.66,3.707,3.707,3.707H23.5\"/>\n							<path d=\"M23.5,8.541h-3.476c-2.147,0-3.894-1.747-3.894-3.894V0.688h0.375v3.959c0,1.94,1.579,3.519,3.519,3.519H23.5V8.541z\"/>\n						</g>\n						<path fill=\"#FFFFFF\" d=\"M16.818,0.187v3.96c0,2.047,1.66,3.707,3.707,3.707H24\"/>\n						<path fill=\"#4C5355\" d=\"M24,8.041h-3.476c-2.147,0-3.894-1.747-3.894-3.894V0.188h0.375v3.959c0,1.94,1.579,3.519,3.519,3.519\n									H24V8.041z\"/>\n						<text fill=\"#4C5355\" font-weight=\"600\" font-family=\"Open Sans\" "
    + ((stack1 = alias1((depth0 != null ? depth0.extensionSize : depth0), depth0)) != null ? stack1 : "")
    + ">"
    + this.escapeExpression(alias1((depth0 != null ? depth0.upperExtension : depth0), depth0))
    + "</text>\n					</g>\n				</svg>\n\n		</div>\n";
},"5":function(depth0,helpers,partials,data) {
    return "			<svg class=\"remove\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.0\" x=\"0px\" y=\"0px\" viewBox=\"0 0 32 24\" enable-background=\"new 0 0 32 24\" xml:space=\"preserve\">\n				<path d=\"M29.167 24H2.833C1.269 24 0 22.7 0 21.2V2.8C0 1.3 1.3 0 2.8 0h26.333C30.731 0 32 1.3 32 2.8 v18.4C32 22.7 30.7 24 29.2 24z\" class=\"background\"/>\n					<polygon points=\"19,4 19,3 14,3 14,4 10,4 10,6 23,6 23,4\" class=\"foreground\"/>\n					<polygon points=\"11.1,7 12.1,19 20.7,19 21.7,7\" class=\"foreground\"/>\n				</svg>\n";
},"7":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "	<div class=\"asset-visibility dropdown tooltipped tooltipped-w\" aria-label=\""
    + alias2(alias1((depth0 != null ? depth0.visibilityTypeDescription : depth0), depth0))
    + "\">\n		<input class=\"visibility-selection dn\" name=\"visibilityType\" value=\""
    + alias2(alias1((depth0 != null ? depth0.visibilityCode : depth0), depth0))
    + "\"/>\n		<span class=\"visibility-copy\">Visible to:</span>\n		<a class=\"toggle-visibility dropdown-toggle button\" data-toggle=\"dropdown\" href=\"#\">\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.visibilitySettings : depth0),{"name":"each","hash":{},"fn":this.program(8, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</a>\n		<ul class=\"dropdown-menu\">\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.visibilitySettings : depth0),{"name":"each","hash":{},"fn":this.program(10, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</ul>\n	</div>\n";
},"8":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<span class=\""
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.code : depth0), depth0))
    + "\">"
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"name":(depth0 != null ? depth0['icon-code'] : depth0)},"data":data,"helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "</span>\n";
},"10":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "				<li>\n					<a class=\"visibility-option\" data-visibility-code=\""
    + alias2(alias1((depth0 != null ? depth0.code : depth0), depth0))
    + "\" data-visibility-description=\""
    + alias2(alias1((depth0 != null ? depth0.description : depth0), depth0))
    + "\">\n						<span class=\"visibility-icon\">"
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"name":(depth0 != null ? depth0['icon-code'] : depth0)},"data":data,"helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "</span>\n						<span class=\"visibility-description\">"
    + alias2(alias1((depth0 != null ? depth0.description : depth0), depth0))
    + "</span>\n						<span class=\"checkmark\"><i class=\"wm-icon-checkmark\"></i></span>\n					</a>\n				</li>\n";
},"12":function(depth0,helpers,partials,data) {
    return "dn";
},"14":function(depth0,helpers,partials,data) {
    return "readonly";
},"16":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<div class=\"asset-options "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.description : depth0),{"name":"if","hash":{},"fn":this.program(17, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.description : depth0),{"name":"unless","hash":{},"fn":this.program(19, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n			<a class=\"asset-edit-option\" href=\"javascript:void(0);\">Edit</a>\n			<a class=\"asset-add-option\" href=\"javascript:void(0);\">Add Description</a>\n			<a class=\"asset-save-option\" href=\"javascript:void(0);\">Save</a>\n			<a class=\"asset-cancel-option\" href=\"javascript:void(0);\">Cancel</a>\n		</div>\n";
},"17":function(depth0,helpers,partials,data) {
    return "with-description";
},"19":function(depth0,helpers,partials,data) {
    return "with-no-description";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"deliverable-icon-container span3\">\n\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.containsImageMimeType : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.containsImageMimeType : depth0),{"name":"unless","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n\n	<div class=\"options -hidden\">\n		<a class=\"asset-download\" href=\"/asset/download/"
    + alias2(alias1((depth0 != null ? depth0.uuid : depth0), depth0))
    + "\">\n			<svg class=\"deliverable-download\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.0\" x=\"0px\" y=\"0px\" viewBox=\"0 0 32 24\" enable-background=\"new 0 0 32 24\" xml:space=\"preserve\">\n				<path d=\"M29.167 24H2.833C1.269 24 0 22.7 0 21.2V2.8C0 1.3 1.3 0 2.8 0h26.333C30.731 0 32 1.3 32 2.8 v18.4C32 22.7 30.7 24 29.2 24z\" class=\"background\"/>\n					<path d=\"M18.025 16l-1.373 1.362c-0.261 0.259-0.608 0.402-0.977 0.4 c-0.369 0-0.716-0.143-0.976-0.401L13.323 16H8v3.528h16V16H18.025z M22.553 18.646h-2.465c-0.3 0-0.543-0.582-0.543-0.882 c0-0.3 0.243-0.882 0.543-0.882h2.465c0.3 0 0.5 0.6 0.5 0.882C23.095 18.1 22.9 18.6 22.6 18.646z\" class=\"foreground\"/>\n					<path d=\"M18.331 3h-5.582c-0.423 0-0.766 0.34-0.766 0.759v4.047l-1.677-0.002c-0.854 0-1.145 0.478-1.231 0.7 C8.99 8.7 8.9 9.2 9.5 9.835l5.048 5.002c0.276 0.3 0.6 0.4 1 0.425c0.391 0 0.759-0.151 1.035-0.426 l5.044-5.002c0.603-0.597 0.468-1.14 0.382-1.346c-0.086-0.206-0.378-0.684-1.231-0.684l-1.676 0.002V3.759 C19.096 3.3 18.8 3 18.3 3L18.331 3z\" class=\"foreground\"/>\n				</svg>\n		</a>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isPermitted : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</div>\n</div>\n\n\n<p class=\"asset-name\">"
    + alias2(alias1((depth0 != null ? depth0.name : depth0), depth0))
    + "</p>\n\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isPermitted : depth0),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n<div class=\"asset-description-container\">\n	<textarea rows=\"1\" maxlength=\"512\" placeholder=\"Short Description (Optional)\" name=\"asset-description\" class=\"asset-description "
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.description : depth0),{"name":"unless","hash":{},"fn":this.program(12, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.description : depth0),{"name":"if","hash":{},"fn":this.program(14, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">"
    + alias2(alias1((depth0 != null ? depth0.description : depth0), depth0))
    + "</textarea>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isPermitted : depth0),{"name":"if","hash":{},"fn":this.program(16, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["assignments/details/employee"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper;

  return this.escapeExpression(((helper = (helper = helpers.icon || (depth0 != null ? depth0.icon : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"icon","hash":{},"data":data}) : helper)));
},"3":function(depth0,helpers,partials,data) {
    var helper;

  return "			, ID: "
    + this.escapeExpression(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper;

  return "<div class=\"employee\">\n	<div class=\""
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.icon : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " employee--name\">\n		"
    + this.escapeExpression(((helper = (helper = helpers.fullName || (depth0 != null ? depth0.fullName : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"fullName","hash":{},"data":data}) : helper)))
    + "\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.hideId : depth0),{"name":"unless","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/estimated_time_log"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "	<strong>Estimated Time Spent:</strong>\n	<span>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.time_tracking_duration_out : depth0), depth0))
    + "</span>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.active_resource : depth0)) != null ? stack1.timeTrackingLog : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"useData":true});

this["wm"]["templates"]["assignments/details/hidden_follower"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "<input type=\"hidden\" name=\"followers["
    + alias2(alias1((depth0 != null ? depth0.i : depth0), depth0))
    + "]\" value=\""
    + alias2(alias1((depth0 != null ? depth0.val : depth0), depth0))
    + "\" />\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/location"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.lambda((depth0 != null ? depth0.location : depth0), depth0)) != null ? stack1 : "");
},"3":function(depth0,helpers,partials,data) {
    return "Unspecified Location";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<p class=\"parts-table--address\">"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.location : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.location : depth0),{"name":"unless","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</p>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/messages"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing;

  return "	<div class=\"assignment-messages--filters\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.activeWorker : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.program(7, data, 0),"data":data})) != null ? stack1 : "")
    + "		<span class=\"tooltipped tooltipped-n\" aria-label=\"These messages are visible to your company.\">\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.activeFilter : depth0),"private-messages",{"name":"eq","hash":{},"fn":this.program(12, data, 0),"inverse":this.program(14, data, 0),"data":data})) != null ? stack1 : "")
    + "		</span>\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.activeFilter : depth0),"questions",{"name":"eq","hash":{},"fn":this.program(16, data, 0),"inverse":this.program(18, data, 0),"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.activeFilter : depth0),"all",{"name":"eq","hash":{},"fn":this.program(20, data, 0),"inverse":this.program(22, data, 0),"data":data})) != null ? stack1 : "")
    + "	</div>\n";
},"2":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<span class=\"tooltipped tooltipped-n\" aria-label=\"These messages are visible to "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.activeWorker : depth0)) != null ? stack1.fullName : stack1), depth0))
    + ".\">\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.activeFilter : depth0),"privileged-messages",{"name":"eq","hash":{},"fn":this.program(3, data, 0),"inverse":this.program(5, data, 0),"data":data})) != null ? stack1 : "")
    + "			</span>\n";
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.filter,depth0,{"name":"filter","hash":{"isChecked":true,"text":((stack1 = (depth0 != null ? depth0.activeWorker : depth0)) != null ? stack1.fullName : stack1),"classlist":"wm-icon-add-user-circle","value":"privileged-messages","name":"messaging-filter"},"data":data,"indent":"\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"5":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.filter,depth0,{"name":"filter","hash":{"text":((stack1 = (depth0 != null ? depth0.activeWorker : depth0)) != null ? stack1.fullName : stack1),"classlist":"wm-icon-add-user-circle","value":"privileged-messages","name":"messaging-filter"},"data":data,"indent":"\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"7":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<span class=\"tooltipped tooltipped-n\" aria-label=\"These messages are visible to all applied workers.\">\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.activeFilter : depth0),"public-messages",{"name":"eq","hash":{},"fn":this.program(8, data, 0),"inverse":this.program(10, data, 0),"data":data})) != null ? stack1 : "")
    + "			</span>\n";
},"8":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.filter,depth0,{"name":"filter","hash":{"isChecked":true,"text":"Public","classlist":"wm-icon-globe-circle","value":"public-messages","name":"messaging-filter"},"data":data,"indent":"\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"10":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.filter,depth0,{"name":"filter","hash":{"text":"Public","classlist":"wm-icon-globe-circle","value":"public-messages","name":"messaging-filter"},"data":data,"indent":"\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"12":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.filter,depth0,{"name":"filter","hash":{"isChecked":true,"text":"Private","classlist":"wm-icon-lock-circle","value":"private-messages","name":"messaging-filter"},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"14":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.filter,depth0,{"name":"filter","hash":{"text":"Private","classlist":"wm-icon-lock-circle","value":"private-messages","name":"messaging-filter"},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"16":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.filter,depth0,{"name":"filter","hash":{"isChecked":true,"text":"Q&A","value":"questions","name":"messaging-filter"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"18":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.filter,depth0,{"name":"filter","hash":{"text":"Q&A","value":"questions","name":"messaging-filter"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"20":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.filter,depth0,{"name":"filter","hash":{"isChecked":true,"text":"All","value":"all","name":"messaging-filter"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"22":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.filter,depth0,{"name":"filter","hash":{"text":"All","value":"all","name":"messaging-filter"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"24":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isQuestion : depth0),{"name":"if","hash":{},"fn":this.program(25, data, 0, blockParams, depths),"inverse":this.program(39, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "");
},"25":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return "			<div class=\"question-pair\">\n				<h4>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.responses : depth0)) != null ? stack1.length : stack1),{"name":"if","hash":{},"fn":this.program(26, data, 0, blockParams, depths),"inverse":this.program(28, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "")
    + "				</h4>\n"
    + ((stack1 = this.invokePartial(partials.message,depth0,{"name":"message","hash":{"message":depth0},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depths[2] != null ? depths[2].canAnswerQuestion : depths[2]),{"name":"if","hash":{},"fn":this.program(33, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.responses : depth0),{"name":"if","hash":{},"fn":this.program(36, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</div>\n";
},"26":function(depth0,helpers,partials,data) {
    return "						Q&amp;A\n";
},"28":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.creator : depth0)) != null ? stack1.isCurrentUser : stack1),{"name":"if","hash":{},"fn":this.program(29, data, 0),"inverse":this.program(31, data, 0),"data":data})) != null ? stack1 : "");
},"29":function(depth0,helpers,partials,data) {
    return "							Your question has been sent\n";
},"31":function(depth0,helpers,partials,data) {
    return "							Question\n";
},"33":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.responses : depth0),{"name":"unless","hash":{},"fn":this.program(34, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"34":function(depth0,helpers,partials,data) {
    return "						<button class=\"button -primary question-pair--answer-button\">Answer</button>\n";
},"36":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.responses : depth0),{"name":"each","hash":{},"fn":this.program(37, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"37":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.message,depth0,{"name":"message","hash":{"message":depth0},"data":data,"indent":"\t\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"39":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<div class=\"message-thread\">\n"
    + ((stack1 = this.invokePartial(partials.message,depth0,{"name":"message","hash":{"message":depth0},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			</div>\n";
},"41":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.activeFilter : depth0),"questions",{"name":"eq","hash":{},"fn":this.program(42, data, 0),"inverse":this.program(47, data, 0),"data":data})) != null ? stack1 : "");
},"42":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.canAskQuestion : depth0),{"name":"if","hash":{},"fn":this.program(43, data, 0),"inverse":this.program(45, data, 0),"data":data})) != null ? stack1 : "");
},"43":function(depth0,helpers,partials,data) {
    return "				<div class=\"assignment-messages--empty -large\">Start your conversation with the company.</div>\n";
},"45":function(depth0,helpers,partials,data) {
    return "				<div class=\"assignment-messages--empty\">There are currently no questions to display.</div>\n";
},"47":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isActiveWorker : depth0),{"name":"if","hash":{},"fn":this.program(48, data, 0),"inverse":this.program(54, data, 0),"data":data})) != null ? stack1 : "");
},"48":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.canSendMessage : depth0),{"name":"if","hash":{},"fn":this.program(49, data, 0),"inverse":this.program(51, data, 0),"data":data})) != null ? stack1 : "");
},"49":function(depth0,helpers,partials,data) {
    return "					<div class=\"assignment-messages--empty -large\">Start your conversation with the company.</div>\n";
},"51":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.canAskQuestion : depth0),{"name":"if","hash":{},"fn":this.program(52, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"52":function(depth0,helpers,partials,data) {
    return "						<div class=\"assignment-messages--empty -large\">Have a question about the assignment, ask below.</div>\n";
},"54":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.canSendMessage : depth0),{"name":"if","hash":{},"fn":this.program(55, data, 0),"inverse":this.program(71, data, 0),"data":data})) != null ? stack1 : "");
},"55":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.activeFilter : depth0),"privileged-messages",{"name":"eq","hash":{},"fn":this.program(56, data, 0),"inverse":this.program(63, data, 0),"data":data})) != null ? stack1 : "");
},"56":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.activeWorker : depth0),{"name":"if","hash":{},"fn":this.program(57, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"57":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['with'].call(depth0,(depth0 != null ? depth0.activeWorker : depth0),{"name":"with","hash":{},"fn":this.program(58, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"58":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing;

  return "								<div class=\"assignment-messages--empty -large\">Start your conversation with<br>\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.thumbnail : depth0),"",{"name":"eq","hash":{},"fn":this.program(59, data, 0),"inverse":this.program(61, data, 0),"data":data})) != null ? stack1 : "")
    + "									"
    + this.escapeExpression(((helper = (helper = helpers.fullName || (depth0 != null ? depth0.fullName : depth0)) != null ? helper : alias1),(typeof helper === "function" ? helper.call(depth0,{"name":"fullName","hash":{},"data":data}) : helper)))
    + "\n								</div>\n";
},"59":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.avatar,depth0,{"name":"avatar","hash":{"hash":(depth0 != null ? depth0.id : depth0)},"data":data,"indent":"\t\t\t\t\t\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"61":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.avatar,depth0,{"name":"avatar","hash":{"src":(depth0 != null ? depth0.thumbnail : depth0)},"data":data,"indent":"\t\t\t\t\t\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"63":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.activeFilter : depth0),"public-messages",{"name":"eq","hash":{},"fn":this.program(64, data, 0),"inverse":this.program(66, data, 0),"data":data})) != null ? stack1 : "");
},"64":function(depth0,helpers,partials,data) {
    return "							<div class=\"assignment-messages--empty -large\">Start your conversation with the applied workers.</div>\n";
},"66":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.activeFilter : depth0),"private-messages",{"name":"eq","hash":{},"fn":this.program(67, data, 0),"inverse":this.program(69, data, 0),"data":data})) != null ? stack1 : "");
},"67":function(depth0,helpers,partials,data) {
    return "								<div class=\"assignment-messages--empty -large\">Start a private, internal company chat about this assignment.</div>\n";
},"69":function(depth0,helpers,partials,data) {
    return "								<div class=\"assignment-messages--empty\">There are currently no messages to display.</div>\n";
},"71":function(depth0,helpers,partials,data) {
    return "					<div class=\"assignment-messages--empty\">There are currently no messages to display.</div>\n";
},"73":function(depth0,helpers,partials,data) {
    return "-active";
},"75":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isCompany : depth0),{"name":"if","hash":{},"fn":this.program(76, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"76":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.checkbox,depth0,{"name":"checkbox","hash":{"text":"Label as Private","value":"private","name":"private"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isCompany : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "<div class=\"assignment-messages--feed\">\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.messages : depth0),{"name":"each","hash":{},"fn":this.program(24, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.messages : depth0),{"name":"unless","hash":{},"fn":this.program(41, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n<div class=\"assignment-messages--message-prompt "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.canAskQuestion : depth0),{"name":"if","hash":{},"fn":this.program(73, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n	<textarea name=\"new-question-content\" class=\"assignment-messages--new-question-content\" placeholder=\"Type your question here&hellip;\" rows=\"1\"></textarea>\n	<button class=\"button -primary assignment-messages--send-question\">Ask Question</button>\n</div>\n<div class=\"assignment-messages--message-prompt "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.canSendMessage : depth0),{"name":"if","hash":{},"fn":this.program(73, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n	<textarea name=\"new-message-content\" class=\"assignment-messages--new-message-content\" placeholder=\"Write message here&hellip;\" rows=\"1\"></textarea>\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.activeFilter : depth0),"all",{"name":"eq","hash":{},"fn":this.program(75, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	<button class=\"button -primary assignment-messages--send-message\">Send</button>\n</div>\n";
},"usePartial":true,"useData":true,"useDepths":true});

this["wm"]["templates"]["assignments/details/meta_information_on_current"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "		<br>\n		<p>Rejected by "
    + alias2(alias1((depth0 != null ? depth0.rejectedBy : depth0), depth0))
    + " on "
    + alias2(alias1((depth0 != null ? depth0.rejectedOn : depth0), depth0))
    + "</p>\n		<p>Reason: <br> <em> "
    + alias2(alias1((depth0 != null ? depth0.rejectionReason : depth0), depth0))
    + " </em> </p>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"deliverables-currently-previewed-information\">\n	<i class=\"wm-icon-information-filled\"></i>\n	<p>"
    + alias2(alias1((depth0 != null ? depth0.imageName : depth0), depth0))
    + "</p>\n	<p>Submitted on "
    + alias2(alias1((depth0 != null ? depth0.submittedOnTime : depth0), depth0))
    + " by "
    + alias2(alias1((depth0 != null ? depth0.uploadedBy : depth0), depth0))
    + ".</p>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isRejected : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/mobilePartsHeaderTemplate"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "-active-worker-exists";
},"3":function(depth0,helpers,partials,data) {
    return "is-company";
},"5":function(depth0,helpers,partials,data) {
    return "-is-supplied-by-worker";
},"7":function(depth0,helpers,partials,data) {
    return "\n					Parts being returned to:\n";
},"9":function(depth0,helpers,partials,data) {
    return "					Parts being sent to:\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"parts-mobile"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isNotSentOrDraft : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isOwnerOrAdmin : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isSuppliedByWorker : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n	<div class=\"parts-table--part-header\">\n		<div class=\"parts-table--part-information\">\n			<h3>"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isReturn : depth0),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.program(9, data, 0),"data":data})) != null ? stack1 : "")
    + "			</h3>\n		</div>\n\n		<div class=\"parts-table--part-creation\">\n			<input maxlength=\"36\" class=\"parts-table--item-name\" type=\"text\" placeholder=\"Part Name\" />\n			<input maxlength=\"36\" class=\"parts-table--tracking-number-input\" type=\"text\" placeholder=\"Tracking Number\"/>\n			<input maxlength=\"10\" class=\"parts-table--item-price\" placeholder=\"Part Value\" pattern=\"[0-9$,.]*\" type=\"text\" />\n			<button disabled class=\"button parts-table--add\">Add</button>\n		</div>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/nonRequiredDeliverableUploadPending"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper;

  return "<li class=\"non-required-deliverable row\">\n	<div class=\"fileNamePlaceholder\">"
    + this.escapeExpression(((helper = (helper = helpers.fileName || (depth0 != null ? depth0.fileName : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"fileName","hash":{},"data":data}) : helper)))
    + "</div>\n	<div class=\"progress progress-striped active\">\n		<div class=\"bar\"></div>\n	</div>\n</li>";
},"useData":true});

this["wm"]["templates"]["assignments/details/nonRequiredDeliverableUploader"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<form class=\"deliverable-form\">\n	<input class=\"uploader-input deliverable-upload\" type=\"file\" name=\"files[]\" multiple/>\n	<a class=\"button uploader-button\"><i class=\"wm-icon-upload\"></i> Upload Files</a>\n</form>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/non_required_deliverable_asset"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "		<div class=\"deliverable-icon\" style=\"background-image: url('"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.uri : depth0), depth0))
    + "');\"></div>\n";
},"3":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda;

  return "		<div class=\"no-background deliverable-icon\">\n			<svg version=\"1.1\"  class=\"deliverables-live-icon icon-position file-type-icon\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 29 29\" enable-background=\"new 0 0 29 29\" xml:space=\"preserve\">\n					<g>\n						<path fill=\"#949899\" d=\"M16.744,1C18.264,1,23,6.097,23,7.733v12.433C23,21.729,21.729,23,20.166,23H3.835\n								C2.272,23,1,21.729,1,20.166V3.834C1,2.271,2.272,1,3.835,1H16.744 M16.744,0H3.835C1.721,0,0,1.72,0,3.834v16.331\n								C0,22.28,1.721,24,3.835,24h16.331C22.281,24,24,22.28,24,20.166V7.733C24,5.565,18.779,0,16.744,0L16.744,0z\"/>\n						<g opacity=\"0.3\">\n							<path d=\"M16.318,0.687v3.96c0,2.047,1.66,3.707,3.707,3.707H23.5\"/>\n							<path d=\"M23.5,8.541h-3.476c-2.147,0-3.894-1.747-3.894-3.894V0.688h0.375v3.959c0,1.94,1.579,3.519,3.519,3.519H23.5V8.541z\"/>\n						</g>\n						<path fill=\"#FFFFFF\" d=\"M16.818,0.187v3.96c0,2.047,1.66,3.707,3.707,3.707H24\"/>\n						<path fill=\"#4C5355\" d=\"M24,8.041h-3.476c-2.147,0-3.894-1.747-3.894-3.894V0.188h0.375v3.959c0,1.94,1.579,3.519,3.519,3.519\n									H24V8.041z\"/>\n						<text fill=\"#4C5355\" font-weight=\"600\" font-family=\"Open Sans\""
    + ((stack1 = alias1((depth0 != null ? depth0.extensionSize : depth0), depth0)) != null ? stack1 : "")
    + ">"
    + this.escapeExpression(alias1((depth0 != null ? depth0.upperExtension : depth0), depth0))
    + "</text>\n					</g>\n				</svg>\n\n		</div>\n";
},"5":function(depth0,helpers,partials,data) {
    return "			<svg class=\"remove\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.0\" x=\"0px\" y=\"0px\" viewBox=\"0 0 32 24\" enable-background=\"new 0 0 32 24\" xml:space=\"preserve\">\n				<path d=\"M29.167 24H2.833C1.269 24 0 22.7 0 21.2V2.8C0 1.3 1.3 0 2.8 0h26.333C30.731 0 32 1.3 32 2.8 v18.4C32 22.7 30.7 24 29.2 24z\" class=\"background\"/>\n				<polygon points=\"19,4 19,3 14,3 14,4 10,4 10,6 23,6 23,4\" class=\"foreground\"/>\n				<polygon points=\"11.1,7 12.1,19 20.7,19 21.7,7\" class=\"foreground\"/>\n			</svg>\n";
},"7":function(depth0,helpers,partials,data) {
    return "dn";
},"9":function(depth0,helpers,partials,data) {
    return "readonly";
},"11":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<div class=\"asset-options "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.description : depth0),{"name":"if","hash":{},"fn":this.program(12, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.description : depth0),{"name":"unless","hash":{},"fn":this.program(14, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n			<a class=\"asset-edit-option\" href=\"javascript:void(0);\">Edit</a>\n			<a class=\"asset-add-option\" href=\"javascript:void(0);\">Add Description</a>\n			<a class=\"asset-save-option\" href=\"javascript:void(0);\">Save</a>\n			<a class=\"asset-cancel-option\" href=\"javascript:void(0);\">Cancel</a>\n		</div>\n";
},"12":function(depth0,helpers,partials,data) {
    return "with-description";
},"14":function(depth0,helpers,partials,data) {
    return "with-no-description";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"deliverable-icon-container span3\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.containsImageMimeType : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.containsImageMimeType : depth0),{"name":"unless","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n	<div class=\"options -hidden\">\n		<a class=\"asset-download\" href=\"/asset/download/"
    + alias2(alias1((depth0 != null ? depth0.uuid : depth0), depth0))
    + "\">\n			<svg class=\"deliverable-download\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.0\" x=\"0px\" y=\"0px\" viewBox=\"0 0 32 24\" enable-background=\"new 0 0 32 24\" xml:space=\"preserve\">\n			<path d=\"M29.167 24H2.833C1.269 24 0 22.7 0 21.2V2.8C0 1.3 1.3 0 2.8 0h26.333C30.731 0 32 1.3 32 2.8 v18.4C32 22.7 30.7 24 29.2 24z\" class=\"background\"/>\n				<path d=\"M18.025 16l-1.373 1.362c-0.261 0.259-0.608 0.402-0.977 0.4 c-0.369 0-0.716-0.143-0.976-0.401L13.323 16H8v3.528h16V16H18.025z M22.553 18.646h-2.465c-0.3 0-0.543-0.582-0.543-0.882 c0-0.3 0.243-0.882 0.543-0.882h2.465c0.3 0 0.5 0.6 0.5 0.882C23.095 18.1 22.9 18.6 22.6 18.646z\" class=\"foreground\"/>\n				<path d=\"M18.331 3h-5.582c-0.423 0-0.766 0.34-0.766 0.759v4.047l-1.677-0.002c-0.854 0-1.145 0.478-1.231 0.7 C8.99 8.7 8.9 9.2 9.5 9.835l5.048 5.002c0.276 0.3 0.6 0.4 1 0.425c0.391 0 0.759-0.151 1.035-0.426 l5.044-5.002c0.603-0.597 0.468-1.14 0.382-1.346c-0.086-0.206-0.378-0.684-1.231-0.684l-1.676 0.002V3.759 C19.096 3.3 18.8 3 18.3 3L18.331 3z\" class=\"foreground\"/>\n			</svg>\n		</a>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isPermitted : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</div>\n</div>\n\n<p class=\"asset-name\">"
    + alias2(alias1((depth0 != null ? depth0.name : depth0), depth0))
    + "</p>\n\n<div class=\"asset-description-container\">\n	<textarea rows=\"1\" maxlength=\"512\" placeholder=\"Short Description (Optional)\" name=\"asset-description\" class=\"asset-description "
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.description : depth0),{"name":"unless","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.description : depth0),{"name":"if","hash":{},"fn":this.program(9, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">"
    + alias2(alias1((depth0 != null ? depth0.description : depth0), depth0))
    + "</textarea>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isPermitted : depth0),{"name":"if","hash":{},"fn":this.program(11, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/non_required_document"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.isUpload : stack1),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.isUpload : stack1),{"name":"unless","hash":{},"fn":this.program(4, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"2":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<div class=\"deliverable-icon\" style=\"background-image: url('/asset/downloadTemp/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.uuid : stack1), depth0))
    + "');\"></div>\n";
},"4":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<div class=\"deliverable-icon\" style=\"background-image: url('/asset/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.uuid : stack1), depth0))
    + "');\"></div>\n";
},"6":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda;

  return "			<div class=\"no-background deliverable-icon\">\n				<svg version=\"1.1\"  class=\"deliverables-live-icon icon-position file-type-icon\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\"\n		 		viewBox=\"0 0 29 29\" enable-background=\"new 0 0 29 29\" xml:space=\"preserve\">\n				<g>\n					<path fill=\"#949899\" d=\"M16.744,1C18.264,1,23,6.097,23,7.733v12.433C23,21.729,21.729,23,20.166,23H3.835\n							C2.272,23,1,21.729,1,20.166V3.834C1,2.271,2.272,1,3.835,1H16.744 M16.744,0H3.835C1.721,0,0,1.72,0,3.834v16.331\n							C0,22.28,1.721,24,3.835,24h16.331C22.281,24,24,22.28,24,20.166V7.733C24,5.565,18.779,0,16.744,0L16.744,0z\"/>\n					<g opacity=\"0.3\">\n						<path d=\"M16.318,0.687v3.96c0,2.047,1.66,3.707,3.707,3.707H23.5\"/>\n						<path d=\"M23.5,8.541h-3.476c-2.147,0-3.894-1.747-3.894-3.894V0.688h0.375v3.959c0,1.94,1.579,3.519,3.519,3.519H23.5V8.541z\"/>\n					</g>\n					<path fill=\"#FFFFFF\" d=\"M16.818,0.187v3.96c0,2.047,1.66,3.707,3.707,3.707H24\"/>\n					<path fill=\"#4C5355\" d=\"M24,8.041h-3.476c-2.147,0-3.894-1.747-3.894-3.894V0.188h0.375v3.959c0,1.94,1.579,3.519,3.519,3.519\n								H24V8.041z\"/>\n					<text fill=\"#4C5355\" font-weight=\"600\" font-family=\"Open Sans\" "
    + ((stack1 = alias1(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.extensionSize : stack1), depth0)) != null ? stack1 : "")
    + ">"
    + this.escapeExpression(alias1(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.upperExtension : stack1), depth0))
    + "</text>\n				</g>\n			</svg>\n\n			</div>\n";
},"8":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.isUpload : stack1),{"name":"if","hash":{},"fn":this.program(9, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.isUpload : stack1),{"name":"unless","hash":{},"fn":this.program(11, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				<svg class=\"deliverable-download\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.0\" x=\"0px\" y=\"0px\" viewBox=\"0 0 32 24\" enable-background=\"new 0 0 32 24\" xml:space=\"preserve\">\n					<path d=\"M29.167 24H2.833C1.269 24 0 22.7 0 21.2V2.8C0 1.3 1.3 0 2.8 0h26.333C30.731 0 32 1.3 32 2.8 v18.4C32 22.7 30.7 24 29.2 24z\" class=\"background\"/>\n					<path d=\"M18.025 16l-1.373 1.362c-0.261 0.259-0.608 0.402-0.977 0.4 c-0.369 0-0.716-0.143-0.976-0.401L13.323 16H8v3.528h16V16H18.025z M22.553 18.646h-2.465c-0.3 0-0.543-0.582-0.543-0.882 c0-0.3 0.243-0.882 0.543-0.882h2.465c0.3 0 0.5 0.6 0.5 0.882C23.095 18.1 22.9 18.6 22.6 18.646z\" class=\"foreground\"/>\n					<path d=\"M18.331 3h-5.582c-0.423 0-0.766 0.34-0.766 0.759v4.047l-1.677-0.002c-0.854 0-1.145 0.478-1.231 0.7 C8.99 8.7 8.9 9.2 9.5 9.835l5.048 5.002c0.276 0.3 0.6 0.4 1 0.425c0.391 0 0.759-0.151 1.035-0.426 l5.044-5.002c0.603-0.597 0.468-1.14 0.382-1.346c-0.086-0.206-0.378-0.684-1.231-0.684l-1.676 0.002V3.759 C19.096 3.3 18.8 3 18.3 3L18.331 3z\" class=\"foreground\"/>\n				</svg>\n			</a>\n				<svg class=\"remove\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.0\" x=\"0px\" y=\"0px\" viewBox=\"0 0 32 24\" enable-background=\"new 0 0 32 24\" xml:space=\"preserve\">\n				<path d=\"M29.167 24H2.833C1.269 24 0 22.7 0 21.2V2.8C0 1.3 1.3 0 2.8 0h26.333C30.731 0 32 1.3 32 2.8 v18.4C32 22.7 30.7 24 29.2 24z\" class=\"background\"/>\n				<polygon points=\"19,4 19,3 14,3 14,4 10,4 10,6 23,6 23,4\" class=\"foreground\"/>\n				<polygon points=\"11.1,7 12.1,19 20.7,19 21.7,7\" class=\"foreground\"/>\n			</svg>\n";
},"9":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<a href=\"/asset/downloadTemp/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.uuid : stack1), depth0))
    + "\">\n";
},"11":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<a href=\"/asset/download/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.uuid : stack1), depth0))
    + "\">\n";
},"13":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.isUpload : stack1),{"name":"if","hash":{},"fn":this.program(14, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.isUpload : stack1),{"name":"unless","hash":{},"fn":this.program(16, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				<svg class=\"deliverable-download\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.0\" x=\"0px\" y=\"0px\" viewBox=\"0 0 32 24\" enable-background=\"new 0 0 32 24\" xml:space=\"preserve\">\n					<path d=\"M29.167 24H2.833C1.269 24 0 22.7 0 21.2V2.8C0 1.3 1.3 0 2.8 0h26.333C30.731 0 32 1.3 32 2.8 v18.4C32 22.7 30.7 24 29.2 24z\" class=\"background\"/>\n					<path d=\"M18.025 16l-1.373 1.362c-0.261 0.259-0.608 0.402-0.977 0.4 c-0.369 0-0.716-0.143-0.976-0.401L13.323 16H8v3.528h16V16H18.025z M22.553 18.646h-2.465c-0.3 0-0.543-0.582-0.543-0.882 c0-0.3 0.243-0.882 0.543-0.882h2.465c0.3 0 0.5 0.6 0.5 0.882C23.095 18.1 22.9 18.6 22.6 18.646z\" class=\"foreground\"/>\n					<path d=\"M18.331 3h-5.582c-0.423 0-0.766 0.34-0.766 0.759v4.047l-1.677-0.002c-0.854 0-1.145 0.478-1.231 0.7 C8.99 8.7 8.9 9.2 9.5 9.835l5.048 5.002c0.276 0.3 0.6 0.4 1 0.425c0.391 0 0.759-0.151 1.035-0.426 l5.044-5.002c0.603-0.597 0.468-1.14 0.382-1.346c-0.086-0.206-0.378-0.684-1.231-0.684l-1.676 0.002V3.759 C19.096 3.3 18.8 3 18.3 3L18.331 3z\" class=\"foreground\"/>\n				</svg>\n			</a>\n";
},"14":function(depth0,helpers,partials,data) {
    var stack1;

  return "					<a class=\"asset-name\" href=\"/asset/downloadTemp/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.uuid : stack1), depth0))
    + "\">\n";
},"16":function(depth0,helpers,partials,data) {
    var stack1;

  return "					<a class=\"asset-name\" href=\"/asset/download/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.uuid : stack1), depth0))
    + "\">\n";
},"18":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<input type=\"hidden\" name=\"attachments["
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.index : stack1), depth0))
    + "].isUpload\" value=\"1\"/>\n";
},"20":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<input type=\"hidden\" name=\"attachments["
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.index : stack1), depth0))
    + "].isUpload\" value=\"0\"/>\n";
},"22":function(depth0,helpers,partials,data) {
    var stack1;

  return "					<span class=\""
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.code : depth0), depth0))
    + "\">"
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"name":(depth0 != null ? depth0['icon-code'] : depth0)},"data":data,"helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "</span>\n";
},"24":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					<li>\n						<a class=\"visibility-option\" data-visibility-code=\""
    + alias2(alias1((depth0 != null ? depth0.code : depth0), depth0))
    + "\" data-visibility-description=\""
    + alias2(alias1((depth0 != null ? depth0.description : depth0), depth0))
    + "\">\n							<span class=\"visibility-icon\">"
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"name":(depth0 != null ? depth0['icon-code'] : depth0)},"data":data,"helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "</span>\n							<span class=\"visibility-description\">"
    + alias2(alias1((depth0 != null ? depth0.description : depth0), depth0))
    + "</span>\n							<span class=\"checkmark\"><i class=\"wm-icon-checkmark\"></i></span>\n						</a>\n					</li>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<li class=\"non-required-deliverable\">\n	<div class=\"deliverable-icon-container span3\">\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.containsImageMimeType : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.containsImageMimeType : stack1),{"name":"unless","hash":{},"fn":this.program(6, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		<div class=\"options -hidden\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isAdminOrActiveResource : depth0),{"name":"if","hash":{},"fn":this.program(8, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isAdminOrActiveResource : depth0),{"name":"unless","hash":{},"fn":this.program(13, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</div>\n		<input type=\"hidden\" name=\"attachments["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.index : stack1), depth0))
    + "].id\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.id : stack1), depth0))
    + "\"/>\n		<input type=\"hidden\" name=\"attachments["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.index : stack1), depth0))
    + "].uuid\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.uuid : stack1), depth0))
    + "\"/>\n		<input type=\"hidden\" name=\"attachments["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.index : stack1), depth0))
    + "].name\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.name : stack1), depth0))
    + "\"/>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.isUpload : stack1),{"name":"if","hash":{},"fn":this.program(18, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.isUpload : stack1),{"name":"unless","hash":{},"fn":this.program(20, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</div>\n	<div class=\"asset-info span6\">\n		<p class=\"asset-name\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.name : stack1), depth0))
    + "</p>\n	</div>\n	<div class=\"asset-visibility span6\">\n		<div class=\"asset-visibility dropdown tooltipped tooltipped-n\" aria-label=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.visibilityTypeDescription : stack1), depth0))
    + "\">\n			<input class=\"visibility-selection dn\" name=\"attachments["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.index : stack1), depth0))
    + "].visibilityType\"  value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.visibilityCode : stack1), depth0))
    + "\"/>\n			<span class=\"visibility-copy\">Visible to:</span>\n			<a class=\"toggle-visibility dropdown-toggle button\" data-toggle=\"dropdown\" href=\"#\">\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.visibilitySettings : depth0),{"name":"each","hash":{},"fn":this.program(22, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</a>\n			<ul class=\"dropdown-menu\">\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.visibilitySettings : depth0),{"name":"each","hash":{},"fn":this.program(24, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</ul>\n		</div>\n	</div>\n	<div class=\"asset-description-container\">\n		<textarea rows=\"1\" maxlength=\"512\" class=\"asset-description\" placeholder=\"Short Description (Optional)\" name=\"attachments["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.index : stack1), depth0))
    + "].description\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.description : stack1), depth0))
    + "</textarea>\n	</div>\n</li>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["assignments/details/parts_table"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "-active-worker-exists";
},"3":function(depth0,helpers,partials,data) {
    return "-is-company";
},"5":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression, alias3=helpers.helperMissing;

  return "		<tr data-id=\""
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\">\n			<td class=\"tooltipped tooltipped-n\" aria-label=\""
    + alias2(alias1((depth0 != null ? depth0.name : depth0), depth0))
    + "\">"
    + alias2(alias1((depth0 != null ? depth0.displayName : depth0), depth0))
    + "</td>\n			<td>\n				<a class=\"tooltipped tooltipped-n parts-table--tracking-label "
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias3).call(depth0,(depth0 != null ? depth0.shippingProvider : depth0),"OTHER",{"name":"eq","hash":{},"fn":this.program(6, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\"\n				   aria-label=\""
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias3).call(depth0,(depth0 != null ? depth0.shippingProvider : depth0),"OTHER",{"name":"eq","hash":{},"fn":this.program(8, data, 0),"inverse":this.program(10, data, 0),"data":data})) != null ? stack1 : "")
    + "\"\n				   href=\""
    + alias2(alias1((depth0 != null ? depth0.providerUrl : depth0), depth0))
    + "\" target=\"_blank\">"
    + alias2(alias1((depth0 != null ? depth0.trackingNumber : depth0), depth0))
    + "\n				</a>\n			</td>\n			<td class=\"_provider_ "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.hasProviderIcon : depth0),{"name":"if","hash":{},"fn":this.program(12, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.hasProviderIcon : depth0),{"name":"unless","hash":{},"fn":this.program(6, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n"
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"active":"true","name":"fedex"},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"active":"true","name":"dhl"},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"active":"true","name":"usps"},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"active":"true","name":"ups"},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"name":"truck"},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			</td>\n			 <td class=\"partValue\">$"
    + alias2((helpers.roundCurrency || (depth0 && depth0.roundCurrency) || alias3).call(depth0,(depth0 != null ? depth0.partValue : depth0),{"name":"roundCurrency","hash":{},"data":data}))
    + "</td>\n			<td class=\"-status\">\n				<span class=\"parts-table--tracking-status -"
    + alias2((helpers.lowerAndReplace || (depth0 && depth0.lowerAndReplace) || alias3).call(depth0,(depth0 != null ? depth0.trackingStatus : depth0),"-",{"name":"lowerAndReplace","hash":{},"data":data}))
    + "\">\n					"
    + alias2((helpers.lowerAndReplace || (depth0 && depth0.lowerAndReplace) || alias3).call(depth0,(depth0 != null ? depth0.trackingStatus : depth0)," ",{"name":"lowerAndReplace","hash":{},"data":data}))
    + "\n				</span>\n				<i class=\"wm-icon-trash _remove_\"></i>\n			</td>\n		</tr>\n";
},"6":function(depth0,helpers,partials,data) {
    return "-untracked";
},"8":function(depth0,helpers,partials,data) {
    return "";
},"10":function(depth0,helpers,partials,data) {
    var stack1;

  return "View package on "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.part : depth0)) != null ? stack1.shippingProvider : stack1), depth0))
    + " website";
},"12":function(depth0,helpers,partials,data) {
    return "-"
    + this.escapeExpression((helpers.lowerAndReplace || (depth0 && depth0.lowerAndReplace) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.shippingProvider : depth0),"_",{"name":"lowerAndReplace","hash":{},"data":data}));
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<table class=\"parts-table "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isNotDraftOrSent : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isOwnerOrAdmin : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n	<thead>\n		<tr>\n			<th>Part Name</th>\n			<th>Tracking Number</th>\n			<th>Provider</th>\n			<th>Part Value</th>\n			<th>Status</th>\n		</tr>\n	</thead>\n	<tbody>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.parts : depth0),{"name":"each","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</tbody>\n	<tfoot>\n	<tr class=\"parts-table--total-price\">\n		<td colspan=\"4\">Total: $"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.totalPrice : depth0), depth0))
    + "</td>\n	</tr>\n	</tfoot>\n</table>";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["assignments/details/pdf_viewer"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<embed class=\"pdf-previewer\" src=\"/asset/"
    + this.escapeExpression(this.lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.asset : depth0)) != null ? stack1.attributes : stack1)) != null ? stack1.uuid : stack1), depth0))
    + "\" alt=\"pdf\" pluginspage=\"http://www.adobe.com/products/acrobat/readstep2.html\">\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/pendingUploadRequiredDeliverable"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper;

  return "<li class=\"temp\" data-position=\""
    + this.escapeExpression(((helper = (helper = helpers.position || (depth0 != null ? depth0.position : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"position","hash":{},"data":data}) : helper)))
    + "\"></li>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/shortcut_reject_deliverable_modal"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"alert alert-error\">\n	<span>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.error : depth0), depth0))
    + "</span>\n	<button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/shortcut_reject_deliverable_modal_body"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"error-notifications\" style=\"display: none;\">\n	<div class=\"errors\"></div>\n</div>\n<div class=\"shortcutRejectDeliverableModalBody\">\n	<p>When you reject a deliverable, the worker is notified and your rejection reason is provided.</p>\n	<textarea class=\"shortcutRejectDeliverableModalReason required\" placeholder=\"Please input your reason for rejecting the file\" name=\"rejection_reason\" maxlength=\"200\"></textarea>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/uploadProgressBar"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<div class=\"upload-progress\">\n	<div class=\"fileNamePlaceholder\">Uploading <span class=\"number\">"
    + alias3(((helper = (helper = helpers.number || (depth0 != null ? depth0.number : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"number","hash":{},"data":data}) : helper)))
    + "</span> <span class=\"object\">"
    + alias3(((helper = (helper = helpers.object || (depth0 != null ? depth0.object : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"object","hash":{},"data":data}) : helper)))
    + "</span></div>\n	<div class=\"progress progress-striped active\">\n		<div class=\"bar\"></div>\n	</div>\n</div>";
},"useData":true});

this["wm"]["templates"]["assignments/details/workerMapCount"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<div>\n	<span id=\"all_workers\" class=\"resources\" style=\"margin-right:20px\"><img src=\""
    + alias3(((helper = (helper = helpers.mediaPrefix || (depth0 != null ? depth0.mediaPrefix : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"mediaPrefix","hash":{},"data":data}) : helper)))
    + "/images/map/worker_invite_small.png\"/> All "
    + alias3(((helper = (helper = helpers.all || (depth0 != null ? depth0.all : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"all","hash":{},"data":data}) : helper)))
    + "</span>\n	<span id=\"applied_workers\" class=\"resources\" style=\"margin-right:20px\"><img src=\""
    + alias3(((helper = (helper = helpers.mediaPrefix || (depth0 != null ? depth0.mediaPrefix : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"mediaPrefix","hash":{},"data":data}) : helper)))
    + "/images/map/worker_applied_small.png\"/> Applied "
    + alias3(((helper = (helper = helpers.applied || (depth0 != null ? depth0.applied : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"applied","hash":{},"data":data}) : helper)))
    + "</span>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/details/workers"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "			Sort by\n			<select name=\"sortColumn\" class=\"wm-select assignment-workers--sort-type\">\n				<option value=\"NEGOTIATION_TOTAL_COST\">Assignment Value</option>\n				<option value=\"NEGOTIATION_SCHEDULE_FROM\">Start Time</option>\n				<option value=\"NEGOTIATION_CREATED_ON\" selected=\"selected\">Applied Time</option>\n				<option value=\"AVG_RATING\">Rating</option>\n				<option value=\"DISTANCE\" data-default-direction=\"ASC\">Distance</option>\n			</select>\n\n			<select name=\"sortDirection\" class=\"wm-select assignment-workers--sort-dir\">\n				<option value=\"ASC\">&#9650;</option>\n				<option value=\"DESC\" selected=\"selected\">&#9660;</option>\n			</select>\n";
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isDispatcher : depth0),{"name":"unless","hash":{},"fn":this.program(4, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"4":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<div class=\"assignment-workers--talent-filter\">\n"
    + ((stack1 = this.invokePartial(partials.filter,depth0,{"name":"filter","hash":{"isChecked":"true","text":"Workers","classlist":"show-workers","name":"workers"},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.filter,depth0,{"name":"filter","hash":{"text":"Vendors","classlist":"show-vendors","name":"vendors"},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "				<div class=\"vendor-count\"></div>\n				<div class=\"worker-count\"></div>\n			</div>\n";
},"6":function(depth0,helpers,partials,data) {
    var stack1;

  return "	<div class=\"wm-action-container\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.showAssignButton : depth0),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isSent : depth0),{"name":"if","hash":{},"fn":this.program(9, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		<a href=\"/assignments/contact/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.workNumber : stack1), depth0))
    + "\" class=\"button js-workers-invite-more-workers\">Invite More Workers</a>\n	</div>\n";
},"7":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<button href=\"/assignments/assign/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.workNumber : stack1), depth0))
    + "\" class=\"button assign_action\">Assign to Employee</button>\n";
},"9":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<a href=\"/assignments/resend_resource_invitation/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.workNumber : stack1), depth0))
    + "\" class=\"resend_invite button\">Resend</a>\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.workNotifyAvailable : stack1),{"name":"unless","hash":{},"fn":this.program(10, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.workNotifyAllowed : stack1),{"name":"unless","hash":{},"fn":this.program(12, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.workNotifyAvailable : stack1),{"name":"if","hash":{},"fn":this.program(14, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"10":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.button,depth0,{"name":"button","hash":{"text":"Work Notify","disabled":"true","tooltip":"None of the workers have opted to receive notifications."},"data":data,"indent":"\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"12":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.button,depth0,{"name":"button","hash":{"text":"Work Notify","disabled":"true","tooltip":"Notifying workers is limited to once per hour."},"data":data,"indent":"\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"14":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.workNotifyAllowed : stack1),{"name":"if","hash":{},"fn":this.program(15, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"15":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.button,depth0,{"name":"button","hash":{"text":"Work Notify<sup>&trade;</sup","classlist":"work-notify"},"data":data,"indent":"\t\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"17":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isDispatcher : depth0),{"name":"if","hash":{},"fn":this.program(18, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"18":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<div class=\"wm-action-container\">\n"
    + ((stack1 = this.invokePartial(partials.button,depth0,{"name":"button","hash":{"text":"Decline","classlist":"vendor-decline"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			<a href=\"/assignments/contact/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.work : depth0)) != null ? stack1.workNumber : stack1), depth0))
    + "\" class=\"button -add\">Add Candidates</a>\n		</div>\n";
},"20":function(depth0,helpers,partials,data) {
    var stack1, helper;

  return "	<div class=\"form-actions\">\n		This assignment is part of a bundle called: \""
    + this.escapeExpression(((helper = (helper = helpers.bundleTitle || (depth0 != null ? depth0.bundleTitle : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"bundleTitle","hash":{},"data":data}) : helper)))
    + "\".\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isDispatcher : depth0),{"name":"if","hash":{},"fn":this.program(21, data, 0),"inverse":this.program(23, data, 0),"data":data})) != null ? stack1 : "")
    + "	</div>\n";
},"21":function(depth0,helpers,partials,data) {
    return "			You can click <a href=\"/assignments/view_bundle/${workResponse.workBundleParent.id}\">here</a> to see this bundle.\n";
},"23":function(depth0,helpers,partials,data) {
    return "			You can click <a href=\"/assignments/view_bundle/${workResponse.workBundleParent.id}\">here</a> to invite more workers to this bundle.\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"assignment-workers--toolbar\">\n	<div class=\"assignment-workers--sort-container\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.showSort : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</div>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.hasInvitedAtLeastOneVendor : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n<div class=\"assignment-workers--feed\"></div>\n"
    + ((stack1 = this.invokePartial(partials.pagination,depth0,{"name":"pagination","hash":{"max":"10","min":"1"},"data":data,"helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.showActions : depth0),{"name":"if","hash":{},"fn":this.program(6, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.showBundleActions : depth0),{"name":"unless","hash":{},"fn":this.program(17, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.showBundleActions : depth0),{"name":"if","hash":{},"fn":this.program(20, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"usePartial":true,"useData":true});

this["wm"]["templates"]["assignments/documents/nonRequiredDocumentContainer"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"uploader-container\"></div>\n<a id=\"select_filemanager_files\" class=\"button file-manager-button\">File Manager</a>\n";
},"useData":true});

this["wm"]["templates"]["assignments/documents/nonRequiredDocumentUploadPending"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<li class=\"non-required-deliverable row\">\n	<div class=\"fileNamePlaceholder qq-upload-file\"></div>\n	<div class=\"progress-bar progress progress-striped active\">\n		<div class=\"bar\"></div>\n	</div>\n	<div class=\"qq-upload-size-parent\">\n		<span class=\"qq-upload-size\"></span>\n	</div>\n	<div class=\"qq-upload-cancel\"></div>\n	<div class=\"qq-upload-spinner-parent\">\n		<div class=\"qq-upload-spinner\"></div>\n	</div>\n</li>\n";
},"useData":true});

this["wm"]["templates"]["assignments/documents/uploaderButton"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"button uploader-button qq-upload-button\">\n	<div class=\"input\"></div>\n	<i class=\"wm-icon-upload\"></i>\n	<span class=\"upload-files\">Upload New Document</span>\n</div>\n<div class=\"qq-upload-drop-area\"></div>\n";
},"useData":true});

this["wm"]["templates"]["assignments/routing/routing_form"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "						<option value=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\">"
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "</option>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"assignment-routing\">\n	<div class=\"assignment-routing--header\">\n		Routing Options\n	</div>\n	<div class=\"assignment-routing--section-header\">\n		Publishing\n	</div>\n	<div class=\"public-marketplace routing-option\">\n"
    + ((stack1 = this.invokePartial(partials.checkbox,depth0,{"name":"checkbox","hash":{"text":"Public Marketplace","isChecked":(depth0 != null ? depth0.showInFeed : depth0),"value":"true","name":"show_in_feed"},"data":data,"indent":"\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "		<label for=\"public_marketplace\">Selecting this option will make the assignment visible to any worker or vendor on Work Market.</label>\n	</div>\n	<div class=\"assignment-routing--section-header\">\n		Invite Talent\n	</div>\n	<div class=\"wm-accordion routing-selector\">\n		<div class=\"wm-accordion--heading routing-option\" data-accordion-target=\"direct_send_options\">\n"
    + ((stack1 = this.invokePartial(partials.radio,depth0,{"name":"radio","hash":{"text":"Send to Specific Talent","value":"direct_send","name":"send_type"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			<label class=\"subtext\" for=\"send_type\">Select which Talent Pools and/or Workers you'd like to send the assignment to.</label>\n		</div>\n		<div class=\"wm-accordion--content\" data-accordion-name=\"direct_send_options\">\n			<div class=\"inner\">\n				<label class=\"heading\" for=\"talent_select\">Talent</label>\n				<input id=\"routing_resource_ids\" name=\"resourceNumbers\" placeholder=\"Type any part of workers name (minimum of 3 characters)\" type=\"text\" value=\"\" tabindex=\"-1\"  />\n				<label class=\"heading\" for=\"talent_pool_select\">Groups</label>\n				<select id=\"routing_groups_ids\" name=\"groupIds\" tabindex=\"-1\" class=\"wm-select\" placeholder=\"Type any part of group name\" multiple=\"multiple\">\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.routableGroups : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</select>\n"
    + ((stack1 = this.invokePartial(partials.checkbox,depth0,{"name":"checkbox","hash":{"text":"Automatically assign to first applicant","isChecked":(depth0 != null ? depth0.assignToFirstResource : depth0),"value":"true","name":"assign_to_first_resource"},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "				<div class=\"estimated-worker-count\"></div>\n			</div>\n		</div>\n\n		<div class=\"wm-accordion--heading routing-option\">\n"
    + ((stack1 = this.invokePartial(partials.radio,depth0,{"name":"radio","hash":{"text":"WorkSend<span class=\"tm\">TM</span>","value":"work_send","class":"work-send","name":"send_type"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			<label for=\"send_type\" class=\"subtext\">This option will automatically determine the best workers for your assignment.</label>\n		</div>\n\n		<div class=\"wm-accordion--heading routing-option\">\n"
    + ((stack1 = this.invokePartial(partials.radio,depth0,{"name":"radio","hash":{"text":"Browse Talent Marketplace","value":"search_resources","class":"search-resources","name":"send_type"},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			<label for=\"send_type\" class=\"subtext\">Search the Talent Marketplace to find the best people for the job.</label>\n		</div>\n	</div>\n	<div class=\"assignment-routing--actions\">\n"
    + ((stack1 = this.invokePartial(partials.button,depth0,{"name":"button","hash":{"text":"Save &amp; Route","id":"submit-form"},"data":data,"indent":"\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "	</div>\n</div>\n\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["assignments/uploader/uploaderButton"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"qq-uploader\">\n	<div class=\"qq-upload-drop-area\"></div>\n	<a href=\"javascript:void(0);\" name=\"upload\" class=\"qq-upload-button btn\"></a>\n	<ul class=\"qq-upload-list\"></ul>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["compliancerulesets/abstractcomplianceruleform"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div data-placeholder=\"partitioner\"></div>\n<div data-placeholder=\"complianceable\"></div>\n<div data-placeholder=\"add-button\"></div>\n";
},"useData":true});

this["wm"]["templates"]["compliancerulesets/assignment-count-compliance-form"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"control-group\">\n	<label for=\"maximum-assignments\" class=\"control-label\">Input the maximum number of allowed assignments:</label>\n	<div class=\"controls\">\n		<input id=\"maximum-assignments\" name=\"maximum-assignments\" type=\"text\" />\n	</div>\n	<label for=\"interval\" class=\"control-label\">Select the compliance interval</label>\n	<div class=\"controls\">\n		<select id=\"interval\" class=\"input-block-level\" name=\"interval\" data-selections=\"interval\">\n			<option value=\"WEEK\">Weekly</option>\n			<option value=\"MONTH\">Monthly</option>\n			<option value=\"QUARTER\">Quarterly</option>\n			<option value=\"YEAR\">Yearly</option>\n			<option value=\"LIFETIME\">Lifetime</option>\n		</select>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["compliancerulesets/button"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "disabled=\"disabled\"";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<button class=\"button\" "
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.enabled : depth0),{"name":"unless","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "data-action=\"add\">Add "
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.label : depth0), depth0))
    + "</button>\n";
},"useData":true});

this["wm"]["templates"]["compliancerulesets/cart-item"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "<span><i class=\"wm-icon-trash icon-large muted\" data-action=\"trash\"></i></span>\n<span>"
    + alias2(alias1((depth0 != null ? depth0.complianceRuleType : depth0), depth0))
    + ": <strong>"
    + alias2(alias1((depth0 != null ? depth0.content : depth0), depth0))
    + "</strong></span>\n";
},"useData":true});

this["wm"]["templates"]["compliancerulesets/compliance-rules-cart"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<h5>Current Requirements</h5>\n<div class=\"well-content\">\n	<div data-placeholder=\"cart\"></div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["compliancerulesets/compliancerulesetform"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"row\">\n	<div class=\"span5\">\n		<div class=\"compliance-rules-form\"></div>\n	</div>\n	<div class=\"span1\">\n		<div class=\"segue\">\n			<i class=\"icon-arrow-right icon-4x muted\"></i>\n		</div>\n	</div>\n	<div class=\"span5\">\n		<div class=\"compliance-rules-cart\"></div>\n		<div class=\"compliance-rules-save\"></div>\n	</div>\n</div>\n<hr />\n";
},"useData":true});

this["wm"]["templates"]["compliancerulesets/compliancerulesform"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					<option value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.name : stack1), depth0))
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.humanName : stack1), depth0))
    + "</option>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<h5>Add Compliance Rules</h5>\n<div class=\"well-content\">\n	<div class=\"control-group\">\n		<label for=\"complianceRuleTypes\" class=\"control-label\">Select the type of compliance rule to add to your set</label>\n		<div class=\"controls\">\n			<select id=\"complianceRuleTypes\" class=\"input-block-level\" data-toggle=\"form\">\n				<option class=\"prompt\">- Select -</option>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.complianceRuleTypes : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</select>\n		</div>\n		<div data-placeholder=\"form\"></div>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["compliancerulesets/save"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<a href=\"#\" class=\"button\" data-action=\"save\">Save</a>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/approve_assignment_for_pay"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<tr>\n					<td class='span4'>Attachments</td>\n					<td>\n						<ul class=\"unstyled\">\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.closingAssets : depth0),{"name":"each","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "						</ul>\n					</td>\n				</tr>\n";
},"2":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "								<li>\n									<a href=\"/asset/download/"
    + alias2(alias1((depth0 != null ? depth0.uuid : depth0), depth0))
    + "\">"
    + alias2(alias1((depth0 != null ? depth0.name : depth0), depth0))
    + "</a>\n									"
    + alias2(alias1((depth0 != null ? depth0.description : depth0), depth0))
    + "\n								</li>\n";
},"4":function(depth0,helpers,partials,data) {
    return "				<tr>\n					<td class='span4'>Pricing</td>\n					<td>Internal</td>\n				</tr>\n";
},"6":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "				<tr>\n					<td class='span4'>Payment Requested</td>\n					<td>"
    + alias2(alias1((depth0 != null ? depth0.formattedCurrency : depth0), depth0))
    + "</td>\n				</tr>\n				<tr>\n				<td class='span4'>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.payment : stack1)) != null ? stack1.legacyBuyerFee : stack1),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.program(9, data, 0),"data":data})) != null ? stack1 : "")
    + "				</td>\n					<td>"
    + alias2(alias1((depth0 != null ? depth0.buyerFee : depth0), depth0))
    + "</td>\n				</tr>\n\n				<tr>\n					<td class=\"span4\">Total Cost</td>\n					<td>"
    + alias2(alias1((depth0 != null ? depth0.totalCost : depth0), depth0))
    + "</td>\n				</tr>\n				<tr>\n					<td class=\"span4\">Payment Terms</td>\n					<td>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isImmediate : depth0),{"name":"if","hash":{},"fn":this.program(11, data, 0),"inverse":this.program(13, data, 0),"data":data})) != null ? stack1 : "")
    + "					</td>\n				</tr>\n";
},"7":function(depth0,helpers,partials,data) {
    return "					Flat Fee\n";
},"9":function(depth0,helpers,partials,data) {
    var stack1;

  return "					Transaction Fee ("
    + this.escapeExpression(this.lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.payment : stack1)) != null ? stack1.buyerFeePercentage : stack1), depth0))
    + "%)\n";
},"11":function(depth0,helpers,partials,data) {
    var stack1;

  return "						"
    + this.escapeExpression(this.lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.configuration : stack1)) != null ? stack1.paymentTermsDays : stack1), depth0))
    + " days\n";
},"13":function(depth0,helpers,partials,data) {
    return "						Immediate\n";
},"15":function(depth0,helpers,partials,data) {
    return "disabled";
},"17":function(depth0,helpers,partials,data) {
    return "				<div style=\"color:red; margin-top: 10px\">You are not authorized to approve or decline this request. Please contact your manager or account administrator to approve or decline</div>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"row\">\n	<div class=\"span12\">\n		<form id=\"approve_assignment_form\" accept-charset=\"utf-8\">\n		<input type=\"hidden\" name=\"id\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.workNumber : stack1), depth0))
    + "\" />\n		<table id=\"approve_assignment_table\">\n			<tbody>\n				<tr>\n					<td class='span4'>Resolution</td>\n					<td class='span8'>"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.resolution : stack1), depth0))
    + "</td>\n				</tr>\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.closingAssets : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isInternalAssignment : depth0),{"name":"if","hash":{},"fn":this.program(4, data, 0),"inverse":this.program(6, data, 0),"data":data})) != null ? stack1 : "")
    + "			</tbody>\n		</table>\n		<div class=\"wm-action-container\">\n			<a class=\"button sendback_action\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isNotAuthorizedForPayment : depth0),{"name":"if","hash":{},"fn":this.program(15, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " href=\"/assignments/sendback/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.id : stack1), depth0))
    + "/assignments\" data-title=\"Send Assignment Back to Worker\">I&rsquo;m Not Satisfied</a>\n			<a class=\"button submit_approve_pay\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isNotAuthorizedForPayment : depth0),{"name":"if","hash":{},"fn":this.program(15, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">Approve Work</a>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isNotAuthorizedForPayment : depth0),{"name":"if","hash":{},"fn":this.program(17, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</div>\n		</form>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/bulk_add_attachment"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<input type=\"hidden\" name=\"assignment_ids_attachments\" id=\"assignment_ids_attachments\"/>\n\n<div id=\"assets\">\n	<div class=\"general-assets\">\n		<ul></ul>\n	</div>\n\n	<div class=\"upload-assets\">\n		<form>\n			<div class=\"clearfix\">\n				<div class=\"pull-left\">\n					<div class=\"attachment-uploader pull-left mr\"></div>\n					<input name=\"attachment_description\" style=\"height: 28px;\" placeholder=\"Description\" class=\"span2\"/>\n				</div>\n			</div>\n		</form>\n	</div>\n	<div class=\"wm-action-container\">\n		<button class=\"button\" data-modal-close>Close</button>\n		<button type=\"button\" id=\"fileupload_done\" class=\"button\">Done</button>\n	</div>\n</div>";
},"useData":true});

this["wm"]["templates"]["dashboard/bulk_add_label"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div id=\"bulk-add-label\">\n	<input type=\"hidden\" name=\"assignment_id_label\" id=\"assignment_id_label\"/>\n\n	<div id=\"add_note_container_labels\"></div>\n	<div id=\"add_note_container_schedule\"></div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/bulk_asset_item"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<a href=\"/asset/downloadDefault/"
    + alias2(alias1((depth0 != null ? depth0.uuid : depth0), depth0))
    + "\">"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.file_name : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.program(4, data, 0),"data":data})) != null ? stack1 : "")
    + "</a>\n		<a class=\"remove muted no-decor\"><i class=\"icon-large wm-icon-trash\"></i>remove</a>\n		<br/>\n		<small>"
    + alias2(alias1((depth0 != null ? depth0.description : depth0), depth0))
    + "</small>\n";
},"2":function(depth0,helpers,partials,data) {
    return this.escapeExpression(this.lambda((depth0 != null ? depth0.file_name : depth0), depth0));
},"4":function(depth0,helpers,partials,data) {
    return this.escapeExpression(this.lambda((depth0 != null ? depth0.name : depth0), depth0));
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.mimeType : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/bulk_custom_field_input"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "required";
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<div class=\"text\">\n			"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.value : stack1),{"name":"if","hash":{},"fn":this.program(4, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n		</div>\n";
},"4":function(depth0,helpers,partials,data) {
    var stack1;

  return this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.value : stack1), depth0));
},"6":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<div class=\"container\">\n			<input type=\"hidden\" name=\"fields["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.index : stack1), depth0))
    + "].id\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.id : stack1), depth0))
    + "\"/>\n			<input type=\"text\" name=\"fields["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.index : stack1), depth0))
    + "].value\" placeholder=\"Insert value\" value=\"\" "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.readOnly : stack1),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " />\n"
    + ((stack1 = this.invokePartial(partials.checkbox,depth0,{"name":"checkbox","hash":{"name":"bulk_field_update[]","value":((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.id : stack1)},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "		</div>\n";
},"7":function(depth0,helpers,partials,data) {
    return "readonly=\"readonly\"";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"clearfix\">\n	<label for=\"fields["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.index : stack1), depth0))
    + "].id\"  class=\""
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.isRequired : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.name : stack1), depth0))
    + "</label>\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.readOnly : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.program(6, data, 0),"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["dashboard/bulk_custom_field_select"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "required";
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<div class=\"text\">\n			"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.value : stack1),{"name":"if","hash":{},"fn":this.program(4, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n		</div>\n";
},"4":function(depth0,helpers,partials,data) {
    var stack1;

  return this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.value : stack1), depth0));
},"6":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<div class=\"container\">\n			<input type=\"hidden\" name=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.param : depth0)) != null ? stack1.prefix : stack1), depth0))
    + "fields["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.index : stack1), depth0))
    + "].id\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.id : stack1), depth0))
    + "\"/>\n			<select name=\"fields["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.index : stack1), depth0))
    + "].value\" "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.readOnly : stack1),{"name":"if","hash":{},"fn":this.program(7, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">\n				<option value=\"\">Select</option>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.options : stack1),{"name":"each","hash":{},"fn":this.program(9, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</select>\n"
    + ((stack1 = this.invokePartial(partials.checkbox,depth0,{"name":"checkbox","hash":{"name":"bulk_field_update[]","value":((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.id : stack1)},"data":data,"indent":"\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "		</div>\n";
},"7":function(depth0,helpers,partials,data) {
    return "readonly=\"readonly\"";
},"9":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					<option value=\""
    + alias2(alias1(depth0, depth0))
    + "\" "
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,depth0,((stack1 = (depths[2] != null ? depths[2].model : depths[2])) != null ? stack1.value : stack1),{"name":"eq","hash":{},"fn":this.program(10, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">"
    + alias2(alias1(depth0, depth0))
    + "</option>\n";
},"10":function(depth0,helpers,partials,data) {
    return "selected=\"selected\"";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"clearfix\">\n	<label for=\"fields["
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.index : stack1), depth0))
    + "].id\" class=\""
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.isRequired : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.name : stack1), depth0))
    + "</label>\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.readOnly : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0, blockParams, depths),"inverse":this.program(6, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"usePartial":true,"useData":true,"useDepths":true});

this["wm"]["templates"]["dashboard/bulk_delete_assignments"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div id=\"bulk-delete-assignments\">\n	<input type=\"hidden\" name=\"assignment_id_delete\" id=\"assignment_id_delete\"/>\n	<p>Are you sure you want to delete or void the pre-selected assignments?</p>\n	<div class=\"wm-action-container\">\n		<button data-modal-close class=\"button\">Close</button>\n		<button id=\"delete_button\" class=\"button\">Delete / Void Assignments</button>\n	</div>\n</div>";
},"useData":true});

this["wm"]["templates"]["dashboard/bulk_download"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div id=\"bulk-download-modal\">\n	<input type=\"hidden\" name=\"assignment_id_download\" id=\"assignment_id_download\"/>\n\n	<p>We will package up all the attachments associated with the already selected assignments. Click send below to start.</p>\n\n	<div class=\"wm-action-container\">\n		<button class=\"button\" data-modal-close>Close</button>\n		<button id=\"download_assets\" class=\"button\">Send</button>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/bulk_notes"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<form action=\"/assignments/add_notes\" method=\"post\" id=\"add-note-form\" class=\"form-stacked\">\n\n	<input type=\"hidden\" name=\"assignment_ids\" id=\"assignment_ids\"/>\n	<input type=\"hidden\" name=\"is_select_all\" id=\"is_select_all\"/>\n\n	<div class=\"clearfix\">\n		<div class=\"input\">\n			<textarea name=\"multiple_content\" id=\"multiple_content\" rows=\"5\" class=\"span8\"></textarea>\n		</div>\n	</div>\n\n	<div class=\"clearfix\">\n		<div class=\"input\">\n			<ul class=\"inputs-list\">\n				<li>\n					<label>\n						<input type=\"radio\" name=\"is_private_multiple\" value=\"0\" checked=\"true\"/>\n						Everyone on assignment can see\n					</label>\n				</li>\n				<li>\n					<label>\n						<input type=\"radio\" name=\"is_private_multiple\" value=\"1\"/>\n						Private: Only my company can see\n					</label>\n				</li>\n			</ul>\n		</div>\n	</div>\n\n	<div class=\"wm-action-container\">\n		<button class=\"button\" data-modal-close>Close</button>\n		<button class=\"button\" id=\"submit_add_notes\">Add Note</button>\n	</div>\n\n</form>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/bulk_remove_attachment"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<input type=\"hidden\" name=\"assignment_ids_remove_attachments\" id=\"assignment_ids_remove_attachments\"/>\n\n<div id=\"assets\">\n	<div class=\"message\"></div>\n	<div class=\"general-assets\">\n		<ul></ul>\n	</div>\n	<div class=\"wm-action-container\">\n		<button type=\"button\" id=\"fileupload_done\" data-modal-close class=\"button\">Finished</button>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/bulk_remove_label"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div id=\"remove_label_container\">\n	<input type=\"hidden\" name=\"assignment_id_label\" id=\"remove_assignment_id_label\"/>\n	<div id=\"remove-all-labels-alert\" class=\"dn alert alert-error\"></div>\n\n	<div id=\"label-remove-form-container\"></div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/bulk_reschedule"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<form class=\"form-inline\" id=\"bulk_reschedule_form\" onsubmit=\"return false;\">\n\n	<div class=\"control-group\">\n		<label class=\"radio inline\">\n			<input type=\"radio\" value=\"1\" name=\"scheduling\" id=\"scheduling2\">\n			<span>Schedule <strong>time</strong></span>\n		</label>\n\n		<label class=\"radio inline\">\n			<input type=\"radio\" checked=\"checked\" value=\"0\" name=\"scheduling\" id=\"scheduling1\">\n			<span>Schedule <strong>time window</strong></span>\n		</label>\n	</div>\n\n	<div class=\"reschedule-inputs\">\n		<div class=\"control-group\">\n			<input type=\"text\" value=\"\" placeholder=\"Select Date\" class=\"span2 hasDatePicker\" name=\"from\" id=\"reschedule-from\">\n			<input type=\"text\" value=\"\" placeholder=\"Select Time\" class=\"span2\" name=\"fromtime\" id=\"reschedule-from-time\" autocomplete=\"off\">\n			<div id=\"reschedule-variable-time\">\n				<span>to</span>\n				<br/>\n				<input type=\"text\" value=\"\" placeholder=\"Select Date\" class=\"span2 hasDatePicker\" name=\"to\" id=\"reschedule-to\">\n				<input type=\"text\" value=\"\" placeholder=\"Select Time\" class=\"span2\" name=\"totime\" id=\"reschedule-to-time\" autocomplete=\"off\">\n			</div>\n		</div>\n	</div>\n\n	<div class=\"control-group\">\n		<label class=\"control-label\" for=\"label_note\">Note:</label>\n		<div class=\"controls\">\n			<textarea  id=\"label_note\"></textarea>\n		</div>\n	</div>\n\n	<div class=\"wm-action-container\">\n		<button class=\"button\" data-modal-close>Close</button>\n		<button type=\"button\" class=\"button\" id=\"reschedule_assignments\">Reschedule</button>\n	</div>\n</form>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/bulk_update_project"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div id=\"bulk_edit_projects_container\">\n	<p>You are about to update the selected assignments with the following client / project</p>\n\n	<div class=\"control-group\">\n		<label>Client:</label>\n		<select name=\"client_id\"></select>\n	</div>\n\n	<div class=\"control-group\">\n		<label>Project:</label>\n		<select name=\"project_id\" disabled=\"disabled\">\n			<option>Select</option>\n		</select>\n	</div>\n\n	<div class=\"wm-action-container\">\n		<button class=\"button\" data-modal-close>Close</button>\n		<button id=\"do_update_project\" class=\"button\">Update Projects</button>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/calendar_popover_content"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "					<a href="
    + alias3(((helper = (helper = helpers.detail_url || (depth0 != null ? depth0.detail_url : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"detail_url","hash":{},"data":data}) : helper)))
    + ">"
    + alias3(((helper = (helper = helpers.title || (depth0 != null ? depth0.title : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"title","hash":{},"data":data}) : helper)))
    + "</a>\n";
},"3":function(depth0,helpers,partials,data) {
    var helper;

  return "					<small class=\"meta\">("
    + this.escapeExpression(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + ")</small>\n";
},"5":function(depth0,helpers,partials,data) {
    var helper;

  return "				<td>"
    + this.escapeExpression(((helper = (helper = helpers.status || (depth0 != null ? depth0.status : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"status","hash":{},"data":data}) : helper)))
    + "</td>\n";
},"7":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.hidePricing : depth0),{"name":"unless","hash":{},"fn":this.program(8, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"8":function(depth0,helpers,partials,data) {
    var helper;

  return "					<td>"
    + this.escapeExpression(((helper = (helper = helpers.price || (depth0 != null ? depth0.price : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"price","hash":{},"data":data}) : helper)))
    + "</td>\n";
},"10":function(depth0,helpers,partials,data) {
    var helper;

  return "				<td>"
    + this.escapeExpression(((helper = (helper = helpers.price || (depth0 != null ? depth0.price : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"price","hash":{},"data":data}) : helper)))
    + "</td>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<table id=\"calendar-popover-content\">\n	<tbody>\n		<tr>\n			<td>\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.custom : depth0),{"name":"unless","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.id : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</td>\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.custom : depth0),{"name":"unless","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			<td>"
    + alias3(((helper = (helper = helpers.scheduled_date || (depth0 != null ? depth0.scheduled_date : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"scheduled_date","hash":{},"data":data}) : helper)))
    + "</td>\n			<td>"
    + alias3(((helper = (helper = helpers.resource_full_name || (depth0 != null ? depth0.resource_full_name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"resource_full_name","hash":{},"data":data}) : helper)))
    + "</td>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isWorkerCompany : depth0),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.program(10, data, 0),"data":data})) != null ? stack1 : "")
    + "			<td>"
    + alias3(((helper = (helper = helpers.address || (depth0 != null ? depth0.address : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"address","hash":{},"data":data}) : helper)))
    + "</td>\n		</tr>\n	</tbody>\n</table>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/calendar_popover_intuit_content"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<table id=\"calendar-popover-custom-content\">\n	<tbody>\n		<tr>\n			<td>\n				<p>"
    + alias3(((helper = (helper = helpers.custom_copy || (depth0 != null ? depth0.custom_copy : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"custom_copy","hash":{},"data":data}) : helper)))
    + " <span class=\"third-party-logo -inline -square -intuit-qb\"></span> <a href="
    + alias3(((helper = (helper = helpers.detail_url || (depth0 != null ? depth0.detail_url : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"detail_url","hash":{},"data":data}) : helper)))
    + " target=\"_blank\">"
    + alias3(((helper = (helper = helpers.custom_cta || (depth0 != null ? depth0.custom_cta : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"custom_cta","hash":{},"data":data}) : helper)))
    + "</a></p>\n			</td>\n		</tr>\n	</tbody>\n</table>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/calendar_popover_title"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return this.escapeExpression(this.lambda((depth0 != null ? depth0.title : depth0), depth0))
    + "\n<button\n	type=\"button\"\n	class=\"close wm-icon-x\"\n></button>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/create_bundle"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div id=\"create-bundle-container\">\n	<p>Bundles allow you to group multiple assignments together. Upon acceptance, all assignments in the bundle are assigned to the same worker.</p>\n\n	<form id=\"bundle_form\" class=\"form-horizontal\">\n		<div class=\"control-group\">\n			<div class=\"control-label\">I want to:</div>\n			<div class=\"controls\">\n				<label class=\"radio\">\n					<input type=\"radio\" name=\"bundle_radio\" value=\"new\" checked> Create a new bundle\n				</label>\n				<label class=\"radio\">\n					<input type=\"radio\" name=\"bundle_radio\" value=\"existing\"> Add to an existing bundle\n				</label>\n			</div>\n		</div>\n		<div id=\"bundle_new\" class=\"control-group\">\n			<div class=\"control-label\">\n				<label class=\"required\">Bundle Name</label>\n			</div>\n			<div class=\"controls\">\n				<input type=\"text\" name=\"title\">\n			</div>\n			<br/>\n			<div class=\"control-label\">\n				<label class=\"required\">Bundle Description</label>\n			</div>\n			<div class=\"controls\">\n				<textarea rows=\"9\" name=\"description\"></textarea>\n			</div>\n		</div>\n		<div id=\"bundle_existing\" class=\"dn control-group\">\n			<div class=\"control-label\">\n				<label class=\"required\">Select a Bundle Title:</label>\n			</div>\n			<div class=\"input controls\">\n				<select data-placeholder=\"Choose an existing bundle...\" name=\"id\" id=\"bundle_select\"></select>\n			</div>\n		</div>\n		<p/>\n		<div class=\"wm-action-container\">\n			<button data-modal-close class=\"button\">Cancel</button>\n			<button type=\"submit\" class=\"button\" id=\"submit_create_bundle\">Create New Bundle</button>\n		</div>\n	</form>\n</div>";
},"useData":true});

this["wm"]["templates"]["dashboard/create_bundle_errors"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<li><strong>"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0['0'] : depth0)) != null ? stack1.title : stack1), depth0))
    + "</strong> is in "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0['0'] : depth0)) != null ? stack1.status : stack1), depth0))
    + " status, not Draft status.</li>\n";
},"3":function(depth0,helpers,partials,data) {
    return "		<li><strong>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.title : depth0), depth0))
    + "</strong> is already in a bundle.</li>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<p>The following errors are preventing bundle creation:</p>\n<br/>\n<ul>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.nonDraft : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.inBundle : depth0),{"name":"each","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</ul>\n<br/>\n<p>Close this modal, reselect assignments in draft status and not already in a bundle to either create a new bundle or add assignments to an existing bundle.</p>";
},"useData":true});

this["wm"]["templates"]["dashboard/follower"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"existing_follower\">"
    + alias2(alias1((depth0 != null ? depth0.fullName : depth0), depth0))
    + " <small><a href=\"/assignments/remove_follower/"
    + alias2(alias1((depth0 != null ? depth0.workNumber : depth0), depth0))
    + "/"
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" class=\"remove_follower\">(remove)</a></small></div>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/no_results"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div>\n	<br/>\n	<div class=\"alert alert-block tac\">\n		You currently have no assignments in this status.<br/>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/pin-info"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<table style=\"margin-bottom: -2px\">\n	<tr>\n		<td><a href="
    + alias3(((helper = (helper = helpers.detail_url || (depth0 != null ? depth0.detail_url : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"detail_url","hash":{},"data":data}) : helper)))
    + ">"
    + alias3(((helper = (helper = helpers.title || (depth0 != null ? depth0.title : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"title","hash":{},"data":data}) : helper)))
    + "</a></td>\n		<td style=\"font-color:red\">"
    + alias3(((helper = (helper = helpers.price || (depth0 != null ? depth0.price : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"price","hash":{},"data":data}) : helper)))
    + "</td>\n	</tr>\n	<tr>\n		<td>"
    + alias3(((helper = (helper = helpers.scheduledDate || (depth0 != null ? depth0.scheduledDate : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"scheduledDate","hash":{},"data":data}) : helper)))
    + "</td>\n		<td>"
    + alias3(((helper = (helper = helpers.status || (depth0 != null ? depth0.status : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"status","hash":{},"data":data}) : helper)))
    + "</td>\n	</tr>\n</table>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/qq_uploader"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"qq-uploader\">\n	<div class=\"qq-upload-drop-area\"><span>Drop attachment here to upload</span></div>\n	<a href=\"javascript:void(0);\" class=\"qq-upload-button button\">Choose File</a>\n	<ul class=\"qq-upload-list\"></ul>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["dashboard/single_note_add"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div>\n	<form action=\"/assignments/add_note\" method=\"post\" id=\"single-note-add\" class=\"form-stacked\">\n		<input type=\"hidden\" name=\"assignment_id\" id=\"assignment_id\"/>\n		<div class=\"clearfix\">\n			<div class=\"input\">\n				<textarea name=\"content\" id=\"note_content\" rows=\"5\" class=\"span8\"></textarea>\n			</div>\n		</div>\n		<div class=\"clearfix\">\n			<div class=\"input\">\n				<ul class=\"inputs-list\">\n					<li>\n						<label>\n							<input type=\"radio\" name=\"is_private\" value=\"0\" checked=\"true\"/>\n							Everyone on assignment can see\n						</label>\n					</li>\n					<li>\n						<label>\n							<input type=\"radio\" name=\"is_private\" value=\"1\"/>\n							Private: Only my company can see\n						</label>\n					</li>\n				</ul>\n			</div>\n		</div>\n		<div class=\"wm-action-container\">\n			<button data-modal-close id=\"add-note-close\" class=\"button\">Close</button>\n			<button class=\"button\" id=\"submit_add_note\">Save</button>\n		</div>\n	</form>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["feed/empty-results"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<ul class=\"unstyled\">\n	<li>Sorry! Your search didn't match any assignments.</li>\n</ul>\n";
},"useData":true});

this["wm"]["templates"]["feed/results"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing, alias2=this.escapeExpression, alias3=this.lambda;

  return "	<li class=\"media\">\n		<hr />\n		<div class=\"date-widget pull-left\">\n			<div>\n				"
    + alias2((helpers.dateFormat || (depth0 && depth0.dateFormat) || alias1).call(depth0,(depth0 != null ? depth0.scheduleFromDate : depth0),"d",{"name":"dateFormat","hash":{},"data":data}))
    + "\n				<span>\n					"
    + alias2((helpers.dateFormat || (depth0 && depth0.dateFormat) || alias1).call(depth0,(depth0 != null ? depth0.scheduleFromDate : depth0),"mmm",{"name":"dateFormat","hash":{},"data":data}))
    + "\n				</span>\n			</div>\n		</div>\n\n		<a class=\"button pull-right\" href=\"/work/"
    + alias2(alias3((depth0 != null ? depth0.workNumber : depth0), depth0))
    + "\">Apply</a>\n			<span class=\"pull-right\">\n				<a id=\"linkedin\" href=\"#\"\n				   data-socialize=\"linkedIn\"\n				   data-title=\""
    + alias2(alias3((depth0 != null ? depth0.publicTitle : depth0), depth0))
    + "\"\n				   data-url=\"http://www.workmarket.com/work/"
    + alias2(alias3((depth0 != null ? depth0.workNumber : depth0), depth0))
    + "\"\n				   data-summary=\""
    + alias2(alias3((depth0 != null ? depth0.description : depth0), depth0))
    + "\">\n					<i class=\"icon-linkedin-sign icon-large muted\"></i>\n				</a>\n				<a id=\"facebook\" href=\"#\"\n				   data-socialize=\"facebook\"\n				   data-title=\""
    + alias2(alias3((depth0 != null ? depth0.publicTitle : depth0), depth0))
    + "\"\n				   data-url=\"http://www.workmarket.com/work/"
    + alias2(alias3((depth0 != null ? depth0.workNumber : depth0), depth0))
    + "\"\n				   data-summary=\""
    + alias2(alias3((depth0 != null ? depth0.description : depth0), depth0))
    + "\">\n					<i class=\"icon-facebook-sign icon-large muted\"></i>\n				</a>\n				<a id=\"twitter\" href=\"#\"\n				   data-socialize=\"twitter\"\n				   data-text=\"Find work @workmarket "
    + alias2(alias3((depth0 != null ? depth0.publicTitle : depth0), depth0))
    + "\"\n				   data-url=\"http://www.workmarket.com/work/"
    + alias2(alias3((depth0 != null ? depth0.workNumber : depth0), depth0))
    + "\">\n					<i class=\"icon-twitter-sign icon-large muted\"></i>\n				</a>\n				<a id=\"mailto\" href=\"mailto:?subject=Check out this job on Work Market&body=I thought you'd be interested in this job I found on Work Market.%0D%0D"
    + alias2(alias3((depth0 != null ? depth0.publicTitle : depth0), depth0))
    + "%0D%0Dhttp://www.workmarket.com/work/"
    + alias2(alias3((depth0 != null ? depth0.workNumber : depth0), depth0))
    + "\">\n					<i class=\"icon-envelope-alt icon-large muted\"></i>\n				</a>\n			</span>\n\n		<div class=\"media-body\">\n			<h4 class=\"media-heading\"><a href=\"/work/"
    + alias2(alias3((depth0 != null ? depth0.workNumber : depth0), depth0))
    + "\">"
    + alias2(alias3((depth0 != null ? depth0.publicTitle : depth0), depth0))
    + "</a></h4>\n			<ul class=\"inline\">\n				<li>\n					<small>\n						"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.city : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n						"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.city : depth0),{"name":"unless","hash":{},"fn":this.program(4, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n					</small>\n				</li>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.options : depth0)) != null ? stack1.hasDispatchEnabled : stack1),{"name":"if","hash":{},"fn":this.program(6, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.options : depth0)) != null ? stack1.hasDispatchEnabled : stack1),{"name":"unless","hash":{},"fn":this.program(12, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n				<li><small>Posted "
    + alias2((helpers.ago || (depth0 && depth0.ago) || alias1).call(depth0,(depth0 != null ? depth0.createdDate : depth0),{"name":"ago","hash":{},"data":data}))
    + "</small></li>\n			</ul>\n		</div>\n	</li>\n";
},"2":function(depth0,helpers,partials,data) {
    var alias1=helpers.helperMissing, alias2=this.escapeExpression;

  return alias2((helpers.trim || (depth0 && depth0.trim) || alias1).call(depth0,(depth0 != null ? depth0.city : depth0),{"name":"trim","hash":{},"data":data}))
    + ", "
    + alias2((helpers.trim || (depth0 && depth0.trim) || alias1).call(depth0,(depth0 != null ? depth0.state : depth0),{"name":"trim","hash":{},"data":data}));
},"4":function(depth0,helpers,partials,data) {
    return "Virtual Location";
},"6":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.options : depth0)) != null ? stack1.hasAnyRoleAdminManagerDispatcher : stack1),{"name":"unless","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.options : depth0)) != null ? stack1.hasAnyRoleAdminManagerDispatcher : stack1),{"name":"if","hash":{},"fn":this.program(10, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"7":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.options : depth0)) != null ? stack1.companyHidesPricing : stack1),{"name":"unless","hash":{},"fn":this.program(8, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"8":function(depth0,helpers,partials,data) {
    return "							<li><small>&#36;"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.spendLimit : depth0), depth0))
    + "</small></li>\n";
},"10":function(depth0,helpers,partials,data) {
    return "						<li><small>&#36;"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.spendLimit : depth0), depth0))
    + "</small></li>\n";
},"12":function(depth0,helpers,partials,data) {
    return "					<li><small>&#36;"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.spendLimit : depth0), depth0))
    + "</small></li>\n";
},"14":function(depth0,helpers,partials,data) {
    return "		<a id=\"prev-page\" class = \"small\" href=\"\">&laquo; Prev Page </a>\n";
},"16":function(depth0,helpers,partials,data) {
    return "		<a id=\"next-page\" class = \"small\" href=\"\"> Next Page &raquo;</a>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.escapeExpression;

  return "<h4 style=\"color:black;\"><span class=\"work_\">"
    + alias1(this.lambda(((stack1 = (depth0 != null ? depth0.items : depth0)) != null ? stack1.totalCount : stack1), depth0))
    + "</span> "
    + alias1((helpers.pluralize || (depth0 && depth0.pluralize) || helpers.helperMissing).call(depth0,"assignment",((stack1 = (depth0 != null ? depth0.items : depth0)) != null ? stack1.totalCount : stack1),{"name":"pluralize","hash":{},"data":data}))
    + " available near you.</h4>\n<ul class=\"media-list\">\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.items : depth0)) != null ? stack1.results : stack1),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</ul>\n\n<div class=\"tac\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isNotFirstPage : depth0),{"name":"if","hash":{},"fn":this.program(14, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isMorePages : depth0),{"name":"if","hash":{},"fn":this.program(16, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n\n";
},"useData":true});

this["wm"]["templates"]["forums/deleted-post"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<strong>[Deleted]</strong>\n";
},"useData":true});

this["wm"]["templates"]["forums/edit-comment"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "<textarea name=\"comment\" class=\"input-block-level new-comment-"
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + " new-comment\" rows=\"5\" maxLength=\""
    + alias2(alias1((depth0 != null ? depth0.maxLength : depth0), depth0))
    + "\"> "
    + alias2(alias1((depth0 != null ? depth0.oldComment : depth0), depth0))
    + " </textarea>\n";
},"useData":true});

this["wm"]["templates"]["forums/edit-post"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "<textarea name=\"comment\" class=\"input-block-level new-post\" rows=\"5\" maxLength=\""
    + alias2(alias1((depth0 != null ? depth0.maxLength : depth0), depth0))
    + "\"> "
    + alias2(alias1((depth0 != null ? depth0.oldPost : depth0), depth0))
    + " </textarea>\n";
},"useData":true});

this["wm"]["templates"]["forums/edit-title"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<input id=\"title\" name=\"title\" type=\"text\" value=\""
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.oldTitle : depth0), depth0))
    + "\" maxLength=\"70\">\n";
},"useData":true});

this["wm"]["templates"]["forums/followedposts/avatar_icon"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper;

  return "<img src=\""
    + this.escapeExpression(((helper = (helper = helpers.avatarURI || (depth0 != null ? depth0.avatarURI : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"avatarURI","hash":{},"data":data}) : helper)))
    + "\">\n";
},"useData":true});

this["wm"]["templates"]["forums/followedposts/followed_category"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "in <a href=\"/forums/"
    + alias3(((helper = (helper = helpers.categoryId || (depth0 != null ? depth0.categoryId : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"categoryId","hash":{},"data":data}) : helper)))
    + "\">"
    + alias3(((helper = (helper = helpers.categoryName || (depth0 != null ? depth0.categoryName : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"categoryName","hash":{},"data":data}) : helper)))
    + "</a>\n";
},"useData":true});

this["wm"]["templates"]["forums/followedposts/followed_last_post"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper;

  return "<p class=\"stats\">Last post: "
    + this.escapeExpression(((helper = (helper = helpers.lastCommentDate || (depth0 != null ? depth0.lastCommentDate : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"lastCommentDate","hash":{},"data":data}) : helper)))
    + "</p>\n";
},"useData":true});

this["wm"]["templates"]["forums/followedposts/no_avatar_icon"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<i class=\"wm-icon-user\"></i>\n";
},"useData":true});

this["wm"]["templates"]["forums/followedposts/title"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<a class=\"title\" href=\"/forums/post/"
    + alias3(((helper = (helper = helpers.postId || (depth0 != null ? depth0.postId : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"postId","hash":{},"data":data}) : helper)))
    + "\">"
    + alias3(((helper = (helper = helpers.title || (depth0 != null ? depth0.title : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"title","hash":{},"data":data}) : helper)))
    + "</a>\n<p class=\"stats\"><span class=\"stat-number\">"
    + alias3(((helper = (helper = helpers.commentCount || (depth0 != null ? depth0.commentCount : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"commentCount","hash":{},"data":data}) : helper)))
    + "</span><span class=\"stat-label\"> comments</span></p>\n";
},"useData":true});

this["wm"]["templates"]["forums/followedposts/unfollow_post_button"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper;

  return "<a href=\"/forums/follow/"
    + this.escapeExpression(((helper = (helper = helpers.postId || (depth0 != null ? depth0.postId : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"postId","hash":{},"data":data}) : helper)))
    + "\" class=\"follow-post-btn tooltipped tooltipped-n\" aria-label=\"Unfollow this post\"><i class=\"wm-icon-follow\"></i></a>\n";
},"useData":true});

this["wm"]["templates"]["forums/post-reply"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "			<a class=\"admin-post-delete-btn\" data-post-id=\""
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\">Delete Post</a>\n			<a class=\"admin-ban-user-btn\" data-user-id=\""
    + alias2(alias1((depth0 != null ? depth0.creatorId : depth0), depth0))
    + "\">Ban User</a>\n";
},"3":function(depth0,helpers,partials,data) {
    return "			<a class=\"user-post-delete-btn\" data-post-id=\""
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.id : depth0), depth0))
    + "\">Delete Post</a>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div id=\"reply"
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" class=\"forums-reply-reply-box\">\n	<div class=\"forums-user-icon\">"
    + ((stack1 = alias1((depth0 != null ? depth0.avatarURI : depth0), depth0)) != null ? stack1 : "")
    + "</div>\n	<p class=\"creator-name\">"
    + alias2(alias1((depth0 != null ? depth0.creator : depth0), depth0))
    + "</p>\n	<p class=\"forums-post-date\"> "
    + alias2(alias1((depth0 != null ? depth0.createdOn : depth0), depth0))
    + " </p>\n	<div class=\"forums-post-controls\">\n		<p id=\"comment"
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" class=\"forums-post-comment\">"
    + alias2(alias1((depth0 != null ? depth0.comment : depth0), depth0))
    + "</p>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isInternal : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInternal : depth0),{"name":"unless","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		<a class=\"user-comment-edit-btn\" data-post-id=\""
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\">Edit Post</a>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["forums/post"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "			<a class=\"admin-post-delete-btn\" data-post-id=\""
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\">Delete Post</a>\n			<a class=\"admin-ban-user-btn\" data-user-id=\""
    + alias2(alias1((depth0 != null ? depth0.creatorId : depth0), depth0))
    + "\">Ban User</a>\n";
},"3":function(depth0,helpers,partials,data) {
    return "			<a class=\"user-post-delete-btn\" data-post-id=\""
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.id : depth0), depth0))
    + "\">Delete Post</a>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div id=\"reply"
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" class=\"forums-reply-box\">\n	<div class=\"forums-user-icon\">"
    + ((stack1 = alias1((depth0 != null ? depth0.avatarURI : depth0), depth0)) != null ? stack1 : "")
    + "</div>\n	<p class=\"creator-name\">"
    + alias2(alias1((depth0 != null ? depth0.creator : depth0), depth0))
    + "</p>\n	<p class=\"forums-post-date\"> "
    + alias2(alias1((depth0 != null ? depth0.createdOn : depth0), depth0))
    + " </p>\n	<p id=\"comment"
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" class=\"forums-post-comment\">"
    + alias2(alias1((depth0 != null ? depth0.comment : depth0), depth0))
    + "</p>\n	<div class=\"forums-post-controls\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isInternal : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInternal : depth0),{"name":"unless","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		<a class=\"user-comment-edit-btn\" data-post-id=\""
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\">Edit Post</a>\n		<button class=\"user-post-reply-btn button\" id=\"commentReply"
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\">Reply</button>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["forums/reply-post"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "	<div class=\"forums-add-reply-box\" id=\"commentReplyBox"
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\">\n		<h4 class=\"forums-leave-comment\">Leave a Reply<span id=\"commentClose"
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" class=\"user-post-reply-close-btn\">\n			<svg version=\"1.0\"  xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 16 16\" xml:space=\"preserve\">\n				<g>\n					<g>\n						<g>\n							<path fill=\"#4C5355\" d=\"M10.288,8l3.522-3.522c0.253-0.253,0.253-0.662,0-0.915L12.437,2.19c-0.253-0.253-0.662-0.253-0.915,0\n								L8,5.712L4.478,2.19c-0.253-0.253-0.662-0.253-0.915,0L2.19,3.562c-0.253,0.253-0.253,0.663,0,0.915L5.712,8L2.19,11.522\n								c-0.253,0.253-0.253,0.662,0,0.915l1.373,1.373c0.253,0.253,0.662,0.253,0.915,0L8,10.288l3.522,3.522\n								c0.253,0.253,0.662,0.253,0.915,0l1.373-1.373c0.253-0.253,0.253-0.663,0-0.915L10.288,8z\"/>\n						</g>\n					</g>\n				</g>\n			</svg>\n\n			</span>\n		</h4>\n		<form id=\"forum_post_reply_"
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" name=\"form\" class=\"forum_post_reply_reply_form\" action=\"/forums/post/reply\" method=\"POST\">\n\n			<textarea id=\"comment\" name=\"comment\" class=\"input-block-level\" rows=\"5\"></textarea>\n			<input id=\"parentId\" name=\"parentId\" value=\""
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" type=\"hidden\">\n			<input id=\"rootId\" name=\"rootId\" value=\""
    + alias2(alias1((depth0 != null ? depth0.rootId : depth0), depth0))
    + "\" type=\"hidden\">\n			<input id=\"categoryId\" name=\"categoryId\" value=\""
    + alias2(alias1((depth0 != null ? depth0.categoryId : depth0), depth0))
    + "\" type=\"hidden\">\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInternal : depth0),{"name":"unless","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			<button type=\"submit\" class=\"button\">Add Reply</button>\n		</form>\n	</div>\n";
},"2":function(depth0,helpers,partials,data) {
    return "				<span class=\"post-char-countdown\" data-post-id=\""
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.id : depth0), depth0))
    + "\" />\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isUserBanned : depth0),{"name":"unless","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"useData":true});

this["wm"]["templates"]["forums/search/category"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "<a href=\"/forums/"
    + alias2(alias1((depth0 != null ? depth0.categoryId : depth0), depth0))
    + "\">"
    + alias2(alias1((depth0 != null ? depth0.categoryName : depth0), depth0))
    + "</a>\n";
},"useData":true});

this["wm"]["templates"]["forums/search/new-tag"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<a href=\"javascript:void(0);\" class=\"forums-tag\">"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.tag : depth0), depth0))
    + "</a>\n";
},"useData":true});

this["wm"]["templates"]["forums/search/title-reply"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "<a href=\"/forums/post/"
    + alias2(alias1((depth0 != null ? depth0.rootId : depth0), depth0))
    + "&#35;commentBox"
    + alias2(alias1((depth0 != null ? depth0.postId : depth0), depth0))
    + "\">Reply to: "
    + alias2(alias1((depth0 != null ? depth0.title : depth0), depth0))
    + "</a>\n";
},"useData":true});

this["wm"]["templates"]["forums/search/title"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "<a href=\"/forums/post/"
    + alias2(alias1((depth0 != null ? depth0.postId : depth0), depth0))
    + "\">"
    + alias2(alias1((depth0 != null ? depth0.title : depth0), depth0))
    + "</a>\n";
},"useData":true});

this["wm"]["templates"]["groups/missing_requirements"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<li class=\"outer\">\n			<ul>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.countries : stack1),{"name":"each","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</ul>\n		</li>\n";
},"2":function(depth0,helpers,partials,data) {
    return "					<li class=\"inner\"><i class=\"wm-icon-x\"></i> Location is: <strong>"
    + this.escapeExpression(this.lambda(depth0, depth0))
    + "</strong></li>\n";
},"4":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<li class=\"outer\">\n			<ul>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.proximities : stack1),{"name":"each","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</ul>\n		</li>\n";
},"5":function(depth0,helpers,partials,data) {
    var stack1;

  return "					<li class=\"inner\">Willing to travel to "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.radius : depth0),{"name":"if","hash":{},"fn":this.program(6, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "<strong>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.address : depth0), depth0))
    + "</strong></li>\n";
},"6":function(depth0,helpers,partials,data) {
    return " and within <strong>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.radius : depth0), depth0))
    + " miles</strong> of ";
},"8":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<li class=\"outer\">\n			<ul>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.availability : stack1),{"name":"each","hash":{},"fn":this.program(9, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</ul>\n		</li>\n";
},"9":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "					<li class=\"inner\">Available <strong>"
    + alias2(alias1((depth0 != null ? depth0.day : depth0), depth0))
    + " "
    + alias2(alias1((depth0 != null ? depth0.timeframe : depth0), depth0))
    + "</strong></li>\n";
},"11":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<li class=\"outer\">\n			<ul>\n				<li class=\"inner\">Minimum satisfaction rate of <strong>"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.rating : stack1), depth0))
    + "%</strong></li>\n			</ul>\n		</li>\n";
},"13":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<li class=\"outer\">\n			<ul>\n				<li class=\"inner\">Hourly rate between "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.rate : depth0)) != null ? stack1.min : stack1), depth0))
    + " and "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.rate : depth0)) != null ? stack1.max : stack1), depth0))
    + "</li>\n			</ul>\n		</li>\n";
},"15":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<li class=\"outer\">\n			<ul>\n				<li class=\"inner\">"
    + this.escapeExpression(this.lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.lane : stack1)) != null ? stack1.displayText : stack1), depth0))
    + "</li>\n			</ul>\n		</li>\n";
},"17":function(depth0,helpers,partials,data) {
    return "		<li class=\"outer\">\n			<ul>\n				<li class=\"inner\">Video: At least one video on your profile is required</li>\n			</ul>\n		</li>\n";
},"19":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<li class=\"outer\">\n			<ul>\n				<li class=\"inner\">Company is <strong>"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.companyType : stack1), depth0))
    + "</strong></li>\n			</ul>\n		</li>\n";
},"21":function(depth0,helpers,partials,data) {
    return "		<li class=\"outer\">\n			<ul>\n				<li class=\"inner\"><i class=\"wm-icon-x\"></i> Passed a background check</li>\n			</ul>\n		</li>\n";
},"23":function(depth0,helpers,partials,data) {
    return "		<li class=\"outer\">\n			<ul>\n				<li class=\"inner\">Passed a drug test</li>\n			</ul>\n		</li>\n";
},"25":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<li class=\"outer\">\n			<ul>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.industries : stack1),{"name":"each","hash":{},"fn":this.program(26, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</ul>\n		</li>\n";
},"26":function(depth0,helpers,partials,data) {
    return "					<li class=\"inner\">Industry is: <strong>"
    + this.escapeExpression(this.lambda(depth0, depth0))
    + "</strong></li>\n";
},"28":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<li class=\"outer\">\n			<ul>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.licenses : stack1),{"name":"each","hash":{},"fn":this.program(29, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</ul>\n		</li>\n";
},"29":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "					<li class=\"inner\">Licensed: <strong>"
    + alias2(alias1((depth0 != null ? depth0.name : depth0), depth0))
    + " ("
    + alias2(alias1((depth0 != null ? depth0.state : depth0), depth0))
    + ")</strong></li>\n";
},"31":function(depth0,helpers,partials,data) {
    var stack1;

  return "	<li class=\"outer\">\n		<ul>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.insurances : stack1),{"name":"each","hash":{},"fn":this.program(32, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</ul>\n	</li>\n";
},"32":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<li class=\"inner\">Insurance: <strong>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.name : depth0), depth0))
    + "</strong>"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isMinCoverageRequired : depth0),{"name":"if","hash":{},"fn":this.program(33, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</li>\n";
},"33":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "Minimum Coverage: "
    + alias2(alias1((depth0 != null ? depth0.requiredCoverage : depth0), depth0))
    + " Your Coverage: "
    + alias2(alias1((depth0 != null ? depth0.currentCoverage : depth0), depth0));
},"35":function(depth0,helpers,partials,data) {
    var stack1;

  return "	<li class=\"outer\">\n		<ul>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.certifications : stack1),{"name":"each","hash":{},"fn":this.program(36, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</ul>\n	</li>\n";
},"36":function(depth0,helpers,partials,data) {
    return "				<li class=\"inner\">Certified: "
    + this.escapeExpression(this.lambda(depth0, depth0))
    + "</li>\n";
},"38":function(depth0,helpers,partials,data) {
    var stack1;

  return "	<li class=\"outer\">\n		<ul>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.documents : stack1),{"name":"each","hash":{},"fn":this.program(39, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</ul>\n	</li>\n";
},"39":function(depth0,helpers,partials,data) {
    return "				<li class=\"inner\">Document: <strong>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.name : depth0), depth0))
    + "</strong></li>\n";
},"41":function(depth0,helpers,partials,data) {
    var stack1;

  return "	<li class=\"outer\">\n		<ul>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.assessments : stack1),{"name":"each","hash":{},"fn":this.program(42, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</ul>\n	</li>\n";
},"42":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "				<li class=\"inner\">Passed: <strong>"
    + alias2(alias1((depth0 != null ? depth0.name : depth0), depth0))
    + "</strong> ("
    + alias2(alias1((depth0 != null ? depth0.status : depth0), depth0))
    + ")</li>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<ul class=\"categories\">\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.countries : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.proximities : stack1),{"name":"if","hash":{},"fn":this.program(4, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.availability : stack1),{"name":"if","hash":{},"fn":this.program(8, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.rating : stack1),{"name":"if","hash":{},"fn":this.program(11, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.rate : stack1),{"name":"if","hash":{},"fn":this.program(13, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.lane : stack1),{"name":"if","hash":{},"fn":this.program(15, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.video : stack1),{"name":"if","hash":{},"fn":this.program(17, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.companyType : stack1),{"name":"if","hash":{},"fn":this.program(19, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.backgroundCheck : stack1),{"name":"if","hash":{},"fn":this.program(21, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.drugTest : stack1),{"name":"if","hash":{},"fn":this.program(23, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.industries : stack1),{"name":"if","hash":{},"fn":this.program(25, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.licenses : stack1),{"name":"if","hash":{},"fn":this.program(28, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.insurances : stack1),{"name":"if","hash":{},"fn":this.program(31, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.certifications : stack1),{"name":"if","hash":{},"fn":this.program(35, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.documents : stack1),{"name":"if","hash":{},"fn":this.program(38, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.assessments : stack1),{"name":"if","hash":{},"fn":this.program(41, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</ul>\n";
},"useData":true});

this["wm"]["templates"]["modals/confirmAction"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div id=\"confirmation_dialog\">\n	<p>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.message : depth0), depth0))
    + "</p>\n	<div class=\"wm-action-container\">\n		<button data-modal-close class=\"cta-confirm-no button\">No</button>\n		<button class=\"cta-confirm-yes button\">Yes</button>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["nav/notifications"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return "	<li class=\"dropdown-menu--item\">"
    + ((stack1 = this.lambda((depth0 != null ? depth0.display_message : depth0), depth0)) != null ? stack1 : "")
    + "\n		<small class=\"meta\"><i class=\"icon-time muted\"></i> "
    + this.escapeExpression((helpers.timeAgo || (depth0 && depth0.timeAgo) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.created_on : depth0),{"name":"timeAgo","hash":{},"data":data}))
    + "<i class=\"icon-circle "
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.viewed_at : depth0),{"name":"unless","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\" id=\"notification_status\"></i></small>\n	</li>\n";
},"2":function(depth0,helpers,partials,data) {
    return " power-on";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<li class=\"dropdown-menu--header\">\n	<div class=\"row-fluid\">\n		<span class=\"muted span8\">Notifications</span>\n		<a id=\"see-all\" class=\"span8 text-right\" href=\"/mysettings/notifications\">Settings</a>\n	</div>\n</li>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.notifications : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "<li class=\"dropdown-menu--footer\">\n	<a id=\"see-all\" class=\"pull-left\" href=\"/notifications/active\">See All</a>\n</li>\n";
},"useData":true});

this["wm"]["templates"]["onboarding/get-started"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "		<li>\n			Complete your banking information so you can withdraw your funds\n		</li>\n		<li>\n			Complete your tax information so you can get get paid for assignments\n		</li>\n		<li>\n			Add additional skills to your profile to become eligible for more assignments\n		</li>\n";
},"3":function(depth0,helpers,partials,data) {
    return "		<li>\n			<a href=\"#\" data-url=\"/funds/accounts\" data-slide=\"finish\" data-track=\"Done/Finish\">Complete your banking information</a> so you can withdraw your funds\n		</li>\n		<li>\n			<a href=\"#\" data-url=\"/account/tax\" data-slide=\"finish\" data-track=\"Done/Finish\">Complete your tax information</a> so you can get get paid for assignments\n		</li>\n		<li>\n			<a href=\"#\" data-url=\"/profile-edit/skills\" data-slide=\"finish\" data-track=\"Done/Finish\">Add additional skills to your profile</a> to become eligible for more assignments\n		</li>\n";
},"5":function(depth0,helpers,partials,data) {
    return "		<button class=\"button -continue\" data-url=\"/mobile\" data-slide=\"finish\" data-track=\"Done/Finish\">Get Started</button>\n";
},"7":function(depth0,helpers,partials,data) {
    return "		<button class=\"button -continue\" data-url=\"/profile-edit\" data-slide=\"finish\" data-track=\"Done/Finish\">Get Started</button>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<h2>Great! You are now ready to search and apply for assignments.</h2>\n\n<p>A few tips before you get started:</p>\n<ul>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isMobile : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isMobile : depth0),{"name":"unless","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</ul>\n\n<div class=\"wm-modal--footer\">\n	<button class=\"button -back\" data-slide=\"prev\" data-track=\"Done/Back\">Back</button>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isMobile : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isMobile : depth0),{"name":"unless","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.spinner,depth0,{"name":"spinner","data":data,"indent":"\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "</div>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["onboarding/industries"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return "	<li class=\"industry\">\n"
    + ((stack1 = this.invokePartial(partials.checkbox,depth0,{"name":"checkbox","hash":{"text":((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.name : stack1),"isChecked":((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.checked : stack1),"value":((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.id : stack1),"name":"industry"},"data":data,"indent":"\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.name : stack1),"General",{"name":"eq","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</li>\n";
},"2":function(depth0,helpers,partials,data) {
    return "			<small>- Some of the most interesting work comes from this category</small>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<h2>Select the types of work you're interested in:</h2>\n<ul class=\"industries\" name=\"industries\" data-float-label data-onboarding>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.industries : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</ul>\n<div class=\"wm-modal--footer\">\n	<button class=\"button -back\" data-slide=\"prev\" data-track=\"Industries/Back\">Back</button>\n	<button class=\"button -continue tooltipped-w\" aria-label=\"\" disabled=\"disabled\" data-slide=\"next\" data-track=\"Industries/Continue\">Continue</button>\n"
    + ((stack1 = this.invokePartial(partials.spinner,depth0,{"name":"spinner","data":data,"indent":"\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "</div>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["onboarding/legal"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "				<option value=\""
    + alias2(alias1(depth0, depth0))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.companyYearFounded : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">"
    + alias2(alias1(depth0, depth0))
    + "</option>\n";
},"2":function(depth0,helpers,partials,data) {
    return "selected";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"legal-status\">\n	<h2>I am:</h2>\n"
    + ((stack1 = this.invokePartial(partials.radio,depth0,{"name":"radio","hash":{"text":"A sole proprietor","attributes":((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.individual : stack1),"value":"true","name":"individual","id":"sole-proprietor"},"data":data,"indent":"\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.radio,depth0,{"name":"radio","hash":{"text":"An incorporated business","attributes":((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.individual : stack1),"value":"false","name":"individual","id":"business"},"data":data,"indent":"\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "</div>\n\n<div class=\"company-details\">\n	<div class=\"company-logo\" data-onboarding></div>\n	<div class=\"company-information company-name\">\n		<label class=\"onboarding-label\" for=\"companyName\">Business Name <span>*</span></label>\n		<input type=\"text\" name=\"companyName\" placeholder=\"Business Name\" value=\""
    + alias2(alias1((depth0 != null ? depth0.companyName : depth0), depth0))
    + "\" data-onboarding />\n	</div>\n	<div class=\"work-experience\">\n		<label class=\"onboarding-label\" for=\"companyOverview\">Business Overview <span>*</span></label>\n		<textarea name=\"companyOverview\" data-onboarding placeholder=\"Provide an overview of your company\">"
    + alias2(alias1((depth0 != null ? depth0.companyOverview : depth0), depth0))
    + "</textarea>\n	</div>\n	<div class=\"company-information company-website\">\n		<label class=\"onboarding-label\" for=\"companyWebsite\">Website <span>*</span></label>\n		<input type=\"url\" name=\"companyWebsite\" placeholder=\"Website\" value=\""
    + alias2(alias1((depth0 != null ? depth0.companyWebsite : depth0), depth0))
    + "\" data-onboarding />\n	</div>\n	<div class=\"year-founded\">\n		<label class=\"onboarding-label\" for=\"companyOverview\">Year Founded <span>*</span></label>\n		<select class=\"wm-select\" name=\"companyYearFounded\" data-onboarding>\n			<option value=\"\" disabled selected>Year Founded</option>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.years : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</select>\n	</div>\n</div>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["onboarding/location"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"location-address\">\n	<label class=\"onboarding-label\" for=\"address\">Address <span>*</span></label>\n	<input type=\"text\" name=\"address\" value=\""
    + alias2(alias1((depth0 != null ? depth0.address : depth0), depth0))
    + "\" data-onboarding />\n</div>\n<div class=\"map-assignments\">\n	<div class=\"map\"></div>\n	<small class=\"caption\">There are <strong class=\"number-of-assignments\" name=\"numberOfNearbyAssignments\" data-onboarding>"
    + alias2(alias1((depth0 != null ? depth0.numberOfNearbyAssignments : depth0), depth0))
    + "</strong> assignments available near you.</small>\n</div>\n<div class=\"wm-modal--footer\">\n	<button class=\"button -back\" data-slide=\"prev\" data-track=\"Location/Back\">Back</button>\n	<button class=\"button -continue tooltipped-w\" aria-label=\"\" disabled=\"disabled\" data-slide=\"next\" data-track=\"Location/Continue\">Continue</button>\n"
    + ((stack1 = this.invokePartial(partials.spinner,depth0,{"name":"spinner","data":data,"indent":"\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "</div>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["onboarding/logo"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "-loaded";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"profile-picture "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isLoaded : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n	<img class=\"profile-picture--image\" src=\""
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.src : depth0), depth0))
    + "\" />\n	<i class=\"wm-icon-buildings\"></i>\n"
    + ((stack1 = this.invokePartial(partials['file-input'],depth0,{"name":"file-input","hash":{"text":"Add Logo","name":"logo","inline":"true"},"data":data,"indent":"\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "</div>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["onboarding/phones"] = Handlebars.template({"1":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return "	<li class=\"phone\">\n		<div class=\"phone--area\" data-float-label=\"Country\">\n			<select class=\"wm-select\" name=\"code\">\n				<option value=\"\">Country</option>\n"
    + ((stack1 = helpers.each.call(depth0,(depths[1] != null ? depths[1].countryCodes : depths[1]),{"name":"each","hash":{},"fn":this.program(2, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</select>\n		</div>\n		<div data-float-label=\"Number\">\n			<input class=\"phone--number\" name=\"number\" type=\"tel\" value=\""
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.number : stack1), depth0))
    + "\" data-mask/>\n		</div>\n	</li>\n";
},"2":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					<option value=\""
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" "
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,((stack1 = (depths[1] != null ? depths[1].attributes : depths[1])) != null ? stack1.code : stack1),(depth0 != null ? depth0.id : depth0),{"name":"eq","hash":{},"fn":this.program(3, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">"
    + alias2(alias1((depth0 != null ? depth0.name : depth0), depth0))
    + "</option>\n";
},"3":function(depth0,helpers,partials,data) {
    return "selected";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.phones : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"useData":true,"useDepths":true});

this["wm"]["templates"]["onboarding/profile-info"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"user-details\">\n	<div class=\"profile\"></div>\n	<div class=\"contact-information\">\n		<label class=\"onboarding-label\" for=\"firstName\">First Name <span>*</span></label>\n		<input type=\"text\" name=\"firstName\" placeholder=\"First Name\" value=\""
    + alias2(alias1((depth0 != null ? depth0.firstName : depth0), depth0))
    + "\" data-onboarding />\n	</div>\n	<div class=\"contact-information\">\n		<label class=\"onboarding-label\" for=\"lastName\">Last Name <span>*</span></label>\n		<input type=\"text\" name=\"lastName\" placeholder=\"Last Name\" value=\""
    + alias2(alias1((depth0 != null ? depth0.lastName : depth0), depth0))
    + "\" data-onboarding />\n	</div>\n	<div class=\"contact-email\">\n		<label class=\"onboarding-label\" for=\"email\">Email Address <span>*</span></label>\n		<input type=\"email\" name=\"email\" placeholder=\"Email Address\" value=\""
    + alias2(alias1((depth0 != null ? depth0.email : depth0), depth0))
    + "\" data-onboarding />\n	</div>\n	<label class=\"onboarding-label\" for=\"phones\">Phone Number <span>*</span></label>\n	<ul class=\"phones\" name=\"phones\"></ul>\n</div>\n\n<div id=\"legalView\"></div>\n<div class=\"wm-modal--footer\">\n	<button class=\"button -continue tooltipped-w\" aria-label=\"\" disabled=\"disabled\" data-slide=\"next\" data-track=\"Profile/Continue\">Continue</button>\n"
    + ((stack1 = this.invokePartial(partials.spinner,depth0,{"name":"spinner","data":data,"indent":"\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "</div>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["onboarding/profile-picture"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "-loaded";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"profile-picture "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isLoaded : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\" data-onboarding>\n	<div class=\"photo-crop\">\n		<img class=\"photo-crop--photo\" src=\""
    + alias2(alias1((depth0 != null ? depth0.src : depth0), depth0))
    + "\" />\n	</div>\n	<div class=\"profile-picture--image\" style=\"background-image: url("
    + alias2(alias1((depth0 != null ? depth0.src : depth0), depth0))
    + ");\"></div>\n	<i class=\"wm-icon-user\"></i>\n	<button class=\"profile-picture--save button -primary\">Save</button>\n"
    + ((stack1 = this.invokePartial(partials['file-input'],depth0,{"name":"file-input","hash":{"text":"Add photo"},"data":data,"indent":"\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "</div>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["onboarding/select-options"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "disabled";
},"3":function(depth0,helpers,partials,data) {
    return "selected";
},"5":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "	<option value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.value : stack1), depth0))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.checked : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.value : stack1), depth0))
    + "</option>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<option value=\"\" "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.options : depth0)) != null ? stack1.isOptional : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isSomeChecked : depth0),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.selectedOption : depth0), depth0))
    + "</option>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.options : depth0),{"name":"each","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"useData":true});

this["wm"]["templates"]["payments/custom-header"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<p>\n	<small>\n		<a href=\"/payments/ledger.csv\" id=\"export-filtered-outlet\">Export CSV</a> /\n		<a href=\"/reports/custom/manage#step1\">Build Custom Report</a>\n	</small>\n</p>\n";
},"useData":true});

this["wm"]["templates"]["payments/invoice-bundle-row-detail"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return "						"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.editable : stack1),{"name":"unless","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"2":function(depth0,helpers,partials,data) {
    return " class=\"tooltipped tooltipped-n\" aria-label=\"One or more of the invoices in this bundle have been locked, and invoices can no longer be removed from it or paid individually\"";
},"4":function(depth0,helpers,partials,data) {
    var stack1;

  return "						"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.editable : stack1),{"name":"unless","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"5":function(depth0,helpers,partials,data) {
    return "<i class=\"wm-icon-lock-circle\"></i>";
},"7":function(depth0,helpers,partials,data) {
    return " data-confirmation-message=\"Are you sure you want to print the invoice bundle? Once printed, the invoice will be locked and can only be unlocked by an authorized users.\" ";
},"9":function(depth0,helpers,partials,data) {
    return " data-confirmation-message=\"Are you sure you want to email the invoice bundle? Once emailed, the invoice will be locked and can only be unlocked by an authorized users.\" ";
},"11":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodePending : depth0),{"name":"if","hash":{},"fn":this.program(12, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"12":function(depth0,helpers,partials,data) {
    var stack1;

  return "								<li><a class=\"js-unlock-invoice\" data-invoice=\""
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\" href=\"javascript:void(0);\">Unlock</a></li>\n";
},"14":function(depth0,helpers,partials,data) {
    var stack1;

  return "							<li><a href=\"/payments/invoices/pay?ids[]="
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\" class=\"pay-invoice-outlet button\">Pay</a></li>\n";
},"16":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.pendingPaymentFulfillment : stack1),{"name":"unless","hash":{},"fn":this.program(17, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"17":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"unless","hash":{},"fn":this.program(18, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"18":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isReceivables : depth0),{"name":"unless","hash":{},"fn":this.program(19, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"19":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.mmwAutoPayEnabled : depth0),{"name":"if","hash":{},"fn":this.program(20, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"20":function(depth0,helpers,partials,data) {
    return "											<li><span class=\"label ml tooltipped tooltipped-n\" aria-label=\"This bundle will auto pay on its due date if funds are available on your account.\">Auto Pay</span></li>\n";
},"22":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceFulfillmentStatusIsPending : depth0),{"name":"if","hash":{},"fn":this.program(23, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"23":function(depth0,helpers,partials,data) {
    return "						<em>Processing</em>\n";
},"25":function(depth0,helpers,partials,data) {
    var stack1;

  return "					"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodePaid : depth0),{"name":"if","hash":{},"fn":this.program(26, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"26":function(depth0,helpers,partials,data) {
    return "Paid";
},"28":function(depth0,helpers,partials,data) {
    var stack1;

  return "					"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"if","hash":{},"fn":this.program(29, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"29":function(depth0,helpers,partials,data) {
    return "Void";
},"31":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"unless","hash":{},"fn":this.program(32, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"32":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodePaid : depth0),{"name":"unless","hash":{},"fn":this.program(33, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"33":function(depth0,helpers,partials,data) {
    var stack1;

  return "							<small>\n								Due: "
    + this.escapeExpression((helpers.formattedDate || (depth0 && depth0.formattedDate) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceDueDate : stack1),{"name":"formattedDate","hash":{},"data":data}))
    + "\n								"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoicePastDue : stack1),{"name":"if","hash":{},"fn":this.program(34, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoicePastDue : stack1),{"name":"unless","hash":{},"fn":this.program(36, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "							</small>\n";
},"34":function(depth0,helpers,partials,data) {
    return "<br/><span class=\"text-error\">Past Due</span>";
},"36":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceDueWithinWeek : stack1),{"name":"if","hash":{},"fn":this.program(37, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"37":function(depth0,helpers,partials,data) {
    return "										<br/><span class=\"text-error\">Coming Due</span>\n";
},"39":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<tr>"
    + ((stack1 = this.invokePartial(partials['single-row-invoice'],depth0,{"name":"single-row-invoice","data":data,"helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "</tr>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<td class=\"invoice-check check\">\n	<input type=\"checkbox\" name=\"ids[]\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\"/>\n</td>\n<td class=\"bundle-details\" colspan=\"3\">\n	<table>\n		<tbody>\n		<tr class=\"bundle-row\">\n			<td class=\"invoice-detail span6\">\n				<strong\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.isReceivables : stack1),{"name":"unless","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "					>\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isReceivables : depth0),{"name":"unless","hash":{},"fn":this.program(4, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "					"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceDescription : stack1), depth0))
    + "\n				</strong>\n				<br/>"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceNumber : stack1), depth0))
    + " &mdash; "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.numberOfInvoices : stack1), depth0))
    + " Invoices\n				<dl class=\"help-block\">\n					<ul class=\"invoice-actions\">\n						<li><a href=\"/payments/invoices/print/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.editable : stack1),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " class=\"print-invoice-outlet\" target=\"_blank\">Print</a></li>\n						<li><a href=\"/payments/invoices/email/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.editable : stack1),{"name":"if","hash":{},"fn":this.program(9, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " class=\"email-invoice-outlet\">Email</a></li>\n						<li><a href=\"/payments/invoices/export/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\">Export CSV</a></li>\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.editable : stack1),{"name":"unless","hash":{},"fn":this.program(11, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodePaid : depth0),{"name":"unless","hash":{},"fn":this.program(14, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceIsBundled : stack1),{"name":"unless","hash":{},"fn":this.program(16, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "					</ul>\n				</dl>\n			</td>\n			<td class=\"invoice-status span3\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodePaid : depth0),{"name":"if","hash":{},"fn":this.program(22, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceFulfillmentStatusIsPending : depth0),{"name":"unless","hash":{},"fn":this.program(25, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceFulfillmentStatusIsPending : depth0),{"name":"unless","hash":{},"fn":this.program(28, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceFulfillmentStatusIsPending : depth0),{"name":"unless","hash":{},"fn":this.program(31, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</td>\n			<td class=\"invoice-amount amount nowrap span2\">"
    + alias2((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceBalance : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "<br/></td>\n		</tr>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.bundledInvoices : stack1),{"name":"each","hash":{},"fn":this.program(39, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</tbody>\n	</table>\n</td>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["payments/invoice-row-detail"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceIsBundled : stack1),{"name":"unless","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"2":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"unless","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.pendingPaymentFulfillment : stack1),{"name":"unless","hash":{},"fn":this.program(4, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"4":function(depth0,helpers,partials,data) {
    var stack1;

  return "					<input type=\"checkbox\" name=\"ids[]\" value=\""
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\"/>\n";
},"6":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.editable : stack1),{"name":"unless","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"7":function(depth0,helpers,partials,data) {
    return "			<span class=\"tooltipped tooltipped-n\" aria-label=\"This invoice is locked\"><i class=\"wm-icon-lock-circle\"></i></span>\n";
},"9":function(depth0,helpers,partials,data) {
    var stack1;

  return " "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceSummaryNumber : stack1), depth0))
    + " &bull; ";
},"11":function(depth0,helpers,partials,data) {
    var stack1;

  return " &bull; "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.workNumber : stack1), depth0));
},"13":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<strong><a href=\"/payments/invoices/print_service_invoice/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\" class=\"print-invoice-outlet\" target=\"_blank\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceDescription : stack1), depth0))
    + "</a></strong><br/>\n";
},"15":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isInvoiceTypeAdHoc : depth0),{"name":"if","hash":{},"fn":this.program(16, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"16":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "			<strong>\n				<a href=\"/payments/invoices/print_service_invoice/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\" class=\"print-invoice-outlet\" target=\"_blank\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceDescription : stack1), depth0))
    + "</a>\n			</strong>\n";
},"18":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeAdHoc : depth0),{"name":"unless","hash":{},"fn":this.program(19, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"19":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "			<strong><a target=\"_blank\" href=\"/assignments/details/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.workNumber : stack1), depth0))
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.workTitle : stack1), depth0))
    + "</a></strong>\n";
},"21":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.workCountry : stack1),{"name":"if","hash":{},"fn":this.program(22, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"22":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<div class=\"tooltipped tooltipped-n\" aria-label=\"Work Location and Worker Details\">\n				"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.workCountry : stack1),{"name":"if","hash":{},"fn":this.program(23, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n				"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.workResourceName : stack1),{"name":"if","hash":{},"fn":this.program(25, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			</div>\n";
},"23":function(depth0,helpers,partials,data) {
    var stack1;

  return this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.formattedAddressShort : stack1), depth0));
},"25":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.workCountry : stack1),{"name":"if","hash":{},"fn":this.program(26, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.workResourceName : stack1), depth0));
},"26":function(depth0,helpers,partials,data) {
    return "&bull;";
},"28":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<div class=\"tooltipped tooltipped-n\" aria-label=\"Print Date\">Downloaded: "
    + this.escapeExpression((helpers.formattedDate || (depth0 && depth0.formattedDate) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.downloadedOn : stack1),{"name":"formattedDate","hash":{},"data":data}))
    + "</div>\n";
},"30":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<p><small><em>For questions about this invoice, please call "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.buyerFullName : stack1), depth0))
    + " "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.companyName : stack1),{"name":"if","hash":{},"fn":this.program(31, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.buyerPhone : stack1),{"name":"if","hash":{},"fn":this.program(33, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</em></small></p>\n";
},"31":function(depth0,helpers,partials,data) {
    var stack1;

  return " from "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.companyName : stack1), depth0));
},"33":function(depth0,helpers,partials,data) {
    var stack1;

  return " at "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.buyerPhone : stack1), depth0));
},"35":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing, alias2=this.escapeExpression;

  return "			<div class=\"row-fluid\">\n				<div class=\"span5\">Created</div>\n				<div class=\"span11\">"
    + alias2((helpers.formattedDate || (depth0 && depth0.formattedDate) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceCreatedDate : stack1),{"name":"formattedDate","hash":{},"data":data}))
    + "</div>\n			</div>\n			<dt>Voided</dt>\n			<dd>"
    + alias2((helpers.formattedDate || (depth0 && depth0.formattedDate) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceVoidDate : stack1),{"name":"formattedDate","hash":{},"data":data}))
    + " ("
    + alias2((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceBalance : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + ")</dd>\n";
},"37":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeSubscription : depth0),{"name":"unless","hash":{},"fn":this.program(38, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"38":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeAdHoc : depth0),{"name":"unless","hash":{},"fn":this.program(39, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"39":function(depth0,helpers,partials,data) {
    var stack1;

  return "						<div class=\"row-fluid\">\n							<div class=\"span5\">Approved</div>\n							<div class=\"span11\">"
    + this.escapeExpression((helpers.formattedDate || (depth0 && depth0.formattedDate) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.workCloseDate : stack1),{"name":"formattedDate","hash":{},"data":data}))
    + "</div>\n						</div>\n";
},"41":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "			<div class=\"row-fluid\">\n				<div class=\"span5\">\n					<div class=\"custom-field\">"
    + alias2(alias1((depth0 != null ? depth0.fieldName : depth0), depth0))
    + ":</div>\n				</div>\n				<div class=\"span11\">\n					<div class=\"custom-field\">"
    + alias2(alias1((depth0 != null ? depth0.fieldValue : depth0), depth0))
    + "</div>\n				</div>\n			</div>\n";
},"43":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<li><a href=\"/payments/invoices/print_service_invoice/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\" class=\"print-invoice-outlet\" target=\"_blank\">Print</a></li>\n";
},"45":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isInvoiceTypeAdHoc : depth0),{"name":"if","hash":{},"fn":this.program(46, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"46":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					<li><a href=\"/payments/invoices/print_service_invoice/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\" class=\"print-invoice-outlet\" target=\"_blank\">Print</a></li>\n					<li><a href=\"/payments/invoices/email/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\" class=\"email-invoice-outlet\">Email</a></li>\n";
},"48":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeAdHoc : depth0),{"name":"unless","hash":{},"fn":this.program(49, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"49":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					<li><a href=\"/payments/invoices/print/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\" class=\"print-invoice-outlet\" target=\"_blank\">Print</a></li>\n					<li><a href=\"/payments/invoices/email/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\" class=\"email-invoice-outlet\">Email</a></li>\n					<li><a href=\"/payments/invoices/export/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\">Export CSV</a></li>\n";
},"51":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"unless","hash":{},"fn":this.program(52, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"52":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.pendingPaymentFulfillment : depth0),{"name":"unless","hash":{},"fn":this.program(53, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"53":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isReceivables : depth0),{"name":"unless","hash":{},"fn":this.program(54, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"54":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.mmwAutoPayEnabled : depth0),{"name":"if","hash":{},"fn":this.program(55, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"55":function(depth0,helpers,partials,data) {
    return "								<li><span class=\"label success ml tooltipped tooltipped-n\" aria-label=\"This invoice will auto pay on its due date if funds are available on your account.\">Auto Pay</span></li>\n";
},"57":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.editable : stack1),{"name":"if","hash":{},"fn":this.program(58, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"58":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					<li><a class=\"js-remove-from-bundle tooltipped tooltipped-n\" href=\"javascript:void(0);\" data-invoice-id=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\" data-invoice-summary=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceSummaryId : stack1), depth0))
    + "\" aria-label=\"You cannot have an empty bundle.\">Remove From Bundle</a></li>\n";
},"60":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodePending : depth0),{"name":"if","hash":{},"fn":this.program(61, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"61":function(depth0,helpers,partials,data) {
    var stack1;

  return "					<li><a class=\"js-unlock-invoice\" data-invoice=\""
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceId : stack1), depth0))
    + "\" href=\"javascript:void(0);\">Unlock</a></li>\n";
},"63":function(depth0,helpers,partials,data) {
    return "		<em>Processing</em>\n";
},"65":function(depth0,helpers,partials,data) {
    var stack1;

  return "		"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodePaid : depth0),{"name":"if","hash":{},"fn":this.program(66, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"66":function(depth0,helpers,partials,data) {
    return "Paid";
},"68":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodePaid : depth0),{"name":"unless","hash":{},"fn":this.program(69, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"69":function(depth0,helpers,partials,data) {
    var stack1;

  return "			"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"if","hash":{},"fn":this.program(70, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"70":function(depth0,helpers,partials,data) {
    return "Void";
},"72":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodePaid : depth0),{"name":"unless","hash":{},"fn":this.program(73, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"73":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"unless","hash":{},"fn":this.program(74, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"74":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<small>\n					Due: "
    + this.escapeExpression((helpers.formattedDate || (depth0 && depth0.formattedDate) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceDueDate : stack1),{"name":"formattedDate","hash":{},"data":data}))
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoicePastDue : stack1),{"name":"if","hash":{},"fn":this.program(75, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoicePastDue : stack1),{"name":"unless","hash":{},"fn":this.program(77, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</small>\n";
},"75":function(depth0,helpers,partials,data) {
    return "						<br/><span class=\"text-error\">Past Due</span>\n";
},"77":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceDueWithinWeek : stack1),{"name":"if","hash":{},"fn":this.program(78, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"78":function(depth0,helpers,partials,data) {
    return "							<br/><span class=\"text-error\">Coming Due</span>\n";
},"80":function(depth0,helpers,partials,data) {
    return "$0.00";
},"82":function(depth0,helpers,partials,data) {
    var stack1;

  return "		"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceIsSubscriptionOrAdhoc : depth0),{"name":"if","hash":{},"fn":this.program(83, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"83":function(depth0,helpers,partials,data) {
    var stack1;

  return this.escapeExpression((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceBalance : stack1),{"name":"formatCurrency","hash":{},"data":data}));
},"85":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceIsSubscriptionOrAdhoc : depth0),{"name":"unless","hash":{},"fn":this.program(86, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"86":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.owner : stack1),{"name":"if","hash":{},"fn":this.program(87, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"87":function(depth0,helpers,partials,data) {
    var stack1;

  return "				"
    + this.escapeExpression((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.buyerTotalCost : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "\n";
},"89":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceIsSubscriptionOrAdhoc : depth0),{"name":"unless","hash":{},"fn":this.program(90, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"90":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.owner : stack1),{"name":"unless","hash":{},"fn":this.program(91, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"91":function(depth0,helpers,partials,data) {
    var stack1;

  return "				"
    + this.escapeExpression((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.amountEarned : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<td class=\"invoice-check check\">\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isReceivables : depth0),{"name":"unless","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</td>\n\n<td class=\"invoice-detail\">\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isReceivables : depth0),{"name":"unless","hash":{},"fn":this.program(6, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	<span>"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceSummaryNumber : stack1),{"name":"if","hash":{},"fn":this.program(9, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceNumber : stack1), depth0))
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.workNumber : stack1),{"name":"if","hash":{},"fn":this.program(11, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</span>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isInvoiceTypeSubscription : depth0),{"name":"if","hash":{},"fn":this.program(13, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeSubscription : depth0),{"name":"unless","hash":{},"fn":this.program(15, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeSubscription : depth0),{"name":"unless","hash":{},"fn":this.program(18, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.workNumber : stack1),{"name":"if","hash":{},"fn":this.program(21, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.downloadedOn : stack1),{"name":"if","hash":{},"fn":this.program(28, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isReceivables : depth0),{"name":"if","hash":{},"fn":this.program(30, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n	<div class=\"help-block\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"if","hash":{},"fn":this.program(35, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"unless","hash":{},"fn":this.program(37, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.customFields : stack1),{"name":"each","hash":{},"fn":this.program(41, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		<hr/>\n		<ul class=\"invoice-actions\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isInvoiceTypeSubscription : depth0),{"name":"if","hash":{},"fn":this.program(43, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeSubscription : depth0),{"name":"unless","hash":{},"fn":this.program(45, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isInvoiceTypeSubscription : depth0),{"name":"unless","hash":{},"fn":this.program(48, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceIsBundled : stack1),{"name":"unless","hash":{},"fn":this.program(51, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.invoiceIsBundled : stack1),{"name":"if","hash":{},"fn":this.program(57, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.editable : stack1),{"name":"unless","hash":{},"fn":this.program(60, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</ul>\n	</div>\n</td>\n<td class=\"invoice-status\">\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.pendingPaymentFulfillment : stack1),{"name":"if","hash":{},"fn":this.program(63, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.pendingPaymentFulfillment : stack1),{"name":"unless","hash":{},"fn":this.program(65, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.pendingPaymentFulfillment : stack1),{"name":"unless","hash":{},"fn":this.program(68, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.model : depth0)) != null ? stack1.pendingPaymentFulfillment : stack1),{"name":"unless","hash":{},"fn":this.program(72, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</td>\n\n<td class=\"invoice-amount amount nowrap\">\n	"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"if","hash":{},"fn":this.program(80, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"unless","hash":{},"fn":this.program(82, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"unless","hash":{},"fn":this.program(85, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.invoiceStatusTypeCodeVoid : depth0),{"name":"unless","hash":{},"fn":this.program(89, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</td>\n";
},"useData":true});

this["wm"]["templates"]["payments/invoice-row-noresults"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<td colspan=\"4\" class=\"tac\">No data available in table</td>\n";
},"useData":true});

this["wm"]["templates"]["payments/ledger-description-cell"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.work_number : stack1),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.work_number : stack1),{"name":"unless","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.work_number : stack1),{"name":"unless","hash":{},"fn":this.program(10, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.work_number : stack1),{"name":"unless","hash":{},"fn":this.program(14, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.work_number : stack1),{"name":"unless","hash":{},"fn":this.program(19, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.work_number : stack1),{"name":"unless","hash":{},"fn":this.program(33, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"2":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "			<a href=\"/assignments/details/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.work_number : stack1), depth0))
    + "\">"
    + alias2(alias1((depth0 != null ? depth0.data : depth0), depth0))
    + " ("
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.work_number : stack1), depth0))
    + ")</a>\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_owner : stack1),{"name":"unless","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_owner : stack1),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<small>for "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.company_name : stack1), depth0))
    + "</small>\n";
},"5":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<small class=\"meta nowrap\">"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.invoice_number : stack1), depth0))
    + "</small>\n";
},"7":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_bundle : stack1),{"name":"if","hash":{},"fn":this.program(8, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"8":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "				<a href=\"/payments/invoices/print/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.bundle_id : stack1), depth0))
    + "\">"
    + alias2(alias1((depth0 != null ? depth0.data : depth0), depth0))
    + "</a>\n				<small class=\"meta nowrap\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.bundle_number : stack1), depth0))
    + "</small>\n";
},"10":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_bundle : stack1),{"name":"unless","hash":{},"fn":this.program(11, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"11":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_statement : stack1),{"name":"if","hash":{},"fn":this.program(12, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"12":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					<a href=\"/payments/invoices/payables/statements/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.statement_id : stack1), depth0))
    + "\">"
    + alias2(alias1((depth0 != null ? depth0.data : depth0), depth0))
    + "</a>\n					<small class=\"meta nowrap\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.statement_number : stack1), depth0))
    + "</small>\n";
},"14":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_bundle : stack1),{"name":"unless","hash":{},"fn":this.program(15, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"15":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_statement : stack1),{"name":"unless","hash":{},"fn":this.program(16, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"16":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_service_invoice : stack1),{"name":"if","hash":{},"fn":this.program(17, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"17":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "						<a href=\"/payments/invoices/print_service_invoice/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.service_invoice_id : stack1), depth0))
    + "\" target=\"_blank\">"
    + alias2(alias1((depth0 != null ? depth0.data : depth0), depth0))
    + "</a>\n						<small class=\"meta nowrap\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.service_invoice_number : stack1), depth0))
    + "</small>\n";
},"19":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_bundle : stack1),{"name":"unless","hash":{},"fn":this.program(20, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"20":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_statement : stack1),{"name":"unless","hash":{},"fn":this.program(21, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"21":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_service_invoice : stack1),{"name":"unless","hash":{},"fn":this.program(22, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"22":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_pending : stack1),{"name":"unless","hash":{},"fn":this.program(23, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"23":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isDeposit : depth0),{"name":"if","hash":{},"fn":this.program(24, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"24":function(depth0,helpers,partials,data) {
    var stack1;

  return "								"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.depositPaymentTypeCredit : depth0),{"name":"if","hash":{},"fn":this.program(25, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n								"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.depositPaymentTypeBank : depth0),{"name":"if","hash":{},"fn":this.program(27, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n								"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.depositPaymentTypeWire : depth0),{"name":"if","hash":{},"fn":this.program(29, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n								"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.depositPaymentTypeCheck : depth0),{"name":"if","hash":{},"fn":this.program(31, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"25":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return alias2(alias1((depth0 != null ? depth0.data : depth0), depth0))
    + " (<a href=\"/payments/generate_cc_receipt/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.id : stack1), depth0))
    + "\">receipt</a>)";
},"27":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return alias2(alias1((depth0 != null ? depth0.data : depth0), depth0))
    + " (<a href=\"/payments/generate_bank_receipt/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.id : stack1), depth0))
    + "\">receipt</a>)";
},"29":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return alias2(alias1((depth0 != null ? depth0.data : depth0), depth0))
    + " (<a href=\"/payments/generate_wire_or_check_receipt/wire/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.id : stack1), depth0))
    + "\">receipt</a>)";
},"31":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return alias2(alias1((depth0 != null ? depth0.data : depth0), depth0))
    + " (<a href=\"/payments/generate_wire_or_check_receipt/check/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.id : stack1), depth0))
    + "\">receipt</a>)";
},"33":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_bundle : stack1),{"name":"unless","hash":{},"fn":this.program(34, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"34":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_statement : stack1),{"name":"unless","hash":{},"fn":this.program(35, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"35":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_service_invoice : stack1),{"name":"unless","hash":{},"fn":this.program(36, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"36":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_pending : stack1),{"name":"if","hash":{},"fn":this.program(37, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"37":function(depth0,helpers,partials,data) {
    return "							"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.data : depth0), depth0))
    + "\n";
},"39":function(depth0,helpers,partials,data) {
    return this.escapeExpression(this.lambda((depth0 != null ? depth0.data : depth0), depth0));
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div>\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_prefund_assignment_authorization : stack1),{"name":"unless","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.is_prefund_assignment_authorization : stack1),{"name":"if","hash":{},"fn":this.program(39, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n</div>\n";
},"useData":true});

this["wm"]["templates"]["payments/statement-detail"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "<a href=\"/payments/statements/pay\" id=\"pay-statement-outlet\" class=\"button\">Pay Statement</a>";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.escapeExpression, alias2=helpers.helperMissing;

  return "<div class=\"well\">\n	<p>\n		"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isMasquerading : depth0),{"name":"unless","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n		<a href=\"/payments/statements/print\" id=\"print-statement-outlet\" class=\"button\">Print</a>\n	</p>\n\n	<h5>Statement ID</h5>\n	<p>"
    + alias1(this.lambda((depth0 != null ? depth0.statementNumber : depth0), depth0))
    + "</p>\n\n	<h5>Due Date</h5>\n	<p>"
    + alias1((helpers.formattedDate || (depth0 && depth0.formattedDate) || alias2).call(depth0,(depth0 != null ? depth0.statementDueDate : depth0),{"name":"formattedDate","hash":{},"data":data}))
    + "</p>\n\n	<h5>Total Balance</h5>\n	<p>"
    + alias1((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias2).call(depth0,(depth0 != null ? depth0.statementBalance : depth0),{"name":"formatCurrency","hash":{},"data":data}))
    + "</p>\n\n	<h5>Total Paid</h5>\n	<p>"
    + alias1((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias2).call(depth0,(depth0 != null ? depth0.totalPaid : depth0),{"name":"formatCurrency","hash":{},"data":data}))
    + "</p>\n\n	<h5>Remaining Balance</h5>\n	<p>"
    + alias1((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias2).call(depth0,(depth0 != null ? depth0.remainingBalance : depth0),{"name":"formatCurrency","hash":{},"data":data}))
    + "</p>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["profile/block_user_modal"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda;

  return "		<div id=\""
    + this.escapeExpression(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" class=\"wm-modal--slide "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isActive : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n			<div class=\"wm-modal--close wm-icon-x\" data-modal-close></div>\n			<h1 class=\"wm-modal--header\">\n				<span class=\"wm-modal--title\">"
    + ((stack1 = alias1((depth0 != null ? depth0.title : depth0), depth0)) != null ? stack1 : "")
    + "</span>\n			</h1>\n			<div class=\"wm-modal--content\">\n				<p>\n					This worker will not be able to take on work for your company and will be removed from all of your groups. Are you sure you want to block this worker?\n				</p>\n\n				<button class=\"button -primary profile--block-user pull-right\">Block Worker</button>\n			</div>\n		</div>\n";
},"2":function(depth0,helpers,partials,data) {
    return "-active";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"wm-modal\" data-modal>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.slides : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true});

this["wm"]["templates"]["profile/block_vendor_modal"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda;

  return "		<div id=\""
    + this.escapeExpression(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" class=\"wm-modal--slide "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isActive : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n			<div class=\"wm-modal--close wm-icon-x\" data-modal-close></div>\n			<h1 class=\"wm-modal--header\">\n				<span class=\"wm-modal--title\">"
    + ((stack1 = alias1((depth0 != null ? depth0.title : depth0), depth0)) != null ? stack1 : "")
    + "</span>\n			</h1>\n			<div class=\"wm-modal--content\">\n				<p>\n					This vendor will not be able to take on work for your company and will be removed from all of your groups. Are you sure you want to block this vendor?\n				</p>\n\n				<button class=\"button -primary profile--block-vendor pull-right\">Block Worker</button>\n			</div>\n		</div>\n";
},"2":function(depth0,helpers,partials,data) {
    return "-active";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"wm-modal\" data-modal>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.slides : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true});

this["wm"]["templates"]["profile/browse_list_empty"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<p class=\"alert-message warning\">We are currently building a list of skills for this industry. If you would like to help with this effort, please contact us.</p>\n";
},"useData":true});

this["wm"]["templates"]["profile/delete_comment_modal"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<p>Are you sure you want to delete this comment?</p>\n\n<button id=\"delete_comment_confirm_popup\" class=\"button -primary\">Delete</button>\n";
},"useData":true});

this["wm"]["templates"]["profile/profile-card"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.avatar,depth0,{"name":"avatar","hash":{"src":(depth0 != null ? depth0.avatar_uri : depth0)},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.avatar_asset_uri : depth0),{"name":"if","hash":{},"fn":this.program(4, data, 0),"inverse":this.program(6, data, 0),"data":data})) != null ? stack1 : "");
},"4":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.avatar,depth0,{"name":"avatar","hash":{"src":(depth0 != null ? depth0.avatar_asset_uri : depth0)},"data":data,"indent":"\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"6":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.avatar,depth0,{"name":"avatar","hash":{"hash":(depth0 != null ? depth0.number : depth0)},"data":data,"indent":"\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"8":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.status : stack1),"sent",{"name":"eq","hash":{},"fn":this.program(9, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"9":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.checkbox,depth0,{"name":"checkbox","hash":{"value":(depth0 != null ? depth0.number : depth0),"name":"workerNumber"},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"11":function(depth0,helpers,partials,data) {
    return "			"
    + this.escapeExpression((helpers.round || (depth0 && depth0.round) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.distance : depth0),{"name":"round","hash":{},"data":data}))
    + " miles\n";
},"13":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.disablePriceNegotiation : stack1),{"name":"unless","hash":{},"fn":this.program(14, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"14":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.is_best_price : stack1),{"name":"if","hash":{},"fn":this.program(15, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"15":function(depth0,helpers,partials,data) {
    return "						<span class=\"wm-status-label best\">BEST PRICE</span>\n";
},"17":function(depth0,helpers,partials,data) {
    return "				<span class=\"wm-status-label new tooltipped tooltipped-n\" aria-label=\"Worker is new to the marketplace\">NEW</span>\n";
},"19":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.is_expired : stack1),{"name":"unless","hash":{},"fn":this.program(20, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"20":function(depth0,helpers,partials,data) {
    return " 					<span class=\"wm-status-label applied\">APPLIED</span>\n";
},"22":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.targeted : depth0),{"name":"if","hash":{},"fn":this.program(23, data, 0),"inverse":this.program(25, data, 0),"data":data})) != null ? stack1 : "");
},"23":function(depth0,helpers,partials,data) {
    return "					<span class=\"wm-status-label invited\">INVITED</span>\n";
},"25":function(depth0,helpers,partials,data) {
    return "					<span class=\"wm-status-label\">WORK FEED</span>\n";
},"27":function(depth0,helpers,partials,data) {
    return "				<span class=\"wm-status-label declined\">DECLINED</span>\n";
},"29":function(depth0,helpers,partials,data) {
    return "				<span class=\"wm-status-label unassigned\">UNASSIGNED</span>\n";
},"31":function(depth0,helpers,partials,data) {
    return "				<span class=\"wm-status-label member tooltipped tooltipped-n\" aria-label=\"Active Worker\">ACTIVE</span>\n";
},"33":function(depth0,helpers,partials,data) {
    return "				<span class=\"wm-status-label declined tooltipped tooltipped-n\" aria-label=\"Worker declined your assignment\">DECLINED</span>\n";
},"35":function(depth0,helpers,partials,data) {
    return "				<span class=\"wm-status-label declined\">BLOCKED</span>\n";
},"37":function(depth0,helpers,partials,data) {
    return "				<span class=\"wm-status-label declined tooltipped tooltipped-n\" aria-label=\"Worker has a schedule conflict\">CONFLICT</span>\n";
},"39":function(depth0,helpers,partials,data) {
    return "				<span class=\"wm-status-label declined tooltipped tooltipped-n\" aria-label=\"You declined a previous counter offer\">OFFER DECLINED</span>\n";
},"41":function(depth0,helpers,partials,data) {
    return "				<span class=\"wm-status-label declined tooltipped tooltipped-n\" aria-label=\"The worker's counter offer has expired\">OFFER EXPIRED</span>\n";
},"43":function(depth0,helpers,partials,data) {
    return "				<span class=\"wm-status-label pending tooltipped tooltipped-n\" aria-label=\"An open offer is ready for review\">OFFER OPEN</span>\n";
},"45":function(depth0,helpers,partials,data) {
    return "				<span class=\"wm-status-label pending\">Question</span>\n";
},"47":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.ignored : depth0),{"name":"unless","hash":{},"fn":this.program(48, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"48":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "					<span class=\"wm-status-label danger tooltipped tooltipped-n\" aria-label=\""
    + alias3(((helper = (helper = helpers.description || (depth0 != null ? depth0.description : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"description","hash":{},"data":data}) : helper)))
    + "\">"
    + alias3(((helper = (helper = helpers.code || (depth0 != null ? depth0.code : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"code","hash":{},"data":data}) : helper)))
    + "\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depths[2] != null ? depths[2].assignment : depths[2])) != null ? stack1.isDispatcher : stack1),{"name":"unless","hash":{},"fn":this.program(49, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "					</span>\n";
},"49":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, helper, alias1=this.lambda, alias2=this.escapeExpression, alias3=helpers.helperMissing, alias4="function";

  return "							<a href=\"/assignments/"
    + alias2(alias1(((stack1 = (depths[3] != null ? depths[3].assignment : depths[3])) != null ? stack1.workNumber : stack1), depth0))
    + "/resources/"
    + alias2(alias1((depths[3] != null ? depths[3].number : depths[3]), depth0))
    + "/labels/"
    + alias2(((helper = (helper = helpers.encryptedId || (depth0 != null ? depth0.encryptedId : depth0)) != null ? helper : alias3),(typeof helper === alias4 ? helper.call(depth0,{"name":"encryptedId","hash":{},"data":data}) : helper)))
    + "/remove\" title=\""
    + alias2(((helper = (helper = helpers.description || (depth0 != null ? depth0.description : depth0)) != null ? helper : alias3),(typeof helper === alias4 ? helper.call(depth0,{"name":"description","hash":{},"data":data}) : helper)))
    + "\" class=\"remove\" data-behavior=\"remove-label\"> x</a>\n";
},"51":function(depth0,helpers,partials,data) {
    return "				<span class=\"tooltipped tooltipped-n\" aria-label=\"Employee\">E</span>\n";
},"53":function(depth0,helpers,partials,data) {
    return "				<span class=\"tooltipped tooltipped-n\" aria-label=\"Invited Contractor\">C</span>\n";
},"55":function(depth0,helpers,partials,data) {
    return "				<span class=\"profile-card--badge tooltipped tooltipped-n\" aria-label=\"Third Party Contractor\">3</span>\n";
},"57":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.isIndividualBundledAssignment : stack1),{"name":"unless","hash":{},"fn":this.program(58, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"58":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.isParentBundle : stack1),{"name":"unless","hash":{},"fn":this.program(59, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"59":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.status : stack1),"sent",{"name":"eq","hash":{},"fn":this.program(60, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"60":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.isDeputy : stack1),{"name":"if","hash":{},"fn":this.program(61, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"61":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "								<small>\n									<a href=\"/profile/"
    + alias3(((helper = (helper = helpers.number || (depth0 != null ? depth0.number : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"number","hash":{},"data":data}) : helper)))
    + "\" data-usernumber=\"_"
    + alias3(((helper = (helper = helpers.number || (depth0 != null ? depth0.number : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"number","hash":{},"data":data}) : helper)))
    + "\" class=\"worker-link\">(actions)</a>\n								</small>\n";
},"63":function(depth0,helpers,partials,data) {
    var stack1, helper;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.publicWorkers : depth0),{"name":"if","hash":{},"fn":this.program(64, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				<li>Member since: "
    + this.escapeExpression(((helper = (helper = helpers.created_on || (depth0 != null ? depth0.created_on : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"created_on","hash":{},"data":data}) : helper)))
    + "</li>\n";
},"64":function(depth0,helpers,partials,data) {
    var helper;

  return "					<li>Team size: "
    + this.escapeExpression(((helper = (helper = helpers.publicWorkers || (depth0 != null ? depth0.publicWorkers : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"publicWorkers","hash":{},"data":data}) : helper)))
    + "</li>\n";
},"66":function(depth0,helpers,partials,data) {
    var helper;

  return "				<li>"
    + this.escapeExpression(((helper = (helper = helpers.company_name || (depth0 != null ? depth0.company_name : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"company_name","hash":{},"data":data}) : helper)))
    + "</li>\n";
},"68":function(depth0,helpers,partials,data) {
    var helper;

  return "<li>W: "
    + this.escapeExpression(((helper = (helper = helpers.work_phone || (depth0 != null ? depth0.work_phone : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"work_phone","hash":{},"data":data}) : helper)))
    + "</li>";
},"70":function(depth0,helpers,partials,data) {
    var helper;

  return "<li>M: "
    + this.escapeExpression(((helper = (helper = helpers.mobile_phone || (depth0 != null ? depth0.mobile_phone : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"mobile_phone","hash":{},"data":data}) : helper)))
    + "</li>";
},"72":function(depth0,helpers,partials,data) {
    var helper;

  return "				<li>\n					<span class=\"passed\"><i class=\"wm-icon-checkmark\"></i> PASSED</span>\n					<small>"
    + this.escapeExpression(((helper = (helper = helpers.background_check_date || (depth0 != null ? depth0.background_check_date : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"background_check_date","hash":{},"data":data}) : helper)))
    + "</small>\n				</li>\n";
},"74":function(depth0,helpers,partials,data) {
    var helper;

  return "				<li>\n					<span class=\"failed\"><i class=\"wm-icon-x\"></i> ALERT</span>\n					<small>"
    + this.escapeExpression(((helper = (helper = helpers.background_check_date || (depth0 != null ? depth0.background_check_date : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"background_check_date","hash":{},"data":data}) : helper)))
    + "</small>\n				</li>\n";
},"76":function(depth0,helpers,partials,data) {
    var helper;

  return "				<li>\n					<span class=\"passed\"><i class=\"wm-icon-pill\"></i> PASSED</span>\n					<small>"
    + this.escapeExpression(((helper = (helper = helpers.drug_test_date || (depth0 != null ? depth0.drug_test_date : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"drug_test_date","hash":{},"data":data}) : helper)))
    + "</small>\n				</li>\n";
},"78":function(depth0,helpers,partials,data) {
    var helper;

  return "				<li>\n					<span class=\"failed\"><i class=\"wm-icon-pill\"></i> FAILED</span>\n					<small>"
    + this.escapeExpression(((helper = (helper = helpers.drug_test_date || (depth0 != null ? depth0.drug_test_date : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"drug_test_date","hash":{},"data":data}) : helper)))
    + "</small>\n				</li>\n";
},"80":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "			<dl class=\"iconed-dl profile-card--dispatcher-details\">\n				<dt>"
    + ((stack1 = this.invokePartial(partials.icon,depth0,{"name":"icon","hash":{"name":"team-agent"},"data":data,"helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "</dt>\n				<dd>\n					<ul class=\"profile-card--secondary\">\n						<li><strong>"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.dispatcher : depth0)) != null ? stack1.firstName : stack1), depth0))
    + " "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.dispatcher : depth0)) != null ? stack1.lastName : stack1), depth0))
    + "</strong></li>\n						"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.dispatcher : depth0)) != null ? stack1.workPhone : stack1),{"name":"if","hash":{},"fn":this.program(81, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n						"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.dispatcher : depth0)) != null ? stack1.mobilePhone : stack1),{"name":"if","hash":{},"fn":this.program(83, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n						"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.dispatcher : depth0)) != null ? stack1.email : stack1),{"name":"if","hash":{},"fn":this.program(85, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n					</ul>\n				</dd>\n			</dl>\n";
},"81":function(depth0,helpers,partials,data) {
    var stack1;

  return "<li>W: "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.dispatcher : depth0)) != null ? stack1.workPhone : stack1), depth0))
    + "</li>";
},"83":function(depth0,helpers,partials,data) {
    var stack1;

  return "<li>M: "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.dispatcher : depth0)) != null ? stack1.mobilePhone : stack1), depth0))
    + "</li>";
},"85":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<li><a href=\"mailto:"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.dispatcher : depth0)) != null ? stack1.email : stack1), depth0))
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.dispatcher : depth0)) != null ? stack1.email : stack1), depth0))
    + "</a></li>";
},"87":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<h2 class=\"profile-card--header\">\n				Assignment Eligibility Requirements\n			</h2>\n			<ul class=\"profile-card--eligibility\">\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.eligibility : depth0)) != null ? stack1.criteria : stack1),{"name":"each","hash":{},"fn":this.program(88, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</ul>\n";
},"88":function(depth0,helpers,partials,data) {
    var stack1;

  return "					<li>\n						<span class=\""
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.met : depth0),{"name":"if","hash":{},"fn":this.program(89, data, 0),"inverse":this.program(91, data, 0),"data":data})) != null ? stack1 : "")
    + " pull-left\">\n							<i class=\"wm-icon"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.met : depth0),{"name":"if","hash":{},"fn":this.program(93, data, 0),"inverse":this.program(95, data, 0),"data":data})) != null ? stack1 : "")
    + "\"></i>\n						</span>\n						<span class=\"profile-card--eligibility-item\">"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.name : depth0), depth0))
    + "</span>\n					</li>\n";
},"89":function(depth0,helpers,partials,data) {
    return "passed";
},"91":function(depth0,helpers,partials,data) {
    return "failed";
},"93":function(depth0,helpers,partials,data) {
    return "-checkmark";
},"95":function(depth0,helpers,partials,data) {
    return "-x";
},"97":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing;

  return "			<div class=\"page-header\">\n\n				<h5>\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.is_expired : stack1),{"name":"unless","hash":{},"fn":this.program(98, data, 0),"inverse":this.program(106, data, 0),"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.approval_status : stack1),2,{"name":"eq","hash":{},"fn":this.program(108, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</h5>\n			</div>\n			<div>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.is_expired : stack1),{"name":"if","hash":{},"fn":this.program(113, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.expires_on : stack1),{"name":"if","hash":{},"fn":this.program(115, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.note : stack1),{"name":"if","hash":{},"fn":this.program(117, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.is_schedule_negotiation : stack1),{"name":"if","hash":{},"fn":this.program(119, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.is_price_negotiation : stack1),{"name":"if","hash":{},"fn":this.program(121, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</div>\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.is_expired : depth0),{"name":"unless","hash":{},"fn":this.program(135, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.status : depth0),"declined",{"name":"eq","hash":{},"fn":this.program(137, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"98":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.is_price_negotiation : stack1),{"name":"if","hash":{},"fn":this.program(99, data, 0),"inverse":this.program(101, data, 0),"data":data})) != null ? stack1 : "");
},"99":function(depth0,helpers,partials,data) {
    return "							<strong>Status:</strong> Counteroffered\n";
},"101":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.is_schedule_negotiation : stack1),{"name":"if","hash":{},"fn":this.program(102, data, 0),"inverse":this.program(104, data, 0),"data":data})) != null ? stack1 : "");
},"102":function(depth0,helpers,partials,data) {
    return "								<strong>Status:</strong> Counteroffered\n";
},"104":function(depth0,helpers,partials,data) {
    return "								<strong>Status:</strong> Applied\n";
},"106":function(depth0,helpers,partials,data) {
    return "						<strong>Status:</strong> Offer expired\n";
},"108":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.isDispatcher : stack1),{"name":"if","hash":{},"fn":this.program(109, data, 0),"inverse":this.program(111, data, 0),"data":data})) != null ? stack1 : "");
},"109":function(depth0,helpers,partials,data) {
    return "							<span class=\"label label-important tooltipped tooltipped-n\" aria-label=\"Your application was declined\">Offer Declined</span>\n";
},"111":function(depth0,helpers,partials,data) {
    return "							<span class=\"label label-important tooltipped tooltipped-n\" aria-label=\"You declined this application\">Offer Declined</span>\n";
},"113":function(depth0,helpers,partials,data) {
    var stack1;

  return "					<p>This application <strong>expired</strong> on "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.expires_on : stack1), depth0))
    + ".</p>\n";
},"115":function(depth0,helpers,partials,data) {
    var stack1;

  return "					<p>This application <strong>expires</strong> on "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.expires_on : stack1), depth0))
    + ".</p>\n";
},"117":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=this.escapeExpression;

  return "					<div>\n						<h5><strong>Message from "
    + alias1(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + ":</strong></h5>\n						<p>"
    + alias1(this.lambda(((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.note : stack1), depth0))
    + "</p>\n					</div>\n";
},"119":function(depth0,helpers,partials,data) {
    var stack1;

  return "					<div>\n						<h5><strong>Proposed Date / Time:</strong></h5>\n						<p>"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.schedule : stack1), depth0))
    + "</p>\n					</div>\n";
},"121":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing;

  return "					<div>\n						<h5><strong>Proposed Price:</strong></h5>\n						<table class=\"pricing-summary\">\n							<tbody>\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.id : stack1),1,{"name":"eq","hash":{},"fn":this.program(122, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.id : stack1),2,{"name":"eq","hash":{},"fn":this.program(124, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.id : stack1),3,{"name":"eq","hash":{},"fn":this.program(126, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.id : stack1),4,{"name":"eq","hash":{},"fn":this.program(128, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.additional_expenses : stack1),{"name":"if","hash":{},"fn":this.program(130, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n								<tr class=\"subtotal\">\n									<td>Worker Max Earnings</td>\n									<td>"
    + this.escapeExpression((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.spend_limit : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n								</tr>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.isAdmin : stack1),{"name":"if","hash":{},"fn":this.program(132, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "							</tbody>\n						</table>\n					</div>\n";
},"122":function(depth0,helpers,partials,data) {
    var stack1;

  return "									<tr>\n										<td class=\"normal\">Flat price</td>\n										<td>"
    + this.escapeExpression((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || helpers.helperMissing).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.flat_price : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n									</tr>\n";
},"124":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.escapeExpression;

  return "									<tr>\n										<td class=\"normal\">Price per hour (up to "
    + alias1(this.lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.max_number_of_hours : stack1), depth0))
    + " hours)</td>\n										<td>"
    + alias1((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || helpers.helperMissing).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.per_hour_price : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n									</tr>\n";
},"126":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.escapeExpression;

  return "									<tr>\n										<td class=\"normal\">Price per unit (up to "
    + alias1(this.lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.max_number_of_units : stack1), depth0))
    + " units)</td>\n										<td>"
    + alias1((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || helpers.helperMissing).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.per_unit_price : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n									</tr>\n";
},"128":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression, alias3=helpers.helperMissing;

  return "									<tr>\n										<td class=\"normal\">Price per hour (up to "
    + alias2(alias1(((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.initial_number_of_hours : stack1), depth0))
    + " hours)</td>\n										<td>"
    + alias2((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias3).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.initial_per_hour_price : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n									</tr>\n									<tr>\n										<td class=\"normal\">Price per additional hour (up to "
    + alias2(alias1(((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.max_blended_number_of_hours : stack1), depth0))
    + " hours)</td>\n										<td>"
    + alias2((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias3).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.additional_per_hour_price : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n									</tr>\n";
},"130":function(depth0,helpers,partials,data) {
    var stack1;

  return "									<tr>\n										<td class=\"normal\">Additional Expenses</td>\n										<td>"
    + this.escapeExpression((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || helpers.helperMissing).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.additional_expenses : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n									</tr>\n";
},"132":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing;

  return ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.accountPricingType : stack1)) != null ? stack1.code : stack1),"transactional",{"name":"eq","hash":{},"fn":this.program(133, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n									<tr class=\"total\">\n										<td>Assignment Value</td>\n										<td>\n											<strong>"
    + this.escapeExpression((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.total_cost : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</strong>\n										</td>\n									</tr>\n";
},"133":function(depth0,helpers,partials,data) {
    var stack1;

  return "										<tr>\n											<td>Transaction Fee</td>\n											<td>+ "
    + this.escapeExpression((helpers.formatCurrency || (depth0 && depth0.formatCurrency) || helpers.helperMissing).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.pricing : stack1)) != null ? stack1.fee : stack1),{"name":"formatCurrency","hash":{},"data":data}))
    + "</td>\n										</tr>\n";
},"135":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<span class=\"pull-right\">Updated: "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.requested_on_date : stack1), depth0))
    + "</span>\n";
},"137":function(depth0,helpers,partials,data) {
    return "				<small class=\"text-right\">Status: Worker Declined</small>\n";
},"139":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.isIndividualBundledAssignment : stack1),{"name":"unless","hash":{},"fn":this.program(140, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"140":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.is_expired : stack1),{"name":"unless","hash":{},"fn":this.program(141, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"141":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.has_negotiation : depth0),{"name":"if","hash":{},"fn":this.program(142, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"142":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.negotiationOpen || (depth0 && depth0.negotiationOpen) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.status : depth0),((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.approval_status : stack1),{"name":"negotiationOpen","hash":{},"fn":this.program(143, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"143":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.unlessApplicationAccepted || (depth0 && depth0.unlessApplicationAccepted) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.approval_status : stack1),{"name":"unlessApplicationAccepted","hash":{},"fn":this.program(144, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"144":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "								<div class=\"profile-card--actions\">\n									<form action=\"/assignments/accept_negotiation/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.workNumber : stack1), depth0))
    + "\">\n										<input type=\"hidden\" name=\"id\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.encrypted_id : stack1), depth0))
    + "\"/>\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.isParentBundle : stack1),{"name":"unless","hash":{},"fn":this.program(145, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "										<button type=\"submit\" class=\"accept-negotiation button -primary\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.blocked : depth0),{"name":"if","hash":{},"fn":this.program(147, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">Accept</button>\n									</form>\n								</div>\n";
},"145":function(depth0,helpers,partials,data) {
    var stack1;

  return "											<button rel=\"prompt_decline_negotiation\" data-negotiation-id=\""
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.encrypted_id : stack1), depth0))
    + "\" class=\"decline-negotation button\">Decline</button>\n";
},"147":function(depth0,helpers,partials,data) {
    return "disabled";
},"149":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.isDispatcher : stack1),{"name":"if","hash":{},"fn":this.program(150, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"150":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<div class=\"profile-card--actions\">\n					<div class=\"dispatcher-actions\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.negotiation : depth0),{"name":"if","hash":{},"fn":this.program(151, data, 0),"inverse":this.program(157, data, 0),"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.assignToFirstResource : stack1),{"name":"if","hash":{},"fn":this.program(159, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "					</div>\n				</div>\n";
},"151":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.negotiationOpen || (depth0 && depth0.negotiationOpen) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.status : depth0),((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.approval_status : stack1),{"name":"negotiationOpen","hash":{},"fn":this.program(152, data, 0),"inverse":this.program(154, data, 0),"data":data})) != null ? stack1 : "");
},"152":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=this.lambda, alias2=this.escapeExpression;

  return "								<button class=\"-cancel-application button -primary\" data-work-id=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.workNumber : stack1), depth0))
    + "\" data-worker-id=\""
    + alias2(((helper = (helper = helpers.number || (depth0 != null ? depth0.number : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"number","hash":{},"data":data}) : helper)))
    + "\" data-id=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.negotiation : depth0)) != null ? stack1.encrypted_id : stack1), depth0))
    + "\">Cancel</button>\n";
},"154":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=this.escapeExpression;

  return "								<button class=\"-send-application button -primary\" data-work-id=\""
    + alias1(this.lambda(((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.workNumber : stack1), depth0))
    + "\" data-id=\""
    + alias1(((helper = (helper = helpers.number || (depth0 != null ? depth0.number : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"number","hash":{},"data":data}) : helper)))
    + "\""
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.eligibility : depth0)) != null ? stack1.eligible : stack1),{"name":"unless","hash":{},"fn":this.program(155, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">Apply</button>\n";
},"155":function(depth0,helpers,partials,data) {
    return " disabled";
},"157":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.assignToFirstResource : stack1),{"name":"unless","hash":{},"fn":this.program(154, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"159":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=this.escapeExpression;

  return "							<button class=\"-accept-invitation button -primary\" data-work-id=\""
    + alias1(this.lambda(((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.workNumber : stack1), depth0))
    + "\" data-id=\""
    + alias1(((helper = (helper = helpers.number || (depth0 != null ? depth0.number : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"number","hash":{},"data":data}) : helper)))
    + "\""
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.eligibility : depth0)) != null ? stack1.eligible : stack1),{"name":"unless","hash":{},"fn":this.program(155, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">Accept</button>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "<div class=\"profile-card\">\n	<div class=\"profile-card--photo\" data-userNumber=\""
    + alias3(((helper = (helper = helpers.number || (depth0 != null ? depth0.number : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"number","hash":{},"data":data}) : helper)))
    + "\">\n		<div class=\"profile-card--avatar open-user-profile-popup\" data-type=\""
    + alias3(((helper = (helper = helpers.resourceType || (depth0 != null ? depth0.resourceType : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"resourceType","hash":{},"data":data}) : helper)))
    + "\" data-number=\""
    + alias3(((helper = (helper = helpers.number || (depth0 != null ? depth0.number : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"number","hash":{},"data":data}) : helper)))
    + "\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.avatar_uri : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0, blockParams, depths),"inverse":this.program(3, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "")
    + "		</div>\n\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.disableBulkActions : depth0),{"name":"unless","hash":{},"fn":this.program(8, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.distance : depth0),{"name":"if","hash":{},"fn":this.program(11, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n		<div class=\"status\">\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.isDispatcher : stack1),{"name":"unless","hash":{},"fn":this.program(13, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.new_user : depth0),{"name":"if","hash":{},"fn":this.program(17, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.negotiation : depth0),{"name":"if","hash":{},"fn":this.program(19, data, 0, blockParams, depths),"inverse":this.program(22, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.declined : depth0),{"name":"if","hash":{},"fn":this.program(27, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.status : depth0),"unassigned",{"name":"eq","hash":{},"fn":this.program(29, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.status : depth0),"active",{"name":"eq","hash":{},"fn":this.program(31, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.status : depth0),"declined",{"name":"eq","hash":{},"fn":this.program(33, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.blocked : depth0),{"name":"if","hash":{},"fn":this.program(35, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.schedule_conflict : depth0),{"name":"if","hash":{},"fn":this.program(37, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.latest_negotiation_declined : depth0),{"name":"if","hash":{},"fn":this.program(39, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.latest_negotiation_expired : depth0),{"name":"if","hash":{},"fn":this.program(41, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.latest_negotiation_pending : depth0),{"name":"if","hash":{},"fn":this.program(43, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.question_pending : depth0),{"name":"if","hash":{},"fn":this.program(45, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.labels : depth0),{"name":"each","hash":{},"fn":this.program(47, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</div>\n	</div>\n	<div class=\"profile-card--details\">\n\n		<h2 class=\"profile-card--header\">\n			<a href=\"/profile/"
    + alias3(((helper = (helper = helpers.number || (depth0 != null ? depth0.number : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"number","hash":{},"data":data}) : helper)))
    + "\" class=\"profile-card--name open-user-profile-popup\" data-type=\""
    + alias3(((helper = (helper = helpers.resourceType || (depth0 != null ? depth0.resourceType : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"resourceType","hash":{},"data":data}) : helper)))
    + "\" data-number=\""
    + alias3(((helper = (helper = helpers.number || (depth0 != null ? depth0.number : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"number","hash":{},"data":data}) : helper)))
    + "\">"
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "</a>\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.lane : depth0),0,{"name":"eq","hash":{},"fn":this.program(51, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.lane : depth0),1,{"name":"eq","hash":{},"fn":this.program(51, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.lane : depth0),2,{"name":"eq","hash":{},"fn":this.program(53, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.lane : depth0),3,{"name":"eq","hash":{},"fn":this.program(55, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			<small>ID: "
    + alias3(((helper = (helper = helpers.number || (depth0 != null ? depth0.number : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"number","hash":{},"data":data}) : helper)))
    + "</small>\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.disableBulkActions : depth0),{"name":"unless","hash":{},"fn":this.program(57, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</h2>\n"
    + ((stack1 = this.invokePartial(partials['score-card'],depth0,{"name":"score-card","hash":{"isDispatch":((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.isDispatcher : stack1),"nameIdentifier":(depth0 != null ? depth0.number : depth0),"values":((stack1 = (depth0 != null ? depth0.scoreCardData : depth0)) != null ? stack1.values : stack1),"showrecent":true,"classlist":"profile-card--score-card"},"data":data,"indent":"\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "		<ul class=\"profile-card--address\">\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.resourceType : depth0),"vendors",{"name":"eq","hash":{},"fn":this.program(63, data, 0, blockParams, depths),"inverse":this.program(66, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "")
    + "			<li>"
    + alias3(((helper = (helper = helpers.address || (depth0 != null ? depth0.address : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"address","hash":{},"data":data}) : helper)))
    + "</li>\n			"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.work_phone : depth0),{"name":"if","hash":{},"fn":this.program(68, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.mobile_phone : depth0),{"name":"if","hash":{},"fn":this.program(70, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			<li><a href=\"mailto:"
    + alias3(((helper = (helper = helpers.email || (depth0 != null ? depth0.email : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"email","hash":{},"data":data}) : helper)))
    + "\">"
    + alias3(((helper = (helper = helpers.email || (depth0 != null ? depth0.email : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"email","hash":{},"data":data}) : helper)))
    + "</a></li>\n		</ul>\n		<ul class=\"profile-card--secondary\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.background_check : depth0),{"name":"if","hash":{},"fn":this.program(72, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.background_check_failed : depth0),{"name":"if","hash":{},"fn":this.program(74, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.drug_test : depth0),{"name":"if","hash":{},"fn":this.program(76, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.drug_test_failed : depth0),{"name":"if","hash":{},"fn":this.program(78, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</ul>\n\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.dispatcher : depth0),{"name":"if","hash":{},"fn":this.program(80, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.eligibility : depth0)) != null ? stack1.criteria : stack1),{"name":"if","hash":{},"fn":this.program(87, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</div>\n\n	<div>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.negotiation : depth0),{"name":"if","hash":{},"fn":this.program(97, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</div>\n\n	<div class=\"profile-card--shade\">\n		<div class=\"profile-card--view-profile wm-icon-eye open-user-profile-popup\" data-dispatch=\""
    + alias3(this.lambda(((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.isDispatcher : stack1), depth0))
    + "\" data-type=\""
    + alias3(((helper = (helper = helpers.resourceType || (depth0 != null ? depth0.resourceType : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"resourceType","hash":{},"data":data}) : helper)))
    + "\" data-number=\""
    + alias3(((helper = (helper = helpers.number || (depth0 != null ? depth0.number : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"number","hash":{},"data":data}) : helper)))
    + "\">View Profile</div>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.isBuyerAuthorizedToApproveCounter : stack1),{"name":"if","hash":{},"fn":this.program(139, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.assignment : depth0)) != null ? stack1.isIndividualBundledAssignment : stack1),{"name":"unless","hash":{},"fn":this.program(149, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</div>\n</div>\n";
},"usePartial":true,"useData":true,"useDepths":true});

this["wm"]["templates"]["profile/report_concern_modal"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda;

  return "		<div id=\""
    + this.escapeExpression(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" class=\"wm-modal--slide "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isActive : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n			<div class=\"wm-modal--close wm-icon-x\" data-modal-close></div>\n			<h1 class=\"wm-modal--header\">\n				<span class=\"wm-modal--title\">"
    + ((stack1 = alias1((depth0 != null ? depth0.title : depth0), depth0)) != null ? stack1 : "")
    + "</span>\n			</h1>\n			<div class=\"wm-modal--content\">\n				<p>\n					Thank you for choosing to report a concern, we love to hear from you. Please briefly describe your concern below — we’ll review and take action immediately.\n				</p>\n\n				<textarea name=\"content\" id=\"concern_content\" rows=\"5\"></textarea>\n\n				<button class=\"button -primary profile--report-concern pull-right\">Submit</button>\n			</div>\n		</div>\n";
},"2":function(depth0,helpers,partials,data) {
    return "-active";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"wm-modal\" data-modal>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.slides : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true});

this["wm"]["templates"]["profile/review_media_modal"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div id=\"review_media_modal\">\n	<p id=\"mediadetail\"></p>\n	<div class=\"wm-modal--footer\">\n		<a id=\"download_link\" class=\"button -primary wm-icon-download\">Download</a>\n		<button class=\"button\" data-modal-close>Close</button>\n		<a id=\"remove-media\" class=\"button wm-icon-remove\">Remove</a>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["profile/sms_step1"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<p>Before you can receive notifications via SMS, you must activate your phone for Work Market Mobile.</p>\n\nPhone Number: <input type=\"tel\" name=\"smsPhone\" id=\"smsPhone\" alt=\"phone-us\" maxlength=\"255\" data-mask />\n\n<button class=\"button -primary\" data-slide=\"next\">Next</button>\n";
},"useData":true});

this["wm"]["templates"]["profile/sms_step2"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<p>In a few seconds, you should receive a text message that contains your mobile activation code. If you haven't received a code after a few minutes, please repeat <a href=\"javascript:void(0);\" data-slide=\"prev\">Step 1</a>.</p>\n\n<label>\n	Enter the code here\n	<input type=\"text\" name=\"code\" id=\"code\" maxlength=\"20\" />\n</label>\n\n<button class=\"button\" data-slide=\"prev\">Back</button>\n<button class=\"button -primary\" data-slide=\"finish\">Confirm</button>\n";
},"useData":true});

this["wm"]["templates"]["profile/upload_media_modal"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div id=\"add_media_modal\">\n	<div class=\"asset-type-toggle\">\n		<a class=\"pull-right toggle_upload toggle_anchor\"><small>Upload YouTube Video</small></a>\n		<a class=\"pull-right toggle_upload toggle_anchor\" style=\"display: none;\"><small>Upload Photo/Video</small></a>\n	</div>\n	<div id=\"image_preview\" class=\"image_preview\"></div>\n\n	<div class=\"media_details toggle_upload\">\n		<label for=\"add_profile_mult_photo_id\">\n			<input type=\"text\" id=\"add_profile_mult_photo_id\" placeholder=\"Optional File Caption\"/>\n		</label>\n		<span class=\"help-block\">Photo/Video size limit is 150MB.</span>\n		<div id=\"file-uploader\" class=\"file_uploader\">\n			<noscript>\n"
    + ((stack1 = this.invokePartial(partials['file-input'],depth0,{"name":"file-input","hash":{"id":"qqfile","name":"qqfile"},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "			</noscript>\n		</div>\n		<input type=\"hidden\" id=\"media_id\" />\n		<input type=\"hidden\" id=\"position\" />\n	</div>\n	<div class=\"youtube_details toggle_upload\" style=\"display: none;\">\n		<label>\n			<input type=\"text\" id=\"youtube_input\" class=\"span7\" placeholder=\"Paste the embed url here\"/>\n		</label>\n		<span>Upload a <a href=\"http://www.youtube.com\">Youtube</a> video by pasting your url above</span>\n		<span class=\"help-block\">Example of video url: http://www.youtube.com/watch?v=rZBVRw9frhE</span>\n	</div>\n\n	<h5>Privacy Settings - <small>control who can view the uploaded photo/video by selecting from the following.</small></h5>\n"
    + ((stack1 = this.invokePartial(partials.radio,depth0,{"name":"radio","hash":{"isChecked":true,"text":"<span class='tooltipped tooltipped-n' aria-label='All Group Owners can view'>Companies of Groups I am in</span>","value":"group","name":"privacy_upload"},"data":data,"indent":"\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials.radio,depth0,{"name":"radio","hash":{"text":"<span class='tooltipped tooltipped-n' aria-label='Everyone can view'>Anyone</span>","value":"all","name":"privacy_upload"},"data":data,"indent":"\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "\n	<div class=\"wm-modal--footer\">\n		<button id=\"save_media\" class=\"button -primary\" disabled>Complete Upload &amp; Save</button>\n		<button class=\"button\" data-modal-close>Close</button>\n	</div>\n</div>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["public/work-feed-snippet"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<script type=\"text/javascript\">\n	<!--\n		wm_feed_title = '"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.title : stack1), depth0))
    + "';\n		wm_feed_background_color = '"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.backgroundColor : stack1), depth0))
    + "';\n		wm_feed_width = '"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.width : stack1), depth0))
    + "';\n		wm_feed_text_font = '"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.font : stack1), depth0))
    + "';\n		wm_feed_link_color = '"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.linkColor : stack1), depth0))
    + "';\n		wm_feed_border = '"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.border : stack1), depth0))
    + "';\n		wm_feed_padding = '"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.data : depth0)) != null ? stack1.padding : stack1), depth0))
    + "';\n	//-->\n	</script>\n	<script type=\"text/javascript\" src=\"//www.workmarket.com/feed/s"
    + ((stack1 = alias1((depth0 != null ? depth0.query : depth0), depth0)) != null ? stack1 : "")
    + "\"></script>\n	<noscript><a href=\"//www.workmarket.com/\" style=\"text-decoration:none; background-color:#4B5254; padding:2px 5px\"><span style=\"color:#FE8707;\">Work</span><span style=\"color:#FFFFFF;\">Market</span></a></noscript>";
},"useData":true});

this["wm"]["templates"]["ratings/communication"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<div class=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.communicationCode : stack1), depth0))
    + "\">\n			"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.communicationValue : stack1), depth0))
    + "\n		</div>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div>\n"
    + ((stack1 = (helpers.qualityValueNotApplicable || (depth0 && depth0.qualityValueNotApplicable) || helpers.helperMissing).call(depth0,depth0,{"name":"qualityValueNotApplicable","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true});

this["wm"]["templates"]["ratings/name"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "			<a href=\"/assignments/details/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.workNumber : stack1), depth0))
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.workTitle : stack1), depth0))
    + "</a> <small>("
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.workSchedule : stack1), depth0))
    + ")</small><br/>\n";
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<strong>"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.workTitle : stack1), depth0))
    + "</strong>\n";
},"5":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.companyName : stack1),{"name":"if","hash":{},"fn":this.program(6, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"6":function(depth0,helpers,partials,data) {
    var stack1;

  return "				"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.companyName : stack1), depth0))
    + "<br/>\n";
},"8":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<blockquote>\n			<span>"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.ratingReview : stack1), depth0))
    + "</span>\n		</blockquote>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div>\n	<p>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.workNumber : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.program(3, data, 0),"data":data})) != null ? stack1 : "")
    + "\n		"
    + ((stack1 = (helpers.resourceLabels || (depth0 && depth0.resourceLabels) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.resourceLabels : stack1),{"name":"resourceLabels","hash":{},"data":data})) != null ? stack1 : "")
    + "\n\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isOwner : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		<small></small>\n	</p>\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.ratingReview : stack1),{"name":"if","hash":{},"fn":this.program(8, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true});

this["wm"]["templates"]["ratings/overall"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div>\n	<div class=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.ratingCode : stack1), depth0))
    + "\">\n		"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.ratingValue : stack1), depth0))
    + "\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["ratings/professionalism"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<div class=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.professionalismCode : stack1), depth0))
    + "\">\n			"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.professionalismValue : stack1), depth0))
    + "\n		</div>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div>\n"
    + ((stack1 = (helpers.qualityValueNotApplicable || (depth0 && depth0.qualityValueNotApplicable) || helpers.helperMissing).call(depth0,depth0,{"name":"qualityValueNotApplicable","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true});

this["wm"]["templates"]["ratings/quality"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<div class=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.qualityCode : stack1), depth0))
    + "\">\n			"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.meta : depth0)) != null ? stack1.qualityValue : stack1), depth0))
    + "\n		</div>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div>\n"
    + ((stack1 = (helpers.qualityValueNotApplicable || (depth0 && depth0.qualityValueNotApplicable) || helpers.helperMissing).call(depth0,depth0,{"name":"qualityValueNotApplicable","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/abandoned-requirement-form"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"control-group\">\n	<label for=\"maximum-abandoned\" class=\"control-label\">Input the maximum acceptable abandoned assignments</label>\n	<div class=\"controls\">\n		<input id=\"maximum-abandoned\" name=\"maximum-abandons\" type=\"text\" />\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/abstract-requirement-form"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div data-placeholder=\"partitioner\"></div>\n<div data-placeholder=\"requirable\"></div>\n<div data-placeholder=\"expiry\"></div>\n<div data-placeholder=\"mandatory\"></div>\n<div data-placeholder=\"add-button\"></div>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/availability-requirement-form"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"control-group\">\n	<label for=\"from-time\" class=\"control-label\">From</label>\n	<div class=\"controls\">\n		<input id=\"from-time\" name=\"from-time\" type=\"text\" class=\"hours\" />\n	</div>\n</div>\n<div class=\"control-group\">\n	<label for=\"to-time\" class=\"control-label\">To</label>\n	<div class=\"controls\">\n		<input id=\"to-time\" name=\"to-time\" type=\"text\" class=\"hours\" />\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/button"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "disabled=\"disabled\"";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<button class=\"button\" "
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.enabled : depth0),{"name":"unless","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " data-action=\"add\" data-kind=\""
    + alias2(alias1((depth0 != null ? depth0.label : depth0), depth0))
    + "\">Add "
    + alias2(alias1((depth0 != null ? depth0.label : depth0), depth0))
    + "</button>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/cancelled-requirement-form"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"control-group\">\n	<label for=\"maximum-cancelled\" class=\"control-label\">Input the maximum acceptable cancelled assignments</label>\n	<div class=\"controls\">\n		<input id=\"maximum-cancelled\" name=\"maximum-cancelled\" type=\"text\" />\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/cart-item"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "<span><i class=\"wm-icon-trash icon-large muted\" data-action=\"trash\"></i></span>\n<span>"
    + alias2(alias1((depth0 != null ? depth0.requirementType : depth0), depth0))
    + ": <strong>"
    + alias2(alias1((depth0 != null ? depth0.content : depth0), depth0))
    + " "
    + alias2(alias1((depth0 != null ? depth0.mandatory : depth0), depth0))
    + "</strong></span>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/company-work-requirement-form"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"control-group\">\n	<label for=\"minimum-work-count\" class=\"control-label\">Input the minimum number of paid assignments for the selected company</label>\n	<div class=\"controls\">\n		<input id=\"minimum-work-count\" name=\"minimum-work-count\" type=\"text\" />\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/deliverable-ontime-requirement-form"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"control-group\">\n	<label for=\"minimum-deliverable-percentage\" class=\"control-label\">Input the minimum deliverable on-time rating (percentage)</label>\n	<div class=\"controls\">\n		<input id=\"minimum-deliverable-percentage\" name=\"minimum-deliverable-percentage\" type=\"text\" />\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/expirable-requirement-form"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"control-group\">\n	<div class=\"controls\">\n		<label class=\"checkbox\">\n			<input type=\"checkbox\" name=\"notify-on-expiry\" id=\"notify-on-expiry\">\n			Notify me when a group member's "
    + alias2(alias1((depth0 != null ? depth0.label : depth0), depth0))
    + " has expired\n		</label>\n	</div>\n\n	<div class=\"controls\">\n		<label class=\"checkbox\">\n			<input type=\"checkbox\" name=\"remove-membership-on-expiry\" id=\"remove-membership-on-expiry\">\n			Remove group members when their "
    + alias2(alias1((depth0 != null ? depth0.label : depth0), depth0))
    + " has expired\n		</label>\n	</div>\n</div>";
},"useData":true});

this["wm"]["templates"]["requirementsets/form"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"row\">\n	<div class=\"span12\">\n		<div class=\"requirement-set-title-form\"></div>\n	</div>\n</div>\n\n<div class=\"row\">\n	<div class=\"span5\">\n		<div class=\"requirements-form\"></div>\n	</div>\n	<div class=\"span1\">\n		<div class=\"segue\">\n			<i class=\"icon-arrow-right icon-4x muted\"></i>\n		</div>\n	</div>\n	<div class=\"span5\">\n		<div class=\"requirements-cart\"></div>\n	</div>\n</div>\n<hr />\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/insurance-requirement-form"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"control-group\">\n	<label for=\"minimum-coverage\" class=\"control-label\">Minimum Coverage</label>\n	<input id=\"minimum-coverage\" name=\"minimum-coverage\" type=\"text\" />\n</div>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/mandatory"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1;

  return "	<div class=\"mandatory-checkbox\">\n		<input type=\"checkbox\" checked=\""
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.enabled : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.enabled : depth0),{"name":"unless","hash":{},"fn":this.program(4, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\"/> Mandatory\n		<div class=\"muted mandatory-desc\">Worker must pass this requirement in order to apply</div>\n	</div>\n";
},"2":function(depth0,helpers,partials,data) {
    return "true";
},"4":function(depth0,helpers,partials,data) {
    return "false";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isMandatoryRequirement : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"useData":true});

this["wm"]["templates"]["requirementsets/ontime-requirement-form-tmpl"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"control-group\">\n	<label for=\"minimum-percentage\" class=\"control-label\">Input the minimum on-time rating (percentage)</label>\n	<div class=\"controls\">\n		<input id=\"minimum-percentage\" name=\"minimum-percentage\" type=\"text\" />\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/paid-requirement-form"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"control-group\">\n	<label for=\"minimum-assignments\" class=\"control-label\">Input the minimum number of paid assignments</label>\n	<div class=\"controls\">\n		<input id=\"minimum-assignments\" name=\"minimum-assignments\" type=\"text\" />\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/requirement-set-index"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "checked=\"checked\"";
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<a href=\"/groups/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.requirementSet : depth0)) != null ? stack1.groupId : stack1), depth0))
    + "/requirements\" aria-label=\"Edit\" class=\"tooltipped tooltipped-n\">\n			<i class=\"wm-icon-edit icon-large muted\"></i>\n		</a>\n";
},"5":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<a class=\"tooltipped tooltipped-n\" href=\"#edit/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.requirementSet : depth0)) != null ? stack1.id : stack1), depth0))
    + "\" aria-label=\"Edit\">\n			<i class=\"wm-icon-edit icon-large muted\"></i>\n		</a>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<td class=\"actions\">\n	<input type=\"checkbox\""
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.requirementSet : depth0)) != null ? stack1.active : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " data-action=\"power-on\"/>\n</td>\n<td class=\"name\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.requirementSet : depth0)) != null ? stack1.name : stack1), depth0))
    + "</td>\n<td class=\"is-required\">\n	<input type=\"checkbox\""
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.requirementSet : depth0)) != null ? stack1.required : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " data-action=\"require\"/>\n</td>\n<td class=\"creator\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.requirementSet : depth0)) != null ? stack1.creatorName : stack1), depth0))
    + "</td>\n<td class=\"actions\">\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.requirementSet : depth0)) != null ? stack1.groupId : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.requirementSet : depth0)) != null ? stack1.groupId : stack1),{"name":"unless","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</td>\n<td class=\"actions\">\n	<a class=\"tooltipped tooltipped-n\" aria-label=\"Delete\">\n		<i class=\"wm-icon-trash icon-large muted\" data-action=\"trash\"></i>\n	</a>\n</td>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/requirement-set-title-form"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return " checked=\"checked\"";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<form>\n	<label for=\"title\">Title:</label>\n	<input id=\"title\" name=\"name\" type=\"text\" class=\"title\" placeholder=\"New Requirement Set Name\" value=\""
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.requirementSet : depth0)) != null ? stack1.name : stack1), depth0))
    + "\" />\n	<label class=\"checkbox\" style=\"display: inline\">\n		<input name=\"required\" type=\"checkbox\" class=\"checkbox\" value=\"true\""
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.requirementSet : depth0)) != null ? stack1.required : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " />\n		Required\n	</label>\n	<a href=\"#\" class=\"button\" data-action=\"save\">Save</a>\n	<a href=\"#\" class=\"button\" data-action=\"cancel\">Cancel</a>\n</form>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/requirement-sets-index"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<table>\n	<thead>\n	<tr>\n		<th class=\"actions power\">Active</th>\n		<th class=\"name\">Requirement Set Name</th>\n		<th class=\"is-required\">\n			Required\n			<span class=\"tooltipped tooltipped-n\" aria-label=\"Required Requirement Sets are automatically added to every assignment.\"><i class=\"icon-question-sign\"></i></span>\n		</th>\n		<th class=\"creator\">Creator</th>\n		<th class=\"actions\">Edit</th>\n		<th class=\"actions\">Delete</th>\n	</tr>\n	</thead>\n	<tbody></tbody>\n</table>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/requirements-cart"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<h5>Current Requirements</h5>\n<div class=\"well-content\">\n	<div data-placeholder=\"cart\">\n	</div>\n	<small class=\"muted\"><em>* Mandatory</em></small>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/requirements-form"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					<option value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.name : stack1), depth0))
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.humanName : stack1), depth0))
    + "</option>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<h5>Add Requirements</h5>\n<div class=\"well-content\">\n	<div class=\"control-group\">\n		<label for=\"requirementTypes\" class=\"control-label\">Select the type of requirement to add</label>\n		<div class=\"controls\">\n			<select id=\"requirementTypes\" class=\"input-block-level\" data-toggle=\"form\">\n				<option class=\"prompt\">- Select -</option>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.requirementTypes : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</select>\n		</div>\n		<div data-placeholder=\"form\"></div>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/select"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "				<option value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.id : stack1), depth0))
    + "\" "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.disabled : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.name : stack1), depth0))
    + "</option>\n";
},"2":function(depth0,helpers,partials,data) {
    return "disabled";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div class=\"control-group\">\n	<label for=\""
    + alias2(alias1((depth0 != null ? depth0.lowerLabel : depth0), depth0))
    + "\" class=\"control-label\">"
    + alias2(alias1((depth0 != null ? depth0.label : depth0), depth0))
    + "</label>\n	<div class=\"controls\">\n		<select id=\""
    + alias2(alias1((depth0 != null ? depth0.lowerLabel : depth0), depth0))
    + "\" class=\"input-block-level\" data-selections=\""
    + alias2(alias1((depth0 != null ? depth0.selectionType : depth0), depth0))
    + "\">\n			<option class=\"prompt\">- Select -</option>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.collection : depth0)) != null ? stack1.models : stack1),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</select>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["requirementsets/travel-distance-requirement-form"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"control-group\">\n	<label for=\"distance\" class=\"control-label\">Distance</label>\n	<div class=\"controls\">\n		<input id=\"distance\" name=\"distance\" type=\"text\" />\n	</div>\n</div>\n<div class=\"control-group\">\n	<label for=\"address\" class=\"control-label\">Address</label>\n	<div class=\"controls\">\n		<input id=\"address\" name=\"address\" type=\"text\" />\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["search/bulk_group_modal"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda;

  return "		<div id=\""
    + this.escapeExpression(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" class=\"wm-modal--slide "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isActive : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n			<div class=\"wm-modal--close wm-icon-x\" data-modal-close></div>\n			<h1 class=\"wm-modal--header\">\n				<span class=\"wm-modal--title\">"
    + ((stack1 = alias1((depth0 != null ? depth0.title : depth0), depth0)) != null ? stack1 : "")
    + "</span>\n			</h1>\n			<div class=\"wm-modal--content\">\n				<small>If the group is public, the worker will receive an invitation via email with information regarding your group. If the group is private, the worker will be added immediately.</small>\n				<select class=\"bulk-group-select\" name=\"bulk-group-select\"></select>\n			</div>\n			<div class=\"wm-modal--footer\">\n				<button class=\"button -primary profile-card--invite-group bulk\">Send Invites</button>\n				<button class=\"button\" data-modal-close>Close</button>\n			</div>\n		</div>\n";
},"2":function(depth0,helpers,partials,data) {
    return "-active";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"wm-modal\" data-modal>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.slides : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true});

this["wm"]["templates"]["search/bulk_test_modal"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda;

  return "		<div id=\""
    + this.escapeExpression(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" class=\"wm-modal--slide "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isActive : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n			<div class=\"wm-modal--close wm-icon-x\" data-modal-close></div>\n			<h1 class=\"wm-modal--header\">\n				<span class=\"wm-modal--title\">"
    + ((stack1 = alias1((depth0 != null ? depth0.title : depth0), depth0)) != null ? stack1 : "")
    + "</span>\n			</h1>\n			<div class=\"wm-modal--content\" id=\"bulkTest\">\n				<small>The worker will receive a notification informing them of a pending test invitation including any instructions outlined in the tests description.</small>\n				<div>\n					<select class=\"bulk-test-select\" name=\"bulk-test-select\"></select>\n				</div>\n			</div>\n			<div class=\"wm-modal--footer\">\n				<button class=\"button -primary profile-card--invite-test bulk\">Send Invites</button>\n				<button class=\"button\" data-modal-close>Close</button>\n			</div>\n		</div>\n";
},"2":function(depth0,helpers,partials,data) {
    return "-active";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"wm-modal\" data-modal>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.slides : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true});

this["wm"]["templates"]["search/cart-row"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "<div><a href=\"/profile/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.attributes : depth0)) != null ? stack1.userName : stack1), depth0))
    + "</a></div>\n";
},"useData":true});

this["wm"]["templates"]["search/cart"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div>\n	<div class=\"selected-workers\">\n		<ul class=\"unstyled\"></ul>\n		<button class=\"button remove-all\">Remove All Workers</button>\n	</div>\n\n	<div class=\"results-meta row\">\n		<div class=\"text-center\">\n			<div class=\"pagination\">\n				<ul>\n					<li class=\"prev\"><a>&laquo; Previous</a></li>\n					<li class=\"status\"><span>Page <span class=\"current_page\">1</span> of <span class=\"num_pages\">1</span></span></li>\n					<li class=\"next\"><a>Next &raquo;</a></li>\n				</ul>\n			</div>\n		</div>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["search/drawer"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "	<button class=\"button -bulk-action download-documents\">Download Assets</button>\n	<button class=\"button -bulk-action export-as-csv\">Export CSV</button>\n	<button class=\"button -bulk-action remove-decline-users\">Remove/Decline</button>\n	<button class=\"button -bulk-action uninvite-users\">Uninvite</button>\n	<button class=\"button -bulk-action approve-users\">Approve</button>\n	<button class=\"button -bulk-action group-modal bulk\">Group Action</button>\n";
},"3":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isAssignment : depth0),{"name":"if","hash":{},"fn":this.program(4, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isVendor : depth0),{"name":"unless","hash":{},"fn":this.program(6, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"4":function(depth0,helpers,partials,data) {
    return "		<button class=\"button -bulk-action send-work bulk fadeIn animated\">Send Assignment</button>\n";
},"6":function(depth0,helpers,partials,data) {
    return "		<button class=\"button -bulk-action group-modal bulk fadeIn animated\">Group Action</button>\n		<button class=\"button -bulk-action test-modal bulk fadeIn animated\">Invite to Test</button>\n		<button class=\"button -bulk-action add-to-network bulk fadeIn animated\">Add to Network</button>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isGroupDetail : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isGroupDetail : depth0),{"name":"unless","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"useData":true});

this["wm"]["templates"]["search/no_results_message"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "			<li>"
    + this.escapeExpression(this.lambda(depth0, depth0))
    + "</li>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"alert\">\n	<p><strong>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.title : depth0), depth0))
    + "</strong></p>\n	<ul>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.messages : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		<li class=\"lastNoResultsMessage\">Looking for someone not yet on Work Market? <a href=\"/invitations\">Invite them today</a></li>\n	</ul>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["search/results_item"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return " -vendor";
},"3":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return " data-first-name=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.first_name : stack1), depth0))
    + "\" data-last-name=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.last_name : stack1), depth0))
    + "\" data-userNumber=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" ";
},"5":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return " data-company-name=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.company_name : stack1), depth0))
    + "\" data-companyNumber=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.companyNumber : stack1), depth0))
    + "\"";
},"7":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.avatar,depth0,{"name":"avatar","hash":{"src":(depth0 != null ? depth0.avatarAssetUri : depth0)},"data":data,"helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"9":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.avatar,depth0,{"name":"avatar","hash":{"hash":(depth0 != null ? depth0.orUserNumberOrCompanyNumber : depth0)},"data":data,"helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"11":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isCheckboxBlocked : depth0),{"name":"unless","hash":{},"fn":this.program(12, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"12":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.checkbox,depth0,{"name":"checkbox","hash":{"value":((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.userNumber : stack1),"name":"profile"},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"14":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing;

  return "				"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.derivedStatus : stack1),((stack1 = (depth0 != null ? depth0.memberStatus : depth0)) != null ? stack1.member : stack1),{"name":"eq","hash":{},"fn":this.program(15, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n				"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.derivedStatus : stack1),((stack1 = (depth0 != null ? depth0.memberStatus : depth0)) != null ? stack1.memberOverride : stack1),{"name":"eq","hash":{},"fn":this.program(17, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n				"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.derivedStatus : stack1),((stack1 = (depth0 != null ? depth0.memberStatus : depth0)) != null ? stack1.pending : stack1),{"name":"eq","hash":{},"fn":this.program(19, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n				"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.derivedStatus : stack1),((stack1 = (depth0 != null ? depth0.memberStatus : depth0)) != null ? stack1.pendingFailed : stack1),{"name":"eq","hash":{},"fn":this.program(21, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n				"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.derivedStatus : stack1),((stack1 = (depth0 != null ? depth0.memberStatus : depth0)) != null ? stack1.invited : stack1),{"name":"eq","hash":{},"fn":this.program(23, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n				"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.derivedStatus : stack1),((stack1 = (depth0 != null ? depth0.memberStatus : depth0)) != null ? stack1.declined : stack1),{"name":"eq","hash":{},"fn":this.program(25, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"15":function(depth0,helpers,partials,data) {
    return "<span class=\"wm-status-label member\">MEMBER</span>";
},"17":function(depth0,helpers,partials,data) {
    return "<span class=\"wm-status-label pending\">MEMBER OVERRIDE</span>";
},"19":function(depth0,helpers,partials,data) {
    return "<span class=\"wm-status-label pending\">PENDING</span>";
},"21":function(depth0,helpers,partials,data) {
    return "<span class=\"wm-status-label pending\">PENDING OVERRIDE</span>";
},"23":function(depth0,helpers,partials,data) {
    return "<span class=\"wm-status-label invited\">INVITED</span>";
},"25":function(depth0,helpers,partials,data) {
    return "<span class=\"wm-status-label declined\">DECLINED</span>";
},"27":function(depth0,helpers,partials,data) {
    return "<span class=\"wm-status-label declined\">BLOCKED</span>";
},"29":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.assessmentStatus : stack1),((stack1 = (depth0 != null ? depth0.assessmentStatuses : depth0)) != null ? stack1.invited : stack1),{"name":"eq","hash":{},"fn":this.program(30, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"30":function(depth0,helpers,partials,data) {
    return "				<div class=\"status\"><span class=\"wm-status-label invited\">INVITED</span></div>\n";
},"32":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<div class=\"status\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isExistingWorker : depth0),{"name":"if","hash":{},"fn":this.program(33, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n				"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isInvitedVendor : depth0),{"name":"if","hash":{},"fn":this.program(36, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n				"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isDeclinedVendor : depth0),{"name":"if","hash":{},"fn":this.program(38, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			</div>\n";
},"33":function(depth0,helpers,partials,data) {
    var stack1;

  return "					"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isAppliedWorker : depth0),{"name":"if","hash":{},"fn":this.program(34, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n					"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isAppliedWorker : depth0),{"name":"unless","hash":{},"fn":this.program(36, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n					"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isDeclinedWorker : depth0),{"name":"if","hash":{},"fn":this.program(38, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"34":function(depth0,helpers,partials,data) {
    return "<span class=\"wm-status-label pending\">Applied</span>";
},"36":function(depth0,helpers,partials,data) {
    return "<span class=\"wm-status-label invited\">Invited</span>";
},"38":function(depth0,helpers,partials,data) {
    return "<span class=\"wm-status-label declined\">Declined</span>";
},"40":function(depth0,helpers,partials,data) {
    return "<span class=\"profile-card--name\">"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.company_name : depth0), depth0))
    + "</span>";
},"42":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "				<a href=\"/profile/"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" class=\"profile-card--name open-user-profile-popup\" data-user-number=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" data-number=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.first_name : stack1), depth0))
    + " "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.last_name : stack1), depth0))
    + "</a>\n";
},"44":function(depth0,helpers,partials,data) {
    return "<span class=\"profile-card--badge tooltipped tooltipped-n\" aria-label=\"Employee\">E</span>";
},"46":function(depth0,helpers,partials,data) {
    return "<span class=\"profile-card--badge tooltipped tooltipped-n\" aria-label=\"Invited Contractor\">C</span>";
},"48":function(depth0,helpers,partials,data) {
    return "<span class=\"profile-card--badge tooltipped tooltipped-n\" aria-label=\"Third Party Contractor\">3</span>";
},"50":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing;

  return ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.mbo_status : stack1),"NORMAL",{"name":"eq","hash":{},"fn":this.program(51, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.mbo_status : stack1),"PREREGISTERED",{"name":"eq","hash":{},"fn":this.program(53, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"51":function(depth0,helpers,partials,data) {
    return "					<a href=\"http://www.mboenterprise.com/how-we-help\" target=\"_blank\"><img class=\"tooltipped tooltipped-n bkchk\" aria-label=\"This user is an MBO Associate\" src=\"${mediaPrefix}/images/mbo.png\"/></a>\n";
},"53":function(depth0,helpers,partials,data) {
    return "					<a href=\"http://www.mboenterprise.com/how-we-help\" target=\"_blank\"><img class=\"tooltipped tooltipped-n bkchk\" aria-label=\"This user is pre-registered with MBO Partners\" src=\"${mediaPrefix}/images/mbo-gray.png\"/></a>\n";
},"55":function(depth0,helpers,partials,data) {
    var stack1;

  return this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.companyNumber : stack1), depth0));
},"57":function(depth0,helpers,partials,data) {
    var stack1;

  return this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.userNumber : stack1), depth0));
},"59":function(depth0,helpers,partials,data) {
    return "<li>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.job_title : depth0), depth0))
    + "</li>";
},"61":function(depth0,helpers,partials,data) {
    var stack1;

  return "<li>"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.company_name : stack1), depth0))
    + "</li>";
},"63":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.city : stack1), depth0))
    + ", "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.state : stack1), depth0))
    + " "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.postal_code : stack1), depth0))
    + "\n";
},"65":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.postal_code : stack1), depth0))
    + " "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.city : stack1), depth0))
    + " "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.country : stack1), depth0))
    + "\n";
},"67":function(depth0,helpers,partials,data) {
    return this.escapeExpression(this.lambda((depth0 != null ? depth0.distance : depth0), depth0))
    + " miles";
},"69":function(depth0,helpers,partials,data) {
    return this.escapeExpression(this.lambda((depth0 != null ? depth0.email : depth0), depth0));
},"71":function(depth0,helpers,partials,data) {
    return "<li><span>Member since</span> <small>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.created_on : depth0), depth0))
    + "</small></li>";
},"73":function(depth0,helpers,partials,data) {
    return "				<li>\n					<span class=\"passed\"><i class=\"wm-icon-checkmark\"></i> PASSED</span>\n					<small>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.background_check_date : depth0), depth0))
    + "</small>\n				</li>\n";
},"75":function(depth0,helpers,partials,data) {
    return "				<li>\n					<span class=\"failed\"><i class=\"wm-icon-x\"></i> ALERT</span>\n					<small>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.background_check_date : depth0), depth0))
    + "</small>\n				</li>\n";
},"77":function(depth0,helpers,partials,data) {
    return "				<li>\n					<span class=\"passed\"><i class=\"wm-icon-pill\"></i> PASSED</span>\n					<small>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.drug_test_date : depth0), depth0))
    + "</small>\n				</li>\n";
},"79":function(depth0,helpers,partials,data) {
    return "				<li>\n					<span class=\"failed\"><i class=\"wm-icon-pill\"></i> FAILED</span>\n					<small>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.drug_test_date : depth0), depth0))
    + "</small>\n				</li>\n";
},"81":function(depth0,helpers,partials,data) {
    return "<li><span>Team Size</span> <small>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.publicWorkers : depth0), depth0))
    + "</small></li>";
},"83":function(depth0,helpers,partials,data) {
    return "<li><span>Last Work</span> <small>"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.lastWork : depth0), depth0))
    + "</small></li>";
},"85":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.groups : depth0)) != null ? stack1.length : stack1),{"name":"if","hash":{},"fn":this.program(86, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"86":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					<div class=\"card-data\">\n						<span>Groups ("
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.groups : depth0)) != null ? stack1.length : stack1), depth0))
    + "): </span>\n						<span>"
    + alias2(alias1((depth0 != null ? depth0.firstGroup : depth0), depth0))
    + "</span>\n					</div>\n";
},"88":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.company_assessments : depth0)) != null ? stack1.length : stack1),{"name":"if","hash":{},"fn":this.program(89, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"89":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					<div class=\"card-data\">\n						<span>Tests ("
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.company_assessments : depth0)) != null ? stack1.length : stack1), depth0))
    + "): </span>\n						<span>"
    + alias2(alias1((depth0 != null ? depth0.firstCompanyAssessment : depth0), depth0))
    + "</span>\n					</div>\n";
},"91":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.certifications : depth0)) != null ? stack1.length : stack1),{"name":"if","hash":{},"fn":this.program(92, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"92":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "					<div class=\"card-data\">\n						<span>Certifications ("
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.certifications : depth0)) != null ? stack1.length : stack1), depth0))
    + "): </span>\n						<span>"
    + alias2(alias1((depth0 != null ? depth0.firstCertifications : depth0), depth0))
    + "</span>\n					</div>\n";
},"94":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.licenses : depth0)) != null ? stack1.length : stack1),{"name":"if","hash":{},"fn":this.program(95, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"95":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "				<div class=\"card-data\">\n					<span>Licenses ("
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.licenses : depth0)) != null ? stack1.length : stack1), depth0))
    + "): </span>\n					<span>"
    + alias2(alias1((depth0 != null ? depth0.firstLicenses : depth0), depth0))
    + "</span>\n				</div>\n";
},"97":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<div class=\"profile-card--shade\">\n			<div class=\"profile-card--view-profile wm-icon-eye\" data-user-number=\""
    + alias2(alias1((depth0 != null ? depth0.orUserNumberOrCompanyNumber : depth0), depth0))
    + "\">View Profile</div>\n			<div class=\"profile-card--actions\">\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isDispatch : depth0),{"name":"if","hash":{},"fn":this.program(98, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isDispatch : depth0),{"name":"unless","hash":{},"fn":this.program(112, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</div>\n			<div class=\"profile-card--drawer\">\n				<div class=\"profile-card--missingreqs\">\n					<small class=\"missing-reqs--template\"></small>\n					<button class=\"button profile-card--close\">Close</button>\n				</div>\n				<div class=\"profile-card--group\">\n					<small>If the group is public, the worker will receive an invitation via email with information regarding your group. If the group is private, the worker will be added immediately.</small>\n					<label>\n						Select Group:\n						<select class=\"wm-select group-select\"></select>\n					</label>\n					<button class=\"button -primary profile-card--invite-group profile-card--close\" data-user-number=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">Invite to Group</button>\n					<button class=\"button profile-card--close\">Close</button>\n				</div>\n				<div class=\"profile-card--test\">\n					<small>The worker will receive a notification informing them of a pending test invitation including any instructions outlined in the tests description.</small>\n					<label>\n						Select Test:\n						<select class=\"wm-select test-select\"></select>\n					</label>\n					<button class=\"button -primary profile-card--invite-test profile-card--close\" data-user-number=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">Invite to Test</button>\n					<button class=\"button profile-card--close\">Close</button>\n				</div>\n				<div class=\"profile-card--comment\" data-float-label=\"Add Comment\">\n					<textarea placeholder=\"Add Comment here...\"></textarea>\n					<button class=\"button -primary profile-card--add-comment profile-card--close\" data-user-number=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">Add Comment</button>\n					<button class=\"button profile-card--close\">Close</button>\n				</div>\n			</div>\n		</div>\n";
},"98":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.assignToFirstWorker : depth0),{"name":"if","hash":{},"fn":this.program(99, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.assignToFirstWorker : depth0),{"name":"unless","hash":{},"fn":this.program(104, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"99":function(depth0,helpers,partials,data) {
    var stack1;

  return "						<button\n							"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.eligibility : depth0)) != null ? stack1.eligible : stack1),{"name":"unless","hash":{},"fn":this.program(100, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n							"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.eligibility : depth0)) != null ? stack1.eligible : stack1),{"name":"if","hash":{},"fn":this.program(102, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n								data-usernumber=\""
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\"\n								href=\"javascript:void(0);\">Accept\n						</button>\n";
},"100":function(depth0,helpers,partials,data) {
    return " class=\"button -toggle dispatch-accept tooltipped tooltipped-n\" aria-label=\"Worker must meet all requirements\" disabled";
},"102":function(depth0,helpers,partials,data) {
    return "class=\"button dispatch-accept -toggle\"";
},"104":function(depth0,helpers,partials,data) {
    var stack1;

  return "						<button\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.isAppliedWorker : depth0),true,{"name":"eq","hash":{},"fn":this.program(105, data, 0),"inverse":this.program(107, data, 0),"data":data})) != null ? stack1 : "")
    + "								data-usernumber=\""
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">Apply\n						</button>\n";
},"105":function(depth0,helpers,partials,data) {
    return "								class=\"button -toggle dispatch-apply tooltipped tooltipped-n\"\n								disabled\n								aria-label=\"Worker has already applied\"\n";
},"107":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.eligibility : depth0)) != null ? stack1.eligible : stack1),{"name":"unless","hash":{},"fn":this.program(108, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "								"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.eligibility : depth0)) != null ? stack1.eligible : stack1),{"name":"if","hash":{},"fn":this.program(110, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n";
},"108":function(depth0,helpers,partials,data) {
    return "									class=\"button -toggle dispatch-apply tooltipped tooltipped-n\"\n									disabled\n";
},"110":function(depth0,helpers,partials,data) {
    return "class=\"button -toggle dispatch-apply\"";
},"112":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing;

  return ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.mode : depth0),((stack1 = (depth0 != null ? depth0.searchModes : depth0)) != null ? stack1.groupDetail : stack1),{"name":"eq","hash":{},"fn":this.program(113, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isVendor : depth0),{"name":"unless","hash":{},"fn":this.program(116, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.lane : stack1),4,{"name":"eq","hash":{},"fn":this.program(118, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.lane : stack1),3,{"name":"eq","hash":{},"fn":this.program(120, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"113":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = (helpers.eq || (depth0 && depth0.eq) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.derivedStatus : stack1),((stack1 = (depth0 != null ? depth0.memberStatus : depth0)) != null ? stack1.pendingFailed : stack1),{"name":"eq","hash":{},"fn":this.program(114, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"114":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials['switch'],depth0,{"name":"switch","hash":{"tooltip":"Show Missing Group Requirements","value":"missingreqs","text":"<i class='wm-icon-x'></i>","isUnique":"true","classlist":"profile-card--action missingreqs-quick-action tooltipped tooltipped-n","name":"profile-card-action"},"data":data,"indent":"\t\t\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"116":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials['switch'],depth0,{"name":"switch","hash":{"tooltip":"Send Assignment","text":"<i class='wm-icon-page-out'></i>","classlist":"profile-card--action send-assignment tooltipped tooltipped-n","name":"profile-card-action"},"data":data,"indent":"\t\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials['switch'],depth0,{"name":"switch","hash":{"tooltip":"Invite to Group","value":"group","text":"<i class='wm-icon-users'></i>","isUnique":"true","classlist":"profile-card--action groups-quick-action tooltipped tooltipped-n","name":"profile-card-action"},"data":data,"indent":"\t\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials['switch'],depth0,{"name":"switch","hash":{"tooltip":"Invite to Test","value":"test","text":"<i class='wm-icon-test'></i>","isUnique":"true","classlist":"profile-card--action tests-quick-action tooltipped tooltipped-n","name":"profile-card-action"},"data":data,"indent":"\t\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + ((stack1 = this.invokePartial(partials['switch'],depth0,{"name":"switch","hash":{"tooltip":"Add Comments","value":"comment","text":"<i class='wm-icon-speech'></i>","isUnique":"true","classlist":"profile-card--action tooltipped tooltipped-n","name":"profile-card-action"},"data":data,"indent":"\t\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"118":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials['switch'],depth0,{"name":"switch","hash":{"attributes":(depth0 != null ? depth0.userNumber : depth0),"tooltip":"Add To Network","value":"comment","text":"<i class='wm-icon-plus'></i>","isUnique":"true","classlist":"add-to-network tooltipped tooltipped-n","name":"profile-card-action"},"data":data,"indent":"\t\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"120":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials['switch'],depth0,{"name":"switch","hash":{"attributes":(depth0 != null ? depth0.userNumber : depth0),"tooltip":"Remove From Network","value":"comment","text":"<i class='wm-icon-checkmark-circle'></i>","isUnique":"true","classlist":"remove-from-network tooltipped tooltipped-n","name":"profile-card-action"},"data":data,"indent":"\t\t\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"122":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.eligibility : depth0)) != null ? stack1.criteria : stack1)) != null ? stack1.length : stack1),{"name":"if","hash":{},"fn":this.program(123, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"123":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<h2 class=\"profile-card--header\">\n					Assignment Eligibility Requirements\n				</h2>\n				<ul class=\"profile-card--eligibility\">\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.eligibility : depth0)) != null ? stack1.criteria : stack1),{"name":"each","hash":{},"fn":this.program(124, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</ul>\n";
},"124":function(depth0,helpers,partials,data) {
    var stack1;

  return "					<li>\n						<span class=\""
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.met : depth0),{"name":"if","hash":{},"fn":this.program(125, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.met : depth0),{"name":"unless","hash":{},"fn":this.program(127, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " pull-left\">\n							<i class=\"wm-icon"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.met : depth0),{"name":"if","hash":{},"fn":this.program(129, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.met : depth0),{"name":"unless","hash":{},"fn":this.program(131, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\"></i>\n						</span>\n						<span class=\"profile-card--eligibility-item\">"
    + this.escapeExpression(this.lambda((depth0 != null ? depth0.name : depth0), depth0))
    + "</span>\n					</li>\n";
},"125":function(depth0,helpers,partials,data) {
    return "passed";
},"127":function(depth0,helpers,partials,data) {
    return "failed";
},"129":function(depth0,helpers,partials,data) {
    return "-checkmark";
},"131":function(depth0,helpers,partials,data) {
    return "-x";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing;

  return "<div class=\"profile-card"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isVendor : depth0),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n	<div class=\"profile-card--photo\" "
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isVendor : depth0),{"name":"unless","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isVendor : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">\n		"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.avatarAssetUri : depth0),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n		"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.avatarAssetUri : depth0),{"name":"unless","hash":{},"fn":this.program(9, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isDispatch : depth0),{"name":"unless","hash":{},"fn":this.program(11, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n		<div class=\"status\">\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.mode : depth0),((stack1 = (depth0 != null ? depth0.searchModes : depth0)) != null ? stack1.groupDetail : stack1),{"name":"eq","hash":{},"fn":this.program(14, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.blocked : stack1),{"name":"if","hash":{},"fn":this.program(27, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n		</div>\n\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.mode : depth0),((stack1 = (depth0 != null ? depth0.searchModes : depth0)) != null ? stack1.assessment : stack1),{"name":"eq","hash":{},"fn":this.program(29, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,(depth0 != null ? depth0.mode : depth0),((stack1 = (depth0 != null ? depth0.searchModes : depth0)) != null ? stack1.assignment : stack1),{"name":"eq","hash":{},"fn":this.program(32, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</div>\n	<div class=\"profile-card--details\">\n		<h2 class=\"profile-card--header\">\n			"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isVendor : depth0),{"name":"if","hash":{},"fn":this.program(40, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isVendor : depth0),{"name":"unless","hash":{},"fn":this.program(42, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.lane : stack1),0,{"name":"eq","hash":{},"fn":this.program(44, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.lane : stack1),1,{"name":"eq","hash":{},"fn":this.program(44, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.lane : stack1),2,{"name":"eq","hash":{},"fn":this.program(46, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			"
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.lane : stack1),3,{"name":"eq","hash":{},"fn":this.program(48, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.mbo : stack1),{"name":"if","hash":{},"fn":this.program(50, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			<small>ID: "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isVendor : depth0),{"name":"if","hash":{},"fn":this.program(55, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isVendor : depth0),{"name":"unless","hash":{},"fn":this.program(57, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</small>\n		</h2>\n"
    + ((stack1 = this.invokePartial(partials['search-score-card'],depth0,{"name":"search-score-card","hash":{"abandoned":((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.abandoned_count : stack1),"paidassignforcompany":((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.work_completed_company_count : stack1),"cancelled":((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.work_cancelled_count : stack1),"paidassign":((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.work_completed_count : stack1),"deliverables":((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.deliverable_on_time_reliability : stack1),"ontime":((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.ontime_reliability : stack1),"satisfaction":((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.rating : stack1),"score":((stack1 = (depth0 != null ? depth0.user : depth0)) != null ? stack1.score : stack1),"isDispatch":(depth0 != null ? depth0.isDispatch : depth0)},"data":data,"indent":"\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "		<ul class=\"profile-card--address\">\n			"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.job_title : depth0),{"name":"if","hash":{},"fn":this.program(59, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isVendor : depth0),{"name":"unless","hash":{},"fn":this.program(61, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			<li>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isUsa : depth0),{"name":"if","hash":{},"fn":this.program(63, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isUsa : depth0),{"name":"unless","hash":{},"fn":this.program(65, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.distance : depth0),{"name":"if","hash":{},"fn":this.program(67, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			</li>\n			<li>"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isValidEmail : depth0),{"name":"if","hash":{},"fn":this.program(69, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</li>\n		</ul>\n		<ul class=\"profile-card--secondary\">\n			"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.isVendor : depth0),{"name":"unless","hash":{},"fn":this.program(71, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.background_check : depth0),{"name":"if","hash":{},"fn":this.program(73, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.background_check_failed : depth0),{"name":"if","hash":{},"fn":this.program(75, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.drug_test : depth0),{"name":"if","hash":{},"fn":this.program(77, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.drug_test_failed : depth0),{"name":"if","hash":{},"fn":this.program(79, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</ul>\n\n		<ul class=\"profile-card--tests\">\n			"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.publicWorkers : depth0),{"name":"if","hash":{},"fn":this.program(81, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.lastWork : depth0),{"name":"if","hash":{},"fn":this.program(83, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isVendor : depth0),{"name":"if","hash":{},"fn":this.program(71, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.groups : depth0),{"name":"if","hash":{},"fn":this.program(85, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.company_assessments : depth0),{"name":"if","hash":{},"fn":this.program(88, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.certifications : depth0),{"name":"if","hash":{},"fn":this.program(91, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.licenses : depth0),{"name":"if","hash":{},"fn":this.program(94, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</ul>\n	</div>\n"
    + ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.disableActions : depth0),{"name":"unless","hash":{},"fn":this.program(97, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n	<div class=\"profile-card--eligibility-requirements\">\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.eligibility : depth0)) != null ? stack1.criteria : stack1),{"name":"if","hash":{},"fn":this.program(122, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</div>\n</div>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["search/user_profile_modal_container"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<div id=\""
    + alias2(alias1((depth0 != null ? depth0.id : depth0), depth0))
    + "\" class=\"wm-modal--slide "
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isActive : depth0),{"name":"if","hash":{},"fn":this.program(2, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\">\n			<a href=\"#\" class=\"user-profile--arrow previous-profile wm-icon-left-arrow-large\"></a>\n			<a href=\"#\" class=\"user-profile--arrow next-profile wm-icon-right-arrow-large\"></a>\n			<div class=\"wm-modal--close wm-icon-x\" data-modal-close></div>\n			<h1 class=\"wm-modal--header\">\n				<span class=\"wm-modal--title\"><a href='"
    + alias2(alias1((depth0 != null ? depth0.title : depth0), depth0))
    + "'>View Full Profile</a></span>\n			</h1>\n			<div class=\"wm-modal--content profile\"></div>\n		</div>\n";
},"2":function(depth0,helpers,partials,data) {
    return "-active";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"wm-modal user-profile-popup user-profile\" data-modal>\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.slides : depth0),{"name":"each","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n";
},"useData":true});

this["wm"]["templates"]["search/user_profile_modal_content"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "		<h4>\n			This user is currently suspended.\n		</h4>\n";
},"3":function(depth0,helpers,partials,data) {
    return "		<h4>\n			This user is currently deactivated.\n		</h4>\n";
},"5":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "		<h4 class=\"tac\">Your company has blocked "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.firstName : stack1), depth0))
    + " "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.lastName : stack1), depth0))
    + ". Please contact your administrator for more information.</h4>\n";
},"7":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.avatar,depth0,{"name":"avatar","hash":{"src":((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.avatarLargeAssetUri : stack1)},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"9":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.avatar,depth0,{"name":"avatar","hash":{"hash":((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1)},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"11":function(depth0,helpers,partials,data) {
    var stack1;

  return "				"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.jobTitle : stack1), depth0))
    + " |\n";
},"13":function(depth0,helpers,partials,data) {
    var stack1;

  return "				"
    + this.escapeExpression(this.lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.address : stack1)) != null ? stack1.shortAddress : stack1), depth0))
    + "\n";
},"15":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyAddress : stack1),{"name":"if","hash":{},"fn":this.program(16, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"16":function(depth0,helpers,partials,data) {
    var stack1;

  return "					"
    + this.escapeExpression(this.lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyAddress : stack1)) != null ? stack1.shortAddress : stack1), depth0))
    + "\n";
},"18":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2=this.escapeExpression;

  return "		<div class=\"profile--contact\">\n\n			<a class=\"user-contact--email\" href=\"mailto:"
    + alias2(((helper = (helper = helpers.email || (depth0 != null ? depth0.email : depth0)) != null ? helper : alias1),(typeof helper === "function" ? helper.call(depth0,{"name":"email","hash":{},"data":data}) : helper)))
    + "\">\n				<i class=\"icon-envelope\"></i>\n				"
    + alias2(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.email : stack1), depth0))
    + "\n			</a>\n\n			"
    + ((stack1 = (helpers.showPhoneNumbers || (depth0 && depth0.showPhoneNumbers) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.laneType : stack1),(depth0 != null ? depth0.isInternal : depth0),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.workPhone : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.workPhoneExtension : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.mobilePhone : stack1),{"name":"showPhoneNumbers","hash":{},"data":data})) != null ? stack1 : "")
    + "\n		</div>\n";
},"20":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "			<li class=\"wm-tab comments-tab\" data-content=\"#user-comments-"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">Comments</li>\n			<li class=\"wm-tab\" data-content=\"#user-tags-"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" data-badge=\""
    + alias2((helpers.resourceCount || (depth0 && depth0.resourceCount) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.privateTags : stack1),{"name":"resourceCount","hash":{},"data":data}))
    + "\">Tags</li>\n";
},"22":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.escapeExpression;

  return "			<li class=\"wm-tab\" data-content=\"#user-media-"
    + alias1(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" data-badge=\""
    + alias1((helpers.resourceCount || (depth0 && depth0.resourceCount) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.ImageOutput : depth0),{"name":"resourceCount","hash":{},"data":data}))
    + "\">Media</li>\n";
},"24":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<p>"
    + ((stack1 = (helpers.laneTypeBadge || (depth0 && depth0.laneTypeBadge) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.laneType : stack1),true,{"name":"laneTypeBadge","hash":{},"data":data})) != null ? stack1 : "")
    + "</p>\n";
},"26":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing, alias2=this.escapeExpression;

  return "				<p class=\"user-drug-test\">\n					<img class=\"test-status-image\" src=\""
    + alias2((helpers.drugTestStatusImage || (depth0 && depth0.drugTestStatusImage) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.drugTestStatus : stack1),{"name":"drugTestStatusImage","hash":{},"data":data}))
    + "\"/>\n					"
    + alias2((helpers.drugTestResult || (depth0 && depth0.drugTestResult) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.drugTestStatus : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.priorPassedDrugTest : stack1),{"name":"drugTestResult","hash":{},"data":data}))
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.lastBackgroundCheckResponseDate : stack1),{"name":"if","hash":{},"fn":this.program(27, data, 0),"inverse":this.program(29, data, 0),"data":data})) != null ? stack1 : "")
    + "					"
    + ((stack1 = (helpers.drugTestActions || (depth0 && depth0.drugTestActions) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.owner : stack1),(depth0 != null ? depth0.isInternal : depth0),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.drugTestStatus : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.priorPassedDrugTest : stack1),{"name":"drugTestActions","hash":{},"data":data})) != null ? stack1 : "")
    + "\n				</p>\n";
},"27":function(depth0,helpers,partials,data) {
    var stack1;

  return "						<small>"
    + this.escapeExpression((helpers.formatDate || (depth0 && depth0.formatDate) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.lastDrugTestResponseDate : stack1),{"name":"formatDate","hash":{},"data":data}))
    + "</small>\n";
},"29":function(depth0,helpers,partials,data) {
    var stack1;

  return "						<small>"
    + this.escapeExpression((helpers.formatDate || (depth0 && depth0.formatDate) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.lastDrugTestRequestDate : stack1),{"name":"formatDate","hash":{},"data":data}))
    + "</small>\n";
},"31":function(depth0,helpers,partials,data) {
    var stack1, alias1=helpers.helperMissing, alias2=this.escapeExpression;

  return "				<p class=\"user-background-check\">\n					<img class=\"test-status-image\" src=\""
    + alias2((helpers.backgroundCheckStatusImage || (depth0 && depth0.backgroundCheckStatusImage) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.backgroundCheckStatus : stack1),{"name":"backgroundCheckStatusImage","hash":{},"data":data}))
    + "\"/>\n					"
    + alias2((helpers.backgroundCheckResult || (depth0 && depth0.backgroundCheckResult) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.backgroundCheckStatus : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.priorPassedBackgroundCheck : stack1),{"name":"backgroundCheckResult","hash":{},"data":data}))
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.lastBackgroundCheckResponseDate : stack1),{"name":"if","hash":{},"fn":this.program(32, data, 0),"inverse":this.program(34, data, 0),"data":data})) != null ? stack1 : "")
    + "					"
    + ((stack1 = (helpers.backgroundCheckActions || (depth0 && depth0.backgroundCheckActions) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.owner : stack1),(depth0 != null ? depth0.isInternal : depth0),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.backgroundCheckStatus : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.priorPassedBackgroundCheck : stack1),{"name":"backgroundCheckActions","hash":{},"data":data})) != null ? stack1 : "")
    + "\n				</p>\n";
},"32":function(depth0,helpers,partials,data) {
    var stack1;

  return "						<small>"
    + this.escapeExpression((helpers.formatDate || (depth0 && depth0.formatDate) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.lastBackgroundCheckResponseDate : stack1),{"name":"formatDate","hash":{},"data":data}))
    + "</small>\n";
},"34":function(depth0,helpers,partials,data) {
    var stack1;

  return "						<small>"
    + this.escapeExpression((helpers.formatDate || (depth0 && depth0.formatDate) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.lastBackgroundCheckRequestDate : stack1),{"name":"formatDate","hash":{},"data":data}))
    + "</small>\n";
},"36":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<p>"
    + ((stack1 = (helpers.mboStatus || (depth0 && depth0.mboStatus) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.mboStatus : stack1),{"name":"mboStatus","hash":{},"data":data})) != null ? stack1 : "")
    + "</p>\n";
},"38":function(depth0,helpers,partials,data) {
    return "				<p>Bank Account: <b>Confirmed</b></p>\n";
},"40":function(depth0,helpers,partials,data) {
    return "				<p>Bank Account: <b>Unconfirmed</b></p>\n";
},"42":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<p>\n					<i class=\"icon-linkedin icon-gray\"></i>\n					<a target=\"_blank\" href=\""
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.linkedInPublicProfileUrl : stack1), depth0))
    + "\">LinkedIn</a>\n				</p>\n";
},"44":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.escapeExpression;

  return "				<p>\n					<i class=\"wm-icon-globe-circle\"></i>\n					<a target=\"_blank\" href=\""
    + alias1((helpers.ensureProtocol || (depth0 && depth0.ensureProtocol) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyWebsite : stack1),{"name":"ensureProtocol","hash":{},"data":data}))
    + "\">"
    + alias1(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyWebsite : stack1), depth0))
    + "</a>\n				</p>\n";
},"46":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<p class=\"overview-description\">"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.overview : stack1), depth0))
    + "</p>\n";
},"48":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<p class=\"overview-description\">"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyOverview : stack1), depth0))
    + "</p>\n";
},"50":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2=this.escapeExpression;

  return "			<h2>Groups <span class=\"pull-right wm-badge "
    + alias2((helpers.activeBadge || (depth0 && depth0.activeBadge) || alias1).call(depth0,(depth0 != null ? depth0.totalGroups : depth0),{"name":"activeBadge","hash":{},"data":data}))
    + "\">"
    + alias2(((helper = (helper = helpers.totalGroups || (depth0 != null ? depth0.totalGroups : depth0)) != null ? helper : alias1),(typeof helper === "function" ? helper.call(depth0,{"name":"totalGroups","hash":{},"data":data}) : helper)))
    + "</span></h2>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.publicGroups : stack1),{"name":"if","hash":{},"fn":this.program(51, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.privateGroups : stack1),{"name":"if","hash":{},"fn":this.program(54, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.doesNotBelongToAnyGroup || (depth0 && depth0.doesNotBelongToAnyGroup) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.publicGroups : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.privateGroups : stack1),{"name":"doesNotBelongToAnyGroup","hash":{},"fn":this.program(56, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"51":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<div class=\"user-public-groups\">\n					<p>Your Public Groups:</p>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.publicGroups : stack1),{"name":"each","hash":{},"fn":this.program(52, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</div>\n";
},"52":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "						<a class=\"wm-icon-users user-group\" href=\"/groups/"
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\">"
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "</a>\n";
},"54":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<div class=\"user-private-groups\">\n					<p>Your Private Groups:</p>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.privateGroups : stack1),{"name":"each","hash":{},"fn":this.program(52, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</div>\n";
},"56":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.owner : stack1),{"name":"if","hash":{},"fn":this.program(57, data, 0),"inverse":this.program(59, data, 0),"data":data})) != null ? stack1 : "");
},"57":function(depth0,helpers,partials,data) {
    return "					<p>You are not a member of any groups. Join groups to be eligible for more assignments.</p>\n					<a href=\"/search-groups\" class=\"button small\">Join groups</a>\n";
},"59":function(depth0,helpers,partials,data) {
    return "					<p>This worker is not a member of any of your groups.</p>\n";
},"61":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<table class=\"user--tests-table\">\n				<tbody>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.assessments : stack1),{"name":"each","hash":{},"fn":this.program(62, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</tbody>\n			</table>\n";
},"62":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "						<tr>\n							<td><a href=\"/lms/grade/"
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "/"
    + alias3(((helper = (helper = helpers.secondaryId || (depth0 != null ? depth0.secondaryId : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"secondaryId","hash":{},"data":data}) : helper)))
    + "\">"
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "</a></td>\n							<td>"
    + alias3((helpers.testStatus || (depth0 && depth0.testStatus) || alias1).call(depth0,(depth0 != null ? depth0.verificationStatus : depth0),{"name":"testStatus","hash":{},"data":data}))
    + "</td>\n						</tr>\n";
},"64":function(depth0,helpers,partials,data) {
    return "			<p>This worker has not completed any of your tests or surveys.</p>\n";
},"66":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression;

  return "			<h2>\n				Roles\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.isCompanyAdmin : depth0),{"name":"if","hash":{},"fn":this.program(67, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</h2>\n\n			<p>\n				<strong>Roles:</strong> "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.roleNames : stack1), depth0))
    + "\n			</p>\n			<p>\n				<strong>Role Types:</strong> "
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.laneAccess : stack1), depth0))
    + "\n			</p>\n";
},"67":function(depth0,helpers,partials,data) {
    var stack1;

  return "					<small><a href=\"/users/edit_user/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">Edit</a></small>\n";
},"69":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<article class=\"user-hourly-rates\">\n				<h2>Hourly Rates</h2>\n				<div>\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.minOnsiteHourlyRate : stack1),{"name":"if","hash":{},"fn":this.program(70, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.minOffsiteHourlyRate : stack1),{"name":"if","hash":{},"fn":this.program(72, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</div>\n			</article>\n";
},"70":function(depth0,helpers,partials,data) {
    var stack1;

  return "						<p>On-Site: <b>"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.minOnsiteHourlyRate : stack1), depth0))
    + "</b></p>\n";
},"72":function(depth0,helpers,partials,data) {
    var stack1;

  return "						<p>Off-Site: <b>"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.minOffsiteHourlyRate : stack1), depth0))
    + "</b></p>\n";
},"74":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<h2>Skills</h2>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.skills : stack1),{"name":"each","hash":{},"fn":this.program(75, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"75":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "				<a class=\"wm-tag\" href=\"/search?keyword="
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "\">"
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "</a>\n";
},"77":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<h2>Products</h2>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.specialties : stack1),{"name":"each","hash":{},"fn":this.program(75, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"79":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<h2>Tools</h2>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.tools : stack1),{"name":"each","hash":{},"fn":this.program(75, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"81":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return "			<h2>Certifications</h2>\n			<table class=\"user--certifications-table\">\n				<tbody>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.certifications : stack1),{"name":"each","hash":{},"fn":this.program(82, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</tbody>\n			</table>\n";
},"82":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "						<tr>\n							<td>\n								"
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "\n								("
    + alias3(((helper = (helper = helpers.description || (depth0 != null ? depth0.description : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"description","hash":{},"data":data}) : helper)))
    + ")\n								"
    + ((stack1 = (helpers.documentVerified || (depth0 && depth0.documentVerified) || alias1).call(depth0,depth0,{"name":"documentVerified","hash":{},"data":data})) != null ? stack1 : "")
    + "\n							</td>\n							<td>\n"
    + ((stack1 = (helpers.canShowAssets || (depth0 && depth0.canShowAssets) || alias1).call(depth0,((stack1 = (depths[1] != null ? depths[1].facade : depths[1])) != null ? stack1.owner : stack1),(depths[1] != null ? depths[1].isInternal : depths[1]),(depth0 != null ? depth0.assets : depth0),{"name":"canShowAssets","hash":{},"fn":this.program(83, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "								"
    + ((stack1 = (helpers.canReviewDocument || (depth0 && depth0.canReviewDocument) || alias1).call(depth0,(depths[1] != null ? depths[1].isInternal : depths[1]),(depth0 != null ? depth0.verificationStatus : depth0),{"name":"canReviewDocument","hash":{},"data":data})) != null ? stack1 : "")
    + "\n							</td>\n						</tr>\n";
},"83":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.assets : depth0),{"name":"each","hash":{},"fn":this.program(84, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"84":function(depth0,helpers,partials,data) {
    var helper;

  return "										<span><a href=\"/asset/download/"
    + this.escapeExpression(((helper = (helper = helpers.uuid || (depth0 != null ? depth0.uuid : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"uuid","hash":{},"data":data}) : helper)))
    + "\" title=\"Download\"><i class=\"icon-download\"></i></a></span>\n";
},"86":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return "			<h2>Licenses</h2>\n			<table class=\"user--licenses-table\">\n				<tbody>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.licenses : stack1),{"name":"each","hash":{},"fn":this.program(87, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</tbody>\n			</table>\n";
},"87":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "					<tr>\n						<td>\n							"
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "\n							("
    + alias3(((helper = (helper = helpers.description || (depth0 != null ? depth0.description : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"description","hash":{},"data":data}) : helper)))
    + ")\n						</td>\n						<td width=\"40%\">\n							"
    + ((stack1 = (helpers.documentVerified || (depth0 && depth0.documentVerified) || alias1).call(depth0,depth0,{"name":"documentVerified","hash":{},"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.canShowAssets || (depth0 && depth0.canShowAssets) || alias1).call(depth0,((stack1 = (depths[1] != null ? depths[1].facade : depths[1])) != null ? stack1.owner : stack1),(depths[1] != null ? depths[1].isInternal : depths[1]),(depth0 != null ? depth0.assets : depth0),{"name":"canShowAssets","hash":{},"fn":this.program(88, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "							"
    + ((stack1 = (helpers.canReviewDocument || (depth0 && depth0.canReviewDocument) || alias1).call(depth0,(depths[1] != null ? depths[1].isInternal : depths[1]),(depth0 != null ? depth0.verificationStatus : depth0),{"name":"canReviewDocument","hash":{},"data":data})) != null ? stack1 : "")
    + "\n							"
    + ((stack1 = (helpers.canEditDocument || (depth0 && depth0.canEditDocument) || alias1).call(depth0,(depths[1] != null ? depths[1].isInternal : depths[1]),((stack1 = (depths[1] != null ? depths[1].facade : depths[1])) != null ? stack1.id : stack1),(depth0 != null ? depth0.verificationStatus : depth0),{"name":"canEditDocument","hash":{},"data":data})) != null ? stack1 : "")
    + "\n						</td>\n					</tr>\n";
},"88":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.assets : depth0),{"name":"each","hash":{},"fn":this.program(89, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"89":function(depth0,helpers,partials,data) {
    var helper;

  return "									<a href=\"/asset/download/"
    + this.escapeExpression(((helper = (helper = helpers.uuid || (depth0 != null ? depth0.uuid : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"uuid","hash":{},"data":data}) : helper)))
    + "\" title=\"Download\"><i class=\"icon-download\"></i></a>\n";
},"91":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return "			<h2>Insurance Coverage</h2>\n			<table class=\"user--insurance-table\">\n				<tbody>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.insurance : stack1),{"name":"each","hash":{},"fn":this.program(92, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</tbody>\n			</table>\n";
},"92":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "						<tr>\n							<td>\n								"
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + " - "
    + alias3(((helper = (helper = helpers.notes || (depth0 != null ? depth0.notes : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"notes","hash":{},"data":data}) : helper)))
    + "\n								"
    + ((stack1 = (helpers.documentVerified || (depth0 && depth0.documentVerified) || alias1).call(depth0,depth0,{"name":"documentVerified","hash":{},"data":data})) != null ? stack1 : "")
    + "\n							</td>\n							<td width=\"40%\">\n"
    + ((stack1 = (helpers.canShowAssets || (depth0 && depth0.canShowAssets) || alias1).call(depth0,((stack1 = (depths[1] != null ? depths[1].facade : depths[1])) != null ? stack1.owner : stack1),(depths[1] != null ? depths[1].isInternal : depths[1]),(depth0 != null ? depth0.assets : depth0),{"name":"canShowAssets","hash":{},"fn":this.program(93, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "								"
    + ((stack1 = (helpers.canReviewDocument || (depth0 && depth0.canReviewDocument) || alias1).call(depth0,(depths[1] != null ? depths[1].isInternal : depths[1]),(depth0 != null ? depth0.verificationStatus : depth0),{"name":"canReviewDocument","hash":{},"data":data})) != null ? stack1 : "")
    + "\n							</td>\n						</tr>\n";
},"93":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.assets : depth0),{"name":"each","hash":{},"fn":this.program(94, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"94":function(depth0,helpers,partials,data) {
    var helper;

  return "										<a href=\"/asset/download/"
    + this.escapeExpression(((helper = (helper = helpers.uuid || (depth0 != null ? depth0.uuid : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"uuid","hash":{},"data":data}) : helper)))
    + "\" title=\"Download\"><i class=\"icon-download\"></i></a>\n";
},"96":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<h2>Employment</h2>\n			<table class=\"user--positions-table\">\n				<tbody>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.linkedInPositions : stack1),{"name":"each","hash":{},"fn":this.program(97, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</tbody>\n			</table>\n";
},"97":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "						<tr>\n							<td>\n								<strong>"
    + alias3(((helper = (helper = helpers.title || (depth0 != null ? depth0.title : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"title","hash":{},"data":data}) : helper)))
    + "</strong>\n								"
    + alias3(((helper = (helper = helpers.companyName || (depth0 != null ? depth0.companyName : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"companyName","hash":{},"data":data}) : helper)))
    + "\n							</td>\n							<td width=\"40%\">\n								<b>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.dateFromMonth : depth0),{"name":"if","hash":{},"fn":this.program(98, data, 0),"inverse":this.program(100, data, 0),"data":data})) != null ? stack1 : "")
    + "									&ndash;\n									"
    + alias3((helpers.positionEndDate || (depth0 && depth0.positionEndDate) || alias1).call(depth0,(depth0 != null ? depth0.current : depth0),(depth0 != null ? depth0.dateToMonth : depth0),(depth0 != null ? depth0.dateToYear : depth0),{"name":"positionEndDate","hash":{},"data":data}))
    + "\n								</b>\n							</td>\n						</tr>\n";
},"98":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2=this.escapeExpression;

  return "										"
    + alias2((helpers.monthName || (depth0 && depth0.monthName) || alias1).call(depth0,(depth0 != null ? depth0.dateFromMonth : depth0),{"name":"monthName","hash":{},"data":data}))
    + "\n										"
    + alias2(((helper = (helper = helpers.dateFromYear || (depth0 != null ? depth0.dateFromYear : depth0)) != null ? helper : alias1),(typeof helper === "function" ? helper.call(depth0,{"name":"dateFromYear","hash":{},"data":data}) : helper)))
    + "\n";
},"100":function(depth0,helpers,partials,data) {
    var helper;

  return "										"
    + this.escapeExpression(((helper = (helper = helpers.dateFromYear || (depth0 != null ? depth0.dateFromYear : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"dateFromYear","hash":{},"data":data}) : helper)))
    + "\n";
},"102":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<h2>Education</h2>\n			<table class=\"user--education-table\">\n				<tbody>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.linkedInEducation : stack1),{"name":"each","hash":{},"fn":this.program(103, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</tbody>\n			</table>\n";
},"103":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2=this.escapeExpression;

  return "					<tr>\n						<td>\n							<strong>"
    + alias2(((helper = (helper = helpers.schoolName || (depth0 != null ? depth0.schoolName : depth0)) != null ? helper : alias1),(typeof helper === "function" ? helper.call(depth0,{"name":"schoolName","hash":{},"data":data}) : helper)))
    + "</strong>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.degree : depth0),{"name":"if","hash":{},"fn":this.program(104, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "						</td>\n						<td width=\"40%\">\n							<b>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.dateFromMonth : depth0),{"name":"if","hash":{},"fn":this.program(107, data, 0),"inverse":this.program(109, data, 0),"data":data})) != null ? stack1 : "")
    + "								&ndash;\n								"
    + alias2((helpers.educationEndDate || (depth0 && depth0.educationEndDate) || alias1).call(depth0,(depth0 != null ? depth0.dateToMonth : depth0),(depth0 != null ? depth0.dateToYear : depth0),{"name":"educationEndDate","hash":{},"data":data}))
    + "\n							</b>\n						</td>\n					</tr>\n";
},"104":function(depth0,helpers,partials,data) {
    var stack1, helper;

  return "								"
    + this.escapeExpression(((helper = (helper = helpers.degree || (depth0 != null ? depth0.degree : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"degree","hash":{},"data":data}) : helper)))
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.fieldOfStudy : depth0),{"name":"if","hash":{},"fn":this.program(105, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"105":function(depth0,helpers,partials,data) {
    var helper;

  return "									"
    + this.escapeExpression(((helper = (helper = helpers.fieldOfStudy || (depth0 != null ? depth0.fieldOfStudy : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"fieldOfStudy","hash":{},"data":data}) : helper)))
    + "\n";
},"107":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2=this.escapeExpression;

  return "									"
    + alias2((helpers.monthName || (depth0 && depth0.monthName) || alias1).call(depth0,(depth0 != null ? depth0.dateFromMonth : depth0),{"name":"monthName","hash":{},"data":data}))
    + "\n									"
    + alias2(((helper = (helper = helpers.dateFromYear || (depth0 != null ? depth0.dateFromYear : depth0)) != null ? helper : alias1),(typeof helper === "function" ? helper.call(depth0,{"name":"dateFromYear","hash":{},"data":data}) : helper)))
    + "\n";
},"109":function(depth0,helpers,partials,data) {
    var helper;

  return "									"
    + this.escapeExpression(((helper = (helper = helpers.dateFromYear || (depth0 != null ? depth0.dateFromYear : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"dateFromYear","hash":{},"data":data}) : helper)))
    + "\n";
},"111":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<h2>Resume Download</h2>\n			<ul>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.resumes : stack1),{"name":"each","hash":{},"fn":this.program(112, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</ul>\n";
},"112":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.assets : depth0),{"name":"each","hash":{},"fn":this.program(113, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"113":function(depth0,helpers,partials,data) {
    var helper;

  return "						<li>\n							<i class=\"icon-file-alt\"></i>\n							<a href=\"/asset/download/"
    + this.escapeExpression(((helper = (helper = helpers.uuid || (depth0 != null ? depth0.uuid : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"uuid","hash":{},"data":data}) : helper)))
    + "\">Download Resume</a>\n						</li>\n";
},"115":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<h2>Languages</h2>\n			<ul>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.languages : stack1),{"name":"each","hash":{},"fn":this.program(116, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</ul>\n";
},"116":function(depth0,helpers,partials,data) {
    var helper;

  return "					<li><strong>"
    + this.escapeExpression(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "</strong></li>\n";
},"118":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<h2>Working Hours</h2>\n			<table class=\"user--working-hours-table\">\n				<tbody>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.workingHours : stack1),{"name":"each","hash":{},"fn":this.program(119, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</tbody>\n			</table>\n";
},"119":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = helpers.unless.call(depth0,(depth0 != null ? depth0.deleted : depth0),{"name":"unless","hash":{},"fn":this.program(120, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"120":function(depth0,helpers,partials,data) {
    var stack1, helper;

  return "						<tr>\n							<td>"
    + this.escapeExpression(((helper = (helper = helpers.weekDayName || (depth0 != null ? depth0.weekDayName : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"weekDayName","hash":{},"data":data}) : helper)))
    + "</td>\n							<td>\n								<strong>\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.allDayAvailable : depth0),{"name":"if","hash":{},"fn":this.program(121, data, 0),"inverse":this.program(123, data, 0),"data":data})) != null ? stack1 : "")
    + "								</strong>\n							</td>\n						</tr>\n";
},"121":function(depth0,helpers,partials,data) {
    return "										All Day\n";
},"123":function(depth0,helpers,partials,data) {
    var alias1=helpers.helperMissing, alias2=this.escapeExpression;

  return "										"
    + alias2((helpers.formatTime || (depth0 && depth0.formatTime) || alias1).call(depth0,(depth0 != null ? depth0.fromTime : depth0),{"name":"formatTime","hash":{},"data":data}))
    + " - "
    + alias2((helpers.formatTime || (depth0 && depth0.formatTime) || alias1).call(depth0,(depth0 != null ? depth0.toTime : depth0),{"name":"formatTime","hash":{},"data":data}))
    + "\n";
},"125":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<div class=\"wm-tab--content user-comments\" id=\"user-comments-"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">\n			<h2>Comments</h2>\n			<table class=\"user-comments--table\">\n				<thead>\n					<tr>\n						<th>Date</th>\n						<th>Comment</th>\n						<th>From</th>\n						<th>Actions</th>\n					</tr>\n				</thead>\n				<tbody></tbody>\n			</table>\n			<p class=\"no-results-copy\">You have no comments recorded for this user.</p>\n		</div>\n";
},"127":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<div class=\"wm-tab--content user-tags\" id=\"user-tags-"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">\n"
    + ((stack1 = (helpers.canDisplayTags || (depth0 && depth0.canDisplayTags) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.allowTagging : depth0),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.owner : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.isLane4LimitedVisibility : stack1),{"name":"canDisplayTags","hash":{},"fn":this.program(128, data, 0),"inverse":this.program(133, data, 0),"data":data})) != null ? stack1 : "")
    + "		</div>\n";
},"128":function(depth0,helpers,partials,data) {
    var stack1;

  return "				<h2>Tags</h2>\n"
    + ((stack1 = helpers.each.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.privateTags : stack1),{"name":"each","hash":{},"fn":this.program(129, data, 0),"inverse":this.program(131, data, 0),"data":data})) != null ? stack1 : "");
},"129":function(depth0,helpers,partials,data) {
    return "					<span class=\"wm-tag\">"
    + this.escapeExpression(this.lambda(depth0, depth0))
    + "</span>\n";
},"131":function(depth0,helpers,partials,data) {
    return "					<p class=\"no-results-copy\">There are currently no tags on this profile. Add one below.</p>\n";
},"133":function(depth0,helpers,partials,data) {
    return "				<p class=\"no-results-copy\">Add the worker to your network above to utilize the Tags feature.</p>\n";
},"135":function(depth0,helpers,partials,data) {
    return "			<div class=\"btn-group user-ratings-controls\">\n				<button type=\"button\" name=\"scopeToCompany\" value=\"false\" class=\"btn btn-mini active\">All Ratings</button>\n				<button type=\"button\" name=\"scopeToCompany\" value=\"true\" class=\"btn btn-mini\">Your Ratings</button>\n			</div>\n			<table class=\"user-ratings--table\">\n				<thead>\n				<tr>\n					<th></th>\n					<th class=\"text-center\"></th>\n					<th class=\"text-center\"></th>\n					<th class=\"text-center\"></th>\n					<th class=\"text-center\"></th>\n				</tr>\n				</thead>\n				<tbody></tbody>\n			</table>\n			<p class=\"no-results-copy\">No ratings information available.</p>\n";
},"137":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return "		<div class=\"wm-tab--content user-media\" id=\"user-media-"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">\n			<section class=\"gallery\">\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.ImageOutput : depth0),{"name":"each","hash":{},"fn":this.program(138, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			</section>\n		</div>\n";
},"138":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return ((stack1 = (helpers.isAsset || (depth0 && depth0.isAsset) || helpers.helperMissing).call(depth0,(depth0 != null ? depth0.assetResourceType : depth0),{"name":"isAsset","hash":{},"fn":this.program(139, data, 0, blockParams, depths),"inverse":this.program(145, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "");
},"139":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return ((stack1 = (helpers.canShowAsset || (depth0 && depth0.canShowAsset) || helpers.helperMissing).call(depth0,((stack1 = (depths[2] != null ? depths[2].facade : depths[2])) != null ? stack1.owner : stack1),(depths[2] != null ? depths[2].isGroupOwner : depths[2]),((stack1 = (depth0 != null ? depth0.availability : depth0)) != null ? stack1.code : stack1),{"name":"canShowAsset","hash":{},"fn":this.program(140, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "");
},"140":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1;

  return ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.media : depth0),{"name":"if","hash":{},"fn":this.program(141, data, 0, blockParams, depths),"inverse":this.program(143, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "");
},"141":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "								<div class=\"profile_video\" id=\"videoplayer_video_"
    + alias3(((helper = (helper = helpers.index || (data && data.index)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"index","hash":{},"data":data}) : helper)))
    + "\" data-order=\""
    + alias3(((helper = (helper = helpers.index || (data && data.index)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"index","hash":{},"data":data}) : helper)))
    + "\"/>\n";
},"143":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "								<a class=\"gallery--image\"\n									data-uri=\""
    + alias3(((helper = (helper = helpers.uri || (depth0 != null ? depth0.uri : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"uri","hash":{},"data":data}) : helper)))
    + "\"\n									data-total=\""
    + alias3(((helper = (helper = helpers.index || (data && data.index)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"index","hash":{},"data":data}) : helper)))
    + "\"\n									data-desc=\""
    + alias3(((helper = (helper = helpers.description || (depth0 != null ? depth0.description : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"description","hash":{},"data":data}) : helper)))
    + "\"\n									data-code=\""
    + alias3(this.lambda(((stack1 = (depth0 != null ? depth0.availability : depth0)) != null ? stack1.code : stack1), depth0))
    + "\"\n									data-bytes=\""
    + alias3(((helper = (helper = helpers.byteCountToDisplaySize || (depth0 != null ? depth0.byteCountToDisplaySize : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"byteCountToDisplaySize","hash":{},"data":data}) : helper)))
    + "\"\n									data-name=\""
    + alias3(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"name","hash":{},"data":data}) : helper)))
    + "\"\n									data-id=\""
    + alias3(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"id","hash":{},"data":data}) : helper)))
    + "\"\n									data-uuid=\""
    + alias3(((helper = (helper = helpers.UUID || (depth0 != null ? depth0.UUID : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"UUID","hash":{},"data":data}) : helper)))
    + "\"\n									data-type=\"asset\"\n								>\n									<img src=\""
    + alias3((helpers.assetsImageUrl || (depth0 && depth0.assetsImageUrl) || alias1).call(depth0,(depths[4] != null ? depths[4].ImageLargeOutput : depths[4]),(data && data.index),{"name":"assetsImageUrl","hash":{},"data":data}))
    + "\" default=\""
    + alias3(((helper = (helper = helpers.mediaPrefix || (depth0 != null ? depth0.mediaPrefix : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"mediaPrefix","hash":{},"data":data}) : helper)))
    + "/images/no_picture.png\" alt=\"Photo\"/>\n								</a>\n";
},"145":function(depth0,helpers,partials,data) {
    var helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression;

  return "						<li class=\"filled\">\n							<div class=\"profile_video\" id=\"videoplayer_video_"
    + alias3(((helper = (helper = helpers.index || (data && data.index)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"index","hash":{},"data":data}) : helper)))
    + "\" data-order=\""
    + alias3(((helper = (helper = helpers.index || (data && data.index)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"index","hash":{},"data":data}) : helper)))
    + "\"/>\n						</li>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data,blockParams,depths) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression, alias4=this.lambda;

  return "<div class=\"profile--messages\">\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.suspended : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.deactivated : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.blocked : stack1),{"name":"if","hash":{},"fn":this.program(5, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n	"
    + ((stack1 = (helpers.hasChangedEmail || (depth0 && depth0.hasChangedEmail) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.owner : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.changedEmail : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.email : stack1),{"name":"hasChangedEmail","hash":{},"data":data})) != null ? stack1 : "")
    + "\n</div>\n\n<header class=\"profile--header "
    + alias3(((helper = (helper = helpers.profileBackground || (depth0 != null ? depth0.profileBackground : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"profileBackground","hash":{},"data":data}) : helper)))
    + "\">\n\n	<div class=\"profile--personal-info\">\n		<div class=\"profile--avatar\">\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.avatarLargeAssetUri : stack1),{"name":"if","hash":{},"fn":this.program(7, data, 0, blockParams, depths),"inverse":this.program(9, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "")
    + "		</div>\n\n		<div class=\"profile--name\">\n			"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.firstName : stack1), depth0))
    + "\n			<!-- isLane4LimitedVisibility helper -->\n			"
    + alias3((helpers.limitedVisibility || (depth0 && depth0.limitedVisibility) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.isLane4LimitedVisibility : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.lastName : stack1),{"name":"limitedVisibility","hash":{},"data":data}))
    + "\n\n			"
    + ((stack1 = (helpers.laneTypeBadge || (depth0 && depth0.laneTypeBadge) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.laneType : stack1),false,{"name":"laneTypeBadge","hash":{},"data":data})) != null ? stack1 : "")
    + "\n		</div>\n\n		<div class=\"user-personal--job-title\">\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.jobTitle : stack1),{"name":"if","hash":{},"fn":this.program(11, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyName : stack1), depth0))
    + "\n		</div>\n		<address class=\"profile--address\">\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.address : stack1),{"name":"if","hash":{},"fn":this.program(13, data, 0, blockParams, depths),"inverse":this.program(15, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "")
    + "\n		</address>\n	</div>\n\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.isLane4LimitedVisibility : stack1),{"name":"unless","hash":{},"fn":this.program(18, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n</header>\n\n<section class=\"profile--details\">\n	<!-- Tabs -->\n	<ul class=\"wm-tabs profile--details-tabs\">\n		<li class=\"wm-tab -active\" data-content=\"#user-overview-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">\n			Overview\n		</li>\n		<li class=\"wm-tab\" data-content=\"#user-qualifications-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">\n			Qualifications\n		</li>\n		<li class=\"wm-tab ratings-tab\" data-content=\"#user-ratings-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">\n			Ratings\n		</li>\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.owner : stack1),{"name":"unless","hash":{},"fn":this.program(20, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ((stack1 = (helpers.hasVideosOrPhotos || (depth0 && depth0.hasVideosOrPhotos) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.video : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.photo : stack1),{"name":"hasVideosOrPhotos","hash":{},"fn":this.program(22, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</ul>\n\n	<!-- *** Overview tab *** -->\n	<div class=\"wm-tab--content -active user-overview\" id=\"user-overview-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">\n		<div class=\"user-overview-left\">\n			<p>\n				User ID: <strong>"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "</strong>\n			</p>\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.laneType : stack1),{"name":"if","hash":{},"fn":this.program(24, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			<p>URL: <a class=\"break-word\" href=\""
    + alias3(((helper = (helper = helpers.baseurl || (depth0 != null ? depth0.baseurl : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"baseurl","hash":{},"data":data}) : helper)))
    + "/profile/"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">/profile/"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "</a></p>\n\n			<!-- Drug test -->\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.drugTestStatus : stack1),{"name":"if","hash":{},"fn":this.program(26, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "			<!-- Background Check -->\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.backgroundCheckStatus : stack1),{"name":"if","hash":{},"fn":this.program(31, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</div>\n\n		<div class=\"user-overview-right\">\n			<p>Since: "
    + alias3((helpers.formatDateShort || (depth0 && depth0.formatDateShort) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.createdOn : stack1),{"name":"formatDateShort","hash":{},"data":data}))
    + "</p>\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.mboStatus : stack1),{"name":"if","hash":{},"fn":this.program(36, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n			"
    + ((stack1 = (helpers.taxEntityStatus || (depth0 && depth0.taxEntityStatus) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.taxEntityExists : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.verifiedTaxEntity : stack1),{"name":"taxEntityStatus","hash":{},"data":data})) != null ? stack1 : "")
    + "\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.confirmedBankAccount : stack1),{"name":"if","hash":{},"fn":this.program(38, data, 0, blockParams, depths),"inverse":this.program(40, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.linkedInVerified : stack1),{"name":"if","hash":{},"fn":this.program(42, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyWebsite : stack1),{"name":"if","hash":{},"fn":this.program(44, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</div>\n\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.overview : stack1),{"name":"if","hash":{},"fn":this.program(46, data, 0, blockParams, depths),"inverse":this.program(48, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "")
    + "\n		<!--Groups-->\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.isLane4LimitedVisibility : stack1),{"name":"unless","hash":{},"fn":this.program(50, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n		<!--Assessments-->\n		<h2>Tests Taken <span class=\"pull-right wm-badge "
    + alias3((helpers.activeBadge || (depth0 && depth0.activeBadge) || alias1).call(depth0,((stack1 = ((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.assessments : stack1)) != null ? stack1.length : stack1),{"name":"activeBadge","hash":{},"data":data}))
    + "\">"
    + alias3(alias4(((stack1 = ((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.assessments : stack1)) != null ? stack1.length : stack1), depth0))
    + "</span></h2>\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.assessments : stack1),{"name":"if","hash":{},"fn":this.program(61, data, 0, blockParams, depths),"inverse":this.program(64, data, 0, blockParams, depths),"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.canSeeRoles || (depth0 && depth0.canSeeRoles) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.owner : stack1),(depth0 != null ? depth0.isCompanyAdmin : depth0),{"name":"canSeeRoles","hash":{},"fn":this.program(66, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.hasHourlyRate || (depth0 && depth0.hasHourlyRate) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.minOnsiteHourlyRate : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.minOffsiteHourlyRate : stack1),{"name":"hasHourlyRate","hash":{},"fn":this.program(69, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</div>\n\n	<!-- *** Qualifications tab *** -->\n	<div class=\"wm-tab--content user-qualifications\" id=\"user-qualifications-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">\n		<h2>Industries</h2>\n		"
    + alias3((helpers.industriesList || (depth0 && depth0.industriesList) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.industries : stack1),{"name":"industriesList","hash":{},"data":data}))
    + "\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.skills : stack1),{"name":"if","hash":{},"fn":this.program(74, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.specialties : stack1),{"name":"if","hash":{},"fn":this.program(77, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.tools : stack1),{"name":"if","hash":{},"fn":this.program(79, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.certifications : stack1),{"name":"if","hash":{},"fn":this.program(81, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.licenses : stack1),{"name":"if","hash":{},"fn":this.program(86, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.insurance : stack1),{"name":"if","hash":{},"fn":this.program(91, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.hasLinkedInPositions || (depth0 && depth0.hasLinkedInPositions) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.linkedInVerified : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.linkedInPositions : stack1),{"name":"hasLinkedInPositions","hash":{},"fn":this.program(96, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = (helpers.hasLinkedInPositions || (depth0 && depth0.hasLinkedInPositions) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.linkedInVerified : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.linkedInEducation : stack1),{"name":"hasLinkedInPositions","hash":{},"fn":this.program(102, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.resumes : stack1),{"name":"if","hash":{},"fn":this.program(111, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.languages : stack1),{"name":"if","hash":{},"fn":this.program(115, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.workingHours : stack1),{"name":"if","hash":{},"fn":this.program(118, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</div>\n\n	<!-- *** Comments tab *** -->\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.owner : stack1),{"name":"unless","hash":{},"fn":this.program(125, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n	<!-- *** Tags tab *** -->\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.owner : stack1),{"name":"unless","hash":{},"fn":this.program(127, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n	<!-- *** Ratings tab *** -->\n	<div class=\"wm-tab--content user-ratings\" id=\"user-ratings-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\">\n		<h2>Ratings</h2>\n"
    + ((stack1 = helpers.unless.call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.owner : stack1),{"name":"unless","hash":{},"fn":this.program(135, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "	</div>\n\n	<!-- *** Media tab *** -->\n"
    + ((stack1 = (helpers.hasVideosOrPhotos || (depth0 && depth0.hasVideosOrPhotos) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.video : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.photo : stack1),{"name":"hasVideosOrPhotos","hash":{},"fn":this.program(137, data, 0, blockParams, depths),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</section>\n\n<!--user scorecard-->\n<aside class=\"sidebar-card\">\n"
    + ((stack1 = this.invokePartial(partials['score-card'],depth0,{"name":"score-card","hash":{"isDispatch":(depth0 != null ? depth0.isDispatch : depth0),"values":(depth0 != null ? depth0.scoreCard : depth0),"showrecent":true},"data":data,"indent":"\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "	<button id=\"show-ratings\" class=\"sidebar-card--button\">See Ratings</button>\n</aside>\n\n<footer class=\"profile--footer\">\n	<div class=\"profile--quick-actions\">\n		<label class=\"switch profile--quick-action tooltipped tooltipped-n\" aria-label=\"Add Comments\">\n			<input type=\"checkbox\" name=\"profile-quick-action\" class=\"switch--checkbox\"/>\n			<div data-toggle=\"collapse\" href=\"#collapseComments-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" data-parent=\"#collapse-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" class=\"switch--skin\"><i class=\"wm-icon-speech\"></i></div>\n		</label>\n		<label class=\"switch profile--quick-action tags-quick-action tooltipped tooltipped-n\" aria-label=\"Add Tags\">\n			<input type=\"checkbox\" name=\"profile-quick-action\" class=\"switch--checkbox\"/>\n			<div data-toggle=\"collapse\" href=\"#collapseTags-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" data-parent=\"#collapse-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" class=\"switch--skin\"><i class=\"wm-icon-tag\"></i></div>\n		</label>\n		<label class=\"switch profile--quick-action tests-quick-action tooltipped tooltipped-n\" aria-label=\"Invite to Test\">\n			<input type=\"checkbox\" name=\"profile-quick-action\" class=\"switch--checkbox\"/>\n			<div data-toggle=\"collapse\" href=\"#collapseTests-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" data-parent=\"#collapse-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" class=\"switch--skin\"><i class=\"wm-icon-test\"></i></div>\n		</label>\n		<label class=\"switch profile--quick-action groups-quick-action tooltipped tooltipped-n\" aria-label=\"Invite to Group\">\n			<input type=\"checkbox\" name=\"profile-quick-action\" class=\"switch--checkbox\"/>\n			<div data-toggle=\"collapse\" href=\"#collapseGroups-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" data-parent=\"#collapse-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" class=\"switch--skin\"><i class=\"wm-icon-users\"></i></div>\n		</label>\n		<label class=\"switch profile--quick-action assignment-quick-action tooltipped tooltipped-n\" aria-label=\"Send Assignment\">\n			<input type=\"checkbox\" name=\"profile-quick-action\" class=\"switch--checkbox\"/>\n			<div class=\"switch--skin\"><i class=\"wm-icon-page-out\"></i></div>\n		</label>\n	</div>\n	<div id=\"collapse-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" class=\"profile--quick-actions-content\">\n		<div class=\"accordion-group\">\n			<div id=\"collapseGroups-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" class=\"collapse\">\n				<div class=\"collapse--inner\">\n					<p>If the group is public, the worker will receive an invitation via email with information regarding your group. If the group is private, the worker will be added immediately.</p>\n					<label>\n						Select Group:\n						<select class=\"wm-select group-select\"></select>\n					</label>\n					<button class=\"button -primary profile--invite-group\" data-user-number=\""
    + alias3(((helper = (helper = helpers.userNumber || (depth0 != null ? depth0.userNumber : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"userNumber","hash":{},"data":data}) : helper)))
    + "\">Invite to Group</button>\n					<button class=\"button profile--quick-actions-close\">Close</button>\n				</div>\n			</div>\n		</div>\n		<div class=\"accordion-group\">\n			<div id=\"collapseTests-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" class=\"collapse\">\n				<div class=\"collapse--inner\">\n					<p>The worker will receive a notification informing them of a pending test invitation including any instructions outlined in the tests description.</p>\n					<label>\n						Select Test:\n						<select class=\"wm-select test-select\"></select>\n					</label>\n					<button class=\"button -primary profile--invite-test\" data-user-number=\""
    + alias3(((helper = (helper = helpers.userNumber || (depth0 != null ? depth0.userNumber : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"userNumber","hash":{},"data":data}) : helper)))
    + "\">Invite to Test</button>\n					<button class=\"button profile--quick-actions-close\">Close</button>\n				</div>\n			</div>\n		</div>\n		<div class=\"accordion-group\">\n			<div id=\"collapseTags-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" class=\"collapse\">\n				<div class=\"collapse--inner\">\n					<p>Add tag below...</p>\n					<input class=\"wm-tags tags-input\" name=\"tags\" type=\"text\" placeholder=\"Add Tags...\" value=\""
    + alias3((helpers.existingTags || (depth0 && depth0.existingTags) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.privateTags : stack1),{"name":"existingTags","hash":{},"data":data}))
    + "\"/>\n					<button class=\"button -primary profile--add-tag\">Save Tags</button>\n					<button class=\"button profile--quick-actions-close\">Close</button>\n				</div>\n			</div>\n		</div>\n		<div class=\"accordion-group\">\n			<div id=\"collapseComments-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.userNumber : stack1), depth0))
    + "\" class=\"collapse\">\n				<div class=\"collapse--inner\">\n					<textarea class=\"comment-input\" placeholder=\"Add Comment here...\"></textarea>\n					<button class=\"button -primary profile--add-comment\" data-user-number=\""
    + alias3(((helper = (helper = helpers.userNumber || (depth0 != null ? depth0.userNumber : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"userNumber","hash":{},"data":data}) : helper)))
    + "\">Add Comment</button>\n					<button class=\"button profile--quick-actions-close\">Close</button>\n				</div>\n			</div>\n		</div>\n	</div>\n</footer>\n";
},"usePartial":true,"useData":true,"useDepths":true});

this["wm"]["templates"]["search/vendor_profile_modal_content"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "		<h4>\n			This company is currently suspended.\n		</h4>\n";
},"3":function(depth0,helpers,partials,data) {
    return "		<h4>\n			This company is currently deactivated.\n		</h4>\n";
},"5":function(depth0,helpers,partials,data) {
    var stack1;

  return "		<h4 class=\"tac\">Your company has blocked "
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyName : stack1), depth0))
    + " Please contact your administrator for more information.</h4>\n";
},"7":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.avatar,depth0,{"name":"avatar","hash":{"src":((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.avatarLargeAssetUri : stack1)},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"9":function(depth0,helpers,partials,data) {
    var stack1;

  return ((stack1 = this.invokePartial(partials.avatar,depth0,{"name":"avatar","hash":{"hash":((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyNumber : stack1)},"data":data,"indent":"\t\t\t\t","helpers":helpers,"partials":partials})) != null ? stack1 : "");
},"11":function(depth0,helpers,partials,data) {
    var stack1;

  return "				"
    + this.escapeExpression(this.lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyAddress : stack1)) != null ? stack1.shortAddress : stack1), depth0))
    + "\n";
},"13":function(depth0,helpers,partials,data) {
    return "				<p>Bank Account: <b>Confirmed</b></p>\n";
},"15":function(depth0,helpers,partials,data) {
    return "				<p>Bank Account: <b>Unconfirmed</b></p>\n";
},"17":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.escapeExpression;

  return "				<p>\n					<i class=\"wm-icon-globe-circle\"></i>\n					<a target=\"_blank\" href=\""
    + alias1((helpers.ensureProtocol || (depth0 && depth0.ensureProtocol) || helpers.helperMissing).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyWebsite : stack1),{"name":"ensureProtocol","hash":{},"data":data}))
    + "\">"
    + alias1(this.lambda(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyWebsite : stack1), depth0))
    + "</a>\n				</p>\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, helper, alias1=helpers.helperMissing, alias2="function", alias3=this.escapeExpression, alias4=this.lambda;

  return "<div class=\"profile--messages\">\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.suspended : stack1),{"name":"if","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.deactivated : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.blocked : stack1),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "</div>\n\n<header class=\"profile--header "
    + alias3(((helper = (helper = helpers.profileBackground || (depth0 != null ? depth0.profileBackground : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"profileBackground","hash":{},"data":data}) : helper)))
    + "\">\n\n	<div class=\"profile--personal-info\">\n		<div class=\"profile--avatar\">\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.avatarLargeAssetUri : stack1),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.program(9, data, 0),"data":data})) != null ? stack1 : "")
    + "		</div>\n\n		<div class=\"profile--name\">\n			"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyName : stack1), depth0))
    + "\n			<span class=\"lane-type-badge tooltipped tooltipped-n\" aria-label=\"Vendor\">V</span>\n		</div>\n\n		<address class=\"profile--address\">\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyAddress : stack1),{"name":"if","hash":{},"fn":this.program(11, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</address>\n	</div>\n</header>\n\n<section class=\"profile--details\">\n	<!-- Tabs -->\n	<ul class=\"wm-tabs profile--details-tabs\">\n		<li class=\"wm-tab -active\" data-content=\"#user-overview-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyNumber : stack1), depth0))
    + "\">\n			Overview\n		</li>\n		<li class=\"wm-tab team-tab\" data-content=\"#user-team-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyNumber : stack1), depth0))
    + "\">\n			Team\n		</li>\n	</ul>\n\n	<!-- *** Overview tab *** -->\n	<div class=\"wm-tab--content -active user-overview\" id=\"user-overview-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyNumber : stack1), depth0))
    + "\">\n		<div class=\"user-overview-left\">\n			<p>\n				Company ID: <strong>"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyNumber : stack1), depth0))
    + "</strong>\n			</p>\n\n			<span class=\"lane-type-badge tooltipped tooltipped-n\" aria-label=\"Vendor\">V</span>\n\n			<p>URL: <a class=\"break-word\" href=\""
    + alias3(((helper = (helper = helpers.baseurl || (depth0 != null ? depth0.baseurl : depth0)) != null ? helper : alias1),(typeof helper === alias2 ? helper.call(depth0,{"name":"baseurl","hash":{},"data":data}) : helper)))
    + "/profile/company/"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyNumber : stack1), depth0))
    + "\">/profile/company/"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyNumber : stack1), depth0))
    + "</a></p>\n		</div>\n\n		<div class=\"user-overview-right\">\n			<p>Since: "
    + alias3((helpers.formatDateShort || (depth0 && depth0.formatDateShort) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.createdOn : stack1),{"name":"formatDateShort","hash":{},"data":data}))
    + "</p>\n\n			"
    + ((stack1 = (helpers.taxEntityStatus || (depth0 && depth0.taxEntityStatus) || alias1).call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.taxEntityExists : stack1),((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.verifiedTaxEntity : stack1),{"name":"taxEntityStatus","hash":{},"data":data})) != null ? stack1 : "")
    + "\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.confirmedBankAccount : stack1),{"name":"if","hash":{},"fn":this.program(13, data, 0),"inverse":this.program(15, data, 0),"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companycompanyWebsite : stack1),{"name":"if","hash":{},"fn":this.program(17, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "		</div>\n\n\n		<p class=\"overview-description\">"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyOverview : stack1), depth0))
    + "</p>\n	</div>\n\n	<!-- *** Team tab *** -->\n	<div class=\"wm-tab--content user-team\" id=\"user-team-"
    + alias3(alias4(((stack1 = (depth0 != null ? depth0.facade : depth0)) != null ? stack1.companyNumber : stack1), depth0))
    + "\"></div>\n</section>\n\n<!--user scorecard-->\n<aside class=\"sidebar-card\">\n"
    + ((stack1 = this.invokePartial(partials['score-card'],depth0,{"name":"score-card","hash":{"values":(depth0 != null ? depth0.scoreCard : depth0),"showrecent":true},"data":data,"indent":"\t","helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "	<button id=\"show-ratings\" class=\"sidebar-card--button\">See Ratings</button>\n</aside>\n";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["settings/labels_modal"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
    return "selected";
},"3":function(depth0,helpers,partials,data) {
    return "checked";
},"5":function(depth0,helpers,partials,data) {
    return "			<label>\n				Define which <strong>templates</strong> can use this label (leave blank for \"all\"):\n				<span class=\"tooltipped tooltipped-n\" aria-label=\"If this label should only be used on assignments created with particular templates, you can specify them here.\"><i class=\"wm-icon-question-filled\"></i></span>\n			</label>\n";
},"7":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<div id=\"workTemplateIdsDiv\">\n				<select id=\"workTemplateIds\" name=\"workTemplateIds\" data-placeholder=\"Enter template name\" multiple=\"multiple\">\n"
    + ((stack1 = helpers.each.call(depth0,(depth0 != null ? depth0.templates : depth0),{"name":"each","hash":{},"fn":this.program(8, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "				</select>\n			</div>\n";
},"8":function(depth0,helpers,partials,data) {
    var helper, alias1=this.escapeExpression;

  return "						<option value=\""
    + alias1(((helper = (helper = helpers.key || (data && data.key)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"key","hash":{},"data":data}) : helper)))
    + "\">"
    + alias1(this.lambda(depth0, depth0))
    + "</option>\n";
},"10":function(depth0,helpers,partials,data) {
    return "		<div id=\"scopeWarning\" class=\"dn alert-message\">\n			<strong>Warning:</strong> If this label is on any assignments no longer matching the specified scope, it will be removed from those assignments. This cannot be undone.\n		</div>\n";
},"12":function(depth0,helpers,partials,data) {
    var stack1;

  return "			<span>Active Label</span>\n			<input name=\"active\" type=\"checkbox\" "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.active : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + " />\n			<span class=\"tooltipped tooltipped-n\" aria-label=\"Deactivated labels are visible (per their settings) and can be removed from assignments, but cannot be added to them.\"><i class=\"wm-icon-question-filled\"></i></span>\n			<a class=\"button cta-delete-label\" href=\"/settings/manage/label_delete/"
    + this.escapeExpression(this.lambda(((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.workSubStatusTypeId : stack1), depth0))
    + "\">Delete Permanently</a>\n";
},"14":function(depth0,helpers,partials,data) {
    return "			<input type=\"hidden\" name=\"active\" value=\"true\" checked=\"checked\" />\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1, alias1=this.lambda, alias2=this.escapeExpression, alias3=helpers.helperMissing;

  return "<form id=\"form_labels_manage\">\n\n	<input type=\"hidden\" name=\"id\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.workSubStatusTypeId : stack1), depth0))
    + "\">\n\n	<div id=\"label-form\">\n		<div>\n			<label>Label Name</label>\n			<input id=\"label_name\" name=\"description\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.description : stack1), depth0))
    + "\" type=\"text\"/>\n		</div>\n\n		<div>\n			<label>\n				Worker Access\n				<span class=\"tooltipped tooltipped-n\" aria-label=\"Indicate if the worker can view and/or apply this label.\"><i class=\"wm-icon-question-filled\"></i></span>\n			</label>\n\n			<select id=\"resource_access\" name=\"resourceAccess\" class=\"wm-select\">\n				<option value=\"\">None</option>\n				<option value=\"view\" "
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias3).call(depth0,((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.resourceAccess : stack1),"view",{"name":"eq","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">View</option>\n				<option value=\"view_edit\" "
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias3).call(depth0,((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.resourceAccess : stack1),"view_edit",{"name":"eq","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">View &amp; Set</option>\n			</select>\n		</div>\n\n		<div>\n			<label>Notifications\n				<span class=\"tooltipped tooltipped-n\" aria-label=\"Indicate who should be notified when this label is applied.  Note that individual users can turn off these notifications.\"><i class=\"wm-icon-question-filled\"></i></span>\n			</label>\n\n			<input id=\"notify\" type=\"hidden\" name=\"notify\"/>\n\n			<input id=\"label-notifications\" name=\"workSubStatusTypeRecipientIds\"/>\n		</div>\n	</div>\n\n	<div class=\"page-header\">\n		<br/>\n		<h4>Actions:<small> available when this label is applied:</small></h4>\n	</div>\n\n	<div>\n		<div id=\"action_checkboxes\" class=\"label-left\">\n			<label>\n				<input type=\"checkbox\" name=\"alert\" value=\"true\" "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.alert : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "/> Mark the assignment as Alert status\n				<span class=\"tooltipped tooltipped-n\" aria-label=\"Alert labels appear in the Alert section of the Assignment Dashboard.\"><i class=\"wm-icon-question-filled\"></i></span>\n			</label>\n			<label>\n				<input type=\"checkbox\" name=\"noteRequired\" "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.noteRequired : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "/> Require the user to enter a\n				<select id=\"noteRequiredAccess\" name=\"noteRequiredAccess\" class=\"span3\">\n					<option value=\"pr\" "
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias3).call(depth0,((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.noteRequiredAccess : stack1),"pr",{"name":"eq","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">Private message</option>\n					<option value=\"sh\" "
    + ((stack1 = (helpers.eq || (depth0 && depth0.eq) || alias3).call(depth0,((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.noteRequiredAccess : stack1),"sh",{"name":"eq","hash":{},"fn":this.program(1, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + ">Privileged message</option>\n				</select>\n				<span class=\"tooltipped tooltipped-n\" aria-label=\"If unchecked, adding a note is optional. Note: if the label is added by the worker, it is automatically considered shared.\"><i class=\"wm-icon-question-filled\"></i></span>\n			</label>\n			<label>\n				<input type=\"checkbox\" name=\"includeInstructions\" "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.includeInstructions : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "/> Include instructions for note field\n				<span class=\"tooltipped tooltipped-n\" aria-label=\"If checked, instructions are visible even when note is not required.\"><i class=\"wm-icon-question-filled\"></i></span>\n			</label>\n			<div class=\"additional_fields dn\">\n				<textarea name=\"instructions\" rows=\"10\" placeholder=\"Provide the user with instructions on what to include in their note.\">"
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.instructions : stack1), depth0))
    + "</textarea>\n			</div>\n			<label>\n				<input type=\"checkbox\" name=\"scheduleRequired\" "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.scheduleRequired : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "/> Require a reschedule\n				<span class=\"tooltipped tooltipped-n\" aria-label=\"If checked, the user must propose a reschedule time.\"><i class=\"wm-icon-question-filled\"></i></span>\n			</label>\n			<div class=\"additional_fields dn\">\n				<label>\n					<input type=\"checkbox\" name=\"removeAfterReschedule\" "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.removeAfterReschedule : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "/> Remove label once reschedule is approved or declined.\n				</label>\n			</div>\n		</div>\n	</div>\n\n	<div class=\"page-header\">\n		<br/>\n		<h4>\n			Scope: <small>define the range of <strong>statuses</strong> where this label can be applied/seen</small>\n			<span id=\"scope-popover\" class=\"tooltipped tooltipped-n\" aria-label=\"Often, labels are only relevant while an assignment is in a particular status, or set of statuses. Once an assignment enters the defined range, you may apply this label - once it leaves, the label is removed automatically. 'Complete' here means the worker has completed the assignment and it is now awaiting approval. 'Approved' includes assignments that have been approved and are either pending payment or already paid.\">\n				<i class=\"wm-icon-question-filled\"></i>\n			</span>\n		</h4>\n	</div>\n\n	<div class=\"input label-left\">\n		<div id=\"scope_slider_container\" class=\"slider-container\">\n			<div id=\"labels_container\" class=\"labels-container\">\n				<span class=\"slider_label\">Draft</span>\n				<span class=\"slider_label\">Sent</span>\n				<span class=\"slider_label\">Assigned</span>\n				<span class=\"slider_label\">Complete</span>\n				<span class=\"slider_label last\">Invoiced</span>\n			</div>\n			<div id=\"labelScopeRange\" class=\"labels\"></div>\n			<input type=\"hidden\" id=\"scope_range_from\" name=\"workStatusTypeScopeRangeFrom\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.workStatusTypeScopeRangeFrom : stack1), depth0))
    + "\"/>\n			<input type=\"hidden\" id=\"scope_range_to\" name=\"workStatusTypeScopeRangeTo\" value=\""
    + alias2(alias1(((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.workStatusTypeScopeRangeTo : stack1), depth0))
    + "\"/>\n		</div>\n		<label>\n			<input type=\"checkbox\" name=\"removeOnVoidOrCancelled\" "
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.removeOnVoidOrCancelled : stack1),{"name":"if","hash":{},"fn":this.program(3, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "/> Remove on Void/Cancel\n			<span class=\"tooltipped tooltipped-n\" aria-label=\"If checked, this label will be removed when the assignment is voided/cancelled.\"><i class=\"wm-icon-question-filled\"></i></span>\n		</label>\n\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.templatesEnabled : depth0),{"name":"if","hash":{},"fn":this.program(5, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n"
    + ((stack1 = helpers['if'].call(depth0,(depth0 != null ? depth0.templatesEnabled : depth0),{"name":"if","hash":{},"fn":this.program(7, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n		<!--enable for thrillist-->\n		<!--<br/>-->\n		<!--<label>Add which employees should be alerted when this label is added:</label>-->\n		<!--<select class=\"company-employees\"></select>-->\n	</div>\n\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.workSubStatusTypeId : stack1),{"name":"if","hash":{},"fn":this.program(10, data, 0),"inverse":this.noop,"data":data})) != null ? stack1 : "")
    + "\n\n	<div class=\"wm-action-container\">\n"
    + ((stack1 = helpers['if'].call(depth0,((stack1 = (depth0 != null ? depth0.form : depth0)) != null ? stack1.workSubStatusTypeId : stack1),{"name":"if","hash":{},"fn":this.program(12, data, 0),"inverse":this.program(14, data, 0),"data":data})) != null ? stack1 : "")
    + "\n		<button type=\"button\" class=\"button\" data-modal-close>Cancel</button>\n		<button id=\"save-label\" class=\"button -primary\">Save</button>\n	</div>\n</form>\n";
},"useData":true});

this["wm"]["templates"]["settings/last_team_agent_warning_modal"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "This is the only user in your organization with the Team Agent role.  If you remove it, your company will be removed from search listings.\n<div class=\"wm-action-container\">\n	<button type=\"button\" class=\"button\" data-modal-close>Cancel</button>\n	<button id=\"remove-dispatcher-role\" class=\"button -primary\" data-modal-close data-modal-accept=true>Accept</button>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["workerservices/index"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"page-header\">\n	<h3>Worker Services</h3>\n</div>\n\n<div class=\"worker-services\">\n	<div class=\"worker-services--service -stride\">\n		<div class=\"logo\"></div>\n		<p>Health Care built for independents. Simple tools that help you save money and manage your healthcare.</p>\n		<button class=\"button worker-services--nav\" data-slug=\"stride\">Learn More</button>\n	</div>\n	<div class=\"worker-services--service -intuit\">\n		<div class=\"logo\"></div>\n		<p>Save money on your taxes, stay cool with the IRS, and be ready for whatever self-employment throws your way.</p>\n		<button class=\"button worker-services--nav\" data-slug=\"intuit\">Start saving</button>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["workerservices/intuit"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var helper;

  return "<div class=\"page-header\">\n	<h3>Intuit QuickBooks</h3>\n</div>\n\n<div class=\"worker-services\">\n	<div class=\"worker-services--service -intuit\">\n		<div class=\"logo\"></div>\n		<div class=\"stride-actions\">\n			<p>Subscribers find an average of $3,809 in tax savings per year!</p>\n			<a href=\"https://selfemployed.intuit.com/workmarket?utm_source=workmarket&utm_medium=IPD&utm_content=serviceprofile&cid=IPD_workmarket_serviceprofile_QBSE&utm_email="
    + this.escapeExpression(((helper = (helper = helpers.email || (depth0 != null ? depth0.email : depth0)) != null ? helper : helpers.helperMissing),(typeof helper === "function" ? helper.call(depth0,{"name":"email","hash":{},"data":data}) : helper)))
    + "\" class=\"button\">Subscribe now</a>\n		</div>\n	</div>\n	<div class=\"worker-services--service -detail\">\n		<div class=\"worker-services--service-header\">\n			<h3>Intuit Quickbooks</h3>\n			<div class=\"back-to-list\"></div>\n			<div class=\"clearfix\"></div>\n		</div>\n		<p class=\"lead\">Overwhelmed by self-employment taxes? QuickBooks Self-Employed can help you…</p>\n		<p class=\"sub\">Easily separate your work and personal expenses in one place.</p>\n		<p class=\"sub\">Fill out your Schedule C and maximize your tax deductions.</p>\n		<p class=\"sub\">Calculate your quarterly estimated taxes to lower your annual tax bill and update your total financial picture.</p>\n	</div>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["workerservices/stride-permission-modal"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"stride-permission-modal\">\n	<p>Work Market would like to share the following information with Stride Health in order to personalize your experience and find health plans in your region:</p>\n	<ul>\n		<li>Email Address</li>\n		<li>Zip Code</li>\n	</ul>\n	<p>This information will not be used for any marketing purposes.</p>\n	<button class=\"button stride-accept\">Yes, proceed to Stride Health</button>\n	<button class=\"button cancel\" data-modal-close>Cancel</button>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["workerservices/stride"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    var stack1;

  return "<div class=\"page-header\">\n	<h3>Health Insurance</h3>\n</div>\n\n<div class=\"worker-services\">\n	<div class=\"worker-services--service -stride\">\n		<div class=\"logo\"></div>\n		<p>Health care built for independents. Simple tools that help you save money and manage your healthcare.</p>\n		<div class=\"stride-actions\">\n			<button class=\"button stride-start\">Find Your Plan</button>\n			<div class=\"stride-loading\">"
    + ((stack1 = this.invokePartial(partials.spinner,depth0,{"name":"spinner","data":data,"helpers":helpers,"partials":partials})) != null ? stack1 : "")
    + "</div>\n		</div>\n		<small>(US workers only)</small>\n		<p>Want to learn more? Check out the <a class=\"stride-guide\">insurance guide</a>.</p>\n	</div>\n	<div class=\"worker-services--service -detail\">\n		<div class=\"worker-services--service-header\">\n			<h3>Stride Health care</h3>\n			<div class=\"back-to-list\"></div>\n			<div class=\"clearfix\"></div>\n		</div>\n		<p class=\"lead\">Find a better health plan in 10 minutes or less</p>\n		<p class=\"sub\">A personal forecast ensures you'll never overpay for a plan.</p>\n		<p class=\"lead\">Access doctors, urgent care, and pharmacy deals</p>\n		<p class=\"sub\">Unlock the best care, you’re already paying for it!</p>\n		<p class=\"lead\">Take control of your finances</p>\n		<p class=\"sub\">Personalized recommendations for keeping more of what you earn.</p>\n		<p class=\"lead\">We’ve got your back</p>\n		<p class=\"sub\">Our agents and tax specialists are here to support you year-round.</p>\n	</div>\n</div>";
},"usePartial":true,"useData":true});

this["wm"]["templates"]["workerservicespromo/intuit"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"workerservices-promo-banner\">\n	<div class=\"logo\"></div>\n	<p>Save on your self-employment taxes year-round. Log miles, deductions, receipts, and more.</p>\n	<button class=\"button learn-more\">Start for Free</button>\n</div>\n";
},"useData":true});

this["wm"]["templates"]["workerservicespromo/stride"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
    return "<div class=\"stride-promo-banner\">\n	<div class=\"logo\"></div>\n	<p>Health insurance is now available to all US Work Market users through <strong>Stride Health</strong>.</p>\n	<div class=\"cta\">\n		<button class=\"button learn-more\">Learn More</button>\n	</div>\n	<div class=\"dismiss\">\n		<button type=\"button\" class=\"close wm-icon-x\"></button>\n	</div>\n</div>";
},"useData":true});