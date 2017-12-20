<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<li>
	<input type="checkbox" name="workinghours[<c:out value='${param.row_i}'/>][active]" value="1" id="day_<c:out value='${param.row_i}'/>" />
	<label for="day_<c:out value='${param.row_i}'/>" style="width:70px;display:inline-block;">
		<c:out value='${wmfmt:weekdayName(param.row_i)}'/>
	</label>
	
	<input type="text" name="workinghours[<c:out value='${param.row_i}'/>][time_from]" value="8:30am" id="time_from_<c:out value='${param.row_i}'/>" class="hours" />
	<span class="ml mr">to</span>
	<input type="text" name="workinghours[<c:out value='${param.row_i}'/>][time_to]" value="6:30pm" id="time_to_<c:out value='${param.row_i}'/>" class="hours" />
</li>