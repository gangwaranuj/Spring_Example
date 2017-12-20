function getFindWorkCount () {
	var lat = $('#home-lat').val();
	var lon = $('#home-lon').val();
	var latlonurl = '';
	if(lat){
		latlonurl += '&lat=' + lat;
	}
	if(lon){
		latlonurl += '&lon=' + lon;
	}
	$.get("/mobile/assignments/findwork/count?page=1" + latlonurl, function (data) {
		$('#findwork-count').text(data.totalCount);
	});
}

function getCounts () {
	$.get("/mobile/counts",function (data) {
		$('#available-count').text(data['available'].statusCount);
		if (data['applied']) {
			$('#applied-count').text(data['applied'].statusCount);
		}
		$('#inprogress-count').text(data['inprogress'].statusCount);
		$('#assigned-count').text(data['active'].statusCount);
		$('#pending-count').text(data['complete'].statusCount);
		$('#invoiced-count').text(data['paymentPending'].statusCount);
		$('#paid-count').text(data['paid'].statusCount);
	});
}

function gotNewHomePosition (position) {
	$('#home-lat').val(position.coords.latitude);
	$('#home-lon').val(position.coords.longitude);
	getFindWorkCount();
}

function failedNewHomePosition () {
	$('#home-lat').val('-999');
	$('#home-lon').val('-999');
	getFindWorkCount();
}

$(document).on('pagebeforeshow','#home-page', function () {
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(gotNewHomePosition, failedNewHomePosition);
	}
	$('#find-work').click(function (e) {
		e.preventDefault();
		var lat = $('#home-lat').val();
		var lon = $('#home-lon').val();
		$.mobile.changePage('/mobile/assignments/findwork?page=1&lat=' + lat + '&lon=' + lon);
	});
	getFindWorkCount();
	getCounts();
});
