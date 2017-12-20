$.fn.socialize = function() {
	var api = {
		linkedIn: function(data) {
			return 'http://www.linkedin.com/shareArticle?' + $.param(data);
		},

		facebook: function(data) {
			data = {
				s: 100,
				p: {
					title: data.title,
					url: data.url,
					summary: data.summary,
					images: {
						'0': data.image
					}
				}
			};
			return 'https://www.facebook.com/sharer/sharer.php?' + $.param(data);
		},

		twitter: function(data) {
			data = {
				url: data.url,
				text: data.text
			};
			return 'https://twitter.com/intent/tweet?' + $.param(data);
		}
	};

	var open = function(e) {
		e.preventDefault();
		window.open(e.data.url, "_blank","toolbar=no,menubar=0,status=0,copyhistory=0,scrollbars=yes,resizable=1,location=0,Width=550,Height=525");
	};

	this.each(function(index) {
		var $this = $(this);
		var on = $this.data('socialize');
		var data = $this.data();
		delete data.socialize;
		$this.on('click', {url: api[on](data)}, open);
	});
};
