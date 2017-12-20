define([
	'funcs/googleMap'
], function (googleMap) {
	'use strict';

	describe('googleMap', function () {
		'use strict';

		window.google = {
			maps: {
				event: {
					addListener: function () {}
				},
				LatLng: function () {},
				MapTypeId: {
					SATELLITE: '',
					HYBRID: ''
				},
				Map: function () {
					return {
						setTilt: function () {},
						setZoom: function () {},
						mapTypes: {
							set: function () {}
						},
						overlayMapTypes: {
							insertAt: function () {},
							removeAt: function () {}
						}
					};
				},
				Marker: function () {
					return {
						setIcon: function () {}
					}
				},
				MaxZoomService: function () {
					return {
						getMaxZoomAtLatLng: function () {}
					};
				},
				ImageMapType: function () {},
				Size: function () {},
				Point: function () {},
				places: {
					Autocomplete: function () {
						return {
							bindTo: function () {}
						}
					},
					AutocompleteService: function () {
						return {
							getPlacePredictions: function () {}
						};
					}
				}
			}
		};

		it('is defined', function () {
			expect(googleMap).toBeDefined();
		});

		it('returns a googleMap object', function () {
			expect(googleMap()).toEqual(jasmine.any(Object));
		});

		it('can run against a singular map', function () {
			expect(googleMap({ map: document.createElement('div') })).toBeDefined();
		});

		it('can run against multiple maps', function () {
			expect(googleMap({ maps: [ document.createElement('div'), document.createElement('div') ] })).toBeDefined();
		});

		it('uses Work Market\'s location as lat/long by default', function () {
			spyOn(window.google.maps, 'LatLng').and.callThrough();
			googleMap();
			expect(window.google.maps.LatLng).toHaveBeenCalledWith(40.749868, -73.994427);
		});
	});

});