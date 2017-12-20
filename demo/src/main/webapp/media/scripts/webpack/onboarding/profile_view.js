import $ from 'jquery';
import 'jquery-jcrop';
import _ from 'underscore';
import Template from './templates/profile-picture.hbs';
import OnboardView from './onboard_view';

export default OnboardView.extend({
	el: '.profile',
	template: Template,
	events: _.defaults({
		'click .profile-picture--save': 'closePreview'
	}, OnboardView.prototype.events),

	render: function () {
		// If we are rendering an image which was taken off the user's computer
		// instead of one sent from the server, then prepend it with the base64
		// string to render in browser.
		this.$el.html(this.template({
			src: _.isNull(this.model.get('url')) ? 'data:image/jpeg;base64,' + this.model.get('image') : this.model.get('url'),
			isLoaded: !_.isNull(this.model.get('url'))
		}));

		this.$inputText = this.$('.wm-file-input--name');
		this.$photoCrop = this.$('.photo-crop');
		this.$profilePicture = this.$('.profile-picture');
		this.$originalPhoto = this.$('.photo-crop--photo');
		this.$previewPhoto = this.$('.profile-picture--image');
		this.$slideContent = this.$el.closest('.wm-modal--content');

		this.trigger('render');
		return this;
	},

	loadPhoto: function (response) {
		var endOfBase = response.indexOf(','),
			image = response.substring(endOfBase + 1);

		this.imageBase = response.substring(0, endOfBase);
		this.model.set('url', null, { silent: true });
		this.model.set('image', image);
		this.$profilePicture.addClass('-loaded');
		this.$slideContent.addClass('-preview');
		// The plugin requires some time to initialize apparently
		// http://stackoverflow.com/a/5938269/1824448
		_.delay(_.bind(this.crop, this), 100, this.$originalPhoto.get(0));
	},

	crop: function (photo) {
		var smallScreen = $(window).width() < 500,
			width = photo.naturalWidth,
			height = photo.naturalHeight,
			// Center the initial selection
			initialSelectionSizeConstant = 0.85,
			selectionSize = _.min([ width, height ]) * initialSelectionSizeConstant,
			coordX = (width - selectionSize) / 2,
			coordX2 = width - coordX,
			coordY = (height - selectionSize) / 2,
			coordY2 = height - coordY,
			bounds = [ coordX, coordY, coordX2, coordY2 ],
			jCropDeferred = $.Deferred();

		$(photo).Jcrop({
			// Arbitrary height and width, needed by the plugin
			boxWidth: (smallScreen) ? 300 : 500,
			boxHeight: (smallScreen) ? 300 : 500,
			trueSize: [ photo.naturalWidth, photo.naturalHeight ],
			aspectRatio: 1,
			setSelect: bounds,
			onChange: _.bind(_.partial(this.previewImage, photo), this),
			onSelect: _.bind(_.partial(this.previewImage, photo), this)
		}, function () {
			// Stupid way the plugin makes you implement the API
			jCropDeferred.resolve(this);
		});

		this.jcropAPI = jCropDeferred.promise();
	},

	previewImage: function (image, dimensions) {
		var width = image.naturalWidth,
			height = image.naturalHeight,
			scalingFactor = width / dimensions.w * 100 + '%',
			offsetX = width - dimensions.w,
			offsetY = height - dimensions.h,
			positionX = dimensions.x / offsetX * 100 + '%',
			positionY = dimensions.y / offsetY * 100 + '%';

		this.$previewPhoto.css({
			'background-size': scalingFactor,
			'background-position-x': positionX,
			'background-position-y': positionY
		});

		// Silent is necessary to prevent the view from re-rendering, thereby
		// removing the Jcrop plugin.
		this.model.set('coordinates', dimensions, { silent: true });
	},

	closePreview: function () {
		$.when(this.jcropAPI).then(function (jCrop) { jCrop.destroy(); });
		this.$originalPhoto.removeAttr('style');
		this.$slideContent.removeClass('-preview');
	}
});
