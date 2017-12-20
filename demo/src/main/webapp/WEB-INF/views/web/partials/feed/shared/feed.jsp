<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<div class="work-feed">
	<form id="feed-searcher" class="form-inline">
		<input id="feed-keyword" name="keyword" type="text" class="input-big" placeholder="Title, Keyword, or Company" />

		<div class="input-prepend dropdown">
			<button class="btn dropdown-toggle" type="button" data-toggle="dropdown">
				<i class="input-dropdown-icon icon-map-marker"></i>
				<span class="caret"></span>
			</button>
			<ul class="dropdown-menu location">
				<li><a data-searchtype="postal" href="#" class="active"><i class="icon-map-marker"></i> Postal Code</a></li>
				<li><a data-searchtype="state" href="#"><i class="icon-globe"></i> State/Region</a></li>
				<li><a data-searchtype="virtual" href="#"><i class="icon-external-link"></i> Virtual Location</a></li>
			</ul>
			<input type="hidden" name="togglePostal" value="true">
			<input id="feed-postal" name="postal" type="text" class="input-medium" placeholder="Postal Code" />
			<input id="feed-state" name="state" type="text" class="input-medium" placeholder="State/Region" style="display:none;"/>
			<input id="feed-virtual" name="virtual" type="text" class="input-medium" placeholder="Virtual Location" style="display:none;"/>
		</div>

		<div class="input-append">
			<input id="feed-distance" name="distance" type="text" class="span2" placeholder="Distance"/>
			<span class="add-on">mi</span>
		</div>

		<select id="feed-industry" name="industry" class="span-medium">
			<option value="">All Industries</option>
			<option value="1000">Technology and Communications</option>
			<option value="1007">Facilities and Maintenance</option>
			<option value="1014">Business and Financial Services</option>
			<option value="1016">Health and Wellness</option>
			<option value="1018">Education</option>
			<option value="1022">Legal Services</option>
			<option value="1033">Security Services</option>
			<option value="1036">Writing and Translation</option>
			<option value="1044">Design and Multimedia Production</option>
			<option value="1045">Promotional Marketing</option>
			<option value="1049">Software Development</option>
			<option value="1051">Consumer Services</option>
			<option value="1052">Administrative Support</option>
			<option value="1053">Transportation & Logistics</option>
			<option value="1054">Human Resources</option>
			<option value="1055">Hospitality</option>
			<option value="1056">Retail Merchandising and Installation</option>
			<option value="1057">Home Services</option>
			<option value="1058">Personal Services</option>
			<option value="1059">Sales and Marketing</option>
			<option value="1060">Other</option>
			<option value="1062">Enterprise Software</option>
			<option value="1064">Digital Signage</option>
			<option value="1066">Audio/Visual</option>
			<option value="1068">Property Preservation</option>
			<option value="1069">Meeting & Event Services</option>
		</select>

		<div class="work-feed--filters">
			<label>
				<input type="radio" name="when" value="all" checked/> All
			</label>

			<label>
				<input type="radio" name="when" value="today" /> Today
			</label>

			<label>
				<input type="radio" name="when" value="tomorrow" /> Tomorrow
			</label>

			<label>
				<input type="radio" name="when" value="next 7 days" /> Next 7 days
			</label>

			<button id="search-button" class="button" data-action="search">Search</button>
			<img id="spinner" src="${mediaPrefix}/images/loading.gif" alt="Loading..." height="16" width="16" />
		</div>
	</form>

	<div id="front-feed"></div>

	<hr />
	<c:if test="${not currentUser.seller}">
		<a href="/feed/build">
			<i class="icon-list"></i> Get a Work Feed
		</a>
	</c:if>
</div>
