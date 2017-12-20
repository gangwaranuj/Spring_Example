var googlePlaces = googlePlaces  || {};

googlePlaces.auto_complete = function () {

	var input = document.getElementById('addressTyper');
	var autocomplete = new google.maps.places.Autocomplete(input);

	function getReverseGeocodingDataForPostalCode(lat, lng) {
		var latlng = new google.maps.LatLng(lat, lng);
		// This is the reverse geocode request
		var geocoder = new google.maps.Geocoder();
		geocoder.geocode({ 'latLng': latlng }, function (results) {
			if (results != null) {
				var result = results[0].address_components;

				for (var i = 0; i<result.length; ++i) {
					if (result[i].types[0] == "postal_code") {
						postal = result[i].long_name;
					}
				}
				$("#postalCode").val(postal);
			}
		});
	}

	google.maps.event.addListener(autocomplete, 'place_changed', function() {

		$("#postalCode").val('');
		$("#country").val('');
		$("#state").val('');
		$("#city").val('');
		$("#longitude").val('');
		$("#latitude").val('');
		$("#address1").val('');

		var place = autocomplete.getPlace();
		if (place.address_components) {
			for(comp in place.address_components){
				for(type in place.address_components[comp].types){
					var addressComponent = place.address_components[comp].types[type];
					var lat = place.geometry.location.lat();
					var lng = place.geometry.location.lng();

					if (addressComponent == 'postal_code' && place.address_components[comp].short_name != null) {
						$("#postalCode").val(place.address_components[comp].short_name);
					} else {
						// if user did not provide us with address or postal code use lat long to reverse geo-code postal
						getReverseGeocodingDataForPostalCode(lat, lng);
					}
					if (addressComponent == 'country' && place.address_components[comp].short_name != null)$("#country").val(place.address_components[comp].short_name);
					if (addressComponent == 'administrative_area_level_1' && place.address_components[comp].short_name != null)$("#state").val(place.address_components[comp].short_name);
					if (addressComponent == 'administrative_area_level_2' && place.address_components[comp].short_name != null && $("#state").val() == '')$("#state").val(place.address_components[comp].short_name);
					if (addressComponent == 'locality' && place.address_components[comp].long_name != null)$("#city").val(place.address_components[comp].long_name);
					if (addressComponent == 'sublocality' && place.address_components[comp].long_name != null && $("#city").val() == '')$("#city").val(place.address_components[comp].long_name);
					if (addressComponent == 'administrative_area_level_1' && place.address_components[comp].long_name != null && $("#city").val() == '')$("#city").val(place.address_components[comp].long_name);
					if (addressComponent == 'administrative_area_level_2' && place.address_components[comp].long_name != null && $("#city").val() == '')$("#city").val(place.address_components[comp].long_name);
					if (addressComponent == 'street_number' && place.address_components[comp].short_name != null)$("#address1").val(place.address_components[comp].short_name);
					if (addressComponent == 'route' && place.address_components[comp].short_name != null)$("#address1").val($("#address1").val() + " " +  place.address_components[comp].short_name);
				}
			}
		}

		if (place.formatted_address) {
			$("#formatted_address").val(place.formatted_address);
		}

		$("#latitude").val(lat);
		$("#longitude").val(lng);
	});

	var input = document.getElementById('addressTyper');
	google.maps.event.addDomListener(input, 'keydown', function(e) {
		if (e.keyCode == 13)
		{
			if (e.preventDefault) {
				e.preventDefault();
			} else {
				// Since the google event handler framework does not handle early IE versions, we have to do it by our self. :-(
				e.cancelBubble = true;
				e.returnValue = false;
			}
		}
	});

};
