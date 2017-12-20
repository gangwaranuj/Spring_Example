import $ from 'jquery';
import _ from 'underscore';
import d3 from 'd3';
import d3Tip from 'd3-tip';
import * as topojson from 'topojson';
import '../funcs/dateFormat';

let row;
d3.tip = d3Tip;
d3.gantt = function(argWidth, argHeight) {
	var FIT_TIME_DOMAIN_MODE = "fit";

	var timeDomainStart = d3.time.day.offset(new Date(),0);
	var timeDomainEnd = d3.time.hour.offset(new Date(),0);
	var timeDomainMode = FIT_TIME_DOMAIN_MODE;// fixed or fit
	var taskTypes = [];
	var taskStatus = [];

	var margin = {top: 20, right: 40, bottom: 40, left: 90},
		width = argWidth - margin.left - margin.right,
		height = argHeight - margin.top - margin.bottom;

	var keyFunction = function(d) {
		return d.startDate + d.taskName + d.endDate;
	};

	var rectTransform = function(d) {
		return "translate(" + (5 + x(d.startDate)) + "," + y(d.taskName) + ")";
	};

	var x = d3.time.scale().domain([ timeDomainStart, timeDomainEnd ]).range([ 0, width ]).clamp(true);

	var x2 = d3.time.scale().domain([ timeDomainStart, timeDomainEnd ]).range([ 1, width ]).clamp(true);

	var y = d3.scale.ordinal().domain(taskTypes).rangeRoundBands([ 0, height - margin.top - margin.bottom ], .1);

	var xAxis = d3.svg.axis().scale(x).orient("top").tickFormat(function(d) { return "$"; }).outerTickSize(0);

	var xAxisLine = d3.svg.axis().scale(x2).orient("top").tickFormat(d3.time.format(" ")).outerTickSize(0);

	var yAxis = d3.svg.axis().scale(y).orient("left").tickSize(0);

	function gantt(tasks, tip, divTag) {
		initTimeDomain(tasks);
		initAxis();

		var svg = d3.select(divTag).append("svg")
			.attr("width", width + margin.left + margin.right)
			.attr("height", height + margin.top + margin.bottom)
			.append("g")
			.attr("transform", "translate(" + margin.left + "," + margin.top + ")")
			.attr('viewbox', '0 0 960 1000')
			.attr('preserveAspectRatio', 'xMinYMid')
			.attr('class', 'responsive');

		svg.call(tip);

		svg.selectAll(".chart")
			.data(tasks, keyFunction).enter()
			.append("rect")
			.attr("rx", 5)
			.attr("ry", 5)
			.attr("class", function(d) {
				if(taskStatus[d.status] == null) { return "bar";}
				return taskStatus[d.status];
			})
			.attr("y", 0)
			.attr("transform", rectTransform)
			.attr("height", function(d) { return y.rangeBand(); })
			.attr("width", function(d) {
				return (x(d.endDate) - x(d.startDate));
			})
			.on('mouseover', tip.show)
			.on('mouseout', tip.hide);


		svg.append("g")
			.attr("class", "x axis invisi-axes")
			.attr("transform", "translate(16, -30)")
			.transition()
			.call(xAxis);

		svg.append("g")
			.attr("class", "x axis invisiTick")
			.attr("transform", "translate(0, 0)")
			.transition()
			.call(xAxisLine);

		svg.append("g").attr("class", "y axis").transition().call(yAxis);

		return gantt;

	}

	var initTimeDomain = function(tasks) {
		if (timeDomainMode === FIT_TIME_DOMAIN_MODE) {
			if (tasks === undefined || tasks.length < 1) {
				timeDomainStart = d3.time.day.offset(new Date(), -3);
				timeDomainEnd = d3.time.hour.offset(new Date(), +3);
				return;
			}
			tasks.sort(function(a, b) {
				return a.endDate - b.endDate;
			});
			timeDomainEnd = tasks[tasks.length - 1].endDate;
			tasks.sort(function(a, b) {
				return a.startDate - b.startDate;
			});
			timeDomainStart = tasks[0].startDate;
		}
	};

	var tickFormat = "%d";

	var timeDiffFunction = function(d) {
		var t2 = new Date(0).getTime();
		var t1 = d.getTime();

		return parseInt((t1-t2)/(24*3600*1000));
	};

	var initAxis = function() {
		x = d3.time.scale().domain([ timeDomainStart, timeDomainEnd ]).range([ 0, width ]).clamp(true);
		var x2 = d3.time.scale().domain([ timeDomainStart, timeDomainEnd ]).range([ -10, width -10]).clamp(true);
		y = d3.scale.ordinal().domain(taskTypes).rangeRoundBands([ 0, height - margin.top - margin.bottom ], .1);
		xAxis = d3.svg.axis().scale(x2).orient("bottom").tickFormat(timeDiffFunction).tickSubdivide(true)
			.tickSize(8).tickPadding(8).ticks(10);

		yAxis = d3.svg.axis().scale(y).orient("left").tickSize(0);
	};

	gantt.redraw = function(tasks) {

		initTimeDomain();
		initAxis();

		var svg = d3.select("svg");

		var ganttChartGroup = svg.select(".gantt-chart");
		var rect = ganttChartGroup.selectAll("rect").data(tasks, keyFunction);

		rect.enter()
			.insert("rect",":first-child")
			.attr("rx", 5)
			.attr("ry", 5)
			.attr("class", function(d) {
				if(taskStatus[d.status] == null) { return "bar";}
				return taskStatus[d.status];
			})
			.transition()
			.attr("y", 0)
			.attr("transform", rectTransform)
			.attr("height", function(d) { return y.rangeBand(); })
			.attr("width", function(d) {
				return (x(d.endDate) - x(d.startDate));
			});

		rect.transition()
			.attr("transform", rectTransform)
			.attr("height", function(d) { return y.rangeBand(); })
			.attr("width", function(d) {
				return (x(d.endDate) - x(d.startDate));
			});

		rect.exit().remove();

		svg.select(".x").transition().call(xAxis);
		svg.select(".y").transition().call(yAxis);

		return gantt;
	};

	gantt.margin = function(value) {
		if (!arguments.length)
			return margin;
		margin = value;
		return gantt;
	};

	gantt.timeDomain = function(value) {
		if (!arguments.length)
			return [ timeDomainStart, timeDomainEnd ];
		timeDomainStart = +value[0], timeDomainEnd = +value[1];
		return gantt;
	};

	/**
	 * @param {string}
	 *                vale The value can be "fit" - the domain fits the data or
	 *                "fixed" - fixed domain.
	 */
	gantt.timeDomainMode = function(value) {
		if (!arguments.length)
			return timeDomainMode;
		timeDomainMode = value;
		return gantt;

	};

	gantt.taskTypes = function(value) {
		if (!arguments.length)
			return taskTypes;
		taskTypes = value;
		return gantt;
	};

	gantt.taskStatus = function(value) {
		if (!arguments.length)
			return taskStatus;
		taskStatus = value;
		return gantt;
	};

	gantt.width = function(value) {
		if (!arguments.length)
			return width;
		width = +value;
		return gantt;
	};

	gantt.height = function(value) {
		if (!arguments.length)
			return height;
		height = +value;
		return gantt;
	};

	gantt.tickFormat = function(value) {
		if (!arguments.length)
			return tickFormat;
		tickFormat = value;
		return gantt;
	};
	return gantt;
};

function createMinimizedBarGraph(argWidth, argHeight, assignmentsSent, divTag, title, dataFormatter) {
	function compare(a,b) {
		if (a.x < b.x)
			return -1;
		if (a.x > b.x)
			return 1;
		return 0;
	}

	assignmentsSent.sort(compare);

	var data = assignmentsSent.slice(0, 12);

	var amountFn = function(d) { return d.y };
	var dateFn = function(d) { return d.x };

	var offset = assignmentsSent[1].x - assignmentsSent[0].x;

	var margin = {top: 10, right: 0, bottom: 0, left: 0},
		width = argWidth - margin.left - margin.right,
		height = argHeight - margin.top - margin.bottom;

	// Declare axes scales
	var x = d3.time.scale()
		.range([5, width-6])
		.domain([d3.min(data, dateFn), d3.max(data, dateFn)]);

	var y = d3.scale.linear()
		.range([height, 0])
		.domain([0, d3.max(data, amountFn)]);

	// Tooltips
	var tip = createTooltip([-20, 0], function(d) {
		return (new Date(d.x - offset/2)).format("mmmm yyyy") + "<br>" +
			title + ": <span class=\"tooltipHighlight\">" + dataFormatter(d.y) + "</span>  <br>" +
			"</span>";
	});

	// Grab div element, start appending everything to it
	var svg = d3.select(divTag).append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
		.append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")")
		.attr('viewbox', '0 0 100 100')
		.attr('preserveAspectRatio', 'xMinYMid')
		.attr('class', 'responsive');

	// Append tooltip
	svg.call(tip);

	// Append bar data
	svg.selectAll(".bar")
		.data(data)
		.enter().append("rect")
		.attr("class", "bar")
		.attr("x", function(d) { return x(dateFn(d)) - 0})
		.attr("width", 6)
		.attr("y", function(d) { return y( amountFn(d)) - 2})
		.attr("height", function(d) { return 2 + Math.max(height - y(amountFn(d)), 1)})
		.attr("rx", 2)
		.on('mouseover', tip.show)
		.on('mouseout', tip.hide);
}

function populateDataForMinSegment(minSegmentId, data, title, dataFormatter){
	var element = document.getElementById(minSegmentId);
	var titleElement = element.getElementsByClassName("minimized-label-title")[0].getElementsByTagName("p")[0];
	var pctElement = element.getElementsByClassName("minimized-label-title")[0].getElementsByTagName("p")[1];
	var labelElement = element.getElementsByClassName("minimized-label-value")[0].getElementsByTagName("p")[0];

	var pv = data[10].y;
	var cv = data[11].y;

	if (pv == 0) {
		pctElement.innerHTML =  "---";
		pctElement.className += " noPercentageChange";
	} else {
		var pct = 100 * (cv - pv) / pv;
		pct = pct.toFixed(1);

		if (pct == 0){
			pctElement.innerHTML =  "0%";
			pctElement.className += " noPercentageChange";
		}
		if (pct < 0){
			pctElement.innerHTML = "&#9660" + pct + "%";
			pctElement.className += " negativePercent";
		}
		if (pct > 0){
			pctElement.innerHTML = "&#9650" + "+" + pct + "%";
			pctElement.className += " positivePercent";
		}
	}

	titleElement.innerHTML = title;
	labelElement.innerHTML = dataFormatter(cv);
}

// 0 decimal places, comma after 3 numbers
function formatterAssignmentsSent(value){
	return value.formatMoney("", 0, '.', ',');
}

// Percentage with two decimal places
function formatVoidRate(value){
	return (value*100).toFixed(1) + "%";
}

// Money no decimal places
function formatPaidAmount(value){
	return value.formatMoney("$", 0, '.', ',');
}

// Money two decimal places
function formatAveragePrice(value){
	return value.formatMoney("$", 2, '.', ',');
}

// formatter for lifeCycleDays, one decimal place, in days, from hours
function formatLifeCycleDaysHoursToDays(value){
	return (value / 24).toFixed(1);
}

// Takes data from assignmentsSent, and averagePrice.
// Creates a bar and line graph, and appends it to the specified divTag
function createAssignmentChart(argWidth, argHeight, assignmentsSent, averagePrice, divTag) {
	// Always take 13 elements
	var data = assignmentsSent.slice(0, 13);
	var lineData = averagePrice.slice(0, 13);

	var amountFn = function(d) { return d.y };
	var dateFn = function(d) { return d.x };
	var changeFn = function(d) { return d.id };

	var offset = assignmentsSent[1].x - assignmentsSent[0].x;
	var isGroupedByDay = (offset < 100000000);
	var isMonthly = (offset > 2000000000);
	var monthlyMaxDomain = d3.time.day.offset(d3.max(data, dateFn),2);

	var margin = {top: 20, right: 40, bottom: 40, left: 80},
		width = argWidth - margin.left - margin.right,
		height = argHeight - margin.top - margin.bottom;

	// Declare axes scales
	var x = d3.time.scale()
		.range([10, width-20])
		.domain([d3.min(data,dateFn) , ((isMonthly) ? monthlyMaxDomain : d3.max(data, dateFn))]);

	var x2 = d3.time.scale()
		.range([-15, width])
		.domain(d3.extent(data, dateFn));

	var xChange = d3.scale.linear()
		.range([10, width-20])
		.domain(d3.extent(data, changeFn));

	var y = d3.scale.linear()
		.range([height, 0])
		.domain([0, d3.max(data, amountFn)]);

	var y2 = d3.scale.linear()
		.range([height, 0])
		.domain([0, d3.max(lineData, amountFn)]);

	// Axis tick format functions
	var formatValue = d3.format(".2s");

	var changeFormat = function(d) {
		if (d == 0) return "Spend chg:";
		if (d == 12) return '';
		var pv = data[d-1].y;
		var cv = data[d].y;
		if (pv == 0) return "---";

		var pct = 100 * (cv - pv) / pv;
		pct = pct.toFixed(2);

		if (pct == 0) return "0%";
		if (pct < 0) return pct + "%";
		if (pct > 0) return "+" + pct + "%";
	}

	// Declare x and y axes
	var yAxisLeft = d3.svg.axis()
		.scale(y)
		.orient("left")
		.tickFormat(function(d) { return "$" + formatValue(d);})
		.outerTickSize(0);

	var yAxisRight = d3.svg.axis()
		.scale(y2)
		.orient("right")
		.tickFormat(function(d) { return "$" + formatValue(d);})
		.outerTickSize(0);

	var xAxis = d3.svg.axis()
		.scale(x)
		.tickFormat(function(d) { return isMonthly ? (new Date(d - 1).format("mmm 'yy")) : (new Date(d).format("mmm dd"));})
		.outerTickSize(0)
		.ticks(14);

	var xAxisLong = d3.svg.axis()
		.scale(x2)
		.tickFormat('')
		.outerTickSize(0);

	var xAxisChange = d3.svg.axis()
		.scale(xChange)
		.tickFormat(changeFormat)
		.outerTickSize(0)
		.ticks(14);

	// Tooltips
	var tip = createTooltip([-10, 0], function(d) {
		return ((isGroupedByDay) ? ((new Date(d.x)).format("mmm dd, yyyy")) : isMonthly ? (new Date(d.x - offset/2)).format("mmmm yyyy") : ((new Date(d.x - offset)).format("mmm dd, yyyy") + " to " + (new Date(d.x)).format("mmm dd, yyyy")))  + "<br>" +
			"Spend: <span class=\"tooltipHighlight\">" + (d.y.formatMoney("$", 2, '.', ',')) + "</span>  <br>" +
			"Volume: <span class=\"tooltipHighlight\">" + (d.count) + "</span> <br>" +
			"Average Price: <span class=\"tooltipHighlight\">" + ((d.avg.formatMoney("$", 2, '.', ','))) + "</span> <br>";
	});

	var circleTip = createTooltip([-15, 0], function(d) {
		return ((isGroupedByDay) ? ((new Date(d.x)).format("mmm dd, yyyy")) : isMonthly ? (new Date(d.x - offset/2)).format("mmmm yyyy") : ((new Date(d.x - offset)).format("mmm dd, yyyy") + " to " + (new Date(d.x)).format("mmm dd, yyyy")))  + "<br>" +
			"Average Price: <span class=\"tooltipHighlight\">" + (d.y.formatMoney("$", 2, '.', ',')) + "</span>  <br>";
	});

	// Grab div element, start appending everything to it
	var svg = d3.select(divTag).append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
		.append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")")
		.attr('viewbox', '0 0 960 1000')
		.attr('preserveAspectRatio', 'xMinYMid')
		.attr('class', 'responsive');

	// Append tooltip
	svg.call(tip);
	svg.call(circleTip);

	// Append axes
	svg.append("g")
		.attr("class", "x axis invisi-axis")
		.attr("transform", "translate(-3,291)")
		.call(xAxis)
		.append("text");

	svg.append("g")
		.attr("class", "x axis")
		.attr("transform", "translate(0,291)")
		.call(xAxisLong)
		.append("text");

	svg.append("g")
		.attr("class", "x axis invisi-axis x-axis-change")
		.attr("transform", "translate(-3,311)")
		.call(xAxisChange)
		.append("text");

	svg.append("g")
		.attr("class", "y axis")
		.attr("transform", "translate(-15,0)")
		.call(yAxisLeft)
		.append("text")
		.attr("y", 6)
		.attr("dy", ".71em");

	svg.append("g")
		.attr("class", "y axis axisRight")
		.call(yAxisRight)
		.attr("transform", "translate(" + (width) + ",0)")
		.append("text")
		.attr("y", 6)
		.attr("dy", ".71em");

	// Append bar data
	svg.selectAll(".bar")
		.data(data)
		.enter().append("rect")
		.attr("class", "bar")
		.attr("x", function(d) { return x(dateFn(d)) - 20})
		.attr("width", 40)
		.attr("y", function(d) { return y( amountFn(d))})
		.attr("height", function(d) { return Math.max(height - y(amountFn(d)), 1)})
		.attr("rx", 3)
		.attr("rx", 3)
		.on('mouseover', tip.show)
		.on('mouseout', tip.hide);

	// This part deals with the line chart
	var line = d3.svg.line()
		.x(function(d) { return x(dateFn(d)); })
		.y(function(d) { return y2(amountFn(d)); })
		.interpolate("monotone");

	svg.append("path")
		.datum(lineData)
		.attr("class", "line")
		.attr("d", line);

	svg.selectAll("circle")
		.data(lineData)
		.enter().append("circle")
		.attr("fill", "#f7961d")
		.attr("r", 5)
		.attr("cx", function(d) { return x(dateFn(d)) })
		.attr("cy", function(d) { return y(amountFn(d)*d3.max(data, amountFn)/((d3.max(lineData, amountFn) > 0) ? d3.max(lineData, amountFn) : 1))})
		.on('mouseover', circleTip.show)
		.on('mouseout', circleTip.hide);
}

// Takes data from assignmentStatistics, which represents how much time assignments stay in each stage on average
// Creates a gantt chart, and appends it to the specified divTag
function createAssignmentGanttChart(argWidth, argHeight, assignmentStatistics, divTag) {
	// Format data in a way the gantt library can use it
	var assignmentStatisticsTime = [];
	assignmentStatisticsTime.push(0);
	assignmentStatisticsTime.push(assignmentStatisticsTime[0] + assignmentStatistics[0]);
	assignmentStatisticsTime.push(assignmentStatisticsTime[1] + assignmentStatistics[1]);
	assignmentStatisticsTime.push(assignmentStatisticsTime[2] + assignmentStatistics[2]);
	assignmentStatisticsTime.push(assignmentStatisticsTime[3] + assignmentStatistics[3]);

	var assignmentData = [
		{"startDate":new Date(assignmentStatisticsTime[0]),"endDate":new Date(assignmentStatisticsTime[1]),"taskName":"Lead Time","status":"LOAD_TIME"},
		{"startDate":new Date(assignmentStatisticsTime[1]),"endDate":new Date(assignmentStatisticsTime[2]),"taskName":"Completion Time","status":"COMPLETION_TIME"},
		{"startDate":new Date(assignmentStatisticsTime[2]),"endDate":new Date(assignmentStatisticsTime[3]),"taskName":"Approval Time","status":"APPROVAL_TIME"},
		{"startDate":new Date(assignmentStatisticsTime[3]),"endDate":new Date(assignmentStatisticsTime[4]),"taskName":"Payment Time","status":"PAYMENT_TIME"},
		{"startDate":new Date(assignmentStatisticsTime[0]),"endDate":new Date(assignmentStatisticsTime[4]),"taskName":"Total Life Cycle","status":"TOTAL_LIFE_CYCLE"}];

	// Class name for coloring
	var assignmentClass = {
		"LOAD_TIME" : "bar-load-time",
		"COMPLETION_TIME" : "bar-completion-time",
		"APPROVAL_TIME" : "bar-approval-time",
		"PAYMENT_TIME" : "bar-payment-time",
		"TOTAL_LIFE_CYCLE" : "bar-total-life-cycle"
	};

	var assignmentLabels = [ "Lead Time", "Completion Time", "Approval Time", "Payment Time", "Total Life Cycle"];

	assignmentData.sort(function(a, b) {
		return a.endDate - b.endDate;
	});
	assignmentData.sort(function(a, b) {
		return a.startDate - b.startDate;
	});

	// Tooltip for gantt chart
	var tip = createTooltip([-10, 0], function(d) {
		return d.taskName + ": <span class=\"tooltipHighlight\">" + parseFloat((d.endDate - d.startDate)/(24*60*60*1000)).toFixed(2) + "</span> Days";
	});

	var gantt = d3.gantt(argWidth, argHeight).taskTypes(assignmentLabels).taskStatus(assignmentClass);
	gantt(assignmentData, tip, divTag);
}

// Takes data, a total of the data, and colors for each piece of data
// Creates a pie chart from given data and colors, and appends it to the specified divTag
function createAssignmentSegmentationGraph(argWidth, argHeight, data, sumTotal, divTag) {
	var colors = [];
	for (var i = 0; i < data.length; i++) {
		colors.push(data[i].color);
		data[i].number+=0.0001*i; // This fixes a bug with D3 pie graph coloring, leave in!
		sumTotal+=0.0001*i;
	}
	var width = argWidth,
		height = argHeight,
		radius = Math.min(width, height) / 2.7,
		color = d3.scale.ordinal()
			.range(colors),
		donut = d3.layout.pie(),
		arc = d3.svg.arc().innerRadius(radius*0.65).outerRadius(radius);

	var vis = d3.select(divTag)
		.append("svg:svg")
		.data([data])
		.attr("width", width)
		.attr("height", height);

	// tooltip data
	var tip = createTooltip([-10, 0], function(d) {
		if ((d.data.number / sumTotal) > 0.5 ) tip.offset([-10, (Math.pi - d.startAngle < d.endAngle - Math.pi) ? -10 : +10]);
		else tip.offset([-10,0]);
		return d.data.name + ": <span class=\"tooltipHighlight\">" + (parseFloat(100 * d.data.number / sumTotal).toFixed(2)) + "</span>%";
	});

	vis.call(tip);

	// Pie slices
	var arcs = vis.selectAll("g.slice")
		.data(donut.value(function(d) { return d.number; }))
		.enter().append("g")
		.attr("class", "arc")
		.attr("transform", "translate(" + (radius + 10) + "," + radius + ")");

	if (sumTotal > 0) {
		arcs.append("path")
			.attr("fill", function(d) { return color(d.data.number); })
			.attr("d", arc)
			.on('mouseover', tip.show)
			.on('mouseout', tip.hide);
	}
}

var centered;

// Creates a map of the US, and appends it to the specified div tag. Also creates table for top 10 regions, and legend table
function createDataMap(argWidth, argHeight, argScale, scaleMultiplier, countyData, topMarkets, divTag) {
	// INITIAL SIZE VAR DECLARATIONS
	var width =  argWidth * scaleMultiplier,
		height = argHeight * scaleMultiplier,
		scale =  argScale * scaleMultiplier;

	var rateById = d3.map(),
		countyNames = d3.map(),
		mapOfCountyObjectsById = d3.map(),
		maxRate = 0;

	_.each(countyData, function(data) {
		rateById.set(data.id, + data.rate);
		countyNames.set(data.id, lowercaseStringAndCapitaliseFirstLetter(data.name));
		maxRate = Math.max(maxRate, data.rate);
	});

	$.getJSON(mediaPrefix + '/us-county.json')
		.done(ready);

	var quantize = d3.scale.quantize()
		.domain([0, maxRate])
		.range(d3.range(9).map(function(i) { return "q" + i + "-9"; }));

	var projection = d3.geo.albersUsa()
		.scale(scale)
		.translate([width / 2, height / 2]);

	var path = d3.geo.path().projection(projection);

	var svg = d3.select(divTag).append("svg")
		.attr("width", width)
		.attr("height", height);

	var tip = createTooltip([-15, 0], function(d) {
		return countyNames.get(d.id) + " County" + "</br>" + "Assignments: " + "<span class=\"tooltipHighlight\">" + rateById.get(d.id) + "</span>" + "</br>";
	});

	svg.call(tip);

	var g = svg.append("g");

	generateLegend("table-legend-market", quantize);
	generateZoomButton("marketDataMapZoomControls", g, path, width, height);

	function ready(us) {
		countyData.sort(compareCountyData);
		var topojsonStates = topojson.feature(us, us.objects.states).features;
		var filteredCountyData = topojson.feature(us, us.objects.counties).features.filter( function(e) { return rateById.has(e.id);});

		g.append("g")
			.attr("class", "states")
			.selectAll("path")
			.data(topojsonStates)
			.enter().append("path")
			.attr("d", path);

		g.append("g")
			.attr("class", "counties")
			.selectAll("path")
			.data(filteredCountyData)
			.enter().append("path")
			.attr("class", function(d) {
				mapOfCountyObjectsById.set(d.id, d);
				return quantize(rateById.get(d.id));
			})
			.attr("d", path)
			.on("click", function (d) { clicked(d, g, path, width, height);})
			.on('mouseover', tip.show)
			.on('mouseout', tip.hide);

		g.append("path")
			.datum(topojson.mesh(us, us.objects.states, function(a, b) { return a !== b; }))
			.attr("class", "state-border")
			.attr("d", path);

		g.append("path")
			.datum(topojson.mesh(us, us.objects.states, function(a, b) { return a === b; }))
			.attr("class", "country-border")
			.attr("d", path);

		generateTopMarketTable("table-topMarkets", topMarkets, mapOfCountyObjectsById, g, path, width, height);
	}
}

function generateZoomButton(id, g, path, width, height) {
	var marketDataMapZoomOutButton = document.getElementById(id);
	var zoomOutButton = marketDataMapZoomOutButton.createTHead().insertRow(0).insertCell(0);
	zoomOutButton.innerHTML = "<span class=\"zoom-tooltip\"><img src=\"" + mediaPrefix + "/images/icons/zoom_out.svg\"><span><span class=\"boldTextOrange\">ZOOM OUT</span>";
	zoomOutButton.addEventListener("click", function() { clicked(centered, g, path, width, height)});
	var disclaimerLabel = marketDataMapZoomOutButton.insertRow(1).insertCell(0);
	disclaimerLabel.innerHTML = "<span class=\"zoom-disclaimer-label\">*International Market Breakdown coming soon</span>";
}

function generateLegend(legendId, quantize) {
	var elementTable = document.getElementById(legendId);
	clearChildrenOfElementById(legendId);

	var length = 9;
	var header = elementTable.createTHead();
	var firstRow = row = header.insertRow(0);

	var cell = firstRow.insertCell(0);
	cell.innerHTML = "<span class=\"boldText\">" + "Volume Range" + "</span>";
	cell.colSpan = 2;

	var prefix = "<svg class=\"legendSvg ";
	var suffix = "\"><rect x=\"0\" y=\"0\" width=\"15\" height=\"15\"></svg>";

	for (var i = length-1; i >= 0; i--) {
		var nextRow = elementTable.insertRow(elementTable.rows.length);
		var colorClass = "q" + i + "-9";
		nextRow.insertCell(0).innerHTML = prefix + colorClass + suffix ;
		nextRow.insertCell(1).appendChild(document.createTextNode(quantize.invertExtent(colorClass)[0].toFixed(1) + " - " + quantize.invertExtent(colorClass)[1].toFixed(1)));
	}

	var nextRow = elementTable.insertRow(elementTable.rows.length);
	var colorClass = "no-data";
	nextRow.insertCell(0).innerHTML = prefix + colorClass + suffix;
	nextRow.insertCell(1).appendChild(document.createTextNode("No data"));
}

function generateTopMarketTable(tableId, countyData, mapOfCountyObjectsById, g, path, width, height) {
	var topResourcesMarket = document.getElementById(tableId);
	clearChildrenOfElementById(tableId);

	var length = Math.min(10, countyData.length);
	var header = topResourcesMarket.createTHead();
	var firstRow = row = header.insertRow(0);

	firstRow.insertCell(0).innerHTML = "<span>Top " + " " + "Markets" + "</span>";
	firstRow.insertCell(1).innerHTML = "<span>Volume</span>";

	for (var i = 0; i < length; i++) {
		var nextRow = topResourcesMarket.insertRow(topResourcesMarket.rows.length);
		var elementInfo = countyData[i];

		var countyLinkElement = document.createElement('div');
		countyLinkElement.innerHTML = "<span class=\"boldTextOrange\">" + lowercaseStringAndCapitaliseFirstLetter(elementInfo.name ) + " County"+ "</span>";
		addMyEventListener(countyLinkElement, mapOfCountyObjectsById.get(elementInfo.id), g, path, width, height);
		nextRow.insertCell(0).appendChild(countyLinkElement);
		nextRow.insertCell(1).appendChild(document.createTextNode(elementInfo.rate));
	}
}

// Add custom click event to an element
function addMyEventListener(element, object, g, path, width, height) {
	element.addEventListener("click", function() { clicked(object, g, path, width, height)});
}

// Handle clicked event
function clicked(d, g, path, width, height) {
	var x, y, k, centroid;

	if (d && centered !== d) {
		centroid = path.centroid(d);
		x = centroid[0];
		y = centroid[1];
		k = 5;
		centered = d;
	} else {
		x = width / 2;
		y = height / 2;
		k = 1;
		centered = null;
	}

	g.selectAll("path")
		.classed("active", centered && function(d) { return d === centered; });

	g.transition()
		.duration(750)
		.attr("transform", "translate(" + width / 2 + "," + height / 2 + ")scale(" + k + ")translate(" + -x + "," + -y + ")")
		.style("stroke-width", 1.5 / k + "px");
}

// Compare function to sort counties
function compareCountyData(a,b) {
	if (a.rate > b.rate)
		return -1;
	if (a.rate < b.rate)
		return 1;
	return 0;
}

// Helper function to format a currency
Number.prototype.formatMoney = function(currencySymbol, decPlaces, thouSeparator, decSeparator) {
	var n = this,
		decPlaces = isNaN(decPlaces = Math.abs(decPlaces)) ? 2 : decPlaces,
		thouSeparator = thouSeparator == undefined ? "." : thouSeparator,
		decSeparator = decSeparator == undefined ? "," : decSeparator,
		sign = n < 0 ? "-" : "",
		i = parseInt(n = Math.abs(+n || 0).toFixed(decPlaces)) + "",
		j = (j = i.length) > 3 ? j % 3 : 0;
	return currencySymbol + sign + (j ? i.substr(0, j) + decSeparator : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + decSeparator) + (decPlaces ? thouSeparator + Math.abs(n - i).toFixed(decPlaces).slice(2) : "");
};

// Helper function to standardize proper nouns of counties, states, etc
function lowercaseStringAndCapitaliseFirstLetter(string) {
	var str = string.toLowerCase();
	var pieces = str.split(" ");
	for ( var i = 0; i < pieces.length; i++ ) {
		var j = pieces[i].charAt(0).toUpperCase();
		pieces[i] = j + pieces[i].substr(1);
	}
	return pieces.join(" ");
}

// Helper function to clear any children from an element, for refreshing data
function clearChildrenOfElementById(id) {
	var node = document.getElementById(id);
	while (node.hasChildNodes()) {
		node.removeChild(node.lastChild);
	}
}

// Helper method to populate table with data
function populateHTMLTable(elementType, throughputLabel, elementTable, elementData, useFullName) {
	var length = elementData.length;
	var header = elementTable.createTHead();
	var firstRow = row = header.insertRow(0);

	firstRow.insertCell(0).innerHTML = "<span>Top " + " " + elementType + "</span>";
	firstRow.insertCell(1).innerHTML = "<span>" + throughputLabel + "</span>";
	firstRow.insertCell(2).innerHTML = "<span>Volume</span>";
	firstRow.insertCell(3).innerHTML = "<span>Average Price</span>";

	for (var i = 0; i < length; i++) {
		var nextRow = elementTable.insertRow(elementTable.rows.length);
		var elementInfo = elementData[i];
		nextRow.insertCell(0).appendChild(document.createTextNode(useFullName ? elementInfo.firstName + " " + elementInfo.lastName : elementInfo.name));
		nextRow.insertCell(1).appendChild(document.createTextNode(elementInfo.throughput.formatMoney("$", 2, '.', ',')));
		nextRow.insertCell(2).appendChild(document.createTextNode(elementInfo.sentAssignments));
		nextRow.insertCell(3).appendChild(document.createTextNode((elementInfo.throughput / elementInfo.sentAssignments).formatMoney("$", 2, '.', ',')));
	}
}

function createTooltip(offset, messageFunction) {
	return d3.tip()
		.attr('class', 'd3-tip')
		.offset(offset)
		.html(messageFunction);
}

// On click, ajax request with buttonId as param, will return data relevant to button pressed
function myClickFunction(el, els, alternateId) {
	for (var i = 0; i < els.length; i++) {
		els[i].className = els[i].className.replace('active','')
	}
	el.className += " active";

	var data = {"id": config.companyId, requestId: ((alternateId !== undefined) ? alternateId : el.id )};
	$.ajax({
		url: '/reports/statisticsAjaxReload',
		type: 'GET',
		dataType: 'json',
		data: data,
		success: function (response) {
			if (response.successful &&  response.data !== undefined && response.data.ajaxResponse != 0) {
				refreshPanel(response.data.ajaxResponse, ((alternateId !== undefined) ? alternateId : el.id ));
			} else {
				clearPanel(((alternateId !== undefined) ? alternateId : el.id ));
				console.log('FAILED AJAX REQUEST');
			}
		}
	});
}

// Use in case of error or exception, show empty data
function clearPanel(id) {
	switch(id) {
		case "button-throughput-daily":
		case "button-throughput-weekly":
		case "button-throughput-monthly":
			clearChildrenOfElementById("assignmentsBarGraph");
			break;
		case "button-life-cycle-monthly":
		case "button-life-cycle-quarterly":
		case "button-life-cycle-yearly":
			clearChildrenOfElementById("ganttChart");
			break;
		case "button-segmentation-assignment-monthly":
		case "button-segmentation-assignment-quarterly":
		case "button-segmentation-assignment-yearly":
			clearChildrenOfElementById("assignmentPieGraph");
			break;
		case "button-segmentation-routing-monthly":
		case "button-segmentation-routing-quarterly":
		case "button-segmentation-routing-yearly":
			clearChildrenOfElementById("assignmentPieGraphSecond");
			break;
		case "button-clients-monthly":
		case "button-clients-quarterly":
		case "button-clients-yearly":
			clearChildrenOfElementById("table-topClients");
			var topClientsTable = document.getElementById("table-topClients");
			var header   = topClientsTable.createTHead();
			var firstRow = row = header.insertRow(0);
			firstRow.insertCell(0).innerHTML = "<b>Top Users</b>";
			firstRow.insertCell(1).innerHTML = "<b>Spend</b>";
			firstRow.insertCell(2).innerHTML = "<b>Volume</b>";
			firstRow.insertCell(3).innerHTML = "<b>Average Price</b>";

			clearChildrenOfElementById("table-topProjects");
			var topProjectsTable = document.getElementById("table-topProjects");
			header   = topProjectsTable.createTHead();
			firstRow = row = header.insertRow(0);
			firstRow.insertCell(0).innerHTML = "<b>Top Projects</b>";
			firstRow.insertCell(1).innerHTML = "<b>Spend</b>";
			firstRow.insertCell(2).innerHTML = "<b>Volume</b>";
			firstRow.insertCell(3).innerHTML = "<b>Average Price</b>";
			break;
		case "button-market-monthly":
		case "button-market-quarterly":
		case "button-market-yearly":
			clearChildrenOfElementById("table-topResources");
			clearChildrenOfElementById("marketDataMap");
			break;
		default:
	}
}

// Depending on what button was pressed, refresh the corresponding segment on the page
function refreshPanel(ajaxResponse, id) {
	var jsreports = $.parseJSON( ajaxResponse );
	switch(id) {
		case "button-throughput-daily":
		case "button-throughput-weekly":
		case "button-throughput-monthly":
			var assignmentsSent = [];
			var averagePrice = [];
			for(var i = 0; i < jsreports.assignmentsSent.chartData.length; i++) {
				var assignmentsSentI = jsreports.assignmentsSent.chartData[i].y;
				var totalValueInAssignmentsSentI = jsreports.totalValueInAssignmentsSent.chartData[i].y;
				var avgAssignmentsSentI = (totalValueInAssignmentsSentI / ((assignmentsSentI > 0) ? assignmentsSentI : 1));
				var timeI = jsreports.assignmentsSent.chartData[i].x;

				assignmentsSent.push({ id: i, x: timeI , y: totalValueInAssignmentsSentI, avg:avgAssignmentsSentI, count:assignmentsSentI});
				averagePrice.push({id: i, x: timeI, y: avgAssignmentsSentI });
			}
			clearChildrenOfElementById("assignmentsBarGraph");
			createAssignmentChart(1000, 350, assignmentsSent, averagePrice, "#assignmentsBarGraph");
			break;
		case "button-life-cycle-monthly":
		case "button-life-cycle-quarterly":
		case "button-life-cycle-yearly":
			var assignmentStatistics = [];
			assignmentStatistics.push(jsreports.AVERAGE_HOURS_ASSIGNMENT_SENT_TO_START.chartData[0].y * 3600 * 1000);
			assignmentStatistics.push(jsreports.AVERAGE_HOURS_ASSIGNMENT_START_TO_COMPLETE.chartData[0].y * 3600 * 1000);
			assignmentStatistics.push(jsreports.AVERAGE_HOURS_ASSIGNMENT_COMPLETE_TO_CLOSED.chartData[0].y * 3600 * 1000);
			assignmentStatistics.push(jsreports.AVERAGE_HOURS_ASSIGNMENT_CLOSED_TO_PAID.chartData[0].y * 3600 * 1000);

			clearChildrenOfElementById("ganttChart");
			createAssignmentGanttChart(400, 300, assignmentStatistics, "#ganttChart");

			break;
		case "button-segmentation-assignment-monthly":
		case "button-segmentation-assignment-quarterly":
		case "button-segmentation-assignment-yearly":
			var assignmentSegmentationReport = jsreports.assignmentSegmentationReportAssignment;
			var data = [
				{name: "Cancel", number: assignmentSegmentationReport.cancelRate, color: "#b1844f"},
				{name: "Void", number: assignmentSegmentationReport.voidRate, color: "#ffbe6b"},
				{name: "Paid", number: assignmentSegmentationReport.paidRate, color: "#f7961d"},
				{name: "Active", number: Math.max(0, (assignmentSegmentationReport.sentRate - assignmentSegmentationReport.paidRate - assignmentSegmentationReport.voidRate - assignmentSegmentationReport.cancelRate)), color: "#E9772E"}
			];
			var total = assignmentSegmentationReport.sentRate;

			clearChildrenOfElementById("assignmentPieGraph");
			createAssignmentSegmentationGraph(200, 400, data, total, "#assignmentPieGraph");
			break;
		case "button-segmentation-routing-monthly":
		case "button-segmentation-routing-quarterly":
		case "button-segmentation-routing-yearly":
			var assignmentSegmentationReport = jsreports.assignmentSegmentationReportRouting;

			var data = [
				{name: "WorkSend", number: assignmentSegmentationReport.workSend, color: "#ccd2d6"},
				{name: "User Send", number: assignmentSegmentationReport.userSend, color: "#40b0ed"},
				{name: "Groups", number: assignmentSegmentationReport.groups, color: "#9bddf9"},
				{name: "Search", number: assignmentSegmentationReport.search, color: "#7b99a6"}
			];
			total = assignmentSegmentationReport.workSend + assignmentSegmentationReport.userSend + assignmentSegmentationReport.groups + assignmentSegmentationReport.search;

			clearChildrenOfElementById("assignmentPieGraphSecond");
			createAssignmentSegmentationGraph(200, 400, data, total, "#assignmentPieGraphSecond");
			break;
		case "button-clients-monthly":
		case "button-clients-quarterly":
		case "button-clients-yearly":
			var topClientsTable = document.getElementById("table-topClients");
			clearChildrenOfElementById("table-topClients");
			populateHTMLTable("Users", "Spend", topClientsTable, jsreports.topUsers, true);

			var topProjectsTable = document.getElementById("table-topProjects");
			clearChildrenOfElementById("table-topProjects");
			populateHTMLTable("Projects", "Spend", topProjectsTable, jsreports.topProjects, false);
			break;
		case "button-market-monthly":
		case "button-market-quarterly":
		case "button-market-yearly":
			var topResourcesMarket = document.getElementById("table-topResources");
			clearChildrenOfElementById("table-topResources");
			populateHTMLTable("Resources", "Paid", topResourcesMarket, jsreports.topResources, false);

			clearChildrenOfElementById("marketDataMap");
			clearChildrenOfElementById("marketDataMapZoomControls");
			createDataMap(500, 320, 600, 1.65, jsreports.countyData, jsreports.topMarkets, "#marketDataMap");
			break;
		case "button-snapshot-default":
			var assignmentsSentData = [];
			var voidRateData = [];
			var paidAmountData = [];
			var averagePriceData = [];
			var lifeCycleData = [];
			var assignmentsSentI, voidRateI, paidAmountI, averagePriceI, lifeCycleI, timeI;

			for (var i = 0; i < jsreports.snapshotReport.snapshotDataPoints.length; i++){

				assignmentsSentI = jsreports.snapshotReport.snapshotDataPoints[i].assignmentsSentCount;
				voidRateI = jsreports.snapshotReport.snapshotDataPoints[i].voidRate;
				paidAmountI = jsreports.snapshotReport.snapshotDataPoints[i].assignmentsSent;
				averagePriceI = paidAmountI/((assignmentsSentI > 0) ? assignmentsSentI : 1);
				lifeCycleI = jsreports.snapshotReport.snapshotDataPoints[i].lifeCycleDays;
				timeI = jsreports.snapshotReport.snapshotDataPoints[i].timeInMillis;

				assignmentsSentData.push({ id: i, x: timeI , y: assignmentsSentI});
				voidRateData.push({ id: i, x: timeI , y: voidRateI});
				paidAmountData.push({ id: i, x: timeI , y: paidAmountI});
				averagePriceData.push({ id: i, x: timeI , y: averagePriceI});
				lifeCycleData.push({ id: i, x: timeI , y: lifeCycleI});
			}

			createMinimizedBarGraph(85, 40, assignmentsSentData, ("#snapshot-min-1-content"), "Assignments Sent", formatterAssignmentsSent);
			populateDataForMinSegment("min1", assignmentsSentData, "Assignments Sent", formatterAssignmentsSent);

			createMinimizedBarGraph(85, 40, voidRateData, ("#snapshot-min-2-content"), "Void Rate", formatVoidRate);
			populateDataForMinSegment("min2", voidRateData, "Void Rate", formatVoidRate);

			createMinimizedBarGraph(85, 40, paidAmountData, ("#snapshot-min-3-content"), "Paid Amount", formatPaidAmount);
			populateDataForMinSegment("min3", paidAmountData, "Paid Amount", formatPaidAmount);

			createMinimizedBarGraph(85, 40, averagePriceData, ("#snapshot-min-4-content"), "Average Price", formatAveragePrice);
			populateDataForMinSegment("min4", averagePriceData, "Average Price", formatAveragePrice);

			createMinimizedBarGraph(85, 40, lifeCycleData, ("#snapshot-min-5-content"), "Life Cycle Days", formatLifeCycleDaysHoursToDays);
			populateDataForMinSegment("min5", lifeCycleData, "Life Cycle Days", formatLifeCycleDaysHoursToDays);
			break;
		default:
			console.log("Invalid ID, no corresponding panel to refresh");
	}
}

function handleCustomButtonTooltip(buttonElement, tooltipId, tooltipMessage, position) {
	var flip = (position == "Bottom");
	var tooltip = document.getElementById(tooltipId);
	tooltip.setAttribute("style","width:0px; height:0px; display:none;");
	buttonElement.addEventListener("mouseover", function() {
		var marginTop = flip ? 50 : -50;
		tooltip.innerHTML = tooltipMessage
		tooltip.setAttribute("style","display:inline-block; margin-top: " + marginTop + "px; margin-left: " + 0 + "px");
		var rectButton = buttonElement.getBoundingClientRect();
		var rectTooltip = tooltip.getBoundingClientRect();
		tooltip.setAttribute("style","display:inline-block; margin-top: " + marginTop + "px; margin-left: " + ((((rectButton.left + rectButton.right))/2) - ((rectTooltip.left + rectTooltip.right)/2)) + "px");
	});
	buttonElement.addEventListener("mouseout", function() {
		tooltip.setAttribute("style","display:none");
	});
}

export default function () {
	var btnThroughputDaily = document.getElementById("button-throughput-daily");
	var btnThroughputWeekly = document.getElementById("button-throughput-weekly");
	var btnThroughputMonthly = document.getElementById("button-throughput-monthly");
	btnThroughputDaily.addEventListener("click", function() { myClickFunction(btnThroughputDaily, [btnThroughputDaily, btnThroughputWeekly, btnThroughputMonthly])});
	btnThroughputWeekly.addEventListener("click", function() { myClickFunction(btnThroughputWeekly, [btnThroughputDaily, btnThroughputWeekly, btnThroughputMonthly])});
	btnThroughputMonthly.addEventListener("click", function() { myClickFunction(btnThroughputMonthly, [btnThroughputDaily, btnThroughputWeekly, btnThroughputMonthly])});

	var btnLifeCycleMonthly = document.getElementById("button-life-cycle-monthly");
	var btnLifeCycleQuarterly = document.getElementById("button-life-cycle-quarterly");
	var btnLifeCycleYearly = document.getElementById("button-life-cycle-yearly");
	btnLifeCycleMonthly.addEventListener("click", function() { myClickFunction(btnLifeCycleMonthly, [btnLifeCycleMonthly, btnLifeCycleQuarterly, btnLifeCycleYearly])});
	btnLifeCycleQuarterly.addEventListener("click", function() { myClickFunction(btnLifeCycleQuarterly, [btnLifeCycleMonthly, btnLifeCycleQuarterly, btnLifeCycleYearly])});
	btnLifeCycleYearly.addEventListener("click", function() { myClickFunction(btnLifeCycleYearly, [btnLifeCycleMonthly, btnLifeCycleQuarterly, btnLifeCycleYearly])});

	var btnSegmentationMonthly = document.getElementById("button-segmentation-monthly");
	var btnSegmentationQuarterly = document.getElementById("button-segmentation-quarterly");
	var btnSegmentationYearly = document.getElementById("button-segmentation-yearly");
	btnSegmentationMonthly.addEventListener("click", function() { myClickFunction(btnSegmentationMonthly, [btnSegmentationMonthly, btnSegmentationQuarterly, btnSegmentationYearly], "button-segmentation-assignment-monthly")});
	btnSegmentationQuarterly.addEventListener("click", function() { myClickFunction(btnSegmentationQuarterly, [btnSegmentationMonthly, btnSegmentationQuarterly, btnSegmentationYearly], "button-segmentation-assignment-quarterly")});
	btnSegmentationYearly.addEventListener("click", function() { myClickFunction(btnSegmentationYearly, [btnSegmentationMonthly, btnSegmentationQuarterly, btnSegmentationYearly], "button-segmentation-assignment-yearly")});

	btnSegmentationMonthly.addEventListener("click", function() { myClickFunction(btnSegmentationMonthly, [btnSegmentationMonthly, btnSegmentationQuarterly, btnSegmentationYearly], "button-segmentation-routing-monthly")});
	btnSegmentationQuarterly.addEventListener("click", function() { myClickFunction(btnSegmentationQuarterly, [btnSegmentationMonthly, btnSegmentationQuarterly, btnSegmentationYearly], "button-segmentation-routing-quarterly")});
	btnSegmentationYearly.addEventListener("click", function() { myClickFunction(btnSegmentationYearly, [btnSegmentationMonthly, btnSegmentationQuarterly, btnSegmentationYearly], "button-segmentation-routing-yearly")});

	var btnClientsMonthly = document.getElementById("button-clients-monthly");
	var btnClientsQuarterly = document.getElementById("button-clients-quarterly");
	var btnClientsYearly = document.getElementById("button-clients-yearly");
	btnClientsMonthly.addEventListener("click", function() { myClickFunction(btnClientsMonthly, [btnClientsMonthly, btnClientsQuarterly, btnClientsYearly])});
	btnClientsQuarterly.addEventListener("click", function() { myClickFunction(btnClientsQuarterly, [btnClientsMonthly, btnClientsQuarterly, btnClientsYearly])});
	btnClientsYearly.addEventListener("click", function() { myClickFunction(btnClientsYearly, [btnClientsMonthly, btnClientsQuarterly, btnClientsYearly])});

	var btnMarketMonthly = document.getElementById("button-market-monthly");
	var btnMarketQuarterly = document.getElementById("button-market-quarterly");
	var btnMarketYearly = document.getElementById("button-market-yearly");
	btnMarketMonthly.addEventListener("click", function() { myClickFunction(btnMarketMonthly, [btnMarketMonthly, btnMarketQuarterly, btnMarketYearly])});
	btnMarketQuarterly.addEventListener("click", function() { myClickFunction(btnMarketQuarterly, [btnMarketMonthly, btnMarketQuarterly, btnMarketYearly])});
	btnMarketYearly.addEventListener("click", function() { myClickFunction(btnMarketYearly, [btnMarketMonthly, btnMarketQuarterly, btnMarketYearly])});

	handleCustomButtonTooltip(btnThroughputDaily, "btn-group-throughput-tooltip", "Daily spend", "Bottom");
	handleCustomButtonTooltip(btnThroughputWeekly, "btn-group-throughput-tooltip", "Weekly spend", "Bottom");
	handleCustomButtonTooltip(btnThroughputMonthly, "btn-group-throughput-tooltip", "Monthly spend", "Bottom");

	handleCustomButtonTooltip(btnLifeCycleMonthly, "btn-group-life-cycle-tooltip", "Life cycle data summary for the month.", "Top");
	handleCustomButtonTooltip(btnLifeCycleQuarterly, "btn-group-life-cycle-tooltip", "Life cycle data summary for the quarter.", "Top");
	handleCustomButtonTooltip(btnLifeCycleYearly, "btn-group-life-cycle-tooltip", "Life cycle data summary for the year.", "Top");;

	handleCustomButtonTooltip(btnSegmentationMonthly, "btn-group-segmentation-tooltip", "Assignment category data summary for the month.", "Top");
	handleCustomButtonTooltip(btnSegmentationQuarterly, "btn-group-segmentation-tooltip", "Assignment category data summary for the quarter.", "Top");
	handleCustomButtonTooltip(btnSegmentationYearly, "btn-group-segmentation-tooltip", "Assignment category data summary for the year.", "Top");

	handleCustomButtonTooltip(btnClientsMonthly, "btn-group-clients-tooltip", "Client data summary for the month.", "Top");
	handleCustomButtonTooltip(btnClientsQuarterly, "btn-group-clients-tooltip", "Client data summary for the quarter.", "Top");
	handleCustomButtonTooltip(btnClientsYearly, "btn-group-clients-tooltip", "Client data summary for the the year.", "Top");

	handleCustomButtonTooltip(btnMarketMonthly, "btn-group-market-tooltip", "Market data summary for the month.", "Top");
	handleCustomButtonTooltip(btnMarketQuarterly, "btn-group-market-tooltip", "Market data summary for the quarter.", "Top");
	handleCustomButtonTooltip(btnMarketYearly, "btn-group-market-tooltip", "Market data summary for the year.", "Top");

	// To make the page start with different data, change the button
	myClickFunction(btnThroughputMonthly, [btnThroughputDaily, btnThroughputWeekly, btnThroughputMonthly]);
	myClickFunction(btnLifeCycleYearly, [btnLifeCycleMonthly, btnLifeCycleQuarterly, btnLifeCycleYearly]);
	myClickFunction(btnSegmentationYearly, [btnSegmentationMonthly, btnSegmentationQuarterly, btnSegmentationYearly], "button-segmentation-assignment-yearly");
	myClickFunction(btnSegmentationYearly, [btnSegmentationMonthly, btnSegmentationQuarterly, btnSegmentationYearly], "button-segmentation-routing-yearly");
	myClickFunction(btnClientsYearly, [btnClientsMonthly, btnClientsQuarterly, btnClientsYearly]);
	myClickFunction(btnMarketYearly, [btnMarketMonthly, btnMarketQuarterly, btnMarketYearly]);

	//VERY TEMPORARY WAY OF RELOADING DATA WITHOUT BUTTONS
	var snapshotButton = document.createElement('div');
	snapshotButton.setAttribute("id", "button-snapshot-default");
	myClickFunction(snapshotButton, []);

	var throughputExplanationTooltipElement = document.getElementById("information-tooltip-throughput");
	var throughputTooltipHTML =
		"<span class=\"explanationTooltipContent\">" +
		"<p class=\"boldTextOrange\">" + "Assignment Spend" + "</p>" +
		"<p>" + "Assignment Spend chart shows the company’s assignment throughput trend over time, with daily, monthly and quarterly trends." + "</p>" +
		"<ul>" +
		"<li><strong><em>" + "Spend " + "</em></strong>" + "is the sum of all paid assignment value for the time period." + "</li>" +
		"<li><strong><em>" + "Volume " + "</em></strong>" + "is the number of paid assignments for the time period." + "</li>" +
		"<li><strong><em>" + "Average Price " + "</em></strong>" + "is the average price of all paid assignments for the time period." + "</li>" +
		"<li><strong><em>" + "Spend Change " + "</em></strong>" + "is the percentage change of the throughput from one time period to the next time period." + "</li>" +
		"</ul>" + "</span>";

	handleCustomButtonTooltip(throughputExplanationTooltipElement, "btn-group-throughput-tooltip", throughputTooltipHTML, "Bottom");

	var lifeCycleExplanationTooltipElement = document.getElementById("information-tooltip-life-cycle");
	var lifeCycleTooltipHTML =
		"<span class=\"explanationTooltipContent\">" +
		"<p class=\"boldTextOrange\">" + "Assignment Life" + "</p>" +
		"<p>" + "Assignment Life Cycle chart indicates the average time it takes for the company’s assignments to move through the Work Market platform, from assignment routing to assignment payment." + "</p>" +
		"<ul>" +
		"<li><strong><em>" + "Lead Time " + "</em></strong>" + "is the average time from assignment sent date to assignment start date." + "</li>" +
		"<li><strong><em>" + "Completion Time " + "</em></strong>" + "is the average time from assignment start date to assignment completion date. It represents the time it takes for a worker to complete the assignment." + "</li>" +
		"<li><strong><em>" + "Approval Time " + "</em></strong>" + "is the average time from assignment completion date to assignment approval date. It represents the time it take for a company to approve an assignment for payment." + "</li>" +
		"<li><strong><em>" + "Payment Time " + "</em></strong>" + "is the average time from assignment approval date to assignment payment date. It represents the time it takes for a company to pay an assignment after approval." + "</li>" +
		"<li><strong><em>" + "Total Life Cycle " + "</em></strong>" + "is the summation of Lead Time, Completion Time, Approval Time, and Payment Time." + "</li>" +
		"</ul>" + "</span>";
	handleCustomButtonTooltip(lifeCycleExplanationTooltipElement, "btn-group-life-cycle-tooltip-south", lifeCycleTooltipHTML, "Bottom");


	var segmentationExplanationTooltipElement = document.getElementById("information-tooltip-segmentation");
	var segmentationTooltipHTML =
		"<span class=\"explanationTooltipContent\">" +
		"<p class=\"boldTextOrange\">" + "Segmentation" + "</p>" +
		"<p>" + "Assignment Breakdown:" + "</p>" +
		"<ul>" +
		"<li><strong><em>" + "Void " + "</em></strong>" + "is the percentage of routed assignment that are voided." + "</li>" +
		"<li><strong><em>" + "Cancel " + "</em></strong>" + "is the percentage of routed assignment that are cancelled." + "</li>" +
		"<li><strong><em>" + "Paid " + "</em></strong>" + "is the percentage of routed assignment that are paid." + "</li>" +
		"<li><strong><em>" + "Active " + "</em></strong>" + "is the percentage of routed of assignments that are sent, assigned, in progress, pending approval, and invoiced." + "</li>" +
		"</ul>" +
		"<br/>" +
		"<p>" + "Routing Breakdown:" + "</p>" +
		"<ul>" +
		"<li><strong><em>" + "WorkSend " + "</em></strong>" + "is the percentage of assignments that are routed via the WorkSend feature." + "</li>" +
		"<li><strong><em>" + "User Send " + "</em></strong>" + "is the percentage of assignments that are routed to direct workers." + "</li>" +
		"<li><strong><em>" + "Groups " + "</em></strong>" + "is the percentage of assignments that are routed to groups." + "</li>" +
		"<li><strong><em>" + "Search " + "</em></strong>" + "is the percentage of assignments that are routed via people search." + "</li>" +
		"</ul>" +"</span>";
	handleCustomButtonTooltip(segmentationExplanationTooltipElement, "btn-group-segmentation-tooltip-south", segmentationTooltipHTML, "Bottom");

};
