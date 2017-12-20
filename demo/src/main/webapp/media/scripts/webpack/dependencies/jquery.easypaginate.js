/*
 * 	Easy Paginate 1.0 - jQuery plugin
 *	written by Alen Grakalic
 *	http://cssglobe.com/
 *
 *	Copyright (c) 2011 Alen Grakalic (http://cssglobe.com)
 *	Dual licensed under the MIT (MIT-LICENSE.txt)
 *	and GPL (GPL-LICENSE.txt) licenses.
 *
 *	Built for jQuery library
 *	http://jquery.com
 *
 *	*** This file was modified by WM ***
 *
 */

(function($) {

	$.fn.easyPaginate = function(options){

		var defaults = {
			step: 4,
			delay:100, //modified for WM
			numeric: true,
			maxNumericNav: 10, //added for WM
			nextprev: true,
			auto:false,
			loop:false,
			pause:4000,
			clickstop:true,
			controls: 'pagination',
			current: 'current',
			randomstart: false
		};

		var options = $.extend(defaults, options);
		var step = options.step;
		var lower, upper;
		var count = options.count || $(this).children().length;
		var obj, next, prev;
		var pages = Math.ceil(count/step);
		var page = (options.randomstart) ? Math.floor(Math.random()*pages)+1 : 1;
		var timeout;
		var clicked = false;
		var self = this; //added for WM

		function show(){
			lower = ((page-1) * step);
			upper = lower+step;

			$(self).children().each(function (i) {
				if(options.nextprev){
					if(upper >= count) { next.fadeOut('fast'); } else { next.fadeIn('fast'); };
					if(lower >= 1) { prev.fadeIn('fast'); } else { prev.fadeOut('fast'); };
				};
			});

			//Carousel animation
			var margin =  Number(page-1) *  Number(-960);
			$(self.selector).animate({ marginLeft: margin }, 1000);

			$('li','.'+ options.controls).removeClass(options.current);
			$('li[data-index="'+page+'"]','.'+ options.controls).addClass(options.current);

			if(options.auto){
				if(options.clickstop && clicked){}else{ timeout = setTimeout(auto,options.pause); };
			};
		};

		function auto(){
			if(options.loop) if(upper >= count){ page=0; show(); }
			if(upper < count){ page++; show(); }
		};


		this.each(function(){
			var parent_obj = $(this).parent();

			if(count>step){
				var ol = $('<ol class="'+ options.controls +'"></ol>').insertAfter(parent_obj);
				if(options.nextprev){
					prev = $('<li class="prev">Previous</li>')
						.hide()
						.appendTo(ol)
						.click(function(){
							clicked = true;
							if (page > 1) //added for WM
								page--;
							show();
						});
				};

				if(options.numeric){
					for(var i=1;i<=Math.min(pages, options.maxNumericNav);i++){
						$('<li data-index="'+ i +'">'+ i +'</li>')
							.appendTo(ol)
							.click(function(){
								clicked = true;
								page = $(this).attr('data-index');

								//added for WM
								if (typeof options.dataCallback !== "undefined") {
									options.dataCallback(page, show);
								} else {
									show();
								}
							});
					};
				};

				if(options.nextprev){
					var positionLeft = (20 * (Math.min(pages, options.maxNumericNav))) + 3; // added for WM
					next = $('<li class="next" style="left:'+ positionLeft +'px; margin-left:0px;">Next</li>') // added for WM
						.hide()
						.appendTo(ol)
						.click(function() {
							clicked = true;
							if (page < pages) //added for WM
								page++;
							//added for WM
							if (typeof options.dataCallback !== "undefined") {
								options.dataCallback(page, show);
							} else {
								show();
							}
						});
				};

				show();
			};
		});

	};

})(jQuery);
