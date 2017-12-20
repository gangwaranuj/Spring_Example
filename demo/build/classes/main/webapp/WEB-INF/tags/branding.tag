<%@ tag description="Work Market Branding" %>
<%@ attribute name="work" required="false" %>
<%@ attribute name="name" required="true" %>

<span class="wm-branding">
  <span class="work">${not empty work ? work : 'Work'}</span>
  <span class="other">${name}&#8482</span>
</span>
