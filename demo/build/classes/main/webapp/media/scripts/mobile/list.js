$(document).on('pagebeforeshow','#list-page',function () {
	$(function () {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(gotNewListPosition, failedNewListPosition, { enableHighAccuracy:true });
		}
	});

	function gotNewListPosition(position) {
		$.each($('.list-distance'), function (e) {
			if($(this).children('.lat').length) {
				$(this).children('.main-value').text(
						Math.ceil(geo_distance( position.coords.latitude,
								position.coords.longitude,
								$(this).find('.lat').val(),
								$(this).find('.lon').val())
						)
				)
			}
		});
	}

	function failedNewListPosition(position) {
		$.each($('.list-distance'), function (e) {
			$(this).children('.main-value').text("-");
		});
	}

	$(".details-link").click(function(e) {
		e.preventDefault();
		var id = $(this).attr('details-id');
		$.mobile.loading( 'show', {
			text: '',
			textVisible: false,
			theme: 'z',
			html: ""
		});
		$.mobile.changePage('/mobile/assignments/details/' + id);
		//location.href = '/mobile/assignments/details/' + id;

	});
	$(".details-link").hover(function(){
		$(this).css('cursor','pointer');
	})

});

