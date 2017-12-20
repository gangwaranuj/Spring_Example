var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.signature = function () {

	return function () {
		// DO NOT USE FASTCLICK HERE

		var canvas = document.getElementById('signature-canvas');

		// Adjust canvas based on pixel ratio to make it crisp on high-density displays
		function resizeCanvas() {
			var ratio = window.devicePixelRatio || 1;
			canvas.width = canvas.offsetWidth * ratio;
			canvas.height = canvas.offsetHeight * ratio;
			canvas.getContext("2d").scale(ratio, ratio);
		}

		window.onresize = resizeCanvas;
		resizeCanvas();

		var signaturePad = new SignaturePad(canvas);
		signaturePad.maxWidth = 2.0;

		$(".clear").bind("click", function () {
			signaturePad.clear();
		});

		$(".save").bind("click", function () {
			if (signaturePad.isEmpty()) {
				alert("Please provide signature first.");
			} else {
				$(".data-url").val(signaturePad.toDataURL());
				$(".add-signature-form").submit();
			}
		});

		$('.add-signature-form').on('submit', function () {
			trackEvent('mobile', 'signature');
		});
	}
};